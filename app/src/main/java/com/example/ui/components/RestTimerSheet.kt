package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VoiceOverOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GymElectricAmber
import com.example.ui.theme.GymElectricLime
import com.example.ui.theme.GymNeonCyan
import com.example.ui.theme.GymSurfaceCard
import com.example.ui.theme.GymSurfaceElevated

@Composable
fun RestTimerSheet(
    isRunning: Boolean,
    remainingSec: Int,
    totalSec: Int,
    isAlarmEnabled: Boolean,
    isVibrateEnabled: Boolean,
    isVoiceEnabled: Boolean,
    onAdjustTime: (Int) -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onSkip: () -> Unit,
    onStartPreset: (Int) -> Unit,
    onToggleAlarm: () -> Unit,
    onToggleVibrate: () -> Unit,
    onToggleVoice: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = if (totalSec > 0) remainingSec.toFloat() / totalSec.toFloat() else 0f
    val minutes = remainingSec / 60
    val seconds = remainingSec % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Drag handle / Header
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isRunning) GymElectricLime else GymElectricAmber)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isRunning) "AUTOMATED REST INTERVAL" else "REST PAUSED",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isRunning) GymElectricLime else GymElectricAmber,
                        letterSpacing = 1.sp
                    )
                }

                // Audio / Haptic / Voice switches
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onToggleAlarm,
                        modifier = Modifier.size(36.dp).testTag("toggle_rest_alarm_button")
                    ) {
                        Icon(
                            imageVector = if (isAlarmEnabled) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                            contentDescription = "Rest Alarm",
                            tint = if (isAlarmEnabled) GymElectricLime else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = onToggleVibrate,
                        modifier = Modifier.size(36.dp).testTag("toggle_rest_vibrate_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Vibration,
                            contentDescription = "Rest Vibration",
                            tint = if (isVibrateEnabled) GymNeonCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = onToggleVoice,
                        modifier = Modifier.size(36.dp).testTag("toggle_rest_voice_button")
                    ) {
                        Icon(
                            imageVector = if (isVoiceEnabled) Icons.Default.RecordVoiceOver else Icons.Default.VoiceOverOff,
                            contentDescription = "Voice Coach",
                            tint = if (isVoiceEnabled) GymElectricAmber else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Glowing Circular Rest Countdown
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(170.dp)
            ) {
                // Background Track
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.size(160.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    strokeWidth = 10.dp,
                    trackColor = Color.Transparent
                )
                // Active countdown ring
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(160.dp),
                    color = if (remainingSec <= 10) GymElectricAmber else GymElectricLime,
                    strokeWidth = 10.dp,
                    strokeCap = StrokeCap.Round,
                    trackColor = Color.Transparent
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.displaySmall,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "RECOVERY TIMER",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.8.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Adjust and Control Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // -15s Button
                Surface(
                    onClick = { onAdjustTime(-15) },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.testTag("rest_minus_15_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Minus 15s", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("15s", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                // Pause / Resume Button
                Surface(
                    onClick = { if (isRunning) onPause() else onResume() },
                    shape = CircleShape,
                    color = if (isRunning) GymElectricAmber else GymElectricLime,
                    modifier = Modifier
                        .size(54.dp)
                        .testTag("rest_play_pause_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isRunning) "Pause Rest" else "Resume Rest",
                            tint = Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // +15s Button
                Surface(
                    onClick = { onAdjustTime(15) },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.testTag("rest_plus_15_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add 15s", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("15s", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                // Skip Rest Button
                Surface(
                    onClick = onSkip,
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.testTag("rest_skip_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.SkipNext, contentDescription = "Skip Rest", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Skip", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Presets row: 45s, 60s, 90s, 120s, 180s
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val presets = listOf(45, 60, 90, 120, 180)
                presets.forEach { sec ->
                    val isSelected = totalSec == sec
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                            .clickable { onStartPreset(sec) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${sec}s",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
