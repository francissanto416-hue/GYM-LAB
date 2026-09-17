package com.example.ui.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GymBlack
import com.example.ui.theme.GymElectricAmber
import com.example.ui.theme.GymElectricLime
import com.example.ui.theme.GymNeonCyan
import com.example.ui.viewmodel.ShareWorkoutSummary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialShareSheet(
    summary: ShareWorkoutSummary,
    onDismiss: () -> Unit,
    onPublishToCommunity: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var customCaption by remember { mutableStateOf("") }

    val shareText = buildString {
        append("🔥 CRUSHED IT WITH IRONPULSE GYM!\n\n")
        append("🏋️ Routine: ${summary.routineTitle}\n")
        append("⚡ Total Volume: ${summary.totalVolumeKg.toInt()} kg\n")
        append("⏱ Duration: ${summary.durationMinutes} minutes\n")
        append("📊 Sets Completed: ${summary.totalSets} sets (${summary.totalReps} total reps)\n")
        append("🏆 Top Performance: ${summary.bestSetPr}\n\n")
        append("#IronPulse #GymMotivation #Hypertrophy #WorkoutStreak")
    }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 14.dp,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GymElectricLime.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Celebration,
                            contentDescription = "Workout Complete",
                            tint = GymElectricAmber,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "WORKOUT CRUSHED!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Aesthetic Share Card (Instagram / WhatsApp ready format)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(GymBlack, Color(0xFF141A26), Color(0xFF0F141E))
                            )
                        )
                        .border(1.5.dp, GymElectricLime.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                        .padding(18.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "IRONPULSE ATHLETE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = GymElectricLime,
                                letterSpacing = 1.5.sp
                            )
                            Text(
                                text = summary.dateString,
                                fontSize = 11.sp,
                                color = Color(0xFF8E9EB5)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = summary.routineTitle,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Big Stats Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${summary.totalVolumeKg.toInt()} kg",
                                    fontSize = 22.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    color = GymElectricLime
                                )
                                Text(
                                    text = "TOTAL VOLUME",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF8E9EB5)
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${summary.durationMinutes}m",
                                    fontSize = 22.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    color = GymNeonCyan
                                )
                                Text(
                                    text = "DURATION",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF8E9EB5)
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${summary.totalSets}",
                                    fontSize = 22.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    color = GymElectricAmber
                                )
                                Text(
                                    text = "SETS COMPLETED",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF8E9EB5)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // PR Highlight Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(GymElectricLime.copy(alpha = 0.12f))
                                .border(1.dp, GymElectricLime.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                .padding(vertical = 8.dp, horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🏆 Top Effort: ${summary.bestSetPr}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GymElectricLime,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Caption for community post
                OutlinedTextField(
                    value = customCaption,
                    onValueChange = { customCaption = it },
                    placeholder = { Text("Add caption for friends & community feed...", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth().testTag("social_share_caption_input"),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Actions: Share via Intent & Post to Community
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // System Share Intent (Instagram, WhatsApp, Messages)
                    OutlinedButton(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Share Workout Accountability")
                            context.startActivity(shareIntent)
                        },
                        modifier = Modifier.weight(1f).testTag("system_share_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share App", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    // Post to Community Feed
                    Button(
                        onClick = { onPublishToCommunity(customCaption) },
                        modifier = Modifier.weight(1f).testTag("publish_community_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GymElectricLime,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(Icons.Default.Public, contentDescription = "Community", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Post Feed", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
