package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun QrCodeRenderer(
    content: String,
    modifier: Modifier = Modifier,
    size: Dp = 190.dp
) {
    // Generate deterministic 21x21 QR pattern grid
    val grid = remember(content) {
        val n = 21
        val matrix = Array(n) { BooleanArray(n) }

        // Draw Finder Patterns (7x7 eyes at (0,0), (0, 14), (14, 0))
        fun drawFinder(startX: Int, startY: Int) {
            for (r in 0 until 7) {
                for (c in 0 until 7) {
                    val isBorder = r == 0 || r == 6 || c == 0 || c == 6
                    val isInner = r in 2..4 && c in 2..4
                    matrix[startX + r][startY + c] = isBorder || isInner
                }
            }
        }
        drawFinder(0, 0)
        drawFinder(0, 14)
        drawFinder(14, 0)

        // Timing patterns
        for (i in 8 until 13) {
            matrix[6][i] = (i % 2 == 0)
            matrix[i][6] = (i % 2 == 0)
        }

        // Pseudo-random deterministic payload bits based on content hash
        var hash = content.hashCode()
        for (r in 0 until n) {
            for (c in 0 until n) {
                // Avoid overwriting finder areas
                val inFinder1 = r < 8 && c < 8
                val inFinder2 = r < 8 && c >= 13
                val inFinder3 = r >= 13 && c < 8
                val inTiming = (r == 6 && c in 8..13) || (c == 6 && r in 8..13)

                if (!inFinder1 && !inFinder2 && !inFinder3 && !inTiming) {
                    hash = (hash * 31 + r * 17 + c * 23)
                    matrix[r][c] = (hash % 3 == 0 || (r + c) % 3 == 0)
                }
            }
        }
        matrix
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(12.dp)
    ) {
        Canvas(modifier = Modifier.size(size - 24.dp)) {
            val n = 21
            val cellSize = this.size.width / n
            val darkColor = Color(0xFF09111E)

            for (r in 0 until n) {
                for (c in 0 until n) {
                    if (grid[r][c]) {
                        drawRect(
                            color = darkColor,
                            topLeft = Offset(c * cellSize, r * cellSize),
                            size = Size(cellSize * 0.98f, cellSize * 0.98f)
                        )
                    }
                }
            }
        }
    }
}
