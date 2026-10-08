package com.example.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

class VirtualPhonePreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("virtual_phone_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_DEVICE_NAME = "device_name"
        private const val KEY_WALLPAPER_ID = "wallpaper_id"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_FRAME_MODE = "frame_mode"
        private const val KEY_IS_LOCKED = "is_locked"
        private const val KEY_PIN_ENABLED = "pin_enabled"
        private const val KEY_PIN_CODE = "pin_code"
        private const val KEY_BATTERY = "battery_percent"
        private const val KEY_WIFI = "wifi_enabled"
        private const val KEY_BLUETOOTH = "bluetooth_enabled"
        private const val KEY_AIRPLANE = "airplane_mode"
        private const val KEY_NOTES = "saved_notes_json"
        private const val KEY_CALC_HISTORY = "calc_history_json"
        private const val KEY_GAME_HIGHSCORE = "game_2048_highscore"
        private const val KEY_GAME_BOARD = "game_2048_board"
        private const val KEY_GAME_SCORE = "game_2048_score"
    }

    fun loadPhoneState(): VirtualPhoneState {
        return VirtualPhoneState(
            deviceName = prefs.getString(KEY_DEVICE_NAME, "CloudDroid OS Pro") ?: "CloudDroid OS Pro",
            wallpaperId = prefs.getInt(KEY_WALLPAPER_ID, 0),
            isDarkMode = prefs.getBoolean(KEY_DARK_MODE, true),
            showDeviceFrame = prefs.getBoolean(KEY_FRAME_MODE, true),
            isLocked = prefs.getBoolean(KEY_IS_LOCKED, false),
            isPinEnabled = prefs.getBoolean(KEY_PIN_ENABLED, false),
            pinCode = prefs.getString(KEY_PIN_CODE, "1234") ?: "1234",
            batteryPercent = prefs.getInt(KEY_BATTERY, 88),
            wifiEnabled = prefs.getBoolean(KEY_WIFI, true),
            bluetoothEnabled = prefs.getBoolean(KEY_BLUETOOTH, true),
            airplaneMode = prefs.getBoolean(KEY_AIRPLANE, false),
            flashlightOn = false,
            brightness = 0.9f
        )
    }

    fun savePhoneState(state: VirtualPhoneState) {
        prefs.edit().apply {
            putString(KEY_DEVICE_NAME, state.deviceName)
            putInt(KEY_WALLPAPER_ID, state.wallpaperId)
            putBoolean(KEY_DARK_MODE, state.isDarkMode)
            putBoolean(KEY_FRAME_MODE, state.showDeviceFrame)
            putBoolean(KEY_IS_LOCKED, state.isLocked)
            putBoolean(KEY_PIN_ENABLED, state.isPinEnabled)
            putString(KEY_PIN_CODE, state.pinCode)
            putInt(KEY_BATTERY, state.batteryPercent)
            putBoolean(KEY_WIFI, state.wifiEnabled)
            putBoolean(KEY_BLUETOOTH, state.bluetoothEnabled)
            putBoolean(KEY_AIRPLANE, state.airplaneMode)
            apply()
        }
    }

    fun loadNotes(): List<NoteItem> {
        val jsonStr = prefs.getString(KEY_NOTES, null) ?: return getSampleNotes()
        val list = mutableListOf<NoteItem>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    NoteItem(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        content = obj.getString("content"),
                        timestamp = obj.getLong("timestamp"),
                        colorHex = obj.optLong("colorHex", 0xFF1E293B)
                    )
                )
            }
        } catch (e: Exception) {
            return getSampleNotes()
        }
        return if (list.isEmpty()) getSampleNotes() else list
    }

    fun saveNotes(notes: List<NoteItem>) {
        val array = JSONArray()
        for (n in notes) {
            val obj = JSONObject().apply {
                put("id", n.id)
                put("title", n.title)
                put("content", n.content)
                put("timestamp", n.timestamp)
                put("colorHex", n.colorHex)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_NOTES, array.toString()).apply()
    }

    fun loadCalcHistory(): List<CalcHistoryItem> {
        val jsonStr = prefs.getString(KEY_CALC_HISTORY, null) ?: return emptyList()
        val list = mutableListOf<CalcHistoryItem>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    CalcHistoryItem(
                        id = obj.getString("id"),
                        expression = obj.getString("expression"),
                        result = obj.getString("result"),
                        timestamp = obj.getLong("timestamp")
                    )
                )
            }
        } catch (e: Exception) {
            return emptyList()
        }
        return list
    }

    fun saveCalcHistory(history: List<CalcHistoryItem>) {
        val array = JSONArray()
        for (item in history.take(30)) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("expression", item.expression)
                put("result", item.result)
                put("timestamp", item.timestamp)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_CALC_HISTORY, array.toString()).apply()
    }

    fun getGameHighScore(): Int = prefs.getInt(KEY_GAME_HIGHSCORE, 0)

    fun saveGameHighScore(score: Int) {
        val current = getGameHighScore()
        if (score > current) {
            prefs.edit().putInt(KEY_GAME_HIGHSCORE, score).apply()
        }
    }

    private fun getSampleNotes(): List<NoteItem> {
        val now = System.currentTimeMillis()
        return listOf(
            NoteItem(
                id = "1",
                title = "مرحباً بك في هاتفك السحابي!",
                content = "هذا الهاتف الافتراضي يعمل بكفاءة مستقلة تماماً داخل جهازك.\n• يدعم حفظ الملاحظات.\n• يحتوي على متصفح ومعرض وحاسبة ولعبة 2048.\n• يمكنك تخصيص الخلفية والإعدادات وسيتم حفظها تلقائياً.",
                timestamp = now - 1000 * 60 * 30,
                colorHex = 0xFF1E293B
            ),
            NoteItem(
                id = "2",
                title = "قائمة المهام السحابية",
                content = "1. تجربة متصفح الويب السحابي 🌐\n2. تغيير خلفية الهاتف من تطبيق الإعدادات ⚙️\n3. تحقيق رقم قياسي في لعبة 2048 🎮\n4. اختبار تعدد المهام من زر المربعات السفلي 🔲",
                timestamp = now - 1000 * 60 * 60 * 2,
                colorHex = 0xFF064E3B
            ),
            NoteItem(
                id = "3",
                title = "Cloud Server Specs",
                content = "Instance: CloudDroid Pro 24\nRegion: Frankfurt EU-Central\nRAM: 12GB LPDDR5X\nStorage: 256GB UFS 4.0\nStatus: Secure Encrypted Sandbox",
                timestamp = now - 1000 * 60 * 60 * 24,
                colorHex = 0xFF312E81
            )
        )
    }
}
