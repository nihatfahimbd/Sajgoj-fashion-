package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ExchangeItem
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.ReturnItem
import com.example.data.model.ReturnRecord
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentRed
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.AntiqueGoldDark
import com.example.ui.theme.BorderGold
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.DarkEspresso
import com.example.ui.theme.RoyalMaroon
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

data class SelectedReturnItemState(
  val selected: Boolean = true,
  val returnQty: Int = 1,
  val condition: String = "ভালো (বিক্রিযোগ্য)",
  val restock: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReturnExchangeDialog(
  orders: List<Order>,
  products: List<Product>,
  initialOrder: Order? = null,
  onDismiss: () -> Unit,
  onConfirm: (ReturnRecord) -> Unit
) {
  var selectedOrder by remember { mutableStateOf(initialOrder ?: orders.firstOrNull()) }
  var isOrderDropdownExpanded by remember { mutableStateOf(false) }

  // Return selection for items of the selected order
  val returnSelections = remember(selectedOrder) {
    mutableStateMapOf<Int, SelectedReturnItemState>().apply {
      selectedOrder?.items?.forEachIndexed { idx, itm ->
        put(
          idx,
          SelectedReturnItemState(
            selected = true,
            returnQty = maxOf(1, itm.quantity),
            condition = "ভালো (বিক্রিযোগ্য)",
            restock = true
          )
        )
      }
    }
  }

  // Settlement Mode: Exchange vs Refund
  var isExchangeMode by remember { mutableStateOf(true) }
  var refundMethod by remember { mutableStateOf("Cash") }

  // Exchange items chosen from products catalog
  var exchangeItemsList by remember { mutableStateOf<List<ExchangeItem>>(emptyList()) }
  var exchangeDeliveryFee by remember { mutableStateOf(0) }
  var settlementPaymentMethod by remember { mutableStateOf("Cash") } // when customer pays extra
  var settlementRefundMethod by remember { mutableStateOf("Cash") } // when store refunds
  var settlementTrxId by remember { mutableStateOf("") }
  var returnReason by remember { mutableStateOf("পণ্য পরিবর্তন / অপছন্দ") }
  var returnNote by remember { mutableStateOf("") }

  // Product picker for exchange
  var isAddingExchangeProduct by remember { mutableStateOf(false) }
  var exchangeSearchQuery by remember { mutableStateOf("") }
  var exchangeSelectedProduct by remember { mutableStateOf<Product?>(null) }
  var exchangeSelectedSize by remember { mutableStateOf("") }
  var exchangeSelectedColor by remember { mutableStateOf("") }
  var exchangeQty by remember { mutableStateOf(1) }

  // Calculations
  val totalReturnValue = remember(selectedOrder, returnSelections.values.toList()) {
    selectedOrder?.items?.mapIndexed { idx, itm ->
      val sel = returnSelections[idx]
      if (sel != null && sel.selected) {
        itm.price * sel.returnQty
      } else 0
    }?.sum() ?: 0
  }

  val totalExchangeValue = remember(exchangeItemsList) {
    exchangeItemsList.sumOf { it.price * it.quantity }
  }

  // Net Price Difference
  // Positive: Customer pays remaining extra
  // Negative: Store refunds customer
  // Zero: Even exchange
  val netPriceDifference = remember(totalReturnValue, totalExchangeValue, exchangeDeliveryFee, isExchangeMode) {
    if (isExchangeMode) {
      (totalExchangeValue + exchangeDeliveryFee) - totalReturnValue
    } else {
      -totalReturnValue
    }
  }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .heightIn(max = 680.dp)
        .testTag("return_exchange_dialog"),
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
              Text("🔄", fontSize = 18.sp)
            }
            Column {
              Text(
                text = "রিটার্ন ও এক্সচেঞ্জ সমন্বয়",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = RoyalMaroon
              )
              Text(
                text = "মূল্য পার্থক্য হিসাব ও নিষ্পত্তিকরণ",
                fontSize = 10.sp,
                color = DarkEspresso
              )
            }
          }

          IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = DarkEspresso)
          }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = BorderGold)

        // Step 1: Order Selector
        Text(
          text = "১. মূল অর্ডার নির্বাচন করুন:",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = RoyalMaroon
        )
        Spacer(modifier = Modifier.height(4.dp))

        ExposedDropdownMenuBox(
          expanded = isOrderDropdownExpanded,
          onExpandedChange = { isOrderDropdownExpanded = !isOrderDropdownExpanded }
        ) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = CreamSurface,
            border = BorderStroke(1.dp, BorderGold),
            modifier = Modifier
              .fillMaxWidth()
              .menuAnchor()
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = selectedOrder?.let { "#${it.id} - ${it.customerName} (${it.phone}) - ৳${it.total}" }
                  ?: "অর্ডার নির্বাচন করুন",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkEspresso
              )
              ExposedDropdownMenuDefaults.TrailingIcon(expanded = isOrderDropdownExpanded)
            }
          }

          ExposedDropdownMenu(
            expanded = isOrderDropdownExpanded,
            onDismissRequest = { isOrderDropdownExpanded = false }
          ) {
            orders.forEach { ord ->
              DropdownMenuItem(
                text = {
                  Text(
                    text = "#${ord.id} - ${ord.customerName} (৳${ord.total})",
                    fontSize = 12.sp
                  )
                },
                onClick = {
                  selectedOrder = ord
                  isOrderDropdownExpanded = false
                }
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (selectedOrder != null) {
          // Step 2: Returned Products Selection
          Text(
            text = "২. কোন পণ্যটি ফেরত দিচ্ছেন? (রিটার্ন আইটেম):",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = RoyalMaroon
          )
          Spacer(modifier = Modifier.height(6.dp))

          Column(
            modifier = Modifier
              .fillMaxWidth()
              .background(CreamBackground, RoundedCornerShape(14.dp))
              .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            selectedOrder!!.items.forEachIndexed { idx, itm ->
              val sel = returnSelections[idx] ?: SelectedReturnItemState()
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, if (sel.selected) RoyalMaroon else BorderGold)
              ) {
                Column(modifier = Modifier.padding(8.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Checkbox(
                      checked = sel.selected,
                      onCheckedChange = { isChecked ->
                        returnSelections[idx] = sel.copy(selected = isChecked)
                      },
                      colors = CheckboxDefaults.colors(checkedColor = RoyalMaroon)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                      Text(text = itm.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkEspresso)
                      Text(text = "মূল্য: ৳${itm.price} • কেনা পরিমাণ: ${itm.quantity}টি", fontSize = 10.sp, color = Color.Gray)
                    }

                    if (sel.selected) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                          shape = CircleShape,
                          color = CreamSurface,
                          border = BorderStroke(1.dp, BorderGold),
                          modifier = Modifier
                            .size(26.dp)
                            .clickable {
                              if (sel.returnQty > 1) {
                                returnSelections[idx] = sel.copy(returnQty = sel.returnQty - 1)
                              }
                            }
                        ) {
                          Box(contentAlignment = Alignment.Center) { Text("-", fontSize = 14.sp, fontWeight = FontWeight.Bold) }
                        }
                        Text(
                          text = "${sel.returnQty}",
                          fontSize = 12.sp,
                          fontWeight = FontWeight.Bold,
                          modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Surface(
                          shape = CircleShape,
                          color = CreamSurface,
                          border = BorderStroke(1.dp, BorderGold),
                          modifier = Modifier
                            .size(26.dp)
                            .clickable {
                              if (sel.returnQty < itm.quantity) {
                                returnSelections[idx] = sel.copy(returnQty = sel.returnQty + 1)
                              }
                            }
                        ) {
                          Box(contentAlignment = Alignment.Center) { Text("+", fontSize = 14.sp, fontWeight = FontWeight.Bold) }
                        }
                      }
                    }
                  }

                  if (sel.selected) {
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, start = 36.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                          checked = sel.restock,
                          onCheckedChange = { returnSelections[idx] = sel.copy(restock = it) },
                          colors = CheckboxDefaults.colors(checkedColor = AccentGreen),
                          modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "স্টকে পুনরায় যুক্ত করুন", fontSize = 10.sp, color = DarkEspresso)
                      }
                      Text(
                        text = "ফেরত মূল্য: ৳${itm.price * sel.returnQty}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalMaroon
                      )
                    }
                  }
                }
              }
            }

            Text(
              text = "মোট ফেরত জমা ক্রেডিট: ৳$totalReturnValue",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = RoyalMaroon,
              modifier = Modifier.align(Alignment.End).padding(end = 4.dp, top = 2.dp)
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Step 3: Exchange vs Refund Mode
          Text(
            text = "৩. নিষ্পত্তির ধরন নির্ধারণ:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = RoyalMaroon
          )
          Spacer(modifier = Modifier.height(6.dp))

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
              onClick = { isExchangeMode = true },
              colors = ButtonDefaults.buttonColors(
                containerColor = if (isExchangeMode) Color(0xFFE65100) else CreamSurface,
                contentColor = if (isExchangeMode) Color.White else DarkEspresso
              ),
              border = BorderStroke(1.dp, if (isExchangeMode) Color(0xFFE65100) else BorderGold),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.weight(1f)
            ) {
              Text("🔄 নতুন পণ্য এক্সচেঞ্জ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Button(
              onClick = { isExchangeMode = false },
              colors = ButtonDefaults.buttonColors(
                containerColor = if (!isExchangeMode) AccentGreen else CreamSurface,
                contentColor = if (!isExchangeMode) Color.White else DarkEspresso
              ),
              border = BorderStroke(1.dp, if (!isExchangeMode) AccentGreen else BorderGold),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.weight(1f)
            ) {
              Text("💵 সম্পূর্ণ টাকা ফেরত", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Step 4: If Exchange, Select New Items
          if (isExchangeMode) {
            Text(
              text = "৪. নতুন যে পণ্যটি কাস্টমার এক্সচেঞ্জ নিচ্ছেন:",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFFE65100)
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Button to open item picker
            Button(
              onClick = { isAddingExchangeProduct = !isAddingExchangeProduct },
              colors = ButtonDefaults.buttonColors(containerColor = CreamSurface, contentColor = Color(0xFFE65100)),
              border = BorderStroke(1.dp, BorderGold),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("স্টোর ক্যাটালগ থেকে নতুন পণ্য নির্বাচন করুন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            // Product Picker Dropdown/Panel
            if (isAddingExchangeProduct) {
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(top = 6.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9F2)),
                border = BorderStroke(1.dp, Color(0xFFFFCC80))
              ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  OutlinedTextField(
                    value = exchangeSearchQuery,
                    onValueChange = { exchangeSearchQuery = it },
                    placeholder = { Text("পণ্য খুঁজুন...", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = BorderGold)
                  )

                  val filteredExchangeProducts = products.filter {
                    exchangeSearchQuery.isBlank() || it.name.contains(exchangeSearchQuery, ignoreCase = true)
                  }.take(5)

                  LazyColumn(
                    modifier = Modifier.heightIn(max = 140.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    items(filteredExchangeProducts) { prod ->
                      Surface(
                        modifier = Modifier
                          .fillMaxWidth()
                          .clickable {
                            exchangeSelectedProduct = prod
                            exchangeSelectedSize = prod.sizes.firstOrNull() ?: ""
                            exchangeSelectedColor = prod.colors.firstOrNull() ?: ""
                          },
                        shape = RoundedCornerShape(8.dp),
                        color = if (exchangeSelectedProduct?.id == prod.id) Color(0xFFFFE0B2) else Color.White,
                        border = BorderStroke(1.dp, BorderGold)
                      ) {
                        Row(
                          modifier = Modifier.padding(6.dp),
                          verticalAlignment = Alignment.CenterVertically,
                          horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                          Column(modifier = Modifier.weight(1f)) {
                            Text(text = prod.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                            Text(text = "মূল্য: ৳${prod.price} • স্টক: ${prod.stock}টি", fontSize = 10.sp, color = Color.Gray)
                          }
                          if (exchangeSelectedProduct?.id == prod.id) {
                            Text("✓", color = Color(0xFFE65100), fontWeight = FontWeight.Bold)
                          }
                        }
                      }
                    }
                  }

                  if (exchangeSelectedProduct != null) {
                    // Variants selection if available
                    if (exchangeSelectedProduct!!.sizes.isNotEmpty()) {
                      Text("সাইজ নির্বাচন:", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                      Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        exchangeSelectedProduct!!.sizes.forEach { sz ->
                          Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (exchangeSelectedSize == sz) RoyalMaroon else CreamSurface,
                            modifier = Modifier.clickable { exchangeSelectedSize = sz }
                          ) {
                            Text(
                              text = sz,
                              fontSize = 10.sp,
                              color = if (exchangeSelectedSize == sz) Color.White else DarkEspresso,
                              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                          }
                        }
                      }
                    }

                    if (exchangeSelectedProduct!!.colors.isNotEmpty()) {
                      Text("রং নির্বাচন:", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                      Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        exchangeSelectedProduct!!.colors.forEach { clr ->
                          Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (exchangeSelectedColor == clr) RoyalMaroon else CreamSurface,
                            modifier = Modifier.clickable { exchangeSelectedColor = clr }
                          ) {
                            Text(
                              text = clr,
                              fontSize = 10.sp,
                              color = if (exchangeSelectedColor == clr) Color.White else DarkEspresso,
                              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                          }
                        }
                      }
                    }

                    Button(
                      onClick = {
                        val prod = exchangeSelectedProduct!!
                        exchangeItemsList = exchangeItemsList + ExchangeItem(
                          productId = prod.id,
                          name = prod.name,
                          price = prod.price,
                          quantity = 1,
                          color = exchangeSelectedColor,
                          size = exchangeSelectedSize,
                          sku = prod.sku
                        )
                        isAddingExchangeProduct = false
                        exchangeSelectedProduct = null
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
                      shape = RoundedCornerShape(10.dp),
                      modifier = Modifier.fillMaxWidth()
                    ) {
                      Text("এক্সচেঞ্জ তালিকায় যোগ করুন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                  }
                }
              }
            }

            // Chosen Exchange items display
            if (exchangeItemsList.isNotEmpty()) {
              Spacer(modifier = Modifier.height(6.dp))
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(Color(0xFFFFF3E0), RoundedCornerShape(12.dp))
                  .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                exchangeItemsList.forEachIndexed { exIdx, exItm ->
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .background(Color.White, RoundedCornerShape(8.dp))
                      .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(text = exItm.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                      val v = listOfNotNull(
                        if (exItm.size.isNotBlank()) "সাইজ: ${exItm.size}" else null,
                        if (exItm.color.isNotBlank()) "কালার: ${exItm.color}" else null
                      ).joinToString(" | ")
                      if (v.isNotBlank()) {
                        Text(text = v, fontSize = 9.sp, color = Color.Gray)
                      }
                    }
                    Text(text = "৳${exItm.price}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                    IconButton(
                      onClick = { exchangeItemsList = exchangeItemsList.filterIndexed { i, _ -> i != exIdx } },
                      modifier = Modifier.size(24.dp)
                    ) {
                      Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = AccentRed, modifier = Modifier.size(16.dp))
                    }
                  }
                }

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(text = "এক্সচেঞ্জ ডেলিভারি চার্জ:", fontSize = 11.sp, color = DarkEspresso)
                  Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(0, 60, 120).forEach { fee ->
                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (exchangeDeliveryFee == fee) Color(0xFFE65100) else Color.White,
                        border = BorderStroke(1.dp, BorderGold),
                        modifier = Modifier.clickable { exchangeDeliveryFee = fee }
                      ) {
                        Text(
                          text = if (fee == 0) "ফ্রি (৳০)" else "৳$fee",
                          fontSize = 10.sp,
                          color = if (exchangeDeliveryFee == fee) Color.White else DarkEspresso,
                          fontWeight = FontWeight.Bold,
                          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                      }
                    }
                  }
                }

                Text(
                  text = "মোট নতুন এক্সচেঞ্জ পণ্যের মূল্য: ৳$totalExchangeValue",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFFE65100),
                  modifier = Modifier.align(Alignment.End)
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // =========================================================
            // STEP 5: PRICE DIFFERENCE CALCULATION & SETTLEMENT
            // =========================================================
            if (exchangeItemsList.isNotEmpty()) {
              Text(
                text = "৫. মূল্য পার্থক্য ও সমন্বয় হিসাব (Price Difference Settlement):",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = RoyalMaroon
              )
              Spacer(modifier = Modifier.height(6.dp))

              // Breakdown summary
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Surface(
                  modifier = Modifier.weight(1f),
                  shape = RoundedCornerShape(12.dp),
                  color = Color(0xFFFFF0F2),
                  border = BorderStroke(1.dp, Color(0xFFFFCDD2))
                ) {
                  Column(modifier = Modifier.padding(8.dp)) {
                    Text(text = "ফেরত পণ্য ক্রেডিট:", fontSize = 10.sp, color = Color.Gray)
                    Text(text = "৳$totalReturnValue", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)
                  }
                }

                Surface(
                  modifier = Modifier.weight(1f),
                  shape = RoundedCornerShape(12.dp),
                  color = Color(0xFFFFF8E1),
                  border = BorderStroke(1.dp, Color(0xFFFFE082))
                ) {
                  Column(modifier = Modifier.padding(8.dp)) {
                    Text(text = "নতুন এক্সচেঞ্জ মূল্য:", fontSize = 10.sp, color = Color.Gray)
                    Text(
                      text = "৳${totalExchangeValue + exchangeDeliveryFee}",
                      fontSize = 13.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFFE65100)
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              // CASE 1: NEW PRODUCT IS MORE EXPENSIVE (Customer pays extra)
              if (netPriceDifference > 0) {
                Card(
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(16.dp),
                  colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                  border = BorderStroke(1.5.dp, Color(0xFFFF9800))
                ) {
                  Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column {
                        Text(
                          text = "⚠️ নতুন পণ্যের মূল্য বেশি",
                          fontSize = 11.sp,
                          fontWeight = FontWeight.Bold,
                          color = Color(0xFFB26A00)
                        )
                        Text(
                          text = "কাস্টমার থেকে অবশিষ্ট আদায়যোগ্য:",
                          fontSize = 12.sp,
                          fontWeight = FontWeight.Bold,
                          color = DarkEspresso
                        )
                      }
                      Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFFF9800))
                      ) {
                        Text(
                          text = "+ ৳$netPriceDifference",
                          fontSize = 16.sp,
                          fontWeight = FontWeight.Black,
                          color = Color(0xFFE65100),
                          modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                      }
                    }

                    Text(
                      text = "হিসাব: [নতুন পণ্য ৳${totalExchangeValue + exchangeDeliveryFee}] - [ফেরত পণ্য ৳$totalReturnValue] = + ৳$netPriceDifference",
                      fontSize = 10.sp,
                      color = Color.DarkGray
                    )

                    HorizontalDivider(color = Color(0xFFFFE0B2))

                    Text(text = "টাকা সংগ্রহের মাধ্যম:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkEspresso)
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                      listOf("Cash", "bKash", "Nagad", "Card").forEach { m ->
                        Surface(
                          shape = RoundedCornerShape(8.dp),
                          color = if (settlementPaymentMethod == m) Color(0xFFE65100) else Color.White,
                          border = BorderStroke(1.dp, BorderGold),
                          modifier = Modifier.clickable { settlementPaymentMethod = m }
                        ) {
                          Text(
                            text = when (m) {
                              "Cash" -> "💵 ক্যাশ"
                              "bKash" -> "📱 বিকাশ"
                              "Nagad" -> "📱 নগদ"
                              else -> "💳 কার্ড"
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (settlementPaymentMethod == m) Color.White else DarkEspresso,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                          )
                        }
                      }
                    }

                    OutlinedTextField(
                      value = settlementTrxId,
                      onValueChange = { settlementTrxId = it },
                      placeholder = { Text("লেনদেন TrxID / মেমো রেফারেন্স (ঐচ্ছিক)...", fontSize = 10.sp) },
                      modifier = Modifier.fillMaxWidth(),
                      shape = RoundedCornerShape(8.dp),
                      colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = BorderGold)
                    )
                  }
                }
              }

              // CASE 2: NEW PRODUCT IS CHEAPER (Store refunds customer)
              if (netPriceDifference < 0) {
                val refundAmount = kotlin.math.abs(netPriceDifference)
                Card(
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(16.dp),
                  colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                  border = BorderStroke(1.5.dp, AccentGreen)
                ) {
                  Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column {
                        Text(
                          text = "💵 নতুন পণ্যের মূল্য কম",
                          fontSize = 11.sp,
                          fontWeight = FontWeight.Bold,
                          color = AccentGreen
                        )
                        Text(
                          text = "কাস্টমারকে বাকি টাকা ফেরতযোগ্য:",
                          fontSize = 12.sp,
                          fontWeight = FontWeight.Bold,
                          color = DarkEspresso
                        )
                      }
                      Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, AccentGreen)
                      ) {
                        Text(
                          text = "- ৳$refundAmount",
                          fontSize = 16.sp,
                          fontWeight = FontWeight.Black,
                          color = AccentGreen,
                          modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                      }
                    }

                    Text(
                      text = "হিসাব: [ফেরত পণ্য ৳$totalReturnValue] - [নতুন পণ্য ৳${totalExchangeValue + exchangeDeliveryFee}] = ফেরতযোগ্য ৳$refundAmount",
                      fontSize = 10.sp,
                      color = Color.DarkGray
                    )

                    HorizontalDivider(color = Color(0xFFC8E6C9))

                    Text(text = "বাকি টাকা ফেরত দেওয়ার মাধ্যম:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkEspresso)
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                      listOf("Cash", "bKash", "Nagad", "Credit").forEach { m ->
                        Surface(
                          shape = RoundedCornerShape(8.dp),
                          color = if (settlementRefundMethod == m) AccentGreen else Color.White,
                          border = BorderStroke(1.dp, BorderGold),
                          modifier = Modifier.clickable { settlementRefundMethod = m }
                        ) {
                          Text(
                            text = when (m) {
                              "Cash" -> "💵 ক্যাশ ফেরত"
                              "bKash" -> "📱 বিকাশ"
                              "Nagad" -> "📱 নগদ"
                              else -> "🏷️ স্টোর ক্রেডিট"
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (settlementRefundMethod == m) Color.White else DarkEspresso,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                          )
                        }
                      }
                    }

                    OutlinedTextField(
                      value = settlementTrxId,
                      onValueChange = { settlementTrxId = it },
                      placeholder = { Text("গ্রাহকের রিফান্ড অ্যাকাউন্ট / রেফারেন্স নম্বর...", fontSize = 10.sp) },
                      modifier = Modifier.fillMaxWidth(),
                      shape = RoundedCornerShape(8.dp),
                      colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = BorderGold)
                    )
                  }
                }
              }

              // CASE 3: EVEN EXCHANGE
              if (netPriceDifference == 0) {
                Card(
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(16.dp),
                  colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                  border = BorderStroke(1.5.dp, Color(0xFF2196F3))
                ) {
                  Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    Text("✅", fontSize = 20.sp)
                    Column {
                      Text(
                        text = "সমমূল্য পণ্য বিনিময় (০ ৳ ব্যালেন্স)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D47A1)
                      )
                      Text(
                        text = "ফেরত ও নতুন পণ্যের মূল্য সমান থাকায় কোনো বাড়তি অর্থ লেনদেন নেই।",
                        fontSize = 10.sp,
                        color = Color(0xFF1565C0)
                      )
                    }
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Step 6: Return Reason & Note
          Text(text = "রিটার্নের কারণ ও অতিরিক্ত নোট:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkEspresso)
          Spacer(modifier = Modifier.height(4.dp))
          OutlinedTextField(
            value = returnNote,
            onValueChange = { returnNote = it },
            placeholder = { Text("যেমন: কাস্টমার সাইজ M বদলে L নিয়েছেন...", fontSize = 11.sp) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = BorderGold)
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Confirm Action Button
          Button(
            onClick = {
              val ord = selectedOrder ?: return@Button
              val selectedReturnItems = ord.items.mapIndexedNotNull { idx, itm ->
                val sel = returnSelections[idx]
                if (sel != null && sel.selected) {
                  ReturnItem(
                    productId = itm.productId,
                    name = itm.name,
                    price = itm.price,
                    quantity = sel.returnQty,
                    condition = sel.condition,
                    restock = sel.restock,
                    color = itm.color,
                    size = itm.size
                  )
                } else null
              }

              if (selectedReturnItems.isEmpty()) return@Button

              val returnId = "RET-" + Random.nextInt(100000, 999999)
              val record = ReturnRecord(
                id = returnId,
                orderId = ord.id,
                customerName = ord.customerName,
                phone = ord.phone,
                date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                returnedItems = selectedReturnItems,
                totalReturnValue = totalReturnValue,
                isExchange = isExchangeMode,
                exchangeItems = if (isExchangeMode) exchangeItemsList else emptyList(),
                totalExchangeValue = if (isExchangeMode) totalExchangeValue else 0,
                deliveryFee = if (isExchangeMode) exchangeDeliveryFee else 0,
                priceDifference = netPriceDifference,
                differenceType = if (isExchangeMode) {
                  if (netPriceDifference > 0) "customer_paid_extra"
                  else if (netPriceDifference < 0) "customer_refunded_balance"
                  else "even_exchange"
                } else "full_refund",
                settlementMethod = if (isExchangeMode) {
                  if (netPriceDifference > 0) settlementPaymentMethod
                  else if (netPriceDifference < 0) settlementRefundMethod
                  else "সমমূল্য"
                } else refundMethod,
                settlementTrxId = settlementTrxId.trim(),
                settlementStatus = "paid",
                reason = returnReason,
                note = returnNote.trim()
              )

              onConfirm(record)
            },
            colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("confirm_return_button")
          ) {
            Text(
              text = "রিটার্ন ও এক্সচেঞ্জ নিশ্চিত করুন 📄",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}
