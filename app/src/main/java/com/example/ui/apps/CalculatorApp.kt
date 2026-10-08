package com.example.ui.apps

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CalcHistoryItem
import kotlin.math.sqrt

@Composable
fun CalculatorApp(
    history: List<CalcHistoryItem>,
    onAddHistory: (String, String) -> Unit,
    onClearHistory: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var displayExpression by remember { mutableStateOf("0") }
    var resultText by remember { mutableStateOf("") }
    var showHistory by remember { mutableStateOf(false) }

    fun onDigit(d: String) {
        if (displayExpression == "0" || displayExpression == "Error") {
            displayExpression = d
        } else {
            displayExpression += d
        }
    }

    fun onOp(op: String) {
        if (displayExpression.isNotEmpty() && !displayExpression.endsWith(" ")) {
            displayExpression += " $op "
        }
    }

    fun calculate() {
        try {
            val tokens = displayExpression.trim().split(" ")
            if (tokens.isEmpty()) return

            var current = tokens[0].toDouble()
            var i = 1
            while (i < tokens.size) {
                val op = tokens[i]
                if (i + 1 < tokens.size) {
                    val next = tokens[i + 1].toDouble()
                    when (op) {
                        "+" -> current += next
                        "-" -> current -= next
                        "×" -> current *= next
                        "÷" -> current = if (next == 0.0) Double.NaN else current / next
                        "%" -> current %= next
                    }
                }
                i += 2
            }

            if (current.isNaN()) {
                resultText = "Error"
            } else {
                val formatted = if (current % 1.0 == 0.0) current.toLong().toString() else "%.4f".format(current).trimEnd('0').trimEnd('.')
                resultText = formatted
                onAddHistory(displayExpression, formatted)
            }
        } catch (e: Exception) {
            resultText = "Error"
        }
    }

    Surface(
        color = Color(0xFF0F172A),
        contentColor = Color.White,
        modifier = modifier
            .fillMaxSize()
            .testTag("calculator_app")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            Surface(
                color = Color(0xFF1E293B),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "الحاسبة (Calculator)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Row {
                        IconButton(onClick = { showHistory = !showHistory }) {
                            Icon(Icons.Default.History, contentDescription = "History", tint = if (showHistory) Color(0xFF38BDF8) else Color.LightGray)
                        }
                        IconButton(onClick = onClose) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                        }
                    }
                }
            }

            if (showHistory) {
                // History Panel
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color(0xFF111827))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "سجل العمليات الحسابية", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        if (history.isNotEmpty()) {
                            IconButton(onClick = onClearHistory) {
                                Icon(Icons.Default.DeleteSweep, contentDescription = "Clear", tint = Color(0xFFF87171))
                            }
                        }
                    }

                    if (history.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = "السجل فارغ", color = Color.Gray, fontSize = 13.sp)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(history, key = { it.id }) { h ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth().clickable {
                                        displayExpression = h.result
                                        resultText = ""
                                        showHistory = false
                                    }
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(text = h.expression, fontSize = 12.sp, color = Color(0xFF94A3B8))
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(text = "= ${h.result}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Display Area
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = displayExpression,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.LightGray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.End
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (resultText.isNotEmpty()) "= $resultText" else "",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF38BDF8),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.End
                    )
                }

                // Keypad
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val rows = listOf(
                        listOf("AC" to Color(0xFFEF4444), "√" to Color(0xFF475569), "%" to Color(0xFF475569), "÷" to Color(0xFF0284C7)),
                        listOf("7" to Color(0xFF1E293B), "8" to Color(0xFF1E293B), "9" to Color(0xFF1E293B), "×" to Color(0xFF0284C7)),
                        listOf("4" to Color(0xFF1E293B), "5" to Color(0xFF1E293B), "6" to Color(0xFF1E293B), "-" to Color(0xFF0284C7)),
                        listOf("1" to Color(0xFF1E293B), "2" to Color(0xFF1E293B), "3" to Color(0xFF1E293B), "+" to Color(0xFF0284C7)),
                        listOf("±" to Color(0xFF1E293B), "0" to Color(0xFF1E293B), "." to Color(0xFF1E293B), "=" to Color(0xFF10B981))
                    )

                    rows.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            row.forEach { (label, bg) ->
                                CalcButton(
                                    label = label,
                                    bgColor = bg,
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        when (label) {
                                            "AC" -> {
                                                displayExpression = "0"
                                                resultText = ""
                                            }
                                            "=" -> calculate()
                                            "+", "-", "×", "÷", "%" -> onOp(label)
                                            "√" -> {
                                                try {
                                                    val v = displayExpression.toDouble()
                                                    val res = sqrt(v)
                                                    displayExpression = if (res % 1.0 == 0.0) res.toLong().toString() else "%.4f".format(res).trimEnd('0').trimEnd('.')
                                                    resultText = displayExpression
                                                } catch (e: Exception) {
                                                    resultText = "Error"
                                                }
                                            }
                                            "±" -> {
                                                if (displayExpression.startsWith("-")) {
                                                    displayExpression = displayExpression.removePrefix("-")
                                                } else if (displayExpression != "0") {
                                                    displayExpression = "-$displayExpression"
                                                }
                                            }
                                            else -> onDigit(label)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalcButton(
    label: String,
    bgColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(54.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .testTag("calc_btn_$label"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}
