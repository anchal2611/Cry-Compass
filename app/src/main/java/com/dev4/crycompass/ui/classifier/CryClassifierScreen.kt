package com.dev4.crycompass.ui.classifier

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.dev4.crycompass.ui.components.RoundedBottomNav
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.abs
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryClassifierScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val activity = context as ComponentActivity

    var isListening by remember { mutableStateOf(false) }
    var detectedCry by remember { mutableStateOf("Calm") }
    var waveformHeights by remember { mutableStateOf(List(20) { 10f }) }

    val permissionGranted = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionGranted.value = granted
    }

    fun loadModel(): Interpreter {
        val fileDescriptor = context.assets.openFd("cry_model.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        val model: MappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        return Interpreter(model)
    }

    fun startRecording(interpreter: Interpreter) {
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

        val buffer = ShortArray(bufferSize)

        audioRecord.startRecording()
        coroutineScope.launch(Dispatchers.Default) {
            while (isActive && isListening) {
                audioRecord.read(buffer, 0, buffer.size)

                // Optional: Update waveform
                val normalized = buffer.map { abs(it.toFloat()) / Short.MAX_VALUE }
                waveformHeights = normalized.chunked(buffer.size / 20).map {
                    it.average().toFloat() * 100f
                }

                // Dummy input for model. You can replace this with MFCCs or MelSpectrogram
                val input = Array(1) { FloatArray(16000) { it / 16000f } }
                val output = Array(1) { FloatArray(4) }

                interpreter.run(input, output)
                val labels = listOf("Hunger", "Sleep", "Discomfort", "Calm")
                val maxIdx = output[0].indices.maxByOrNull { output[0][it] } ?: 3
                detectedCry = labels[maxIdx]

                delay(1000)
            }
            audioRecord.stop()
            audioRecord.release()
        }
    }

    if (!permissionGranted.value) {
        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    val interpreter by remember { mutableStateOf(loadModel()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cry Classifier", fontSize = 20.sp) },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Outlined.GraphicEq,
                        contentDescription = null,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            RoundedBottomNav(
                selectedItem = "classifier",
                onItemSelected = { route ->
                    navController.navigate(route) {
                        popUpTo("home") { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        },
        containerColor = Color(0xFFF9F9F9)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF6F6))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isListening) "Listening..." else "Tap to Start",
                        fontSize = 18.sp,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Detected: $detectedCry",
                        fontSize = 24.sp,
                        color = when (detectedCry) {
                            "Hunger" -> Color(0xFFF4926F)
                            "Sleep" -> Color(0xFFB3E5DC)
                            "Discomfort" -> Color(0xFF80BFEA)
                            else -> Color(0xFF4CAF50)
                        }
                    )
                }
            }

            WaveformVisualizer(heights = waveformHeights)

            Button(
                onClick = {
                    isListening = !isListening
                    if (isListening) startRecording(interpreter)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isListening) Color.Red else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(if (isListening) "Stop Detection" else "Start Detection")
            }
        }
    }
}

@Composable
fun WaveformVisualizer(heights: List<Float>) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFEEF7F7))
            .padding(8.dp)
    ) {
        val space = size.width / (heights.size * 2)

        heights.forEachIndexed { i, height ->
            val x = i * 2 * space
            drawLine(
                color = Color(0xFF80BFEA),
                start = androidx.compose.ui.geometry.Offset(x, size.height),
                end = androidx.compose.ui.geometry.Offset(x, size.height - height),
                strokeWidth = space
            )
        }
    }
}
