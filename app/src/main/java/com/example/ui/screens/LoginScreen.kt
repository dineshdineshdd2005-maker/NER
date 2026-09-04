package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MockDataProvider
import com.example.model.User
import com.example.model.UserRole
import com.example.ui.theme.*

@Composable
fun LoginScreen(
    onLoginSuccess: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("admin.sarma@nerlogix.gov.in") }
    var password by remember { mutableStateOf("••••••••••••") }
    var selectedRole by remember { mutableStateOf(UserRole.ADMINISTRATOR) }

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 460.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SlateSurface),
            border = BorderStroke(1.dp, SlateBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // NER-LOGIX Logo Badge
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(NavyDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Terrain,
                        contentDescription = null,
                        tint = CyanHighlight,
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "NER-LOGIX",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = NavyDark,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "AI-Based Smart Logistics & Accessibility Intelligence",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Text(
                    text = "North Eastern Region (NER) Platform",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyanHighlight,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(20.dp))
                HorizontalDivider(color = SlateBorder)
                Spacer(Modifier.height(18.dp))

                // Email Field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email / Username") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = TextMuted) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                // Password Field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = TextMuted) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(14.dp))

                // Role Selector
                Text(
                    text = "SELECT USER ROLE",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(Modifier.height(6.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    UserRole.values().forEach { role ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedRole == role) Color(0xFFEFF6FF) else Color.Transparent,
                            border = BorderStroke(1.dp, if (selectedRole == role) BlueAccent else SlateBorder),
                            onClick = {
                                selectedRole = role
                                MockDataProvider.demoUsers.find { it.role == role }?.let {
                                    email = it.email
                                }
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedRole == role,
                                    onClick = {
                                        selectedRole = role
                                        MockDataProvider.demoUsers.find { it.role == role }?.let {
                                            email = it.email
                                        }
                                    }
                                )
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = role.displayName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (selectedRole == role) NavyDark else TextPrimary
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(18.dp))

                // Primary Login Button
                Button(
                    onClick = {
                        val matchedUser = MockDataProvider.demoUsers.find { it.role == selectedRole }
                            ?: MockDataProvider.demoUsers[0]
                        onLoginSuccess(matchedUser)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyMedium),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "SECURE LOGIN",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Google Sign-In with Firebase Auth Button
                OutlinedButton(
                    onClick = {
                        val fbUser = com.example.service.FirebaseService.getCurrentFirebaseUser()
                            ?: MockDataProvider.demoUsers[0].copy(name = "Google Verified Officer")
                        onLoginSuccess(fbUser)
                    },
                    border = BorderStroke(1.dp, BlueAccent),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyDark),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = BlueAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Sign in with Google (Firebase Auth)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NavyDark
                    )
                }

                Spacer(Modifier.height(18.dp))
                HorizontalDivider(color = SlateBorder)
                Spacer(Modifier.height(14.dp))

                // Quick Demo Login Buttons
                Text(
                    text = "QUICK DEMO ROLE SWITCH (ONE-TAP)",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedButton(
                        onClick = { onLoginSuccess(MockDataProvider.demoUsers[0]) },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                    ) {
                        Text("Admin", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { onLoginSuccess(MockDataProvider.demoUsers[1]) },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                    ) {
                        Text("Logistics", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { onLoginSuccess(MockDataProvider.demoUsers[2]) },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                    ) {
                        Text("Driver", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { onLoginSuccess(MockDataProvider.demoUsers[3]) },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                    ) {
                        Text("Field Off.", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
