package com.example.ui.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.VirtualAppType
import com.example.data.WallpaperManager
import com.example.ui.apps.BrowserApp
import com.example.ui.apps.CalculatorApp
import com.example.ui.apps.ClockApp
import com.example.ui.apps.GalleryApp
import com.example.ui.apps.Game2048App
import com.example.ui.apps.MessagesApp
import com.example.ui.apps.NotesApp
import com.example.ui.apps.SettingsApp
import com.example.ui.components.NotificationShade
import com.example.ui.components.SimulatedDeviceFrame
import com.example.ui.components.SimulatedNavBar
import com.example.ui.components.SimulatedStatusBar
import com.example.viewmodel.VirtualPhoneViewModel

@Composable
fun VirtualPhoneScreen(
    viewModel: VirtualPhoneViewModel = viewModel()
) {
    val context = LocalContext.current
    val phoneState by viewModel.phoneState.collectAsState()
    val currentApp by viewModel.currentApp.collectAsState()
    val isRecentsOpen by viewModel.isRecentsOpen.collectAsState()
    val isNotificationShadeOpen by viewModel.isNotificationShadeOpen.collectAsState()
    val recentApps by viewModel.recentApps.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val calcHistory by viewModel.calcHistory.collectAsState()
    val gameHighScore by viewModel.gameHighScore.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val galleryItems by viewModel.galleryItems.collectAsState()

    var showExitAppDialog by remember { mutableStateOf(false) }

    // Physical Back button handling for complete independence
    BackHandler {
        when {
            isNotificationShadeOpen -> viewModel.toggleNotificationShade()
            isRecentsOpen -> viewModel.toggleRecents()
            currentApp != null -> viewModel.goHome()
            phoneState.isLocked -> {
                // If locked and user presses back, show exit dialog
                showExitAppDialog = true
            }
            else -> {
                // In Home Screen: show exit confirmation to return safely to real Android system
                showExitAppDialog = true
            }
        }
    }

    val wallpaper = remember(phoneState.wallpaperId) {
        WallpaperManager.getWallpaper(phoneState.wallpaperId)
    }

    SimulatedDeviceFrame(
        showFrame = phoneState.showDeviceFrame,
        onToggleFrame = { viewModel.toggleFrameMode() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .testTag("virtual_phone_viewport")
        ) {
            // Wallpaper Background
            Box(modifier = Modifier.fillMaxSize()) {
                if (wallpaper.drawableResId != null) {
                    Image(
                        painter = painterResource(id = wallpaper.drawableResId),
                        contentDescription = "Wallpaper",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (wallpaper.gradientBrush != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(wallpaper.gradientBrush)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(wallpaper.previewColor)
                    )
                }

                // Dim overlay for screen readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.25f))
                )
            }

            // Phone Operating System Viewport
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Status Bar (always visible)
                SimulatedStatusBar(
                    phoneState = phoneState,
                    notificationCount = notifications.size,
                    onOpenNotificationShade = { viewModel.toggleNotificationShade() }
                )

                // Main Screen Area: Lock Screen, Home Screen, or Running App
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    if (phoneState.isLocked) {
                        LockScreen(
                            phoneState = phoneState,
                            onUnlock = { pin -> viewModel.unlockPhone(pin) },
                            onCameraQuickLaunch = {
                                viewModel.unlockPhone(phoneState.pinCode)
                                viewModel.openApp(VirtualAppType.GALLERY)
                            }
                        )
                    } else {
                        // When unlocked: show Home Screen or Current App
                        AnimatedContent(
                            targetState = currentApp,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "app_transition"
                        ) { targetApp ->
                            if (targetApp == null) {
                                HomeScreen(
                                    phoneState = phoneState,
                                    onAppClick = { app -> viewModel.openApp(app) },
                                    onSearchClick = { viewModel.openApp(VirtualAppType.BROWSER) }
                                )
                            } else {
                                when (targetApp) {
                                    VirtualAppType.BROWSER -> BrowserApp(onClose = { viewModel.goHome() })
                                    VirtualAppType.GALLERY -> GalleryApp(
                                        galleryItems = galleryItems,
                                        onSetAsWallpaper = { id -> viewModel.setWallpaper(id) },
                                        onClose = { viewModel.goHome() }
                                    )
                                    VirtualAppType.NOTES -> NotesApp(
                                        notes = notes,
                                        onAddNote = { t, c, col -> viewModel.addNote(t, c, col) },
                                        onUpdateNote = { id, t, c, col -> viewModel.updateNote(id, t, c, col) },
                                        onDeleteNote = { id -> viewModel.deleteNote(id) },
                                        onClose = { viewModel.goHome() }
                                    )
                                    VirtualAppType.CALCULATOR -> CalculatorApp(
                                        history = calcHistory,
                                        onAddHistory = { expr, res -> viewModel.addCalcHistory(expr, res) },
                                        onClearHistory = { viewModel.clearCalcHistory() },
                                        onClose = { viewModel.goHome() }
                                    )
                                    VirtualAppType.SETTINGS -> SettingsApp(
                                        phoneState = phoneState,
                                        onSetWallpaper = { id -> viewModel.setWallpaper(id) },
                                        onUpdateDeviceName = { name -> viewModel.updateDeviceName(name) },
                                        onToggleFrameMode = { viewModel.toggleFrameMode() },
                                        onToggleDarkMode = { viewModel.toggleDarkMode() },
                                        onSetPin = { pin, enabled -> viewModel.setPin(pin, enabled) },
                                        onSetBattery = { percent -> viewModel.setBatteryPercent(percent) },
                                        onClose = { viewModel.goHome() }
                                    )
                                    VirtualAppType.GAME -> Game2048App(
                                        highScore = gameHighScore,
                                        onRecordScore = { score -> viewModel.recordGameScore(score) },
                                        onClose = { viewModel.goHome() }
                                    )
                                    VirtualAppType.CLOCK -> ClockApp(onClose = { viewModel.goHome() })
                                    VirtualAppType.MESSAGES -> MessagesApp(onClose = { viewModel.goHome() })
                                }
                            }
                        }
                    }

                    // Recent Apps Multitasking Overlay
                    RecentsScreen(
                        isOpen = isRecentsOpen,
                        recentApps = recentApps,
                        onSelectApp = { app -> viewModel.openApp(app) },
                        onCloseApp = { app -> viewModel.closeRecentApp(app) },
                        onCloseAll = { viewModel.clearAllRecents() },
                        onDismiss = { viewModel.toggleRecents() }
                    )

                    // Notification & Quick Settings Dropdown Shade
                    NotificationShade(
                        isOpen = isNotificationShadeOpen,
                        phoneState = phoneState,
                        notifications = notifications,
                        onClose = { viewModel.toggleNotificationShade() },
                        onToggleWifi = { viewModel.toggleWifi() },
                        onToggleBluetooth = { viewModel.toggleBluetooth() },
                        onToggleAirplane = { viewModel.toggleAirplane() },
                        onToggleFlashlight = { viewModel.toggleFlashlight() },
                        onToggleDarkMode = { viewModel.toggleDarkMode() },
                        onToggleFrameMode = { viewModel.toggleFrameMode() },
                        onBrightnessChange = { b -> viewModel.setBrightness(b) },
                        onOpenSettings = {
                            viewModel.toggleNotificationShade()
                            viewModel.openApp(VirtualAppType.SETTINGS)
                        },
                        onDismissNotification = { id -> viewModel.dismissNotification(id) },
                        onClearAllNotifications = { viewModel.clearNotifications() }
                    )
                }

                // Bottom Simulated Navigation Bar (always active for virtual navigation)
                SimulatedNavBar(
                    onBack = {
                        when {
                            isNotificationShadeOpen -> viewModel.toggleNotificationShade()
                            isRecentsOpen -> viewModel.toggleRecents()
                            currentApp != null -> viewModel.goHome()
                            else -> showExitAppDialog = true
                        }
                    },
                    onHome = { viewModel.goHome() },
                    onRecents = { viewModel.toggleRecents() }
                )
            }
        }
    }

    // Exit App Confirmation Dialog (preserves original Android OS system)
    if (showExitAppDialog) {
        AlertDialog(
            onDismissRequest = { showExitAppDialog = false },
            title = {
                Text(
                    text = "الخروج من الهاتف السحابي",
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = "هل ترغب في إغلاق تطبيق الهاتف السحابي والرجوع إلى الشاشة الرئيسية لهاتفك الحقيقي؟ بياناتك وملاحظاتك محفوظة بالكامل.",
                    color = Color(0xFFCBD5E1),
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExitAppDialog = false
                        (context as? Activity)?.finish()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("خروج من التطبيق")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitAppDialog = false }) {
                    Text("البقاء في الهاتف الوهمي", color = Color(0xFF38BDF8))
                }
            },
            containerColor = Color(0xFF1E293B)
        )
    }
}
