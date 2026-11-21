package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.data.BangladeshLocations
import com.example.data.model.CartItem
import com.example.data.model.Coupon
import com.example.data.model.StoreConfig
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentRed
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.AntiqueGoldDark
import com.example.ui.theme.BorderGold
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.DarkEspresso
import com.example.ui.theme.RoyalMaroon
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartAndCheckoutDialog(
  cartItems: List<CartItem>,
  coupons: List<Coupon>,
  storeConfig: StoreConfig = StoreConfig(),
  onDismiss: () -> Unit,
  onUpdateQty: (String, Int) -> Unit,
  onRemoveItem: (String) -> Unit,
  onPlaceOrder: (String, String, String, String, String, String, Coupon?, String, String, String, String, Boolean) -> Unit,
  onShowToast: (String) -> Unit,
  visible: Boolean = true
) {
  var isCheckoutMode by remember { mutableStateOf(false) }

  // Reset back to cart view after drawer finishes closing
  LaunchedEffect(visible) {
    if (!visible && isCheckoutMode) {
      delay(260)
      isCheckoutMode = false
    }
  }

  // Form states (Daraz-style Shipping Address + Chandpur Express Delivery Type)
  var name by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var selectedDistrict by remember { mutableStateOf("ঢাকা (Dhaka)") }
  var thana by remember { mutableStateOf("ধানমন্ডি") }
  var showLocationPickerDialog by remember { mutableStateOf(false) }
  var locationPickerStep by remember { mutableStateOf("DISTRICT") } // "DISTRICT" | "THANA"
  var districtSearchQuery by remember { mutableStateOf("") }

  var address by remember { mutableStateOf("") }
  var landmark by remember { mutableStateOf("") }
  var addressCategory by remember { mutableStateOf("Home") } // "Home" | "Office"
  var defaultShippingAddress by remember { mutableStateOf(false) }
  var deliveryType by remember { mutableStateOf("Standard") } // "Standard" | "Express"
  var orderType by remember { mutableStateOf("Normal") } // "Normal" | "Custom"
  var paymentMethod by remember { mutableStateOf("Cash on Delivery") }

  val isChandpur = remember(selectedDistrict) { BangladeshLocations.isChandpurDistrict(selectedDistrict) }

  // Auto-revert Express back to Standard if user changes district away from Chandpur
  LaunchedEffect(isChandpur) {
    if (!isChandpur && deliveryType == "Express") {
      deliveryType = "Standard"
    }
  }

  // Coupon state
  var couponCodeInput by remember { mutableStateOf("") }
  var appliedCoupon by remember { mutableStateOf<Coupon?>(null) }

  // B2G1 Eligibility Tracking & Calculations
  val b2g1Cfg = storeConfig.b2g1
  val eligibleIdSet = remember(b2g1Cfg.eligibleProductIds) { b2g1Cfg.eligibleProductIds.toSet() }
  val isB2G1Active = b2g1Cfg.enabled && eligibleIdSet.isNotEmpty()
  val eligiblePaidQty = remember(cartItems, eligibleIdSet) {
    cartItems.filter { !it.isFreeGift && eligibleIdSet.contains(it.product.id) }.sumOf { it.quantity }
  }
  val hasFreeGiftInCart = remember(cartItems) { cartItems.any { it.isFreeGift } }

  // Calculations using StoreConfig ShippingInfo rules (free gifts cost 0)
  val subtotal = cartItems.filterNot { it.isFreeGift }.sumOf { it.product.price * it.quantity }
  val deliveryCharge = BangladeshLocations.calculateShippingFee(
    subtotal = subtotal,
    district = selectedDistrict,
    deliveryType = deliveryType,
    shippingInfo = storeConfig.shippingInfo
  )

  var discount = 0
  if (appliedCoupon != null) {
    discount = if (appliedCoupon!!.type == "%") {
      (subtotal * appliedCoupon!!.value) / 100
    } else {
      appliedCoupon!!.value
    }
  }
  val grandTotal = (subtotal - discount).coerceAtLeast(0) + deliveryCharge

  Box(modifier = Modifier.fillMaxSize()) {
    // 1. Animated Backdrop Scrim
    AnimatedVisibility(
      visible = visible,
      enter = fadeIn(animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)),
      exit = fadeOut(animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing))
    ) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black.copy(alpha = 0.45f))
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onDismiss
          )
          .testTag("cart_drawer_backdrop")
      )
    }

    // 2. Animated Slide-In End Cart Drawer Panel
    AnimatedVisibility(
      visible = visible,
      enter = slideInHorizontally(
        initialOffsetX = { fullWidth -> fullWidth },
        animationSpec = spring(
          dampingRatio = Spring.DampingRatioLowBouncy,
          stiffness = Spring.StiffnessMediumLow
        )
      ) + fadeIn(animationSpec = tween(durationMillis = 220)),
      exit = slideOutHorizontally(
        targetOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing)
      ) + fadeOut(animationSpec = tween(durationMillis = 180)),
      modifier = Modifier.align(Alignment.CenterEnd)
    ) {
      Surface(
        shape = RoundedCornerShape(topStart = 28.dp, bottomStart = 28.dp),
        color = Color.White,
        shadowElevation = 16.dp,
        tonalElevation = 4.dp,
        modifier = Modifier
          .fillMaxHeight()
          .fillMaxWidth(0.88f)
          .widthIn(max = 430.dp)
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = {}
          )
          .testTag("cart_dialog")
      ) {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .imePadding()
        ) {
          // Sticky Drawer Header
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(CreamSurface)
              .padding(start = 16.dp, end = 8.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              modifier = Modifier.weight(1f)
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
                color = RoyalMaroon,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }

            IconButton(
              onClick = onDismiss,
              modifier = Modifier
                .minimumInteractiveComponentSize()
                .testTag("cart_drawer_close_button")
            ) {
              Box(
                modifier = Modifier
                  .size(34.dp)
                  .clip(CircleShape)
                  .background(Color.White)
                  .border(1.dp, BorderGold.copy(alpha = 0.6f), CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "কার্ট বন্ধ করুন",
                  tint = DarkEspresso,
                  modifier = Modifier.size(18.dp)
                )
              }
            }
          }

          HorizontalDivider(color = BorderGold.copy(alpha = 0.45f))

          // Scrollable Drawer Content
          Column(
            modifier = Modifier
              .fillMaxSize()
              .verticalScroll(rememberScrollState())
          ) {
            if (cartItems.isEmpty()) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 24.dp, vertical = 64.dp),
                contentAlignment = Alignment.Center
              ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(text = "🛍️", fontSize = 52.sp)
                  Spacer(modifier = Modifier.height(12.dp))
                  Text(
                    text = "আপনার কার্ট বর্তমানে খালি!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkEspresso
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "পণ্য নির্বাচন করে 'অর্ডার' বাটনে চাপুন।",
                    fontSize = 12.sp,
                    color = Color.Gray
                  )
                }
              }
            } else {
              Column(modifier = Modifier.padding(16.dp)) {
                // B2G1 Progress Banner inside Cart (ONLY shown when B2G1 is enabled, products selected, AND at least 1 eligible item or gift is in cart)
                if (isB2G1Active && (eligiblePaidQty > 0 || hasFreeGiftInCart)) {
                  val minReq = b2g1Cfg.minItems.coerceAtLeast(1)
                  val unlocked = eligiblePaidQty >= minReq && hasFreeGiftInCart
                  val remaining = (minReq - eligiblePaidQty).coerceAtLeast(0)
                  Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (unlocked) Color(0xFFECFDF5) else Color(0xFFFFF8E1),
                    border = BorderStroke(1.dp, if (unlocked) AccentGreen else BorderGold),
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(bottom = 10.dp)
                      .testTag("cart_b2g1_progress_card")
                  ) {
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column(modifier = Modifier.weight(1f)) {
                        Text(
                          text = if (unlocked) "🎉 অভিনন্দন! ফ্রি প্রোডাক্ট যুক্ত হয়েছে" else "🎁 Buy 2 Get 1 অফার চলমান",
                          fontSize = 11.5.sp,
                          fontWeight = FontWeight.Black,
                          color = if (unlocked) Color(0xFF047857) else DarkEspresso
                        )
                        Text(
                          text = if (unlocked) {
                            "অর্ডার কনফার্ম করলে ফ্রি গিফটসহ ৩টি পণ্যের স্টক সমন্বয় হবে।"
                          } else {
                            "আরো ${remaining}টি নির্বাচিত পণ্য নিলেই ফ্রি উপহার কার্টে যুক্ত হবে!"
                          },
                          fontSize = 10.sp,
                          color = if (unlocked) Color(0xFF065F46) else AntiqueGoldDark
                        )
                      }
                      Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (unlocked) AccentGreen else RoyalMaroon
                      ) {
                        Text(
                          text = "${eligiblePaidQty.coerceAtMost(minReq)}/$minReq",
                          fontSize = 10.sp,
                          fontWeight = FontWeight.Black,
                          color = Color.White,
                          modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                      }
                    }
                  }
                }

                // Cart items listing
                cartItems.forEach { item ->
                  Card(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(vertical = 4.dp)
                      .testTag(if (item.isFreeGift) "cart_free_gift_item" else "cart_item_${item.product.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                      containerColor = if (item.isFreeGift) Color(0xFFECFDF5) else CreamBackground
                    ),
                    border = BorderStroke(1.dp, if (item.isFreeGift) AccentGreen else BorderGold)
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
                        if (item.isFreeGift) {
                          Surface(
                            shape = RoundedCornerShape(50),
                            color = AccentGreen,
                            modifier = Modifier.padding(bottom = 3.dp)
                          ) {
                            Text(
                              text = "🎁 ফ্রি উপহার (Buy 2 Get 1)",
                              fontSize = 9.sp,
                              fontWeight = FontWeight.Black,
                              color = Color.White,
                              modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                            )
                          }
                        }
                        Text(
                          text = item.product.name,
                          fontSize = 13.sp,
                          fontWeight = FontWeight.SemiBold,
                          color = DarkEspresso,
                          maxLines = 1,
                          overflow = TextOverflow.Ellipsis
                        )
                        if (item.isFreeGift) {
                          Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                          ) {
                            Text(
                              text = "০ ৳ (ফ্রি)",
                              fontSize = 11.5.sp,
                              color = Color(0xFF047857),
                              fontWeight = FontWeight.Black
                            )
                            if (item.originalPrice > 0) {
                              Text(
                                text = "${item.originalPrice} ৳",
                                fontSize = 10.sp,
                                color = Color.Gray,
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                              )
                            }
                          }
                        } else {
                          Text(
                            text = "${item.product.price} ৳ / পিস",
                            fontSize = 11.sp,
                            color = AntiqueGoldDark,
                            fontWeight = FontWeight.Medium
                          )
                        }

                        // Variant Badges (Color, Size, Kids Age)
                        val hasVariants = item.selectedColor.isNotBlank() ||
                          item.selectedSize.isNotBlank() ||
                          item.selectedAgeGroup.isNotBlank()
                        if (hasVariants) {
                          Row(
                            modifier = Modifier
                              .fillMaxWidth()
                              .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                          ) {
                            if (item.selectedColor.isNotBlank()) {
                              Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = RoyalMaroon.copy(alpha = 0.12f),
                                border = BorderStroke(0.5.dp, RoyalMaroon.copy(alpha = 0.4f))
                              ) {
                                Text(
                                  text = "রং: ${item.selectedColor}",
                                  fontSize = 10.sp,
                                  fontWeight = FontWeight.Bold,
                                  color = RoyalMaroon,
                                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                              }
                            }

                            if (item.selectedSize.isNotBlank()) {
                              Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = DarkEspresso.copy(alpha = 0.1f),
                                border = BorderStroke(0.5.dp, DarkEspresso.copy(alpha = 0.3f))
                              ) {
                                Text(
                                  text = "সাইজ: ${item.selectedSize}",
                                  fontSize = 10.sp,
                                  fontWeight = FontWeight.Bold,
                                  color = DarkEspresso,
                                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                              }
                            }

                            if (item.selectedAgeGroup.isNotBlank()) {
                              Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = AntiqueGold.copy(alpha = 0.2f),
                                border = BorderStroke(0.5.dp, BorderGold)
                              ) {
                                Text(
                                  text = "👶 ${item.selectedAgeGroup}",
                                  fontSize = 10.sp,
                                  fontWeight = FontWeight.Bold,
                                  color = RoyalMaroon,
                                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                              }
                            }
                          }
                        }

                        if (!item.isFreeGift) {
                          // Quantity controls
                          Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(top = 4.dp)
                          ) {
                            Box(
                              modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.dp, BorderGold, CircleShape)
                                .clickable { onUpdateQty(item.itemKey, -1) },
                              contentAlignment = Alignment.Center
                            ) {
                              Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(14.dp))
                            }

                            Text(
                              text = item.quantity.toString(),
                              fontSize = 12.sp,
                              fontWeight = FontWeight.Bold,
                              modifier = Modifier.padding(horizontal = 6.dp)
                            )

                            Box(
                              modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.dp, BorderGold, CircleShape)
                                .clickable { onUpdateQty(item.itemKey, 1) },
                              contentAlignment = Alignment.Center
                            ) {
                              Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(14.dp))
                            }
                          }
                        } else {
                          Text(
                            text = "✓ অটো-সিলেক্টেড • ১ পিস",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF047857),
                            modifier = Modifier.padding(top = 4.dp)
                          )
                        }
                      }

                      Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(start = 6.dp)
                      ) {
                        Text(
                          text = if (item.isFreeGift) "০ ৳" else "${item.product.price * item.quantity} ৳",
                          fontSize = 14.sp,
                          fontWeight = FontWeight.Bold,
                          color = if (item.isFreeGift) Color(0xFF047857) else RoyalMaroon
                        )
                        IconButton(
                          onClick = { onRemoveItem(item.itemKey) },
                          modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                          Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove", tint = AccentRed, modifier = Modifier.size(18.dp))
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
                          .heightIn(min = 48.dp)
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
                        modifier = Modifier.heightIn(min = 48.dp)
                      ) {
                        Text("প্রয়োগ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                      }
                    }

                    AnimatedVisibility(
                      visible = appliedCoupon != null,
                      enter = expandVertically() + fadeIn(),
                      exit = shrinkVertically() + fadeOut()
                    ) {
                      appliedCoupon?.let { coupon ->
                        Row(
                          modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                          verticalAlignment = Alignment.CenterVertically,
                          horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                          Text(
                            text = "✓ কুপন ${coupon.code} প্রযোজ্য হয়েছে (-$discount ৳)",
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

                // Animated Mode Switching: Order Summary CTA <-> Checkout Form
                AnimatedContent(
                  targetState = isCheckoutMode,
                  transitionSpec = {
                    (fadeIn(animationSpec = tween(220)) + slideInHorizontally { width ->
                      if (targetState) width / 4 else -width / 4
                    }) togetherWith (fadeOut(animationSpec = tween(160)) + slideOutHorizontally { width ->
                      if (targetState) -width / 4 else width / 4
                    })
                  },
                  label = "cart_checkout_mode_transition"
                ) { checkoutActive ->
                  if (!checkoutActive) {
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
                    // Customer Checkout Form (Screenshots 3 & 4 Daraz Shipping Address + 64 Districts + Express Chandpur Delivery Type)
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                      Text(
                        text = "🚚 Shipping Address",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = RoyalMaroon
                      )

                      // Recipient's Name *
                      OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Recipient's Name *", fontSize = 12.sp) },
                        placeholder = { Text("Input the real name", fontSize = 12.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                          .fillMaxWidth()
                          .testTag("checkout_name_input")
                      )

                      // Phone Number *
                      OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number * (১১ ডিজিট)", fontSize = 12.sp) },
                        placeholder = { Text("Please input Phone Number", fontSize = 12.sp) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                          .fillMaxWidth()
                          .testTag("checkout_phone_input")
                      )

                      // District / Thana / City * (Screenshot 3 & 4: searchable 64 districts + auto-cascading Thana)
                      Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CreamBackground,
                        border = BorderStroke(1.2.dp, BorderGold),
                        modifier = Modifier
                          .fillMaxWidth()
                          .heightIn(min = 56.dp)
                          .clickable {
                            locationPickerStep = "DISTRICT"
                            districtSearchQuery = ""
                            showLocationPickerDialog = true
                          }
                          .testTag("checkout_district_thana_selector")
                      ) {
                        Row(
                          modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                          horizontalArrangement = Arrangement.SpaceBetween,
                          verticalAlignment = Alignment.CenterVertically
                        ) {
                          Column(modifier = Modifier.weight(1f)) {
                            Text(
                              text = "District / Thana / City *",
                              fontSize = 11.sp,
                              fontWeight = FontWeight.Bold,
                              color = RoyalMaroon
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                              text = if (selectedDistrict.isNotBlank() && thana.isNotBlank()) {
                                "$selectedDistrict / $thana"
                              } else {
                                "Please input District / Thana / City"
                              },
                              fontSize = 12.5.sp,
                              fontWeight = FontWeight.SemiBold,
                              color = DarkEspresso,
                              maxLines = 1,
                              overflow = TextOverflow.Ellipsis
                            )
                          }
                          Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                          ) {
                            Text(
                              text = "জেলা নির্বাচন করুন",
                              fontSize = 11.sp,
                              fontWeight = FontWeight.Bold,
                              color = Color(0xFF0284C7)
                            )
                            Icon(
                              imageVector = Icons.Default.ArrowDropDown,
                              contentDescription = "জেলা নির্বাচন করুন",
                              tint = Color(0xFF0284C7)
                            )
                          }
                        }
                      }

                      // Quick cascading Thana trigger chip row
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                      ) {
                        Surface(
                          shape = RoundedCornerShape(10.dp),
                          color = Color(0xFFF0F9FF),
                          border = BorderStroke(1.dp, Color(0xFF0284C7).copy(alpha = 0.4f)),
                          modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .clickable {
                              locationPickerStep = "DISTRICT"
                              districtSearchQuery = ""
                              showLocationPickerDialog = true
                            }
                        ) {
                          Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                          ) {
                            Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(15.dp))
                            Text("জেলা নির্বাচন করুন", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0369A1))
                          }
                        }

                        Surface(
                          shape = RoundedCornerShape(10.dp),
                          color = Color(0xFFFFF7ED),
                          border = BorderStroke(1.dp, Color(0xFFEA580C).copy(alpha = 0.4f)),
                          modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .clickable {
                              locationPickerStep = "THANA"
                              showLocationPickerDialog = true
                            }
                            .testTag("checkout_thana_picker_btn")
                        ) {
                          Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                          ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFEA580C), modifier = Modifier.size(15.dp))
                            Text("থানা: ${thana.ifBlank { "বাছাই" }}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC2410C), maxLines = 1, overflow = TextOverflow.Ellipsis)
                          }
                        }
                      }

                      // Address * (House no./building/street/area)
                      OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Address *", fontSize = 12.sp) },
                        placeholder = { Text("House no./building/street/area", fontSize = 12.sp) },
                        minLines = 2,
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                          .fillMaxWidth()
                          .testTag("checkout_address_input")
                      )

                      // Landmark (Optional)
                      OutlinedTextField(
                        value = landmark,
                        onValueChange = { landmark = it },
                        label = { Text("Landmark(Optional)", fontSize = 12.sp) },
                        placeholder = { Text("Add Additional Info", fontSize = 12.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                      )

                      // Address Category: Home / Office
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Text(text = "Address Category", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkEspresso)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                          listOf("Home", "Office").forEach { cat ->
                            val selected = addressCategory == cat
                            Row(
                              verticalAlignment = Alignment.CenterVertically,
                              modifier = Modifier
                                .heightIn(min = 48.dp)
                                .clickable { addressCategory = cat }
                                .padding(horizontal = 6.dp)
                            ) {
                              RadioButton(
                                selected = selected,
                                onClick = { addressCategory = cat },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0284C7))
                              )
                              Text(text = cat, fontSize = 12.5.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                            }
                          }
                        }
                      }

                      // Default Shipping Address On / Off
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Text(text = "Default Shipping Address", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkEspresso)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                          listOf(true to "on", false to "Off").forEach { (flag, label) ->
                            val selected = defaultShippingAddress == flag
                            Row(
                              verticalAlignment = Alignment.CenterVertically,
                              modifier = Modifier
                                .heightIn(min = 48.dp)
                                .clickable { defaultShippingAddress = flag }
                                .padding(horizontal = 4.dp)
                            ) {
                              RadioButton(
                                selected = selected,
                                onClick = { defaultShippingAddress = flag },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0284C7))
                              )
                              Text(text = label, fontSize = 12.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                            }
                          }
                        }
                      }

                      // Delivery Type & Shipping Info Block (Styled from Admin Shipping info studio + Chandpur-only Express)
                      val shipCfg = storeConfig.shippingInfo
                      val sSectionBg = parseShippingHexColor(shipCfg.sectionBgColor, Color(0xFFF0F9FF))
                      val sBlockBg = parseShippingHexColor(shipCfg.blockBgColor, Color.White)
                      val sBorder = parseShippingHexColor(shipCfg.borderColor, Color(0xFF0284C7))
                      val sText = parseShippingHexColor(shipCfg.textColor, Color(0xFF0F172A))
                      val sBtnBg = parseShippingHexColor(shipCfg.buttonBgColor, Color(0xFF0284C7))
                      val sBtnText = parseShippingHexColor(shipCfg.buttonTextColor, Color.White)

                      Column(
                        modifier = Modifier
                          .fillMaxWidth()
                          .clip(RoundedCornerShape(14.dp))
                          .background(sSectionBg)
                          .border(1.3.dp, sBorder, RoundedCornerShape(14.dp))
                          .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                      ) {
                        Text(
                          text = shipCfg.sectionTitle.ifBlank { "Delivery Type & Shipping Info" },
                          fontSize = 13.sp,
                          fontWeight = FontWeight.ExtraBold,
                          color = sText
                        )

                        shipCfg.options.filter { it.enabled }.forEach { opt ->
                          val optionType = if (opt.deliveryType.equals("Express", true)) "Express" else "Standard"
                          val isOptionEligible = when (opt.districtRule) {
                            "CHANDPUR_ONLY" -> isChandpur
                            "CUSTOM" -> opt.customDistricts.isEmpty() || opt.customDistricts.any { selectedDistrict.contains(it, true) }
                            else -> true
                          }
                          val isSelected = deliveryType.equals(optionType, ignoreCase = true) && isOptionEligible
                          val baseFee = when {
                            optionType == "Express" -> opt.fee
                            isChandpur -> shipCfg.chandpurStandardFee
                            else -> opt.fee
                          }
                          val effFee = if (opt.freeAbove > 0 && subtotal >= opt.freeAbove && optionType != "Express") 0 else baseFee

                          Row(
                            modifier = Modifier
                              .fillMaxWidth()
                              .clip(RoundedCornerShape(12.dp))
                              .background(if (isOptionEligible) sBlockBg else Color(0xFFF1F5F9))
                              .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) sBtnBg else sBorder.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(12.dp)
                              )
                              .clickable {
                                if (isOptionEligible) {
                                  deliveryType = optionType
                                } else {
                                  onShowToast(shipCfg.chandpurExpressNotice.ifBlank { "Express ডেলিভারি শুধুমাত্র চাঁদপুর জেলার জন্য প্রযোজ্য!" })
                                }
                              }
                              .padding(10.dp)
                              .testTag("checkout_delivery_type_$optionType"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                          ) {
                            Box(
                              modifier = Modifier
                                .size(22.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                  when {
                                    isSelected -> sBtnBg
                                    !isOptionEligible -> Color(0xFFCBD5E1)
                                    else -> Color.White
                                  }
                                )
                                .border(1.5.dp, if (isSelected) sBtnBg else sBorder, RoundedCornerShape(6.dp)),
                              contentAlignment = Alignment.Center
                            ) {
                              if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = sBtnText, modifier = Modifier.size(14.dp))
                              } else if (!isOptionEligible) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(12.dp))
                              }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                  text = opt.title,
                                  fontSize = 12.5.sp,
                                  fontWeight = FontWeight.ExtraBold,
                                  color = if (isOptionEligible) sText else Color.Gray
                                )
                                if (opt.badge.isNotBlank()) {
                                  Surface(shape = RoundedCornerShape(6.dp), color = if (isOptionEligible) sBtnBg else Color(0xFF94A3B8)) {
                                    Text(
                                      text = opt.badge,
                                      fontSize = 9.5.sp,
                                      fontWeight = FontWeight.Bold,
                                      color = sBtnText,
                                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                  }
                                }
                              }
                              Text(
                                text = if (!isOptionEligible) {
                                  "🔒 শুধুমাত্র চাঁদপুর জেলার জন্য প্রযোজ্য"
                                } else {
                                  opt.subtitle.ifBlank { opt.estimatedDays }
                                },
                                fontSize = 10.5.sp,
                                color = if (!isOptionEligible) Color(0xFFDC2626) else sText.copy(alpha = 0.78f)
                              )
                            }

                            Text(
                              text = if (effFee == 0) "ফ্রি" else "৳$effFee",
                              fontSize = 12.5.sp,
                              fontWeight = FontWeight.Black,
                              color = if (isOptionEligible) sBtnBg else Color.Gray
                            )
                          }
                        }
                      }

                      // Order Type selector (Normal / Custom)
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Text(text = "Order Type:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkEspresso)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                          listOf("Normal", "Custom").forEach { ot ->
                            val selected = orderType == ot
                            Surface(
                              shape = RoundedCornerShape(10.dp),
                              color = if (selected) RoyalMaroon else CreamBackground,
                              border = BorderStroke(1.dp, if (selected) RoyalMaroon else BorderGold),
                              modifier = Modifier
                                .heightIn(min = 48.dp)
                                .clickable { orderType = ot }
                            ) {
                              Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                                Text(ot, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = if (selected) Color.White else DarkEspresso)
                              }
                            }
                          }
                        }
                      }

                      // Payment Method Selector (dynamically honors Admin Footer/Payment toggles)
                      Text(
                        text = "পেমেন্ট মাধ্যম নির্বাচন:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkEspresso
                      )

                      val enabledPayments = buildList {
                        if (storeConfig.codEnabled) add("Cash on Delivery")
                        if (storeConfig.bkashEnabled) add("bKash")
                        if (storeConfig.nagadEnabled) add("Nagad")
                        if (storeConfig.rocketEnabled) add("Rocket")
                        if (storeConfig.visaEnabled) add("Visa/Mastercard")
                      }.ifEmpty { listOf("Cash on Delivery") }

                      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        enabledPayments.chunked(3).forEach { rowMethods ->
                          Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                          ) {
                            rowMethods.forEach { method ->
                              val isSelected = paymentMethod == method
                              Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) RoyalMaroon else CreamBackground,
                                border = BorderStroke(1.dp, if (isSelected) RoyalMaroon else BorderGold),
                                modifier = Modifier
                                  .weight(1f)
                                  .heightIn(min = 48.dp)
                                  .clickable { paymentMethod = method }
                              ) {
                                Box(
                                  modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                  contentAlignment = Alignment.Center
                                ) {
                                  Text(
                                    text = method,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else DarkEspresso,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                  )
                                }
                              }
                            }
                          }
                        }
                      }

                      Spacer(modifier = Modifier.height(8.dp))

                      // Submit Button
                      Button(
                        onClick = {
                          if (name.isBlank() || phone.isBlank() || address.isBlank() || selectedDistrict.isBlank() || thana.isBlank()) {
                            onShowToast("অনুগ্রহ করে নাম, মোবাইল নম্বর, জেলা/থানা এবং ঠিকানা লিখুন!")
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
                            appliedCoupon,
                            orderType,
                            deliveryType,
                            landmark,
                            addressCategory,
                            defaultShippingAddress
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
                        modifier = Modifier
                          .fillMaxWidth()
                          .heightIn(min = 48.dp)
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
    }
  }

  // Searchable 64 Districts -> Auto-Cascading Thanas Picker Dialog
  if (showLocationPickerDialog) {
    val matchingDistricts = remember(districtSearchQuery) {
      BangladeshLocations.searchDistricts(districtSearchQuery)
    }
    val districtThanas = remember(selectedDistrict) {
      BangladeshLocations.getThanasForDistrict(selectedDistrict)
    }

    AlertDialog(
      onDismissRequest = { showLocationPickerDialog = false },
      containerColor = Color.White,
      title = {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text = if (locationPickerStep == "DISTRICT") "District / জেলা খুঁজুন (৬৪টি জেলা)" else "Thana / থানা নির্বাচন — $selectedDistrict",
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            color = DarkEspresso
          )
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (locationPickerStep == "DISTRICT") Color(0xFF0284C7) else Color(0xFFF1F5F9),
              modifier = Modifier.clickable { locationPickerStep = "DISTRICT" }
            ) {
              Text(
                text = "১. জেলা (৬৪)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (locationPickerStep == "DISTRICT") Color.White else DarkEspresso,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
              )
            }
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (locationPickerStep == "THANA") Color(0xFF0284C7) else Color(0xFFF1F5F9),
              modifier = Modifier.clickable { locationPickerStep = "THANA" }
            ) {
              Text(
                text = "২. থানা (${districtThanas.size})",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (locationPickerStep == "THANA") Color.White else DarkEspresso,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
              )
            }
          }
        }
      },
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .height(360.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          if (locationPickerStep == "DISTRICT") {
            OutlinedTextField(
              value = districtSearchQuery,
              onValueChange = { districtSearchQuery = it },
              placeholder = { Text("জেলার নাম লিখুন (যেমন: চাঁদপুর / Chandpur / Dhaka)", fontSize = 12.sp) },
              leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
              singleLine = true,
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("district_search_input")
            )
            LazyColumn(
              modifier = Modifier.fillMaxSize(),
              verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              items(matchingDistricts, key = { it.nameEn }) { dist ->
                val isCurrent = selectedDistrict == dist.displayLabel
                val isDistChandpur = dist.nameEn.equals("Chandpur", true)
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isCurrent) Color(0xFFE0F2FE) else Color(0xFFF8FAFC))
                    .clickable {
                      selectedDistrict = dist.displayLabel
                      thana = dist.thanas.firstOrNull() ?: "সদর"
                      // Automatically display cascading Thanas list right after choosing a district!
                      locationPickerStep = "THANA"
                    }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = dist.displayLabel,
                      fontSize = 13.sp,
                      fontWeight = if (isCurrent) FontWeight.ExtraBold else FontWeight.SemiBold,
                      color = DarkEspresso
                    )
                    Text(
                      text = "${dist.division} Division • ${dist.thanas.size} টি থানা",
                      fontSize = 10.5.sp,
                      color = Color.Gray
                    )
                  }
                  if (isDistChandpur) {
                    Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFFEF3C7)) {
                      Text(
                        text = "⚡ Express Available",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB45309),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }
                }
              }
            }
          } else {
            Text(
              text = "$selectedDistrict এর সকল থানা (${districtThanas.size} টি):",
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF0369A1)
            )
            LazyColumn(
              modifier = Modifier.fillMaxSize(),
              verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              items(districtThanas.withIndex().toList(), key = { it.value + it.index }) { (idx, th) ->
                val isCurrent = thana == th
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isCurrent) Color(0xFFE0F2FE) else Color(0xFFF8FAFC))
                    .clickable {
                      thana = th
                      showLocationPickerDialog = false
                    }
                    .padding(horizontal = 12.dp, vertical = 11.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "${idx + 1}. $th",
                    fontSize = 13.sp,
                    fontWeight = if (isCurrent) FontWeight.ExtraBold else FontWeight.Medium,
                    color = DarkEspresso
                  )
                  if (isCurrent) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(18.dp))
                  }
                }
              }
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showLocationPickerDialog = false }) {
          Text("সম্পন্ন", fontWeight = FontWeight.Bold)
        }
      }
    )
  }
}
