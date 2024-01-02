package com.eyedetector

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.eyedetector.ui.theme.EyeDetectorTheme
import com.eyedetector.utils.SimpleCameraPreview
import com.eyedetector.utils.EyeImageAnalyzer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EyeDetectorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}



@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val offsetLeftEyeXY = remember {
        mutableStateOf(DpOffset.Zero)
    }
    val offsetRightEyeXY = remember {
        mutableStateOf(DpOffset.Zero)
    }
    val density = LocalDensity.current.density
    val size = remember { mutableStateOf(IntSize.Zero) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged {
                size.value = it
            }
    ) {
        SimpleCameraPreview(
            EyeImageAnalyzer { leftEye, rightEye ->
                val width = size.value.width / density
                val height = size.value.height / density
                Log.e("testsizeandxy", "$width $height ${leftEye?.position} ${rightEye?.position}")
                offsetLeftEyeXY.value = leftEye?.position?.let {
                    DpOffset(
                        (width - it.x).dp,
                        (it.y).dp
                    )
                } ?: DpOffset.Zero
                offsetRightEyeXY.value = rightEye?.position?.let {
                    DpOffset(
                        (width - it.x).dp,
                        (it.y).dp
                    )
                } ?: DpOffset.Zero
            }
        ) {
        }
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.TopStart)
                .offset(offsetLeftEyeXY.value.x, offsetLeftEyeXY.value.y)
        ) {
            drawCircle(
                brush = Brush.sweepGradient(listOf(Color.Cyan, Color.Blue)),
                radius = 10f,
                style = Stroke(10f)
            )
        }
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.TopStart)
                .offset(offsetRightEyeXY.value.x, offsetRightEyeXY.value.y)
        ) {
            drawCircle(
                brush = Brush.sweepGradient(listOf(Color.Cyan, Color.Blue)),
                radius = 10f,
                style = Stroke(10f)
            )
        }
    }
}
