package com.example.ui.components

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Order
import com.example.data.model.StoreConfig
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.BorderGold
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.DarkEspresso
import com.example.ui.theme.RoyalMaroon

@Composable
fun InvoiceDialog(
  order: Order,
  config: StoreConfig,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current

  fun shareInvoice() {
    val itemsSummary = order.items.joinToString("\n") {
      "• ${it.name} x${it.quantity} = ${it.price * it.quantity} ৳"
    }
    val invoiceText = """
      ==============================
      ${config.storeName}
      ${config.tagline}
      হটলাইন: ${config.phone}
      ==============================
      ইনভয়েস নং: #${order.id}
      তারিখ: ${order.date}
      স্ট্যাটাস: ${order.status}
      ${if (order.trackingId.isNotBlank()) "কুরিয়ার ট্র্যাকিং: ${order.trackingId}" else ""}
      
      গ্রাহকের তথ্য:
      নাম: ${order.customerName}
      মোবাইল: ${order.phone}
      ঠিকানা: ${order.address}, ${order.thana}, ${order.district}
      
      অর্ডারের বিবরণ:
      $itemsSummary
      
      ------------------------------
      সাবটোটাল: ${order.subtotal} ৳
      ডিসকাউন্ট: ${order.discount} ৳
      ডেলিভারি চার্জ: ${order.deliveryCharge} ৳
      সর্বমোট: ${order.total} ৳ (${order.paymentMethod})
      ==============================
      সজগোজ ফ্যাশনের সাথে থাকার জন্য ধন্যবাদ!
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
      type = "text/plain"
      putExtra(Intent.EXTRA_SUBJECT, "Invoice #${order.id} - ${config.storeName}")
      putExtra(Intent.EXTRA_TEXT, invoiceText)
    }
    context.startActivity(Intent.createChooser(intent, "ইনভয়েস শেয়ার করুন"))
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = Color.White,
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .padding(vertical = 20.dp)
        .testTag("invoice_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        // Invoice Header
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(RoyalMaroon)
            .padding(16.dp)
        ) {
          Column {
            Text(
              text = config.storeName,
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
            Text(
              text = config.tagline,
              fontSize = 11.sp,
              color = AntiqueGold
            )
            Text(
              text = "হটলাইন: ${config.phone}  |  ${config.email}",
              fontSize = 10.sp,
              color = CreamSurface
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .align(Alignment.TopEnd)
              .size(28.dp)
              .clip(CircleShape)
              .background(Color.White.copy(alpha = 0.2f))
          ) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(16.dp))
          }
        }

        // Invoice Meta Info
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text(text = "INVOICE / বিল চালান", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)
              Text(text = "অর্ডার ID: #${order.id}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkEspresso)
              Text(text = "তারিখ: ${order.date}", fontSize = 11.sp, color = Color.Gray)
            }

            Column(horizontalAlignment = Alignment.End) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (order.status == "Delivered" || order.status == "Shipped") AccentGreen.copy(alpha = 0.15f) else Color(0xFFFEF3C7))
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = order.status,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (order.status == "Delivered" || order.status == "Shipped") AccentGreen else Color(0xFFB45309)
                )
              }
              if (order.trackingId.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "Track: ${order.trackingId}",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = RoyalMaroon
                )
              }
            }
          }

          HorizontalDivider(color = BorderGold, modifier = Modifier.padding(vertical = 12.dp))

          // Customer Details Box
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CreamBackground),
            border = BorderStroke(1.dp, BorderGold)
          ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
              Text(text = "গ্রাহকের বিবরণ:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)
              Text(text = "নাম: ${order.customerName}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
              Text(text = "মোবাইল: ${order.phone}", fontSize = 12.sp)
              Text(text = "ঠিকানা: ${order.address}, ${order.thana}, ${order.district}", fontSize = 12.sp)
              Text(text = "পেমেন্ট মাধ্যম: ${order.paymentMethod}", fontSize = 12.sp, color = DarkEspresso)
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Items Table Header
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(CreamSurface)
              .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = "পণ্য", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f))
            Text(text = "পরিমাণ", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text(text = "মূল্য", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text(text = "মোট", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
          }

          // Items rows
          order.items.forEach { item ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(text = item.name, fontSize = 11.sp, modifier = Modifier.weight(2f), maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
              Text(text = "${item.quantity}", fontSize = 11.sp, modifier = Modifier.weight(1f))
              Text(text = "${item.price} ৳", fontSize = 11.sp, modifier = Modifier.weight(1f))
              Text(text = "${item.price * item.quantity} ৳", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            }
            HorizontalDivider(color = Color(0xFFF0F0F0))
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Calculations
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(CreamBackground)
              .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("সাবটোটাল", fontSize = 12.sp)
              Text("${order.subtotal} ৳", fontSize = 12.sp)
            }
            if (order.discount > 0) {
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("ছাড় (${order.couponCode})", fontSize = 12.sp, color = AccentGreen)
                Text("- ${order.discount} ৳", fontSize = 12.sp, color = AccentGreen, fontWeight = FontWeight.Bold)
              }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("ডেলিভারি চার্জ", fontSize = 12.sp)
              Text("${order.deliveryCharge} ৳", fontSize = 12.sp)
            }
            HorizontalDivider(color = BorderGold, modifier = Modifier.padding(vertical = 4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("সর্বমোট বিল", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)
              Text("${order.total} ৳", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Action buttons: Share / Print invoice
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Button(
              onClick = { shareInvoice() },
              colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon),
              shape = RoundedCornerShape(18.dp),
              modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .testTag("share_invoice_button")
            ) {
              Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("ইনভয়েস শেয়ার / প্রিন্ট", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}
