package com.dev4.crycompass.ui.cry

import android.Manifest
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.tensorflow.lite.task.audio.classifier.AudioClassifier
import org.tensorflow.lite.support.audio.TensorAudio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CryDetectorScreen() {
    val context = LocalContext.current
    val permissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    val hasPermission = permissionState.status == PermissionStatus.Granted

    var isRecording by remember { mutableStateOf(false) }
    var detectedCry by remember { mutableStateOf("None") }
    var lastCryType by remember { mutableStateOf("") }

    val classifier = remember {
        try {
            AudioClassifier.createFromFile(context, "cry_model.tflite")
        } catch (e: Exception) {
            null
        }
    }

    val tensorAudio = remember(classifier) {
        classifier?.createInputTensorAudio()
    }

    val audioRecord = remember(classifier) {
        classifier?.createAudioRecord()
    }

    val handler = remember { Handler(Looper.getMainLooper()) }

    fun startRecording() {
        if (classifier == null || tensorAudio == null || audioRecord == null) {
            detectedCry = "Model failed to load"
            return
        }

        isRecording = true
        audioRecord.startRecording()

        val detectRunnable = object : Runnable {
            override fun run() {
                if (!isRecording) return

                try {
                    tensorAudio.load(audioRecord)
                    val results = classifier.classify(tensorAudio)
                    val categories = results.getOrNull(0)?.categories
                    val topResult = categories?.maxByOrNull { it.score }

                    topResult?.let {
                        val label = it.label
                        val confidence = (it.score * 100).toInt()
                        detectedCry = "$label ($confidence%)"

                        // Log only if new cry type or confidence > 80%
                        if (label != lastCryType && confidence > 80) {
                            lastCryType = label
                            val userId = FirebaseAuth.getInstance().currentUser?.uid
                            if (userId != null) {
                                val logData = hashMapOf(
                                    "timestamp" to System.currentTimeMillis(),
                                    "cryType" to label,
                                    "confidence" to it.score
                                )
                                FirebaseFirestore.getInstance()
                                    .collection("users")
                                    .document(userId)
                                    .collection("cry_logs")
                                    .add(logData)
                            }
                        }
                    } ?: run {
                        detectedCry = "No cry detected"
                    }
                } catch (e: Exception) {
                    detectedCry = "Detection error"
                }

                handler.postDelayed(this, 2000L)
            }
        }

        handler.post(detectRunnable)
    }

    fun stopRecording() {
        isRecording = false
        try {
            audioRecord?.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        handler.removeCallbacksAndMessages(null)
    }

    // Clean up when user leaves the screen
    DisposableEffect(Unit) {
        onDispose {
            stopRecording()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Detected Cry:",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = detectedCry,
            style = MaterialTheme.typography.headlineSmall,
            color = if (isRecording) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (!hasPermission) {
            Button(onClick = { permissionState.launchPermissionRequest() }) {
                Text("Grant Microphone Permission")
            }
        } else {
            Button(
                onClick = {
                    if (isRecording) stopRecording() else startRecording()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(if (isRecording) "Stop Recording" else "Start Recording")
            }
        }
    }
}
