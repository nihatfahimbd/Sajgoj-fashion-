package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.B2G1Config
import com.example.data.model.Product
import com.example.data.model.StoreConfig

@Composable
fun AdminB2G1Hub(
  storeConfig: StoreConfig,
  products: List<Product>,
  onSaveStoreConfig: (StoreConfig) -> Unit,
  onShowToast: (String) -> Unit
) {
  val b2g1 = storeConfig.b2g1
  var eligibleSearch by remember { mutableStateOf("") }
  var eligibleCategory by remember { mutableStateOf("All") }
  var freeSearch by remember { mutableStateOf("") }
  var titleInput by remember(b2g1.title) { mutableStateOf(b2g1.title) }
  var subtitleInput by remember(b2g1.subtitle) { mutableStateOf(b2g1.subtitle) }

  val activeProducts = remember(products) { products.filter { !it.isDraft } }
  val categories = remember(activeProducts) {
    listOf("All") + activeProducts.map { it.category.trim() }.filter { it.isNotBlank() }.distinct()
  }

  val eligibleIds = b2g1.eligibleProductIds
  val selectedEligibleProducts = remember(activeProducts, eligibleIds) {
    eligibleIds.mapNotNull { id -> activeProducts.find { it.id == id } }
  }
  val selectedFreeProduct = remember(activeProducts, b2g1.freeProductId) {
    activeProducts.find { it.id == b2g1.freeProductId }
  }

  val filteredEligibleList = remember(activeProducts, eligibleSearch, eligibleCategory) {
    val q = eligibleSearch.trim().lowercase()
    activeProducts.filter { p ->
      val catMatch = eligibleCategory == "All" || p.category.equals(eligibleCategory, ignoreCase = true)
      val qMatch = q.isEmpty() || "${p.name} ${p.sku} ${p.category}".lowercase().contains(q)
      catMatch && qMatch
    }
  }

  val filteredFreeList = remember(activeProducts, freeSearch) {
    val q = freeSearch.trim().lowercase()
    if (q.isEmpty()) activeProducts
    else activeProducts.filter { p ->
      "${p.name} ${p.sku} ${p.category}".lowercase().contains(q)
    }
  }

  fun updateB2G1(transform: (B2G1Config) -> B2G1Config) {
    onSaveStoreConfig(storeConfig.copy(b2g1 = transform(storeConfig.b2g1)))
  }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(bottom = 28.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // 1. Header & Enable Switch
    item {
      GlossyAdminCard {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "🎁 Buy 2 Get 1 Free ম্যানেজার",
              fontSize = 15.sp,
              fontWeight = FontWeight.Black,
              color = AdminInkPrimary
            )
            Text(
              text = "শুধুমাত্র আপনার নির্বাচিত পণ্যগুলোতেই ২+১ অফার চালু হবে। অর্ডার করলে ৩টি পণ্যের স্টক অটোমেটিক কমবে।",
              fontSize = 11.sp,
              color = AdminInkMuted,
              lineHeight = 15.sp
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Switch(
            checked = b2g1.enabled,
            onCheckedChange = { checked ->
              updateB2G1 { it.copy(enabled = checked) }
              onShowToast(if (checked) "✅ Buy 2 Get 1 অফার চালু করা হয়েছে!" else "⏸️ Buy 2 Get 1 অফার সাময়িক বন্ধ করা হয়েছে।")
            },
            colors = SwitchDefaults.colors(
              checkedThumbColor = Color.White,
              checkedTrackColor = AdminV2Emerald
            ),
            modifier = Modifier.testTag("b2g1_enabled_switch")
          )
        }
      }
    }

    // 2. Step 1: Search & Select Eligible Products
    item {
      GlossyAdminCard {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "ধাপ ১: অফারের প্রোডাক্ট খুঁজুন ও নির্বাচন করুন",
              fontSize = 13.5.sp,
              fontWeight = FontWeight.Black,
              color = AdminV2CyanDeep
            )
            Text(
              text = "অন্য কোনো পণ্যে এই অফার দেখাবে না — শুধু সিলেক্ট করা পণ্যেই কাজ করবে",
              fontSize = 10.5.sp,
              color = AdminInkMuted
            )
          }
          Surface(
            shape = RoundedCornerShape(50),
            color = if (eligibleIds.isNotEmpty()) AdminV2EmeraldSoft else Color(0xFFFEF2F2),
            border = BorderStroke(1.dp, if (eligibleIds.isNotEmpty()) AdminV2Emerald else Color(0xFFFCA5A5))
          ) {
            Text(
              text = "${eligibleIds.size}টি সিলেক্টেড",
              fontSize = 10.5.sp,
              fontWeight = FontWeight.Black,
              color = if (eligibleIds.isNotEmpty()) Color(0xFF047857) else Color(0xFFB91C1C),
              modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
            )
          }
        }

        if (selectedEligibleProducts.isNotEmpty()) {
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            selectedEligibleProducts.forEach { prod ->
              Surface(
                shape = RoundedCornerShape(50),
                color = AdminV2EmeraldSoft,
                border = BorderStroke(1.dp, AdminV2Emerald),
                modifier = Modifier.clickable {
                  updateB2G1 { cfg -> cfg.copy(eligibleProductIds = cfg.eligibleProductIds.filterNot { it == prod.id }) }
                }
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp),
                  modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                ) {
                  Text(
                    text = prod.name.take(20),
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF065F46),
                    maxLines = 1
                  )
                  Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "সরান",
                    tint = Color(0xFF065F46),
                    modifier = Modifier.size(13.dp)
                  )
                }
              }
            }
            OutlinedButton(
              onClick = {
                updateB2G1 { it.copy(eligibleProductIds = emptyList()) }
                onShowToast("অফারের সব প্রোডাক্ট তালিকা থেকে সরানো হয়েছে।")
              },
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
              modifier = Modifier.heightIn(min = 32.dp)
            ) {
              Text("সব সরান", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
            }
          }
        } else {
          Spacer(modifier = Modifier.height(8.dp))
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFFFFFBEB),
            border = BorderStroke(1.dp, Color(0xFFFCD34D)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = "⚠️ কোনো প্রোডাক্ট নির্বাচিত নেই। নিচে সার্চ করে যেসব পণ্যে Buy 2 Get 1 অফার দিতে চান সেগুলো যুক্ত করুন।",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF92400E),
              modifier = Modifier.padding(10.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = eligibleSearch,
          onValueChange = { eligibleSearch = it },
          placeholder = { Text("প্রোডাক্টের নাম বা SKU দিয়ে খুঁজুন...", fontSize = 12.sp) },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AdminV2CyanDeep) },
          trailingIcon = {
            if (eligibleSearch.isNotEmpty()) {
              IconButton(onClick = { eligibleSearch = "" }) {
                Icon(Icons.Default.Close, contentDescription = "মুছুন", modifier = Modifier.size(16.dp))
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(14.dp),
          colors = adminOutlinedTextFieldColors(),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("b2g1_eligible_search")
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          categories.forEach { cat ->
            val selected = eligibleCategory == cat
            Surface(
              shape = RoundedCornerShape(50),
              color = if (selected) AdminV2CyanDeep else Color(0xFFF1F5F9),
              border = BorderStroke(1.dp, if (selected) AdminV2CyanDeep else Color(0xFFCBD5E1)),
              modifier = Modifier.clickable { eligibleCategory = cat }
            ) {
              Text(
                text = if (cat == "All") "সব ক্যাটাগরি" else cat,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (selected) Color.White else AdminInkPrimary,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          if (filteredEligibleList.isEmpty()) {
            Text(
              text = "কোনো প্রোডাক্ট পাওয়া যায়নি।",
              fontSize = 11.5.sp,
              color = AdminInkMuted,
              modifier = Modifier.padding(vertical = 8.dp)
            )
          } else {
            filteredEligibleList.take(20).forEach { prod ->
              val isSelected = eligibleIds.contains(prod.id)
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) Color(0xFFECFDF5) else Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, if (isSelected) AdminV2Emerald else Color(0xFFE2E8F0)),
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable {
                    val nextIds = if (isSelected) {
                      eligibleIds.filterNot { it == prod.id }
                    } else {
                      eligibleIds + prod.id
                    }
                    updateB2G1 { it.copy(eligibleProductIds = nextIds) }
                  }
                  .testTag("b2g1_eligible_toggle_${prod.id}")
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  AppImage(
                    model = prod.imageUrl,
                    contentDescription = prod.name,
                    modifier = Modifier
                      .size(42.dp)
                      .clip(RoundedCornerShape(8.dp))
                  )
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = prod.name,
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      color = AdminInkPrimary,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis
                    )
                    Text(
                      text = "${prod.sku} • ৳${prod.price} • স্টক: ${prod.stock}",
                      fontSize = 10.5.sp,
                      color = AdminInkMuted
                    )
                  }
                  Surface(
                    shape = RoundedCornerShape(50),
                    color = if (isSelected) AdminV2Emerald else AdminV2CyanDeep
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(4.dp),
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                      if (isSelected) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                      }
                      Text(
                        text = if (isSelected) "যুক্ত আছে" else "+ যুক্ত করুন",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
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

    // 3. Step 2: Search & Select Free Gift Product
    item {
      GlossyAdminCard {
        Text(
          text = "ধাপ ২: ফ্রি প্রোডাক্ট (Free Gift) সিলেক্ট করুন",
          fontSize = 13.5.sp,
          fontWeight = FontWeight.Black,
          color = Color(0xFF047857)
        )
        Text(
          text = "কাস্টমার ২টি নির্বাচিত প্রোডাক্ট কার্টে যোগ করলে নিচের এই প্রোডাক্টটি ফ্রি হিসেবে যুক্ত হবে এবং স্টক থেকেও ১টি কমবে।",
          fontSize = 10.5.sp,
          color = AdminInkMuted
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (selectedFreeProduct != null) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFECFDF5),
            border = BorderStroke(1.2.dp, AdminV2Emerald),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              AppImage(
                model = selectedFreeProduct.imageUrl,
                contentDescription = selectedFreeProduct.name,
                modifier = Modifier
                  .size(48.dp)
                  .clip(RoundedCornerShape(10.dp))
              )
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "🎁 বর্তমান ফ্রি উপহার",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Black,
                  color = Color(0xFF047857)
                )
                Text(
                  text = selectedFreeProduct.name,
                  fontSize = 12.5.sp,
                  fontWeight = FontWeight.Black,
                  color = AdminInkPrimary,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Text(
                  text = "রেগুলার মূল্য: ৳${selectedFreeProduct.price} → ফ্রি (৳০) • স্টক: ${selectedFreeProduct.stock}",
                  fontSize = 10.5.sp,
                  color = if (selectedFreeProduct.stock > 0) Color(0xFF065F46) else Color(0xFFDC2626),
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
          Spacer(modifier = Modifier.height(10.dp))
        }

        OutlinedTextField(
          value = freeSearch,
          onValueChange = { freeSearch = it },
          placeholder = { Text("ফ্রি গিফট প্রোডাক্ট খুঁজুন (নাম/SKU)...", fontSize = 12.sp) },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AdminV2Emerald) },
          trailingIcon = {
            if (freeSearch.isNotEmpty()) {
              IconButton(onClick = { freeSearch = "" }) {
                Icon(Icons.Default.Close, contentDescription = "মুছুন", modifier = Modifier.size(16.dp))
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(14.dp),
          colors = adminOutlinedTextFieldColors(),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("b2g1_free_search")
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          filteredFreeList.take(15).forEach { prod ->
            val isChosen = b2g1.freeProductId == prod.id
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (isChosen) Color(0xFFECFDF5) else Color(0xFFFAFAFC),
              border = BorderStroke(1.dp, if (isChosen) AdminV2Emerald else Color(0xFFE2E8F0)),
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  updateB2G1 { it.copy(freeProductId = prod.id) }
                  onShowToast("🎁 \"${prod.name}\" ফ্রি প্রোডাক্ট হিসেবে নির্বাচিত হয়েছে!")
                }
                .testTag("b2g1_free_select_${prod.id}")
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                AppImage(
                  model = prod.imageUrl,
                  contentDescription = prod.name,
                  modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                )
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = prod.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AdminInkPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  Text(
                    text = "${prod.sku} • ৳${prod.price} • স্টক: ${prod.stock}",
                    fontSize = 10.sp,
                    color = AdminInkMuted
                  )
                }
                Surface(
                  shape = RoundedCornerShape(50),
                  color = if (isChosen) AdminV2Emerald else Color(0xFFE2E8F0)
                ) {
                  Text(
                    text = if (isChosen) "✓ ফ্রি গিফট" else "সিলেক্ট করুন",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isChosen) Color.White else AdminInkPrimary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                  )
                }
              }
            }
          }
        }
      }
    }

    // 4. Step 3: Offer Threshold & Banner Copy Settings
    item {
      GlossyAdminCard {
        Text(
          text = "ধাপ ৩: শর্ত ও ব্যানার টেক্সট",
          fontSize = 13.5.sp,
          fontWeight = FontWeight.Black,
          color = AdminInkPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "সর্বনিম্ন নির্বাচিত পণ্য সংখ্যা",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = AdminInkPrimary
            )
            Text(
              text = "কয়টি নির্বাচিত পণ্য কার্টে নিলে ফ্রি গিফট আনলক হবে",
              fontSize = 10.5.sp,
              color = AdminInkMuted
            )
          }
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            IconButton(
              onClick = {
                val next = (b2g1.minItems - 1).coerceAtLeast(1)
                updateB2G1 { it.copy(minItems = next) }
              },
              modifier = Modifier
                .minimumInteractiveComponentSize()
                .clip(CircleShape)
                .background(Color(0xFFF1F5F9))
            ) {
              Icon(Icons.Default.Remove, contentDescription = "কমান", modifier = Modifier.size(16.dp))
            }
            Text(
              text = "${b2g1.minItems} টি",
              fontSize = 13.sp,
              fontWeight = FontWeight.Black,
              color = AdminV2CyanDeep
            )
            IconButton(
              onClick = {
                val next = (b2g1.minItems + 1).coerceAtMost(10)
                updateB2G1 { it.copy(minItems = next) }
              },
              modifier = Modifier
                .minimumInteractiveComponentSize()
                .clip(CircleShape)
                .background(Color(0xFFF1F5F9))
            ) {
              Icon(Icons.Default.Add, contentDescription = "বাড়ান", modifier = Modifier.size(16.dp))
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = Color(0xFFE2E8F0))
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = titleInput,
          onValueChange = { titleInput = it },
          label = { Text("অফার শিরোনাম", fontSize = 11.5.sp) },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = adminOutlinedTextFieldColors(),
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = subtitleInput,
          onValueChange = { subtitleInput = it },
          label = { Text("অফার সাবটাইটেল", fontSize = 11.5.sp) },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = adminOutlinedTextFieldColors(),
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(
          onClick = {
            updateB2G1 {
              it.copy(
                title = titleInput.trim().ifBlank { "Buy 2 Get 1 Free অফার" },
                subtitle = subtitleInput.trim().ifBlank { "নির্বাচিত ২টি পণ্য কার্টে নিলেই পাচ্ছেন ১টি প্রিমিয়াম উপহার একদম ফ্রি!" }
              )
            }
            onShowToast("✅ Buy 2 Get 1 সেটিংস সেভ করা হয়েছে!")
          },
          colors = ButtonDefaults.buttonColors(containerColor = AdminV2CyanDeep),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .testTag("b2g1_save_settings_btn")
        ) {
          Text("সেটিংস সেভ করুন", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color.White)
        }
      }
    }
  }
}
