package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DeliveryItem
import com.example.model.GpsCoordinate
import com.example.model.RiskLevel

/**
 * Returns the color associated with a route risk score:
 * - Score <= 35: Low Risk -> Green (#10B981)
 * - Score 36..65: Medium Risk -> Yellow / Amber (#F59E0B)
 * - Score > 65: High / Critical Risk -> Red (#EF4444)
 */
fun getRouteRiskScoreColor(riskScore: Int): Color {
    return when {
        riskScore <= 35 -> Color(0xFF10B981) // Safe Green
        riskScore <= 65 -> Color(0xFFF59E0B) // Moderate Caution Yellow
        else -> Color(0xFFEF4444)            // High / Critical Danger Red
    }
}

fun getRouteRiskScoreLabel(riskScore: Int): String {
    return when {
        riskScore <= 35 -> "LOW RISK"
        riskScore <= 65 -> "MEDIUM RISK"
        else -> "HIGH RISK"
    }
}

/**
 * Projects an active delivery's coordinates (start, intermediate waypoints, destination)
 * into screen-space pixel coordinates based on map viewport bounds, zoom, and pan offset.
 */
fun projectDeliveryRoutePoints(
    delivery: DeliveryItem,
    bounds: MapGeoBounds,
    mapWidthPx: Float,
    mapHeightPx: Float,
    zoomLevel: Float = 1.0f,
    panOffset: Offset = Offset.Zero
): List<Offset> {
    val rawCoordinates = mutableListOf<GpsCoordinate>()
    rawCoordinates.add(delivery.startCoordinate)
    rawCoordinates.addAll(delivery.waypoints)
    rawCoordinates.add(delivery.destinationCoordinate)

    return rawCoordinates.map { coord ->
        bounds.project(
            latitude = coord.latitude,
            longitude = coord.longitude,
            mapWidthPx = mapWidthPx,
            mapHeightPx = mapHeightPx,
            zoomLevel = zoomLevel,
            panOffset = panOffset
        )
    }
}

/**
 * Draws the active delivery polyline connecting the start point and destination point.
 * The polyline changes color dynamically based on the route risk score.
 */
fun DrawScope.drawDeliveryPolyline(
    projectedPoints: List<Offset>,
    riskScore: Int,
    zoomLevel: Float = 1.0f,
    dashPhase: Float = 0f
) {
    if (projectedPoints.size < 2) return

    val riskColor = getRouteRiskScoreColor(riskScore)
    val path = Path().apply {
        moveTo(projectedPoints[0].x, projectedPoints[0].y)
        for (i in 1 until projectedPoints.size) {
            lineTo(projectedPoints[i].x, projectedPoints[i].y)
        }
    }

    // 1. Outer ambient glow layer for contrast and visibility on both satellite & terrain
    drawPath(
        path = path,
        color = riskColor.copy(alpha = 0.25f),
        style = Stroke(
            width = 11f * zoomLevel,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )

    // 2. Base solid polyline
    drawPath(
        path = path,
        color = riskColor,
        style = Stroke(
            width = 4.5f * zoomLevel,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )

    // 3. Animated dash overlay showing forward delivery flow
    val dashEffect = PathEffect.dashPathEffect(
        intervals = floatArrayOf(18f * zoomLevel, 14f * zoomLevel),
        phase = dashPhase
    )
    drawPath(
        path = path,
        color = Color.White.copy(alpha = 0.85f),
        style = Stroke(
            width = 2.2f * zoomLevel,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
            pathEffect = dashEffect
        )
    )

    // 4. Subtle waypoint node dots
    for (i in 1 until projectedPoints.size - 1) {
        drawCircle(
            color = Color.White,
            radius = 3.5f * zoomLevel,
            center = projectedPoints[i]
        )
        drawCircle(
            color = riskColor,
            radius = 2.0f * zoomLevel,
            center = projectedPoints[i]
        )
    }
}

/**
 * Start point pin marker for the delivery origin.
 */
@Composable
fun DeliveryStartMarker(
    locationName: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .testTag("delivery_start_marker")
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF064E3B),
        border = BorderStroke(1.dp, Color(0xFF10B981))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF10B981))
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "START: ${locationName.take(14)}",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Destination point pin marker for the delivery destination.
 */
@Composable
fun DeliveryDestinationMarker(
    locationName: String,
    riskScore: Int,
    modifier: Modifier = Modifier
) {
    val riskColor = getRouteRiskScoreColor(riskScore)
    Surface(
        modifier = modifier
            .testTag("delivery_destination_marker")
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF1E293B),
        border = BorderStroke(1.dp, riskColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Flag,
                contentDescription = null,
                tint = riskColor,
                modifier = Modifier.size(11.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "DEST: ${locationName.take(14)}",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * On-map active delivery HUD that displays the route, start/destination names,
 * and current route risk score with color coding, with chips to switch active deliveries.
 */
@Composable
fun ActiveDeliveryRouteHUD(
    deliveries: List<DeliveryItem>,
    activeDelivery: DeliveryItem?,
    onSelectDelivery: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (activeDelivery == null) return

    var isExpanded by remember { mutableStateOf(false) }
    val riskColor = getRouteRiskScoreColor(activeDelivery.riskScore)
    val riskLabel = getRouteRiskScoreLabel(activeDelivery.riskScore)

    Card(
        modifier = modifier
            .testTag("active_delivery_route_hud")
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(10.dp))
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xF00F172A)),
        border = BorderStroke(1.dp, riskColor.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(riskColor)
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = "ROUTE: ${activeDelivery.origin.split(" ").first()} → ${activeDelivery.destination.split(" ").first()}",
                        color = Color.White,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Risk Score Badge with Dynamic Color
                    Surface(
                        shape = RoundedCornerShape(5.dp),
                        color = riskColor.copy(alpha = 0.22f),
                        border = BorderStroke(0.8.dp, riskColor)
                    ) {
                        Text(
                            text = "RISK: ${activeDelivery.riskScore}/100 ($riskLabel)",
                            color = riskColor,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            if (isExpanded) {
                Spacer(Modifier.height(6.dp))

                // Start -> Destination visual connection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Origin
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "START POINT",
                            color = Color(0xFF10B981),
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = activeDelivery.origin,
                            color = Color.White,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = riskColor,
                        modifier = Modifier
                            .size(14.dp)
                            .padding(horizontal = 2.dp)
                    )

                    // Destination
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "DESTINATION POINT",
                            color = riskColor,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = activeDelivery.destination,
                            color = Color.White,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }
                }

                // Quick delivery switch chips to test different polylines and risk scores
                if (deliveries.size > 1) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "SELECT ACTIVE DELIVERY ROUTE:",
                        color = Color(0xFF94A3B8),
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(3.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        deliveries.take(4).forEach { delivery ->
                            val isSelected = delivery.id == activeDelivery.id
                            val dColor = getRouteRiskScoreColor(delivery.riskScore)

                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (isSelected) dColor else Color(0xFF1E293B),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) Color.White else dColor.copy(alpha = 0.4f)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onSelectDelivery(delivery.id) }
                            ) {
                                Text(
                                    text = "${delivery.id.takeLast(4)} (${delivery.riskScore})",
                                    color = if (isSelected) Color.Black else Color(0xFFCBD5E1),
                                    fontSize = 9.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                    modifier = Modifier
                                        .padding(vertical = 3.dp)
                                        .wrapContentWidth(Alignment.CenterHorizontally)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
