package com.example.media

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

data class WorkoutTrack(
    val title: String,
    val artist: String,
    val genre: String,
    val baseBpm: Int,
    val durationSec: Int = 195,
    val vibe: String
)

class WorkoutMusicPlayer(private val scope: CoroutineScope) {

    val playlist = listOf(
        WorkoutTrack(
            title = "Midnight Berserk (Phonk Edit)",
            artist = "IronPulse Beats",
            genre = "Drift Phonk",
            baseBpm = 145,
            vibe = "Aggressive PR Lift"
        ),
        WorkoutTrack(
            title = "Hypertrophy Cyber Horizon",
            artist = "SynthCore 84",
            genre = "Retrowave",
            baseBpm = 132,
            vibe = "Laser Focus Rhythm"
        ),
        WorkoutTrack(
            title = "Heavy Chains Breaking",
            artist = "Titanium Drive",
            genre = "Metalcore / Industrial",
            baseBpm = 150,
            vibe = "High Adrenaline"
        ),
        WorkoutTrack(
            title = "Underground Warehouse 128",
            artist = "Klub Krypton",
            genre = "Tech House",
            baseBpm = 128,
            vibe = "Relentless Flow"
        ),
        WorkoutTrack(
            title = "Post-Set Dopamine Chill",
            artist = "Zenith Recovery",
            genre = "Lo-Fi Beats",
            baseBpm = 86,
            vibe = "Cool Down & Stretch"
        )
    )

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentTrackIndex = MutableStateFlow(0)
    val currentTrackIndex: StateFlow<Int> = _currentTrackIndex.asStateFlow()

    private val _currentTrack = MutableStateFlow(playlist[0])
    val currentTrack: StateFlow<WorkoutTrack> = _currentTrack.asStateFlow()

    private val _playbackProgressSec = MutableStateFlow(0)
    val playbackProgressSec: StateFlow<Int> = _playbackProgressSec.asStateFlow()

    private val _bpm = MutableStateFlow(playlist[0].baseBpm)
    val bpm: StateFlow<Int> = _bpm.asStateFlow()

    private val _bassBoost = MutableStateFlow(true)
    val bassBoost: StateFlow<Boolean> = _bassBoost.asStateFlow()

    private val _volume = MutableStateFlow(0.85f)
    val volume: StateFlow<Float> = _volume.asStateFlow()

    private var audioJob: Job? = null
    private var progressJob: Job? = null
    private var audioTrack: AudioTrack? = null

    init {
        initAudioTrack()
    }

    private fun initAudioTrack() {
        val sampleRate = 22050
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        try {
            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize.coerceAtLeast(4096))
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()
            audioTrack?.setVolume(_volume.value)
        } catch (e: Exception) {
            Log.e("WorkoutMusicPlayer", "AudioTrack init error", e)
        }
    }

    fun togglePlayPause() {
        if (_isPlaying.value) {
            pause()
        } else {
            play()
        }
    }

    fun play() {
        if (_isPlaying.value) return
        _isPlaying.value = true

        try {
            audioTrack?.play()
        } catch (e: Exception) {
            Log.e("WorkoutMusicPlayer", "AudioTrack play error", e)
        }

        // Background rhythm synthesizer generator
        audioJob?.cancel()
        audioJob = scope.launch(Dispatchers.Default) {
            synthesizeWorkoutBeatLoop()
        }

        // Progress timer
        progressJob?.cancel()
        progressJob = scope.launch(Dispatchers.Default) {
            while (isActive && _isPlaying.value) {
                kotlinx.coroutines.delay(1000)
                val newSec = (_playbackProgressSec.value + 1) % _currentTrack.value.durationSec
                _playbackProgressSec.value = newSec
                if (newSec == 0) {
                    nextTrack()
                }
            }
        }
    }

    fun pause() {
        _isPlaying.value = false
        audioJob?.cancel()
        progressJob?.cancel()
        try {
            audioTrack?.pause()
            audioTrack?.flush()
        } catch (e: Exception) {
            Log.w("WorkoutMusicPlayer", "Pause flush error", e)
        }
    }

    fun nextTrack() {
        val nextIdx = (_currentTrackIndex.value + 1) % playlist.size
        selectTrack(nextIdx)
    }

    fun prevTrack() {
        val prevIdx = if (_currentTrackIndex.value - 1 < 0) playlist.size - 1 else _currentTrackIndex.value - 1
        selectTrack(prevIdx)
    }

    fun selectTrack(index: Int) {
        val wasPlaying = _isPlaying.value
        _currentTrackIndex.value = index
        val track = playlist[index]
        _currentTrack.value = track
        _bpm.value = track.baseBpm
        _playbackProgressSec.value = 0
        if (wasPlaying) {
            pause()
            play()
        }
    }

    fun setBpm(newBpm: Int) {
        _bpm.value = newBpm.coerceIn(70, 180)
    }

    fun toggleBassBoost() {
        _bassBoost.value = !_bassBoost.value
    }

    fun setVolume(vol: Float) {
        val clamped = vol.coerceIn(0f, 1f)
        _volume.value = clamped
        audioTrack?.setVolume(clamped)
    }

    /**
     * Synthesize energetic rhythmic PCM pulses matching the current BPM.
     */
    private suspend fun synthesizeWorkoutBeatLoop() {
        val sampleRate = 22050
        val buffer = ShortArray(2048)

        while (scope.isActive && _isPlaying.value) {
            val currentBpm = _bpm.value
            val beatIntervalMs = (60_000.0 / currentBpm).toLong()
            val kickDurationSamples = (sampleRate * 0.14).toInt()
            val bassFactor = if (_bassBoost.value) 1.5 else 1.0

            // Generate kick punch wave (60Hz falling pitch)
            var sampleIndex = 0
            while (sampleIndex < kickDurationSamples && _isPlaying.value) {
                val chunkSize = buffer.size.coerceAtMost(kickDurationSamples - sampleIndex)
                for (i in 0 until chunkSize) {
                    val t = (sampleIndex + i).toDouble() / sampleRate
                    val freq = 120.0 * (1.0 - (t / 0.15)) + 45.0
                    val envelope = (1.0 - (t / 0.15)).coerceIn(0.0, 1.0)
                    val sampleVal = (sin(2.0 * PI * freq * t) * 22000 * envelope * bassFactor).toInt()
                    buffer[i] = sampleVal.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }
                audioTrack?.write(buffer, 0, chunkSize)
                sampleIndex += chunkSize
            }

            // Snare / Hat click
            val remainingMs = (beatIntervalMs - 160).coerceAtLeast(50)
            kotlinx.coroutines.delay(remainingMs / 2)

            // High Hat pulse
            val hatSamples = (sampleRate * 0.05).toInt()
            for (i in 0 until hatSamples.coerceAtMost(buffer.size)) {
                val noise = (Math.random() * 12000 - 6000).toInt()
                buffer[i] = noise.toShort()
            }
            audioTrack?.write(buffer, 0, hatSamples.coerceAtMost(buffer.size))

            kotlinx.coroutines.delay(remainingMs / 2)
        }
    }

    fun release() {
        pause()
        try {
            audioTrack?.release()
        } catch (ignored: Exception) {}
        audioTrack = null
    }
}
