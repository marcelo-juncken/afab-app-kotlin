package feature_auth.domain.use_case

import feature_auth.data.local.TokenStorage

class LogoutUserUseCase(
    private val tokenStorage: TokenStorage,
) {
    operator fun invoke() {
        tokenStorage.deletePrivateKey()
    }
}