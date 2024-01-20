package feature_auth.presentation.util

import java.math.BigInteger
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.interfaces.RSAPrivateKey
import java.security.spec.RSAPublicKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*

fun getPublicKeyFromPrivateKey(privateKey: PrivateKey?): PublicKey {
    val keyFactory = KeyFactory.getInstance("RSA")
    val rsaPrivateKey = privateKey as RSAPrivateKey
    val rsaPublicKeySpec = RSAPublicKeySpec(rsaPrivateKey.modulus, BigInteger.valueOf(65537))
    return keyFactory.generatePublic(rsaPublicKeySpec)
}

fun stringToPublicKey(encodedKey: String): PublicKey {
    val keyBytes = Base64.getDecoder().decode(encodedKey)
    val keySpec = X509EncodedKeySpec(keyBytes)
    val keyFactory = KeyFactory.getInstance("RSA")
    return keyFactory.generatePublic(keySpec)
}

fun PublicKey.publicKeyToString(): String {
    return Base64.getEncoder().encodeToString(encoded)
}