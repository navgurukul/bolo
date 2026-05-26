package co.bolo.app.update

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateViewModel @Inject constructor(
    private val checker: UpdateChecker,
    private val installer: UpdateInstaller
) : ViewModel() {

    private val _state = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val state: StateFlow<UpdateState> = _state.asStateFlow()

    /** Triggered automatically on Home entry; safe to call repeatedly. */
    fun checkSilently() {
        if (_state.value is UpdateState.Available) return
        viewModelScope.launch {
            _state.value = UpdateState.Checking
            _state.value = checker.checkForUpdate()
        }
    }

    /** User tapped "Check for updates" in Settings — surface every outcome. */
    fun checkManually() {
        viewModelScope.launch {
            _state.value = UpdateState.Checking
            _state.value = checker.checkForUpdate()
        }
    }

    fun startInstall() {
        val available = _state.value as? UpdateState.Available ?: return
        val id = installer.startDownload(available.apkUrl, available.versionName)
        installer.installWhenComplete(id)
        // Dialog dismisses while the download notification handles the rest.
        _state.value = UpdateState.Idle
    }

    fun dismiss() {
        _state.value = UpdateState.Idle
    }
}
