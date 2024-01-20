package feature_auth.presentation.login

sealed interface LoginFormEvent{
    object Submit : LoginFormEvent
}
