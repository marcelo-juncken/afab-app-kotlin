package feature_auth.data.remote.request

import com.google.gson.annotations.SerializedName

data class AuthorizationCodeRequest(
    @SerializedName("grant_type") val grantType: String,
    @SerializedName("client_id") val clientId: String,
    @SerializedName("code_verifier") val codeVerifier: String,
    @SerializedName("code") val code: String,
    @SerializedName("redirect_uri") val redirectUri: String
)