package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.ContentCopy
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
import com.example.data.model.ReturnRecord
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.BorderGold
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.DarkEspresso
import com.example.ui.theme.RoyalMaroon

@Composable
fun ReturnSlipDialog(
  record: ReturnRecord,
  onDismiss: () -> Unit,
  onShowToast: (String) -> Unit
) {
  val context = LocalContext.current

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .heightIn(max = 640.dp)
        .testTag("return_slip_dialog"),
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      border = BorderStroke(1.5.dp, BorderGold),
      elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp)
          .verticalScroll(rememberScrollState())
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(CreamSurface),
              contentAlignment = Alignment.Center
            ) {
              Text("📄", fontSize = 18.sp)
            }
            Column {
              Text(
                text = "রিটার্ন ও এক্সচেঞ্জ মেমো স্লিপ",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = RoyalMaroon
              )
              Text(
                text = "মেমো #${record.id} • তারিখ: ${record.date}",
                fontSize = 10.sp,
                color = Color.Gray
              )
            }
          }

          IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = DarkEspresso)
          }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = BorderGold)

        // Customer & Order Info
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = CreamSurface,
          border = BorderStroke(1.dp, BorderGold),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(text = "গ্রাহক: ${record.customerName} (📞 ${record.phone})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(text = "মূল অর্ডার ID: #${record.orderId} • লেনদেন: ${if (record.isExchange) "পণ্য এক্সচেঞ্জ সমন্বয়" else "টাকা ফেরত"}", fontSize = 10.sp, color = DarkEspresso)
            Text(text = "কারণ: ${record.reason}", fontSize = 10.sp, color = Color.Gray)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Returned Items
        Text(text = "📦 ফেরত দেওয়া পণ্যসমূহ:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)
        Spacer(modifier = Modifier.height(4.dp))
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .background(CreamBackground, RoundedCornerShape(10.dp))
            .padding(8.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          record.returnedItems.forEach { itm ->
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(text = "• ${itm.name} (${itm.quantity}টি)", fontSize = 10.sp, color = DarkEspresso, modifier = Modifier.weight(1f))
              Text(text = "৳${itm.price * itm.quantity}", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
          }
          HorizontalDivider(color = BorderGold)
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "মোট ফেরত মূল্য:", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(text = "৳${record.totalReturnValue}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)
          }
        }

        // Exchanged Items
        if (record.isExchange && record.exchangeItems.isNotEmpty()) {
          Spacer(modifier = Modifier.height(10.dp))
          Text(text = "🔄 নতুন এক্সচেঞ্জ নেওয়া পণ্য:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
          Spacer(modifier = Modifier.height(4.dp))
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFFFFF3E0), RoundedCornerShape(10.dp))
              .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            record.exchangeItems.forEach { itm ->
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(text = "• ${itm.name} (${itm.quantity}টি)", fontSize = 10.sp, color = DarkEspresso, modifier = Modifier.weight(1f))
                Text(text = "৳${itm.price * itm.quantity}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
              }
            }
            HorizontalDivider(color = Color(0xFFFFCC80))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text(text = "মোট নতুন পণ্য মূল্য:", fontSize = 10.sp, fontWeight = FontWeight.Bold)
              Text(text = "৳${record.totalExchangeValue}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Settlement Card
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(
            containerColor = when (record.differenceType) {
              "customer_paid_extra" -> Color(0xFFFFF3E0)
              "customer_refunded_balance" -> Color(0xFFE8F5E9)
              else -> Color(0xFFE3F2FD)
            }
          ),
          border = BorderStroke(
            1.dp,
            when (record.differenceType) {
              "customer_paid_extra" -> Color(0xFFFF9800)
              "customer_refunded_balance" -> AccentGreen
              else -> Color(0xFF2196F3)
            }
          )
        ) {
          Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            when (record.differenceType) {
              "customer_paid_extra" -> {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text(text = "⚠️ কাস্টমার থেকে অতিরিক্ত আদায় (${record.settlementMethod}):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB26A00))
                  Text(text = "+ ৳${record.priceDifference}", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color(0xFFE65100))
                }
                if (record.settlementTrxId.isNotBlank()) {
                  Text(text = "TrxID: ${record.settlementTrxId}", fontSize = 9.sp, color = DarkEspresso)
                }
              }
              "customer_refunded_balance" -> {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text(text = "💵 কাস্টমারকে বাকি টাকা ফেরত (${record.settlementMethod}):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AccentGreen)
                  Text(text = "- ৳${kotlin.math.abs(record.priceDifference)}", fontSize = 13.sp, fontWeight = FontWeight.Black, color = AccentGreen)
                }
                if (record.settlementTrxId.isNotBlank()) {
                  Text(text = "অ্যাকাউন্ট/রেফ: ${record.settlementTrxId}", fontSize = 9.sp, color = DarkEspresso)
                }
              }
              else -> {
                Text(text = "✅ সমমূল্যের এক্সচেঞ্জ সমন্বয় (০ ৳ ব্যালেন্স)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
              }
            }

            if (record.note.isNotBlank()) {
              Text(text = "নোট: \"${record.note}\"", fontSize = 9.sp, color = Color.Gray)
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Copy / Close Actions
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Button(
            onClick = {
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              val text = """
                SAJGOJ FASHION - রিটার্ন ও এক্সচেঞ্জ মেমো
                মেমো ID: #${record.id}
                মূল অর্ডার ID: #${record.orderId}
                গ্রাহক: ${record.customerName} (${record.phone})
                ফেরত মূল্য: ৳${record.totalReturnValue}
                ${if (record.isExchange) "নতুন পণ্য মূল্য: ৳${record.totalExchangeValue}" else ""}
                নিষ্পত্তি: ${if (record.priceDifference > 0) "+ ৳${record.priceDifference} আদায় (${record.settlementMethod})" else if (record.priceDifference < 0) "- ৳${kotlin.math.abs(record.priceDifference)} ফেরত (${record.settlementMethod})" else "সমমূল্য"}
              """.trimIndent()
              clipboard.setPrimaryClip(ClipData.newPlainText("Return Slip", text))
              onShowToast("মেমো বিবরণ ক্লিপবোর্ডে কপি করা হয়েছে! 📋")
            },
            colors = ButtonDefaults.buttonColors(containerColor = CreamSurface, contentColor = RoyalMaroon),
            border = BorderStroke(1.dp, BorderGold),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f)
          ) {
            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("কপি মেমো", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = onDismiss,
            colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f)
          ) {
            Text("বন্ধ করুন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
