package feature_auth.data.local

interface DataProtection {
    fun protectData(data: String): String
    fun unprotectData(protectedData: String): String
}