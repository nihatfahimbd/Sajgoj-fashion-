package com.example.ui.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.print.pdf.PrintedPdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas as ComposeCanvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Order
import com.example.data.model.StoreConfig
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.DarkEspresso
import com.example.ui.theme.RoyalMaroon
import com.example.util.QrCodeGenerator
import org.json.JSONObject
import java.io.FileOutputStream

enum class ShippingLabelPrintFormat(val labelBn: String) {
  THERMAL_4X6("4\"×6\" থার্মাল স্টিকার"),
  A4_SHEET("A4 পার্সেল শিট")
}

fun buildShippingQrPayload(order: Order, config: StoreConfig): String {
  val tracking = order.trackingId.ifBlank { order.id }
  val full = JSONObject().apply {
    put("type", "SAJGOJ_SHIPPING_LABEL")
    put("orderId", order.id)
    put("tracking", tracking)
    put("customer", order.customerName.take(28))
    put("phone", order.phone)
    put("district", order.district.take(20))
    put("total", order.total)
    put("payment", order.paymentMethod.take(18))
    put("status", order.status)
    put("store", config.storeName.take(20))
  }.toString()

  if (full.toByteArray(Charsets.UTF_8).size <= 178) return full

  return JSONObject().apply {
    put("type", "SAJGOJ_SHIPPING_LABEL")
    put("orderId", order.id)
    put("tracking", tracking)
    put("customer", order.customerName.take(18))
    put("phone", order.phone)
    put("district", order.district.take(16))
    put("total", order.total)
    put("status", order.status)
  }.toString()
}

private fun isCodOrder(order: Order): Boolean {
  val pm = order.paymentMethod.lowercase()
  return pm.contains("cash") || pm.contains("cod") || pm.contains("ক্যাশ")
}

private tailrec fun Context.findHostActivity(): Activity? = when (this) {
  is Activity -> this
  is ContextWrapper -> baseContext.findHostActivity()
  else -> null
}

fun printShippingLabels(
  context: Context,
  orders: List<Order>,
  config: StoreConfig,
  format: ShippingLabelPrintFormat = ShippingLabelPrintFormat.THERMAL_4X6,
  onShowToast: (String) -> Unit = {}
) {
  if (orders.isEmpty()) {
    onShowToast("প্রিন্ট করার জন্য কোনো Shipped অর্ডার নেই!")
    return
  }

  val activity = context.findHostActivity()
  val printManager = (activity ?: context).getSystemService(Context.PRINT_SERVICE) as? PrintManager
  if (printManager == null) {
    onShowToast("এই ডিভাইসে প্রিন্ট সার্ভিস পাওয়া যায়নি।")
    return
  }

  val mediaSize = when (format) {
    ShippingLabelPrintFormat.THERMAL_4X6 ->
      PrintAttributes.MediaSize("SAJGOJ_4X6", "4x6 Parcel Label", 4000, 6000)
    ShippingLabelPrintFormat.A4_SHEET ->
      PrintAttributes.MediaSize.ISO_A4
  }

  val attributes = PrintAttributes.Builder()
    .setMediaSize(mediaSize)
    .setColorMode(PrintAttributes.COLOR_MODE_MONOCHROME)
    .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
    .build()

  val jobName = if (orders.size == 1) {
    "Shipping_Label_${orders.first().id}"
  } else {
    "Shipping_Labels_Batch_${orders.size}"
  }

  val adapter = ShippingLabelPrintAdapter(
    context = activity ?: context,
    orders = orders,
    config = config
  )

  try {
    printManager.print(jobName, adapter, attributes)
  } catch (e: Exception) {
    onShowToast("প্রিন্ট ডায়ালগ চালু করতে সমস্যা হয়েছে: ${e.message ?: "Unknown"}")
  }
}

fun shareShippingLabelText(context: Context, order: Order, config: StoreConfig) {
  val tracking = order.trackingId.ifBlank { order.id }
  val codLine = if (isCodOrder(order)) "COD আদায়যোগ্য: ${order.total} ৳" else "PREPAID / PAID (${order.paymentMethod})"
  val itemsSummary = order.items.joinToString("\n") { "• ${it.name} × ${it.quantity}" }
  val text = """
    🏷️ PARCEL SHIPPING LABEL - ${config.storeName}
    ==============================
    অর্ডার আইডি: #${order.id}
    ট্র্যাকিং কোড: $tracking
    স্ট্যাটাস: ${order.status}
    তারিখ: ${order.date}

    📍 প্রাপক (TO / CUSTOMER):
    নাম: ${order.customerName}
    মোবাইল: ${order.phone}
    ঠিকানা: ${order.address}${if (order.thana.isNotBlank()) ", ${order.thana}" else ""}${if (order.district.isNotBlank()) ", ${order.district}" else ""}

    💰 $codLine

    📦 পার্সেলের পণ্যসমূহ (${order.items.sumOf { it.quantity }} পিস):
    $itemsSummary

    📤 প্রেরক (FROM):
    ${config.storeName} • হটলাইন: ${config.phone}
    ${config.address}
    ==============================
  """.trimIndent()

  val intent = Intent(Intent.ACTION_SEND).apply {
    type = "text/plain"
    putExtra(Intent.EXTRA_SUBJECT, "Shipping Label #${order.id}")
    putExtra(Intent.EXTRA_TEXT, text)
  }
  context.startActivity(Intent.createChooser(intent, "শিপিং লেবেল শেয়ার করুন"))
}

private class ShippingLabelPrintAdapter(
  private val context: Context,
  private val orders: List<Order>,
  private val config: StoreConfig
) : PrintDocumentAdapter() {

  private var pdfDocument: PrintedPdfDocument? = null
  private var pageCount = 0

  override fun onLayout(
    oldAttributes: PrintAttributes?,
    newAttributes: PrintAttributes,
    cancellationSignal: CancellationSignal,
    callback: LayoutResultCallback,
    extras: Bundle?
  ) {
    pdfDocument = PrintedPdfDocument(context, newAttributes)
    if (cancellationSignal.isCanceled) {
      callback.onLayoutCancelled()
      return
    }
    pageCount = orders.size
    if (pageCount > 0) {
      val info = PrintDocumentInfo.Builder("shipping_labels.pdf")
        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
        .setPageCount(pageCount)
        .build()
      callback.onLayoutFinished(info, true)
    } else {
      callback.onLayoutFailed("No shipped orders available for printing")
    }
  }

  override fun onWrite(
    pages: Array<out PageRange>,
    destination: ParcelFileDescriptor,
    cancellationSignal: CancellationSignal,
    callback: WriteResultCallback
  ) {
    val doc = pdfDocument ?: run {
      callback.onWriteFailed("PDF document not initialized")
      return
    }

    try {
      for (i in 0 until pageCount) {
        if (!isPageInRange(pages, i)) continue
        if (cancellationSignal.isCanceled) {
          callback.onWriteCancelled()
          return
        }
        val page = doc.startPage(i)
        drawShippingLabelOnPdfCanvas(page.canvas, orders[i], config)
        doc.finishPage(page)
      }
      FileOutputStream(destination.fileDescriptor).use { out ->
        doc.writeTo(out)
      }
      callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
    } catch (e: Exception) {
      callback.onWriteFailed(e.message)
    } finally {
      doc.close()
      pdfDocument = null
    }
  }

  override fun onFinish() {
    pdfDocument?.close()
    pdfDocument = null
  }

  private fun isPageInRange(ranges: Array<out PageRange>, page: Int): Boolean {
    return ranges.any { page >= it.start && page <= it.end }
  }
}

private fun drawShippingLabelOnPdfCanvas(canvas: Canvas, order: Order, config: StoreConfig) {
  val pageW = canvas.width.toFloat()
  val pageH = canvas.height.toFloat()
  val margin = (pageW * 0.055f).coerceIn(14f, 36f)
  val cardLeft = margin
  val cardTop = margin
  val cardRight = pageW - margin
  val cardWidth = cardRight - cardLeft
  val maxCardBottom = pageH - margin

  val scale = (cardWidth / 260f).coerceIn(0.8f, 2.2f)

  val whitePaint = Paint().apply { color = AndroidColor.WHITE }
  canvas.drawRect(0f, 0f, pageW, pageH, whitePaint)

  val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    style = Paint.Style.STROKE
    strokeWidth = 1.6f * scale
    color = AndroidColor.BLACK
    pathEffect = DashPathEffect(floatArrayOf(6f * scale, 4f * scale), 0f)
  }

  val solidBoxPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    style = Paint.Style.STROKE
    strokeWidth = 1.1f * scale
    color = AndroidColor.BLACK
  }

  val blackFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    style = Paint.Style.FILL
    color = AndroidColor.BLACK
  }

  val lightGrayFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    style = Paint.Style.FILL
    color = AndroidColor.rgb(244, 244, 245)
  }

  val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
    color = AndroidColor.BLACK
    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    textSize = 13f * scale
  }
  val subtitlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
    color = AndroidColor.DKGRAY
    textSize = 7.5f * scale
  }
  val labelHeaderPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
    color = AndroidColor.BLACK
    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    textSize = 8f * scale
  }
  val bodyBoldPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
    color = AndroidColor.BLACK
    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    textSize = 10f * scale
  }
  val bodyPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
    color = AndroidColor.BLACK
    textSize = 8.5f * scale
  }
  val whiteBoldPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
    color = AndroidColor.WHITE
    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    textSize = 9f * scale
  }

  val innerPad = 10f * scale
  var y = cardTop + innerPad
  val contentLeft = cardLeft + innerPad
  val contentWidth = cardWidth - innerPad * 2

  // 1. Header Bar
  val headerHeight = 32f * scale
  canvas.drawRect(contentLeft, y, contentLeft + contentWidth, y + headerHeight, blackFillPaint)
  val storeHeaderPaint = TextPaint(whiteBoldPaint).apply { textSize = 11.5f * scale }
  canvas.drawText(config.storeName.uppercase(), contentLeft + 8f * scale, y + 14f * scale, storeHeaderPaint)
  val subWhitePaint = TextPaint(whiteBoldPaint).apply { textSize = 7.5f * scale; typeface = Typeface.DEFAULT }
  canvas.drawText("PARCEL SHIPPING LABEL  |  হটলাইন: ${config.phone}", contentLeft + 8f * scale, y + 25f * scale, subWhitePaint)

  val statusText = order.status.uppercase()
  val statusW = whiteBoldPaint.measureText(statusText) + 12f * scale
  val statusRect = RectF(contentLeft + contentWidth - statusW - 6f * scale, y + 7f * scale, contentLeft + contentWidth - 6f * scale, y + 24f * scale)
  val badgeBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    style = Paint.Style.STROKE
    strokeWidth = 1f * scale
    color = AndroidColor.WHITE
  }
  canvas.drawRoundRect(statusRect, 4f * scale, 4f * scale, badgeBorderPaint)
  canvas.drawText(statusText, statusRect.left + 6f * scale, statusRect.bottom - 5f * scale, TextPaint(whiteBoldPaint).apply { textSize = 7.5f * scale })

  y += headerHeight + 8f * scale

  // 2. Order ID, Tracking & District Routing Box
  val tracking = order.trackingId.ifBlank { order.id }
  val districtTag = order.district.ifBlank { "BANGLADESH" }.uppercase()
  val metaRowH = 30f * scale
  canvas.drawRect(contentLeft, y, contentLeft + contentWidth, y + metaRowH, lightGrayFillPaint)
  canvas.drawRect(contentLeft, y, contentLeft + contentWidth, y + metaRowH, solidBoxPaint)

  canvas.drawText("ORDER: #${order.id}", contentLeft + 7f * scale, y + 12f * scale, bodyBoldPaint.apply { textSize = 9f * scale })
  canvas.drawText("TRACKING: $tracking   |   DATE: ${order.date}", contentLeft + 7f * scale, y + 23f * scale, subtitlePaint)

  val distPaint = TextPaint(whiteBoldPaint).apply { textSize = 8.5f * scale }
  val distW = (distPaint.measureText(districtTag) + 14f * scale).coerceAtMost(contentWidth * 0.4f)
  val distRect = RectF(contentLeft + contentWidth - distW - 5f * scale, y + 5f * scale, contentLeft + contentWidth - 5f * scale, y + metaRowH - 5f * scale)
  canvas.drawRoundRect(distRect, 4f * scale, 4f * scale, blackFillPaint)
  canvas.drawText(districtTag, distRect.left + 7f * scale, distRect.bottom - 6f * scale, distPaint)

  y += metaRowH + 8f * scale

  // 3. Recipient ("TO") + QR Code Row
  val qrBoxSize = (contentWidth * 0.34f).coerceAtMost(104f * scale)
  val toBoxWidth = contentWidth - qrBoxSize - 8f * scale
  val fullAddress = listOf(order.address, order.thana, order.district).filter { it.isNotBlank() }.joinToString(", ")

  val toText = "TO / প্রাপক:\n${order.customerName}\n📞 ${order.phone}\n📍 $fullAddress"
  val toLayout = StaticLayout.Builder.obtain(toText, 0, toText.length, bodyPaint.apply { textSize = 8.5f * scale }, (toBoxWidth - 12f * scale).toInt().coerceAtLeast(60))
    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
    .setLineSpacing(2f * scale, 1.05f)
    .build()

  val sectionH = maxOf(qrBoxSize + 14f * scale, toLayout.height + 16f * scale)
  canvas.drawRect(contentLeft, y, contentLeft + toBoxWidth, y + sectionH, solidBoxPaint)
  canvas.save()
  canvas.translate(contentLeft + 6f * scale, y + 6f * scale)
  toLayout.draw(canvas)
  canvas.restore()

  // QR Box on right
  val qrLeft = contentLeft + toBoxWidth + 8f * scale
  canvas.drawRect(qrLeft, y, qrLeft + qrBoxSize, y + sectionH, solidBoxPaint)
  val qrPayload = buildShippingQrPayload(order, config)
  val qrBmp = QrCodeGenerator.encode(qrPayload).toBitmap(pixelSize = 320, quietZoneModules = 2)
  val qrDrawSize = qrBoxSize - 8f * scale
  val qrDst = RectF(qrLeft + 4f * scale, y + 4f * scale, qrLeft + 4f * scale + qrDrawSize, y + 4f * scale + qrDrawSize)
  val bmpPaint = Paint().apply { isFilterBitmap = false }
  canvas.drawBitmap(qrBmp, Rect(0, 0, qrBmp.width, qrBmp.height), qrDst, bmpPaint)
  val scanLabel = "SCAN PARCEL QR"
  val scanPaint = TextPaint(subtitlePaint).apply { textSize = 6.2f * scale; typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD) }
  val scanW = scanPaint.measureText(scanLabel)
  canvas.drawText(scanLabel, qrLeft + (qrBoxSize - scanW) / 2f, y + sectionH - 4f * scale, scanPaint)

  y += sectionH + 8f * scale

  // 4. COD / Payment Banner
  val codText = if (isCodOrder(order)) "COD AMOUNT TO COLLECT: ৳${order.total}" else "PREPAID (${order.paymentMethod}) • COD: ৳0"
  val codBoxH = 24f * scale
  canvas.drawRect(contentLeft, y, contentLeft + contentWidth, y + codBoxH, lightGrayFillPaint)
  canvas.drawRect(contentLeft, y, contentLeft + contentWidth, y + codBoxH, solidBoxPaint)
  val codPaint = TextPaint(titlePaint).apply { textSize = 9.5f * scale }
  val codW = codPaint.measureText(codText)
  canvas.drawText(codText, contentLeft + (contentWidth - codW) / 2f, y + 15.5f * scale, codPaint)

  y += codBoxH + 8f * scale

  // 5. Parcel Contents Summary
  val totalPieces = order.items.sumOf { it.quantity }
  val itemsSummary = order.items.take(4).joinToString("\n") { item ->
    val variant = listOfNotNull(
      item.color.takeIf { it.isNotBlank() },
      item.size.takeIf { it.isNotBlank() }
    ).joinToString("/")
    "• ${item.name}${if (variant.isNotBlank()) " ($variant)" else ""} × ${item.quantity}"
  } + if (order.items.size > 4) "\n+ আরও ${order.items.size - 4} টি আইটেম" else ""

  val itemsBlockText = "PARCEL ITEMS ($totalPieces পিস):\n$itemsSummary"
  val itemsLayout = StaticLayout.Builder.obtain(itemsBlockText, 0, itemsBlockText.length, subtitlePaint.apply { color = AndroidColor.BLACK; textSize = 7.8f * scale }, (contentWidth - 12f * scale).toInt().coerceAtLeast(60))
    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
    .setLineSpacing(1.5f * scale, 1.05f)
    .build()

  val itemsBoxH = (itemsLayout.height + 12f * scale).coerceAtMost((maxCardBottom - y - 40f * scale).coerceAtLeast(28f * scale))
  canvas.drawRect(contentLeft, y, contentLeft + contentWidth, y + itemsBoxH, solidBoxPaint)
  canvas.save()
  canvas.translate(contentLeft + 6f * scale, y + 6f * scale)
  itemsLayout.draw(canvas)
  canvas.restore()

  y += itemsBoxH + 8f * scale

  // 6. Sender ("FROM") Footer
  val senderText = "FROM: ${config.storeName} | ${config.address} | হটলাইন: ${config.phone}"
  val senderLayout = StaticLayout.Builder.obtain(senderText, 0, senderText.length, subtitlePaint.apply { textSize = 7f * scale; color = AndroidColor.DKGRAY }, contentWidth.toInt().coerceAtLeast(60))
    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
    .build()
  canvas.save()
  canvas.translate(contentLeft, y)
  senderLayout.draw(canvas)
  canvas.restore()

  y += senderLayout.height + innerPad
  val finalBottom = y.coerceAtMost(maxCardBottom)

  // Outer dashed cut border around sticker
  canvas.drawRoundRect(RectF(cardLeft, cardTop, cardRight, finalBottom), 8f * scale, 8f * scale, borderPaint)
}

@Composable
fun ShippingLabelQrCode(
  payload: String,
  modifier: Modifier = Modifier
) {
  val qrMatrix = remember(payload) { QrCodeGenerator.encode(payload) }
  ComposeCanvas(
    modifier = modifier
      .background(Color.White)
      .testTag("shipping_label_qr_code")
  ) {
    val quiet = 2
    val totalModules = qrMatrix.size + quiet * 2
    val cellSize = minOf(size.width, size.height) / totalModules.toFloat()
    val offsetX = (size.width - cellSize * totalModules) / 2f
    val offsetY = (size.height - cellSize * totalModules) / 2f

    drawRect(color = Color.White, size = size)

    for (my in 0 until qrMatrix.size) {
      for (mx in 0 until qrMatrix.size) {
        if (qrMatrix.get(mx, my)) {
          drawRect(
            color = Color.Black,
            topLeft = Offset(
              x = offsetX + (mx + quiet) * cellSize,
              y = offsetY + (my + quiet) * cellSize
            ),
            size = Size(cellSize + 0.35f, cellSize + 0.35f)
          )
        }
      }
    }
  }
}

@Composable
fun ShippingLabelDialog(
  order: Order,
  config: StoreConfig,
  onDismiss: () -> Unit,
  onShowToast: (String) -> Unit = {}
) {
  val context = LocalContext.current
  var selectedFormat by remember { mutableStateOf(ShippingLabelPrintFormat.THERMAL_4X6) }
  val qrPayload = remember(order, config) { buildShippingQrPayload(order, config) }
  val trackingCode = order.trackingId.ifBlank { order.id }
  val isCod = isCodOrder(order)
  val totalPieces = remember(order) { order.items.sumOf { it.quantity } }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = Color.White,
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .padding(vertical = 16.dp)
        .testTag("shipping_label_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        // Modal Header
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(RoyalMaroon)
            .padding(16.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(end = 36.dp)
          ) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White.copy(alpha = 0.18f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.LocalShipping,
                contentDescription = "শিপিং লেবেল",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }
            Column {
              Text(
                text = "পার্সেল শিপিং লেবেল",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
              Text(
                text = "প্রিন্ট করে পার্সেলের গায়ে লাগানোর জন্য প্রস্তুত",
                fontSize = 11.sp,
                color = CreamSurface
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .align(Alignment.TopEnd)
              .minimumInteractiveComponentSize()
              .clip(CircleShape)
              .background(Color.White.copy(alpha = 0.2f))
              .testTag("close_shipping_label_dialog")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "বন্ধ করুন",
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Column(
          modifier = Modifier.padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // Format Selector Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            ShippingLabelPrintFormat.entries.forEach { fmt ->
              val isSelected = selectedFormat == fmt
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) RoyalMaroon else CreamSurface,
                border = BorderStroke(1.dp, if (isSelected) RoyalMaroon else Color(0xFFE5D4C0)),
                modifier = Modifier
                  .weight(1f)
                  .heightIn(min = 48.dp)
                  .testTag("label_format_${fmt.name}")
                  .clickable { selectedFormat = fmt }
              ) {
                Box(
                  contentAlignment = Alignment.Center,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp)
                ) {
                  Text(
                    text = fmt.labelBn,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else DarkEspresso,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                }
              }
            }
          }

          // Crisp Black-and-White Parcel Sticker Preview Card
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color.White, RoundedCornerShape(14.dp))
              .border(
                width = 1.5.dp,
                color = Color(0xFF111827),
                shape = RoundedCornerShape(14.dp)
              )
              .padding(12.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              // Sticker Top Banner
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0xFF111827))
                  .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = config.storeName.uppercase(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                  )
                  Text(
                    text = "PARCEL SHIPPING LABEL • ${config.phone}",
                    fontSize = 9.5.sp,
                    color = Color(0xFFE5E7EB)
                  )
                }
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = Color.White
                ) {
                  Text(
                    text = order.status.uppercase(),
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF111827),
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                  )
                }
              }

              // Order ID, Tracking & District Routing Stamp
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0xFFF3F4F6))
                  .border(1.dp, Color(0xFFD1D5DB), RoundedCornerShape(8.dp))
                  .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "ORDER #${order.id}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = Color.Black
                  )
                  Text(
                    text = "TRK: $trackingCode  •  ${order.date}",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF374151)
                  )
                }
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Text(
                    text = order.district.ifBlank { "BD" }.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                  )
                }
              }

              // Recipient Info + Scannable QR Code Side-by-Side
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
              ) {
                Column(
                  modifier = Modifier
                    .weight(1.4f)
                    .border(1.dp, Color(0xFF111827), RoundedCornerShape(10.dp))
                    .padding(10.dp),
                  verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                  Text(
                    text = "TO / প্রাপক (CUSTOMER):",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF374151)
                  )
                  Text(
                    text = order.customerName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                  )
                  Text(
                    text = "📞 ${order.phone}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = Color.Black
                  )
                  val fullAddr = listOf(order.address, order.thana, order.district)
                    .filter { it.isNotBlank() }
                    .joinToString(", ")
                  Text(
                    text = "📍 $fullAddr",
                    fontSize = 11.sp,
                    color = Color(0xFF111827),
                    lineHeight = 15.sp
                  )
                }

                Column(
                  modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color(0xFF111827), RoundedCornerShape(10.dp))
                    .padding(6.dp),
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  ShippingLabelQrCode(
                    payload = qrPayload,
                    modifier = Modifier.size(104.dp)
                  )
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.QrCode2,
                      contentDescription = null,
                      tint = Color.Black,
                      modifier = Modifier.size(12.dp)
                    )
                    Text(
                      text = "SCAN QR",
                      fontSize = 8.5.sp,
                      fontWeight = FontWeight.Black,
                      color = Color.Black
                    )
                  }
                }
              }

              // COD / Prepaid Payment Box
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isCod) Color(0xFFFEF2F2) else Color(0xFFECFDF5))
                  .border(
                    1.dp,
                    if (isCod) Color(0xFF991B1B) else Color(0xFF065F46),
                    RoundedCornerShape(8.dp)
                  )
                  .padding(vertical = 8.dp, horizontal = 10.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = if (isCod) "COD আদায়যোগ্য: ৳${order.total}" else "PREPAID (${order.paymentMethod}) • COD: ৳0",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Black,
                  color = Color.Black
                )
              }

              // Parcel Items List
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .border(1.dp, Color(0xFFD1D5DB), RoundedCornerShape(8.dp))
                  .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
              ) {
                Text(
                  text = "পণ্যসমূহ ($totalPieces পিস):",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Black,
                  color = Color(0xFF111827)
                )
                order.items.forEach { item ->
                  val variant = listOfNotNull(
                    item.color.takeIf { it.isNotBlank() },
                    item.size.takeIf { it.isNotBlank() },
                    item.ageGroup.takeIf { it.isNotBlank() }
                  ).joinToString(" • ")
                  Text(
                    text = "• ${item.name}${if (variant.isNotBlank()) " [$variant]" else ""} × ${item.quantity}",
                    fontSize = 10.5.sp,
                    color = Color(0xFF1F2937),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                  )
                }
              }

              HorizontalDivider(color = Color(0xFFD1D5DB))

              // Sender Info
              Text(
                text = "প্রেরক (FROM): ${config.storeName} • ${config.address} • হটলাইন: ${config.phone}",
                fontSize = 9.5.sp,
                color = Color(0xFF4B5563),
                lineHeight = 13.sp
              )
            }
          }

          // Action Buttons
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedButton(
              onClick = { shareShippingLabelText(context, order, config) },
              border = BorderStroke(1.dp, RoyalMaroon),
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier
                .weight(0.9f)
                .heightIn(min = 48.dp)
                .testTag("share_shipping_label_button"),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "শেয়ার",
                tint = RoyalMaroon,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "শেয়ার",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = RoyalMaroon
              )
            }

            Button(
              onClick = {
                printShippingLabels(
                  context = context,
                  orders = listOf(order),
                  config = config,
                  format = selectedFormat,
                  onShowToast = onShowToast
                )
              },
              colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon),
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier
                .weight(1.4f)
                .heightIn(min = 48.dp)
                .testTag("print_shipping_label_button"),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Print,
                contentDescription = "প্রিন্ট করুন",
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "লেবেল প্রিন্ট করুন",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }
  }
}
