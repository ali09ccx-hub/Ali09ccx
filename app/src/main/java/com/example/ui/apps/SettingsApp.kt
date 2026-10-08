package com.example.ui.apps

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.VirtualPhoneState
import com.example.data.WallpaperManager

@Composable
fun SettingsApp(
    phoneState: VirtualPhoneState,
    onSetWallpaper: (Int) -> Unit,
    onUpdateDeviceName: (String) -> Unit,
    onToggleFrameMode: () -> Unit,
    onToggleDarkMode: () -> Unit,
    onSetPin: (String, Boolean) -> Unit,
    onSetBattery: (Int) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val wallpapers = remember { WallpaperManager.getWallpapers() }
    var showPinDialog by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf(phoneState.pinCode) }
    var showNameDialog by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf(phoneState.deviceName) }

    Surface(
        color = Color(0xFF0F172A),
        contentColor = Color.White,
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_app")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Surface(
                color = Color(0xFF1E293B),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "إعدادات الهاتف (Settings)",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section: Wallpapers
                item {
                    SettingsSection(title = "خلفية الشاشة (Wallpapers)", icon = Icons.Default.Palette) {
                        Text(
                            text = "اختر مظهراً لهاتفك السحابي وسيتم حفظه تلقائياً:",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(wallpapers) { wp ->
                                val isSelected = wp.id == phoneState.wallpaperId
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clickable { onSetWallpaper(wp.id) }
                                        .testTag("wallpaper_item_${wp.id}")
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(72.dp, 110.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .border(
                                                width = if (isSelected) 3.dp else 1.dp,
                                                color = if (isSelected) Color(0xFF38BDF8) else Color(0xFF475569),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .then(
                                                if (wp.gradientBrush != null) Modifier.background(wp.gradientBrush)
                                                else Modifier.background(wp.previewColor)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (wp.drawableResId != null) {
                                            Image(
                                                painter = painterResource(id = wp.drawableResId),
                                                contentDescription = wp.name,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }
                                        if (isSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFF38BDF8)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = wp.arabicName, fontSize = 11.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }

                // Section: Device Appearance & Frame Mode
                item {
                    SettingsSection(title = "المظهر وإطار الهاتف", icon = Icons.Default.PhoneAndroid) {
                        SettingsToggleRow(
                            title = "وضع إطار الهاتف الذكي",
                            subtitle = "عرض إطار خارجي واقعي مع نوتش الكاميرا",
                            checked = phoneState.showDeviceFrame,
                            onCheckedChange = { onToggleFrameMode() },
                            testTag = "toggle_frame_switch"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        SettingsToggleRow(
                            title = "الوضع الداكن للنظام",
                            subtitle = "واجهات داكنة مريحة للعين",
                            checked = phoneState.isDarkMode,
                            onCheckedChange = { onToggleDarkMode() },
                            testTag = "toggle_dark_switch"
                        )
                    }
                }

                // Section: Security & PIN
                item {
                    SettingsSection(title = "شاشة القفل والأمان (PIN)", icon = Icons.Default.Lock) {
                        SettingsToggleRow(
                            title = "قفل الشاشة برمز PIN",
                            subtitle = if (phoneState.isPinEnabled) "الرمز الحالي: ${phoneState.pinCode}" else "غير مفعّل (فتح سريع)",
                            checked = phoneState.isPinEnabled,
                            onCheckedChange = { isChecked ->
                                if (isChecked) {
                                    showPinDialog = true
                                } else {
                                    onSetPin(phoneState.pinCode, false)
                                }
                            },
                            testTag = "toggle_pin_switch"
                        )

                        if (phoneState.isPinEnabled) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { showPinDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155))
                            ) {
                                Text("تغيير رمز PIN", fontSize = 12.sp)
                            }
                        }
                    }
                }

                // Section: Battery Simulation
                item {
                    SettingsSection(title = "محاكاة البطارية", icon = Icons.Default.PowerSettingsNew) {
                        Text(
                            text = "نسبة شحن البطارية الحالية: ${phoneState.batteryPercent}%",
                            fontSize = 12.sp,
                            color = Color(0xFFCBD5E1)
                        )
                        Slider(
                            value = phoneState.batteryPercent.toFloat(),
                            onValueChange = { onSetBattery(it.toInt()) },
                            valueRange = 5f..100f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF4ADE80),
                                activeTrackColor = Color(0xFF4ADE80),
                                inactiveTrackColor = Color.DarkGray
                            )
                        )
                    }
                }

                // Section: Virtual Specs & Info
                item {
                    SettingsSection(title = "مواصفات الهاتف السحابي", icon = Icons.Default.Info) {
                        SpecRow("اسم الجهاز", phoneState.deviceName) {
                            nameInput = phoneState.deviceName
                            showNameDialog = true
                        }
                        SpecRow("نظام التشغيل", "CloudDroid OS 15 (Native Compose)")
                        SpecRow("الخادم السحابي", phoneState.serverRegion)
                        SpecRow("الذاكرة العشوائية (RAM)", "12 GB LPDDR5X")
                        SpecRow("سعة التخزين", "256 GB (مستخدم 38.4 GB)")
                        SpecRow("معدل التحديث", "120 Hz Ultra Smooth")
                    }
                }
            }
        }

        // PIN Setup Dialog
        if (showPinDialog) {
            AlertDialog(
                onDismissRequest = { showPinDialog = false },
                title = { Text("تعيين رمز PIN المكوّن من 4 أرقام", color = Color.White) },
                text = {
                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) pinInput = it },
                        label = { Text("أدخل 4 أرقام") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (pinInput.length == 4) {
                                onSetPin(pinInput, true)
                                showPinDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                    ) {
                        Text("تأكيد")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPinDialog = false }) {
                        Text("إلغاء", color = Color.LightGray)
                    }
                },
                containerColor = Color(0xFF1E293B)
            )
        }

        // Device Name Dialog
        if (showNameDialog) {
            AlertDialog(
                onDismissRequest = { showNameDialog = false },
                title = { Text("تغيير اسم الهاتف الافتراضي", color = Color.White) },
                text = {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (nameInput.isNotBlank()) {
                                onUpdateDeviceName(nameInput)
                                showNameDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                    ) {
                        Text("حفظ")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showNameDialog = false }) {
                        Text("إلغاء", color = Color.LightGray)
                    }
                },
                containerColor = Color(0xFF1E293B)
            )
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            Text(text = subtitle, fontSize = 11.sp, color = Color(0xFF94A3B8))
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF0284C7),
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color(0xFF334155)
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}

@Composable
private fun SpecRow(label: String, value: String, onClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = Color(0xFF94A3B8))
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.White)
    }
}
