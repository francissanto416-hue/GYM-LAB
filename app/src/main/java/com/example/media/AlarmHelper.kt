package com.example.media

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log

class AlarmHelper(private val context: Context) {

    private var toneGenerator: ToneGenerator? = null

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 95)
        } catch (e: Exception) {
            try {
                toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 90)
            } catch (ignored: Exception) {
                Log.w("AlarmHelper", "ToneGenerator initialization skipped", ignored)
            }
        }
    }

    /**
     * Loud double-alert tone and vibration when Rest Timer reaches 0.
     */
    fun playRestCompleteAlarm(vibrate: Boolean = true) {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 750)
        } catch (e: Exception) {
            Log.e("AlarmHelper", "Failed to trigger rest tone", e)
        }

        if (vibrate) {
            triggerVibration(pattern = longArrayOf(0, 300, 150, 450))
        }
    }

    /**
     * Warning chime when 10 seconds remain in rest interval.
     */
    fun playRestWarningChime() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 250)
        } catch (e: Exception) {
            Log.e("AlarmHelper", "Failed to trigger warning tone", e)
        }
        triggerVibration(pattern = longArrayOf(0, 100))
    }

    /**
     * Pleasant melodic double-beep for hydration alerts.
     */
    fun playHydrationReminderTone() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 500)
        } catch (e: Exception) {
            Log.e("AlarmHelper", "Failed to trigger hydration tone", e)
        }
        triggerVibration(pattern = longArrayOf(0, 200, 100, 200))
    }

    private fun triggerVibration(pattern: LongArray) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createWaveform(pattern, -1)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(pattern, -1)
            }
        } catch (e: Exception) {
            Log.w("AlarmHelper", "Vibration failed: ${e.message}")
        }
    }

    fun release() {
        toneGenerator?.release()
        toneGenerator = null
    }
}
