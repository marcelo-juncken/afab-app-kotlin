package feature_config.presentation

import core.presentation.util.UiEvent
import core.util.Resource
import feature_auth.domain.models.UserDisplayInfo
import feature_auth.domain.models.DeviceDisplayInfo
import feature_config.domain.use_case.GetUsersDevicesUseCase
import feature_config.domain.use_case.RemoveUserDeviceUseCase
import feature_config.domain.use_case.ToggleUserAccessUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DevicesViewModel(
    private val getUsersDevicesUseCase: GetUsersDevicesUseCase,
    private val removeUserDeviceUseCase: RemoveUserDeviceUseCase,
    private val toggleUserAccessUseCase: ToggleUserAccessUseCase,
) : CoroutineScope by CoroutineScope(Dispatchers.Default) {

    private var removeDeviceJob: Job? = null
    private var toggleUserAccessJob: Job? = null

    private val _usersState = MutableStateFlow<List<UserDisplayInfo>>(emptyList())
    val usersState = _usersState.asStateFlow()

    private val _loadingState = MutableStateFlow(false)
    val loadingState = _loadingState.asStateFlow()

    private val _dialogLoadingState = MutableStateFlow(false)
    val dialogLoadingState = _dialogLoadingState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        launch(Dispatchers.IO) {
            _loadingState.update { true }

            when (val response = getUsersDevicesUseCase()) {
                is Resource.Error -> _eventFlow.emit(UiEvent.ShowSnackbar(response.errorMessage))
                is Resource.Success -> _usersState.update { response.data ?: emptyList() }
                else -> Unit
            }

            _loadingState.update { false }
        }
    }

    fun onEvent(event: DevicesEvent) {
        when (event) {
            is DevicesEvent.RemoveDevice -> removeDevice(event.selectedUser, event.device)
            is DevicesEvent.ToggleUserAccess -> toggleUserAccess(event.selectedUser)
            DevicesEvent.CancelRemoveDevice -> {
                removeDeviceJob?.cancel()
                _dialogLoadingState.update { false }
            }
        }
    }

    private fun toggleUserAccess(selectedUser: UserDisplayInfo?) {
        toggleUserAccessJob?.cancel()

        toggleUserAccessJob = launch(Dispatchers.IO) {
            _loadingState.update { true }

            when (val response = toggleUserAccessUseCase(selectedUser)) {
                is Resource.Error -> _eventFlow.emit(UiEvent.ShowSnackbar(response.errorMessage))
                is Resource.Success -> {
                    _usersState.update {
                        usersState.value.map { user ->
                            if (user.id == selectedUser?.id) {
                                user.copy(isActive = !user.isActive)
                            } else {
                                user
                            }
                        }
                    }
                }
                is Resource.Disconnect -> {
                    _eventFlow.emit(UiEvent.OnLogout)
                }
            }
            _loadingState.update { false }
        }
    }

    private fun removeDevice(selectedUser: UserDisplayInfo?, device: DeviceDisplayInfo) {

        removeDeviceJob?.cancel()

        removeDeviceJob = launch(Dispatchers.IO) {
            _loadingState.update { true }

            when (val response = removeUserDeviceUseCase(selectedUser, device)) {
                is Resource.Error -> _eventFlow.emit(UiEvent.ShowSnackbar(response.errorMessage))
                is Resource.Success -> {
                    val removedDevice = device.deviceId
                    _usersState.update {
                        usersState.value.map { user ->
                            if (user.id == selectedUser?.id) {
                                user.copy(devices = user.devices.filter { it.deviceId != removedDevice }
                                    .toMutableList())
                            } else {
                                user
                            }
                        }
                    }
                }
                is Resource.Disconnect -> {
                    _eventFlow.emit(UiEvent.OnLogout)
                }
            }
            _loadingState.update { false }
        }
    }
}