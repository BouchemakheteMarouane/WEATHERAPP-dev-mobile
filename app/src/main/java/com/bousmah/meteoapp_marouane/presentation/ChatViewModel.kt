package com.bousmah.meteoapp_marouane.presentation

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bousmah.meteoapp_marouane.data.remote.ChatApi
import com.bousmah.meteoapp_marouane.data.remote.OllamaRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val imageUri: String? = null
)

data class ChatUiState(
    val messages: List<ChatMessage> = listOf(
        ChatMessage("Hello! I'm your weather assistant. Ask me about the weather, what to wear, or outdoor activities!", false)
    ),
    val isLoading: Boolean = false,
    val isListening: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatApi: ChatApi,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(ChatUiState())
    val state = _state.asStateFlow()

    private var systemPrompt = "You are a weather assistant. Help users understand weather conditions, what to wear, and activities based on weather data."
    private var speechRecognizer: SpeechRecognizer? = null

    fun initWeatherContext(city: String, temp: Double, description: String) {
        systemPrompt = "You are a weather assistant. The user is currently in $city where the temperature is ${temp.toInt()}°C with $description. Help users understand weather conditions, what to wear, and activities based on this weather data."
        _state.update { it.copy(messages = listOf(
            ChatMessage("Hello! I'm your weather assistant. It's currently ${temp.toInt()}°C and $description in $city. How can I help you?", false)
        )) }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val userMessage = ChatMessage(text, true)
        _state.update { it.copy(messages = it.messages + userMessage, isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val fullPrompt = "$systemPrompt\n\nUser: $text\nAssistant:"
                val response = chatApi.generate(OllamaRequest(
                    model = "llama3.2:3b",
                    prompt = fullPrompt,
                    stream = false
                ))
                val botMessage = ChatMessage(response.response, false)
                _state.update { it.copy(messages = it.messages + botMessage, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(
                    isLoading = false,
                    error = "Failed to get response. Make sure Ollama is running on your computer."
                ) }
            }
        }
    }

    fun startVoiceInput() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            _state.update { it.copy(error = "Speech recognition not available on this device") }
            return
        }

        speechRecognizer?.destroy()
        val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
        if (recognizer == null) {
            _state.update { it.copy(error = "Speech recognition service unavailable") }
            return
        }
        speechRecognizer = recognizer
        _state.update { it.copy(isListening = true, error = null) }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        recognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}

            override fun onResults(results: Bundle?) {
                val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull() ?: ""
                _state.update { it.copy(isListening = false) }
                if (text.isNotBlank()) {
                    sendMessage(text)
                }
            }

            override fun onError(error: Int) {
                val message = when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH -> "No speech detected"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech timed out"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission not granted"
                    SpeechRecognizer.ERROR_CLIENT -> "Voice input client error"
                    SpeechRecognizer.ERROR_NETWORK -> "Network error during voice input"
                    else -> "Voice input error"
                }
                _state.update { it.copy(isListening = false, error = message) }
                viewModelScope.launch {
                    delay(2000)
                    _state.update { it.copy(error = null) }
                }
            }
        })

        try {
            recognizer.startListening(intent)
        } catch (e: SecurityException) {
            _state.update { it.copy(isListening = false, error = "Microphone permission denied") }
        } catch (e: IllegalStateException) {
            _state.update { it.copy(isListening = false, error = "Speech recognizer not ready") }
        }
    }

    fun stopVoiceInput() {
        speechRecognizer?.stopListening()
        speechRecognizer?.destroy()
        speechRecognizer = null
        _state.update { it.copy(isListening = false) }
    }

    fun sendImageMessage(uri: Uri) {
        val userMessage = ChatMessage("", true, imageUri = uri.toString())
        _state.update { it.copy(messages = it.messages + userMessage, isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val base64Image = withContext(Dispatchers.IO) {
                    val inputStream = context.contentResolver.openInputStream(uri)
                        ?: throw Exception("Could not open image file")
                    inputStream.use { stream ->
                        val bitmap = BitmapFactory.decodeStream(stream)
                            ?: throw Exception("Failed to decode image")
                        ByteArrayOutputStream().use { output ->
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, output)
                            Base64.encodeToString(output.toByteArray(), Base64.NO_WRAP)
                        }
                    }
                }

                val prompt = "$systemPrompt\n\nAnalyze this image and describe the weather conditions you see. What should someone wear in these conditions?"
                val response = chatApi.generate(OllamaRequest(
                    model = "llama3.2:3b",
                    prompt = prompt,
                    stream = false,
                    images = listOf(base64Image)
                ))
                _state.update { it.copy(
                    messages = it.messages + ChatMessage(response.response, false),
                    isLoading = false
                ) }
            } catch (e: Exception) {
                _state.update { it.copy(
                    isLoading = false,
                    error = "Failed to analyze image: ${e.message}"
                ) }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    override fun onCleared() {
        super.onCleared()
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}
