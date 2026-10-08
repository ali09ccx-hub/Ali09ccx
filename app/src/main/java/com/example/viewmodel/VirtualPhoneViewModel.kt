package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.R
import com.example.data.CalcHistoryItem
import com.example.data.GalleryItem
import com.example.data.NoteItem
import com.example.data.VirtualAppType
import com.example.data.VirtualNotification
import com.example.data.VirtualPhonePreferences
import com.example.data.VirtualPhoneState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class VirtualPhoneViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = VirtualPhonePreferences(application)

    private val _phoneState = MutableStateFlow(prefs.loadPhoneState())
    val phoneState: StateFlow<VirtualPhoneState> = _phoneState.asStateFlow()

    private val _currentApp = MutableStateFlow<VirtualAppType?>(null)
    val currentApp: StateFlow<VirtualAppType?> = _currentApp.asStateFlow()

    private val _recentApps = MutableStateFlow<List<VirtualAppType>>(emptyList())
    val recentApps: StateFlow<List<VirtualAppType>> = _recentApps.asStateFlow()

    private val _isRecentsOpen = MutableStateFlow(false)
    val isRecentsOpen: StateFlow<Boolean> = _isRecentsOpen.asStateFlow()

    private val _isNotificationShadeOpen = MutableStateFlow(false)
    val isNotificationShadeOpen: StateFlow<Boolean> = _isNotificationShadeOpen.asStateFlow()

    private val _notes = MutableStateFlow<List<NoteItem>>(prefs.loadNotes())
    val notes: StateFlow<List<NoteItem>> = _notes.asStateFlow()

    private val _calcHistory = MutableStateFlow<List<CalcHistoryItem>>(prefs.loadCalcHistory())
    val calcHistory: StateFlow<List<CalcHistoryItem>> = _calcHistory.asStateFlow()

    private val _gameHighScore = MutableStateFlow(prefs.getGameHighScore())
    val gameHighScore: StateFlow<Int> = _gameHighScore.asStateFlow()

    private val _notifications = MutableStateFlow<List<VirtualNotification>>(
        listOf(
            VirtualNotification(
                id = "n1",
                title = "Cloud Node Connected",
                message = "Cloud Android environment synced with 60 FPS ultra low latency.",
                time = "Just now",
                appType = VirtualAppType.SETTINGS
            ),
            VirtualNotification(
                id = "n2",
                title = "مرحباً بك في هاتفك الافتراضي",
                message = "يمكنك حفظ الملاحظات واستخدام المتصفح واللعب بسلاسة تامة.",
                time = "10:45",
                appType = VirtualAppType.NOTES
            ),
            VirtualNotification(
                id = "n3",
                title = "System Update Ready",
                message = "CloudDroid OS v15.4 security patch loaded.",
                time = "09:30",
                appType = VirtualAppType.SETTINGS
            )
        )
    )
    val notifications: StateFlow<List<VirtualNotification>> = _notifications.asStateFlow()

    private val _galleryItems = MutableStateFlow<List<GalleryItem>>(
        listOf(
            GalleryItem(
                id = "g1",
                title = "Aurora Neon Wallpaper",
                drawableResId = R.drawable.phone_wallpaper_aurora_1791434984339,
                category = "Cloud Wallpapers"
            ),
            GalleryItem(
                id = "g2",
                title = "Cosmic Fluid Art",
                drawableResId = R.drawable.phone_art_abstract_1791434997382,
                category = "Artwork"
            ),
            GalleryItem(
                id = "g3",
                title = "Cloud System Core",
                drawableResId = R.drawable.virtual_phone_icon_1791434948411,
                category = "Screenshots"
            )
        )
    )
    val galleryItems: StateFlow<List<GalleryItem>> = _galleryItems.asStateFlow()

    fun openApp(app: VirtualAppType) {
        _isRecentsOpen.value = false
        _isNotificationShadeOpen.value = false
        _currentApp.value = app

        // Add to recents without duplicates
        _recentApps.update { list ->
            listOf(app) + list.filter { it != app }
        }
    }

    fun goHome() {
        _isRecentsOpen.value = false
        _isNotificationShadeOpen.value = false
        _currentApp.value = null
    }

    fun toggleRecents() {
        _isNotificationShadeOpen.value = false
        _isRecentsOpen.update { !it }
    }

    fun closeRecentApp(app: VirtualAppType) {
        _recentApps.update { list -> list.filter { it != app } }
        if (_currentApp.value == app) {
            _currentApp.value = null
        }
    }

    fun clearAllRecents() {
        _recentApps.value = emptyList()
        _currentApp.value = null
        _isRecentsOpen.value = false
    }

    fun toggleNotificationShade() {
        _isNotificationShadeOpen.update { !it }
    }

    fun clearNotifications() {
        _notifications.value = emptyList()
    }

    fun dismissNotification(id: String) {
        _notifications.update { list -> list.filter { it.id != id } }
    }

    fun lockPhone() {
        _phoneState.update { it.copy(isLocked = true) }
        _isNotificationShadeOpen.value = false
        _isRecentsOpen.value = false
        saveCurrentState()
    }

    fun unlockPhone(pin: String): Boolean {
        val state = _phoneState.value
        if (!state.isPinEnabled || pin == state.pinCode) {
            _phoneState.update { it.copy(isLocked = false) }
            saveCurrentState()
            return true
        }
        return false
    }

    fun toggleFrameMode() {
        _phoneState.update { it.copy(showDeviceFrame = !it.showDeviceFrame) }
        saveCurrentState()
    }

    fun setWallpaper(id: Int) {
        _phoneState.update { it.copy(wallpaperId = id) }
        saveCurrentState()
    }

    fun updateDeviceName(name: String) {
        _phoneState.update { it.copy(deviceName = name) }
        saveCurrentState()
    }

    fun setPin(pin: String, enabled: Boolean) {
        _phoneState.update { it.copy(pinCode = pin, isPinEnabled = enabled) }
        saveCurrentState()
    }

    fun toggleDarkMode() {
        _phoneState.update { it.copy(isDarkMode = !it.isDarkMode) }
        saveCurrentState()
    }

    fun toggleWifi() {
        _phoneState.update { it.copy(wifiEnabled = !it.wifiEnabled) }
        saveCurrentState()
    }

    fun toggleBluetooth() {
        _phoneState.update { it.copy(bluetoothEnabled = !it.bluetoothEnabled) }
        saveCurrentState()
    }

    fun toggleAirplane() {
        _phoneState.update { it.copy(airplaneMode = !it.airplaneMode) }
        saveCurrentState()
    }

    fun toggleFlashlight() {
        _phoneState.update { it.copy(flashlightOn = !it.flashlightOn) }
    }

    fun setBrightness(brightness: Float) {
        _phoneState.update { it.copy(brightness = brightness) }
    }

    fun setBatteryPercent(percent: Int) {
        _phoneState.update { it.copy(batteryPercent = percent.coerceIn(1, 100)) }
        saveCurrentState()
    }

    // Notes Actions
    fun addNote(title: String, content: String, colorHex: Long) {
        val newNote = NoteItem(title = title, content = content, colorHex = colorHex)
        _notes.update { listOf(newNote) + it }
        prefs.saveNotes(_notes.value)
    }

    fun updateNote(id: String, title: String, content: String, colorHex: Long) {
        _notes.update { list ->
            list.map {
                if (it.id == id) it.copy(title = title, content = content, colorHex = colorHex, timestamp = System.currentTimeMillis())
                else it
            }
        }
        prefs.saveNotes(_notes.value)
    }

    fun deleteNote(id: String) {
        _notes.update { list -> list.filter { it.id != id } }
        prefs.saveNotes(_notes.value)
    }

    // Calc Actions
    fun addCalcHistory(expression: String, result: String) {
        val item = CalcHistoryItem(expression = expression, result = result)
        _calcHistory.update { listOf(item) + it }
        prefs.saveCalcHistory(_calcHistory.value)
    }

    fun clearCalcHistory() {
        _calcHistory.value = emptyList()
        prefs.saveCalcHistory(emptyList())
    }

    // Game Actions
    fun recordGameScore(score: Int) {
        prefs.saveGameHighScore(score)
        _gameHighScore.value = prefs.getGameHighScore()
    }

    private fun saveCurrentState() {
        prefs.savePhoneState(_phoneState.value)
    }
}
