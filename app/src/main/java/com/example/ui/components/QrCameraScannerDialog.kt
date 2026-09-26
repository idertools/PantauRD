package com.example.ui.components

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.ui.theme.GuardCardDark
import com.example.ui.theme.GuardEmergencyRed
import com.example.ui.theme.GuardNavyDark
import com.example.ui.theme.GuardPrimaryCyan
import com.example.ui.theme.GuardSafeGreen
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.util.concurrent.Executors

@Composable
fun QrCameraScannerDialog(
    onQrScanned: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xE608101C))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .testTag("qr_camera_scanner_dialog"),
                color = GuardCardDark,
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E334D))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = GuardPrimaryCyan.copy(alpha = 0.2f),
                                modifier = Modifier.size(34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.QrCodeScanner,
                                        contentDescription = null,
                                        tint = GuardPrimaryCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Pindai Kode QR",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Kamera Otorisasi iDerMata",
                                    color = GuardPrimaryCyan,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF132235), CircleShape)
                                .testTag("close_qr_scanner_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Tutup Pemindai",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (!hasCameraPermission) {
                        // Permission rationale / request button
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF0F1A28))
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                tint = GuardEmergencyRed,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Izin Kamera Diperlukan",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Aplikasi membutuhkan akses kamera untuk memindai Kode QR pada ponsel keluarga secara langsung.",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                                colors = ButtonDefaults.buttonColors(containerColor = GuardPrimaryCyan),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("request_camera_perm_btn")
                            ) {
                                Text("Berikan Izin Kamera", color = GuardNavyDark, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        // Real Camera Scanner View with viewfinder HUD
                        CameraScannerContent(
                            onQrDetected = { rawData ->
                                triggerHaptic(context)
                                onQrScanned(rawData)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Arahkan kamera tepat ke layar HP keluarga yang menampilkan Kode QR otorisasi.",
                        color = Color(0xFF8B949E),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CameraScannerContent(
    onQrDetected: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    var isTorchOn by remember { mutableStateOf(false) }
    var cameraRef by remember { mutableStateOf<Camera?>(null) }
    var hasDetected by remember { mutableStateOf(false) }
    var galleryErrorMessage by remember { mutableStateOf<String?>(null) }

    // Laser scanline animation
    val infiniteTransition = rememberInfiniteTransition(label = "laser")
    val laserProgress by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laserProgress"
    )

    // Photo picker for QR from gallery
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val decoded = decodeQrFromUri(context, uri)
            if (decoded != null && !hasDetected) {
                hasDetected = true
                onQrDetected(decoded)
            } else {
                galleryErrorMessage = "Kode QR tidak ditemukan di gambar tersebut."
            }
        }
    }

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(18.dp))
            .background(Color.Black)
            .border(1.dp, Color(0xFF1E3A5F), RoundedCornerShape(18.dp))
    ) {
        // CameraX Preview
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }

                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                        if (!hasDetected) {
                            val scannedText = processImageProxy(imageProxy)
                            if (!scannedText.isNullOrBlank() && !hasDetected) {
                                hasDetected = true
                                previewView.post {
                                    onQrDetected(scannedText)
                                }
                            }
                        }
                        imageProxy.close()
                    }

                    try {
                        cameraProvider.unbindAll()
                        val cameraSelector = CameraSelector.Builder()
                            .requireLensFacing(lensFacing)
                            .build()
                        cameraRef = cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalysis
                        )
                    } catch (_: Exception) {
                    }
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            },
            update = {
                // Camera instance update handled via lens/torch states
            }
        )

        // Viewfinder HUD Overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            val boxSize = size.width * 0.72f
            val left = (size.width - boxSize) / 2f
            val top = (size.height - boxSize) / 2f
            val right = left + boxSize
            val bottom = top + boxSize
            val cornerLength = 28.dp.toPx()
            val strokeW = 4.dp.toPx()
            val cornerColor = GuardPrimaryCyan

            // Darkened vignette background outside scan window
            // Top
            drawRect(Color(0x99000000), Offset.Zero, Size(size.width, top))
            // Bottom
            drawRect(Color(0x99000000), Offset(0f, bottom), Size(size.width, size.height - bottom))
            // Left
            drawRect(Color(0x99000000), Offset(0f, top), Size(left, boxSize))
            // Right
            drawRect(Color(0x99000000), Offset(right, top), Size(size.width - right, boxSize))

            // 4 Corner brackets (Top-Left, Top-Right, Bottom-Left, Bottom-Right)
            // Top-Left
            drawLine(cornerColor, Offset(left, top), Offset(left + cornerLength, top), strokeW)
            drawLine(cornerColor, Offset(left, top), Offset(left, top + cornerLength), strokeW)
            // Top-Right
            drawLine(cornerColor, Offset(right, top), Offset(right - cornerLength, top), strokeW)
            drawLine(cornerColor, Offset(right, top), Offset(right, top + cornerLength), strokeW)
            // Bottom-Left
            drawLine(cornerColor, Offset(left, bottom), Offset(left + cornerLength, bottom), strokeW)
            drawLine(cornerColor, Offset(left, bottom), Offset(left, bottom - cornerLength), strokeW)
            // Bottom-Right
            drawLine(cornerColor, Offset(right, bottom), Offset(right - cornerLength, bottom), strokeW)
            drawLine(cornerColor, Offset(right, bottom), Offset(right, bottom - cornerLength), strokeW)

            // Animated Laser Scanning Line
            val laserY = top + (boxSize * laserProgress)
            drawLine(
                brush = Brush.horizontalGradient(
                    listOf(
                        Color.Transparent,
                        GuardPrimaryCyan,
                        Color.White,
                        GuardPrimaryCyan,
                        Color.Transparent
                    ),
                    startX = left,
                    endX = right
                ),
                start = Offset(left + 6.dp.toPx(), laserY),
                end = Offset(right - 6.dp.toPx(), laserY),
                strokeWidth = 3.dp.toPx()
            )
        }

        // Camera Controls Toolbar at Bottom
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Flashlight Toggle
            IconButton(
                onClick = {
                    val newTorch = !isTorchOn
                    isTorchOn = newTorch
                    cameraRef?.cameraControl?.enableTorch(newTorch)
                },
                modifier = Modifier
                    .size(42.dp)
                    .background(Color(0xAA0B1523), CircleShape)
                    .border(1.dp, Color(0xFF223A5B), CircleShape)
                    .testTag("toggle_torch_btn")
            ) {
                Icon(
                    imageVector = if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                    contentDescription = if (isTorchOn) "Matikan Senter" else "Nyalakan Senter",
                    tint = if (isTorchOn) Color(0xFFF59E0B) else Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Pick Image from Gallery
            IconButton(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier
                    .size(42.dp)
                    .background(Color(0xAA0B1523), CircleShape)
                    .border(1.dp, Color(0xFF223A5B), CircleShape)
                    .testTag("pick_qr_from_gallery_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Pilih Gambar dari Galeri",
                    tint = GuardSafeGreen,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Flip Camera Toggle
            IconButton(
                onClick = {
                    lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                        CameraSelector.LENS_FACING_FRONT
                    } else {
                        CameraSelector.LENS_FACING_BACK
                    }
                },
                modifier = Modifier
                    .size(42.dp)
                    .background(Color(0xAA0B1523), CircleShape)
                    .border(1.dp, Color(0xFF223A5B), CircleShape)
                    .testTag("flip_camera_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.FlipCameraAndroid,
                    contentDescription = "Balik Kamera",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

    if (galleryErrorMessage != null) {
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = galleryErrorMessage!!,
            color = GuardEmergencyRed,
            fontSize = 11.sp,
            textAlign = TextAlign.Center
        )
    }
}

private fun processImageProxy(imageProxy: ImageProxy): String? {
    return try {
        val plane = imageProxy.planes[0]
        val buffer = plane.buffer
        val data = ByteArray(buffer.remaining())
        buffer.get(data)

        val width = imageProxy.width
        val height = imageProxy.height

        // Try standard orientation
        val source = PlanarYUVLuminanceSource(data, width, height, 0, 0, width, height, false)
        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
        val reader = MultiFormatReader().apply {
            setHints(mapOf(
                DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE),
                DecodeHintType.CHARACTER_SET to "UTF-8"
            ))
        }

        try {
            reader.decodeWithState(binaryBitmap).text
        } catch (_: Exception) {
            // Try rotated 90 degrees if portrait mode
            val rotatedData = rotateYuv90(data, width, height)
            val rotatedSource = PlanarYUVLuminanceSource(rotatedData, height, width, 0, 0, height, width, false)
            val rotatedBitmap = BinaryBitmap(HybridBinarizer(rotatedSource))
            try {
                reader.decodeWithState(rotatedBitmap).text
            } catch (_: Exception) {
                null
            }
        }
    } catch (_: Exception) {
        null
    }
}

private fun rotateYuv90(data: ByteArray, imageWidth: Int, imageHeight: Int): ByteArray {
    val yuv = ByteArray(imageWidth * imageHeight)
    var i = 0
    for (x in 0 until imageWidth) {
        for (y in imageHeight - 1 downTo 0) {
            yuv[i] = data[y * imageWidth + x]
            i++
        }
    }
    return yuv
}

private fun decodeQrFromUri(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream) ?: return null
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        val source = RGBLuminanceSource(width, height, pixels)
        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
        val reader = MultiFormatReader().apply {
            setHints(mapOf(
                DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE),
                DecodeHintType.CHARACTER_SET to "UTF-8"
            ))
        }
        reader.decodeWithState(binaryBitmap).text
    } catch (_: Exception) {
        null
    }
}

private fun triggerHaptic(context: Context) {
    try {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(100)
        }
    } catch (_: Exception) {
    }
}

/**
 * Extracts 6-digit PIN from QR payload or raw text
 */
fun parseQrPayloadToPin(rawText: String): String? {
    val clean = rawText.trim()
    // Check if format is IDERMATA_PAIRING_PAYLOAD:<PIN>:<timestamp>
    if (clean.contains("PAIRING_PAYLOAD:")) {
        val parts = clean.split(":")
        if (parts.size >= 2) {
            val potentialPin = parts[1].trim()
            if (potentialPin.length == 6 && potentialPin.all { it.isDigit() }) {
                return potentialPin
            }
        }
    }

    // Direct 6-digit number
    if (clean.length == 6 && clean.all { it.isDigit() }) {
        return clean
    }

    // Search for 6-digit consecutive numbers
    val match = Regex("""\b\d{6}\b""").find(clean)
    return match?.value
}
