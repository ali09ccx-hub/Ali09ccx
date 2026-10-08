package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.NoteItem
import com.example.data.VirtualPhonePreferences
import com.example.data.VirtualPhoneState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Virtual Phone", appName)
  }

  @Test
  fun `verify notes persistence in virtual phone prefs`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = VirtualPhonePreferences(context)

    val testNotes = listOf(
      NoteItem(
        id = "test_1",
        title = "ملاحظة اختبار",
        content = "محتوى الاختبار في الهاتف الوهمي",
        timestamp = 123456789L,
        colorHex = 0xFF064E3B
      )
    )

    prefs.saveNotes(testNotes)
    val loadedNotes = prefs.loadNotes()

    assertEquals(1, loadedNotes.size)
    assertEquals("ملاحظة اختبار", loadedNotes[0].title)
    assertEquals("محتوى الاختبار في الهاتف الوهمي", loadedNotes[0].content)
  }

  @Test
  fun `verify phone state persistence`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = VirtualPhonePreferences(context)

    val state = VirtualPhoneState(
      deviceName = "My Cloud Super Phone",
      wallpaperId = 2,
      isDarkMode = false,
      showDeviceFrame = true,
      batteryPercent = 95
    )

    prefs.savePhoneState(state)
    val loadedState = prefs.loadPhoneState()

    assertEquals("My Cloud Super Phone", loadedState.deviceName)
    assertEquals(2, loadedState.wallpaperId)
    assertEquals(false, loadedState.isDarkMode)
    assertEquals(95, loadedState.batteryPercent)
  }
}
