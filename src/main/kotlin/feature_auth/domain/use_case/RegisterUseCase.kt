package feature_auth.domain.use_case

import core.util.Resource
import feature_auth.data.dto.DomainAppUser
import feature_auth.presentation.register.models.RequestUser
import feature_auth.domain.repository.AppUserRepository
import feature_auth.domain.repository.AuthRepository
import feature_auth.presentation.register.models.RegisterResult
import feature_auth.presentation.util.AuthValidationUtil

class RegisterUseCase(
    private val authRepository: AuthRepository,
    private val checkLoggedInUserUseCase: CheckLoggedInUserUseCase,
    private val appUserRepository: AppUserRepository,
) {
    suspend operator fun invoke(user: RequestUser): RegisterResult {
        handleInputError(user)?.let { inputError ->
            return inputError
        }
        user.apply {

            val userLogged = checkLoggedInUserUseCase()
            if (userLogged is Resource.Error) return RegisterResult(result = Resource.Error(errorMessage = userLogged.errorMessage))
            if (userLogged is Resource.Disconnect) return RegisterResult(result = Resource.Disconnect())

            if (userLogged.data?.roles?.contains("ADMIN") != true) return RegisterResult(result = Resource.Disconnect())

            val auth0Result = authRepository.registerUser(email, password)

            if (auth0Result is Resource.Error) return RegisterResult(result = Resource.Error(auth0Result.errorMessage))

            val auth0Id = auth0Result.data?._id
                ?: return RegisterResult(result = Resource.Error("Erro ao retornar os dados de usuário"))

            val domainAppUser = DomainAppUser(
                auth0UserId = auth0Id,
                email = email,
                firstname = firstName,
                lastname = lastName,
                profilePictureUrl = "",
                roles = setOf("USER"),
                accessCount = 0L
            )

            val appResult = appUserRepository.insertUser(domainAppUser)

            return RegisterResult(
                result = appResult
            )
        }
    }

    private fun handleInputError(user: RequestUser): RegisterResult? {
        with(user) {
            val firstNameError = AuthValidationUtil.validateName(name = firstName)
            val lastNameError = AuthValidationUtil.validateName(name = lastName)
            val emailError = AuthValidationUtil.validateEmail(email = email)
            val passwordError = AuthValidationUtil.validatePassword(password = password)
            val confirmPasswordError = AuthValidationUtil.validatePassword(password = password)
            val passwordMatchError =
                AuthValidationUtil.validatePasswordMatches(password = password, confirmPassword = confirmPassword)

            val hasError = listOf(
                firstNameError,
                lastNameError,
                emailError,
                passwordError,
                confirmPasswordError,
                passwordMatchError
            ).any { it != null }

            if (hasError) {
                return RegisterResult(
                    firstNameError = firstNameError,
                    lastNameError = lastNameError,
                    emailError = emailError,
                    passwordError = passwordMatchError ?: passwordError,
                    confirmPasswordError = passwordMatchError ?: confirmPasswordError
                )
            }

            return null
        }
    }
}