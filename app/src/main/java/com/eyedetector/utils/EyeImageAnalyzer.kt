package com.eyedetector.utils

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark

class EyeImageAnalyzer(private val callback: (FaceLandmark?, FaceLandmark?) -> Unit) : ImageAnalysis.Analyzer {

    private val highAccuracyOpts = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .build()
    private val detector = FaceDetection.getClient(highAccuracyOpts)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            detector.process(image)
                .addOnSuccessListener { faces ->
                    for (face in faces) {
                        callback.invoke(face.getLandmark(FaceLandmark.LEFT_EYE), face.getLandmark(FaceLandmark.RIGHT_EYE))
                    }
                    imageProxy.close()
                }
                .addOnFailureListener { e ->
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}