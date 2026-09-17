package com.example.media

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale

class VoiceCoach(
    context: Context,
    private val onInitComplete: (Boolean) -> Unit = {}
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    var isReady: Boolean = false
        private set
    var isEnabled: Boolean = true

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e("VoiceCoach", "TTS initialization error", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.w("VoiceCoach", "US English TTS not supported, using default locale")
                tts?.setLanguage(Locale.getDefault())
            }
            tts?.setSpeechRate(1.05f)
            tts?.setPitch(1.0f)
            isReady = true
            onInitComplete(true)
        } else {
            Log.e("VoiceCoach", "TTS Init failed with status: $status")
            isReady = false
            onInitComplete(false)
        }
    }

    fun speak(text: String, flushQueue: Boolean = false) {
        if (!isEnabled || !isReady) return
        try {
            val queueMode = if (flushQueue) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
            tts?.speak(text, queueMode, null, "VOICE_COACH_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            Log.w("VoiceCoach", "Speech failed: ${e.message}")
        }
    }

    fun announceSetCompleted(exerciseName: String, setNumber: Int, restSeconds: Int) {
        speak("Set $setNumber of $exerciseName completed. Starting $restSeconds seconds rest. Hydrate and breathe.")
    }

    fun announceRestWarning(secondsLeft: Int) {
        speak("Attention: $secondsLeft seconds of rest remaining. Get ready for your next set.", flushQueue = true)
    }

    fun announceRestFinished(nextExerciseName: String) {
        speak("Rest complete! Time to lift. Next up: $nextExerciseName.", flushQueue = true)
    }

    fun announceExerciseStart(exerciseName: String, targetSets: Int, targetReps: Int, weightKg: Double) {
        speak("Starting $exerciseName. Target: $targetSets sets of $targetReps reps at $weightKg kilograms. Stay locked in.")
    }

    fun announceWorkoutCompleted(totalVolumeKg: Double, setsCount: Int, durationMin: Int) {
        val volumeInt = totalVolumeKg.toInt()
        speak("Outstanding workout! You completed $setsCount sets in $durationMin minutes, crushing a total volume of $volumeInt kilograms. Time to refuel!")
    }

    fun announceHydrationAlert() {
        speak("Hydration check! Drink 250 milliliters of water to optimize muscle recovery and pump.", flushQueue = true)
    }

    /**
     * Interpret voice transcription into automated gym actions.
     */
    fun parseVoiceCommand(text: String): VoiceAction? {
        val lower = text.lowercase().trim()
        return when {
            lower.contains("log set") || lower.contains("done") || lower.contains("complete") || lower.contains("check") ->
                VoiceAction.LOG_CURRENT_SET
            lower.contains("add 30") || lower.contains("plus 30") || lower.contains("more rest") ->
                VoiceAction.ADD_REST_30
            lower.contains("add 15") || lower.contains("plus 15") ->
                VoiceAction.ADD_REST_15
            lower.contains("skip rest") || lower.contains("next exercise") || lower.contains("skip") ->
                VoiceAction.SKIP_REST
            lower.contains("pause") || lower.contains("hold") ->
                VoiceAction.PAUSE_TIMER
            lower.contains("resume") || lower.contains("continue") ->
                VoiceAction.RESUME_TIMER
            lower.contains("play music") || lower.contains("start music") || lower.contains("play beats") ->
                VoiceAction.PLAY_MUSIC
            lower.contains("pause music") || lower.contains("stop music") || lower.contains("mute") ->
                VoiceAction.PAUSE_MUSIC
            lower.contains("next track") || lower.contains("next song") || lower.contains("change song") ->
                VoiceAction.NEXT_TRACK
            lower.contains("water") || lower.contains("hydrate") || lower.contains("drink") ->
                VoiceAction.LOG_HYDRATION
            else -> null
        }
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (ignored: Exception) {}
        tts = null
        isReady = false
    }
}

enum class VoiceAction(val label: String) {
    LOG_CURRENT_SET("Log Set"),
    ADD_REST_30("Add 30s Rest"),
    ADD_REST_15("Add 15s Rest"),
    SKIP_REST("Skip Rest"),
    PAUSE_TIMER("Pause Timer"),
    RESUME_TIMER("Resume Timer"),
    PLAY_MUSIC("Play Music"),
    PAUSE_MUSIC("Pause Music"),
    NEXT_TRACK("Next Track"),
    LOG_HYDRATION("Log Water (+250ml)")
}
