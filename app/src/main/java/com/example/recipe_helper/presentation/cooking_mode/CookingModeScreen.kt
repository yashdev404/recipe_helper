package com.example.recipe_helper.presentation.cooking_mode

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipe_helper.domain.model.Recipe
import com.example.recipe_helper.ui.theme.OrangePrimary
import kotlinx.coroutines.delay
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CookingModeScreen(
    recipe: Recipe,
    onBackClick: () -> Unit,
    onFinishCooking: (Recipe) -> Unit
) {
    val context = LocalContext.current

    // Keep screen awake while cooking
    DisposableEffect(Unit) {
        val activity = context as? Activity
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    var currentStepIndex by remember { mutableIntStateOf(0) }
    val steps = remember(recipe.instructions) {
        if (recipe.instructions.isEmpty()) listOf("Follow standard recipe preparation.") else recipe.instructions
    }

    val totalSteps = steps.size
    val currentStepText = steps[currentStepIndex]

    // TextToSpeech Engine Initialization
    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    var isTtsReady by remember { mutableStateOf(false) }

    DisposableEffect(context) {
        var ttsEngine: TextToSpeech? = null
        ttsEngine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsEngine?.language = Locale.US
                isTtsReady = true
            }
        }
        tts = ttsEngine

        onDispose {
            ttsEngine.stop()
            ttsEngine.shutdown()
        }
    }

    fun speakCurrentStep() {
        if (isTtsReady) {
            tts?.stop()
            tts?.speak("Step ${currentStepIndex + 1}. $currentStepText", TextToSpeech.QUEUE_FLUSH, null, "step_tts")
        }
    }

    // Voice Command Speech Recognizer Setup
    var isVoiceListening by remember { mutableStateOf(false) }
    var voiceFeedbackText by remember { mutableStateOf("") }
    var speechRecognizer: SpeechRecognizer? by remember { mutableStateOf(null) }

    DisposableEffect(context) {
        val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognizer = recognizer

        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                voiceFeedbackText = "Listening for 'Next', 'Back', 'Repeat'..."
            }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {
                if (isVoiceListening) {
                    voiceFeedbackText = "Listening..."
                    // Restart listening on timeout
                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                    }
                    recognizer.startListening(intent)
                }
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val spoken = matches?.firstOrNull()?.lowercase(Locale.getDefault()) ?: ""

                when {
                    spoken.contains("next") || spoken.contains("forward") || spoken.contains("done") -> {
                        if (currentStepIndex < totalSteps - 1) {
                            currentStepIndex += 1
                            voiceFeedbackText = "Command: Next Step ✓"
                        } else {
                            voiceFeedbackText = "Last step reached!"
                        }
                    }
                    spoken.contains("back") || spoken.contains("previous") || spoken.contains("last") -> {
                        if (currentStepIndex > 0) {
                            currentStepIndex -= 1
                            voiceFeedbackText = "Command: Previous Step ✓"
                        }
                    }
                    spoken.contains("repeat") || spoken.contains("read") || spoken.contains("again") || spoken.contains("speak") -> {
                        speakCurrentStep()
                        voiceFeedbackText = "Reading Step Out Loud 🔊"
                    }
                    else -> {
                        voiceFeedbackText = "Heard '$spoken' (Say 'Next', 'Back', or 'Repeat')"
                    }
                }

                if (isVoiceListening) {
                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                    }
                    recognizer.startListening(intent)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        }

        recognizer.setRecognitionListener(listener)

        onDispose {
            recognizer.destroy()
        }
    }

    fun toggleVoiceListening() {
        if (!isVoiceListening) {
            isVoiceListening = true
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            }
            speechRecognizer?.startListening(intent)
        } else {
            isVoiceListening = false
            voiceFeedbackText = ""
            speechRecognizer?.stopListening()
        }
    }

    // Automated timer detection in step text
    val detectedMinutes = remember(currentStepText) {
        val regex = Regex("(\\d+)\\s*(min|minute|mins)")
        val match = regex.find(currentStepText.lowercase(Locale.getDefault()))
        match?.groupValues?.get(1)?.toLongOrNull() ?: 0L
    }

    var timerTotalSeconds by remember(currentStepIndex) { mutableLongStateOf(detectedMinutes * 60) }
    var timerRemainingSeconds by remember(currentStepIndex) { mutableLongStateOf(timerTotalSeconds) }
    var isTimerRunning by remember(currentStepIndex) { mutableStateOf(false) }

    LaunchedEffect(isTimerRunning, timerRemainingSeconds) {
        if (isTimerRunning && timerRemainingSeconds > 0) {
            delay(1000L)
            timerRemainingSeconds -= 1
        } else if (timerRemainingSeconds == 0L) {
            isTimerRunning = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Cooking Mode 🍳",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = recipe.title,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Black)
                    }
                },
                actions = {
                    // Speak Step Button
                    IconButton(onClick = { speakCurrentStep() }) {
                        Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "Read Step", tint = OrangePrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFFAFAFA)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Voice Control Feedback Bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Step ${currentStepIndex + 1} of $totalSteps",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrangePrimary
                    )

                    // Voice Command Toggle
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isVoiceListening) Color(0xFF4CAF50) else Color(0xFFE0E0E0),
                        modifier = Modifier.clickable { toggleVoiceListening() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isVoiceListening) Icons.Default.Mic else Icons.Default.MicOff,
                                contentDescription = "Voice Control",
                                tint = if (isVoiceListening) Color.White else Color.DarkGray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isVoiceListening) "Voice Active" else "Voice Control",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isVoiceListening) Color.White else Color.DarkGray
                            )
                        }
                    }
                }

                if (voiceFeedbackText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = voiceFeedbackText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2E7D32)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { (currentStepIndex + 1).toFloat() / totalSteps },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = OrangePrimary,
                    trackColor = Color(0xFFE0E0E0)
                )
            }

            // Main Instruction Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(OrangePrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${currentStepIndex + 1}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = OrangePrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = currentStepText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        lineHeight = 28.sp
                    )

                    // Step Timer Box if timer detected
                    if (timerTotalSeconds > 0) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFFFF3E0),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Timer, contentDescription = null, tint = Color(0xFFE65100))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    val minutes = timerRemainingSeconds / 60
                                    val seconds = timerRemainingSeconds % 60
                                    Text(
                                        text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds),
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE65100)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    IconButton(
                                        onClick = { isTimerRunning = !isTimerRunning },
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(Color(0xFFE65100))
                                    ) {
                                        Icon(
                                            imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            isTimerRunning = false
                                            timerRemainingSeconds = timerTotalSeconds
                                        },
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(Color.White)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = null,
                                            tint = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous Button
                Button(
                    onClick = {
                        if (currentStepIndex > 0) currentStepIndex -= 1
                    },
                    enabled = currentStepIndex > 0,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEEEEE)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.height(52.dp)
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Previous", tint = Color.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Previous", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                // Next or Finish Button
                if (currentStepIndex < totalSteps - 1) {
                    Button(
                        onClick = {
                            currentStepIndex += 1
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.height(52.dp)
                    ) {
                        Text(text = "Next Step", color = Color.White, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Next", tint = Color.White)
                    }
                } else {
                    Button(
                        onClick = { onFinishCooking(recipe) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.height(52.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Finish", tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Finish Cooking 🎉", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
