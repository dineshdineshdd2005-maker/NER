package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DeliveryItem
import com.example.model.DeliveryPriority
import com.example.model.RiskLevel
import com.example.ui.components.AlertBadge
import com.example.ui.theme.*

@Composable
fun DeliveryTrackingScreen(
    deliveries: List<DeliveryItem>,
    onEmergencyPriorityRouting: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = CyanHighlight,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "LOGISTICS DELIVERY TRACKING",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = NavyDark
                        )
                    }
                    Text(
                        text = "Essential supplies, cold chain pharmaceuticals, rations & military freight manifest.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        // Summary Badges
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DeliverySummaryPill("Total Consignments", "${deliveries.size}", Icons.Default.Inventory2, BlueAccent, Modifier.weight(1f))
                DeliverySummaryPill("Emergency Supplies", "${deliveries.count { it.priority == DeliveryPriority.EMERGENCY }}", Icons.Default.MedicalServices, RiskCriticalRed, Modifier.weight(1f))
                DeliverySummaryPill("In Transit", "${deliveries.count { it.status.contains("Transit") }}", Icons.Default.LocalShipping, RiskLowGreen, Modifier.weight(1f))
            }
        }

        // Consignment Cards
        items(deliveries.size) { index ->
            val d = deliveries[index]
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(
                    1.dp,
                    if (d.priority == DeliveryPriority.EMERGENCY) RiskCriticalRed else SlateBorder
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = d.id,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.width(8.dp))
                            PriorityChip(priority = d.priority)
                        }

                        AlertBadge(level = d.riskLevel)
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "${d.origin} → ${d.destination}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Goods: ${d.goodsType}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 11.5.sp
                        )
                        Text(
                            text = "Vehicle: ${d.vehicleId}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = CyanHighlight,
                            fontSize = 11.5.sp
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Status: ${d.status}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "ETA: ${d.eta}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = RiskLowGreen,
                            fontSize = 11.5.sp
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    // Emergency Delivery Priority Routing Button (Mandatory as per prompt)
                    Button(
                        onClick = { onEmergencyPriorityRouting(d.id) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (d.priority == DeliveryPriority.EMERGENCY) RiskCriticalRed else NavyMedium
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.PriorityHigh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Emergency Delivery Priority Routing",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PriorityChip(priority: DeliveryPriority) {
    val (bg, fg) = when (priority) {
        DeliveryPriority.EMERGENCY -> RiskCriticalRedBg to RiskCriticalRed
        DeliveryPriority.HIGH -> RiskHighOrangeBg to RiskHighOrange
        DeliveryPriority.NORMAL -> Color(0xFFEFF6FF) to BlueAccent
    }
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = bg,
        border = BorderStroke(0.8.dp, fg)
    ) {
        Text(
            text = priority.label,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold,
            color = fg,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun DeliverySummaryPill(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, SlateBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text(text = label, fontSize = 9.sp, color = TextMuted)
            }
            Spacer(Modifier.height(4.dp))
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextPrimary)
        }
    }
}
