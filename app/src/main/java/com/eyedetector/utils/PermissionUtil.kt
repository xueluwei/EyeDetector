package com.eyedetector.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionsRequired
import com.google.accompanist.permissions.rememberMultiplePermissionsState

object PermissionUtil {
    private val REQUIRED_PERMISSIONS = listOf(Manifest.permission.CAMERA)

    fun allPermissionsGranted(context: Context) = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            context, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun CheckPermission(result: (Boolean) -> Unit) {
        val permissionState = rememberMultiplePermissionsState(
            permissions = REQUIRED_PERMISSIONS
        )
        LaunchedEffect(Unit) {
            permissionState.launchMultiplePermissionRequest()
        }
        PermissionsRequired(
            multiplePermissionsState = permissionState,
            permissionsNotGrantedContent = { result.invoke(false) },
            permissionsNotAvailableContent = { result.invoke(false) }
        ) {
            result.invoke(true)
        }
    }
}