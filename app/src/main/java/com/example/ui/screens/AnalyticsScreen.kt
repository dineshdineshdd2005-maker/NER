package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun AnalyticsScreen(
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
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Analytics,
                        contentDescription = null,
                        tint = CyanHighlight,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "ANALYTICS & OPERATIONAL REPORTS",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = NavyDark
                    )
                }
                Text(
                    text = "Historical corridor reliability metrics, incident heatmaps and supply chain KPIs.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // The 5 Mandatory KPI Cards (Total Deliveries, Successful, Delayed, High-Risk Routes, Resolved Alerts)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KpiCard(
                        title = "Total Deliveries",
                        value = "1,482",
                        icon = Icons.Default.Inventory,
                        iconColor = BlueAccent,
                        subtext = "+14% vs last month",
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "Successful Deliveries",
                        value = "1,396",
                        icon = Icons.Default.CheckCircle,
                        iconColor = RiskLowGreen,
                        subtext = "94.2% completion rate",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KpiCard(
                        title = "Delayed Deliveries",
                        value = "86",
                        icon = Icons.Default.HourglassTop,
                        iconColor = RiskHighOrange,
                        subtext = "Mainly due to NH-13 rain",
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "High-Risk Routes",
                        value = "6",
                        icon = Icons.Default.Warning,
                        iconColor = RiskCriticalRed,
                        subtext = "Active rerouting applied",
                        modifier = Modifier.weight(1f)
                    )
                }

                KpiCard(
                    title = "Resolved Alerts",
                    value = "342",
                    icon = Icons.Default.TaskAlt,
                    iconColor = PurpleAi,
                    subtext = "Average response time: 24 mins",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Chart 1: Route Risk Distribution (Donut Chart)
        item {
            val riskSlices = listOf(
                ChartSlice("Low Risk (Safe)", 18f, RiskLowGreen),
                ChartSlice("Medium Risk", 7f, RiskMediumYellow),
                ChartSlice("High Risk", 4f, RiskHighOrange),
                ChartSlice("Critical Hazard", 2f, RiskCriticalRed)
            )
            DonutChart(
                slices = riskSlices,
                title = "Route Risk Distribution (31 Monitored Sectors)",
                centerSubtext = "Routes",
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Chart 2: Vehicle Status Breakdown
        item {
            val vehicleSlices = listOf(
                ChartSlice("In Transit", 14f, CyanHighlight),
                ChartSlice("Rerouted", 3f, RiskLowGreen),
                ChartSlice("Stopped / Weather", 2f, RiskHighOrange),
                ChartSlice("Loading Hub", 5f, Color(0xFF94A3B8))
            )
            DonutChart(
                slices = vehicleSlices,
                title = "Fleet Vehicle Operational Status",
                centerSubtext = "Vehicles",
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Chart 3: Road Disruptions by Type (Bar Chart)
        item {
            val disruptionBars = listOf(
                BarItem("Landslide", 48f, RiskCriticalRed),
                BarItem("Flood", 32f, BlueAccent),
                BarItem("Potholes", 24f, RiskMediumYellow),
                BarItem("Bridge Dam.", 12f, RiskHighOrange),
                BarItem("Tree Fall", 19f, RiskLowGreen)
            )
            SimpleBarChart(
                bars = disruptionBars,
                title = "Road Disruptions by Hazard Type (Quarterly)",
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Chart 4: Average Delivery Time & Delay Trends (Trend Line Chart)
        item {
            val deliveryTimes = listOf(9.2f, 10.4f, 14.8f, 13.2f, 11.0f, 10.5f)
            val months = listOf("Apr", "May", "Jun", "Jul", "Aug", "Sep")

            TrendLineChart(
                dataPoints = deliveryTimes,
                title = "Average Corridor Delivery Time (Hours) vs Monsoon Peaks",
                xAxisLabels = months,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
