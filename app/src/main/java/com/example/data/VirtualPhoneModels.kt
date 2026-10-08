package com.example.data

data class NoteItem(
    val id: String = System.currentTimeMillis().toString(),
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val colorHex: Long = 0xFF1E293B
)

data class GalleryItem(
    val id: String,
    val title: String,
    val drawableResId: Int? = null,
    val uriString: String? = null,
    val category: String = "Wallpapers"
)

data class CalcHistoryItem(
    val id: String = System.currentTimeMillis().toString(),
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class VirtualNotification(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val appType: VirtualAppType
)

enum class VirtualAppType(
    val appName: String,
    val arabicName: String,
    val iconName: String
) {
    BROWSER("Browser", "المتصفح", "language"),
    GALLERY("Gallery", "المعرض", "photo_library"),
    NOTES("Notes", "الملاحظات", "edit_note"),
    CALCULATOR("Calculator", "الحاسبة", "calculate"),
    SETTINGS("Settings", "الإعدادات", "settings"),
    GAME("2048 Game", "لعبة 2048", "sports_esports"),
    CLOCK("Clock", "الساعة", "schedule"),
    MESSAGES("Messages", "الرسائل", "chat")
}

data class VirtualPhoneState(
    val deviceName: String = "CloudDroid OS Pro",
    val wallpaperId: Int = 0,
    val isDarkMode: Boolean = true,
    val showDeviceFrame: Boolean = true,
    val isLocked: Boolean = false,
    val isPinEnabled: Boolean = false,
    val pinCode: String = "1234",
    val batteryPercent: Int = 88,
    val isCharging: Boolean = false,
    val wifiEnabled: Boolean = true,
    val bluetoothEnabled: Boolean = true,
    val airplaneMode: Boolean = false,
    val flashlightOn: Boolean = false,
    val brightness: Float = 0.85f,
    val serverRegion: String = "Cloud Node EU-Frankfurt (12ms)"
)
