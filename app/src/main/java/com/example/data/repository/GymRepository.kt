package com.example.data.repository

import com.example.data.dao.GymDao
import com.example.data.model.BodyCompositionLog
import com.example.data.model.HydrationLog
import com.example.data.model.MealLog
import com.example.data.model.SocialPost
import com.example.data.model.WorkoutRoutine
import com.example.data.model.WorkoutSession
import com.example.data.model.WorkoutSetLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GymRepository(private val gymDao: GymDao) {

    val allRoutines: Flow<List<WorkoutRoutine>> = gymDao.getAllRoutines()
    val allSessions: Flow<List<WorkoutSession>> = gymDao.getAllSessions()
    val recentSessions: Flow<List<WorkoutSession>> = gymDao.getRecentSessions(10)
    val allBodyLogs: Flow<List<BodyCompositionLog>> = gymDao.getAllBodyLogs()
    val latestBodyLog: Flow<BodyCompositionLog?> = gymDao.getLatestBodyLog()
    val allSocialPosts: Flow<List<SocialPost>> = gymDao.getAllSocialPosts()
    val pendingSyncCount: Flow<Int> = gymDao.getPendingSyncCount()

    fun getMealsForDate(dateString: String): Flow<List<MealLog>> = gymDao.getMealsForDate(dateString)

    fun getTodayHydrationTotal(dateString: String): Flow<Int> {
        return gymDao.getHydrationLogsForDate(dateString).map { logs ->
            logs.sumOf { it.amountMl }
        }
    }

    suspend fun insertRoutine(routine: WorkoutRoutine): Long = gymDao.insertRoutine(routine)

    suspend fun deleteRoutine(id: Long) = gymDao.deleteRoutine(id)

    suspend fun saveWorkoutSession(
        session: WorkoutSession,
        setLogs: List<WorkoutSetLog>
    ): Long {
        val sessionId = gymDao.insertSession(session)
        val logsWithId = setLogs.map { it.copy(sessionId = sessionId) }
        gymDao.insertSetLogs(logsWithId)
        return sessionId
    }

    suspend fun insertBodyLog(log: BodyCompositionLog): Long = gymDao.insertBodyLog(log)

    suspend fun insertMeal(meal: MealLog): Long = gymDao.insertMeal(meal)

    suspend fun toggleMealLogged(meal: MealLog) {
        gymDao.updateMeal(meal.copy(isLogged = !meal.isLogged))
    }

    suspend fun deleteMeal(id: Long) = gymDao.deleteMeal(id)

    suspend fun logHydration(dateString: String, amountMl: Int) {
        gymDao.insertHydration(HydrationLog(dateString = dateString, amountMl = amountMl))
    }

    suspend fun resetHydration(dateString: String) {
        gymDao.clearHydrationForDate(dateString)
    }

    suspend fun togglePostLike(post: SocialPost) {
        val updatedLikes = if (post.isLikedByUser) maxOf(0, post.likesCount - 1) else post.likesCount + 1
        gymDao.updateSocialPost(post.copy(isLikedByUser = !post.isLikedByUser, likesCount = updatedLikes))
    }

    suspend fun createSocialPost(post: SocialPost): Long = gymDao.insertSocialPost(post)

    suspend fun performCloudSync(): Boolean {
        // Offline-to-Cloud Sync simulation: verifies connectivity, flushes pending logs, marks synced
        gymDao.markAllSessionsSynced(System.currentTimeMillis())
        return true
    }
}
