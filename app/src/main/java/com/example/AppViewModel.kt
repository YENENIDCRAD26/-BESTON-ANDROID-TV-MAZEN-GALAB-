package com.example

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    // Additional state holders for pairing, desktop, settings etc can go here
    val settingsPrivilegedOps = MutableStateFlow(false)

    fun setLanguage(lang: String) { _language.value = lang }
    fun setActiveTab(tab: String) { _activeTab.value = tab }
    
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
