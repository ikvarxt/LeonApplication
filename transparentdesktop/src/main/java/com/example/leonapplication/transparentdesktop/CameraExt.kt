package com.example.leonapplication.transparentdesktop

import android.Manifest
import android.content.Context
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.TotalCaptureResult
import android.view.Surface
import androidx.annotation.RequiresPermission
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

lateinit var cameraDevice: CameraDevice
lateinit var captureSession: CameraCaptureSession

@RequiresPermission(Manifest.permission.CAMERA)
suspend fun openCamera(context: Context) = suspendCoroutine { con ->
  val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
  val cameraId = cameraManager.cameraIdList.firstOrNull { it.startsWith("0") } // Rear-facing camera

  cameraId ?: con.resumeWithException(IllegalStateException("no camera id"))

  cameraManager.openCamera(cameraId!!, object : CameraDevice.StateCallback() {
    override fun onOpened(camera: CameraDevice) {
      cameraDevice = camera
      con.resume(camera)
    }

    override fun onDisconnected(camera: CameraDevice) {
    }

    override fun onError(camera: CameraDevice, error: Int) {
      con.resumeWithException(IllegalStateException("open camera error code $error"))
    }
  }, null)
}

suspend fun createCaptureSession(surface: Surface) = suspendCoroutine { con ->
  cameraDevice.createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {
    override fun onConfigured(session: CameraCaptureSession) {
      captureSession = session
      con.resume(session)
    }

    override fun onConfigureFailed(session: CameraCaptureSession) {
      con.resumeWithException(IllegalStateException("configure error $session"))
    }
  }, null)
}

fun startPreview(surface: Surface) {
  val previewRequest = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
    addTarget(surface)
    // Configure other capture request settings
  }.build()
  captureSession.setRepeatingRequest(previewRequest, object : CameraCaptureSession.CaptureCallback() {
    override fun onCaptureCompleted(
      session: CameraCaptureSession,
      request: CaptureRequest,
      result: TotalCaptureResult,
    ) {
      // Handle each preview frame
    }
  }, null)
}