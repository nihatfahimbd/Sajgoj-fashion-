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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ShippingInfoConfig
import com.example.data.model.ShippingOptionItem
import com.example.data.model.StoreConfig
import com.example.data.model.defaultShippingOptions

private val shippingColorPresets = listOf(
  "#0284C7" to "Deep Sky",
  "#0369A1" to "Ocean Blue",
  "#0F172A" to "Deep Navy",
  "#FFFFFF" to "Pure White",
  "#F0F9FF" to "Ice Sky",
  "#FEF3C7" to "Warm Cream",
  "#059669" to "Emerald",
  "#E11D48" to "Rose",
  "#D97706" to "Amber",
  "#7C3AED" to "Violet"
)

fun parseShippingHexColor(hex: String, fallback: Color): Color {
  return try {
    val clean = hex.trim().removePrefix("#")
    val parsed = when (clean.length) {
      6 -> android.graphics.Color.parseColor("#$clean")
      8 -> android.graphics.Color.parseColor("#$clean")
      else -> return fallback
    }
    Color(parsed)
  } catch (_: Exception) {
    fallback
  }
}

@Composable
fun AdminShippingInfoHub(
  storeConfig: StoreConfig,
  onSaveStoreConfig: (StoreConfig) -> Unit,
  onShowToast: (String) -> Unit
) {
  var shippingCfg by remember(storeConfig.shippingInfo) { mutableStateOf(storeConfig.shippingInfo) }
  var editingOption by remember { mutableStateOf<ShippingOptionItem?>(null) }
  var showOptionDialog by remember { mutableStateOf(false) }

  fun persist(updated: ShippingInfoConfig, message: String = "🚚 Shipping info সংরক্ষিত হয়েছে!") {
    shippingCfg = updated
    onSaveStoreConfig(storeConfig.copy(shippingInfo = updated))
    onShowToast(message)
  }

  val previewSectionBg = parseShippingHexColor(shippingCfg.sectionBgColor, Color(0xFFF0F9FF))
  val previewBlockBg = parseShippingHexColor(shippingCfg.blockBgColor, Color.White)
  val previewBorder = parseShippingHexColor(shippingCfg.borderColor, Color(0xFF0284C7))
  val previewText = parseShippingHexColor(shippingCfg.textColor, Color(0xFF0F172A))
  val previewBtnBg = parseShippingHexColor(shippingCfg.buttonBgColor, Color(0xFF0284C7))
  val previewBtnText = parseShippingHexColor(shippingCfg.buttonTextColor, Color.White)

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(12.dp),
    contentPadding = PaddingValues(bottom = 24.dp)
  ) {
    item {
      AdminVibrantHeroCard(
        title = "Shipping info & Delivery Control",
        subtitle = "চেকআউট ডেলিভারি অপশন Add/Edit/Delete ও ফুল কালার কাস্টমাইজেশন",
        icon = Icons.Default.LocalShipping,
        brush = adminIndigoSkyBrush(),
        trailingContent = {
          Button(
            onClick = {
              editingOption = null
              showOptionDialog = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = AdminV2CyanDeep),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .heightIn(min = 48.dp)
              .testTag("add_shipping_option_button")
          ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "নতুন Shipping অপশন যোগ", modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Add Shipping", fontSize = 11.5.sp, fontWeight = FontWeight.ExtraBold)
          }
        }
      )
    }

    // Live Checkout Preview Card
    item {
      GlossyAdminCard(cornerRadius = 18.dp, contentPadding = 14.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "👁️ Checkout লাইভ প্রিভিউ",
              fontSize = 13.5.sp,
              fontWeight = FontWeight.Black,
              color = AdminInkPrimary
            )
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = AdminV2SkySoft
            ) {
              Text(
                text = "${shippingCfg.options.count { it.enabled }} টি সক্রিয়",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = AdminV2CyanDeep,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }

          Column(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(previewSectionBg)
              .border(1.5.dp, previewBorder, RoundedCornerShape(14.dp))
              .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              text = shippingCfg.sectionTitle.ifBlank { "Delivery Type & Shipping Info" },
              fontSize = 13.sp,
              fontWeight = FontWeight.ExtraBold,
              color = previewText
            )
            shippingCfg.options.filter { it.enabled }.forEachIndexed { index, opt ->
              val isSelected = index == 0
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .background(previewBlockBg)
                  .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) previewBorder else previewBorder.copy(alpha = 0.45f),
                    shape = RoundedCornerShape(10.dp)
                  )
                  .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = opt.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = previewText)
                    if (opt.badge.isNotBlank()) {
                      Surface(shape = RoundedCornerShape(6.dp), color = previewBtnBg) {
                        Text(
                          text = opt.badge,
                          fontSize = 9.5.sp,
                          fontWeight = FontWeight.Bold,
                          color = previewBtnText,
                          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                      }
                    }
                  }
                  if (opt.subtitle.isNotBlank()) {
                    Text(text = opt.subtitle, fontSize = 10.5.sp, color = previewText.copy(alpha = 0.78f))
                  }
                }
                Text(
                  text = if (opt.fee == 0) "ফ্রি" else "৳${opt.fee}",
                  fontSize = 12.5.sp,
                  fontWeight = FontWeight.Black,
                  color = previewBtnBg
                )
              }
            }
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(previewBtnBg)
                .padding(vertical = 8.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = shippingCfg.chandpurExpressNotice.ifBlank { "⚡ Express শুধুমাত্র চাঁদপুর জেলায়" },
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = previewBtnText
              )
            }
          }
        }
      }
    }

    // Section Text + Full Color Customization Panel
    item {
      GlossyAdminCard(cornerRadius = 18.dp, contentPadding = 14.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Icon(imageVector = Icons.Default.Palette, contentDescription = null, tint = AdminV2CyanDeep, modifier = Modifier.size(18.dp))
              Text(
                text = "ব্লক, বর্ডার, বাটন ও ব্যাকগ্রাউন্ড কালার এডিটর",
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Black,
                color = AdminInkPrimary
              )
            }
            TextButton(
              onClick = {
                persist(
                  ShippingInfoConfig(
                    options = shippingCfg.options,
                    chandpurStandardFee = shippingCfg.chandpurStandardFee
                  ),
                  "ডিফল্ট Shipping কালার রিসেট করা হয়েছে"
                )
              },
              modifier = Modifier.minimumInteractiveComponentSize()
            ) {
              Icon(imageVector = Icons.Default.RestartAlt, contentDescription = "রিসেট", modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(3.dp))
              Text("রিসেট", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }

          OutlinedTextField(
            value = shippingCfg.sectionTitle,
            onValueChange = { shippingCfg = shippingCfg.copy(sectionTitle = it) },
            label = { Text("Shipping সেকশন টাইটেল", fontSize = 11.5.sp) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = shippingCfg.chandpurExpressNotice,
            onValueChange = { shippingCfg = shippingCfg.copy(chandpurExpressNotice = it) },
            label = { Text("Express ডেলিভারি নোটিশ (চাঁদপুর রুল)", fontSize = 11.5.sp) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = shippingCfg.chandpurStandardFee.toString(),
            onValueChange = { raw ->
              val v = raw.filter(Char::isDigit).toIntOrNull() ?: 0
              shippingCfg = shippingCfg.copy(chandpurStandardFee = v)
            },
            label = { Text("চাঁদপুর Standard ডেলিভারি চার্জ (৳)", fontSize = 11.5.sp) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          )

          HorizontalDivider(color = AdminCardBorderSoft)

          ShippingColorPickerField(
            label = "ব্যাকগ্রাউন্ড কালার (Section Background)",
            value = shippingCfg.sectionBgColor,
            onChange = { shippingCfg = shippingCfg.copy(sectionBgColor = it) }
          )
          ShippingColorPickerField(
            label = "ব্লক কালার (Block / Card Color)",
            value = shippingCfg.blockBgColor,
            onChange = { shippingCfg = shippingCfg.copy(blockBgColor = it) }
          )
          ShippingColorPickerField(
            label = "বর্ডার কালার (Border Color)",
            value = shippingCfg.borderColor,
            onChange = { shippingCfg = shippingCfg.copy(borderColor = it) }
          )
          ShippingColorPickerField(
            label = "টেক্সট কালার (Text Color)",
            value = shippingCfg.textColor,
            onChange = { shippingCfg = shippingCfg.copy(textColor = it) }
          )
          ShippingColorPickerField(
            label = "বাটন কালার (Button Background)",
            value = shippingCfg.buttonBgColor,
            onChange = { shippingCfg = shippingCfg.copy(buttonBgColor = it) }
          )
          ShippingColorPickerField(
            label = "বাটন টেক্সট কালার (Button Text)",
            value = shippingCfg.buttonTextColor,
            onChange = { shippingCfg = shippingCfg.copy(buttonTextColor = it) }
          )

          Button(
            onClick = { persist(shippingCfg, "✅ Shipping info কালার ও সেটিংস সেভ হয়েছে!") },
            colors = ButtonDefaults.buttonColors(containerColor = AdminV2CyanDeep, contentColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .heightIn(min = 48.dp)
              .testTag("save_shipping_colors_button")
          ) {
            Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "কালার ও টেক্সট সেভ করুন", fontSize = 13.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // Shipping Methods CRUD Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "ডেলিভারি মেথড ও রুলস (${shippingCfg.options.size} টি)",
          fontSize = 14.sp,
          fontWeight = FontWeight.Black,
          color = AdminInkPrimary
        )
        OutlinedButton(
          onClick = {
            persist(
              shippingCfg.copy(options = defaultShippingOptions()),
              "ডিফল্ট Standard ও Express অপশন রিস্টোর হয়েছে"
            )
          },
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.heightIn(min = 48.dp)
        ) {
          Text("ডিফল্ট রিস্টোর", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    items(shippingCfg.options, key = { it.id }) { option ->
      GlossyAdminCard(
        cornerRadius = 16.dp,
        accentBorderColor = if (option.enabled) AdminV2Cyan else AdminCardBorderSoft,
        contentPadding = 13.dp
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Text(
                  text = option.title,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = AdminInkPrimary,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = if (option.deliveryType == "Express") Color(0xFFFEF3C7) else Color(0xFFE0F2FE)
                ) {
                  Text(
                    text = option.deliveryType,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (option.deliveryType == "Express") Color(0xFFB45309) else AdminV2CyanDeep,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
              if (option.subtitle.isNotBlank()) {
                Text(text = option.subtitle, fontSize = 11.5.sp, color = AdminInkMuted)
              }
            }

            Switch(
              checked = option.enabled,
              onCheckedChange = { checked ->
                val updatedList = shippingCfg.options.map {
                  if (it.id == option.id) it.copy(enabled = checked) else it
                }
                persist(shippingCfg.copy(options = updatedList), "${option.title} আপডেট হয়েছে")
              },
              colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AdminV2CyanDeep)
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(shape = RoundedCornerShape(8.dp), color = AdminV2IceSurface) {
              Text(
                text = "ফি: ৳${option.fee}" + if (option.freeAbove > 0) " (${option.freeAbove}৳+ ফ্রি)" else "",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = AdminInkPrimary,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFF0FDF4)) {
              Text(
                text = when (option.districtRule) {
                  "CHANDPUR_ONLY" -> "📍 শুধুমাত্র চাঁদপুর"
                  "CUSTOM" -> "📍 ${option.customDistricts.joinToString().ifBlank { "কাস্টম জেলা" }}"
                  else -> "📍 ৬৪ জেলা (সারা দেশ)"
                },
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF166534),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedButton(
              onClick = {
                editingOption = option
                showOptionDialog = true
              },
              shape = RoundedCornerShape(10.dp),
              border = BorderStroke(1.dp, AdminV2CyanDeep),
              modifier = Modifier
                .weight(1f)
                .heightIn(min = 48.dp)
            ) {
              Icon(imageVector = Icons.Default.Edit, contentDescription = "এডিট", tint = AdminV2CyanDeep, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Edit", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AdminV2CyanDeep)
            }

            OutlinedButton(
              onClick = {
                if (shippingCfg.options.size <= 1) {
                  onShowToast("⚠️ অন্তত ১টি ডেলিভারি অপশন থাকতে হবে।")
                } else {
                  val remaining = shippingCfg.options.filterNot { it.id == option.id }
                  persist(shippingCfg.copy(options = remaining), "🗑️ ${option.title} মুছে ফেলা হয়েছে")
                }
              },
              shape = RoundedCornerShape(10.dp),
              border = BorderStroke(1.dp, AdminV2Rose),
              modifier = Modifier
                .weight(1f)
                .heightIn(min = 48.dp)
            ) {
              Icon(imageVector = Icons.Default.Delete, contentDescription = "ডিলিট", tint = AdminV2Rose, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Delete", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AdminV2Rose)
            }
          }
        }
      }
    }
  }

  if (showOptionDialog) {
    ShippingOptionFormDialog(
      initial = editingOption,
      onDismiss = {
        showOptionDialog = false
        editingOption = null
      },
      onSave = { saved ->
        val exists = shippingCfg.options.any { it.id == saved.id }
        val nextList = if (exists) {
          shippingCfg.options.map { if (it.id == saved.id) saved else it }
        } else {
          shippingCfg.options + saved
        }
        persist(
          shippingCfg.copy(options = nextList),
          if (exists) "✅ ${saved.title} আপডেট হয়েছে!" else "✨ ${saved.title} যুক্ত হয়েছে!"
        )
        showOptionDialog = false
        editingOption = null
      }
    )
  }
}

@Composable
private fun ShippingColorPickerField(
  label: String,
  value: String,
  onChange: (String) -> Unit
) {
  val swatchColor = parseShippingHexColor(value, AdminV2CyanDeep)
  Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
    Text(text = label, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = AdminInkPrimary)
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(CircleShape)
          .background(swatchColor)
          .border(1.5.dp, AdminCardBorderWarm, CircleShape)
      )
      OutlinedTextField(
        value = value,
        onValueChange = onChange,
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.weight(1f)
      )
    }
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      shippingColorPresets.forEach { (hex, _) ->
        val c = parseShippingHexColor(hex, AdminV2CyanDeep)
        val selected = value.equals(hex, ignoreCase = true)
        Box(
          modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(c)
            .border(if (selected) 2.5.dp else 1.dp, if (selected) AdminInkPrimary else AdminCardBorderWarm, CircleShape)
            .clickable { onChange(hex) }
        )
      }
    }
  }
}

@Composable
private fun ShippingOptionFormDialog(
  initial: ShippingOptionItem?,
  onDismiss: () -> Unit,
  onSave: (ShippingOptionItem) -> Unit
) {
  var title by remember { mutableStateOf(initial?.title ?: "") }
  var subtitle by remember { mutableStateOf(initial?.subtitle ?: "") }
  var badge by remember { mutableStateOf(initial?.badge ?: "") }
  var feeStr by remember { mutableStateOf((initial?.fee ?: 100).toString()) }
  var freeAboveStr by remember { mutableStateOf((initial?.freeAbove ?: 0).toString()) }
  var estimatedDays by remember { mutableStateOf(initial?.estimatedDays ?: "২-৪ কার্যদিবস") }
  var deliveryType by remember { mutableStateOf(initial?.deliveryType ?: "Standard") }
  var districtRule by remember { mutableStateOf(initial?.districtRule ?: "ALL") }
  var customDistrictsStr by remember { mutableStateOf(initial?.customDistricts?.joinToString(", ") ?: "") }
  var enabled by remember { mutableStateOf(initial?.enabled ?: true) }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = Color.White,
    title = {
      Text(
        text = if (initial == null) "নতুন Shipping অপশন যোগ" else "Shipping অপশন এডিট",
        fontWeight = FontWeight.Black,
        fontSize = 16.sp
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("টাইটেল * (যেমন: Express Delivery)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = subtitle,
          onValueChange = { subtitle = it },
          label = { Text("বিস্তারিত বিবরণ") },
          maxLines = 2,
          modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = feeStr,
            onValueChange = { feeStr = it.filter(Char::isDigit) },
            label = { Text("চার্জ (৳)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = freeAboveStr,
            onValueChange = { freeAboveStr = it.filter(Char::isDigit) },
            label = { Text("ফ্রি (৳+ হলে, 0=না)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = badge,
            onValueChange = { badge = it },
            label = { Text("ব্যাজ (যেমন: শুধুমাত্র চাঁদপুর)") },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = estimatedDays,
            onValueChange = { estimatedDays = it },
            label = { Text("সময়কাল") },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
        }
        Text("Delivery Type:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          listOf("Standard", "Express").forEach { dt ->
            val selected = deliveryType == dt
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (selected) AdminV2CyanDeep else AdminV2IceSurface,
              border = BorderStroke(1.dp, if (selected) AdminV2CyanDeep else AdminCardBorderWarm),
              modifier = Modifier
                .weight(1f)
                .heightIn(min = 48.dp)
                .clickable { deliveryType = dt }
            ) {
              Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(8.dp)) {
                Text(dt, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (selected) Color.White else AdminInkPrimary)
              }
            }
          }
        }
        Text("জেলা রুল (District Availability):", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          listOf("ALL" to "৬৪ জেলা", "CHANDPUR_ONLY" to "শুধু চাঁদপুর", "CUSTOM" to "কাস্টম").forEach { (key, label) ->
            val selected = districtRule == key
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (selected) AdminV2CyanDeep else AdminV2IceSurface,
              border = BorderStroke(1.dp, if (selected) AdminV2CyanDeep else AdminCardBorderWarm),
              modifier = Modifier
                .weight(1f)
                .heightIn(min = 48.dp)
                .clickable { districtRule = key }
            ) {
              Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(6.dp)) {
                Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (selected) Color.White else AdminInkPrimary)
              }
            }
          }
        }
        if (districtRule == "CUSTOM") {
          OutlinedTextField(
            value = customDistrictsStr,
            onValueChange = { customDistrictsStr = it },
            label = { Text("জেলার নাম (কমা দিয়ে, যেমন: Dhaka, Chandpur)") },
            modifier = Modifier.fillMaxWidth()
          )
        }
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("চেকআউটে চালু থাকবে?", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          Switch(checked = enabled, onCheckedChange = { enabled = it })
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (title.isBlank()) return@Button
          val customDists = customDistrictsStr.split(",").map { it.trim() }.filter { it.isNotBlank() }
          onSave(
            ShippingOptionItem(
              id = initial?.id ?: "ship-${System.currentTimeMillis()}",
              title = title.trim(),
              subtitle = subtitle.trim(),
              badge = badge.trim(),
              fee = feeStr.toIntOrNull() ?: 0,
              freeAbove = freeAboveStr.toIntOrNull() ?: 0,
              estimatedDays = estimatedDays.trim().ifBlank { "২-৪ কার্যদিবস" },
              deliveryType = deliveryType,
              districtRule = districtRule,
              customDistricts = customDists,
              enabled = enabled
            )
          )
        },
        colors = ButtonDefaults.buttonColors(containerColor = AdminV2CyanDeep)
      ) {
        Text("সংরক্ষণ করুন")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("বাতিল")
      }
    }
  )
}
