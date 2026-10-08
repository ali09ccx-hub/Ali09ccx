package com.example.ui.apps

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class MessageBubble(
    val id: String = System.currentTimeMillis().toString(),
    val sender: String,
    val text: String,
    val isMe: Boolean,
    val time: String = "الآن"
)

@Composable
fun MessagesApp(
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val messages = remember {
        mutableStateListOf(
            MessageBubble(
                sender = "مساعد السحابة (Cloud AI)",
                text = "مرحباً بك! أنا مساعد هاتفك السحابي الافتراضي. كيف يمكنني خدمتك اليوم؟",
                isMe = false,
                time = "10:00"
            ),
            MessageBubble(
                sender = "النظام",
                text = "تم ربط البيئة الافتراضية بنجاح. جميع تطبيقاتك معزولة وآمنة.",
                isMe = false,
                time = "10:02"
            )
        )
    }
    var inputText by remember { mutableStateOf("") }

    fun sendMessage() {
        if (inputText.isNotBlank()) {
            val userMsg = inputText.trim()
            messages.add(
                MessageBubble(
                    sender = "أنا",
                    text = userMsg,
                    isMe = true
                )
            )
            inputText = ""

            // Simple auto response
            val botResponse = when {
                userMsg.contains("مرحبا", ignoreCase = true) || userMsg.contains("سلام", ignoreCase = true) ->
                    "أهلاً وسهلاً بك في هاتفك السحابي المستقل!"
                userMsg.contains("خلفية", ignoreCase = true) ->
                    "يمكنك تغيير خلفية الهاتف في أي وقت من خلال تطبيق 'الإعدادات' أو 'المعرض'."
                userMsg.contains("ملاحظة", ignoreCase = true) ->
                    "تطبيق 'الملاحظات' يحفظ كافة بياناتك بشكل دائم في الذاكرة التخزينية."
                userMsg.contains("من أنت", ignoreCase = true) ->
                    "أنا المساعد الآلي لهاتفك السحابي الذكي CloudDroid OS."
                else ->
                    "تم استلام رسالتك: \"$userMsg\". بيئة الهاتف الافتراضي تعمل بكفاءة 100%!"
            }

            messages.add(
                MessageBubble(
                    sender = "مساعد السحابة",
                    text = botResponse,
                    isMe = false
                )
            )
        }
    }

    Surface(
        color = Color(0xFF0F172A),
        contentColor = Color.White,
        modifier = modifier
            .fillMaxSize()
            .testTag("messages_app")
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
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0284C7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.SmartToy, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "مساعد السحابة الذكي", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(text = "متصل الآن • Cloud Node", fontSize = 10.sp, color = Color(0xFF4ADE80))
                        }
                    }

                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                    }
                }
            }

            // Messages List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (msg.isMe) Arrangement.End else Arrangement.Start
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (msg.isMe) Color(0xFF0284C7) else Color(0xFF1E293B)
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                if (!msg.isMe) {
                                    Text(text = msg.sender, fontSize = 10.sp, color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(2.dp))
                                }
                                Text(text = msg.text, fontSize = 13.sp, color = Color.White)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = msg.time,
                                    fontSize = 9.sp,
                                    color = if (msg.isMe) Color(0xFFBAE6FD) else Color(0xFF94A3B8),
                                    modifier = Modifier.align(Alignment.End)
                                )
                            }
                        }
                    }
                }
            }

            // Input Bar
            Surface(
                color = Color(0xFF1E293B),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("اكتب رسالة...", color = Color.Gray, fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF38BDF8),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedContainerColor = Color(0xFF0F172A),
                            unfocusedContainerColor = Color(0xFF0F172A)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("messages_input")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { sendMessage() },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0284C7))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
