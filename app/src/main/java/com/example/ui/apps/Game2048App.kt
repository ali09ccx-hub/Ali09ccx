package com.example.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

@Composable
fun Game2048App(
    highScore: Int,
    onRecordScore: (Int) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var board by remember { mutableStateOf(initBoard()) }
    var currentScore by remember { mutableIntStateOf(0) }
    var isGameOver by remember { mutableStateOf(false) }

    fun restartGame() {
        board = initBoard()
        currentScore = 0
        isGameOver = false
    }

    fun handleMove(direction: Direction) {
        if (isGameOver) return
        val (newBoard, addedScore, moved) = moveBoard(board, direction)
        if (moved) {
            val boardWithSpawn = spawnTile(newBoard)
            board = boardWithSpawn
            currentScore += addedScore
            if (currentScore > highScore) {
                onRecordScore(currentScore)
            }
            if (checkGameOver(boardWithSpawn)) {
                isGameOver = true
                onRecordScore(currentScore)
            }
        }
    }

    Surface(
        color = Color(0xFF0F172A),
        contentColor = Color.White,
        modifier = modifier
            .fillMaxSize()
            .testTag("game_2048_app")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                    Text(
                        text = "لعبة 2048 السحابية",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Row {
                        IconButton(onClick = { restartGame() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Restart", tint = Color(0xFF38BDF8))
                        }
                        IconButton(onClick = onClose) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Score Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ScoreCard("النقاط الحالية", currentScore)
                ScoreCard("أعلى رقم قياسي", maxOf(highScore, currentScore))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2048 Board Container with drag gestures
            var totalDragX by remember { mutableStateOf(0f) }
            var totalDragY by remember { mutableStateOf(0f) }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E293B))
                    .padding(8.dp)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {
                                totalDragX = 0f
                                totalDragY = 0f
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                totalDragX += dragAmount.x
                                totalDragY += dragAmount.y
                            },
                            onDragEnd = {
                                val absX = kotlin.math.abs(totalDragX)
                                val absY = kotlin.math.abs(totalDragY)
                                val threshold = 40f
                                if (absX > threshold || absY > threshold) {
                                    if (absX > absY) {
                                        if (totalDragX > 0) handleMove(Direction.RIGHT)
                                        else handleMove(Direction.LEFT)
                                    } else {
                                        if (totalDragY > 0) handleMove(Direction.DOWN)
                                        else handleMove(Direction.UP)
                                    }
                                }
                            }
                        )
                    }
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (r in 0 until 4) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (c in 0 until 4) {
                                TileView(
                                    value = board[r][c],
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxSize()
                                )
                            }
                        }
                    }
                }

                if (isGameOver) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.85f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "انتهت اللعبة!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "النتيجة: $currentScore", fontSize = 16.sp, color = Color.White)
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { restartGame() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                            ) {
                                Text("إعادة المحاولة")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Directional On-Screen Controls for easy tapping
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = { handleMove(Direction.UP) },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF334155))
                ) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Up", tint = Color.White)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    IconButton(
                        onClick = { handleMove(Direction.LEFT) },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF334155))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Left", tint = Color.White)
                    }

                    IconButton(
                        onClick = { handleMove(Direction.DOWN) },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF334155))
                    ) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Down", tint = Color.White)
                    }

                    IconButton(
                        onClick = { handleMove(Direction.RIGHT) },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF334155))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Right", tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreCard(title: String, score: Int) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.width(130.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 11.sp, color = Color(0xFF94A3B8))
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = "$score", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
        }
    }
}

@Composable
private fun TileView(value: Int, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = getTileColors(value)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        if (value > 0) {
            Text(
                text = "$value",
                fontSize = if (value > 512) 16.sp else 20.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

private fun getTileColors(value: Int): Pair<Color, Color> {
    return when (value) {
        0 -> Color(0xFF0F172A) to Color.Transparent
        2 -> Color(0xFFE2E8F0) to Color(0xFF0F172A)
        4 -> Color(0xFFFED7AA) to Color(0xFF0F172A)
        8 -> Color(0xFFFB923C) to Color.White
        16 -> Color(0xFFF97316) to Color.White
        32 -> Color(0xFFEA580C) to Color.White
        64 -> Color(0xFFDC2626) to Color.White
        128 -> Color(0xFFFDE047) to Color(0xFF0F172A)
        256 -> Color(0xFFFACC15) to Color(0xFF0F172A)
        512 -> Color(0xFFEAB308) to Color.White
        1024 -> Color(0xFFCA8A04) to Color.White
        2048 -> Color(0xFFA855F7) to Color.White
        else -> Color(0xFF7C3AED) to Color.White
    }
}

private enum class Direction { LEFT, RIGHT, UP, DOWN }

private fun initBoard(): Array<IntArray> {
    val b = Array(4) { IntArray(4) { 0 } }
    val b1 = spawnTile(b)
    return spawnTile(b1)
}

private fun spawnTile(board: Array<IntArray>): Array<IntArray> {
    val copy = Array(4) { r -> board[r].clone() }
    val emptyCells = mutableListOf<Pair<Int, Int>>()
    for (r in 0 until 4) {
        for (c in 0 until 4) {
            if (copy[r][c] == 0) emptyCells.add(r to c)
        }
    }
    if (emptyCells.isNotEmpty()) {
        val (r, c) = emptyCells.random()
        copy[r][c] = if (Random.nextFloat() < 0.9f) 2 else 4
    }
    return copy
}

private fun moveBoard(board: Array<IntArray>, dir: Direction): Triple<Array<IntArray>, Int, Boolean> {
    var moved = false
    var scoreAdded = 0
    val result = Array(4) { r -> board[r].clone() }

    fun processLine(line: List<Int>): Pair<List<Int>, Int> {
        val filtered = line.filter { it != 0 }
        val merged = mutableListOf<Int>()
        var i = 0
        var score = 0
        while (i < filtered.size) {
            if (i + 1 < filtered.size && filtered[i] == filtered[i + 1]) {
                val sum = filtered[i] * 2
                merged.add(sum)
                score += sum
                i += 2
            } else {
                merged.add(filtered[i])
                i++
            }
        }
        while (merged.size < 4) merged.add(0)
        return merged to score
    }

    when (dir) {
        Direction.LEFT -> {
            for (r in 0 until 4) {
                val line = result[r].toList()
                val (newLine, score) = processLine(line)
                scoreAdded += score
                for (c in 0 until 4) {
                    if (result[r][c] != newLine[c]) moved = true
                    result[r][c] = newLine[c]
                }
            }
        }
        Direction.RIGHT -> {
            for (r in 0 until 4) {
                val line = result[r].toList().reversed()
                val (newLine, score) = processLine(line)
                val reversed = newLine.reversed()
                scoreAdded += score
                for (c in 0 until 4) {
                    if (result[r][c] != reversed[c]) moved = true
                    result[r][c] = reversed[c]
                }
            }
        }
        Direction.UP -> {
            for (c in 0 until 4) {
                val line = (0 until 4).map { result[it][c] }
                val (newLine, score) = processLine(line)
                scoreAdded += score
                for (r in 0 until 4) {
                    if (result[r][c] != newLine[r]) moved = true
                    result[r][c] = newLine[r]
                }
            }
        }
        Direction.DOWN -> {
            for (c in 0 until 4) {
                val line = (0 until 4).map { result[it][c] }.reversed()
                val (newLine, score) = processLine(line)
                val reversed = newLine.reversed()
                scoreAdded += score
                for (r in 0 until 4) {
                    if (result[r][c] != reversed[r]) moved = true
                    result[r][c] = reversed[r]
                }
            }
        }
    }

    return Triple(result, scoreAdded, moved)
}

private fun checkGameOver(board: Array<IntArray>): Boolean {
    for (r in 0 until 4) {
        for (c in 0 until 4) {
            if (board[r][c] == 0) return false
            if (r + 1 < 4 && board[r][c] == board[r + 1][c]) return false
            if (c + 1 < 4 && board[r][c] == board[r][c + 1]) return false
        }
    }
    return true
}
