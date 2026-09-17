package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.media.WorkoutMusicPlayer
import com.example.ui.theme.GymElectricAmber
import com.example.ui.theme.GymElectricLime
import com.example.ui.theme.GymNeonCyan
import com.example.ui.theme.GymPurplePulse

@Composable
fun MusicScreen(
    musicPlayer: WorkoutMusicPlayer,
    modifier: Modifier = Modifier
) {
    val isPlaying by musicPlayer.isPlaying.collectAsState()
    val currentTrack by musicPlayer.currentTrack.collectAsState()
    val bpm by musicPlayer.bpm.collectAsState()
    val progressSec by musicPlayer.playbackProgressSec.collectAsState()
    val playlist = musicPlayer.playlist

    val progressFraction = if (currentTrack.durationSec > 0) progressSec.toFloat() / currentTrack.durationSec else 0f
    val minutes = progressSec / 60
    val seconds = progressSec % 60
    val durationMin = currentTrack.durationSec / 60
    val durationSecRem = currentTrack.durationSec % 60

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Screen Header
        item {
            Column {
                Text(
                    text = "WORKOUT BEAT ENGINE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = GymNeonCyan,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Music & Playlists",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "High-tempo rhythmic synthesizer tuned for PR adrenaline and focus",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Active Now Playing Vinyl / Deck Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.5.dp, GymNeonCyan.copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Pulsing Visualizer Spectrum Box
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(GymNeonCyan.copy(alpha = 0.3f), Color(0xFF101928), Color(0xFF090C12))
                                )
                            )
                            .border(2.dp, if (isPlaying) GymNeonCyan else MaterialTheme.colorScheme.outline, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedBeatSpectrum(isPlaying = isPlaying)
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = currentTrack.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${currentTrack.artist} • ${currentTrack.genre}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Progress Bar
                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = GymNeonCyan,
                        trackColor = MaterialTheme.colorScheme.surface
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = String.format("%02d:%02d", minutes, seconds),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = String.format("%02d:%02d", durationMin, durationSecRem),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Player Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { musicPlayer.prevTrack() },
                            modifier = Modifier.size(48.dp).testTag("music_prev_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipPrevious,
                                contentDescription = "Previous Track",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Play/Pause Big Button
                        Surface(
                            onClick = { musicPlayer.togglePlayPause() },
                            shape = CircleShape,
                            color = GymNeonCyan,
                            modifier = Modifier
                                .size(64.dp)
                                .testTag("music_play_pause_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isPlaying) "Pause" else "Play",
                                    tint = Color.Black,
                                    modifier = Modifier.size(34.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        IconButton(
                            onClick = { musicPlayer.nextTrack() },
                            modifier = Modifier.size(48.dp).testTag("music_next_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Next Track",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }

        // Tempo / BPM Pitch Slider Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Speed, contentDescription = null, tint = GymElectricLime, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "WORKOUT CADENCE (BPM)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = GymElectricLime,
                                letterSpacing = 0.8.sp
                            )
                        }
                        Text(
                            text = "$bpm BPM",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Slider(
                        value = bpm.toFloat(),
                        onValueChange = { musicPlayer.setBpm(it.toInt()) },
                        valueRange = 100f..180f,
                        steps = 15,
                        colors = SliderDefaults.colors(
                            thumbColor = GymElectricLime,
                            activeTrackColor = GymElectricLime,
                            inactiveTrackColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.testTag("music_bpm_slider")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "100 Warmup", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "140 Hypertrophy", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "180 PR Sprint", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Gym Playlist Queue
        item {
            Text(
                text = "GYM PLAYLIST TRACKS (${playlist.size})",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = 0.5.sp
            )
        }

        itemsIndexed(playlist) { index, track ->
            val isCurrent = track.title == currentTrack.title
            Surface(
                onClick = { musicPlayer.selectTrack(index) },
                shape = RoundedCornerShape(12.dp),
                color = if (isCurrent) GymNeonCyan.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isCurrent) GymNeonCyan.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("playlist_track_$index")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isCurrent) GymNeonCyan else MaterialTheme.colorScheme.surface),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isCurrent && isPlaying) Icons.Default.GraphicEq else Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = if (isCurrent) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = track.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isCurrent) GymNeonCyan else MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${track.artist} • ${track.genre}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }

                    Text(
                        text = "${track.durationSec / 60}:${String.format("%02d", track.durationSec % 60)}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun AnimatedBeatSpectrum(isPlaying: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "beat_transition")
    val anim1 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = if (isPlaying) 1f else 0.3f,
        animationSpec = infiniteRepeatable(tween(400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bar1"
    )
    val anim2 by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = if (isPlaying) 0.9f else 0.4f,
        animationSpec = infiniteRepeatable(tween(550, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bar2"
    )
    val anim3 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = if (isPlaying) 0.85f else 0.2f,
        animationSpec = infiniteRepeatable(tween(350, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bar3"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.width(6.dp).height((40 * anim1).dp).clip(RoundedCornerShape(3.dp)).background(GymNeonCyan))
        Box(modifier = Modifier.width(6.dp).height((65 * anim2).dp).clip(RoundedCornerShape(3.dp)).background(GymElectricLime))
        Box(modifier = Modifier.width(6.dp).height((80 * anim3).dp).clip(RoundedCornerShape(3.dp)).background(GymElectricAmber))
        Box(modifier = Modifier.width(6.dp).height((50 * anim1).dp).clip(RoundedCornerShape(3.dp)).background(GymPurplePulse))
        Box(modifier = Modifier.width(6.dp).height((35 * anim2).dp).clip(RoundedCornerShape(3.dp)).background(GymNeonCyan))
    }
}
