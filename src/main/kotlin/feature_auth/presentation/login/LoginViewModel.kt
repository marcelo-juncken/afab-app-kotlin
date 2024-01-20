package feature_auth.presentation.login

import core.presentation.util.UiEvent
import core.util.Resource
import feature_auth.domain.use_case.LoginUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
) : CoroutineScope by CoroutineScope(Dispatchers.Default) {

    private var loginJob: Job? = null

    private val _loadingState = MutableStateFlow(false)
    val loadingState = _loadingState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: LoginFormEvent) {
        when (event) {
            LoginFormEvent.Submit -> submitData()
        }
    }

    private fun submitData() {
        loginJob?.cancel()
        loginJob = launch(Dispatchers.IO) {


            when (val loginResult = loginUseCase()) {
                is Resource.Error -> {
                    _eventFlow.emit(
                        UiEvent.ShowSnackbar(
                            textMessage = loginResult.errorMessage
                        )
                    )
                }
                is Resource.Success -> {
                    _eventFlow.emit(
                        UiEvent.OnLogin
                    )
                }
            }
        }
    }
}