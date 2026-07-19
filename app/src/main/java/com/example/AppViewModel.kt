package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppViewModel : ViewModel() {
    private val _language = MutableStateFlow("ar")
    val language: StateFlow<String> = _language.asStateFlow()

    private val _activeTab = MutableStateFlow("dashboard")
    val activeTab: StateFlow<String> = _activeTab.asStateFlow()

    private val _updateState = MutableStateFlow("idle")
    val updateState: StateFlow<String> = _updateState.asStateFlow()

    private val _lastCheck = MutableStateFlow("Not checked in this session")
    val lastCheck: StateFlow<String> = _lastCheck.asStateFlow()

    private val _downloadProgress = MutableStateFlow(0)
    val downloadProgress: StateFlow<Int> = _downloadProgress.asStateFlow()

    private val _downloadSpeed = MutableStateFlow("0 MB/s")
    val downloadSpeed: StateFlow<String> = _downloadSpeed.asStateFlow()

    private val _eta = MutableStateFlow("—")
    val eta: StateFlow<String> = _eta.asStateFlow()

    private val _history = MutableStateFlow(AppData.initialHistory)
    val history: StateFlow<List<HistoryItem>> = _history.asStateFlow()

    private val _checkLogs = MutableStateFlow<List<String>>(emptyList())
    val checkLogs: StateFlow<List<String>> = _checkLogs.asStateFlow()

    private val _revertState = MutableStateFlow("idle")
    val revertState: StateFlow<String> = _revertState.asStateFlow()

    private val _usbInstallState = MutableStateFlow("idle")
    val usbInstallState: StateFlow<String> = _usbInstallState.asStateFlow()

    private val _usbInstallProgress = MutableStateFlow(0f)
    val usbInstallProgress: StateFlow<Float> = _usbInstallProgress.asStateFlow()

    private val _rootEnabled = MutableStateFlow(false)
    val rootEnabled: StateFlow<Boolean> = _rootEnabled.asStateFlow()

    private val _playStoreSigned = MutableStateFlow(false)
    val playStoreSigned: StateFlow<Boolean> = _playStoreSigned.asStateFlow()

    private val _upgradePrepared = MutableStateFlow(false)
    val upgradePrepared: StateFlow<Boolean> = _upgradePrepared.asStateFlow()

    private val _satelliteDialog = MutableStateFlow(false)
    val satelliteDialog: StateFlow<Boolean> = _satelliteDialog.asStateFlow()

    private val _selectedChannelSource = MutableStateFlow("satellite")
    val selectedChannelSource: StateFlow<String> = _selectedChannelSource.asStateFlow()

    private val _powerState = MutableStateFlow("on")
    val powerState: StateFlow<String> = _powerState.asStateFlow()

    private val _startupMode = MutableStateFlow("tv")
    val startupMode: StateFlow<String> = _startupMode.asStateFlow()

    private val _brightness = MutableStateFlow(50f)
    val brightness: StateFlow<Float> = _brightness.asStateFlow()

    private val _contrast = MutableStateFlow(50f)
    val contrast: StateFlow<Float> = _contrast.asStateFlow()

    private val _colorSaturation = MutableStateFlow(50f)
    val colorSaturation: StateFlow<Float> = _colorSaturation.asStateFlow()

    private val _autoDisplaySettings = MutableStateFlow(true)
    val autoDisplaySettings: StateFlow<Boolean> = _autoDisplaySettings.asStateFlow()

    private val _volumeLevel = MutableStateFlow(30f)
    val volumeLevel: StateFlow<Float> = _volumeLevel.asStateFlow()

    private val _autoScanRunning = MutableStateFlow(false)
    val autoScanRunning: StateFlow<Boolean> = _autoScanRunning.asStateFlow()

    private val _cpuUsage = MutableStateFlow(42f)
    val cpuUsage: StateFlow<Float> = _cpuUsage.asStateFlow()

    private val _ramUsage = MutableStateFlow(68f)
    val ramUsage: StateFlow<Float> = _ramUsage.asStateFlow()

    private val _storageHealth = MutableStateFlow(95f)
    val storageHealth: StateFlow<Float> = _storageHealth.asStateFlow()

    init {
        viewModelScope.launch {
            while (true) {
                delay(2000)
                _cpuUsage.value = (_cpuUsage.value + (-5..5).random()).coerceIn(10f, 90f)
                _ramUsage.value = (_ramUsage.value + (-2..2).random()).coerceIn(40f, 85f)
            }
        }
    }

    // Additional state holders for pairing, desktop, settings etc can go here
    val settingsPrivilegedOps = MutableStateFlow(false)

    fun revertOS(t: Translations.Translation) {
        if (_revertState.value != "idle") return
        viewModelScope.launch {
            _revertState.value = "reverting"
            delay(1500)
            addHistory(t.strings["revert_os"] ?: "Revert to Previous OS", "Successful", t.strings["revert_os_desc"] ?: "System restored.")
            _revertState.value = "success"
            delay(3000)
            _revertState.value = "idle"
        }
    }

    fun installFromUsb(t: Translations.Translation) {
        if (_usbInstallState.value != "idle") return
        viewModelScope.launch {
            _usbInstallState.value = "checking"
            _usbInstallProgress.value = 0f
            val updates = LocalUpdateManager.checkForLocalUpdates()
            delay(1000)
            if (updates.isNotEmpty()) {
                val pkg = updates.first()
                if (LocalUpdateManager.verifyUpdatePackage(pkg)) {
                    _usbInstallState.value = "installing"
                    
                    // Simulate progress
                    for (i in 1..100) {
                        delay(30)
                        _usbInstallProgress.value = i / 100f
                    }
                    
                    addHistory(t.strings["install_from_usb"] ?: "Install from USB", "Completed", "Installed ${pkg.name}")
                    _usbInstallState.value = "success"
                } else {
                    addHistory(t.strings["install_from_usb"] ?: "Install from USB", "Failed", "Invalid package signature")
                    _usbInstallState.value = "idle"
                }
            } else {
                addHistory(t.strings["install_from_usb"] ?: "Install from USB", "Failed", "No update package found")
                _usbInstallState.value = "idle"
            }
            delay(3000)
            _usbInstallState.value = "idle"
            _usbInstallProgress.value = 0f
        }
    }

    fun runUpdateCheck(t: Translations.Translation) {
        viewModelScope.launch {
            _updateState.value = "checking"
            _checkLogs.value = listOf(t.strings["log_start"] ?: "Starting OTA catalog query...")
            delay(1000)
            _checkLogs.value = _checkLogs.value + (t.strings["log_verify_model"] ?: "Verifying device model (BSN32100S)... OK")
            delay(1000)
            _checkLogs.value = _checkLogs.value + (t.strings["log_verify_api"] ?: "Checking API level compatibility... OK")
            delay(1500)
            _checkLogs.value = _checkLogs.value + (t.strings["log_pkg_found"] ?: "Update package 5.2.1-TV found in channel Stable OTA")
            delay(800)
            _checkLogs.value = _checkLogs.value + (t.strings["log_verify_checksum"] ?: "Validating package manifest checksum... Verified")
            delay(1000)
            _updateState.value = "available"
            addHistory(t.strings["hist_check_title"] ?: "Update Check Performed", "Completed", t.strings["hist_check_detail"] ?: "Found update 5.2.1-TV")
        }
    }

    fun setLanguage(lang: String) { _language.value = lang }
    fun setActiveTab(tab: String) { _activeTab.value = tab }
    fun setPowerState(state: String) { _powerState.value = state }

    fun setSatelliteDialog(show: Boolean) { _satelliteDialog.value = show }
    fun setChannelSource(source: String) { _selectedChannelSource.value = source }

    fun setStartupMode(mode: String) { _startupMode.value = mode }
    fun setBrightness(value: Float) { _brightness.value = value }
    fun setContrast(value: Float) { _contrast.value = value }
    fun setColorSaturation(value: Float) { _colorSaturation.value = value }
    fun setAutoDisplaySettings(auto: Boolean) { _autoDisplaySettings.value = auto }
    fun setVolumeLevel(level: Float) { _volumeLevel.value = level }
    private val tunerManager = TunerManager()
    private val _scannedChannels = MutableStateFlow<List<TvChannel>>(emptyList())
    val scannedChannels: StateFlow<List<TvChannel>> = _scannedChannels.asStateFlow()

    fun setAutoScanRunning(running: Boolean, tunerType: TunerType? = null) { 
        _autoScanRunning.value = running 
        if (running && tunerType != null) {
            viewModelScope.launch {
                _scannedChannels.value = tunerManager.scanChannels(tunerType)
                _autoScanRunning.value = false
            }
        }
    }

    private val _freeSpaceInstallCompleted = MutableStateFlow(false)
    val freeSpaceInstallCompleted: StateFlow<Boolean> = _freeSpaceInstallCompleted.asStateFlow()

    fun enableRoot() { _rootEnabled.value = true }
    fun prepareUpgrade() { _upgradePrepared.value = true }
    fun signPlayStore() { _playStoreSigned.value = true }
    fun completeFreeSpaceInstall() { _freeSpaceInstallCompleted.value = true }
    
    private val _installProgress = MutableStateFlow(0f)
    val installProgress: StateFlow<Float> = _installProgress.asStateFlow()
    
    fun setInstallProgress(progress: Float) { _installProgress.value = progress }
    
    fun setUpdateState(state: String) { _updateState.value = state }
    fun setLastCheck(check: String) { _lastCheck.value = check }
    fun setDownloadProgress(progress: Int) { _downloadProgress.value = progress }
    fun setDownloadSpeed(speed: String) { _downloadSpeed.value = speed }
    fun setEta(eta: String) { _eta.value = eta }

    fun addHistory(title: String, status: String, detail: String) {
        val now = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date())
        val newItem = HistoryItem(
            id = "hist-\${System.currentTimeMillis()}-\${_history.value.size}",
            time = now,
            title = title,
            status = status,
            detail = detail
        )
        val updated = mutableListOf(newItem).apply { addAll(_history.value) }.take(50)
        _history.value = updated
    }
}
