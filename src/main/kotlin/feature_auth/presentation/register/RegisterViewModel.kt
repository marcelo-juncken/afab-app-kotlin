package feature_auth.presentation.register

import core.presentation.util.UiEvent
import core.util.Resource
import feature_auth.presentation.register.models.RequestUser
import feature_auth.domain.use_case.RegisterUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase,
) : CoroutineScope by CoroutineScope(Dispatchers.Default) {

    private var loginJob: Job? = null

    private val _loadingState = MutableStateFlow(false)
    val loadingState = _loadingState.asStateFlow()

    private val _textFieldsErrorState = MutableStateFlow(RegisterFormState())
    val textFieldsErrorState = _textFieldsErrorState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: RegisterFormEvent) {
        when (event) {
            is RegisterFormEvent.Submit -> {
                val user = RequestUser(
                    firstName = event.firstName,
                    lastName = event.lastName,
                    email = event.email,
                    password = event.password,
                    confirmPassword = event.confirmPassword,
                )
                submitData(user = user)
            }
        }
    }

    private fun submitData(user: RequestUser) {
        loginJob?.cancel()
        loginJob = launch(Dispatchers.IO) {
            _loadingState.update { true }

            _textFieldsErrorState.update {
                textFieldsErrorState.value.copy(
                    firstNameError = null,
                    lastNameError = null,
                    emailError = null,
                    passwordError = null,
                    confirmPasswordError = null
                )
            }

            val registerResult = registerUseCase(user)

            when (registerResult.result) {
                is Resource.Error -> {
                    _eventFlow.emit(
                        UiEvent.ShowSnackbar(
                            textMessage = registerResult.result.errorMessage
                        )
                    )
                    _loadingState.update { false }
                }

                is Resource.Success -> {
                    _eventFlow.emit(
                        UiEvent.ShowSnackbar(
                            textMessage = "Usuário criado com sucesso!"
                        )
                    )
                    _loadingState.update { false }
                }

                null -> {
                    _textFieldsErrorState.update {
                        textFieldsErrorState.value.copy(
                            firstNameError = registerResult.firstNameError,
                            lastNameError = registerResult.lastNameError,
                            emailError = registerResult.emailError,
                            passwordError = registerResult.passwordError,
                            confirmPasswordError = registerResult.confirmPasswordError
                        )
                    }
                    _loadingState.update { false }
                }
                is Resource.Disconnect -> {
                    _eventFlow.emit(UiEvent.OnLogout)
                }
            }
        }
    }
}