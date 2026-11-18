package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InventorySyncEvent
import com.example.data.model.PosInventoryItem
import com.example.data.model.Product
import com.example.data.model.SyncErrorLog
import com.example.data.model.SyncHealthState
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventorySyncPanel(
  products: List<Product>,
  posInventory: Map<String, PosInventoryItem>,
  syncEvents: List<InventorySyncEvent>,
  syncHealth: SyncHealthState,
  onRecordPosSale: (Product, Int, String, String) -> Unit,
  onReconcileNow: () -> Unit,
  syncErrorLogs: List<SyncErrorLog> = emptyList(),
  onClearSyncErrors: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  var selectedProduct by remember(products) { mutableStateOf(products.firstOrNull { it.stock > 0 } ?: products.firstOrNull()) }
  var productDropdownExpanded by remember { mutableStateOf(false) }
  var saleQty by remember { mutableIntStateOf(1) }
  var customerName by remember { mutableStateOf("") }
  var paymentMethod by remember { mutableStateOf("POS Cash") }

  val timeFormatter = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

  LazyColumn(
    modifier = modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(12.dp),
    contentPadding = PaddingValues(bottom = 24.dp)
  ) {
    // 1. Live Bidirectional Sync Status Header Card
    item {
      GlossyAdminCard(
        cornerRadius = 20.dp,
        contentPadding = 14.dp
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(if (syncHealth.driftCount == 0) AccentGreen.copy(alpha = 0.14f) else AccentRed.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = if (syncHealth.driftCount == 0) Icons.Default.CheckCircle else Icons.Default.Warning,
                  contentDescription = "Sync status",
                  tint = if (syncHealth.driftCount == 0) AccentGreen else AccentRed,
                  modifier = Modifier.size(20.dp)
                )
              }
              Column {
                Text(
                  text = "স্টোরফ্রন্ট ↔ POS রিয়েল-টাইম সিঙ্ক",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = AdminInkPrimary
                )
                Text(
                  text = "স্ট্যাটাস: ${if (syncHealth.status == "CONNECTED") "ফায়ারস্টোর ও POS সক্রিয়" else "লোকাল ব্রিজ সক্রিয়"} • ${timeFormatter.format(Date(syncHealth.lastSyncTimestamp))}",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium,
                  color = AdminInkSecondary
                )
              }
            }

            Button(
              onClick = onReconcileNow,
              colors = ButtonDefaults.buttonColors(containerColor = AdminV2CyanDeep, contentColor = Color.White),
              shape = RoundedCornerShape(14.dp),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
              modifier = Modifier
                .height(48.dp)
                .testTag("reconcile_inventory_button")
            ) {
              Icon(imageVector = Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("সিঙ্ক করুন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Metrics Strip
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            SyncMetricChip(
              label = "সিঙ্কড SKU",
              value = "${syncHealth.inSyncCount}/${syncHealth.storefrontProductCount}",
              accent = AccentGreen,
              modifier = Modifier.weight(1f)
            )
            SyncMetricChip(
              label = "স্টক পার্থক্য",
              value = "${syncHealth.driftCount}",
              accent = if (syncHealth.driftCount == 0) AccentGreen else AccentRed,
              modifier = Modifier.weight(1f)
            )
            SyncMetricChip(
              label = "সিঙ্ক ইভেন্ট",
              value = "${syncEvents.size}",
              accent = AdminV2CyanDeep,
              modifier = Modifier.weight(1f)
            )
          }
        }
      }
    }

    // 1B. Diagnostic View: Local POS Storage <-> Central Firestore Timestamp & Error Logs
    item {
      PosSyncDiagnosticCard(
        syncHealth = syncHealth,
        syncErrorLogs = syncErrorLogs,
        onRunDiagnosticSync = onReconcileNow,
        onClearErrorLogs = onClearSyncErrors
      )
    }

    // 2. Quick POS Counter Sale (Instant Bidirectional Test & Billing)
    item {
      GlossyAdminCard(
        cornerRadius = 20.dp,
        contentPadding = 14.dp
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(imageVector = Icons.Default.PointOfSale, contentDescription = null, tint = AdminV2CyanDeep, modifier = Modifier.size(18.dp))
            Text(
              text = "কুইক POS কাউন্টার বিক্রয় (অটো-স্টক সিঙ্ক)",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = AdminInkPrimary
            )
          }

          if (products.isNotEmpty()) {
            ExposedDropdownMenuBox(
              expanded = productDropdownExpanded,
              onExpandedChange = { productDropdownExpanded = !productDropdownExpanded }
            ) {
              OutlinedTextField(
                value = selectedProduct?.let { "${it.name} (স্টক: ${it.stock} • ৳${it.price})" } ?: "পণ্য নির্বাচন করুন",
                onValueChange = {},
                readOnly = true,
                label = { Text("POS পণ্য সিলেক্ট করুন", fontSize = 11.sp) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = productDropdownExpanded) },
                shape = RoundedCornerShape(14.dp),
                colors = adminOutlinedTextFieldColors(),
                modifier = Modifier
                  .menuAnchor()
                  .fillMaxWidth()
              )
              ExposedDropdownMenu(
                expanded = productDropdownExpanded,
                onDismissRequest = { productDropdownExpanded = false },
                containerColor = Color.White
              ) {
                products.forEach { prod ->
                  DropdownMenuItem(
                    text = {
                      Text(
                        text = "${prod.name} (স্টক: ${prod.stock})",
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                      )
                    },
                    onClick = {
                      selectedProduct = prod
                      saleQty = 1
                      productDropdownExpanded = false
                    }
                  )
                }
              }
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              OutlinedTextField(
                value = customerName,
                onValueChange = { customerName = it },
                placeholder = { Text("ক্রেতার নাম (ঐচ্ছিক)", fontSize = 12.sp) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = adminOutlinedTextFieldColors(),
                modifier = Modifier.weight(1f)
              )

              Surface(
                shape = RoundedCornerShape(14.dp),
                color = AdminV2IceSurface,
                border = BorderStroke(1.dp, AdminCardBorderWarm)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  IconButton(
                    onClick = { if (saleQty > 1) saleQty-- },
                    modifier = Modifier.size(48.dp)
                  ) {
                    Icon(imageVector = Icons.Default.Remove, contentDescription = "পরিমাণ কমান", tint = AdminInkPrimary)
                  }
                  Text(
                    text = "$saleQty",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AdminV2CyanDeep
                  )
                  IconButton(
                    onClick = {
                      val maxAvail = selectedProduct?.stock ?: 1
                      if (saleQty < maxAvail) saleQty++
                    },
                    modifier = Modifier.size(48.dp)
                  ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "পরিমাণ বাড়ান", tint = AdminInkPrimary)
                  }
                }
              }
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              listOf("POS Cash", "bKash", "Nagad", "Card").forEach { method ->
                val active = paymentMethod == method
                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = if (active) AdminV2CyanDeep else Color.White,
                  border = BorderStroke(1.dp, if (active) AdminV2CyanDeep else AdminCardBorderWarm),
                  modifier = Modifier
                    .weight(1f)
                    .clickable { paymentMethod = method }
                ) {
                  Box(
                    modifier = Modifier.padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = method,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (active) Color.White else AdminInkPrimary
                    )
                  }
                }
              }
            }

            Button(
              onClick = {
                selectedProduct?.let { prod ->
                  if (prod.stock > 0) {
                    onRecordPosSale(prod, saleQty, customerName, paymentMethod)
                    customerName = ""
                    saleQty = 1
                  }
                }
              },
              enabled = (selectedProduct?.stock ?: 0) > 0,
              colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
              shape = RoundedCornerShape(16.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("complete_pos_sale_button")
            ) {
              Text(
                text = if ((selectedProduct?.stock ?: 0) > 0) {
                  "✓ POS বিক্রয় সম্পন্ন ও স্টোরফ্রন্ট সিঙ্ক করুন (৳${(selectedProduct?.price ?: 0) * saleQty})"
                } else {
                  "স্টক শেষ (POS বিক্রয় অক্ষম)"
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }
        }
      }
    }

    // 3. Storefront vs POS Stock Parity Matrix
    item {
      GlossyAdminCard(
        cornerRadius = 20.dp,
        contentPadding = 14.dp
      ) {
        Column {
          Text(
            text = "স্টোরফ্রন্ট বনাম POS লাইভ স্টক তুলনা",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = AdminInkPrimary
          )
          Spacer(modifier = Modifier.height(8.dp))

          products.take(12).forEachIndexed { index, product ->
            val posItem = posInventory[product.id]
            val posStock = posItem?.stock ?: product.stock
            val inSync = posItem != null && posStock == product.stock
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = product.name,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = AdminInkPrimary,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Text(
                  text = "SKU: ${product.sku.ifBlank { product.id }}",
                  fontSize = 10.sp,
                  color = AdminInkMuted
                )
              }
              Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(8.dp), color = AdminV2IceSurface) {
                  Text(
                    text = "Web: ${product.stock}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AdminInkPrimary,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                  )
                }
                Surface(shape = RoundedCornerShape(8.dp), color = AdminV2IceSurface) {
                  Text(
                    text = "POS: $posStock",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AdminInkPrimary,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                  )
                }
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = if (inSync) AccentGreen.copy(alpha = 0.15f) else AccentRed.copy(alpha = 0.15f)
                ) {
                  Text(
                    text = if (inSync) "সিঙ্কড" else "পার্থক্য",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (inSync) AccentGreen else AccentRed,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                  )
                }
              }
            }
            if (index < minOf(products.size, 12) - 1) {
              HorizontalDivider(color = AdminCardBorderSoft)
            }
          }
        }
      }
    }

    // 4. Real-Time Sync Audit Events Feed
    item {
      Text(
        text = "সাম্প্রতিক দ্বিমুখী সিঙ্ক অডিট লগ (${syncEvents.size})",
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = AdminInkPrimary
      )
    }

    if (syncEvents.isEmpty()) {
      item {
        OutlinedButton(
          onClick = onReconcileNow,
          shape = RoundedCornerShape(16.dp),
          border = BorderStroke(1.dp, AdminCardBorderWarm),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "এখনো কোনো সিঙ্ক ইভেন্ট নেই — অর্ডার, POS বিক্রয় বা রিটার্ন হলে এখানে দেখাবে",
            fontSize = 11.sp,
            color = AdminInkSecondary
          )
        }
      }
    } else {
      items(syncEvents.take(25), key = { it.id }) { ev ->
        GlossyAdminCard(
          cornerRadius = 14.dp,
          contentPadding = 10.dp
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(6.dp), color = AdminV2CyanDeep) {
                  Text(
                    text = ev.source,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
                Text(
                  text = ev.productName,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = AdminInkPrimary,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "${ev.reason} • ${timeFormatter.format(Date(ev.timestamp))}",
                fontSize = 10.sp,
                color = AdminInkMuted
              )
            }

            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (ev.delta >= 0) AccentGreen.copy(alpha = 0.14f) else AccentRed.copy(alpha = 0.14f)
            ) {
              Text(
                text = "${ev.previousStock}→${ev.newStock} (${if (ev.delta >= 0) "+${ev.delta}" else "${ev.delta}"})",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (ev.delta >= 0) AccentGreen else AccentRed,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SyncMetricChip(
  label: String,
  value: String,
  accent: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    color = AdminV2IceSurface,
    border = BorderStroke(1.dp, AdminCardBorderWarm)
  ) {
    Column(
      modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = accent)
      Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = AdminInkSecondary)
    }
  }
}
