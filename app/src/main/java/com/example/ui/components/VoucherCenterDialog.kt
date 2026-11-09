package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.BorderGold
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.DarkEspresso
import com.example.ui.theme.RoyalMaroon

data class VoucherUiItem(
  val id: String,
  val section: String = "mega", // "max", "mega", "shipping"
  val title: String,
  val banglaTitle: String,
  val discountText: String,
  val minSpendText: String,
  val tag: String = "Voucher Max",
  val validity: String = "10/11/2026-30/11/2026",
  val badge: String = "",
  val terms: String = ""
)

val defaultVouchersList = listOf(
  // Voucher Max Section (Top Horizontal Scroll in Screenshot 2)
  VoucherUiItem(
    id = "vm_2250",
    section = "max",
    title = "Flat Tk. 2,250 Off",
    banglaTitle = "বড় অর্ডারে বিশেষ মেগা ক্যাশ ডিসকাউন্ট",
    discountText = "৳২,২৫০",
    minSpendText = "Min. Spend ৳২৪,৯৯৯",
    tag = "Voucher Max",
    validity = "10/11/2026-21/11/2026",
    terms = "এই ভাউচারটি শুধুমাত্র সাজগোজ ফ্যাশনের ন্যূনতম ২৪,৯৯৯ টাকার কেনাকাটায় একবার ব্যবহারযোগ্য। মেয়াদ শেষ হওয়ার পূর্বে সংগ্রহ করে চেকআউটে অটো-অ্যাপ্লাই করুন।"
  ),
  VoucherUiItem(
    id = "vm_3500",
    section = "max",
    title = "Flat Tk. 3,500 Off",
    banglaTitle = "প্রিমিয়াম বিয়ের কেনাকাটায় মেগা সেভিংস",
    discountText = "৳৩,৫০০",
    minSpendText = "Min. Spend ৳৩৪,৯৯৯",
    tag = "Voucher Max",
    validity = "10/11/2026-21/11/2026",
    terms = "৩৪,৯৯৯ টাকার বেশি শাড়ি ও রাজকীয় জুয়েলারি প্যাকেজ ক্রয়ে সরাসরি ৩,৫০০ টাকা ছাড়।"
  ),
  VoucherUiItem(
    id = "vm_500",
    section = "max",
    title = "Flat Tk. 500 Off",
    banglaTitle = "উৎসবের পোশাকে দারুণ ছাড়",
    discountText = "৳৫০০",
    minSpendText = "Min. Spend ৳৪,৯৯৯",
    tag = "Voucher Max",
    validity = "10/11/2026-21/11/2026",
    terms = "উৎসব স্পেশাল পোশাক ও গহনা ক্রয়ে ন্যূনতম ৪,৯৯৯ টাকায় ৫০০ টাকা ক্যাশ ছাড়।"
  ),

  // Section 1: সাজগোজ Mega Voucher (Platform Wide Sajgoj Vouchers - Screenshot 2)
  VoucherUiItem(
    id = "mv_beauty",
    section = "mega",
    title = "Health & Beauty",
    banglaTitle = "সকল প্রিমিয়াম জুয়েলারি ও কসমেটিকস কালেকশন",
    discountText = "10% OFF",
    minSpendText = "Min. Spend ৳১৯৯",
    tag = "Voucher Max",
    validity = "10/11/2026-21/11/2026",
    terms = "ন্যূনতম ১৯৯ টাকার জুয়েলারি বা বিউটি অর্ডারে ১০% ইনস্ট্যান্ট ডিসকাউন্ট।"
  ),
  VoucherUiItem(
    id = "mv_fashion",
    section = "mega",
    title = "Fashion",
    banglaTitle = "শাড়ি, ফ্রক ও ফ্যাশন পোশাকে প্রযোজ্য",
    discountText = "10% OFF",
    minSpendText = "Min. Spend ৳২৪৯",
    tag = "Voucher Max",
    validity = "10/11/2026-21/11/2026",
    terms = "সকল ফ্যাশন আইটেমে ন্যূনতম ২৪৯ টাকার কেনাকাটায় ১০% ছাড়।"
  ),
  VoucherUiItem(
    id = "mv_upto50",
    section = "mega",
    title = "Upto Tk.50 Off",
    banglaTitle = "যেকোনো অর্ডারে সর্বোচ্চ ৫০ টাকা পর্যন্ত ছাড়",
    discountText = "8% OFF",
    minSpendText = "Min. Spend ৳৩৪৯",
    tag = "Voucher Max",
    validity = "10/11/2026-21/11/2026",
    terms = "ন্যূনতম ৩৪৯ টাকার ক্রয়ে ৮% (সর্বোচ্চ ৫০ টাকা) তাৎক্ষণিক ছাড়।"
  ),
  VoucherUiItem(
    id = "mv_upto175",
    section = "mega",
    title = "Upto Tk.175 Off",
    banglaTitle = "১৫০০+ টাকার কেনাকাটায় মেগা সাশ্রয়",
    discountText = "8% OFF",
    minSpendText = "Min. Spend ৳১,৪৯৯",
    tag = "Voucher Max",
    validity = "10/11/2026-21/11/2026",
    terms = "ন্যূনতম ১,৪৯৯ টাকার অর্ডারে ৮% (সর্বোচ্চ ১৭৫ টাকা) সাশ্রয়।"
  ),
  VoucherUiItem(
    id = "mv_saree",
    section = "mega",
    title = "কাতান শাড়ি স্পেশাল",
    banglaTitle = "এক্সক্লুসিভ কাতান ও সিল্ক শাড়িতে বিশেষ ছাড়",
    discountText = "15% OFF",
    minSpendText = "Min. Spend ৳৯৯৯",
    tag = "Voucher Max",
    validity = "10/11/2026-30/11/2026",
    terms = "কাতান, জামদানি ও সিল্ক শাড়িতে ন্যূনতম ৯৯৯ টাকার অর্ডারে ১৫% ক্যাশ ছাড়।"
  ),

  // Section 2: Free shipping vouchers (Screenshot 3)
  VoucherUiItem(
    id = "fs_45",
    section = "shipping",
    title = "Selected Sellers",
    banglaTitle = "সাজগোজ সিলেক্টেড অর্ডারে ডেলিভারি চার্জ ছাড়",
    discountText = "৳৪৫",
    minSpendText = "Min. Spend ৳৯৯",
    tag = "Free Shipping",
    validity = "01/11/2026-30/11/2026",
    badge = "x2",
    terms = "ন্যূনতম ৯৯ টাকার কেনাকাটায় হোম ডেলিভারিতে ৪৫ টাকা নগদ ছাড়।"
  ),
  VoucherUiItem(
    id = "fs_95",
    section = "shipping",
    title = "Save on Delivery",
    banglaTitle = "হোম ডেলিভারিতে ৯৫ টাকা ক্যাশ সেভ করুন",
    discountText = "৳৯৫",
    minSpendText = "Min. Spend ৳২৯৯",
    tag = "Free Shipping",
    validity = "03/11/2026-30/11/2026",
    badge = "x2",
    terms = "ন্যূনতম ২৯৯ টাকার কেনাকাটায় ডেলিভারি ফিতে ৯৫ টাকা ছাড়।"
  ),
  VoucherUiItem(
    id = "fs_120",
    section = "shipping",
    title = "Selected Sellers",
    banglaTitle = "বড় অর্ডারে ১২০ টাকা ডেলিভারি ছাড়",
    discountText = "৳১২০",
    minSpendText = "Min. Spend ৳৪৯৯",
    tag = "Free Shipping",
    validity = "10/11/2026-21/11/2026",
    badge = "",
    terms = "ন্যূনতম ৪৯৯ টাকার অর্ডারে ১২০ টাকা পর্যন্ত ডেলিভারি মাশুল মওকুফ।"
  ),
  VoucherUiItem(
    id = "fs_120_fashion",
    section = "shipping",
    title = "Fashion",
    banglaTitle = "ফ্যাশন ক্যাটাগরির পোশাকে সম্পূর্ণ ডেলিভারি সেভ",
    discountText = "৳১২০",
    minSpendText = "Min. Spend ৳৩৯৯",
    tag = "Free Shipping",
    validity = "10/11/2026-21/11/2026",
    badge = "x2",
    terms = "ফ্যাশন ও শাড়ির অর্ডারে ন্যূনতম ৩৯৯ টাকায় ১২০ টাকা ডেলিভারি ছাড়।"
  ),
  VoucherUiItem(
    id = "fs_150_free",
    section = "shipping",
    title = "Free Delivery Special",
    banglaTitle = "১৯৯৯+ টাকার অর্ডারে সারা দেশে ডেলিভারি সম্পূর্ণ ফ্রি",
    discountText = "৳১৫০",
    minSpendText = "Min. Spend ৳১,৯৯৯",
    tag = "Free Shipping",
    validity = "01/11/2026-30/11/2026",
    badge = "FREE",
    terms = "১৯৯৯ টাকার বেশি কেনাকাটায় ডেলিভারি চার্জ সম্পূর্ণ ০ টাকা (ফ্রি)।"
  )
)

@Composable
fun VoucherCenterDialog(
  collectedVoucherIds: Set<String>,
  initialTab: String = "all",
  onCollectVoucher: (String) -> Unit,
  onCollectAll: (List<String>) -> Unit,
  onDismiss: () -> Unit
) {
  var activeTab by remember { mutableStateOf(initialTab) }
  var selectedTcItem by remember { mutableStateOf<VoucherUiItem?>(null) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .testTag("voucher_center_dialog"),
      color = Color(0xFFFEF8F4)
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // 1. Header (Matches Screenshot 2 & 3: Back button, Voucher Center title, icon, close)
        Surface(
          color = Color.White,
          shadowElevation = 2.dp
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              IconButton(
                onClick = onDismiss,
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(Color(0xFFFFF0F2))
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                  contentDescription = "Back",
                  tint = RoyalMaroon,
                  modifier = Modifier.size(20.dp)
                )
              }

              Column {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Text(
                    text = "Voucher Center",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = DarkEspresso
                  )
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(6.dp))
                      .background(RoyalMaroon)
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text(
                      text = "সাজগোজ",
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color.White
                    )
                  }
                }
                Text(
                  text = "ভাউচার কালেক্ট করুন ও চেকআউটে স্বয়ংক্রিয় ছাড় পান",
                  fontSize = 11.sp,
                  color = AntiqueGold
                )
              }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(text = "🎟️", fontSize = 20.sp)
              IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(32.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Close",
                  tint = Color.Gray,
                  modifier = Modifier.size(18.dp)
                )
              }
            }
          }
        }

        // 2. Filter Tabs (All Vouchers, Voucher Max, Free Shipping Voucher, Category Voucher)
        Surface(
          color = Color.White,
          shadowElevation = 1.dp
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState())
              .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf(
              Triple("all", "All Vouchers", "✨"),
              Triple("max", "Voucher Max", "🔥"),
              Triple("shipping", "Free Shipping Voucher", "🚚"),
              Triple("category", "Category Voucher", "👗")
            ).forEach { (tabId, label, icon) ->
              val isSelected = activeTab == tabId
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(20.dp))
                  .background(if (isSelected) Color(0xFFFFEBEE) else Color(0xFFF5F5F5))
                  .border(
                    width = 1.dp,
                    color = if (isSelected) Color(0xFFE53935) else Color(0xFFE0E0E0),
                    shape = RoundedCornerShape(20.dp)
                  )
                  .clickable { activeTab = tabId }
                  .padding(horizontal = 12.dp, vertical = 6.dp)
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Text(text = icon, fontSize = 12.sp)
                  Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color(0xFFE53935) else DarkEspresso
                  )
                }
              }
            }
          }
        }

        // 3. Scrollable Voucher List Content
        LazyColumn(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // TOP HORIZONTAL CAROUSEL: VOUCHER MAX (Screenshot 2)
          if (activeTab == "all" || activeTab == "max") {
            item {
              Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    Text(text = "👑", fontSize = 14.sp)
                    Text(
                      text = "শীর্ষ মেগা সেভার্স (Voucher Max)",
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Black,
                      color = DarkEspresso
                    )
                  }
                  Text(
                    text = "বড় অর্ডারে সর্বোচ্চ সাশ্রয়",
                    fontSize = 10.sp,
                    color = Color.Gray
                  )
                }

                Spacer(modifier = Modifier.height(4.dp))

                LazyRow(
                  horizontalArrangement = Arrangement.spacedBy(10.dp),
                  contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                  items(defaultVouchersList.filter { it.section == "max" }) { v ->
                    val isCollected = collectedVoucherIds.contains(v.id)
                    Card(
                      modifier = Modifier
                        .width(260.dp)
                        .clip(RoundedCornerShape(16.dp)),
                      shape = RoundedCornerShape(16.dp),
                      colors = CardDefaults.cardColors(containerColor = Color.White),
                      border = BorderStroke(1.5.dp, Color(0xFFFFCDD2)),
                      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                      Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                          modifier = Modifier.fillMaxWidth(),
                          horizontalArrangement = Arrangement.SpaceBetween,
                          verticalAlignment = Alignment.Top
                        ) {
                          Column {
                            Box(
                              modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFFFEBEE))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                              Text(
                                text = v.tag,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFE53935)
                              )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                              text = v.discountText,
                              fontSize = 20.sp,
                              fontWeight = FontWeight.Black,
                              color = Color(0xFFE53935)
                            )
                            Text(
                              text = v.minSpendText,
                              fontSize = 10.sp,
                              color = Color.Gray
                            )
                          }

                          Column(horizontalAlignment = Alignment.End) {
                            Text(
                              text = v.title,
                              fontSize = 12.sp,
                              fontWeight = FontWeight.Black,
                              color = DarkEspresso
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                              text = v.validity,
                              fontSize = 9.sp,
                              color = Color.Gray
                            )
                          }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                          modifier = Modifier.fillMaxWidth(),
                          horizontalArrangement = Arrangement.SpaceBetween,
                          verticalAlignment = Alignment.CenterVertically
                        ) {
                          Text(
                            text = "T&C",
                            fontSize = 10.sp,
                            color = Color.Gray,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable { selectedTcItem = v }
                          )

                          Button(
                            onClick = { if (!isCollected) onCollectVoucher(v.id) },
                            enabled = !isCollected,
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                              containerColor = if (isCollected) Color(0xFFE0E0E0) else Color(0xFFE53935),
                              contentColor = if (isCollected) Color.Gray else Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                          ) {
                            Text(
                              text = if (isCollected) "Collected ✓" else "Collect",
                              fontSize = 11.sp,
                              fontWeight = FontWeight.Bold
                            )
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }

          // SECTION 1: সাজগোজ Mega Voucher (Matches Screenshot 2)
          if (activeTab == "all" || activeTab == "max" || activeTab == "category") {
            item {
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFFFEBEE)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
              ) {
                Column(modifier = Modifier.padding(14.dp)) {
                  // Section Header: সাজগোজ Mega Voucher & Collect all button
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                      Box(
                        modifier = Modifier
                          .size(30.dp)
                          .clip(RoundedCornerShape(8.dp))
                          .background(Color(0xFFFFEBEE)),
                        contentAlignment = Alignment.Center
                      ) {
                        Text(text = "🎟️", fontSize = 16.sp)
                      }
                      Column {
                        Text(
                          text = "সাজগোজ Mega Voucher",
                          fontSize = 15.sp,
                          fontWeight = FontWeight.Black,
                          color = DarkEspresso
                        )
                        Text(
                          text = "Platform Wide Sajgoj Vouchers",
                          fontSize = 10.sp,
                          color = Color.Gray
                        )
                      }
                    }

                    OutlinedButton(
                      onClick = {
                        val megaIds = defaultVouchersList.filter { it.section == "mega" }.map { it.id }
                        onCollectAll(megaIds)
                      },
                      shape = RoundedCornerShape(20.dp),
                      border = BorderStroke(1.dp, Color(0xFFE53935)),
                      colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE53935)),
                      contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                      modifier = Modifier.height(28.dp)
                    ) {
                      Text(text = "Collect all", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                  }

                  HorizontalDivider(color = Color(0xFFF5F5F5), thickness = 1.dp)
                  Spacer(modifier = Modifier.height(10.dp))

                  // Mega Voucher Tickets
                  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    defaultVouchersList.filter { it.section == "mega" }.forEach { v ->
                      val isCollected = collectedVoucherIds.contains(v.id)
                      Box(
                        modifier = Modifier
                          .fillMaxWidth()
                          .clip(RoundedCornerShape(14.dp))
                          .background(
                            Brush.horizontalGradient(
                              listOf(Color.White, Color.White, Color(0xFFFFF7F8))
                            )
                          )
                          .border(1.dp, Color(0xFFFFEBEE), RoundedCornerShape(14.dp))
                          .padding(10.dp)
                      ) {
                        Row(
                          modifier = Modifier.fillMaxWidth(),
                          verticalAlignment = Alignment.CenterVertically,
                          horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                          // Left Amount & Min Spend
                          Column(
                            modifier = Modifier
                              .width(90.dp)
                              .padding(end = 6.dp)
                          ) {
                            Box(
                              modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFFFEBEE))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                              Text(text = v.tag, fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color(0xFFE53935))
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                              text = v.discountText,
                              fontSize = 15.sp,
                              fontWeight = FontWeight.Black,
                              color = Color(0xFFE53935)
                            )
                            Text(
                              text = v.minSpendText,
                              fontSize = 9.sp,
                              color = Color.Gray,
                              maxLines = 1,
                              overflow = TextOverflow.Ellipsis
                            )
                          }

                          // Middle Title, Bangla Title & Validity
                          Column(
                            modifier = Modifier
                              .weight(1f)
                              .padding(horizontal = 4.dp)
                          ) {
                            Text(
                              text = v.title,
                              fontSize = 12.sp,
                              fontWeight = FontWeight.Bold,
                              color = DarkEspresso,
                              maxLines = 1,
                              overflow = TextOverflow.Ellipsis
                            )
                            Text(
                              text = v.banglaTitle,
                              fontSize = 10.sp,
                              color = RoyalMaroon,
                              maxLines = 1,
                              overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                              verticalAlignment = Alignment.CenterVertically,
                              horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                              Text(text = v.validity, fontSize = 9.sp, color = Color.Gray)
                              Text(text = "•", fontSize = 9.sp, color = Color.Gray)
                              Text(
                                text = "T&C",
                                fontSize = 9.sp,
                                color = Color.Gray,
                                textDecoration = TextDecoration.Underline,
                                modifier = Modifier.clickable { selectedTcItem = v }
                              )
                            }
                          }

                          // Right Collect Button
                          Button(
                            onClick = { if (!isCollected) onCollectVoucher(v.id) },
                            enabled = !isCollected,
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                              containerColor = if (isCollected) Color(0xFFEEEEEE) else Color(0xFFE53935),
                              contentColor = if (isCollected) Color.Gray else Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                          ) {
                            Text(
                              text = if (isCollected) "Collected ✓" else "Collect",
                              fontSize = 10.sp,
                              fontWeight = FontWeight.Bold
                            )
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }

          // SECTION 2: Free shipping vouchers (Matches Screenshot 3)
          if (activeTab == "all" || activeTab == "shipping") {
            item {
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE0F2F1)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
              ) {
                Column(modifier = Modifier.padding(14.dp)) {
                  // Section Header: Free shipping vouchers & Collect all
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                      Box(
                        modifier = Modifier
                          .size(30.dp)
                          .clip(RoundedCornerShape(8.dp))
                          .background(Color(0xFFE0F2F1)),
                        contentAlignment = Alignment.Center
                      ) {
                        Text(text = "🚚", fontSize = 16.sp)
                      }
                      Column {
                        Text(
                          text = "Free shipping vouchers",
                          fontSize = 15.sp,
                          fontWeight = FontWeight.Black,
                          color = DarkEspresso
                        )
                        Text(
                          text = "ফ্রি ও ছাড়যুক্ত ডেলিভারি ভাউচার",
                          fontSize = 10.sp,
                          color = Color.Gray
                        )
                      }
                    }

                    OutlinedButton(
                      onClick = {
                        val shipIds = defaultVouchersList.filter { it.section == "shipping" }.map { it.id }
                        onCollectAll(shipIds)
                      },
                      shape = RoundedCornerShape(20.dp),
                      border = BorderStroke(1.dp, Color(0xFF00897B)),
                      colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00897B)),
                      contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                      modifier = Modifier.height(28.dp)
                    ) {
                      Text(text = "Collect all", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                  }

                  HorizontalDivider(color = Color(0xFFF5F5F5), thickness = 1.dp)
                  Spacer(modifier = Modifier.height(10.dp))

                  // Shipping Tickets List
                  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    defaultVouchersList.filter { it.section == "shipping" }.forEach { v ->
                      val isCollected = collectedVoucherIds.contains(v.id)
                      Box(
                        modifier = Modifier
                          .fillMaxWidth()
                          .clip(RoundedCornerShape(14.dp))
                          .background(
                            Brush.horizontalGradient(
                              listOf(Color.White, Color.White, Color(0xFFF0FDF4))
                            )
                          )
                          .border(1.dp, Color(0xFFE0F2F1), RoundedCornerShape(14.dp))
                          .padding(10.dp)
                      ) {
                        Row(
                          modifier = Modifier.fillMaxWidth(),
                          verticalAlignment = Alignment.CenterVertically,
                          horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                          // Left Amount & Min Spend
                          Column(
                            modifier = Modifier
                              .width(90.dp)
                              .padding(end = 6.dp)
                          ) {
                            Text(
                              text = v.discountText,
                              fontSize = 16.sp,
                              fontWeight = FontWeight.Black,
                              color = Color(0xFF00897B)
                            )
                            Text(
                              text = v.minSpendText,
                              fontSize = 9.sp,
                              color = Color.Gray,
                              maxLines = 1,
                              overflow = TextOverflow.Ellipsis
                            )
                          }

                          // Middle Title, Bangla Title & Validity
                          Column(
                            modifier = Modifier
                              .weight(1f)
                              .padding(horizontal = 4.dp)
                          ) {
                            Text(
                              text = v.title,
                              fontSize = 12.sp,
                              fontWeight = FontWeight.Bold,
                              color = DarkEspresso,
                              maxLines = 1,
                              overflow = TextOverflow.Ellipsis
                            )
                            Text(
                              text = v.banglaTitle,
                              fontSize = 10.sp,
                              color = Color(0xFF00695C),
                              maxLines = 1,
                              overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                              verticalAlignment = Alignment.CenterVertically,
                              horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                              Text(text = v.validity, fontSize = 9.sp, color = Color.Gray)
                              Text(text = "•", fontSize = 9.sp, color = Color.Gray)
                              Text(
                                text = "T&C",
                                fontSize = 9.sp,
                                color = Color.Gray,
                                textDecoration = TextDecoration.Underline,
                                modifier = Modifier.clickable { selectedTcItem = v }
                              )
                            }
                          }

                          // Right Collect / Collect x2 Button
                          Column(horizontalAlignment = Alignment.End) {
                            if (v.badge.isNotEmpty()) {
                              Box(
                                modifier = Modifier
                                  .clip(RoundedCornerShape(4.dp))
                                  .background(Color(0xFF00897B))
                                  .padding(horizontal = 4.dp, vertical = 1.dp)
                              ) {
                                Text(
                                  text = v.badge,
                                  fontSize = 8.sp,
                                  fontWeight = FontWeight.Black,
                                  color = Color.White
                                )
                              }
                              Spacer(modifier = Modifier.height(2.dp))
                            }

                            Button(
                              onClick = { if (!isCollected) onCollectVoucher(v.id) },
                              enabled = !isCollected,
                              shape = RoundedCornerShape(20.dp),
                              colors = ButtonDefaults.buttonColors(
                                containerColor = if (isCollected) Color(0xFFEEEEEE) else Color(0xFF00897B),
                                contentColor = if (isCollected) Color.Gray else Color.White
                              ),
                              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                              modifier = Modifier.height(30.dp)
                            ) {
                              Text(
                                text = if (isCollected) "Collected ✓" else (if (v.badge.isNotEmpty()) "Collect ${v.badge}" else "Collect"),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                              )
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

        // 4. Bottom Quick Summary Bar
        Surface(
          color = Color.White,
          shadowElevation = 8.dp
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "সংগৃহীত ভাউচার: ${collectedVoucherIds.size} টি",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = RoyalMaroon
              )
              Text(
                text = "অর্ডারে স্বয়ংক্রিয়ভাবে সর্বোত্তম ছাড় কার্যকর হবে",
                fontSize = 10.sp,
                color = Color.Gray
              )
            }

            Button(
              onClick = onDismiss,
              shape = RoundedCornerShape(20.dp),
              colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon),
              contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp)
            ) {
              Text(text = "শপিং শুরু করুন 🛍️", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }

  // Terms and Conditions Dialog
  selectedTcItem?.let { item ->
    Dialog(onDismissRequest = { selectedTcItem = null }) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(text = "📜", fontSize = 18.sp)
              Text(
                text = "Terms & Conditions",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = DarkEspresso
              )
            }
            IconButton(onClick = { selectedTcItem = null }, modifier = Modifier.size(28.dp)) {
              Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
            }
          }

          Spacer(modifier = Modifier.height(10.dp))
          HorizontalDivider(color = Color(0xFFEEEEEE))
          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "${item.discountText} - ${item.title}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            color = if (item.section == "shipping") Color(0xFF00897B) else Color(0xFFE53935)
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(text = item.banglaTitle, fontSize = 12.sp, color = RoyalMaroon, fontWeight = FontWeight.Medium)
          Spacer(modifier = Modifier.height(8.dp))
          Text(text = item.minSpendText, fontSize = 11.sp, color = Color.Gray)
          Text(text = "মেয়াদ: ${item.validity}", fontSize = 11.sp, color = Color.Gray)
          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = item.terms.ifEmpty { "এই ভাউচারটি শুধুমাত্র সাজগোজ ফ্যাশনের অ্যাপ ও ওয়েবসাইট অর্ডারে প্রযোজ্য। একটি অর্ডারে স্বয়ংক্রিয়ভাবে সর্বোত্তম ভাউচার কার্যকর হবে।" },
            fontSize = 12.sp,
            color = DarkEspresso,
            lineHeight = 18.sp
          )

          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = { selectedTcItem = null },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon)
          ) {
            Text("বুঝেছি", fontSize = 13.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
