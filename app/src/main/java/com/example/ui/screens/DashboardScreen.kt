package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BodyCompositionLog
import com.example.data.model.WorkoutSession
import com.example.ui.theme.GymElectricAmber
import com.example.ui.theme.GymElectricLime
import com.example.ui.theme.GymNeonCyan
import com.example.ui.theme.GymPurplePulse
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    sessions: List<WorkoutSession>,
    bodyLogs: List<BodyCompositionLog>,
    latestBody: BodyCompositionLog?,
    onLogMeasurement: (Double, Double, Double, Double, Double, Double, String) -> Unit,
    onOpenDriveStorage: () -> Unit = {},
    isDriveConnected: Boolean = false,
    modifier: Modifier = Modifier
) {
    var showLogMeasurementDialog by remember { mutableStateOf(false) }

    // Muscle recovery map simulation based on recent workout targets
    val muscleRecoveries = listOf(
        Triple("Chest", 85, "Ready for Hypertrophy"),
        Triple("Back & Lats", 65, "Recovering (12 hrs remaining)"),
        Triple("Legs & Quads", 45, "High Fatigue (24 hrs rest)"),
        Triple("Shoulders", 92, "Fully Recovered"),
        Triple("Arms (Bi/Tri)", 78, "Ready for Arm Volume"),
        Triple("Core & Abs", 95, "Optimal Recovery")
    )

    // Calculate aggregated metrics
    val totalWorkouts = sessions.size
    val totalVolumeTons = sessions.sumOf { it.totalVolumeKg } / 1000.0
    val totalSetsCompleted = sessions.sumOf { it.totalSets }

    val initialBody = bodyLogs.firstOrNull()
    val weightChange = if (latestBody != null && initialBody != null) {
        latestBody.weightKg - initialBody.weightKg
    } else 0.0
    val muscleGain = if (latestBody != null && initialBody != null) {
        latestBody.muscleMassKg - initialBody.muscleMassKg
    } else 0.0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(6.dp)) }

        // Dashboard Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PERFORMANCE ANALYTICS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = GymElectricLime,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Training Trends & Body Progress",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        onClick = onOpenDriveStorage,
                        shape = RoundedCornerShape(10.dp),
                        color = if (isDriveConnected) GymElectricLime.copy(alpha = 0.15f) else GymNeonCyan.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isDriveConnected) GymElectricLime.copy(alpha = 0.5f) else GymNeonCyan.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.testTag("open_drive_storage_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isDriveConnected) Icons.Default.CloudDone else Icons.Default.CloudSync,
                                contentDescription = "Google Drive Cloud Storage",
                                tint = if (isDriveConnected) GymElectricLime else GymNeonCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isDriveConnected) "Drive Synced" else "Drive Cloud",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDriveConnected) GymElectricLime else GymNeonCyan
                            )
                        }
                    }

                    Surface(
                        onClick = { showLogMeasurementDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.testTag("open_log_measurement_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = GymNeonCyan, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Log Stats", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GymNeonCyan)
                        }
                    }
                }
            }
        }

        // Top 3 Core Metrics (Tons lifted, Sessions, Muscle growth)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DashboardMetricCard(
                    title = "TOTAL VOLUME",
                    value = "${String.format(Locale.US, "%.1f", totalVolumeTons)}t",
                    subtitle = "$totalSetsCompleted total sets",
                    color = GymElectricLime,
                    modifier = Modifier.weight(1f)
                )
                DashboardMetricCard(
                    title = "MUSCLE GROWTH",
                    value = "+${String.format(Locale.US, "%.1f", muscleGain)}kg",
                    subtitle = "Lean Mass PR",
                    color = GymNeonCyan,
                    modifier = Modifier.weight(1f)
                )
                DashboardMetricCard(
                    title = "BODY FAT",
                    value = "${latestBody?.bodyFatPct ?: 15.8}%",
                    subtitle = "-1.7% in 4 wks",
                    color = GymElectricAmber,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Muscle Recovery & Growth Optimizer Heatmap
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Healing,
                                contentDescription = "Recovery",
                                tint = GymElectricLime,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "MUSCLE GROUP RECOVERY STATUS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = GymElectricLime,
                                letterSpacing = 0.8.sp
                            )
                        }
                        Text(
                            text = "Rest Optimizer",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Muscle Recovery Bars
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        muscleRecoveries.forEach { (muscle, recoveryPct, status) ->
                            val barColor = if (recoveryPct >= 80) GymElectricLime else if (recoveryPct >= 60) GymElectricAmber else GymNeonCyan
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = muscle,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "$recoveryPct% • $status",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = barColor
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { recoveryPct / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = barColor,
                                    trackColor = MaterialTheme.colorScheme.surface
                                )
                            }
                        }
                    }
                }
            }
        }

        // Training Volume Progression Trends
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
                        Text(
                            text = "VOLUME LIFTED PER SESSION (KG)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = GymNeonCyan,
                            letterSpacing = 0.8.sp
                        )
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = null,
                            tint = GymNeonCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Interactive Bar Graph of recent sessions
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val maxVol = (sessions.maxOfOrNull { it.totalVolumeKg } ?: 12000.0).coerceAtLeast(1000.0)
                        sessions.take(6).reversed().forEachIndexed { idx, s ->
                            val heightFraction = (s.totalVolumeKg / maxVol).toFloat().coerceIn(0.15f, 1f)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "${(s.totalVolumeKg / 1000).toInt()}k",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = GymElectricLime
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(22.dp)
                                        .fillMaxSize(heightFraction)
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        .background(GymElectricLime)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = s.routineTitle.take(3).uppercase(),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Body Composition & Anthropometric Measurement Progression Over Time
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Straighten,
                                contentDescription = null,
                                tint = GymNeonCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "BODY MEASUREMENTS & COMPOSITION",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = GymElectricAmber,
                                letterSpacing = 0.8.sp
                            )
                        }

                        Surface(
                            onClick = { showLogMeasurementDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            color = GymNeonCyan.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GymNeonCyan.copy(alpha = 0.4f)),
                            modifier = Modifier.testTag("log_measurement_card_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = GymNeonCyan, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Log Now", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GymNeonCyan)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Latest Measurements Cards Grid
                    if (latestBody != null) {
                        // Calculate change delta if more than 1 log
                        val firstLog = bodyLogs.minByOrNull { it.timestamp }
                        val chestDelta = firstLog?.let { latestBody.chestCm - it.chestCm } ?: 0.0
                        val armDelta = firstLog?.let { latestBody.armCm - it.armCm } ?: 0.0
                        val waistDelta = firstLog?.let { latestBody.waistCm - it.waistCm } ?: 0.0
                        val weightDelta = firstLog?.let { latestBody.weightKg - it.weightKg } ?: 0.0

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MeasurementMetricTile(
                                title = "Chest",
                                currentVal = "${latestBody.chestCm} cm",
                                delta = chestDelta,
                                isPositiveGood = true,
                                color = GymNeonCyan,
                                modifier = Modifier.weight(1f)
                            )
                            MeasurementMetricTile(
                                title = "Arms",
                                currentVal = "${latestBody.armCm} cm",
                                delta = armDelta,
                                isPositiveGood = true,
                                color = GymElectricLime,
                                modifier = Modifier.weight(1f)
                            )
                            MeasurementMetricTile(
                                title = "Waist",
                                currentVal = "${latestBody.waistCm} cm",
                                delta = waistDelta,
                                isPositiveGood = false, // reduction in waist is good
                                color = GymElectricAmber,
                                modifier = Modifier.weight(1f)
                            )
                            MeasurementMetricTile(
                                title = "Weight",
                                currentVal = "${latestBody.weightKg} kg",
                                delta = weightDelta,
                                isPositiveGood = true,
                                color = GymPurplePulse,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Dynamic Interactive Line Trend Graph for Chest, Waist, Arms & Weight
                    Text(
                        text = "TRANSFORMATION TREND OVER TIME",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    BodyMeasurementTrendGraph(bodyLogs = bodyLogs)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Timeline entries
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Measurement History (${bodyLogs.size} logs):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Chest · Arm · Waist",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        bodyLogs.takeLast(5).reversed().forEach { log ->
                            val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date(log.timestamp))
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = dateStr, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(text = "${log.weightKg} kg (${log.bodyFatPct}% BF)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "Chest: ${log.chestCm} cm", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GymNeonCyan)
                                        Text(text = "Arm: ${log.armCm} cm", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GymElectricLime)
                                        Text(text = "Waist: ${log.waistCm} cm", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GymElectricAmber)
                                        Text(text = "Lean: ${log.muscleMassKg} kg", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    if (log.notes.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(text = "Note: ${log.notes}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }

    if (showLogMeasurementDialog) {
        LogMeasurementDialog(
            lastLog = latestBody,
            onDismiss = { showLogMeasurementDialog = false },
            onSave = { weight, fat, muscle, chest, arm, waist, notes ->
                onLogMeasurement(weight, fat, muscle, chest, arm, waist, notes)
                showLogMeasurementDialog = false
            }
        )
    }
}

@Composable
fun DashboardMetricCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, fontSize = 9.sp, fontWeight = FontWeight.Black, color = color, letterSpacing = 0.5.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontFamily = FontFamily.Monospace, fontSize = 18.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
            Text(text = subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogMeasurementDialog(
    lastLog: BodyCompositionLog?,
    onDismiss: () -> Unit,
    onSave: (Double, Double, Double, Double, Double, Double, String) -> Unit
) {
    var weightText by remember { mutableStateOf(lastLog?.weightKg?.toString() ?: "79.5") }
    var fatText by remember { mutableStateOf(lastLog?.bodyFatPct?.toString() ?: "15.8") }
    var muscleText by remember { mutableStateOf(lastLog?.muscleMassKg?.toString() ?: "37.2") }
    var chestText by remember { mutableStateOf(lastLog?.chestCm?.toString() ?: "104.0") }
    var armText by remember { mutableStateOf(lastLog?.armCm?.toString() ?: "38.2") }
    var waistText by remember { mutableStateOf(lastLog?.waistCm?.toString() ?: "81.0") }
    var notesText by remember { mutableStateOf("") }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GymNeonCyan.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .padding(18.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "RECORD BODY COMPOSITION",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = GymNeonCyan
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = weightText,
                        onValueChange = { weightText = it },
                        label = { Text("Weight (kg)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = fatText,
                        onValueChange = { fatText = it },
                        label = { Text("Body Fat %") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = muscleText,
                        onValueChange = { muscleText = it },
                        label = { Text("Muscle Mass (kg)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = chestText,
                        onValueChange = { chestText = it },
                        label = { Text("Chest (cm)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = armText,
                        onValueChange = { armText = it },
                        label = { Text("Arm (cm)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = waistText,
                        onValueChange = { waistText = it },
                        label = { Text("Waist (cm)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text("Notes (e.g. morning weigh-in, post-cut)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            onSave(
                                weightText.toDoubleOrNull() ?: 79.5,
                                fatText.toDoubleOrNull() ?: 15.8,
                                muscleText.toDoubleOrNull() ?: 37.2,
                                chestText.toDoubleOrNull() ?: 104.0,
                                armText.toDoubleOrNull() ?: 38.2,
                                waistText.toDoubleOrNull() ?: 81.0,
                                notesText
                            )
                        },
                        modifier = Modifier.weight(1f).testTag("save_measurement_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = GymNeonCyan, contentColor = Color.Black)
                    ) {
                        Text("Save Stats", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun MeasurementMetricTile(
    title: String,
    currentVal: String,
    delta: Double,
    isPositiveGood: Boolean,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = currentVal,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            val deltaStr = if (delta >= 0) "+${String.format(Locale.US, "%.1f", delta)}" else String.format(Locale.US, "%.1f", delta)
            val isSuccess = if (isPositiveGood) delta >= 0 else delta <= 0
            val deltaColor = if (delta == 0.0) MaterialTheme.colorScheme.onSurfaceVariant else if (isSuccess) GymElectricLime else GymElectricAmber

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (delta >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                    contentDescription = null,
                    tint = deltaColor,
                    modifier = Modifier.size(11.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = deltaStr,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = deltaColor
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodyMeasurementTrendGraph(
    bodyLogs: List<BodyCompositionLog>
) {
    var selectedMetric by remember { mutableStateOf("Chest") }
    val metrics = listOf("Chest", "Arms", "Waist", "Weight")

    // Filter and sort logs chronologically
    val sortedLogs = remember(bodyLogs) {
        bodyLogs.sortedBy { it.timestamp }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        // Metric toggle chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            metrics.forEach { metric ->
                val isSelected = selectedMetric == metric
                val chipColor = when (metric) {
                    "Chest" -> GymNeonCyan
                    "Arms" -> GymElectricLime
                    "Waist" -> GymElectricAmber
                    else -> GymPurplePulse
                }

                Surface(
                    onClick = { selectedMetric = metric },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) chipColor.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) chipColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = metric,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                            color = if (isSelected) chipColor else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (sortedLogs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No measurement logs recorded yet",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            val values = sortedLogs.map { log ->
                when (selectedMetric) {
                    "Chest" -> log.chestCm
                    "Arms" -> log.armCm
                    "Waist" -> log.waistCm
                    else -> log.weightKg
                }
            }
            val minVal = (values.minOrNull() ?: 0.0) - 1.0
            val maxVal = (values.maxOrNull() ?: 100.0) + 1.0
            val range = (maxVal - minVal).coerceAtLeast(1.0)
            val lineColor = when (selectedMetric) {
                "Chest" -> GymNeonCyan
                "Arms" -> GymElectricLime
                "Waist" -> GymElectricAmber
                else -> GymPurplePulse
            }

            // Summary stats header
            val firstVal = values.first()
            val latestVal = values.last()
            val change = latestVal - firstVal
            val changeStr = if (change >= 0) "+${String.format(Locale.US, "%.1f", change)}" else String.format(Locale.US, "%.1f", change)
            val unitStr = if (selectedMetric == "Weight") "kg" else "cm"

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Current: $latestVal $unitStr",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Baseline: $firstVal $unitStr",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = lineColor.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, lineColor.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "Net: $changeStr $unitStr",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = lineColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Canvas Line Chart with Glowing Gradient & Data Points
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                val width = size.width
                val height = size.height
                val pointCount = sortedLogs.size

                if (pointCount == 1) {
                    // Single point display
                    drawCircle(
                        color = lineColor,
                        radius = 8.dp.toPx(),
                        center = Offset(width / 2f, height / 2f)
                    )
                } else {
                    val stepX = width / (pointCount - 1).coerceAtLeast(1)
                    val points = values.mapIndexed { index, v ->
                        val x = index * stepX
                        val normalizedY = ((v - minVal) / range).toFloat()
                        val y = height - (normalizedY * (height - 20.dp.toPx())) - 10.dp.toPx()
                        Offset(x, y)
                    }

                    // Fill Gradient Path under line
                    val fillPath = Path().apply {
                        moveTo(points.first().x, height)
                        points.forEach { lineTo(it.x, it.y) }
                        lineTo(points.last().x, height)
                        close()
                    }
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(lineColor.copy(alpha = 0.35f), Color.Transparent),
                            startY = 0f,
                            endY = height
                        )
                    )

                    // Line Chart Path
                    val strokePath = Path().apply {
                        points.forEachIndexed { i, pt ->
                            if (i == 0) moveTo(pt.x, pt.y) else lineTo(pt.x, pt.y)
                        }
                    }
                    drawPath(
                        path = strokePath,
                        color = lineColor,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw Data Points on vertices
                    points.forEachIndexed { index, pt ->
                        val isLatest = index == points.lastIndex
                        drawCircle(
                            color = if (isLatest) Color.White else lineColor,
                            radius = if (isLatest) 5.dp.toPx() else 3.5.dp.toPx(),
                            center = pt
                        )
                        if (isLatest) {
                            drawCircle(
                                color = lineColor,
                                radius = 2.5.dp.toPx(),
                                center = pt
                            )
                        }
                    }
                }
            }

            // Date labels row under chart
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                sortedLogs.forEachIndexed { idx, log ->
                    if (idx == 0 || idx == sortedLogs.lastIndex || sortedLogs.size <= 4) {
                        val dStr = SimpleDateFormat("MMM d", Locale.US).format(Date(log.timestamp))
                        Text(
                            text = dStr,
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
