package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.GymDatabase
import com.example.data.drive.DriveSyncResult
import com.example.data.drive.GoogleDriveStorageManager
import com.example.data.drive.GoogleDriveUser
import com.example.data.model.BodyCompositionLog
import com.example.data.model.MealLog
import com.example.data.model.SocialPost
import com.example.data.model.WorkoutExercise
import com.example.data.model.WorkoutRoutine
import com.example.data.model.WorkoutSession
import com.example.data.model.WorkoutSetLog
import com.example.data.repository.GymRepository
import com.example.media.AlarmHelper
import com.example.media.VoiceAction
import com.example.media.VoiceCoach
import com.example.media.WorkoutMusicPlayer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class GymTab(val title: String) {
    ROUTINES("Workouts"),
    ACTIVE_WORKOUT("Active"),
    DIET("Nutrition"),
    DASHBOARD("Progress"),
    MUSIC("Music"),
    SOCIAL("Community")
}

data class CurrentSetDraft(
    val setNumber: Int,
    val weightKg: Double,
    val reps: Int,
    val rpe: Int = 8,
    val isCompleted: Boolean = false
)

data class ShareWorkoutSummary(
    val routineTitle: String,
    val totalVolumeKg: Double,
    val totalSets: Int,
    val totalReps: Int,
    val durationMinutes: Int,
    val bestSetPr: String,
    val dateString: String
)

class GymViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GymRepository
    val alarmHelper: AlarmHelper = AlarmHelper(application)
    val voiceCoach: VoiceCoach = VoiceCoach(application)
    val musicPlayer: WorkoutMusicPlayer = WorkoutMusicPlayer(viewModelScope)
    val driveStorageManager: GoogleDriveStorageManager = GoogleDriveStorageManager(application)

    init {
        val database = GymDatabase.getDatabase(application, viewModelScope)
        repository = GymRepository(database.gymDao())
    }

    // --- App Global States ---
    private val _selectedTab = MutableStateFlow(GymTab.ROUTINES)
    val selectedTab: StateFlow<GymTab> = _selectedTab.asStateFlow()
    val currentTab: StateFlow<GymTab> = selectedTab

    private val _isNightGymMode = MutableStateFlow(true)
    val isNightGymMode: StateFlow<Boolean> = _isNightGymMode.asStateFlow()
    val isDarkMode: StateFlow<Boolean> = isNightGymMode

    // --- Flows from Repository ---
    val routines: StateFlow<List<WorkoutRoutine>> = repository.allRoutines
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sessions: StateFlow<List<WorkoutSession>> = repository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bodyLogs: StateFlow<List<BodyCompositionLog>> = repository.allBodyLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val bodyCompositionLogs: StateFlow<List<BodyCompositionLog>> = bodyLogs

    val latestBodyLog: StateFlow<BodyCompositionLog?> = repository.latestBodyLog
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val todayDateStr = LocalDate.now().toString()
    val todayMeals: StateFlow<List<MealLog>> = repository.getMealsForDate(todayDateStr)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val meals: StateFlow<List<MealLog>> = todayMeals

    val todayHydration: StateFlow<Int> = repository.getTodayHydrationTotal(todayDateStr)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1750)
    val waterIntakeMl: StateFlow<Int> = todayHydration
    val targetWaterMl: StateFlow<Int> = MutableStateFlow(3500).asStateFlow()

    val socialPosts: StateFlow<List<SocialPost>> = repository.allSocialPosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingSyncCount: StateFlow<Int> = repository.pendingSyncCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()
    val syncStatusMessage: StateFlow<String?> = syncMessage

    // --- Active Workout State ---
    private val _isWorkoutActive = MutableStateFlow(false)
    val isWorkoutActive: StateFlow<Boolean> = _isWorkoutActive.asStateFlow()

    private val _activeRoutine = MutableStateFlow<WorkoutRoutine?>(null)
    val activeRoutine: StateFlow<WorkoutRoutine?> = _activeRoutine.asStateFlow()

    private val _activeExercises = MutableStateFlow<List<WorkoutExercise>>(emptyList())
    val activeExercises: StateFlow<List<WorkoutExercise>> = _activeExercises.asStateFlow()

    private val _currentExerciseIndex = MutableStateFlow(0)
    val currentExerciseIndex: StateFlow<Int> = _currentExerciseIndex.asStateFlow()

    private val _workoutElapsedSec = MutableStateFlow(0)
    val workoutElapsedSec: StateFlow<Int> = _workoutElapsedSec.asStateFlow()

    private val _exerciseSetsMap = MutableStateFlow<Map<String, List<CurrentSetDraft>>>(emptyMap())
    val exerciseSetsMap: StateFlow<Map<String, List<CurrentSetDraft>>> = _exerciseSetsMap.asStateFlow()

    private var workoutTimerJob: Job? = null

    // --- Rest Timer State ---
    private val _isRestTimerRunning = MutableStateFlow(false)
    val isRestTimerRunning: StateFlow<Boolean> = _isRestTimerRunning.asStateFlow()

    private val _restTotalSec = MutableStateFlow(90)
    val restTotalSec: StateFlow<Int> = _restTotalSec.asStateFlow()

    private val _restRemainingSec = MutableStateFlow(90)
    val restRemainingSec: StateFlow<Int> = _restRemainingSec.asStateFlow()

    private val _isRestAlarmEnabled = MutableStateFlow(true)
    val isRestAlarmEnabled: StateFlow<Boolean> = _isRestAlarmEnabled.asStateFlow()

    private val _isRestVibrateEnabled = MutableStateFlow(true)
    val isRestVibrateEnabled: StateFlow<Boolean> = _isRestVibrateEnabled.asStateFlow()

    private val _isVoiceAssistantEnabled = MutableStateFlow(true)
    val isVoiceAssistantEnabled: StateFlow<Boolean> = _isVoiceAssistantEnabled.asStateFlow()

    private var restTimerJob: Job? = null

    // --- Diet Plan & Aesthetic Body / Macro Goals ---
    private val _dietIsVeg = MutableStateFlow(false)
    val dietIsVeg: StateFlow<Boolean> = _dietIsVeg.asStateFlow()
    val isVegDiet: StateFlow<Boolean> = dietIsVeg

    private val _dietGoal = MutableStateFlow("Maintain Aesthetic Body")
    val dietGoal: StateFlow<String> = _dietGoal.asStateFlow()

    // Weight & Height profile for exact metabolic & daily protein calculations
    private val _userWeightKg = MutableStateFlow(78.0)
    val userWeightKg: StateFlow<Double> = _userWeightKg.asStateFlow()

    private val _userHeightCm = MutableStateFlow(178.0)
    val userHeightCm: StateFlow<Double> = _userHeightCm.asStateFlow()

    fun updateWeightAndHeight(weightKg: Double, heightCm: Double) {
        _userWeightKg.value = weightKg
        _userHeightCm.value = heightCm
        // Also automatically save to body logs for holistic dashboard synchronization
        viewModelScope.launch {
            val existing = repository.latestBodyLog
            val log = BodyCompositionLog(
                timestamp = System.currentTimeMillis(),
                weightKg = weightKg,
                bodyFatPct = 14.5,
                muscleMassKg = (weightKg * 0.45),
                chestCm = 104.0,
                armCm = 39.0,
                waistCm = 81.0,
                notes = "Updated via Nutrition & Aesthetic Body Profile (Height: ${heightCm}cm)"
            )
            repository.insertBodyLog(log)
        }
    }

    // --- Social Sharing Summary Dialog ---
    private val _shareSummary = MutableStateFlow<ShareWorkoutSummary?>(null)
    val shareSummary: StateFlow<ShareWorkoutSummary?> = _shareSummary.asStateFlow()
    val shareWorkoutSummary: StateFlow<ShareWorkoutSummary?> = shareSummary

    // --- Voice Command Dialog ---
    private val _showVoiceDialog = MutableStateFlow(false)
    val showVoiceDialog: StateFlow<Boolean> = _showVoiceDialog.asStateFlow()

    private val _lastVoiceAction = MutableStateFlow<String?>(null)
    val lastVoiceAction: StateFlow<String?> = _lastVoiceAction.asStateFlow()

    // --- Hydration Dialog & Settings ---
    private val _showHydrationDialog = MutableStateFlow(false)
    val showHydrationDialog: StateFlow<Boolean> = _showHydrationDialog.asStateFlow()

    // --- Google Drive Storage & Web Security Integration ---
    private val _showDriveDialog = MutableStateFlow(false)
    val showDriveDialog: StateFlow<Boolean> = _showDriveDialog.asStateFlow()

    private val _driveUser = MutableStateFlow<GoogleDriveUser?>(driveStorageManager.getStoredUser())
    val driveUser: StateFlow<GoogleDriveUser?> = _driveUser.asStateFlow()

    private val _lastDriveBackupTime = MutableStateFlow(driveStorageManager.getLastBackupTime())
    val lastDriveBackupTime: StateFlow<Long> = _lastDriveBackupTime.asStateFlow()

    private val _isWebSecurityVerified = MutableStateFlow(true)
    val isWebSecurityVerified: StateFlow<Boolean> = _isWebSecurityVerified.asStateFlow()

    fun openDriveDialog() {
        _showDriveDialog.value = true
        verifyDriveWebSecurity()
    }

    fun closeDriveDialog() {
        _showDriveDialog.value = false
    }

    fun signInGoogleDrive(email: String, displayName: String) {
        val user = GoogleDriveUser(email = email, displayName = displayName, accessToken = "oauth_token_${System.currentTimeMillis()}")
        driveStorageManager.saveAuthenticatedUser(user)
        _driveUser.value = user
        voiceCoach.speak("Google account connected for Google Drive storage.")
    }

    fun signOutGoogleDrive() {
        driveStorageManager.clearAuthentication()
        _driveUser.value = null
    }

    fun verifyDriveWebSecurity() {
        viewModelScope.launch {
            val verified = driveStorageManager.verifyDriveWebSecurity()
            _isWebSecurityVerified.value = verified
        }
    }

    fun backupToGoogleDrive() {
        viewModelScope.launch {
            _isSyncing.value = true
            _syncMessage.value = "Encrypting and uploading backup to Google Drive..."
            val json = driveStorageManager.createBackupJson(
                sessions = repository.allSessions.stateIn(viewModelScope).value,
                bodyLogs = repository.allBodyLogs.stateIn(viewModelScope).value,
                meals = todayMeals.value,
                routines = repository.allRoutines.stateIn(viewModelScope).value,
                userWeightKg = _userWeightKg.value,
                userHeightCm = _userHeightCm.value,
                dietGoal = _dietGoal.value
            )
            val token = _driveUser.value?.accessToken ?: ""
            val result = driveStorageManager.uploadBackupToDrive(token, json)
            _isSyncing.value = false
            when (result) {
                is DriveSyncResult.Success -> {
                    _lastDriveBackupTime.value = System.currentTimeMillis()
                    _syncMessage.value = result.message
                    voiceCoach.speak("Gym and nutrition logs backed up to Google Drive.")
                    delay(3500)
                    _syncMessage.value = null
                }
                is DriveSyncResult.Error -> {
                    _syncMessage.value = "Backup notice: ${result.errorMessage}"
                    delay(3500)
                    _syncMessage.value = null
                }
            }
        }
    }

    fun openHydrationDialog() {
        _showHydrationDialog.value = true
    }

    fun closeHydrationDialog() {
        _showHydrationDialog.value = false
    }

    val hydrationTargetMl = 3500
    private val _hydrationIntervalMin = MutableStateFlow(45)
    val hydrationIntervalMin: StateFlow<Int> = _hydrationIntervalMin.asStateFlow()

    fun selectTab(tab: GymTab) {
        _selectedTab.value = tab
    }

    fun toggleNightGymMode() {
        _isNightGymMode.value = !_isNightGymMode.value
    }

    fun toggleDarkMode() {
        toggleNightGymMode()
    }

    fun setDietPreferenceVeg(isVeg: Boolean) {
        _dietIsVeg.value = isVeg
    }

    fun setDietVegPreference(isVeg: Boolean) {
        setDietPreferenceVeg(isVeg)
    }

    fun setDietGoal(goal: String) {
        _dietGoal.value = goal
    }

    fun toggleVoiceAssistant() {
        val next = !_isVoiceAssistantEnabled.value
        _isVoiceAssistantEnabled.value = next
        voiceCoach.isEnabled = next
        if (next) voiceCoach.speak("Voice assistance enabled. I will guide your sets and rest intervals.")
    }

    fun toggleRestAlarm() {
        _isRestAlarmEnabled.value = !_isRestAlarmEnabled.value
    }

    fun toggleRestVibrate() {
        _isRestVibrateEnabled.value = !_isRestVibrateEnabled.value
    }

    // --- Active Workout Control ---
    fun startWorkout(routine: WorkoutRoutine) {
        _activeRoutine.value = routine
        val parsedExercises = parseExerciseList(routine.exerciseListJson)
        _activeExercises.value = parsedExercises
        _currentExerciseIndex.value = 0
        _workoutElapsedSec.value = 0

        // Initialize sets map
        val initialMap = mutableMapOf<String, List<CurrentSetDraft>>()
        for (ex in parsedExercises) {
            val sets = (1..ex.targetSets).map { setNum ->
                CurrentSetDraft(
                    setNumber = setNum,
                    weightKg = ex.defaultWeightKg,
                    reps = ex.targetReps,
                    rpe = 8,
                    isCompleted = false
                )
            }
            initialMap[ex.name] = sets
        }
        _exerciseSetsMap.value = initialMap
        _isWorkoutActive.value = true
        _selectedTab.value = GymTab.ACTIVE_WORKOUT

        workoutTimerJob?.cancel()
        workoutTimerJob = viewModelScope.launch {
            while (isActive && _isWorkoutActive.value) {
                delay(1000)
                _workoutElapsedSec.value += 1
            }
        }

        if (parsedExercises.isNotEmpty()) {
            val first = parsedExercises[0]
            voiceCoach.announceExerciseStart(first.name, first.targetSets, first.targetReps, first.defaultWeightKg)
        }
    }

    fun selectCurrentExerciseIndex(index: Int) {
        if (index in 0 until _activeExercises.value.size) {
            _currentExerciseIndex.value = index
            val ex = _activeExercises.value[index]
            voiceCoach.speak("Current exercise: ${ex.name}")
        }
    }

    fun nextExercise() {
        if (_currentExerciseIndex.value + 1 < _activeExercises.value.size) {
            _currentExerciseIndex.value += 1
            val ex = _activeExercises.value[_currentExerciseIndex.value]
            voiceCoach.speak("Up next: ${ex.name}")
        }
    }

    fun prevExercise() {
        if (_currentExerciseIndex.value > 0) {
            _currentExerciseIndex.value -= 1
        }
    }

    fun updateSet(exerciseName: String, setIndex: Int, weightKg: Double, reps: Int, rpe: Int) {
        val currentMap = _exerciseSetsMap.value.toMutableMap()
        val sets = currentMap[exerciseName]?.toMutableList() ?: return
        if (setIndex in sets.indices) {
            sets[setIndex] = sets[setIndex].copy(
                weightKg = weightKg.coerceAtLeast(0.0),
                reps = reps.coerceAtLeast(1),
                rpe = rpe.coerceIn(1, 10)
            )
            currentMap[exerciseName] = sets
            _exerciseSetsMap.value = currentMap
        }
    }

    fun addSet(exerciseName: String) {
        val currentMap = _exerciseSetsMap.value.toMutableMap()
        val sets = currentMap[exerciseName]?.toMutableList() ?: mutableListOf()
        val lastSet = sets.lastOrNull()
        val nextNumber = sets.size + 1
        sets.add(
            CurrentSetDraft(
                setNumber = nextNumber,
                weightKg = lastSet?.weightKg ?: 60.0,
                reps = lastSet?.reps ?: 10,
                rpe = lastSet?.rpe ?: 8,
                isCompleted = false
            )
        )
        currentMap[exerciseName] = sets
        _exerciseSetsMap.value = currentMap
    }

    fun logAndCompleteSet(exerciseName: String, setIndex: Int) {
        val currentMap = _exerciseSetsMap.value.toMutableMap()
        val sets = currentMap[exerciseName]?.toMutableList() ?: return
        if (setIndex in sets.indices) {
            val set = sets[setIndex]
            val completed = !set.isCompleted
            sets[setIndex] = set.copy(isCompleted = completed)
            currentMap[exerciseName] = sets
            _exerciseSetsMap.value = currentMap

            if (completed) {
                val currentEx = _activeExercises.value.getOrNull(_currentExerciseIndex.value)
                val restSec = currentEx?.defaultRestSec ?: 90
                startRestTimer(restSec)
                voiceCoach.announceSetCompleted(exerciseName, set.setNumber, restSec)
            }
        }
    }

    // --- Automated Rest Timer Controls ---
    fun startRestTimer(seconds: Int) {
        val validSec = seconds.coerceAtLeast(15)
        _restTotalSec.value = validSec
        _restRemainingSec.value = validSec
        _isRestTimerRunning.value = true

        restTimerJob?.cancel()
        restTimerJob = viewModelScope.launch {
            while (isActive && _restRemainingSec.value > 0 && _isRestTimerRunning.value) {
                delay(1000)
                val current = _restRemainingSec.value - 1
                _restRemainingSec.value = current

                if (current == 10) {
                    alarmHelper.playRestWarningChime()
                    voiceCoach.announceRestWarning(10)
                }

                if (current <= 0) {
                    _isRestTimerRunning.value = false
                    if (_isRestAlarmEnabled.value) {
                        alarmHelper.playRestCompleteAlarm(_isRestVibrateEnabled.value)
                    }
                    val nextEx = _activeExercises.value.getOrNull(_currentExerciseIndex.value)?.name ?: "Next Set"
                    voiceCoach.announceRestFinished(nextEx)
                    break
                }
            }
        }
    }

    fun adjustRestTimer(deltaSec: Int) {
        val updated = (_restRemainingSec.value + deltaSec).coerceAtLeast(0)
        _restRemainingSec.value = updated
        _restTotalSec.value = maxOf(_restTotalSec.value, updated)
        voiceCoach.speak("${if (deltaSec > 0) "Added" else "Removed"} ${kotlin.math.abs(deltaSec)} seconds rest")
    }

    fun pauseRestTimer() {
        _isRestTimerRunning.value = false
        restTimerJob?.cancel()
        voiceCoach.speak("Rest timer paused")
    }

    fun resumeRestTimer() {
        if (_restRemainingSec.value > 0) {
            startRestTimer(_restRemainingSec.value)
        }
    }

    fun skipRestTimer() {
        _isRestTimerRunning.value = false
        _restRemainingSec.value = 0
        restTimerJob?.cancel()
        voiceCoach.speak("Rest skipped. Ready for next set.")
    }

    fun finishWorkout(notes: String = "") {
        val routine = _activeRoutine.value ?: return
        val map = _exerciseSetsMap.value
        val allCompletedSets = mutableListOf<WorkoutSetLog>()
        var totalVolume = 0.0
        var totalReps = 0
        var bestSetWeight = 0.0
        var bestSetEx = ""

        map.forEach { (exName, sets) ->
            sets.filter { it.isCompleted }.forEach { set ->
                val vol = set.weightKg * set.reps
                totalVolume += vol
                totalReps += set.reps
                if (set.weightKg > bestSetWeight) {
                    bestSetWeight = set.weightKg
                    bestSetEx = "$exName ${set.weightKg}kg x ${set.reps}"
                }
                allCompletedSets.add(
                    WorkoutSetLog(
                        sessionId = 0,
                        exerciseName = exName,
                        setNumber = set.setNumber,
                        weightKg = set.weightKg,
                        reps = set.reps,
                        rpe = set.rpe,
                        isCompleted = true
                    )
                )
            }
        }

        val durationMinutes = (_workoutElapsedSec.value / 60).coerceAtLeast(1)
        val session = WorkoutSession(
            routineId = routine.id,
            routineTitle = routine.name,
            startTimestamp = System.currentTimeMillis() - (_workoutElapsedSec.value * 1000L),
            endTimestamp = System.currentTimeMillis(),
            durationSeconds = _workoutElapsedSec.value,
            totalVolumeKg = totalVolume,
            totalSets = allCompletedSets.size,
            totalReps = totalReps,
            notes = notes,
            isCompleted = true,
            isSynced = false
        )

        viewModelScope.launch {
            repository.saveWorkoutSession(session, allCompletedSets)
        }

        voiceCoach.announceWorkoutCompleted(totalVolume, allCompletedSets.size, durationMinutes)

        // Show Social Sharing summary card
        _shareSummary.value = ShareWorkoutSummary(
            routineTitle = routine.name,
            totalVolumeKg = totalVolume,
            totalSets = allCompletedSets.size,
            totalReps = totalReps,
            durationMinutes = durationMinutes,
            bestSetPr = if (bestSetEx.isNotEmpty()) bestSetEx else "Crushed workout sets",
            dateString = LocalDate.now().toString()
        )

        cancelWorkout()
        _selectedTab.value = GymTab.DASHBOARD
    }

    fun dismissShareSummary() {
        _shareSummary.value = null
    }

    fun cancelWorkout() {
        _isWorkoutActive.value = false
        workoutTimerJob?.cancel()
        restTimerJob?.cancel()
        _isRestTimerRunning.value = false
    }

    // --- Diet Operations ---
    fun logMeal(
        name: String,
        isVeg: Boolean,
        calories: Int,
        protein: Double,
        carbs: Double,
        fats: Double,
        fiber: Double,
        mealType: String
    ) {
        viewModelScope.launch {
            val meal = MealLog(
                dateString = todayDateStr,
                mealType = mealType,
                name = name,
                isVeg = isVeg,
                calories = calories,
                proteinG = protein,
                carbsG = carbs,
                fatsG = fats,
                fiberG = fiber,
                isLogged = true
            )
            repository.insertMeal(meal)
            voiceCoach.speak("Logged meal: $name. Added ${protein.toInt()} grams of protein.")
        }
    }

    fun toggleMeal(meal: MealLog) {
        viewModelScope.launch {
            repository.toggleMealLogged(meal)
        }
    }

    fun toggleMealLogged(meal: MealLog) {
        toggleMeal(meal)
    }

    fun addCustomMeal(
        name: String,
        isVeg: Boolean,
        calories: Int,
        protein: Double,
        carbs: Double,
        fats: Double,
        fiber: Double,
        mealType: String
    ) {
        logMeal(name, isVeg, calories, protein, carbs, fats, fiber, mealType)
    }

    fun deleteMeal(mealId: Long) {
        viewModelScope.launch {
            repository.deleteMeal(mealId)
        }
    }

    // --- Hydration Operations ---
    fun addHydration(amountMl: Int) {
        viewModelScope.launch {
            repository.logHydration(todayDateStr, amountMl)
            alarmHelper.playHydrationReminderTone()
            val total = _todayHydrationCurrent() + amountMl
            voiceCoach.speak("Logged $amountMl milliliters of water. Total today: $total milliliters.")
        }
    }

    fun logWater(amountMl: Int) {
        addHydration(amountMl)
    }

    fun resetHydration() {
        viewModelScope.launch {
            repository.resetHydration(todayDateStr)
        }
    }

    fun resetWater() {
        resetHydration()
    }

    private fun _todayHydrationCurrent(): Int = todayHydration.value

    fun triggerHydrationAlert() {
        alarmHelper.playHydrationReminderTone()
        voiceCoach.announceHydrationAlert()
    }

    // --- Body Measurement Logging ---
    fun logBodyMeasurement(
        weightKg: Double,
        bodyFatPct: Double,
        muscleMassKg: Double,
        chestCm: Double,
        armCm: Double,
        waistCm: Double,
        notes: String
    ) {
        viewModelScope.launch {
            val log = BodyCompositionLog(
                timestamp = System.currentTimeMillis(),
                weightKg = weightKg,
                bodyFatPct = bodyFatPct,
                muscleMassKg = muscleMassKg,
                chestCm = chestCm,
                armCm = armCm,
                waistCm = waistCm,
                notes = notes
            )
            repository.insertBodyLog(log)
            voiceCoach.speak("Logged body composition. Weight: $weightKg kilograms, Muscle mass: $muscleMassKg kilograms.")
        }
    }

    fun recordBodyComposition(
        weightKg: Double,
        bodyFatPct: Double,
        muscleMassKg: Double,
        chestCm: Double,
        armCm: Double,
        waistCm: Double,
        notes: String
    ) {
        logBodyMeasurement(weightKg, bodyFatPct, muscleMassKg, chestCm, armCm, waistCm, notes)
    }

    // --- Social Community Feed ---
    fun togglePostLike(post: SocialPost) {
        viewModelScope.launch {
            repository.togglePostLike(post)
        }
    }

    fun likeSocialPost(post: SocialPost) {
        togglePostLike(post)
    }

    fun createSocialPost(
        author: String,
        authorId: String,
        caption: String,
        volume: Double,
        sets: Int,
        pr: String
    ) {
        viewModelScope.launch {
            val post = SocialPost(
                authorName = author,
                authorAvatarTag = authorId,
                timestamp = System.currentTimeMillis(),
                workoutTitle = "Custom Gym Session",
                volumeKg = volume,
                durationMinutes = 45,
                setsCount = sets,
                highlightPr = pr,
                likesCount = 0,
                isLikedByUser = false,
                caption = caption
            )
            repository.createSocialPost(post)
        }
    }

    fun publishWorkoutToCommunity(caption: String) {
        val summary = _shareSummary.value ?: return
        viewModelScope.launch {
            val post = SocialPost(
                authorName = "You (Athlete)",
                authorAvatarTag = "ME",
                timestamp = System.currentTimeMillis(),
                workoutTitle = summary.routineTitle,
                volumeKg = summary.totalVolumeKg,
                durationMinutes = summary.durationMinutes,
                setsCount = summary.totalSets,
                highlightPr = summary.bestSetPr,
                likesCount = 1,
                isLikedByUser = true,
                caption = caption.ifEmpty { "Pushed hard today! Automated rest timers kept the pump alive. #IronPulse" }
            )
            repository.createSocialPost(post)
            _shareSummary.value = null
            _selectedTab.value = GymTab.SOCIAL
            voiceCoach.speak("Workout shared with your community accountability feed!")
        }
    }

    // --- Custom Routine Builder ---
    fun createCustomRoutine(
        name: String,
        splitCategory: String,
        description: String,
        level: String,
        targetMuscles: String,
        exercises: List<WorkoutExercise>
    ) {
        val jsonStr = exercises.joinToString(",") { ex ->
            "${ex.name}:${ex.targetSets}:${ex.targetReps}:${ex.defaultWeightKg}:${ex.defaultRestSec}"
        }
        viewModelScope.launch {
            val routine = WorkoutRoutine(
                name = name,
                splitCategory = splitCategory,
                description = description,
                level = level,
                estimatedMinutes = exercises.size * 10,
                targetMuscles = targetMuscles,
                isCustom = true,
                exerciseListJson = jsonStr
            )
            repository.insertRoutine(routine)
            voiceCoach.speak("Custom routine $name created and saved locally.")
        }
    }

    // --- Offline Cloud Sync ---
    fun syncOfflineData() {
        viewModelScope.launch {
            _isSyncing.value = true
            _syncMessage.value = "Connecting to sync service..."
            delay(1200)
            val success = repository.performCloudSync()
            _isSyncing.value = false
            if (success) {
                _syncMessage.value = "All workouts & sets synced successfully!"
                delay(3000)
                _syncMessage.value = null
            }
        }
    }

    // --- Voice Command Execution ---
    fun openVoiceDialog() {
        _showVoiceDialog.value = true
    }

    fun closeVoiceDialog() {
        _showVoiceDialog.value = false
    }

    fun handleVoiceCommandText(text: String) {
        _lastVoiceAction.value = text
        val action = voiceCoach.parseVoiceCommand(text)
        if (action != null) {
            when (action) {
                VoiceAction.LOG_CURRENT_SET -> {
                    val ex = _activeExercises.value.getOrNull(_currentExerciseIndex.value)
                    if (ex != null) {
                        val sets = _exerciseSetsMap.value[ex.name]
                        val firstIncomplete = sets?.indexOfFirst { !it.isCompleted } ?: -1
                        if (firstIncomplete != -1) {
                            logAndCompleteSet(ex.name, firstIncomplete)
                        } else {
                            voiceCoach.speak("All sets already logged for ${ex.name}")
                        }
                    }
                }
                VoiceAction.ADD_REST_30 -> adjustRestTimer(30)
                VoiceAction.ADD_REST_15 -> adjustRestTimer(15)
                VoiceAction.SKIP_REST -> skipRestTimer()
                VoiceAction.PAUSE_TIMER -> pauseRestTimer()
                VoiceAction.RESUME_TIMER -> resumeRestTimer()
                VoiceAction.PLAY_MUSIC -> musicPlayer.play()
                VoiceAction.PAUSE_MUSIC -> musicPlayer.pause()
                VoiceAction.NEXT_TRACK -> musicPlayer.nextTrack()
                VoiceAction.LOG_HYDRATION -> addHydration(250)
            }
        } else {
            voiceCoach.speak("Recognized: $text. Try saying 'Log set', 'Add 30 seconds', or 'Play music'.")
        }
    }

    fun handleVoiceCommand(text: String) {
        handleVoiceCommandText(text)
    }

    private fun parseExerciseList(jsonStr: String): List<WorkoutExercise> {
        if (jsonStr.isBlank()) return emptyList()
        return jsonStr.split(",").mapNotNull { part ->
            val tokens = part.split(":")
            if (tokens.isNotEmpty()) {
                val name = tokens[0].trim()
                val sets = tokens.getOrNull(1)?.toIntOrNull() ?: 4
                val reps = tokens.getOrNull(2)?.toIntOrNull() ?: 10
                val weight = tokens.getOrNull(3)?.toDoubleOrNull() ?: 60.0
                val rest = tokens.getOrNull(4)?.toIntOrNull() ?: 90
                WorkoutExercise(
                    id = name.lowercase().replace(" ", "_"),
                    name = name,
                    targetMuscle = "Target",
                    equipment = "Barbell / Machine",
                    targetSets = sets,
                    targetReps = reps,
                    defaultWeightKg = weight,
                    defaultRestSec = rest
                )
            } else null
        }
    }

    override fun onCleared() {
        super.onCleared()
        alarmHelper.release()
        voiceCoach.shutdown()
        musicPlayer.release()
        workoutTimerJob?.cancel()
        restTimerJob?.cancel()
    }
}
