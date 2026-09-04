package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen

data class NavMenuItem(
    val screen: AppScreen,
    val label: String,
    val icon: ImageVector,
    val badgeCount: Int = 0
)

val primaryNavItems = listOf(
    NavMenuItem(AppScreen.DASHBOARD, "Dashboard", Icons.Default.Dashboard),
    NavMenuItem(AppScreen.AI_COPILOT, "AI Dispatch Copilot", Icons.Default.SmartToy),
    NavMenuItem(AppScreen.LIVE_TRACKING, "Live Tracking", Icons.Default.GpsFixed),
    NavMenuItem(AppScreen.ROUTE_OPTIMIZER, "Route Optimizer", Icons.Default.AltRoute),
    NavMenuItem(AppScreen.AI_RISK_PREDICTION, "AI Risk Prediction", Icons.Default.AutoAwesome),
    NavMenuItem(AppScreen.WEATHER_DISASTER, "Weather & Disaster", Icons.Default.Thunderstorm),
    NavMenuItem(AppScreen.FIELD_REPORTS, "Field Reports", Icons.Default.Assignment),
    NavMenuItem(AppScreen.DELIVERIES, "Deliveries", Icons.Default.LocalShipping),
    NavMenuItem(AppScreen.GIS_MAP, "GIS Map", Icons.Default.Map),
    NavMenuItem(AppScreen.ANALYTICS, "Analytics", Icons.Default.Analytics),
    NavMenuItem(AppScreen.ALERTS, "Alerts", Icons.Default.NotificationsActive, 4),
    NavMenuItem(AppScreen.ADMIN_PANEL, "Admin", Icons.Default.AdminPanelSettings),
    NavMenuItem(AppScreen.LIVE_DEMO, "Live Demo Scenario", Icons.Default.PlayCircleFilled)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavBar(
    currentUser: User,
    currentScreen: AppScreen,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onStartDemoClick: () -> Unit,
    onAiCopilotClick: () -> Unit = {},
    unreadAlertCount: Int,
    isOffline: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = NavyDark,
        tonalElevation = 4.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Menu Icon + App Brand Title
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                    }

                    Spacer(Modifier.width(4.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "NER-LOGIX",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                            Spacer(Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = CyanHighlight
                            ) {
                                Text(
                                    text = "AI • GIS",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "North Eastern Logistics Platform",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8),
                            fontSize = 9.5.sp
                        )
                    }
                }

                // Center/Right Info Widgets: Weather, System Status, Demo button
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Start Live Demo CTA Button
                    FilledTonalButton(
                        onClick = onStartDemoClick,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (currentScreen == AppScreen.LIVE_DEMO) PurpleAi else NavyMedium,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("LIVE DEMO", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // System Status Indicator
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isOffline) Color(0xFF7F1D1D) else Color(0xFF064E3B),
                        border = BorderStroke(1.dp, if (isOffline) Color(0xFFDC2626) else Color(0xFF10B981))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (isOffline) Color(0xFFEF4444) else Color(0xFF34D399))
                            )
                            Spacer(Modifier.width(5.dp))
                            Text(
                                text = if (isOffline) "OFFLINE" else "ONLINE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // AI Copilot Quick Access
                    IconButton(
                        onClick = onAiCopilotClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = "AI Dispatch Copilot",
                            tint = CyanHighlight
                        )
                    }

                    // Notification Bell
                    IconButton(
                        onClick = onNotificationsClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        BadgedBox(badge = {
                            if (unreadAlertCount > 0) {
                                Badge(containerColor = RiskCriticalRed) {
                                    Text("$unreadAlertCount", color = Color.White, fontSize = 9.sp)
                                }
                            }
                        }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Alerts", tint = Color.White)
                        }
                    }

                    // Profile Role Chip
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF1E293B),
                        modifier = Modifier.clickable { onProfileClick() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(CyanHighlight),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentUser.name.take(1),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = currentUser.role.badge,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppSidebarDrawerContent(
    currentScreen: AppScreen,
    currentUser: User,
    onSelectScreen: (AppScreen) -> Unit,
    onLogout: () -> Unit,
    onSwitchRole: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier.width(290.dp),
        drawerContainerColor = NavyDark,
        drawerContentColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyanHighlight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Terrain,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "NER-LOGIX",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Accessibility & Logistics Intelligence",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFF1E293B))
            Spacer(Modifier.height(8.dp))

            // Navigation Items List
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                primaryNavItems.forEach { item ->
                    val isSelected = currentScreen == item.screen
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = if (isSelected) Color.White else Color(0xFF94A3B8),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFFE2E8F0)
                            )
                        },
                        badge = {
                            if (item.badgeCount > 0) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = RiskCriticalRed
                                ) {
                                    Text(
                                        text = "${item.badgeCount}",
                                        fontSize = 10.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        },
                        selected = isSelected,
                        onClick = { onSelectScreen(item.screen) },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = NavyAccent,
                            unselectedContainerColor = Color.Transparent
                        ),
                        modifier = Modifier.height(44.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFF1E293B))
            Spacer(Modifier.height(10.dp))

            // Active User Profile Card with Quick Switch
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF1E293B),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = currentUser.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = currentUser.role.displayName,
                                style = MaterialTheme.typography.bodySmall,
                                color = CyanHighlight,
                                fontSize = 11.sp
                            )
                        }

                        IconButton(
                            onClick = onLogout,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color.LightGray)
                        }
                    }
                }
            }
        }
    }
}
