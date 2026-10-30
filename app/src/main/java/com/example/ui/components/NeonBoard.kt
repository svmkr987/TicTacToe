package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.PlayerMark
import com.example.data.model.WinningLine
import com.example.data.model.WinningLineType
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonCyanGlow
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPinkGlow

@Composable
fun NeonBoard(
    board: List<PlayerMark>,
    winningLine: WinningLine?,
    lastMoveIndex: Int?,
    onCellClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 440.dp)
            .aspectRatio(1f)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        val boardSize = maxWidth

        // Cyber Grid Outer Frame
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = NeonCyanGlow,
                    spotColor = NeonPinkGlow
                )
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                        )
                    )
                )
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            NeonCyan.copy(alpha = 0.7f),
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                            NeonPink.copy(alpha = 0.7f)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(8.dp)
        ) {
            // Background Grid Dividers & Laser Winning Line
            BoardCanvasOverlay(winningLine = winningLine)

            // 3x3 Interactive Cells
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                for (row in 0..2) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (col in 0..2) {
                            val index = row * 3 + col
                            val mark = board[index]
                            val isWinningCell = winningLine?.indices?.contains(index) == true
                            val isLastMove = lastMoveIndex == index

                            NeonCell(
                                mark = mark,
                                isWinningCell = isWinningCell,
                                isLastMove = isLastMove,
                                onClick = { onCellClick(index) },
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxSize()
                                    .padding(4.dp)
                                    .testTag("grid_cell_$index")
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NeonCell(
    mark: PlayerMark,
    isWinningCell: Boolean,
    isLastMove: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Pulse animation for winning or highlighted cells
    val infiniteTransition = rememberInfiniteTransition(label = "cell_pulse")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_glow"
    )

    val cellBgColor = when {
        isWinningCell && mark == PlayerMark.X -> NeonCyan.copy(alpha = 0.22f * pulseGlow)
        isWinningCell && mark == PlayerMark.O -> NeonPink.copy(alpha = 0.22f * pulseGlow)
        isLastMove -> MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
        else -> Color.Transparent
    }

    val cellBorderColor = when {
        isWinningCell && mark == PlayerMark.X -> NeonCyan.copy(alpha = pulseGlow)
        isWinningCell && mark == PlayerMark.O -> NeonPink.copy(alpha = pulseGlow)
        isLastMove -> MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
        else -> Color.Transparent
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(cellBgColor)
            .border(
                width = if (isWinningCell) 2.dp else if (isLastMove) 1.dp else 0.dp,
                color = cellBorderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = NeonCyan),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        when (mark) {
            PlayerMark.X -> AnimatedXMark(isWinning = isWinningCell)
            PlayerMark.O -> AnimatedOMark(isWinning = isWinningCell)
            PlayerMark.NONE -> {
                // Subtle high-tech center dot
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color = Color.White.copy(alpha = 0.06f),
                            radius = 3.dp.toPx()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedXMark(isWinning: Boolean) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
        )
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp)
    ) {
        val p = progress.value
        val strokeWidth = 6.dp.toPx()
        val glowWidth = 14.dp.toPx()

        val start1 = Offset(0f, 0f)
        val end1 = Offset(size.width * p, size.height * p)

        val start2 = Offset(size.width, 0f)
        val end2 = Offset(size.width - (size.width * p), size.height * p)

        // Outer Neon Glow
        drawLine(
            color = NeonCyanGlow,
            start = start1,
            end = end1,
            strokeWidth = glowWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = NeonCyanGlow,
            start = start2,
            end = end2,
            strokeWidth = glowWidth,
            cap = StrokeCap.Round
        )

        // Core Neon Stroke
        drawLine(
            color = NeonCyan,
            start = start1,
            end = end1,
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = NeonCyan,
            start = start2,
            end = end2,
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Center White Hot Flash
        drawLine(
            color = Color.White,
            start = start1,
            end = end1,
            strokeWidth = strokeWidth * 0.35f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.White,
            start = start2,
            end = end2,
            strokeWidth = strokeWidth * 0.35f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun AnimatedOMark(isWinning: Boolean) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing)
        )
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp)
    ) {
        val p = progress.value
        val strokeWidth = 6.dp.toPx()
        val glowWidth = 14.dp.toPx()
        val sweepAngle = 360f * p

        // Outer Neon Glow
        drawArc(
            color = NeonPinkGlow,
            startAngle = -90f,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(width = glowWidth, cap = StrokeCap.Round)
        )

        // Core Neon Stroke
        drawArc(
            color = NeonPink,
            startAngle = -90f,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Center White Hot Flash
        drawArc(
            color = Color.White,
            startAngle = -90f,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(width = strokeWidth * 0.35f, cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun BoardCanvasOverlay(winningLine: WinningLine?) {
    val lineProgress = remember { Animatable(0f) }

    LaunchedEffect(winningLine) {
        if (winningLine != null) {
            lineProgress.snapTo(0f)
            lineProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
            )
        } else {
            lineProgress.snapTo(0f)
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Inner Grid Dividers (Vertical)
        drawLine(
            color = Color(0xFF1E2D4A).copy(alpha = 0.7f),
            start = Offset(w * 0.333f, h * 0.04f),
            end = Offset(w * 0.333f, h * 0.96f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color(0xFF1E2D4A).copy(alpha = 0.7f),
            start = Offset(w * 0.666f, h * 0.04f),
            end = Offset(w * 0.666f, h * 0.96f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Inner Grid Dividers (Horizontal)
        drawLine(
            color = Color(0xFF1E2D4A).copy(alpha = 0.7f),
            start = Offset(w * 0.04f, h * 0.333f),
            end = Offset(w * 0.96f, h * 0.333f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color(0xFF1E2D4A).copy(alpha = 0.7f),
            start = Offset(w * 0.04f, h * 0.666f),
            end = Offset(w * 0.96f, h * 0.666f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Draw Laser Winning Line if active
        if (winningLine != null && lineProgress.value > 0f) {
            drawWinningLaserLine(winningLine.type, lineProgress.value)
        }
    }
}

private fun DrawScope.drawWinningLaserLine(type: WinningLineType, progress: Float) {
    val w = size.width
    val h = size.height

    val (start, fullEnd) = when (type) {
        WinningLineType.ROW_0 -> Offset(w * 0.08f, h * 0.166f) to Offset(w * 0.92f, h * 0.166f)
        WinningLineType.ROW_1 -> Offset(w * 0.08f, h * 0.500f) to Offset(w * 0.92f, h * 0.500f)
        WinningLineType.ROW_2 -> Offset(w * 0.08f, h * 0.833f) to Offset(w * 0.92f, h * 0.833f)
        WinningLineType.COL_0 -> Offset(w * 0.166f, h * 0.08f) to Offset(w * 0.166f, h * 0.92f)
        WinningLineType.COL_1 -> Offset(w * 0.500f, h * 0.08f) to Offset(w * 0.500f, h * 0.92f)
        WinningLineType.COL_2 -> Offset(w * 0.833f, h * 0.08f) to Offset(w * 0.833f, h * 0.92f)
        WinningLineType.DIAG_MAIN -> Offset(w * 0.10f, h * 0.10f) to Offset(w * 0.90f, h * 0.90f)
        WinningLineType.DIAG_ANTI -> Offset(w * 0.90f, h * 0.10f) to Offset(w * 0.10f, h * 0.90f)
    }

    val currentEnd = Offset(
        x = start.x + (fullEnd.x - start.x) * progress,
        y = start.y + (fullEnd.y - start.y) * progress
    )

    // Outer Laser Halo
    drawLine(
        color = Color(0x9900F5FF),
        start = start,
        end = currentEnd,
        strokeWidth = 16.dp.toPx(),
        cap = StrokeCap.Round
    )

    // Vibrant Laser Core
    drawLine(
        color = NeonCyan,
        start = start,
        end = currentEnd,
        strokeWidth = 6.dp.toPx(),
        cap = StrokeCap.Round
    )

    // Pure White Laser Beam Center
    drawLine(
        color = Color.White,
        start = start,
        end = currentEnd,
        strokeWidth = 2.5.dp.toPx(),
        cap = StrokeCap.Round
    )
}
