package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sparkles
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.AIChatMessage
import com.example.data.ai.GeneratedVideoResult
import com.example.data.ai.MapGroundingItem
import com.example.data.ai.MessageSender
import com.example.data.firebase.AppUserProfile
import com.example.ui.theme.GymCharcoal
import com.example.ui.theme.GymElectricAmber
import com.example.ui.theme.GymElectricLime
import com.example.ui.theme.GymNeonCyan

enum class AISubTab(val title: String) {
    CHAT("AI Coach Chat"),
    LIVE_VOICE("Live Voice"),
    VEO_VIDEO("Veo 3 Video"),
    MAPS("Maps Grounding"),
    IMAGE_STUDIO("Image Studio"),
    FIREBASE_CLOUD("Cloud & Auth")
}

@Composable
fun AIStudioScreen(
    chatMessages: List<AIChatMessage>,
    isAiGenerating: Boolean,
    onSendMessage: (String, String) -> Unit,
    onLiveVoiceTalk: (String) -> Unit,
    liveVoiceResponse: String?,
    isLiveListening: Boolean,
    onToggleLiveVoice: () -> Unit,
    onTranscribeAudio: (String) -> Unit,
    transcribedResult: String?,
    mapsResults: List<MapGroundingItem>,
    onSearchMaps: (String) -> Unit,
    veoVideos: List<GeneratedVideoResult>,
    onGenerateVeoVideo: (String, String) -> Unit,
    onAnimatePhotoToVideo: (String, String) -> Unit,
    onGenerateImage: (String, String) -> Unit,
    generatedImageStatus: String?,
    firebaseUser: AppUserProfile?,
    onFirebaseSignIn: (String, String) -> Unit,
    onFirebaseSignOut: () -> Unit,
    onSyncFirestore: () -> Unit,
    firestoreSyncStatus: String?,
    modifier: Modifier = Modifier
) {
    var currentSubTab by remember { mutableIntStateOf(0) }
    val tabs = AISubTab.values()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Sub-Tab Row
        ScrollableTabRow(
            selectedTabIndex = currentSubTab,
            edgePadding = 12.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = GymNeonCyan
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = currentSubTab == index,
                    onClick = { currentSubTab = index },
                    text = {
                        Text(
                            text = tab.title,
                            fontWeight = if (currentSubTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    }
                )
            }
        }

        when (tabs[currentSubTab]) {
            AISubTab.CHAT -> AIChatSection(
                messages = chatMessages,
                isGenerating = isAiGenerating,
                onSendMessage = onSendMessage,
                onTranscribeAudio = onTranscribeAudio,
                transcribedResult = transcribedResult
            )
            AISubTab.LIVE_VOICE -> LiveVoiceSection(
                isListening = isLiveListening,
                liveVoiceResponse = liveVoiceResponse,
                onToggleListening = onToggleLiveVoice,
                onSpokenQuery = onLiveVoiceTalk
            )
            AISubTab.VEO_VIDEO -> VeoVideoSection(
                videos = veoVideos,
                isGenerating = isAiGenerating,
                onGenerateVideo = onGenerateVeoVideo,
                onAnimatePhoto = onAnimatePhotoToVideo
            )
            AISubTab.MAPS -> MapsGroundingSection(
                locations = mapsResults,
                isSearching = isAiGenerating,
                onSearch = onSearchMaps
            )
            AISubTab.IMAGE_STUDIO -> ImageStudioSection(
                isGenerating = isAiGenerating,
                onGenerateImage = onGenerateImage,
                status = generatedImageStatus
            )
            AISubTab.FIREBASE_CLOUD -> FirebaseCloudSection(
                user = firebaseUser,
                syncStatus = firestoreSyncStatus,
                isSyncing = isAiGenerating,
                onSignIn = onFirebaseSignIn,
                onSignOut = onFirebaseSignOut,
                onSyncFirestore = onSyncFirestore
            )
        }
    }
}

// ----------------------------------------------------
// 1. GEMINI CHATBOT SECTION
// ----------------------------------------------------
@Composable
fun AIChatSection(
    messages: List<AIChatMessage>,
    isGenerating: Boolean,
    onSendMessage: (String, String) -> Unit,
    onTranscribeAudio: (String) -> Unit,
    transcribedResult: String?
) {
    var inputPrompt by remember { mutableStateOf("") }
    var selectedModel by remember { mutableStateOf("gemini-3.5-flash") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    LaunchedEffect(transcribedResult) {
        if (!transcribedResult.isNullOrBlank()) {
            inputPrompt = transcribedResult
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        // Model Selection Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Model:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            FilterChip(
                selected = selectedModel == "gemini-3.1-pro-preview",
                onClick = { selectedModel = "gemini-3.1-pro-preview" },
                label = { Text("Pro (Complex)", fontSize = 10.sp) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GymNeonCyan.copy(alpha = 0.25f))
            )
            FilterChip(
                selected = selectedModel == "gemini-3.5-flash",
                onClick = { selectedModel = "gemini-3.5-flash" },
                label = { Text("Flash (General)", fontSize = 10.sp) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GymElectricLime.copy(alpha = 0.25f))
            )
            FilterChip(
                selected = selectedModel == "gemini-3.1-flash-lite-preview",
                onClick = { selectedModel = "gemini-3.1-flash-lite-preview" },
                label = { Text("Lite (Fast)", fontSize = 10.sp) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GymElectricAmber.copy(alpha = 0.25f))
            )
        }

        // Quick Prompt Suggestions
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val suggestions = listOf(
                "Optimize my Push-Pull-Legs split",
                "Calculate 1RM for 100kg x 8 reps",
                "How to hit 180g protein on veg diet?",
                "Deadlift form & bar-path mechanics"
            )
            items(suggestions) { s ->
                Surface(
                    onClick = { inputPrompt = s },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Text(
                        text = s,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Message Thread
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (messages.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Sparkles,
                                contentDescription = null,
                                tint = GymElectricLime,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "IronPulse Elite AI Coach",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Powered by Gemini 3.1 Pro, 3.5 Flash & Flash Lite.\nAsk any workout programming, biomechanics, or macro question.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(messages) { msg ->
                    ChatBubble(message = msg)
                }
            }

            if (isGenerating) {
                item {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = GymNeonCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Coach is thinking ($selectedModel)...", fontSize = 11.sp, color = GymNeonCyan)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Input Box with Send and Transcribe Mic
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onTranscribeAudio("dummy_audio_sample") },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(GymNeonCyan.copy(alpha = 0.2f))
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Transcribe Voice (gemini-3.5-transcribe)",
                    tint = GymNeonCyan,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = inputPrompt,
                onValueChange = { inputPrompt = it },
                placeholder = { Text("Ask coach anything...", fontSize = 13.sp) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("gemini_chat_input"),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GymNeonCyan,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                ),
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (inputPrompt.isNotBlank() && !isGenerating) {
                        onSendMessage(inputPrompt.trim(), selectedModel)
                        inputPrompt = ""
                    }
                },
                enabled = inputPrompt.isNotBlank() && !isGenerating,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (inputPrompt.isNotBlank()) GymElectricLime else MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = if (inputPrompt.isNotBlank()) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun ChatBubble(message: AIChatMessage) {
    val isUser = message.sender == MessageSender.USER
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 2.dp,
                bottomEnd = if (isUser) 2.dp else 16.dp
            ),
            color = if (isUser) GymNeonCyan.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(
                1.dp,
                if (isUser) GymNeonCyan.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            ),
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (!isUser) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GymElectricLime, modifier = Modifier.size(12.dp))
                        Text("IronPulse Coach", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GymElectricLime)
                        if (message.modelUsed != null) {
                            Text("· ${message.modelUsed}", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Text(
                    text = message.text,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

// ----------------------------------------------------
// 2. LIVE VOICE CONVERSATIONS (gemini-3.8-live)
// ----------------------------------------------------
@Composable
fun LiveVoiceSection(
    isListening: Boolean,
    liveVoiceResponse: String?,
    onToggleListening: () -> Unit,
    onSpokenQuery: (String) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 1.25f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "LIVE VOICE COACH",
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            color = GymElectricLime,
            letterSpacing = 1.sp
        )
        Text(
            text = "Model: gemini-3.8-live (Live API)",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(30.dp))

        // Pulse Microphone Button
        Box(
            modifier = Modifier
                .size(140.dp)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(if (isListening) GymElectricLime.copy(alpha = 0.2f) else GymNeonCyan.copy(alpha = 0.15f))
                .clickable { onToggleListening() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(if (isListening) GymElectricLime else GymNeonCyan),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.GraphicEq else Icons.Default.Mic,
                    contentDescription = "Live Voice Mic",
                    tint = Color.Black,
                    modifier = Modifier.size(42.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (isListening) "Listening in real-time..." else "Tap microphone to talk hands-free",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = if (isListening) GymElectricLime else MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Quick spoken voice cue triggers
        Text("Quick Voice Triggers:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { onSpokenQuery("How is my deadlift form?") }) {
                Text("Check Form", fontSize = 11.sp)
            }
            OutlinedButton(onClick = { onSpokenQuery("I'm feeling fatigued on this set!") }) {
                Text("Motivation", fontSize = 11.sp)
            }
            OutlinedButton(onClick = { onSpokenQuery("What should I eat post workout?") }) {
                Text("Post-Workout", fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (liveVoiceResponse != null) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, GymElectricLime.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = GymElectricLime, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = liveVoiceResponse,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// 3. VEO 3 VIDEO STUDIO (veo-3.1-fast-generate-preview)
// ----------------------------------------------------
@Composable
fun VeoVideoSection(
    videos: List<GeneratedVideoResult>,
    isGenerating: Boolean,
    onGenerateVideo: (String, String) -> Unit,
    onAnimatePhoto: (String, String) -> Unit
) {
    var prompt by remember { mutableStateOf("Barbell incline bench press with strict 3-second eccentric tempo") }
    var selectedAspect by remember { mutableStateOf("16:9") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, GymNeonCyan.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Videocam, contentDescription = null, tint = GymNeonCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "VEO 3 VIDEO GENERATION",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = GymNeonCyan
                        )
                    }
                    Text(
                        text = "Model: veo-3.1-fast-generate-preview · Generate exercise form demos or animate physique photos into video clips.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = prompt,
                        onValueChange = { prompt = it },
                        label = { Text("Video Prompt") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = selectedAspect == "16:9",
                                onClick = { selectedAspect = "16:9" },
                                label = { Text("16:9 (Landscape)", fontSize = 11.sp) }
                            )
                            FilterChip(
                                selected = selectedAspect == "9:16",
                                onClick = { selectedAspect = "9:16" },
                                label = { Text("9:16 (Portrait)", fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onGenerateVideo(prompt, selectedAspect) },
                            enabled = !isGenerating,
                            colors = ButtonDefaults.buttonColors(containerColor = GymNeonCyan, contentColor = Color.Black),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Movie, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Generate Video", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { onAnimatePhoto(prompt, selectedAspect) },
                            enabled = !isGenerating,
                            colors = ButtonDefaults.buttonColors(containerColor = GymElectricLime, contentColor = Color.Black),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Animate Photo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "RENDERED VEO VIDEOS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(videos) { vid ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, GymNeonCyan.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = GymNeonCyan)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(vid.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Text(vid.aspectRatio, fontSize = 10.sp, color = GymElectricLime, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = vid.prompt,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Model: ${vid.model}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(vid.status, fontSize = 10.sp, color = GymNeonCyan, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 4. GOOGLE MAPS GROUNDING (gemini-3.5-flash with googleMaps)
// ----------------------------------------------------
@Composable
fun MapsGroundingSection(
    locations: List<MapGroundingItem>,
    isSearching: Boolean,
    onSearch: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("Hardcore bodybuilding gyms near me") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, GymElectricLime.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Map, contentDescription = null, tint = GymElectricLime)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "GOOGLE MAPS GROUNDING",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = GymElectricLime
                    )
                }
                Text(
                    text = "Model: gemini-3.5-flash (with googleMaps tool) · Real-time grounded location data for gym facilities, nutrition bars & calisthenics parks.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search fitness spot") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { onSearch(searchQuery) },
                    enabled = !isSearching,
                    colors = ButtonDefaults.buttonColors(containerColor = GymElectricLime, contentColor = Color.Black),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Explore, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Search Maps Grounding", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "GROUNDED FITNESS LOCATIONS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(locations) { item ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(item.address, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = GymElectricAmber, modifier = Modifier.size(14.dp))
                                Text(" ${item.rating} · ${item.category}", fontSize = 10.sp, color = GymElectricAmber)
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                val uri = Uri.parse("geo:0,0?q=" + Uri.encode(item.title + " " + item.address))
                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                context.startActivity(intent)
                            }
                        ) {
                            Text("Open Map", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 5. CREATE & EDIT IMAGES (gemini-3.1-flash-image-preview)
// ----------------------------------------------------
@Composable
fun ImageStudioSection(
    isGenerating: Boolean,
    onGenerateImage: (String, String) -> Unit,
    status: String?
) {
    var prompt by remember { mutableStateOf("Hyper-realistic aesthetic V-taper physique silhouette in gym lighting") }
    var selectedAspect by remember { mutableStateOf("1:1") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, GymNeonCyan.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Image, contentDescription = null, tint = GymNeonCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CREATE & EDIT IMAGES",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = GymNeonCyan
                    )
                }
                Text(
                    text = "Model: gemini-3.1-flash-image-preview · Generate high-resolution fitness goal visuals, physique transformations, and high-protein meal preps.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    label = { Text("Image Prompt") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedAspect == "1:1",
                        onClick = { selectedAspect = "1:1" },
                        label = { Text("1:1 Square", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = selectedAspect == "4:3",
                        onClick = { selectedAspect = "4:3" },
                        label = { Text("4:3 Standard", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = selectedAspect == "9:16",
                        onClick = { selectedAspect = "9:16" },
                        label = { Text("9:16 Story", fontSize = 11.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { onGenerateImage(prompt, selectedAspect) },
                    enabled = !isGenerating,
                    colors = ButtonDefaults.buttonColors(containerColor = GymNeonCyan, contentColor = Color.Black),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Create Fitness Visual", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        if (status != null) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, GymNeonCyan.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Generated Visual Preview:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(status, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GymNeonCyan)
                }
            }
        }
    }
}

// ----------------------------------------------------
// 6. FIREBASE AUTH & FIRESTORE CLOUD PERSISTENCE
// ----------------------------------------------------
@Composable
fun FirebaseCloudSection(
    user: AppUserProfile?,
    syncStatus: String?,
    isSyncing: Boolean,
    onSignIn: (String, String) -> Unit,
    onSignOut: () -> Unit,
    onSyncFirestore: () -> Unit
) {
    var emailInput by remember { mutableStateOf(user?.email ?: "francissanto416@gmail.com") }
    var nameInput by remember { mutableStateOf(user?.displayName ?: "Francis Santo") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, GymElectricAmber.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = GymElectricAmber)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "FIREBASE AUTH & FIRESTORE DATABASE",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = GymElectricAmber
                        )
                        Text(
                            text = "Google Sign-in with Firebase Auth + Firestore Data Persistence",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (user != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, GymElectricLime.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(user.displayName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(user.email, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("UID: ${user.uid.take(12)}...", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text("Authenticated", color = GymElectricLime, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedButton(
                                onClick = onSignOut,
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Sign Out", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Display Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Google Account Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { onSignIn(emailInput, nameInput) },
                        colors = ButtonDefaults.buttonColors(containerColor = GymElectricAmber, contentColor = Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sign In with Google Firebase Auth", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onSyncFirestore,
                    enabled = !isSyncing,
                    colors = ButtonDefaults.buttonColors(containerColor = GymNeonCyan, contentColor = Color.Black),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isSyncing) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.Black, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Persisting to Firestore...", fontSize = 12.sp)
                    } else {
                        Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Persist User Data to Firestore", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                if (syncStatus != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = syncStatus,
                        fontSize = 11.sp,
                        color = GymElectricLime,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
