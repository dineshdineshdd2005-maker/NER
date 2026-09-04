package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.ChatMessage
import com.example.service.GeminiApiService
import com.example.service.GeminiModel
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GeminiChatScreen(
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var selectedModel by remember { mutableStateOf(GeminiModel.FLASH_GENERAL) }
    var useMapsGrounding by remember { mutableStateOf(true) }
    var isLiveVoiceMode by remember { mutableStateOf(false) }
    var isRecordingAudio by remember { mutableStateOf(false) }
    var isTranscribing by remember { mutableStateOf(false) }
    var isLoadingResponse by remember { mutableStateOf(false) }

    var inputText by remember { mutableStateOf("") }

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                id = "welcome-1",
                text = "Welcome to NER-LOGIX AI Dispatch Copilot. I am specialized in mountain logistics, road accessibility, monsoonal weather hazards, and emergency rerouting across the 8 North Eastern states.\n\nSelect a model above (or enable Google Maps Grounding / Live Voice Mode) to begin.",
                isUser = false,
                timestamp = "Online",
                modelUsed = GeminiModel.FLASH_GENERAL.modelId,
                mapsGroundingUsed = true,
                mapsSources = listOf("IMD Tezpur Radar", "NHAI Road Sentinel", "Google Maps: North East India")
            )
        )
    }

    // Scroll to bottom when messages change
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Live Voice Animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
    ) {
        // Top Header with Model Selector & Live Voice Switcher
        Card(
            shape = RoundedCornerShape(0.dp),
            colors = CardDefaults.cardColors(containerColor = NavyDark),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(CyanHighlight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = null,
                                tint = NavyDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "AI DISPATCH COPILOT",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "Multi-Turn Gemini Intelligence & Maps Grounding",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Live Voice Toggle (gemini-3.1-flash-live-preview)
                    OutlinedButton(
                        onClick = { isLiveVoiceMode = !isLiveVoiceMode },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (isLiveVoiceMode) RiskLowGreen else Color.White,
                            containerColor = if (isLiveVoiceMode) Color(0xFF064E3B) else Color.Transparent
                        ),
                        border = BorderStroke(1.dp, if (isLiveVoiceMode) RiskLowGreen else SlateBorder),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (isLiveVoiceMode) Icons.Default.GraphicEq else Icons.Default.Mic,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = if (isLiveVoiceMode) "Live API Active" else "Live Voice",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Model Selection Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedModel == GeminiModel.FLASH_GENERAL,
                        onClick = { selectedModel = GeminiModel.FLASH_GENERAL },
                        label = { Text("3.5 Flash (General)", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyanHighlight,
                            selectedLabelColor = NavyDark,
                            labelColor = Color(0xFFCBD5E1)
                        ),
                        border = FilterChipDefaults.filterChipBorder(enabled = true, selected = selectedModel == GeminiModel.FLASH_GENERAL, borderColor = SlateBorder)
                    )

                    FilterChip(
                        selected = selectedModel == GeminiModel.PRO_COMPLEX,
                        onClick = { selectedModel = GeminiModel.PRO_COMPLEX },
                        label = { Text("3.1 Pro (Complex)", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PurpleAi,
                            selectedLabelColor = Color.White,
                            labelColor = Color(0xFFCBD5E1)
                        ),
                        border = FilterChipDefaults.filterChipBorder(enabled = true, selected = selectedModel == GeminiModel.PRO_COMPLEX, borderColor = SlateBorder)
                    )

                    FilterChip(
                        selected = selectedModel == GeminiModel.FLASH_LITE,
                        onClick = { selectedModel = GeminiModel.FLASH_LITE },
                        label = { Text("3.1 Lite (Fast)", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BlueAccent,
                            selectedLabelColor = Color.White,
                            labelColor = Color(0xFFCBD5E1)
                        ),
                        border = FilterChipDefaults.filterChipBorder(enabled = true, selected = selectedModel == GeminiModel.FLASH_LITE, borderColor = SlateBorder)
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Maps Grounding toggle row (googleMaps tool)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = if (useMapsGrounding) CyanHighlight else Color(0xFF64748B),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Google Maps Grounding (googleMaps tool)",
                            fontSize = 11.sp,
                            color = if (useMapsGrounding) Color.White else Color(0xFF94A3B8),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Switch(
                        checked = useMapsGrounding,
                        onCheckedChange = { useMapsGrounding = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CyanHighlight,
                            checkedTrackColor = Color(0xFF0369A1)
                        )
                    )
                }
            }
        }

        // Live Voice Mode Banner (gemini-3.1-flash-live-preview)
        AnimatedVisibility(visible = isLiveVoiceMode) {
            Card(
                shape = RoundedCornerShape(0.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size((12 * pulseScale).dp)
                                .clip(CircleShape)
                                .background(RiskLowGreen)
                        )
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "gemini-3.1-flash-live-preview Active",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            )
                            Text(
                                text = "Bidirectional audio channel open for driver hands-free mountain transit",
                                color = Color(0xFFA7F3D0),
                                fontSize = 10.sp
                            )
                        }
                    }

                    TextButton(onClick = { isLiveVoiceMode = false }) {
                        Text("Exit", color = Color.White, fontSize = 11.sp)
                    }
                }
            }
        }

        // Messages Thread
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                ChatBubble(msg)
            }

            if (isLoadingResponse) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = CyanHighlight,
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "${selectedModel.displayName} is synthesizing logistics intelligence...",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Prompt Suggestions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SuggestionChip(
                onClick = {
                    inputText = "Assess road blockage and weather between Guwahati and Tawang on NH-13."
                },
                label = { Text("NH-13 to Tawang Status", fontSize = 10.sp) }
            )
            SuggestionChip(
                onClick = {
                    inputText = "Recommend alternate bypass avoiding high landslide slope in West Kameng."
                },
                label = { Text("Route B Bypass Rationale", fontSize = 10.sp) }
            )
        }

        // Bottom Input Bar with Transcription button (gemini-3.5-transcribe)
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            border = BorderStroke(1.dp, SlateBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Audio Transcription Button (gemini-3.5-transcribe)
                IconButton(
                    onClick = {
                        if (!isRecordingAudio && !isTranscribing) {
                            isRecordingAudio = true
                            coroutineScope.launch {
                                delay(1200) // Simulate listening
                                isRecordingAudio = false
                                isTranscribing = true
                                val transcribed = GeminiApiService.transcribeAudio(byteArrayOf())
                                inputText = transcribed
                                isTranscribing = false
                            }
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isRecordingAudio) RiskCriticalRed else if (isTranscribing) BlueAccent else Color(0xFFF1F5F9))
                ) {
                    if (isTranscribing) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Icon(
                            imageVector = if (isRecordingAudio) Icons.Default.GraphicEq else Icons.Default.Mic,
                            contentDescription = "Transcribe Audio (gemini-3.5-transcribe)",
                            tint = if (isRecordingAudio || isTranscribing) Color.White else NavyDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(Modifier.width(8.dp))

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = if (isRecordingAudio) "Listening to driver voice..." else if (isTranscribing) "Transcribing with gemini-3.5-transcribe..." else "Ask AI Dispatch Copilot...",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = false,
                    maxLines = 3,
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanHighlight,
                        unfocusedBorderColor = SlateBorder
                    )
                )

                Spacer(Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank() && !isLoadingResponse) {
                            val userText = inputText.trim()
                            inputText = ""
                            messages.add(
                                ChatMessage(
                                    id = "msg-${System.currentTimeMillis()}",
                                    text = userText,
                                    isUser = true,
                                    timestamp = "Just now"
                                )
                            )
                            isLoadingResponse = true

                            coroutineScope.launch {
                                val reply = GeminiApiService.sendMessage(
                                    history = messages.toList(),
                                    userPrompt = userText,
                                    selectedModel = selectedModel,
                                    useMapsGrounding = useMapsGrounding
                                )
                                messages.add(reply)
                                isLoadingResponse = false
                            }
                        }
                    },
                    enabled = inputText.isNotBlank() && !isLoadingResponse,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (inputText.isNotBlank()) CyanHighlight else Color(0xFFE2E8F0))
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = if (inputText.isNotBlank()) NavyDark else Color(0xFF94A3B8),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.isUser

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        // Tag Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (!isUser) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = null,
                    tint = CyanHighlight,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "AI Copilot (${message.modelUsed ?: "gemini-3.5-flash"})",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = BlueAccent
                )
            } else {
                Text(
                    text = "Logistics Officer",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )
            }
            Spacer(Modifier.width(6.dp))
            Text(text = message.timestamp, fontSize = 9.sp, color = TextMuted)
        }

        Spacer(Modifier.height(3.dp))

        Surface(
            shape = RoundedCornerShape(
                topStart = 14.dp,
                topEnd = 14.dp,
                bottomStart = if (isUser) 14.dp else 2.dp,
                bottomEnd = if (isUser) 2.dp else 14.dp
            ),
            color = if (isUser) NavyMedium else MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, if (isUser) BlueAccent.copy(alpha = 0.5f) else SlateBorder),
            tonalElevation = 2.dp,
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isUser) Color.White else TextPrimary,
                    fontSize = 12.5.sp,
                    lineHeight = 18.sp
                )

                // Maps Grounding Sources Badge
                if (!isUser && message.mapsGroundingUsed && message.mapsSources.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider(color = SlateBorder)
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = CyanHighlight,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Maps Grounded: ${message.mapsSources.joinToString(", ")}",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BlueAccent
                        )
                    }
                }
            }
        }
    }
}
