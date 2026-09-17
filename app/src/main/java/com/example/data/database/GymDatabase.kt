package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.GymDao
import com.example.data.model.BodyCompositionLog
import com.example.data.model.HydrationLog
import com.example.data.model.MealLog
import com.example.data.model.SocialPost
import com.example.data.model.WorkoutRoutine
import com.example.data.model.WorkoutSession
import com.example.data.model.WorkoutSetLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        WorkoutRoutine::class,
        WorkoutSession::class,
        WorkoutSetLog::class,
        BodyCompositionLog::class,
        MealLog::class,
        HydrationLog::class,
        SocialPost::class
    ],
    version = 1,
    exportSchema = false
)
abstract class GymDatabase : RoomDatabase() {

    abstract fun gymDao(): GymDao

    companion object {
        @Volatile
        private var INSTANCE: GymDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): GymDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GymDatabase::class.java,
                    "ironpulse_gym.db"
                )
                    .addCallback(GymDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class GymDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateInitialData(database.gymDao())
                }
            }
        }

        suspend fun populateInitialData(dao: GymDao) {
            // Seed Default Routines
            val routines = listOf(
                WorkoutRoutine(
                    name = "Push Power & Hypertrophy",
                    splitCategory = "Push",
                    description = "Heavy compound presses targeting Chest, Front Delts & Triceps with strict rest intervals.",
                    level = "Intermediate",
                    estimatedMinutes = 55,
                    targetMuscles = "Chest, Shoulders, Triceps",
                    exerciseListJson = "Barbell Bench Press:4:8:80.0:90,Incline Dumbbell Press:3:10:28.0:90,Overhead Barbell Press:3:8:50.0:120,Dumbbell Lateral Raise:4:15:12.0:60,Dips (Chest Focus):3:12:0.0:75,Cable Tricep Pushdown:4:12:35.0:60"
                ),
                WorkoutRoutine(
                    name = "Pull & Back Density",
                    splitCategory = "Pull",
                    description = "Vertical and horizontal pulls for full back width, thickness, and peak bicep recruitment.",
                    level = "Intermediate",
                    estimatedMinutes = 60,
                    targetMuscles = "Lats, Upper Back, Biceps, Rear Delts",
                    exerciseListJson = "Conventional Deadlift:4:6:120.0:180,Barbell Bent-Over Row:4:8:75.0:90,Lat Pulldown:3:10:65.0:75,Chest-Supported T-Bar Row:3:10:45.0:75,Face Pulls:4:15:25.0:60,Barbell Bicep Curl:4:10:35.0:60,Incline Dumbbell Curl:3:12:14.0:60"
                ),
                WorkoutRoutine(
                    name = "Leg Day & Core Engine",
                    splitCategory = "Legs",
                    description = "High-output quad, hamstring, glute, and core driver for maximal systemic stimulus.",
                    level = "Advanced",
                    estimatedMinutes = 65,
                    targetMuscles = "Quads, Hamstrings, Glutes, Calves, Abs",
                    exerciseListJson = "Barbell Back Squat:4:8:100.0:150,Romanian Deadlift:4:10:90.0:90,Leg Press 45°:3:12:180.0:90,Walking Dumbbell Lunges:3:12:22.0:75,Standing Calf Raises:4:15:70.0:60,Hanging Leg Raises:4:15:0.0:60"
                ),
                WorkoutRoutine(
                    name = "Upper Body Explosive",
                    splitCategory = "Upper",
                    description = "Balanced athletic upper body day blending horizontal pressing with row volume.",
                    level = "Intermediate",
                    estimatedMinutes = 50,
                    targetMuscles = "Chest, Upper Back, Delts, Arms",
                    exerciseListJson = "Incline Barbell Bench:4:8:70.0:90,Weighted Pull-Up:4:6:15.0:120,Seated Cable Row:3:10:60.0:75,Dumbbell Arnold Press:3:10:20.0:75,Hammer Curls:3:12:16.0:60,Skull Crushers:3:12:30.0:60"
                ),
                WorkoutRoutine(
                    name = "Full Body Compound Builder",
                    splitCategory = "Full Body",
                    description = "Time-efficient foundation training for total body strength, density, and posture.",
                    level = "Beginner",
                    estimatedMinutes = 45,
                    targetMuscles = "Full Body System",
                    exerciseListJson = "Goblet Squat:3:10:24.0:75,Dumbbell Flat Press:3:10:22.0:75,Chest Supported Row:3:10:40.0:75,Overhead Dumbbell Press:3:10:14.0:60,Plank Hold:3:45:0.0:45"
                )
            )
            dao.insertRoutines(routines)

            // Seed Initial Meal Plans (Both Veg & Non-Veg)
            val today = java.time.LocalDate.now().toString()
            val meals = listOf(
                // Non-Veg Plan
                MealLog(
                    dateString = today,
                    mealType = "Breakfast",
                    name = "Egg White Scramble & Oatmeal",
                    isVeg = false,
                    calories = 480,
                    proteinG = 38.0,
                    carbsG = 56.0,
                    fatsG = 11.0,
                    fiberG = 7.5,
                    isLogged = true,
                    notes = "4 egg whites + 1 whole egg, 70g rolled oats with berries and honey"
                ),
                MealLog(
                    dateString = today,
                    mealType = "Pre-Workout",
                    name = "Rice Cakes with Banana & Whey",
                    isVeg = false,
                    calories = 310,
                    proteinG = 26.0,
                    carbsG = 45.0,
                    fatsG = 3.5,
                    fiberG = 3.0,
                    isLogged = true,
                    notes = "Fast-digesting carbs with isolate protein 45m before lift"
                ),
                MealLog(
                    dateString = today,
                    mealType = "Post-Workout Lunch",
                    name = "Grilled Chicken Breast & Jasmine Rice",
                    isVeg = false,
                    calories = 620,
                    proteinG = 52.0,
                    carbsG = 72.0,
                    fatsG = 12.0,
                    fiberG = 6.0,
                    isLogged = false,
                    notes = "200g chicken breast, 180g steamed rice, steamed broccoli with olive drizzle"
                ),
                MealLog(
                    dateString = today,
                    mealType = "Evening Snack",
                    name = "Greek Yogurt & Crushed Almonds",
                    isVeg = true,
                    calories = 260,
                    proteinG = 22.0,
                    carbsG = 18.0,
                    fatsG = 10.0,
                    fiberG = 3.0,
                    isLogged = false,
                    notes = "Zero fat Greek yogurt with 15g raw almonds"
                ),
                MealLog(
                    dateString = today,
                    mealType = "Dinner",
                    name = "Pan-Seared Salmon & Sweet Potato",
                    isVeg = false,
                    calories = 540,
                    proteinG = 42.0,
                    carbsG = 48.0,
                    fatsG = 18.0,
                    fiberG = 5.5,
                    isLogged = false,
                    notes = "Atlantic salmon rich in Omega-3, roasted sweet potato wedges & asparagus"
                ),

                // Vegetarian Plan Alternatives
                MealLog(
                    dateString = today,
                    mealType = "Breakfast",
                    name = "Spiced Tofu Bhurji & Multigrain Toast",
                    isVeg = true,
                    calories = 430,
                    proteinG = 32.0,
                    carbsG = 48.0,
                    fatsG = 13.0,
                    fiberG = 8.0,
                    isLogged = false,
                    notes = "180g firm tofu sauteed with turmeric, bell peppers, on 2 slices rye toast"
                ),
                MealLog(
                    dateString = today,
                    mealType = "Post-Workout Lunch",
                    name = "Paneer Tikka & Quinoa Protein Bowl",
                    isVeg = true,
                    calories = 610,
                    proteinG = 44.0,
                    carbsG = 65.0,
                    fatsG = 19.0,
                    fiberG = 9.0,
                    isLogged = false,
                    notes = "150g low-fat cottage cheese/paneer, 150g cooked quinoa, chickpeas & greens"
                ),
                MealLog(
                    dateString = today,
                    mealType = "Dinner",
                    name = "Soya Chunks Curry & Brown Rice",
                    isVeg = true,
                    calories = 510,
                    proteinG = 46.0,
                    carbsG = 58.0,
                    fatsG = 9.0,
                    fiberG = 10.5,
                    isLogged = false,
                    notes = "60g dry soya chunks (52% protein density) in tomato herb curry with 150g brown rice"
                )
            )
            dao.insertMeals(meals)

            // Seed Historical Body Composition Progression
            val oneDayMs = 86_400_000L
            val now = System.currentTimeMillis()
            val bodyLogs = listOf(
                BodyCompositionLog(
                    timestamp = now - (oneDayMs * 28),
                    weightKg = 77.2,
                    bodyFatPct = 17.5,
                    muscleMassKg = 34.8,
                    chestCm = 101.0,
                    armCm = 36.5,
                    waistCm = 83.0,
                    notes = "Cycle launch baseline"
                ),
                BodyCompositionLog(
                    timestamp = now - (oneDayMs * 21),
                    weightKg = 77.8,
                    bodyFatPct = 17.1,
                    muscleMassKg = 35.2,
                    chestCm = 101.5,
                    armCm = 36.8,
                    waistCm = 82.5,
                    notes = "Volume ramp up"
                ),
                BodyCompositionLog(
                    timestamp = now - (oneDayMs * 14),
                    weightKg = 78.4,
                    bodyFatPct = 16.6,
                    muscleMassKg = 35.8,
                    chestCm = 102.2,
                    armCm = 37.2,
                    waistCm = 82.0,
                    notes = "Strength gains evident on Bench"
                ),
                BodyCompositionLog(
                    timestamp = now - (oneDayMs * 7),
                    weightKg = 78.9,
                    bodyFatPct = 16.2,
                    muscleMassKg = 36.4,
                    chestCm = 103.0,
                    armCm = 37.6,
                    waistCm = 81.5,
                    notes = "Hypertrophy peak week"
                ),
                BodyCompositionLog(
                    timestamp = now - (oneDayMs * 1),
                    weightKg = 79.4,
                    bodyFatPct = 15.8,
                    muscleMassKg = 37.0,
                    chestCm = 103.8,
                    armCm = 38.1,
                    waistCm = 81.0,
                    notes = "New lean muscle PR (+2.2 kg lean mass gained)"
                )
            )
            bodyLogs.forEach { dao.insertBodyLog(it) }

            // Seed Initial Workout Sessions for trends & recovery tracking
            val session1 = WorkoutSession(
                routineId = 1,
                routineTitle = "Push Power & Hypertrophy",
                startTimestamp = now - (oneDayMs * 3) - 3600000L,
                endTimestamp = now - (oneDayMs * 3),
                durationSeconds = 3420,
                totalVolumeKg = 8420.0,
                totalSets = 18,
                totalReps = 168,
                notes = "Crushed bench press 80kg x 8 smoothly!",
                isCompleted = true,
                isSynced = true
            )
            val session2 = WorkoutSession(
                routineId = 2,
                routineTitle = "Pull & Back Density",
                startTimestamp = now - (oneDayMs * 2) - 3700000L,
                endTimestamp = now - (oneDayMs * 2),
                durationSeconds = 3600,
                totalVolumeKg = 9850.0,
                totalSets = 19,
                totalReps = 155,
                notes = "Deadlift feeling crisp. High lat pump.",
                isCompleted = true,
                isSynced = true
            )
            val session3 = WorkoutSession(
                routineId = 3,
                routineTitle = "Leg Day & Core Engine",
                startTimestamp = now - (oneDayMs * 1) - 4000000L,
                endTimestamp = now - (oneDayMs * 1),
                durationSeconds = 3900,
                totalVolumeKg = 11400.0,
                totalSets = 20,
                totalReps = 180,
                notes = "Heavy squats 100kg x 8 for 4 clean sets.",
                isCompleted = true,
                isSynced = true
            )
            dao.insertSession(session1)
            dao.insertSession(session2)
            dao.insertSession(session3)

            // Seed Community Social Posts for Accountability & Motivation
            val posts = listOf(
                SocialPost(
                    authorName = "Marcus Vance",
                    authorAvatarTag = "MV",
                    timestamp = now - 7200000L,
                    workoutTitle = "Leg Day & Core Engine",
                    volumeKg = 12450.0,
                    durationMinutes = 62,
                    setsCount = 20,
                    highlightPr = "Squat 120kg x 6 Reps (New PR!)",
                    likesCount = 14,
                    isLikedByUser = false,
                    caption = "Quads are completely torched. Automated 90s rest timer kept intensity on point today! 🔥"
                ),
                SocialPost(
                    authorName = "Elena Rostova",
                    authorAvatarTag = "ER",
                    timestamp = now - 18000000L,
                    workoutTitle = "Push Power & Hypertrophy",
                    volumeKg = 8900.0,
                    durationMinutes = 54,
                    setsCount = 18,
                    highlightPr = "Incline DB Press 30kg x 10",
                    likesCount = 21,
                    isLikedByUser = true,
                    caption = "Hit macro targets today (150g protein) and dialed in rest intervals. Consistency pays off!"
                ),
                SocialPost(
                    authorName = "Devon Chen",
                    authorAvatarTag = "DC",
                    timestamp = now - 43200000L,
                    workoutTitle = "Pull & Back Density",
                    volumeKg = 10800.0,
                    durationMinutes = 58,
                    setsCount = 19,
                    highlightPr = "Conventional Deadlift 160kg x 5",
                    likesCount = 33,
                    isLikedByUser = false,
                    caption = "Hands-free voice coach was clutch while strapping into the bar. Heavy iron only! 💥"
                )
            )
            dao.insertSocialPosts(posts)

            // Seed Today Hydration
            dao.insertHydration(HydrationLog(dateString = today, amountMl = 750))
            dao.insertHydration(HydrationLog(dateString = today, amountMl = 500))
            dao.insertHydration(HydrationLog(dateString = today, amountMl = 500))
        }
    }
}
