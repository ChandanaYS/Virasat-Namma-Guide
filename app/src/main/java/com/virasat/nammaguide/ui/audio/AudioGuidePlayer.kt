package com.virasat.nammaguide.ui.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import java.util.Locale

class AudioGuidePlayer(
    private val context: Context,
    private val onPlaybackFinished: () -> Unit
) : TextToSpeech.OnInitListener {
    private val appContext = context.applicationContext
    private val mainHandler = Handler(Looper.getMainLooper())
    private val audioManager = appContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
        .build()

    private var textToSpeech: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isReady = false
    private var audioFocusRequest: AudioFocusRequest? = null

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.apply {
                language = Locale.ENGLISH
                setSpeechRate(0.9f)
                setPitch(1.0f)
                setAudioAttributes(audioAttributes)
                setOnUtteranceProgressListener(
                    object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) = Unit

                        override fun onDone(utteranceId: String?) {
                            abandonAudioFocus()
                            mainHandler.post { onPlaybackFinished() }
                        }

                        @Deprecated("Deprecated in Android framework")
                        override fun onError(utteranceId: String?) {
                            abandonAudioFocus()
                            mainHandler.post { onPlaybackFinished() }
                        }

                        override fun onError(utteranceId: String?, errorCode: Int) {
                            abandonAudioFocus()
                            mainHandler.post { onPlaybackFinished() }
                        }

                        override fun onStop(utteranceId: String?, interrupted: Boolean) {
                            abandonAudioFocus()
                        }
                    }
                )
            }
            isReady = true
        }
    }

    fun toggle(isPlaying: Boolean, narration: String): Boolean {
        return if (isPlaying) {
            stop()
            true
        } else {
            speak(narration)
        }
    }

    private fun speak(narration: String): Boolean {
        if (!isReady || narration.isBlank()) return false
        if (!requestAudioFocus()) return false

        textToSpeech?.stop()

        val params = Bundle().apply {
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 0.75f)
        }
        textToSpeech?.speak(
            narration,
            TextToSpeech.QUEUE_FLUSH,
            params,
            "heritage_audio_guide"
        )
        return textToSpeech?.isSpeaking == true
    }

    fun stop() {
        textToSpeech?.stop()
        abandonAudioFocus()
        onPlaybackFinished()
    }

    fun release() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        abandonAudioFocus()
        isReady = false
    }

    private fun requestAudioFocus(): Boolean {
        audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
        audioManager.isSpeakerphoneOn = false

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val request = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                .setAudioAttributes(audioAttributes)
                .setOnAudioFocusChangeListener { focusChange ->
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS ||
                        focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT
                    ) {
                        stop()
                    }
                }
                .build()
            audioFocusRequest = request
            audioManager.requestAudioFocus(request) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(
                { focusChange ->
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS ||
                        focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT
                    ) {
                        stop()
                    }
                },
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
            ) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        }
    }

    private fun abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let(audioManager::abandonAudioFocusRequest)
            audioFocusRequest = null
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(null)
        }
        audioManager.mode = AudioManager.MODE_NORMAL
    }
}
