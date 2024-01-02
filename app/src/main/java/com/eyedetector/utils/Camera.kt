package com.eyedetector.utils

import android.annotation.SuppressLint
import android.util.Size
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.Executor

@Composable
fun SimpleCameraPreview(analyzer: ImageAnalysis.Analyzer, cameraResult: (Camera)-> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    AndroidView(
        factory = { ctx ->
            val preview = PreviewView(ctx)
            val executor = ContextCompat.getMainExecutor(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val camera = bindPreview(
                    lifecycleOwner,
                    preview,
                    cameraProvider,
                    analyzer,
                    executor
                )
                cameraResult.invoke(camera)
            }, executor)
            preview
        },
        modifier = Modifier.fillMaxSize(),
    )
}

@SuppressLint("UnsafeExperimentalUsageError")
private fun bindPreview(
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    cameraProvider: ProcessCameraProvider,
    analyzer: ImageAnalysis.Analyzer,
    executor: Executor
): Camera {
    val preview = Preview.Builder().build().also {
        it.setSurfaceProvider(previewView.surfaceProvider)
    }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
        .build()
    cameraProvider.unbindAll()
    return cameraProvider.bindToLifecycle(
        lifecycleOwner,
        cameraSelector,
        setupImageAnalysis(executor, analyzer),
        preview
    )
}

@SuppressLint("UnsafeOptInUsageError")
private fun setupImageAnalysis(executor: Executor, analyzer: ImageAnalysis.Analyzer): ImageAnalysis {
    val builder = ImageAnalysis.Builder()
    return builder
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()
        .apply {
            setAnalyzer(executor,analyzer)
        }
}

