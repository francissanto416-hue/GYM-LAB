package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BodyCompositionLog
import com.example.data.model.HydrationLog
import com.example.data.model.MealLog
import com.example.data.model.SocialPost
import com.example.data.model.WorkoutRoutine
import com.example.data.model.WorkoutSession
import com.example.data.model.WorkoutSetLog
import kotlinx.coroutines.flow.Flow

@Dao
interface GymDao {

    // --- Workout Routines ---
    @Query("SELECT * FROM workout_routines ORDER BY id ASC")
    fun getAllRoutines(): Flow<List<WorkoutRoutine>>

    @Query("SELECT * FROM workout_routines WHERE id = :id")
    suspend fun getRoutineById(id: Long): WorkoutRoutine?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: WorkoutRoutine): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutines(routines: List<WorkoutRoutine>)

    @Query("DELETE FROM workout_routines WHERE id = :id")
    suspend fun deleteRoutine(id: Long)

    // --- Workout Sessions ---
    @Query("SELECT * FROM workout_sessions ORDER BY startTimestamp DESC")
    fun getAllSessions(): Flow<List<WorkoutSession>>

    @Query("SELECT * FROM workout_sessions ORDER BY startTimestamp DESC LIMIT :limit")
    fun getRecentSessions(limit: Int): Flow<List<WorkoutSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: WorkoutSession): Long

    @Query("UPDATE workout_sessions SET isSynced = 1, syncTimestamp = :timestamp WHERE isSynced = 0")
    suspend fun markAllSessionsSynced(timestamp: Long)

    @Query("SELECT COUNT(*) FROM workout_sessions WHERE isSynced = 0")
    fun getPendingSyncCount(): Flow<Int>

    // --- Set Logs ---
    @Query("SELECT * FROM workout_set_logs WHERE sessionId = :sessionId ORDER BY id ASC")
    fun getSetsForSession(sessionId: Long): Flow<List<WorkoutSetLog>>

    @Query("SELECT * FROM workout_set_logs ORDER BY timestamp DESC")
    fun getAllSets(): Flow<List<WorkoutSetLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetLog(log: WorkoutSetLog): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetLogs(logs: List<WorkoutSetLog>)

    // --- Body Composition Tracking ---
    @Query("SELECT * FROM body_composition_logs ORDER BY timestamp ASC")
    fun getAllBodyLogs(): Flow<List<BodyCompositionLog>>

    @Query("SELECT * FROM body_composition_logs ORDER BY timestamp DESC LIMIT 1")
    fun getLatestBodyLog(): Flow<BodyCompositionLog?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBodyLog(log: BodyCompositionLog): Long

    // --- Meal Logs ---
    @Query("SELECT * FROM meal_logs WHERE dateString = :dateString ORDER BY id ASC")
    fun getMealsForDate(dateString: String): Flow<List<MealLog>>

    @Query("SELECT * FROM meal_logs ORDER BY id ASC")
    fun getAllMeals(): Flow<List<MealLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealLog): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeals(meals: List<MealLog>)

    @Update
    suspend fun updateMeal(meal: MealLog)

    @Query("DELETE FROM meal_logs WHERE id = :id")
    suspend fun deleteMeal(id: Long)

    // --- Hydration Logs ---
    @Query("SELECT * FROM hydration_logs WHERE dateString = :dateString")
    fun getHydrationLogsForDate(dateString: String): Flow<List<HydrationLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHydration(log: HydrationLog): Long

    @Query("DELETE FROM hydration_logs WHERE dateString = :dateString")
    suspend fun clearHydrationForDate(dateString: String)

    // --- Social Community Posts ---
    @Query("SELECT * FROM social_posts ORDER BY timestamp DESC")
    fun getAllSocialPosts(): Flow<List<SocialPost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSocialPost(post: SocialPost): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSocialPosts(posts: List<SocialPost>)

    @Update
    suspend fun updateSocialPost(post: SocialPost)
}
