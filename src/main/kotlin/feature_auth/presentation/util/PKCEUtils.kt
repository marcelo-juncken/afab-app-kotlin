package feature_auth.presentation.util

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.util.*

fun generateCodeVerifier(): String {
    val secureRandom = SecureRandom()
    val codeVerifier = ByteArray(32)
    secureRandom.nextBytes(codeVerifier)
    return Base64.getUrlEncoder().withoutPadding().encodeToString(codeVerifier)
}

fun generateCodeChallenge(codeVerifier: String): String {
    return try {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(codeVerifier.toByteArray())
        Base64.getUrlEncoder().withoutPadding().encodeToString(digest)
    } catch (e: NoSuchAlgorithmException) {
        throw RuntimeException("Failed to generate code challenge", e)
    }
}

//private fun generateCodeChallenge(codeVerifier: String): String {
//    val mac = Mac.getInstance("HmacSHA256")
//    mac.init(SecretKeySpec(codeVerifier.toByteArray(), "HmacSHA256"))
//    val codeChallenge = mac.doFinal()
//    return Base64.getUrlEncoder().withoutPadding().encodeToString(codeChallenge)
//}