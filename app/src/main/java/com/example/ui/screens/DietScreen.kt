package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.SetMeal
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.MealLog
import com.example.ui.theme.GymElectricAmber
import com.example.ui.theme.GymElectricLime
import com.example.ui.theme.GymNeonCyan
import com.example.ui.theme.GymPurplePulse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietScreen(
    meals: List<MealLog>,
    isVegPreference: Boolean,
    dietGoal: String,
    userWeightKg: Double = 78.0,
    userHeightCm: Double = 178.0,
    onUpdateWeightAndHeight: (Double, Double) -> Unit = { _, _ -> },
    onToggleVegPreference: (Boolean) -> Unit,
    onSelectGoal: (String) -> Unit,
    onToggleMeal: (MealLog) -> Unit,
    onDeleteMeal: (Long) -> Unit,
    onAddCustomMeal: (String, Boolean, Int, Double, Double, Double, Double, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddMealDialog by remember { mutableStateOf(false) }
    var showProfileBodyDialog by remember { mutableStateOf(false) }

    // Filter meals matching Veg preference or user custom meals
    val displayedMeals = meals.filter { it.isVeg == isVegPreference || it.isLogged }

    // Aggregate Macros
    val loggedMeals = meals.filter { it.isLogged }
    val totalCalories = loggedMeals.sumOf { it.calories }
    val totalProtein = loggedMeals.sumOf { it.proteinG }
    val totalCarbs = loggedMeals.sumOf { it.carbsG }
    val totalFats = loggedMeals.sumOf { it.fatsG }
    val totalFiber = loggedMeals.sumOf { it.fiberG }

    // Scientific Body Metrology:
    // BMI = weight / (height/100)^2
    val heightM = (userHeightCm / 100.0).coerceAtLeast(1.0)
    val bmi = userWeightKg / (heightM * heightM)
    // Harris-Benedict / Mifflin-St Jeor estimate of BMR for athletic active individuals
    val bmr = (10.0 * userWeightKg) + (6.25 * userHeightCm) - (5.0 * 25.0) + 5.0
    val tdee = bmr * 1.45 // Active gym training factor

    // Dynamic Macro & Protein Calculations based on Body Aesthetic Goals:
    // Weight Gain (Clean Bulk): 2.0g protein / kg bodyweight, +350 surplus
    // Weight Loss (Aesthetic Cut): 2.4g protein / kg bodyweight (spares lean muscle tissue under deficit), -450 deficit
    // Maintain Aesthetic Body (Lean Recomp / V-Taper): 2.2g protein / kg bodyweight, maintenance calories
    val isBulk = dietGoal.contains("Gain", ignoreCase = true) || dietGoal.contains("Bulk", ignoreCase = true)
    val isCut = dietGoal.contains("Loss", ignoreCase = true) || dietGoal.contains("Cut", ignoreCase = true)

    val targetProtein = when {
        isBulk -> (userWeightKg * 2.0).toInt().coerceIn(120, 260)
        isCut -> (userWeightKg * 2.4).toInt().coerceIn(130, 270)
        else -> (userWeightKg * 2.2).toInt().coerceIn(125, 250) // Maintain Aesthetic Body
    }

    val targetCalories = when {
        isBulk -> (tdee + 350.0).toInt().coerceIn(2400, 3600)
        isCut -> (tdee - 450.0).toInt().coerceIn(1800, 2700)
        else -> tdee.toInt().coerceIn(2100, 3000) // Maintenance Aesthetic
    }

    // Allocate remaining calories: Fats (approx 0.9g/kg) and remainder Carbs
    val targetFats = (userWeightKg * 0.9).toInt().coerceIn(50, 95)
    val remainingCaloriesForCarbs = (targetCalories - (targetProtein * 4) - (targetFats * 9)).coerceAtLeast(400)
    val targetCarbs = (remainingCaloriesForCarbs / 4).coerceIn(140, 450)

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
                        .height(170.dp)
                        .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.gym_diet_banner),
                        contentDescription = "Nutrition Meal Prep Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
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
                        Text(
                            text = "METABOLIC NUTRITION & MACROS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GymNeonCyan,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Macro Diet Architecture",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "Structured for hyper-recovery, protein synthesis & lean density",
                            fontSize = 12.sp,
                            color = Color(0xFFB0BDD0)
                        )
                    }
                }
            }

            // Weight & Height Body Section + Goal Selector
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Weight and Height Metrics Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, GymNeonCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.FitnessCenter,
                                        contentDescription = null,
                                        tint = GymNeonCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "BODY ANTHROPOMETRY & METRICS",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = GymNeonCyan,
                                        letterSpacing = 0.8.sp
                                    )
                                }

                                Surface(
                                    onClick = { showProfileBodyDialog = true },
                                    shape = RoundedCornerShape(8.dp),
                                    color = GymNeonCyan.copy(alpha = 0.15f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, GymNeonCyan.copy(alpha = 0.4f)),
                                    modifier = Modifier.testTag("edit_body_metrics_button")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit Weight and Height",
                                            tint = GymNeonCyan,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Edit",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GymNeonCyan
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // 3 Metric Stat Badges: Weight, Height, BMI
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Weight
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("Weight", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(
                                            text = "${String.format(java.util.Locale.US, "%.1f", userWeightKg)} kg",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Black,
                                            color = GymElectricLime
                                        )
                                    }
                                }

                                // Height
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("Height", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(
                                            text = "${String.format(java.util.Locale.US, "%.1f", userHeightCm)} cm",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Black,
                                            color = GymNeonCyan
                                        )
                                    }
                                }

                                // BMI & Physique Index
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("Aesthetic BMI", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(
                                            text = String.format(java.util.Locale.US, "%.1f", bmi),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Black,
                                            color = GymElectricAmber
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Protein recommendation formula indicator
                            val proteinRatio = if (isBulk) "2.0g/kg" else if (isCut) "2.4g/kg (deficit sparing)" else "2.2g/kg (aesthetic recomp)"
                            Text(
                                text = "Daily Target: $targetProtein g protein ($proteinRatio for ${userWeightKg}kg physique)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GymElectricLime
                            )
                        }
                    }

                    // Veg / Non-Veg Segmented Control
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    ) {
                        Row(modifier = Modifier.padding(4.dp)) {
                            // Non-Veg Button
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (!isVegPreference) GymElectricLime else Color.Transparent)
                                    .clickable { onToggleVegPreference(false) }
                                    .padding(vertical = 10.dp)
                                    .testTag("diet_preference_non_veg"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.SetMeal,
                                        contentDescription = "Non-Veg",
                                        tint = if (!isVegPreference) Color.Black else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Non-Vegetarian 🍗",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (!isVegPreference) Color.Black else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            // Veg Button
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isVegPreference) GymElectricLime else Color.Transparent)
                                    .clickable { onToggleVegPreference(true) }
                                    .padding(vertical = 10.dp)
                                    .testTag("diet_preference_veg"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Grass,
                                        contentDescription = "Vegetarian",
                                        tint = if (isVegPreference) Color.Black else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Vegetarian 🌱",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isVegPreference) Color.Black else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    // Goal Selector: Weight Gain (Bulk) | Maintain Aesthetic Body | Weight Loss (Cut)
                    Text(
                        text = "PHYSIQUE & METABOLIC OBJECTIVE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.8.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Triple("Weight Gain (Bulk)", "Weight Gain", "2.0g/kg Pro"),
                            Triple("Maintain Aesthetic Body", "Maintain Aesthetic", "2.2g/kg Pro"),
                            Triple("Weight Loss (Cut)", "Weight Loss", "2.4g/kg Pro")
                        ).forEach { (goalKey, label, subtext) ->
                            val isSelected = dietGoal == goalKey || (dietGoal.contains("Maintain", ignoreCase = true) && goalKey.contains("Maintain"))
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) GymNeonCyan else MaterialTheme.colorScheme.surfaceVariant)
                                    .border(
                                        1.dp,
                                        if (isSelected) Color.White.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onSelectGoal(goalKey) }
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = subtext,
                                        fontSize = 9.sp,
                                        color = if (isSelected) Color.Black.copy(alpha = 0.8f) else GymElectricLime
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Daily Macro Analysis Dashboard Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
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
                            Text(
                                text = "TODAY'S MACRO BREAKDOWN",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = GymElectricLime,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "$totalCalories / $targetCalories kcal",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Calories Progress Bar
                        LinearProgressIndicator(
                            progress = { (totalCalories.toFloat() / targetCalories.toFloat()).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = GymElectricLime,
                            trackColor = MaterialTheme.colorScheme.surface
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 3 Key Macro Meters: Protein, Carbs, Fats + Fiber
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            MacroStatMeter(
                                name = "Protein",
                                currentG = totalProtein.toInt(),
                                targetG = targetProtein,
                                color = GymNeonCyan,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            MacroStatMeter(
                                name = "Carbs",
                                currentG = totalCarbs.toInt(),
                                targetG = targetCarbs,
                                color = GymElectricAmber,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            MacroStatMeter(
                                name = "Fats",
                                currentG = totalFats.toInt(),
                                targetG = targetFats,
                                color = GymPurplePulse,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Dietary Fiber: ${String.format("%.1f", totalFiber)}g logged today",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Meal Items Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MEAL SCHEDULE & ANALYSIS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "${displayedMeals.count { it.isLogged }}/${displayedMeals.size} logged",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GymElectricLime
                    )
                }
            }

            // Detailed Meal Cards
            items(displayedMeals) { meal ->
                MealCardItem(
                    meal = meal,
                    onToggle = { onToggleMeal(meal) },
                    onDelete = { onDeleteMeal(meal.id) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // FAB to add custom meal
        FloatingActionButton(
            onClick = { showAddMealDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 70.dp, end = 20.dp)
                .testTag("add_custom_meal_fab"),
            containerColor = GymNeonCyan,
            contentColor = Color.Black
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Custom Meal")
        }
    }

    if (showAddMealDialog) {
        AddMealDialog(
            isVegDefault = isVegPreference,
            onDismiss = { showAddMealDialog = false },
            onSave = { name, isVeg, cal, pro, carb, fat, fib, type ->
                onAddCustomMeal(name, isVeg, cal, pro, carb, fat, fib, type)
                showAddMealDialog = false
            }
        )
    }

    if (showProfileBodyDialog) {
        BodyMetricsDialog(
            currentWeightKg = userWeightKg,
            currentHeightCm = userHeightCm,
            onDismiss = { showProfileBodyDialog = false },
            onSave = { w, h ->
                onUpdateWeightAndHeight(w, h)
                showProfileBodyDialog = false
            }
        )
    }
}

@Composable
fun MacroStatMeter(
    name: String,
    currentG: Int,
    targetG: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    val progress = (currentG.toFloat() / targetG.toFloat()).coerceIn(0f, 1f)
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${currentG}g",
                fontFamily = FontFamily.Monospace,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "of ${targetG}g",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = color,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
fun MealCardItem(
    meal: MealLog,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                1.dp,
                if (meal.isLogged) GymElectricLime.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (meal.isVeg) Color(0xFF2E7D32).copy(alpha = 0.2f) else Color(0xFFC62828).copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (meal.isVeg) "VEG 🌱" else "NON-VEG 🍗",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (meal.isVeg) Color(0xFF81C784) else Color(0xFFEF9A9A)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = meal.mealType.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.8.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${meal.calories} kcal",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GymElectricAmber
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onToggle,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (meal.isLogged) GymElectricLime else MaterialTheme.colorScheme.surface)
                            .testTag("toggle_meal_logged_${meal.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Log Meal",
                            tint = if (meal.isLogged) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = meal.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (meal.notes.isNotBlank()) {
                Text(
                    text = meal.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Detailed Macro Breakdown Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Protein: ${meal.proteinG.toInt()}g", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GymNeonCyan)
                Text(text = "Carbs: ${meal.carbsG.toInt()}g", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GymElectricAmber)
                Text(text = "Fats: ${meal.fatsG.toInt()}g", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GymPurplePulse)
                Text(text = "Fiber: ${meal.fiberG}g", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMealDialog(
    isVegDefault: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, Boolean, Int, Double, Double, Double, Double, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var isVeg by remember { mutableStateOf(isVegDefault) }
    var mealType by remember { mutableStateOf("Post-Workout Lunch") }
    var caloriesText by remember { mutableStateOf("450") }
    var proteinText by remember { mutableStateOf("35") }
    var carbsText by remember { mutableStateOf("45") }
    var fatsText by remember { mutableStateOf("12") }
    var fiberText by remember { mutableStateOf("5") }

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
                    text = "LOG CUSTOM GYM MEAL",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = GymNeonCyan
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Meal Name") },
                    placeholder = { Text("e.g. Whey Shake + Peanut Butter") },
                    modifier = Modifier.fillMaxWidth().testTag("add_meal_name_input"),
                    singleLine = true
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { isVeg = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isVeg) GymElectricLime else MaterialTheme.colorScheme.surface,
                            contentColor = if (isVeg) Color.Black else MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Vegetarian 🌱", fontSize = 12.sp)
                    }

                    Button(
                        onClick = { isVeg = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isVeg) GymElectricLime else MaterialTheme.colorScheme.surface,
                            contentColor = if (!isVeg) Color.Black else MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Non-Veg 🍗", fontSize = 12.sp)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = caloriesText,
                        onValueChange = { caloriesText = it },
                        label = { Text("Calories") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = proteinText,
                        onValueChange = { proteinText = it },
                        label = { Text("Protein (g)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = carbsText,
                        onValueChange = { carbsText = it },
                        label = { Text("Carbs (g)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = fatsText,
                        onValueChange = { fatsText = it },
                        label = { Text("Fats (g)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onSave(
                                    name,
                                    isVeg,
                                    caloriesText.toIntOrNull() ?: 400,
                                    proteinText.toDoubleOrNull() ?: 30.0,
                                    carbsText.toDoubleOrNull() ?: 40.0,
                                    fatsText.toDoubleOrNull() ?: 10.0,
                                    fiberText.toDoubleOrNull() ?: 4.0,
                                    mealType
                                )
                            }
                        },
                        modifier = Modifier.weight(1f).testTag("save_custom_meal_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = GymNeonCyan, contentColor = Color.Black)
                    ) {
                        Text("Save Meal", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodyMetricsDialog(
    currentWeightKg: Double,
    currentHeightCm: Double,
    onDismiss: () -> Unit,
    onSave: (Double, Double) -> Unit
) {
    var weightText by remember { mutableStateOf(currentWeightKg.toString()) }
    var heightText by remember { mutableStateOf(currentHeightCm.toString()) }

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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Scale,
                        contentDescription = null,
                        tint = GymElectricLime,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "UPDATE BODY METRICS",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = GymElectricLime
                    )
                }

                Text(
                    text = "Enter your current weight and height to calibrate your daily protein intake and aesthetic target calories.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it },
                    label = { Text("Weight (kg)") },
                    placeholder = { Text("e.g. 78.5") },
                    leadingIcon = {
                        Icon(Icons.Default.MonitorWeight, contentDescription = null, tint = GymNeonCyan)
                    },
                    modifier = Modifier.fillMaxWidth().testTag("weight_input_field"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = heightText,
                    onValueChange = { heightText = it },
                    label = { Text("Height (cm)") },
                    placeholder = { Text("e.g. 178") },
                    leadingIcon = {
                        Icon(Icons.Default.Height, contentDescription = null, tint = GymNeonCyan)
                    },
                    modifier = Modifier.fillMaxWidth().testTag("height_input_field"),
                    singleLine = true
                )

                val parsedWeight = weightText.toDoubleOrNull()
                val parsedHeight = heightText.toDoubleOrNull()
                if (parsedWeight != null && parsedHeight != null && parsedHeight > 0) {
                    val hM = parsedHeight / 100.0
                    val calcBmi = parsedWeight / (hM * hM)
                    Text(
                        text = "Calculated BMI: ${String.format(java.util.Locale.US, "%.1f", calcBmi)} kg/m²",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GymElectricAmber
                    )
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
                            val w = weightText.toDoubleOrNull() ?: currentWeightKg
                            val h = heightText.toDoubleOrNull() ?: currentHeightCm
                            onSave(w, h)
                        },
                        modifier = Modifier.weight(1f).testTag("save_body_metrics_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = GymElectricLime, contentColor = Color.Black)
                    ) {
                        Text("Save & Calculate", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
