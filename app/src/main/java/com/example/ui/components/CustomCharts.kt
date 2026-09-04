package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class ChartSlice(
    val label: String,
    val value: Float,
    val color: Color
)

data class BarItem(
    val label: String,
    val value: Float,
    val color: Color
)

@Composable
fun DonutChart(
    slices: List<ChartSlice>,
    title: String,
    centerSubtext: String = "",
    modifier: Modifier = Modifier
) {
    val total = slices.sumOf { it.value.toDouble() }.toFloat().coerceAtLeast(1f)
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Donut Canvas
                Box(
                    modifier = Modifier.size(130.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 26f
                        val radius = (size.minDimension - strokeWidth) / 2
                        var startAngle = -90f

                        slices.forEach { slice ->
                            val sweepAngle = (slice.value / total) * 360f
                            drawArc(
                                color = slice.color,
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                                size = Size(size.width - strokeWidth, size.height - strokeWidth),
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                            )
                            startAngle += sweepAngle
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = total.toInt().toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (centerSubtext.isNotEmpty()) {
                            Text(
                                text = centerSubtext,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                fontSize = 9.sp
                            )
                        }
                    }
                }

                // Legend Column
                Column(
                    modifier = Modifier.padding(start = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    slices.forEach { slice ->
                        val percent = ((slice.value / total) * 100).toInt()
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(slice.color)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "${slice.label} ($percent%)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleBarChart(
    bars: List<BarItem>,
    title: String,
    modifier: Modifier = Modifier
) {
    val maxValue = (bars.maxOfOrNull { it.value } ?: 1f).coerceAtLeast(1f)
    val textMeasurer = rememberTextMeasurer()

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(14.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                val barWidth = 26f
                val spacing = (size.width - (bars.size * barWidth)) / (bars.size + 1)
                val chartHeight = size.height - 35f

                // Draw baseline
                drawLine(
                    color = Color(0xFFCBD5E1),
                    start = Offset(0f, chartHeight),
                    end = Offset(size.width, chartHeight),
                    strokeWidth = 1.5f
                )

                bars.forEachIndexed { index, bar ->
                    val x = spacing + index * (barWidth + spacing)
                    val barHeight = (bar.value / maxValue) * (chartHeight - 20f)
                    val y = chartHeight - barHeight

                    // Bar with rounded top corners
                    drawRoundRect(
                        color = bar.color,
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                    )

                    // Value label on top
                    val valueLayout = textMeasurer.measure(
                        text = bar.value.toInt().toString(),
                        style = TextStyle(color = Color(0xFF475569), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    )
                    drawText(valueLayout, topLeft = Offset(x + (barWidth - valueLayout.size.width) / 2f, y - 16f))

                    // Axis Label
                    val labelLayout = textMeasurer.measure(
                        text = bar.label,
                        style = TextStyle(color = Color(0xFF64748B), fontSize = 9.sp, fontWeight = FontWeight.Medium)
                    )
                    drawText(labelLayout, topLeft = Offset(x + (barWidth - labelLayout.size.width) / 2f, chartHeight + 6f))
                }
            }
        }
    }
}

@Composable
fun TrendLineChart(
    dataPoints: List<Float>,
    title: String,
    xAxisLabels: List<String>,
    modifier: Modifier = Modifier
) {
    val maxValue = (dataPoints.maxOrNull() ?: 1f).coerceAtLeast(1f)
    val textMeasurer = rememberTextMeasurer()

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(14.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                if (dataPoints.size < 2) return@Canvas
                val chartHeight = size.height - 25f
                val stepX = size.width / (dataPoints.size - 1)

                val points = dataPoints.mapIndexed { index, value ->
                    val x = index * stepX
                    val y = chartHeight - ((value / maxValue) * (chartHeight - 15f))
                    Offset(x, y)
                }

                // Gradient Area under curve
                val fillPath = Path().apply {
                    moveTo(points.first().x, chartHeight)
                    points.forEach { lineTo(it.x, it.y) }
                    lineTo(points.last().x, chartHeight)
                    close()
                }
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(CyanHighlight.copy(alpha = 0.35f), Color.Transparent),
                        startY = 0f,
                        endY = chartHeight
                    )
                )

                // Line Path
                val linePath = Path().apply {
                    moveTo(points.first().x, points.first().y)
                    for (i in 1 until points.size) {
                        lineTo(points[i].x, points[i].y)
                    }
                }
                drawPath(
                    path = linePath,
                    color = CyanHighlight,
                    style = Stroke(width = 3.5f, cap = StrokeCap.Round)
                )

                // Dots and X labels
                points.forEachIndexed { i, pt ->
                    drawCircle(color = NavyDark, radius = 5f, center = pt)
                    drawCircle(color = CyanHighlight, radius = 3.5f, center = pt)

                    if (i < xAxisLabels.size) {
                        val lbl = textMeasurer.measure(
                            text = xAxisLabels[i],
                            style = TextStyle(color = Color(0xFF64748B), fontSize = 8.5.sp)
                        )
                        drawText(lbl, topLeft = Offset(pt.x - lbl.size.width / 2f, chartHeight + 6f))
                    }
                }
            }
        }
    }
}

@Composable
fun KpiCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    subtext: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtext,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                fontSize = 11.sp
            )
        }
    }
}
