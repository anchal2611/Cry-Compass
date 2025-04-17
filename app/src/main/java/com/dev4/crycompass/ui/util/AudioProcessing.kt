package com.dev4.crycompass.ui.util

import kotlin.math.*

object AudioProcessing {

    // Convert raw audio to dummy MelSpectrogram (placeholder logic)
    fun generateMelSpectrogram(audioData: ShortArray, sampleRate: Int): Array<FloatArray> {
        val melBands = 64
        val timeSteps = 64
        val melSpec = Array(melBands) { FloatArray(timeSteps) }


        // Normalize audio
        val floatAudio = audioData.map { it.toFloat() / Short.MAX_VALUE }

        // Dummy logic to simulate spectrogram
        for (i in 0 until melBands) {
            for (j in 0 until timeSteps) {
                val index = (j * (floatAudio.size / timeSteps) + i) % floatAudio.size
                melSpec[i][j] = floatAudio[index]
            }
        }

        return melSpec
    }

    // Resize to 32x32 spectrogram input for the model
    fun resizeSpectrogram(input: Array<FloatArray>, newWidth: Int, newHeight: Int): Array<FloatArray> {
        val oldHeight = input.size
        val oldWidth = input[0].size

        val scaleX = oldWidth.toFloat() / newWidth
        val scaleY = oldHeight.toFloat() / newHeight

        val resized = Array(newHeight) { FloatArray(newWidth) }

        for (y in 0 until newHeight) {
            val srcY = (y * scaleY).toInt().coerceIn(0, oldHeight - 1)
            for (x in 0 until newWidth) {
                val srcX = (x * scaleX).toInt().coerceIn(0, oldWidth - 1)
                resized[y][x] = input[srcY][srcX]
            }
        }

        return resized
    }
}

