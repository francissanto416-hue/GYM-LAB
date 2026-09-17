package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.WorkoutExercise
import com.example.data.model.WorkoutRoutine
import com.example.ui.theme.GymElectricAmber
import com.example.ui.theme.GymElectricLime
import com.example.ui.theme.GymNeonCyan

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RoutinesScreen(
    routines: List<WorkoutRoutine>,
    onStartWorkout: (WorkoutRoutine) -> Unit,
    onCreateCustomRoutine: (String, String, String, String, String, List<WorkoutExercise>) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("All") }
    var showCustomRoutineDialog by remember { mutableStateOf(false) }

    val categories = listOf("All", "Push", "Pull", "Legs", "Upper", "Full Body")
    val filteredRoutines = if (selectedCategory == "All") {
        routines
    } else {
        routines.filter { it.splitCategory.equals(selectedCategory, ignoreCase = true) }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.gym_hero_banner),
                        contentDescription = "Gym Training Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color(0xCC090C12), Color(0xFF090C12))
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(20.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ElectricBolt,
                                contentDescription = null,
                                tint = GymElectricLime,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "HYPERTROPHY & POWER ENGINE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GymElectricLime,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = "Gym Routines & Sets",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "Select a split or build custom sets with automated rest timers",
                            fontSize = 12.sp,
                            color = Color(0xFFB0BDD0)
                        )
                    }
                }
            }

            // Category Filter Pills
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = selectedCategory == category
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) GymElectricLime else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { selectedCategory = category }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .testTag("filter_category_$category")
                        ) {
                            Text(
                                text = category,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Routine Cards List
            items(filteredRoutines) { routine ->
                RoutineCard(
                    routine = routine,
                    onStart = { onStartWorkout(routine) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // FAB to build custom routine
        FloatingActionButton(
            onClick = { showCustomRoutineDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 70.dp, end = 20.dp)
                .testTag("create_custom_routine_fab"),
            containerColor = GymElectricLime,
            contentColor = Color.Black
        ) {
            Icon(Icons.Default.Add, contentDescription = "Create Custom Routine")
        }
    }

    if (showCustomRoutineDialog) {
        CreateRoutineDialog(
            onDismiss = { showCustomRoutineDialog = false },
            onSave = { name, split, desc, level, muscles, exercises ->
                onCreateCustomRoutine(name, split, desc, level, muscles, exercises)
                showCustomRoutineDialog = false
            }
        )
    }
}

@Composable
fun RoutineCard(
    routine: WorkoutRoutine,
    onStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val exerciseCount = routine.exerciseListJson.split(",").filter { it.isNotBlank() }.size

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
            .testTag("routine_card_${routine.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Split Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(GymNeonCyan.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = routine.splitCategory.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = GymNeonCyan
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Duration",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${routine.estimatedMinutes} min",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "• ${routine.level}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GymElectricAmber
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = routine.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = routine.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Target Muscles",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = routine.targetMuscles,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = "$exerciseCount Exercises",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GymElectricLime
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("start_workout_button_${routine.id}"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GymElectricLime,
                    contentColor = Color.Black
                )
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Start Workout",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "START WORKOUT",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRoutineDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, List<WorkoutExercise>) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var splitCategory by remember { mutableStateOf("Custom") }
    var description by remember { mutableStateOf("") }
    var level by remember { mutableStateOf("Intermediate") }
    var targetMuscles by remember { mutableStateOf("Chest, Arms, Core") }
    var exerciseNameInput by remember { mutableStateOf("") }
    var targetSetsInput by remember { mutableStateOf("4") }
    var targetRepsInput by remember { mutableStateOf("10") }
    var defaultWeightInput by remember { mutableStateOf("50") }
    var defaultRestInput by remember { mutableStateOf("90") }

    val exercises = remember {
        mutableListOf(
            WorkoutExercise("1", "Dumbbell Press", "Chest", "Dumbbell", 4, 10, 24.0, 90),
            WorkoutExercise("2", "Cable Flyes", "Chest", "Cable", 3, 12, 18.0, 60),
            WorkoutExercise("3", "Tricep Extension", "Triceps", "Cable", 4, 12, 25.0, 60)
        )
    }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GymElectricLime.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                .padding(18.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "CREATE CUSTOM ROUTINE",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = GymElectricLime
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Routine Name") },
                    placeholder = { Text("e.g. Arm Day Hypertrophy") },
                    modifier = Modifier.fillMaxWidth().testTag("custom_routine_name_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description & Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = splitCategory,
                        onValueChange = { splitCategory = it },
                        label = { Text("Split") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = targetMuscles,
                        onValueChange = { targetMuscles = it },
                        label = { Text("Target Muscles") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Text(
                    text = "Exercises in Routine (${exercises.size}):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    exercises.forEach { ex ->
                        Text(
                            text = "• ${ex.name}: ${ex.targetSets} sets x ${ex.targetReps} reps (${ex.defaultWeightKg}kg, ${ex.defaultRestSec}s rest)",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Add Exercise Mini-Form
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                        value = exerciseNameInput,
                        onValueChange = { exerciseNameInput = it },
                        placeholder = { Text("Exercise") },
                        modifier = Modifier.weight(2f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = targetSetsInput,
                        onValueChange = { targetSetsInput = it },
                        placeholder = { Text("Sets") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = targetRepsInput,
                        onValueChange = { targetRepsInput = it },
                        placeholder = { Text("Reps") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Button(
                    onClick = {
                        if (exerciseNameInput.isNotBlank()) {
                            exercises.add(
                                WorkoutExercise(
                                    id = exerciseNameInput.lowercase(),
                                    name = exerciseNameInput,
                                    targetMuscle = targetMuscles,
                                    equipment = "Machine/Free Weight",
                                    targetSets = targetSetsInput.toIntOrNull() ?: 4,
                                    targetReps = targetRepsInput.toIntOrNull() ?: 10,
                                    defaultWeightKg = defaultWeightInput.toDoubleOrNull() ?: 50.0,
                                    defaultRestSec = defaultRestInput.toIntOrNull() ?: 90
                                )
                            )
                            exerciseNameInput = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text("+ Add Exercise to List", fontSize = 12.sp, color = GymNeonCyan)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onSave(name, splitCategory, description, level, targetMuscles, exercises)
                            }
                        },
                        modifier = Modifier.weight(1f).testTag("save_custom_routine_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = GymElectricLime, contentColor = Color.Black)
                    ) {
                        Text("Save Routine", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
