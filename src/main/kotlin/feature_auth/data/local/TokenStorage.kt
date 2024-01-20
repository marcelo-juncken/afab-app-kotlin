package feature_auth.data.local

import java.security.KeyPair
import java.security.PrivateKey

interface TokenStorage {
    fun hasPrivateKey(): Boolean
    fun saveEncryptedPrivateKey(privateKey: PrivateKey)
    fun loadPrivateKey(): PrivateKey?
    fun generateRSAKeyPair(): KeyPair
    fun deletePrivateKey()
    fun isTimestampValid(): Boolean
    fun saveTheme(theme: String)
    fun loadTheme(): String?
}