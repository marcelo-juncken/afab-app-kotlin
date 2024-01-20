package feature_auth.data.local.platform

import feature_auth.data.local.DataProtection
import feature_auth.data.local.TokenStorage
import java.io.File
import java.nio.file.Paths
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.RSAKeyGenParameterSpec
import java.util.*

class WindowsTokenStorage(
    private val dataProtection: DataProtection,
) : TokenStorage {

    private val privateKeyFilePath = getPrivateKeyPath()
    private val loginTimestampFilePath = getLoginTimestampPath()
    private val savedThemeFilePath = getSavedThemePath()

    override fun generateRSAKeyPair(): KeyPair {
        return try {
            val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
            keyPairGenerator.initialize(RSAKeyGenParameterSpec(2048, RSAKeyGenParameterSpec.F4))
            keyPairGenerator.generateKeyPair()
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to generate RSA key pair", e)
        }
    }

    override fun saveEncryptedPrivateKey(privateKey: PrivateKey) {
        val privateKeyString = Base64.getEncoder().encodeToString(privateKey.encoded)
        val encryptedPrivateKeyString = dataProtection.protectData(privateKeyString)

        writeFile(filePath = privateKeyFilePath, content = encryptedPrivateKeyString)

        val timestamp = System.currentTimeMillis().toString()
        val encryptedTimestamp = dataProtection.protectData(timestamp)
        writeFile(filePath = loginTimestampFilePath, content = encryptedTimestamp)
    }

    override fun hasPrivateKey(): Boolean = File(privateKeyFilePath).exists() && File(loginTimestampFilePath).exists()

    override fun loadPrivateKey(): PrivateKey? {
        val encryptedPrivateKeyString = readFile(privateKeyFilePath) ?: return null
        val privateKeyString = dataProtection.unprotectData(encryptedPrivateKeyString)

        // Convert the string back to PrivateKey
        val keyBytes = Base64.getDecoder().decode(privateKeyString)
        val keySpec = PKCS8EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")

        return keyFactory.generatePrivate(keySpec)
    }

    override fun deletePrivateKey() {
        File(privateKeyFilePath).let { privateKeyFile ->
            if (privateKeyFile.exists()) {
                privateKeyFile.delete()
            }
        }
        File(loginTimestampFilePath).let { timestampFile ->
            if (timestampFile.exists()) {
                timestampFile.delete()
            }
        }
    }

    override fun isTimestampValid(): Boolean {
        val encryptedTimestampString = readFile(loginTimestampFilePath) ?: return false
        val decryptedTimestampString = dataProtection.unprotectData(encryptedTimestampString)

        val loginTimestamp = decryptedTimestampString.toLong()
        val maxLoginDurationMillis = 7 * 24 * 60 * 60 * 1000L // 7 days in milliseconds
        val currentTimestamp = System.currentTimeMillis()

        return (currentTimestamp - loginTimestamp) <= maxLoginDurationMillis
    }

    override fun saveTheme(theme: String) {
        val encryptedTheme = dataProtection.protectData(theme)
        writeFile(filePath = savedThemeFilePath, content = encryptedTheme)
    }


    override fun loadTheme(): String? {
        val encryptedTheme = readFile(savedThemeFilePath) ?: return null

        return dataProtection.unprotectData(encryptedTheme)
    }

    private fun writeFile(filePath: String, content: String) {
        val file = File(filePath)
        file.parentFile.mkdirs()
        file.writeText(content)
    }

    private fun readFile(filePath: String): String? {
        val file = File(filePath)
        if (!file.exists()) return null
        return file.readText()
    }

    private fun getLoginTimestampPath(): String {
        val osName = System.getProperty("os.name").lowercase(Locale.ROOT)
        val userHome = System.getProperty("user.home")
        val appName = "A Fabrica App"

        return when {
            osName.contains("windows") -> Paths.get(userHome, "AppData", "Roaming", appName, "keys", "login_timestamp")
                .toString()
            osName.contains("mac") -> Paths.get(
                userHome,
                "Library",
                "Application Support",
                appName,
                "keys",
                "login_timestamp"
            ).toString()
            else -> Paths.get(userHome, ".config", appName, "keys", "login_timestamp")
                .toString() // For Linux and other Unix-based systems
        }
    }

    private fun getPrivateKeyPath(): String {
        val osName = System.getProperty("os.name").lowercase(Locale.ROOT)
        val userHome = System.getProperty("user.home")
        val appName = "A Fabrica App"

        return when {
            osName.contains("windows") -> Paths.get(userHome, "AppData", "Roaming", appName, "keys", "private_key.pem")
                .toString()
            osName.contains("mac") -> Paths.get(
                userHome,
                "Library",
                "Application Support",
                appName,
                "keys",
                "private_key.pem"
            ).toString()
            else -> Paths.get(userHome, ".config", appName, "keys", "private_key.pem")
                .toString() // For Linux and other Unix-based systems
        }
    }

    private fun getSavedThemePath(): String {
        val osName = System.getProperty("os.name").lowercase(Locale.ROOT)
        val userHome = System.getProperty("user.home")
        val appName = "A Fabrica App"

        return when {
            osName.contains("windows") -> Paths.get(userHome, "AppData", "Roaming", appName, "theme", "saved_theme")
                .toString()
            osName.contains("mac") -> Paths.get(
                userHome,
                "Library",
                "Application Support",
                appName,
                "theme",
                "saved_theme"
            ).toString()
            else -> Paths.get(userHome, ".config", appName, "theme", "saved_theme")
                .toString() // For Linux and other Unix-based systems
        }
    }
}