package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.data.model.Order
import com.example.data.model.PosInventoryItem
import com.example.data.model.Product
import com.example.util.AdminQrLookupHelper
import com.example.util.AdminQrLookupResult
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

@Composable
fun AdminQrScannerDialog(
  products: List<Product>,
  orders: List<Order>,
  posInventory: Map<String, PosInventoryItem> = emptyMap(),
  onDismiss: () -> Unit,
  onEditProduct: (Product) -> Unit,
  onViewInvoice: (Order) -> Unit,
  onOpenShippingLabel: (Order) -> Unit,
  onUpdateOrderStatus: (String, String) -> Unit,
  onShowToast: (String) -> Unit
) {
  val context = LocalContext.current
  val mainExecutor = remember(context) { ContextCompat.getMainExecutor(context) }

  var hasCameraPermission by remember {
    mutableStateOf(
      try {
        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
          PackageManager.PERMISSION_GRANTED
      } catch (_: Exception) {
        false
      }
    )
  }

  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { granted ->
    hasCameraPermission = granted
    if (!granted) {
      onShowToast("ক্যামেরা অনুমতি না দিলে নিচের বক্স থেকে কোড লিখে সার্চ করতে পারবেন।")
    }
  }

  var manualCodeInput by remember { mutableStateOf("") }
  var rawScannedValue by remember { mutableStateOf("") }
  var lookupResult by remember { mutableStateOf<AdminQrLookupResult?>(null) }
  var torchEnabled by remember { mutableStateOf(false) }
  var hasFlashUnit by remember { mutableStateOf(false) }
  var cameraErrorMessage by remember { mutableStateOf<String?>(null) }
  var boundCamera by remember { mutableStateOf<Camera?>(null) }

  val performLookup: (String, Boolean) -> Unit = { code, fromCamera ->
    val clean = code.trim()
    if (clean.isNotEmpty()) {
      rawScannedValue = clean
      val resolved = AdminQrLookupHelper.lookup(clean, products, orders)
      lookupResult = resolved
      if (fromCamera) {
        torchEnabled = false
        boundCamera?.cameraControl?.enableTorch(false)
      }
      when (resolved) {
        is AdminQrLookupResult.OrderMatch ->
          onShowToast("অর্ডার #${resolved.order.id} পাওয়া গেছে")
        is AdminQrLookupResult.ProductMatch ->
          onShowToast("প্রোডাক্ট পাওয়া গেছে: ${resolved.product.name}")
        is AdminQrLookupResult.NotFound ->
          onShowToast("কোড '$clean' এর সাথে কোনো পণ্য বা অর্ডার মেলেনি")
      }
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = Color.White,
      border = BorderStroke(1.2.dp, AdminCardBorderWarm),
      shadowElevation = 14.dp,
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .widthIn(max = 560.dp)
        .testTag("admin_qr_scanner_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        // Sky-Azure Header
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(adminRoyalGlossBrush())
            .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              modifier = Modifier.weight(1f),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(RoundedCornerShape(13.dp))
                  .background(Color.White.copy(alpha = 0.24f))
                  .border(1.dp, Color.White.copy(alpha = 0.55f), RoundedCornerShape(13.dp)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.QrCodeScanner,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(23.dp)
                )
              }
              Column {
                Text(
                  text = "অ্যাডমিন QR ও বারকোড স্ক্যানার",
                  fontSize = 15.5.sp,
                  fontWeight = FontWeight.Black,
                  color = Color.White
                )
                Text(
                  text = "প্রোডাক্ট SKU বা পার্সেল ট্র্যাকিং QR মুহূর্তেই দেখুন",
                  fontSize = 11.sp,
                  color = Color.White.copy(alpha = 0.92f)
                )
              }
            }

            IconButton(
              onClick = onDismiss,
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.22f))
                .testTag("admin_qr_close_button")
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "স্ক্যানার বন্ধ করুন",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }

        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // 1. Live Camera Scanner Viewport OR Result View
          if (lookupResult == null) {
            when {
              !hasCameraPermission -> {
                CameraPermissionPromptCard(
                  onRequestPermission = { permissionLauncher.launch(Manifest.permission.CAMERA) }
                )
              }
              cameraErrorMessage != null -> {
                CameraUnavailableCard(
                  message = cameraErrorMessage ?: "ক্যামেরা চালু করা যায়নি",
                  onRetry = { cameraErrorMessage = null }
                )
              }
              else -> {
                CameraQrViewfinder(
                  torchEnabled = torchEnabled,
                  hasFlashUnit = hasFlashUnit,
                  onToggleTorch = {
                    val next = !torchEnabled
                    torchEnabled = next
                    try {
                      boundCamera?.cameraControl?.enableTorch(next)
                    } catch (_: Exception) {
                    }
                  },
                  onCameraBound = { cam ->
                    boundCamera = cam
                    hasFlashUnit = try {
                      cam.cameraInfo.hasFlashUnit()
                    } catch (_: Exception) {
                      false
                    }
                  },
                  onCameraError = { msg -> cameraErrorMessage = msg },
                  onBarcodeDetected = { raw ->
                    mainExecutor.execute {
                      if (lookupResult == null) {
                        performLookup(raw, true)
                      }
                    }
                  }
                )
              }
            }
          }

          // 2. Lookup Result Card (when a QR / SKU / Tracking code has been scanned or entered)
          AnimatedVisibility(
            visible = lookupResult != null,
            enter = fadeIn(animationSpec = tween(220)),
            exit = fadeOut(animationSpec = tween(150))
          ) {
            val currentResult = lookupResult
            if (currentResult != null) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("admin_qr_result_card"),
                verticalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                when (currentResult) {
                  is AdminQrLookupResult.ProductMatch -> {
                    val liveProduct = products.find { it.id == currentResult.product.id }
                      ?: currentResult.product
                    ScannedProductResultCard(
                      product = liveProduct,
                      matchedBy = currentResult.matchedBy,
                      posItem = posInventory[liveProduct.id],
                      onEditProduct = { onEditProduct(liveProduct) },
                      onScanAgain = {
                        lookupResult = null
                        rawScannedValue = ""
                      }
                    )
                  }
                  is AdminQrLookupResult.OrderMatch -> {
                    val liveOrder = orders.find { it.id == currentResult.order.id }
                      ?: currentResult.order
                    ScannedOrderResultCard(
                      order = liveOrder,
                      matchedBy = currentResult.matchedBy,
                      onViewInvoice = { onViewInvoice(liveOrder) },
                      onOpenShippingLabel = { onOpenShippingLabel(liveOrder) },
                      onUpdateStatus = { newStatus ->
                        onUpdateOrderStatus(liveOrder.id, newStatus)
                      },
                      onScanAgain = {
                        lookupResult = null
                        rawScannedValue = ""
                      }
                    )
                  }
                  is AdminQrLookupResult.NotFound -> {
                    ScannedNotFoundCard(
                      rawCode = currentResult.rawCode,
                      onScanAgain = {
                        lookupResult = null
                        rawScannedValue = ""
                      }
                    )
                  }
                }
              }
            }
          }

          HorizontalDivider(color = AdminCardBorderSoft)

          // 3. Manual QR / SKU / Tracking ID Lookup Input + Quick-Pick Chips
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
              text = "ম্যানুয়াল কোড / SKU / অর্ডার / ট্র্যাকিং আইডি সার্চ",
              fontSize = 12.sp,
              fontWeight = FontWeight.ExtraBold,
              color = AdminInkPrimary
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              OutlinedTextField(
                value = manualCodeInput,
                onValueChange = { manualCodeInput = it },
                placeholder = {
                  Text(
                    "যেমন: SKU-101, #ORD-1001 বা ট্র্যাকিং কোড",
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = adminOutlinedTextFieldColors(),
                modifier = Modifier
                  .weight(1f)
                  .testTag("admin_qr_manual_input")
              )

              Button(
                onClick = { performLookup(manualCodeInput, false) },
                enabled = manualCodeInput.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                  containerColor = AdminV2CyanDeep,
                  contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                modifier = Modifier
                  .heightIn(min = 52.dp)
                  .testTag("admin_qr_lookup_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Search,
                  contentDescription = "কোড খুঁজুন",
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("খুঁজুন", fontSize = 12.5.sp, fontWeight = FontWeight.ExtraBold)
              }
            }

            if (products.isNotEmpty() || orders.isNotEmpty()) {
              Text(
                text = "দ্রুত টেস্ট / কুইক লুকআপ:",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = AdminInkMuted
              )
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                orders.take(2).forEach { order ->
                  val chipCode = order.trackingId.ifBlank { order.id }
                  Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AdminV2EmeraldSoft,
                    border = BorderStroke(1.dp, Color(0xFFA7F3D0)),
                    modifier = Modifier
                      .minimumInteractiveComponentSize()
                      .clip(RoundedCornerShape(12.dp))
                      .clickable {
                        manualCodeInput = chipCode
                        performLookup(chipCode, false)
                      }
                  ) {
                    Text(
                      text = "📦 #${order.id}",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF065F46),
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                    )
                  }
                }

                products.take(2).forEach { prod ->
                  val skuOrId = prod.sku.ifBlank { prod.id }
                  Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AdminV2SkySoft,
                    border = BorderStroke(1.dp, AdminCardBorderWarm),
                    modifier = Modifier
                      .minimumInteractiveComponentSize()
                      .clip(RoundedCornerShape(12.dp))
                      .clickable {
                        manualCodeInput = skuOrId
                        performLookup(skuOrId, false)
                      }
                  ) {
                    Text(
                      text = "🏷️ $skuOrId",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = AdminV2CyanDeep,
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                    )
                  }
                }
              }
            }

            if (rawScannedValue.isNotBlank()) {
              Text(
                text = "সর্বশেষ স্ক্যানকৃত ডাটা: ${rawScannedValue.take(72)}",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = AdminInkMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalGetImage::class)
@Composable
private fun CameraQrViewfinder(
  torchEnabled: Boolean,
  hasFlashUnit: Boolean,
  onToggleTorch: () -> Unit,
  onCameraBound: (Camera) -> Unit,
  onCameraError: (String) -> Unit,
  onBarcodeDetected: (String) -> Unit
) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current
  val previewView = remember {
    PreviewView(context).apply {
      implementationMode = PreviewView.ImplementationMode.COMPATIBLE
      scaleType = PreviewView.ScaleType.FILL_CENTER
    }
  }

  val infiniteTransition = rememberInfiniteTransition(label = "qr_scan_line")
  val scanLineProgress by infiniteTransition.animateFloat(
    initialValue = 0.15f,
    targetValue = 0.85f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1600),
      repeatMode = RepeatMode.Reverse
    ),
    label = "scan_line_y"
  )

  DisposableEffect(lifecycleOwner) {
    val analysisExecutor = Executors.newSingleThreadExecutor()
    val detectionSent = AtomicBoolean(false)
    val options = BarcodeScannerOptions.Builder()
      .setBarcodeFormats(
        Barcode.FORMAT_QR_CODE,
        Barcode.FORMAT_CODE_128,
        Barcode.FORMAT_CODE_39,
        Barcode.FORMAT_EAN_13,
        Barcode.FORMAT_EAN_8,
        Barcode.FORMAT_UPC_A,
        Barcode.FORMAT_UPC_E
      )
      .build()
    val barcodeScanner = BarcodeScanning.getClient(options)
    var cameraProvider: ProcessCameraProvider? = null

    try {
      val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
      cameraProviderFuture.addListener({
        try {
          val provider = cameraProviderFuture.get()
          cameraProvider = provider
          val preview = Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
          }
          val imageAnalysis = ImageAnalysis.Builder()
            .setResolutionSelector(ResolutionSelector.Builder().build())
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

          imageAnalysis.setAnalyzer(analysisExecutor) { imageProxy ->
            val mediaImage = imageProxy.image
            if (mediaImage != null && !detectionSent.get()) {
              val inputImage = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
              )
              barcodeScanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                  val raw = barcodes.firstOrNull { !it.rawValue.isNullOrBlank() }?.rawValue
                  if (!raw.isNullOrBlank() && detectionSent.compareAndSet(false, true)) {
                    onBarcodeDetected(raw)
                  }
                }
                .addOnCompleteListener {
                  imageProxy.close()
                }
            } else {
              imageProxy.close()
            }
          }

          val selector = if (provider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)) {
            CameraSelector.DEFAULT_BACK_CAMERA
          } else {
            CameraSelector.DEFAULT_FRONT_CAMERA
          }

          provider.unbindAll()
          val camera = provider.bindToLifecycle(
            lifecycleOwner,
            selector,
            preview,
            imageAnalysis
          )
          onCameraBound(camera)
        } catch (e: Exception) {
          onCameraError("ক্যামেরা প্রিভিউ চালু করা যায়নি: ${e.localizedMessage ?: "হার্ডওয়্যার পাওয়া যায়নি"}")
        }
      }, ContextCompat.getMainExecutor(context))
    } catch (e: Exception) {
      onCameraError("ক্যামেরা সেবা পাওয়া যায়নি: ${e.localizedMessage ?: "অনুপলব্ধ"}")
    }

    onDispose {
      try {
        cameraProvider?.unbindAll()
      } catch (_: Exception) {
      }
      try {
        barcodeScanner.close()
      } catch (_: Exception) {
      }
      analysisExecutor.shutdown()
    }
  }

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(236.dp)
      .clip(RoundedCornerShape(18.dp))
      .background(Color(0xFF0F2430))
      .border(1.5.dp, AdminV2Cyan, RoundedCornerShape(18.dp))
      .testTag("admin_qr_camera_preview")
  ) {
    AndroidView(
      factory = { previewView },
      modifier = Modifier.fillMaxSize()
    )

    // Viewfinder Reticle & Animated Cyan Laser
    Canvas(modifier = Modifier.fillMaxSize()) {
      val boxSize = minOf(size.width, size.height) * 0.62f
      val left = (size.width - boxSize) / 2f
      val top = (size.height - boxSize) / 2f
      val corner = boxSize * 0.18f
      val stroke = 5f
      val bracketColor = Color(0xFF38BDF8)

      // Top-left bracket
      drawLine(bracketColor, Offset(left, top), Offset(left + corner, top), stroke, StrokeCap.Round)
      drawLine(bracketColor, Offset(left, top), Offset(left, top + corner), stroke, StrokeCap.Round)
      // Top-right bracket
      drawLine(bracketColor, Offset(left + boxSize - corner, top), Offset(left + boxSize, top), stroke, StrokeCap.Round)
      drawLine(bracketColor, Offset(left + boxSize, top), Offset(left + boxSize, top + corner), stroke, StrokeCap.Round)
      // Bottom-left bracket
      drawLine(bracketColor, Offset(left, top + boxSize), Offset(left + corner, top + boxSize), stroke, StrokeCap.Round)
      drawLine(bracketColor, Offset(left, top + boxSize - corner), Offset(left, top + boxSize), stroke, StrokeCap.Round)
      // Bottom-right bracket
      drawLine(bracketColor, Offset(left + boxSize - corner, top + boxSize), Offset(left + boxSize, top + boxSize), stroke, StrokeCap.Round)
      drawLine(bracketColor, Offset(left + boxSize, top + boxSize - corner), Offset(left + boxSize, top + boxSize), stroke, StrokeCap.Round)

      // Subtle animated laser beam
      val laserY = top + boxSize * scanLineProgress
      drawRect(
        brush = Brush.horizontalGradient(
          colors = listOf(Color.Transparent, Color(0xFF38BDF8), Color.Transparent),
          startX = left + 8f,
          endX = left + boxSize - 8f
        ),
        topLeft = Offset(left + 8f, laserY - 2f),
        size = Size(boxSize - 16f, 4f)
      )
    }

    // Torch Toggle Button
    if (hasFlashUnit) {
      IconButton(
        onClick = onToggleTorch,
        modifier = Modifier
          .align(Alignment.TopEnd)
          .padding(8.dp)
          .size(48.dp)
          .clip(CircleShape)
          .background(Color.Black.copy(alpha = 0.45f))
          .testTag("admin_qr_torch_button")
      ) {
        Icon(
          imageVector = if (torchEnabled) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
          contentDescription = if (torchEnabled) "ফ্ল্যাশ বন্ধ করুন" else "ফ্ল্যাশ চালু করুন",
          tint = if (torchEnabled) Color(0xFFFDE047) else Color.White,
          modifier = Modifier.size(20.dp)
        )
      }
    }

    Surface(
      shape = RoundedCornerShape(50),
      color = Color.Black.copy(alpha = 0.62f),
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 10.dp)
    ) {
      Text(
        text = "QR কোড বা বারকোড ফ্রেমের ভেতর ধরুন",
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.White,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
      )
    }
  }
}

@Composable
private fun CameraPermissionPromptCard(
  onRequestPermission: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(18.dp),
    color = AdminV2IceSurface,
    border = BorderStroke(1.dp, AdminCardBorderWarm),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Box(
        modifier = Modifier
          .size(54.dp)
          .clip(CircleShape)
          .background(AdminV2SkySoft),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.CameraAlt,
          contentDescription = null,
          tint = AdminV2CyanDeep,
          modifier = Modifier.size(26.dp)
        )
      }
      Text(
        text = "লাইভ QR স্ক্যান করতে ক্যামেরা অনুমতি প্রয়োজন",
        fontSize = 13.5.sp,
        fontWeight = FontWeight.ExtraBold,
        color = AdminInkPrimary
      )
      Text(
        text = "প্রোডাক্ট বারকোড বা পার্সেল শিপিং লেবেলের QR স্ক্যান করতে ক্যামেরা অ্যাক্সেস দিন, অথবা নিচের বক্সে সরাসরি কোড লিখুন।",
        fontSize = 11.5.sp,
        color = AdminInkSecondary,
        lineHeight = 16.sp
      )
      Button(
        onClick = onRequestPermission,
        colors = ButtonDefaults.buttonColors(
          containerColor = AdminV2CyanDeep,
          contentColor = Color.White
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .heightIn(min = 48.dp)
          .testTag("admin_qr_grant_permission_button")
      ) {
        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("ক্যামেরা চালু করুন", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}

@Composable
private fun CameraUnavailableCard(
  message: String,
  onRetry: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(18.dp),
    color = Color(0xFFFFFBEB),
    border = BorderStroke(1.dp, Color(0xFFFDE68A)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Text(
        text = "ক্যামেরা প্রিভিউ অনুপলব্ধ",
        fontSize = 13.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color(0xFF92400E)
      )
      Text(
        text = "$message। নিচের সার্চ বক্স ব্যবহার করে যেকোনো SKU, অর্ডার ID বা ট্র্যাকিং কোড খুঁজতে পারবেন।",
        fontSize = 11.5.sp,
        color = Color(0xFF78350F)
      )
      OutlinedButton(
        onClick = onRetry,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.heightIn(min = 48.dp)
      ) {
        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("পুনরায় চেষ্টা করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}

@Composable
private fun ScannedProductResultCard(
  product: Product,
  matchedBy: String,
  posItem: PosInventoryItem?,
  onEditProduct: () -> Unit,
  onScanAgain: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(18.dp),
    color = AdminV2IceSurface,
    border = BorderStroke(1.2.dp, AdminV2Cyan.copy(alpha = 0.6f)),
    shadowElevation = 4.dp,
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(50),
          color = AdminV2SkySoft,
          border = BorderStroke(1.dp, AdminCardBorderWarm)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = AdminV2CyanDeep,
              modifier = Modifier.size(14.dp)
            )
            Text(
              text = "প্রোডাক্ট ম্যাচ • $matchedBy",
              fontSize = 10.5.sp,
              fontWeight = FontWeight.ExtraBold,
              color = AdminV2CyanDeep
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (product.stock > 5) AdminV2EmeraldSoft else Color(0xFFFEE2E2)
        ) {
          Text(
            text = if (product.stock > 0) "স্টক: ${product.stock} পিস" else "স্টক শেষ",
            fontSize = 10.5.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (product.stock > 5) AdminV2Emerald else AdminV2Rose,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        AppImage(
          model = product.imageUrl,
          contentDescription = product.name,
          modifier = Modifier
            .size(66.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(1.dp, AdminCardBorderSoft, RoundedCornerShape(14.dp))
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
          Text(
            text = product.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            color = AdminInkPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = "SKU: ${product.sku.ifBlank { product.id }} • ক্যাটাগরি: ${product.category}",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = AdminInkSecondary
          )
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              text = "৳${product.price}",
              fontSize = 15.sp,
              fontWeight = FontWeight.Black,
              color = AdminV2CyanDeep
            )
            if (product.regularPrice > product.price) {
              Text(
                text = "৳${product.regularPrice}",
                fontSize = 11.5.sp,
                color = AdminInkMuted,
                textDecoration = TextDecoration.LineThrough
              )
            }
          }
        }
      }

      if (posItem != null || product.sizes.isNotEmpty() || product.colors.isNotEmpty()) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color.White,
          border = BorderStroke(1.dp, AdminCardBorderSoft),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
          ) {
            if (posItem != null) {
              Text(
                text = "POS স্টক: ${posItem.stock} পিস • অনলাইন স্টক: ${product.stock} পিস",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = AdminInkPrimary
              )
            }
            if (product.colors.isNotEmpty()) {
              Text(
                text = "রং: ${product.colors.joinToString(", ")}",
                fontSize = 10.5.sp,
                color = AdminInkSecondary
              )
            }
            if (product.sizes.isNotEmpty()) {
              Text(
                text = "সাইজ: ${product.sizes.joinToString(", ")}",
                fontSize = 10.5.sp,
                color = AdminInkSecondary
              )
            }
          }
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = onEditProduct,
          colors = ButtonDefaults.buttonColors(
            containerColor = AdminV2CyanDeep,
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .weight(1f)
            .heightIn(min = 48.dp)
            .testTag("admin_qr_edit_product_button")
        ) {
          Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("প্রোডাক্ট এডিট", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
          onClick = onScanAgain,
          shape = RoundedCornerShape(12.dp),
          border = BorderStroke(1.dp, AdminV2CyanDeep),
          modifier = Modifier
            .heightIn(min = 48.dp)
            .testTag("admin_qr_rescan_button")
        ) {
          Icon(
            imageVector = Icons.Default.QrCodeScanner,
            contentDescription = null,
            tint = AdminV2CyanDeep,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text("আবার স্ক্যান", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = AdminV2CyanDeep)
        }
      }
    }
  }
}

@Composable
private fun ScannedOrderResultCard(
  order: Order,
  matchedBy: String,
  onViewInvoice: () -> Unit,
  onOpenShippingLabel: () -> Unit,
  onUpdateStatus: (String) -> Unit,
  onScanAgain: () -> Unit
) {
  val statusBg = when (order.status.lowercase()) {
    "delivered" -> AdminV2EmeraldSoft
    "shipped" -> AdminV2SkySoft
    "confirmed" -> Color(0xFFDBEAFE)
    "cancelled" -> Color(0xFFFEE2E2)
    else -> AdminV2AmberSoft
  }
  val statusInk = when (order.status.lowercase()) {
    "delivered" -> Color(0xFF065F46)
    "shipped" -> AdminV2CyanDeep
    "confirmed" -> Color(0xFF1D4ED8)
    "cancelled" -> AdminV2Rose
    else -> Color(0xFF92400E)
  }

  Surface(
    shape = RoundedCornerShape(18.dp),
    color = Color(0xFFF6FEFB),
    border = BorderStroke(1.2.dp, AdminV2Emerald.copy(alpha = 0.5f)),
    shadowElevation = 4.dp,
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(50),
          color = AdminV2EmeraldSoft,
          border = BorderStroke(1.dp, Color(0xFFA7F3D0))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
          ) {
            Icon(
              imageVector = Icons.Default.LocalShipping,
              contentDescription = null,
              tint = AdminV2Emerald,
              modifier = Modifier.size(14.dp)
            )
            Text(
              text = matchedBy,
              fontSize = 10.5.sp,
              fontWeight = FontWeight.ExtraBold,
              color = Color(0xFF065F46)
            )
          }
        }

        Surface(shape = RoundedCornerShape(8.dp), color = statusBg) {
          Text(
            text = order.status,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            color = statusInk,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
          )
        }
      }

      Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
          text = "অর্ডার #${order.id} • ৳${order.total}",
          fontSize = 15.5.sp,
          fontWeight = FontWeight.Black,
          color = AdminInkPrimary
        )
        Text(
          text = "${order.customerName} (${order.phone})",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = AdminInkSecondary
        )
        Text(
          text = "${order.address}, ${order.thana}, ${order.district}",
          fontSize = 11.sp,
          color = AdminInkMuted,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )
      }

      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, AdminCardBorderSoft),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(10.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "কুরিয়ার ট্র্যাকিং:",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = AdminInkSecondary
            )
            Text(
              text = order.trackingId.ifBlank { "অপেক্ষমাণ (Pending Dispatch)" },
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.ExtraBold,
              color = if (order.trackingId.isNotBlank()) AdminV2CyanDeep else AdminInkMuted
            )
          }
          Text(
            text = "পেমেন্ট: ${order.paymentMethod} • তারিখ: ${order.date}",
            fontSize = 10.5.sp,
            color = AdminInkMuted
          )
          val itemSummary = order.items.take(2).joinToString(", ") { "${it.name} ×${it.quantity}" }
          val extra = if (order.items.size > 2) " +${order.items.size - 2} টি" else ""
          if (itemSummary.isNotBlank()) {
            Text(
              text = "পণ্য: $itemSummary$extra",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = AdminInkPrimary
            )
          }
        }
      }

      // Quick Status & Document Actions
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = onViewInvoice,
          colors = ButtonDefaults.buttonColors(
            containerColor = AdminV2CyanDeep,
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(12.dp),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
          modifier = Modifier
            .weight(1f)
            .heightIn(min = 48.dp)
            .testTag("admin_qr_view_invoice_button")
        ) {
          Icon(imageVector = Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(5.dp))
          Text("চালান", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
        }

        Button(
          onClick = onOpenShippingLabel,
          colors = ButtonDefaults.buttonColors(
            containerColor = AdminV2Emerald,
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(12.dp),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
          modifier = Modifier
            .weight(1f)
            .heightIn(min = 48.dp)
            .testTag("admin_qr_shipping_label_button")
        ) {
          Icon(imageVector = Icons.Default.QrCode2, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(5.dp))
          Text("লেবেল", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        val nextStatus = when (order.status.lowercase()) {
          "pending" -> "Confirmed" to "কনফার্ম করুন"
          "confirmed" -> "Shipped" to "Shipped করুন"
          "shipped" -> "Delivered" to "Delivered করুন"
          else -> null
        }
        if (nextStatus != null) {
          OutlinedButton(
            onClick = { onUpdateStatus(nextStatus.first) },
            border = BorderStroke(1.dp, AdminV2Emerald),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .weight(1f)
              .heightIn(min = 48.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Inventory2,
              contentDescription = null,
              tint = AdminV2Emerald,
              modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
              text = nextStatus.second,
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Bold,
              color = AdminV2Emerald
            )
          }
        }

        OutlinedButton(
          onClick = onScanAgain,
          border = BorderStroke(1.dp, AdminV2CyanDeep),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .heightIn(min = 48.dp)
            .testTag("admin_qr_rescan_button")
        ) {
          Icon(
            imageVector = Icons.Default.QrCodeScanner,
            contentDescription = null,
            tint = AdminV2CyanDeep,
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text("আবার স্ক্যান", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = AdminV2CyanDeep)
        }
      }
    }
  }
}

@Composable
private fun ScannedNotFoundCard(
  rawCode: String,
  onScanAgain: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(18.dp),
    color = Color(0xFFFEF2F2),
    border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Icon(
          imageVector = Icons.Default.ErrorOutline,
          contentDescription = null,
          tint = AdminV2Rose,
          modifier = Modifier.size(20.dp)
        )
        Text(
          text = "কোনো প্রোডাক্ট বা অর্ডার পাওয়া যায়নি",
          fontSize = 13.5.sp,
          fontWeight = FontWeight.ExtraBold,
          color = Color(0xFF991B1B)
        )
      }
      Text(
        text = "স্ক্যানকৃত কোড: $rawCode",
        fontSize = 11.5.sp,
        fontFamily = FontFamily.Monospace,
        color = Color(0xFF7F1D1D)
      )
      OutlinedButton(
        onClick = onScanAgain,
        border = BorderStroke(1.dp, AdminV2Rose),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .heightIn(min = 48.dp)
          .testTag("admin_qr_rescan_button")
      ) {
        Icon(
          imageVector = Icons.Default.QrCodeScanner,
          contentDescription = null,
          tint = AdminV2Rose,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text("আবার স্ক্যান করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AdminV2Rose)
      }
    }
  }
}
