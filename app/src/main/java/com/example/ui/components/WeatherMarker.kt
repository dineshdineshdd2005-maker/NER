package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RiskLevel
import com.example.model.WeatherZone

/**
 * Geographic bounding box for North East India regional corridor.
 */
data class MapGeoBounds(
    val minLat: Double = 24.3,
    val maxLat: Double = 28.2,
    val minLng: Double = 91.2,
    val maxLng: Double = 94.8
) {
    companion object {
        val NER_DEFAULT = MapGeoBounds(
            minLat = 24.3,
            maxLat = 28.2,
            minLng = 91.2,
            maxLng = 94.8
        )
    }

    /**
     * Projects GPS coordinates (latitude, longitude) into map viewport pixel space.
     * Takes into account map dimensions, zoom level, and pan offset.
     */
    fun project(
        latitude: Double,
        longitude: Double,
        mapWidthPx: Float,
        mapHeightPx: Float,
        zoomLevel: Float = 1.0f,
        panOffset: Offset = Offset.Zero
    ): Offset {
        val normX = ((longitude - minLng) / (maxLng - minLng)).coerceIn(0.02, 0.98).toFloat()
        // In screen coordinates: higher latitude (North) corresponds to lower Y (top of screen)
        val normY = (1.0 - (latitude - minLat) / (maxLat - minLat)).coerceIn(0.04, 0.96).toFloat()

        val cx = mapWidthPx / 2f + panOffset.x
        val cy = mapHeightPx / 2f + panOffset.y

        val relX = (normX - 0.5f) * mapWidthPx * zoomLevel
        val relY = (normY - 0.5f) * mapHeightPx * zoomLevel

        return Offset(cx + relX, cy + relY)
    }
}

/**
 * Maps severity level to standard map indicator colors:
 * - Low Severity -> Green
 * - Medium Severity -> Yellow
 * - High & Critical Severity -> Red
 */
fun getSeverityColor(severity: RiskLevel): Color {
    return when (severity) {
        RiskLevel.LOW -> Color(0xFF10B981) // Green
        RiskLevel.MEDIUM -> Color(0xFFF59E0B) // Yellow
        RiskLevel.HIGH -> Color(0xFFEF4444) // Red
        RiskLevel.CRITICAL -> Color(0xFFDC2626) // Red (Critical Alert)
    }
}

/**
 * WeatherMarker Component
 * Dynamically positions itself on the simulated Google Maps area based on coordinate data.
 * Changes color (Green, Yellow, Red) based on the severity level property in the mock data.
 */
@Composable
fun WeatherMarker(
    weatherZone: WeatherZone,
    mapWidthPx: Float,
    mapHeightPx: Float,
    zoomLevel: Float = 1.0f,
    panOffset: Offset = Offset.Zero,
    mapBounds: MapGeoBounds = MapGeoBounds.NER_DEFAULT,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    WeatherMarker(
        latitude = weatherZone.coordinates.latitude,
        longitude = weatherZone.coordinates.longitude,
        severity = weatherZone.severity,
        title = weatherZone.regionName,
        rainfallMmPerHour = weatherZone.rainfallMmPerHour,
        mapWidthPx = mapWidthPx,
        mapHeightPx = mapHeightPx,
        zoomLevel = zoomLevel,
        panOffset = panOffset,
        mapBounds = mapBounds,
        modifier = modifier,
        onClick = onClick
    )
}

/**
 * Overloaded WeatherMarker Composable accepting explicit coordinate data and severity.
 */
@Composable
fun WeatherMarker(
    latitude: Double,
    longitude: Double,
    severity: RiskLevel,
    title: String = "",
    rainfallMmPerHour: Double? = null,
    mapWidthPx: Float,
    mapHeightPx: Float,
    zoomLevel: Float = 1.0f,
    panOffset: Offset = Offset.Zero,
    mapBounds: MapGeoBounds = MapGeoBounds.NER_DEFAULT,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    if (mapWidthPx <= 0f || mapHeightPx <= 0f) return

    // Calculate dynamic map position from coordinates
    val projectedOffset = mapBounds.project(
        latitude = latitude,
        longitude = longitude,
        mapWidthPx = mapWidthPx,
        mapHeightPx = mapHeightPx,
        zoomLevel = zoomLevel,
        panOffset = panOffset
    )

    val density = LocalDensity.current
    val offsetX = with(density) { (projectedOffset.x).toDp() }
    val offsetY = with(density) { (projectedOffset.y).toDp() }

    // Severity color: Green (Low), Yellow (Medium), Red (High/Critical)
    val markerColor = getSeverityColor(severity)

    // Pulsing radar animation for high/critical severity alerts
    val isSevere = severity == RiskLevel.HIGH || severity == RiskLevel.CRITICAL
    val infiniteTransition = rememberInfiniteTransition(label = "weatherPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = if (isSevere) 1.0f else 1.0f,
        targetValue = if (isSevere) 1.8f else 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isSevere) 1200 else 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = if (isSevere) 0.6f else 0.2f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isSevere) 1200 else 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = modifier
            .offset(x = offsetX - 28.dp, y = offsetY - 14.dp)
            .wrapContentSize(Alignment.Center),
        contentAlignment = Alignment.Center
    ) {
        // Pulse ring around severe weather pins
        if (isSevere) {
            Box(
                modifier = Modifier
                    .size((28 * pulseScale).dp)
                    .clip(CircleShape)
                    .background(markerColor.copy(alpha = pulseAlpha))
            )
        }

        // Main Google Maps style Weather Pin
        Surface(
            onClick = onClick,
            shape = RoundedCornerShape(12.dp),
            color = markerColor,
            border = BorderStroke(1.2.dp, Color.White),
            shadowElevation = 5.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = when (severity) {
                        RiskLevel.CRITICAL, RiskLevel.HIGH -> Icons.Default.WaterDrop
                        RiskLevel.MEDIUM -> Icons.Default.Cloud
                        RiskLevel.LOW -> Icons.Default.CheckCircle
                    },
                    contentDescription = "Weather severity: ${severity.name}",
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )

                Spacer(Modifier.width(3.dp))

                Text(
                    text = if (rainfallMmPerHour != null) {
                        "${rainfallMmPerHour.toInt()} mm/h"
                    } else {
                        severity.label
                    },
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }
    }
}
