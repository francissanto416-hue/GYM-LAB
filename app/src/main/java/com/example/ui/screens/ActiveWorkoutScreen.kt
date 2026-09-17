package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SportsGymnastics
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WorkoutExercise
import com.example.data.model.WorkoutRoutine
import com.example.ui.components.MiniMusicBar
import com.example.ui.components.RestTimerSheet
import com.example.ui.theme.GymElectricAmber
import com.example.ui.theme.GymElectricLime
import com.example.ui.theme.GymHotRed
import com.example.ui.theme.GymNeonCyan
import com.example.ui.viewmodel.CurrentSetDraft
import com.example.ui.viewmodel.GymViewModel

@Composable
fun ActiveWorkoutScreen(
    viewModel: GymViewModel,
    onOpenMusicTab: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeRoutine by viewModel.activeRoutine.collectAsState()
    val exercises by viewModel.activeExercises.collectAsState()
    val currentIndex by viewModel.currentExerciseIndex.collectAsState()
    val elapsedSec by viewModel.workoutElapsedSec.collectAsState()
    val setsMap by viewModel.exerciseSetsMap.collectAsState()

    val isRestRunning by viewModel.isRestTimerRunning.collectAsState()
    val restRemainingSec by viewModel.restRemainingSec.collectAsState()
    val restTotalSec by viewModel.restTotalSec.collectAsState()
    val isAlarmEnabled by viewModel.isRestAlarmEnabled.collectAsState()
    val isVibrateEnabled by viewModel.isRestVibrateEnabled.collectAsState()
    val isVoiceEnabled by viewModel.isVoiceAssistantEnabled.collectAsState()

    val currentExercise = exercises.getOrNull(currentIndex)
    val currentSets = currentExercise?.let { setsMap[it.name] } ?: emptyList()

    val hours = elapsedSec / 3600
    val minutes = (elapsedSec % 3600) / 60
    val seconds = elapsedSec % 60
    val formattedDuration = String.format("%02d:%02d:%02d", hours, minutes, seconds)

    var workoutNotes by remember { mutableStateOf("") }

    if (activeRoutine == null || exercises.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(54.dp)
                )
                Text(
                    text = "No Active Workout Session",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Select a routine from the Workouts tab to begin logging sets",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Workout Top Header: Routine title, duration clock, and hands-free voice coach button
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "TRAINING IN PROGRESS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GymElectricLime,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = activeRoutine?.name ?: "Workout Session",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Elapsed Clock
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = "Session Timer",
                                        tint = GymNeonCyan,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = formattedDuration,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GymNeonCyan
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Fast Toolbar: Voice Assistant & Music playback control trigger
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Voice Coach Button
                            Surface(
                                onClick = { viewModel.openVoiceDialog() },
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.weight(1f).testTag("open_voice_coach_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = "Voice Coach",
                                        tint = if (isVoiceEnabled) GymElectricLime else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Voice Assistant",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            // Manual Rest Trigger
                            Surface(
                                onClick = { viewModel.startRestTimer(currentExercise?.defaultRestSec ?: 90) },
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.weight(1f).testTag("manual_start_rest_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = "Rest Timer",
                                        tint = GymElectricAmber,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Rest ${currentExercise?.defaultRestSec ?: 90}s",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Exercise Selection Carousel
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(exercises) { idx, ex ->
                        val isSelected = idx == currentIndex
                        val exSets = setsMap[ex.name] ?: emptyList()
                        val completedCount = exSets.count { it.isCompleted }
                        val isFullyDone = completedCount > 0 && completedCount == exSets.size

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) GymElectricLime
                                    else if (isFullyDone) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { viewModel.selectCurrentExerciseIndex(idx) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .testTag("exercise_chip_$idx")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isFullyDone) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Done",
                                        tint = GymNeonCyan,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text(
                                    text = "${idx + 1}. ${ex.name}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "$completedCount/${exSets.size}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isSelected) Color(0xFF1E2B08) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Active Exercise Card with Sets Table
            if (currentExercise != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
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
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = currentExercise.name,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Target Rest: ${currentExercise.defaultRestSec}s • Equipment: ${currentExercise.equipment}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row {
                                    IconButton(
                                        onClick = { viewModel.prevExercise() },
                                        enabled = currentIndex > 0
                                    ) {
                                        Icon(Icons.Default.NavigateBefore, contentDescription = "Previous Exercise")
                                    }
                                    IconButton(
                                        onClick = { viewModel.nextExercise() },
                                        enabled = currentIndex < exercises.size - 1
                                    ) {
                                        Icon(Icons.Default.NavigateNext, contentDescription = "Next Exercise")
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Sets Table Header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "SET", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(36.dp))
                                Text(text = "KG", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(72.dp))
                                Text(text = "REPS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(72.dp))
                                Text(text = "RPE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(44.dp))
                                Text(text = "LOG", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(44.dp))
                            }

                            // Set Rows
                            currentSets.forEachIndexed { setIdx, setDraft ->
                                SetRowItem(
                                    draft = setDraft,
                                    onUpdate = { weight, reps, rpe ->
                                        viewModel.updateSet(currentExercise.name, setIdx, weight, reps, rpe)
                                    },
                                    onToggleComplete = {
                                        viewModel.logAndCompleteSet(currentExercise.name, setIdx)
                                    }
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // + Add Set Button
                            OutlinedButton(
                                onClick = { viewModel.addSet(currentExercise.name) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("add_set_button_${currentExercise.name}"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Add Another Set", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // Automated Rest Timer (When active)
            if (isRestRunning) {
                item {
                    RestTimerSheet(
                        isRunning = isRestRunning,
                        remainingSec = restRemainingSec,
                        totalSec = restTotalSec,
                        isAlarmEnabled = isAlarmEnabled,
                        isVibrateEnabled = isVibrateEnabled,
                        isVoiceEnabled = isVoiceEnabled,
                        onAdjustTime = { delta -> viewModel.adjustRestTimer(delta) },
                        onPause = { viewModel.pauseRestTimer() },
                        onResume = { viewModel.resumeRestTimer() },
                        onSkip = { viewModel.skipRestTimer() },
                        onStartPreset = { sec -> viewModel.startRestTimer(sec) },
                        onToggleAlarm = { viewModel.toggleRestAlarm() },
                        onToggleVibrate = { viewModel.toggleRestVibrate() },
                        onToggleVoice = { viewModel.toggleVoiceAssistant() },
                        modifier = Modifier.testTag("active_rest_timer_sheet")
                    )
                }
            }

            // Embedded Mini Music Playback Controller for Training Screen
            item {
                Text(
                    text = "TRAINING AUDIO & MUSIC CONTROL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = GymElectricLime,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                MiniMusicBar(
                    musicPlayer = viewModel.musicPlayer,
                    onOpenFullMusic = onOpenMusicTab,
                    modifier = Modifier.clip(RoundedCornerShape(14.dp))
                )
            }

            // Workout Finish Button
            item {
                Button(
                    onClick = { viewModel.finishWorkout(workoutNotes) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("finish_workout_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GymElectricLime,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Finish Workout",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "FINISH WORKOUT & GENERATE STATS",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@Composable
fun SetRowItem(
    draft: CurrentSetDraft,
    onUpdate: (Double, Int, Int) -> Unit,
    onToggleComplete: () -> Unit
) {
    var weightText by remember(draft.weightKg) { mutableStateOf(draft.weightKg.toString()) }
    var repsText by remember(draft.reps) { mutableStateOf(draft.reps.toString()) }
    var rpeText by remember(draft.rpe) { mutableStateOf(draft.rpe.toString()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (draft.isCompleted) GymElectricLime.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface)
            .border(
                1.dp,
                if (draft.isCompleted) GymElectricLime.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Set #
        Text(
            text = "${draft.setNumber}",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = if (draft.isCompleted) GymElectricLime else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(36.dp)
        )

        // Weight Input (kg)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(72.dp)
        ) {
            OutlinedTextField(
                value = weightText,
                onValueChange = { input ->
                    weightText = input
                    val parsed = input.toDoubleOrNull() ?: draft.weightKg
                    onUpdate(parsed, draft.reps, draft.rpe)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("set_weight_input_${draft.setNumber}"),
                shape = RoundedCornerShape(8.dp)
            )
        }

        // Reps Input
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(72.dp)
        ) {
            OutlinedTextField(
                value = repsText,
                onValueChange = { input ->
                    repsText = input
                    val parsed = input.toIntOrNull() ?: draft.reps
                    onUpdate(draft.weightKg, parsed, draft.rpe)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("set_reps_input_${draft.setNumber}"),
                shape = RoundedCornerShape(8.dp)
            )
        }

        // RPE (Rate of Perceived Exertion)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(44.dp)
        ) {
            OutlinedTextField(
                value = rpeText,
                onValueChange = { input ->
                    rpeText = input
                    val parsed = input.toIntOrNull() ?: draft.rpe
                    onUpdate(draft.weightKg, draft.reps, parsed)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(8.dp)
            )
        }

        // Log Checkbox
        IconButton(
            onClick = onToggleComplete,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (draft.isCompleted) GymElectricLime else MaterialTheme.colorScheme.surfaceVariant)
                .testTag("toggle_set_complete_${draft.setNumber}")
        ) {
            Icon(
                imageVector = if (draft.isCompleted) Icons.Default.Check else Icons.Default.RadioButtonUnchecked,
                contentDescription = "Complete Set",
                tint = if (draft.isCompleted) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
