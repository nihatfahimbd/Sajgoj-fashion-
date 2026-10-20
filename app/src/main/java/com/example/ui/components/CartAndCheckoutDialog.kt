package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.CartItem
import com.example.data.model.Coupon
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentRed
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.AntiqueGoldDark
import com.example.ui.theme.BorderGold
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.DarkEspresso
import com.example.ui.theme.RoyalMaroon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartAndCheckoutDialog(
  cartItems: List<CartItem>,
  coupons: List<Coupon>,
  onDismiss: () -> Unit,
  onUpdateQty: (String, Int) -> Unit,
  onRemoveItem: (String) -> Unit,
  onPlaceOrder: (String, String, String, String, String, String, Coupon?) -> Unit,
  onShowToast: (String) -> Unit
) {
  var isCheckoutMode by remember { mutableStateOf(false) }

  // Form states
  var name by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  val districts = listOf("ঢাকা (Dhaka)", "চট্টগ্রাম (Chattogram)", "সিলেট (Sylhet)", "রাজশাহী (Rajshahi)", "খুলনা (Khulna)", "বরিশাল (Barishal)", "রংপুর (Rangpur)", "ময়মনসিংহ (Mymensingh)")
  var selectedDistrict by remember { mutableStateOf(districts[0]) }
  var isDistrictExpanded by remember { mutableStateOf(false) }
  var thana by remember { mutableStateOf("") }
  var address by remember { mutableStateOf("") }
  var paymentMethod by remember { mutableStateOf("Cash on Delivery") }

  // Coupon state
  var couponCodeInput by remember { mutableStateOf("") }
  var appliedCoupon by remember { mutableStateOf<Coupon?>(null) }

  // Calculations
  val subtotal = cartItems.sumOf { it.product.price * it.quantity }
  val isFreeDelivery = subtotal >= 2000
  val isDhaka = selectedDistrict.contains("ঢাকা") || selectedDistrict.contains("Dhaka")
  val deliveryCharge = if (isFreeDelivery) 0 else if (isDhaka) 60 else 120

  var discount = 0
  if (appliedCoupon != null) {
    discount = if (appliedCoupon!!.type == "%") {
      (subtotal * appliedCoupon!!.value) / 100
    } else {
      appliedCoupon!!.value
    }
  }
  val grandTotal = (subtotal - discount).coerceAtLeast(0) + deliveryCharge

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
        .testTag("cart_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        // Modal Header
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(CreamSurface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = if (isCheckoutMode) Icons.Default.Payment else Icons.Default.ShoppingBag,
              contentDescription = null,
              tint = RoyalMaroon
            )
            Text(
              text = if (isCheckoutMode) "ক্যাশ অন ডেলিভারি চেকআউট" else "শপিং কার্ট (${cartItems.size})",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = RoyalMaroon
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(Color.White)
          ) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = DarkEspresso, modifier = Modifier.size(18.dp))
          }
        }

        // Cart Items List
        if (cartItems.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(40.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "🛍️", fontSize = 48.sp)
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "আপনার কার্ট বর্তমানে খালি!",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = DarkEspresso
              )
              Text(
                text = "পণ্য নির্বাচন করে 'অর্ডার' বাটনে চাপুন।",
                fontSize = 12.sp,
                color = Color.Gray
              )
            }
          }
        } else {
          Column(modifier = Modifier.padding(16.dp)) {
            // Cart items listing
            cartItems.forEach { item ->
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CreamBackground),
                border = BorderStroke(1.dp, BorderGold)
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  AppImage(
                    model = item.product.imageUrl,
                    contentDescription = item.product.name,
                    modifier = Modifier
                      .size(54.dp)
                      .clip(RoundedCornerShape(8.dp))
                  )

                  Spacer(modifier = Modifier.width(10.dp))

                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = item.product.name,
                      fontSize = 13.sp,
                      fontWeight = FontWeight.SemiBold,
                      color = DarkEspresso,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis
                    )
                    Text(
                      text = "${item.product.price} ৳ / পিস",
                      fontSize = 11.sp,
                      color = AntiqueGoldDark,
                      fontWeight = FontWeight.Medium
                    )

                    // Quantity controls
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(4.dp),
                      modifier = Modifier.padding(top = 4.dp)
                    ) {
                      Box(
                        modifier = Modifier
                          .size(24.dp)
                          .clip(CircleShape)
                          .background(Color.White)
                          .border(1.dp, BorderGold, CircleShape)
                          .clickable { onUpdateQty(item.product.id, -1) },
                        contentAlignment = Alignment.Center
                      ) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(12.dp))
                      }

                      Text(
                        text = item.quantity.toString(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp)
                      )

                      Box(
                        modifier = Modifier
                          .size(24.dp)
                          .clip(CircleShape)
                          .background(Color.White)
                          .border(1.dp, BorderGold, CircleShape)
                          .clickable { onUpdateQty(item.product.id, 1) },
                        contentAlignment = Alignment.Center
                      ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(12.dp))
                      }
                    }
                  }

                  Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(start = 6.dp)
                  ) {
                    Text(
                      text = "${item.product.price * item.quantity} ৳",
                      fontSize = 14.sp,
                      fontWeight = FontWeight.Bold,
                      color = RoyalMaroon
                    )
                    IconButton(
                      onClick = { onRemoveItem(item.product.id) },
                      modifier = Modifier.size(24.dp)
                    ) {
                      Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove", tint = AccentRed, modifier = Modifier.size(16.dp))
                    }
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Coupon Box
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = CreamSurface),
              border = BorderStroke(1.dp, BorderGold)
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text(
                  text = "🎟️ কুপন বা ডিসকাউন্ট কোড",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = RoyalMaroon
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  OutlinedTextField(
                    value = couponCodeInput,
                    onValueChange = { couponCodeInput = it.uppercase() },
                    placeholder = { Text("যেমন: EID2026", fontSize = 12.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                      focusedContainerColor = Color.White,
                      unfocusedContainerColor = Color.White,
                      focusedBorderColor = RoyalMaroon,
                      unfocusedBorderColor = BorderGold
                    ),
                    modifier = Modifier
                      .weight(1f)
                      .height(44.dp)
                      .testTag("coupon_input")
                  )

                  Button(
                    onClick = {
                      val found = coupons.find { it.code.equals(couponCodeInput.trim(), ignoreCase = true) && it.isActive }
                      if (found == null) {
                        onShowToast("ভুল বা মেয়াদোত্তীর্ণ কুপন কোড!")
                      } else if (subtotal < found.minOrder) {
                        onShowToast("এই কুপনের জন্য নূন্যতম ${found.minOrder} ৳ এর অর্ডার প্রয়োজন!")
                      } else {
                        appliedCoupon = found
                        onShowToast("🎉 কুপন \"${found.code}\" কার্যকর হয়েছে!")
                      }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkEspresso),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.height(44.dp)
                  ) {
                    Text("প্রয়োগ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                  }
                }

                if (appliedCoupon != null) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(top = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(
                      text = "✓ কুপন ${appliedCoupon!!.code} প্রযোজ্য হয়েছে (-$discount ৳)",
                      color = AccentGreen,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold
                    )
                    Text(
                      text = "বাতিল",
                      color = AccentRed,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.clickable {
                        appliedCoupon = null
                        couponCodeInput = ""
                      }
                    )
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Pricing Summary Card
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = CreamBackground)
            ) {
              Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text(text = "সাবটোটাল", fontSize = 12.sp, color = DarkEspresso)
                  Text(text = "$subtotal ৳", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                if (discount > 0) {
                  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "কুপন ছাড় (${appliedCoupon?.code})", fontSize = 12.sp, color = AccentGreen)
                    Text(text = "- $discount ৳", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AccentGreen)
                  }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text(text = "ডেলিভারি চার্জ", fontSize = 12.sp, color = DarkEspresso)
                  Text(
                    text = if (deliveryCharge == 0) "ফ্রি (Free ৳)" else "$deliveryCharge ৳",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (deliveryCharge == 0) AccentGreen else DarkEspresso
                  )
                }

                HorizontalDivider(color = BorderGold, modifier = Modifier.padding(vertical = 4.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text(text = "সর্বমোট প্রদেয়", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)
                  Text(text = "$grandTotal ৳", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Mode Switching: Show Order Form vs Proceed Button
            if (!isCheckoutMode) {
              Button(
                onClick = { isCheckoutMode = true },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp)
                  .testTag("proceed_checkout_button")
              ) {
                Text(
                  text = "চেকআউট ও ঠিকানা দিন →",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            } else {
              // Customer Checkout Form
              Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                  text = "🚚 ডেলিভারি ঠিকানা পূরণ করুন",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = RoyalMaroon
                )

                // Name
                OutlinedTextField(
                  value = name,
                  onValueChange = { name = it },
                  label = { Text("আপনার নাম *", fontSize = 12.sp) },
                  placeholder = { Text("সম্পূর্ণ নাম লিখুন", fontSize = 12.sp) },
                  singleLine = true,
                  shape = RoundedCornerShape(14.dp),
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("checkout_name_input")
                )

                // Phone
                OutlinedTextField(
                  value = phone,
                  onValueChange = { phone = it },
                  label = { Text("মোবাইল নম্বর * (১১ ডিজিট)", fontSize = 12.sp) },
                  placeholder = { Text("01XXXXXXXXX", fontSize = 12.sp) },
                  singleLine = true,
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                  shape = RoundedCornerShape(14.dp),
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("checkout_phone_input")
                )

                // District Dropdown
                ExposedDropdownMenuBox(
                  expanded = isDistrictExpanded,
                  onExpandedChange = { isDistrictExpanded = !isDistrictExpanded }
                ) {
                  OutlinedTextField(
                    value = selectedDistrict,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("জেলা নির্বাচন করুন", fontSize = 12.sp) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDistrictExpanded) },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                      .menuAnchor()
                      .fillMaxWidth()
                  )
                  ExposedDropdownMenu(
                    expanded = isDistrictExpanded,
                    onDismissRequest = { isDistrictExpanded = false }
                  ) {
                    districts.forEach { dist ->
                      DropdownMenuItem(
                        text = { Text(dist, fontSize = 13.sp) },
                        onClick = {
                          selectedDistrict = dist
                          isDistrictExpanded = false
                        }
                      )
                    }
                  }
                }

                // Thana / Upazila
                OutlinedTextField(
                  value = thana,
                  onValueChange = { thana = it },
                  label = { Text("থানা / উপজেলা", fontSize = 12.sp) },
                  placeholder = { Text("যেমন: ধানমন্ডি / মিরপুর", fontSize = 12.sp) },
                  singleLine = true,
                  shape = RoundedCornerShape(14.dp),
                  modifier = Modifier.fillMaxWidth()
                )

                // Full Address
                OutlinedTextField(
                  value = address,
                  onValueChange = { address = it },
                  label = { Text("সম্পূর্ণ বিস্তারিত ঠিকানা *", fontSize = 12.sp) },
                  placeholder = { Text("বাসা নম্বর, রোড নম্বর, ফ্ল্যাট নম্বর...", fontSize = 12.sp) },
                  minLines = 2,
                  maxLines = 3,
                  shape = RoundedCornerShape(14.dp),
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("checkout_address_input")
                )

                // Payment Method Selector
                Text(
                  text = "পেমেন্ট মাধ্যম নির্বাচন:",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = DarkEspresso
                )

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  listOf("Cash on Delivery", "bKash", "Nagad").forEach { method ->
                    val isSelected = paymentMethod == method
                    Surface(
                      shape = RoundedCornerShape(12.dp),
                      color = if (isSelected) RoyalMaroon else CreamBackground,
                      border = BorderStroke(1.dp, if (isSelected) RoyalMaroon else BorderGold),
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
                          fontSize = 11.sp,
                          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                          color = if (isSelected) Color.White else DarkEspresso
                        )
                      }
                    }
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Submit Button
                Button(
                  onClick = {
                    if (name.isBlank() || phone.isBlank() || address.isBlank()) {
                      onShowToast("অনুগ্রহ করে নাম, ফোন নম্বর এবং সম্পূর্ণ ঠিকানা লিখুন!")
                      return@Button
                    }
                    if (phone.length < 11) {
                      onShowToast("সঠিক ১১ ডিজিটের মোবাইল নম্বর দিন!")
                      return@Button
                    }
                    onPlaceOrder(
                      name,
                      phone,
                      selectedDistrict,
                      thana,
                      address,
                      paymentMethod,
                      appliedCoupon
                    )
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon),
                  shape = RoundedCornerShape(20.dp),
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("confirm_order_button")
                ) {
                  Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "অর্ডার নিশ্চিত করুন ($grandTotal ৳)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                  )
                }

                Button(
                  onClick = { isCheckoutMode = false },
                  colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = DarkEspresso),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Text("← কার্টে ফিরে যান", fontSize = 12.sp)
                }
              }
            }
          }
        }
      }
    }
  }
}
