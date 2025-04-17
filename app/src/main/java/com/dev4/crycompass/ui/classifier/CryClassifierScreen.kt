package com.dev4.crycompass.ui.classifier

import android.Manifest
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryClassifierScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var detectionResult by remember { mutableStateOf<String?>(null) }
    var isDetecting by remember { mutableStateOf(false) }

    val interpreter by remember {
        mutableStateOf(
            Interpreter(
                context.assets.open("cry_model.tflite").readBytes(),
                Interpreter.Options()
            )
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDFDFD))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Baby Crying Detection",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF222222)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFFE7EAFE), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = detectionResult ?: "No cry detected yet",
                    fontSize = 18.sp,
                    color = Color(0xFF444444)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    isDetecting = true
                    detectionResult = "Listening..."
                    coroutineScope.launch(Dispatchers.IO) {
                        val result = startDetection(context, interpreter)
                        detectionResult = result
                        isDetecting = false
                    }
                },
                enabled = !isDetecting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFC8D87)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = if (isDetecting) "Detecting..." else "Start Detecting",
                    fontSize = 16.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

suspend fun startDetection(context: Context, interpreter: Interpreter): String {
    val sampleRate = 16000
    val bufferSize = AudioRecord.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )

    val audioRecord = AudioRecord(
        MediaRecorder.AudioSource.MIC,
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT,
        bufferSize
    )

    val audioBuffer = ShortArray(sampleRate) // 1 sec of audio

    audioRecord.startRecording()
    audioRecord.read(audioBuffer, 0, audioBuffer.size)
    audioRecord.stop()
    audioRecord.release()

    // Convert audio to MelSpectrogram
    val melSpec = AudioProcessing.generateMelSpectrogram(audioBuffer, sampleRate)

    // Resize to 32x32 (you may use interpolation or center crop depending on size)
    val resized = AudioProcessing.resizeSpectrogram(melSpec, 32, 32)

    // Convert to ByteBuffer (float32, grayscale)
    val input = ByteBuffer.allocateDirect(4 * 32 * 32)
    input.order(ByteOrder.nativeOrder())
    for (row in resized) {
        for (value in row) {
            input.putFloat(value)
        }
    }
    input.rewind()

    val output = TensorBuffer.createFixedSize(intArrayOf(1, 3), org.tensorflow.lite.DataType.FLOAT32)
    interpreter.run(input, output.buffer.rewind())

    val predictions = output.floatArray
    val labels = listOf("Hungry", "Sleepy", "Discomfort")
    val maxIndex = predictions.indices.maxByOrNull { predictions[it] } ?: 0
    return "Detected: ${labels[maxIndex]}"
}

