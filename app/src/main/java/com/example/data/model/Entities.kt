package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Workout Routine entity defining splits, target muscle groups, and exercise lists.
 */
@Entity(tableName = "workout_routines")
data class WorkoutRoutine(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val splitCategory: String, // "Push", "Pull", "Legs", "Upper", "Lower", "Full Body", "Custom"
    val description: String,
    val level: String, // "Beginner", "Intermediate", "Advanced"
    val estimatedMinutes: Int,
    val targetMuscles: String,
    val isCustom: Boolean = false,
    val exerciseListJson: String // Comma or structured format of exercises
)

/**
 * Exercise model used in routines and active training.
 */
data class WorkoutExercise(
    val id: String,
    val name: String,
    val targetMuscle: String,
    val equipment: String,
    val targetSets: Int = 4,
    val targetReps: Int = 10,
    val defaultWeightKg: Double = 60.0,
    val defaultRestSec: Int = 90,
    val tips: String = ""
)

/**
 * A completed or logged workout session.
 */
@Entity(tableName = "workout_sessions")
data class WorkoutSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val routineId: Long,
    val routineTitle: String,
    val startTimestamp: Long,
    val endTimestamp: Long,
    val durationSeconds: Int,
    val totalVolumeKg: Double,
    val totalSets: Int,
    val totalReps: Int,
    val notes: String = "",
    val isCompleted: Boolean = true,
    val isSynced: Boolean = true,
    val syncTimestamp: Long = System.currentTimeMillis()
)

/**
 * Individual set logged for an exercise during a session.
 */
@Entity(tableName = "workout_set_logs")
data class WorkoutSetLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val exerciseName: String,
    val setNumber: Int,
    val weightKg: Double,
    val reps: Int,
    val rpe: Int = 8, // Rate of Perceived Exertion (1-10)
    val isWarmup: Boolean = false,
    val isCompleted: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Body composition record for tracking weight, body fat %, muscle mass, and tape measurements.
 */
@Entity(tableName = "body_composition_logs")
data class BodyCompositionLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val weightKg: Double,
    val bodyFatPct: Double,
    val muscleMassKg: Double,
    val chestCm: Double,
    val armCm: Double,
    val waistCm: Double,
    val notes: String = ""
)

/**
 * Nutrition & Diet meal plan and logged meals.
 */
@Entity(tableName = "meal_logs")
data class MealLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateString: String, // YYYY-MM-DD
    val mealType: String, // "Breakfast", "Pre-Workout", "Post-Workout Lunch", "Evening Snack", "Dinner"
    val name: String,
    val isVeg: Boolean,
    val calories: Int,
    val proteinG: Double,
    val carbsG: Double,
    val fatsG: Double,
    val fiberG: Double,
    val isLogged: Boolean = true,
    val notes: String = ""
)

/**
 * Hydration log record.
 */
@Entity(tableName = "hydration_logs")
data class HydrationLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateString: String,
    val amountMl: Int,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Social Accountability Post for community sharing.
 */
@Entity(tableName = "social_posts")
data class SocialPost(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val authorName: String,
    val authorAvatarTag: String,
    val timestamp: Long,
    val workoutTitle: String,
    val volumeKg: Double,
    val durationMinutes: Int,
    val setsCount: Int,
    val highlightPr: String,
    val likesCount: Int = 0,
    val isLikedByUser: Boolean = false,
    val caption: String = ""
)
