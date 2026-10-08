package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun SimulatedDeviceFrame(
    showFrame: Boolean,
    onToggleFrame: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
            .testTag("simulated_device_container")
    ) {
        if (!showFrame) {
            // Full Screen Mode (direct rendering)
            Box(modifier = Modifier.fillMaxSize()) {
                content()
            }
        } else {
            // Framed Smartphone Mode
            BoxWithConstraints(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                val frameMaxWidth = 440.dp
                val framePaddingVertical = if (maxHeight > 700.dp) 16.dp else 4.dp
                val frameCorner = 36.dp

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = frameMaxWidth)
                        .padding(horizontal = 8.dp, vertical = framePaddingVertical)
                        .shadow(elevation = 24.dp, shape = RoundedCornerShape(frameCorner), clip = false)
                        .clip(RoundedCornerShape(frameCorner))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF334155),
                                    Color(0xFF0F172A),
                                    Color(0xFF1E293B)
                                )
                            )
                        )
                        .border(
                            width = 3.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF64748B),
                                    Color(0xFF1E293B),
                                    Color(0xFF475569)
                                )
                            ),
                            shape = RoundedCornerShape(frameCorner)
                        )
                        .padding(6.dp)
                ) {
                    // Inner Screen Bezel
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(frameCorner - 6.dp))
                            .background(Color.Black)
                    ) {
                        // Top Punch-hole camera notch area
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(16.dp)
                                .background(Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            // Punch hole camera
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF09090B))
                                    .border(0.5.dp, Color(0xFF27272A), CircleShape)
                            )
                        }

                        // Main Screen Display Area
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            content()
                        }
                    }
                }
            }
        }

        // Floating Quick Switcher between Full Screen and Frame Mode
        FloatingActionButton(
            onClick = onToggleFrame,
            containerColor = Color(0xFF0284C7).copy(alpha = 0.85f),
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 60.dp, end = 12.dp)
                .size(40.dp)
                .testTag("frame_toggle_fab")
        ) {
            Icon(
                imageVector = if (showFrame) Icons.Default.CropFree else Icons.Default.PhoneAndroid,
                contentDescription = if (showFrame) "Full Screen Mode" else "Device Frame Mode",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
