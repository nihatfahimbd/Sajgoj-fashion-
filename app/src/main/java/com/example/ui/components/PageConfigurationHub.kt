package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PageConfigJson
import com.example.data.model.PageConfiguration
import com.example.data.model.Product
import com.example.data.model.StoreConfig
import org.json.JSONObject

private enum class PageConfigMainTab(val title: String) {
  DETAIL_PAGE("Product Details Page"),
  ADD_PRODUCT("Add Product Page"),
  MASTER("Master System"),
  DEVELOPER_JSON("Developer JSON")
}

@Composable
fun PageConfigurationHub(
  storeConfig: StoreConfig,
  products: List<Product>,
  onSaveStoreConfig: (StoreConfig) -> Unit,
  onShowToast: (String) -> Unit
) {
  var draft by remember(storeConfig.pageConfig) { mutableStateOf(storeConfig.pageConfig) }
  var activeTab by remember { mutableStateOf(PageConfigMainTab.DETAIL_PAGE) }
  var rawJsonText by remember { mutableStateOf(PageConfigJson.toJsonObject(draft).toString(2)) }
  var jsonError by remember { mutableStateOf<String?>(null) }

  LaunchedEffect(activeTab, draft) {
    if (activeTab == PageConfigMainTab.DEVELOPER_JSON) {
      rawJsonText = PageConfigJson.toJsonObject(draft).toString(2)
      jsonError = null
    }
  }

  fun persist(next: PageConfiguration, message: String = "✅ Page Configuration সেভ ও লাইভ হয়েছে!") {
    draft = next
    onSaveStoreConfig(storeConfig.copy(pageConfig = next))
    onShowToast(message)
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // Master Developer Hub Banner
    Surface(
      shape = RoundedCornerShape(18.dp),
      color = Color.White,
      border = BorderStroke(1.2.dp, Color(0xFF7DD3FC)),
      shadowElevation = 4.dp,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("page_configuration_hub")
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(adminIndigoSkyBrush())
            .padding(horizontal = 14.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
          ) {
            Icon(Icons.Default.Tune, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
            Column {
              Text("Page Configuration (Developer Studio)", fontSize = 15.sp, fontWeight = FontWeight.Black, color = Color.White)
              Text("ডিটেলস পেজ, অ্যাড প্রোডাক্ট ও মাস্টার কনফিগারেশন — লাইভ ভিউ সহ", fontSize = 11.sp, color = Color.White.copy(alpha = 0.9f))
            }
          }
        }

        // Action Bar (Save + Reset)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Button(
            onClick = { persist(draft) },
            colors = ButtonDefaults.buttonColors(containerColor = AdminV2CyanDeep, contentColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .weight(1.2f)
              .heightIn(min = 48.dp)
              .testTag("page_config_save_button")
          ) {
            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("সেভ ও পাবলিশ", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
          }

          OutlinedButton(
            onClick = { persist(PageConfiguration(), "ডিফল্ট কনফিগারেশন রিসেট করা হয়েছে") },
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFFEF4444)),
            modifier = Modifier
              .weight(0.8f)
              .heightIn(min = 48.dp)
              .testTag("page_config_reset_button")
          ) {
            Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("রিসেট", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
          }
        }
      }
    }

    // 4 Primary Tabs
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      PageConfigMainTab.entries.forEach { tab ->
        val sel = tab == activeTab
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = if (sel) AdminV2CyanDeep else Color.White,
          border = BorderStroke(1.2.dp, if (sel) AdminV2CyanDeep else Color(0xFFCBD5E1)),
          modifier = Modifier
            .heightIn(min = 48.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { activeTab = tab }
            .testTag("page_config_tab_${tab.name.lowercase()}")
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
          ) {
            Text(
              text = tab.title,
              fontSize = 12.5.sp,
              fontWeight = FontWeight.ExtraBold,
              color = if (sel) Color.White else AdminInkPrimary
            )
          }
        }
      }
    }

    // Adaptive Layout: Side-by-Side Live View + Controls on >= 700dp, Live View on Top on Compact
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
      val wide = maxWidth >= 700.dp

      @Composable
      fun LivePreviewSection() {
        when (activeTab) {
          PageConfigMainTab.DETAIL_PAGE -> DetailPageLivePreview(
            dp = draft.detailPage,
            master = draft.master,
            sampleProduct = products.firstOrNull()
          )
          PageConfigMainTab.ADD_PRODUCT -> AddProductPageLivePreview(
            ap = draft.addProductPage,
            master = draft.master
          )
          PageConfigMainTab.MASTER -> MasterConfigLivePreview(pc = draft)
          PageConfigMainTab.DEVELOPER_JSON -> DetailPageLivePreview(
            dp = draft.detailPage,
            master = draft.master,
            sampleProduct = products.firstOrNull()
          )
        }
      }

      @Composable
      fun ControlsSection() {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = Color.White,
          border = BorderStroke(1.dp, Color(0xFFD6E8F5)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            when (activeTab) {
              PageConfigMainTab.DETAIL_PAGE -> DetailPageConfigControls(
                dp = draft.detailPage,
                onUpdate = { nextDp -> draft = draft.copy(detailPage = nextDp) }
              )
              PageConfigMainTab.ADD_PRODUCT -> AddProductPageConfigControls(
                ap = draft.addProductPage,
                onUpdate = { nextAp -> draft = draft.copy(addProductPage = nextAp) }
              )
              PageConfigMainTab.MASTER -> MasterSystemConfigTab(
                pc = draft,
                onUpdate = { nextPc -> draft = nextPc },
                onApplyFontEverywhere = { font ->
                  val updated = draft.copy(
                    master = draft.master.copy(globalFontFamily = font),
                    detailPage = draft.detailPage.copy(fontFamily = font),
                    addProductPage = draft.addProductPage.copy(fontFamily = font)
                  )
                  draft = updated
                  onShowToast("ফন্ট \"$font\" উভয় পেজে অ্যাপ্লাই হয়েছে!")
                }
              )
              PageConfigMainTab.DEVELOPER_JSON -> DeveloperJsonEditorTab(
                rawJson = rawJsonText,
                error = jsonError,
                onJsonChange = {
                  rawJsonText = it
                  jsonError = null
                },
                onImportJson = {
                  try {
                    val obj = JSONObject(rawJsonText)
                    val parsed = PageConfigJson.fromJsonObject(obj)
                    persist(parsed, "✅ Developer JSON সফলভাবে অ্যাপ্লাই হয়েছে!")
                    jsonError = null
                  } catch (e: Exception) {
                    jsonError = "JSON ফরম্যাট সঠিক নয়: ${e.message ?: "Invalid JSON"}"
                  }
                }
              )
            }
          }
        }
      }

      if (wide) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
          Column(modifier = Modifier.weight(1.15f)) { ControlsSection() }
          Column(modifier = Modifier.weight(0.85f)) { LivePreviewSection() }
        }
      } else {
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          LivePreviewSection()
          ControlsSection()
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MasterSystemConfigTab(
  pc: PageConfiguration,
  onUpdate: (PageConfiguration) -> Unit,
  onApplyFontEverywhere: (String) -> Unit
) {
  val m = pc.master
  Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
    Text("গ্লোবাল ফন্ট নির্বাচন (One-Click Apply to Both Pages):", fontSize = 12.5.sp, fontWeight = FontWeight.Black, color = AdminInkPrimary)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
      PAGE_FONT_CHOICES.forEach { (key, label) ->
        val sel = m.globalFontFamily.equals(key, ignoreCase = true)
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = if (sel) AdminV2CyanDeep else Color(0xFFF8FAFC),
          border = BorderStroke(1.dp, if (sel) AdminV2CyanDeep else Color(0xFFCBD5E1)),
          modifier = Modifier.clickable { onApplyFontEverywhere(key) }
        ) {
          Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (sel) Color.White else AdminInkPrimary,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
          )
        }
      }
    }

    PageRenameInputRow("মুদ্রার চিহ্ন (Currency Symbol)", m.currencySymbol) {
      onUpdate(pc.copy(master = m.copy(currencySymbol = it.ifBlank { "৳" })))
    }

    PageConfigSwitchRow(
      title = "High Contrast Pure Black Text Mode (#000000)",
      subtitle = "বিবরণ ও টেবিলের সব লেখা গাঢ় ব্ল্যাক (#000000) রঙে রাখুন",
      checked = m.highContrastMode,
      onCheckedChange = { onUpdate(pc.copy(master = m.copy(highContrastMode = it))) },
      testTag = "master_high_contrast_switch"
    )
    PageConfigSwitchRow(
      title = "Details পেজে Specifications & Variant Stock টেবিল",
      subtitle = "পণ্যের বিবরণ পেজে কালার-সাইজ-স্টক ও স্পেসিফিকেশন টেবিল দেখান",
      checked = m.showSpecsTableOnDetails,
      onCheckedChange = { onUpdate(pc.copy(master = m.copy(showSpecsTableOnDetails = it))) },
      testTag = "master_specs_table_switch"
    )
    PageConfigSwitchRow(
      title = "Details পেজে Stock Status দেখান",
      subtitle = "স্টক পরিমাণ ও এভেইলেবিলিটি দেখান",
      checked = m.showStockOnDetails,
      onCheckedChange = { onUpdate(pc.copy(master = m.copy(showStockOnDetails = it))) },
      testTag = "master_stock_switch"
    )
    PageConfigSwitchRow(
      title = "Details পেজে QR কোড বাটন দেখান",
      subtitle = "যেকোনো পেজ থেকে পণ্যের QR কোড শেয়ার বাটন দেখান",
      checked = m.showQrOnDetails,
      onCheckedChange = { onUpdate(pc.copy(master = m.copy(showQrOnDetails = it))) },
      testTag = "master_qr_switch"
    )
    PageConfigSwitchRow(
      title = "Details পেজে Customer Reviews সেকশন",
      subtitle = "গ্রাহকদের রিভিউ ও রেটিং সেকশন প্রদর্শন করুন",
      checked = m.showReviewsOnDetails,
      onCheckedChange = { onUpdate(pc.copy(master = m.copy(showReviewsOnDetails = it))) },
      testTag = "master_reviews_switch"
    )
    PageConfigSwitchRow(
      title = "Product Card Hover Scale Animation",
      subtitle = "স্টোরফ্রন্ট কার্ড হোভার/টাচে সাবলীল অ্যানিমেশন চালু রাখুন",
      checked = m.cardHoverScaleEnabled,
      onCheckedChange = { onUpdate(pc.copy(master = m.copy(cardHoverScaleEnabled = it))) },
      testTag = "master_hover_scale_switch"
    )
  }
}

@Composable
private fun DeveloperJsonEditorTab(
  rawJson: String,
  error: String?,
  onJsonChange: (String) -> Unit,
  onImportJson: () -> Unit
) {
  val clipboard = LocalClipboardManager.current
  var copied by remember { mutableStateOf(false) }

  Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
      Icon(Icons.Default.Code, contentDescription = null, tint = AdminV2CyanDeep, modifier = Modifier.size(18.dp))
      Text("Developer JSON Schema (Web ↔ Android Sync Ready)", fontSize = 12.5.sp, fontWeight = FontWeight.Black, color = AdminInkPrimary)
    }

    OutlinedTextField(
      value = rawJson,
      onValueChange = onJsonChange,
      minLines = 10,
      maxLines = 16,
      textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp),
      shape = RoundedCornerShape(12.dp),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("developer_json_input")
    )

    if (!error.isNullOrBlank()) {
      Text(error, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
    }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      OutlinedButton(
        onClick = {
          clipboard.setText(AnnotatedString(rawJson))
          copied = true
        },
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.weight(1f).heightIn(min = 48.dp)
      ) {
        Icon(if (copied) Icons.Default.Check else Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(if (copied) "কপি হয়েছে!" else "Copy JSON", fontWeight = FontWeight.Bold)
      }

      Button(
        onClick = onImportJson,
        colors = ButtonDefaults.buttonColors(containerColor = AdminV2CyanDeep, contentColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("import_json_apply_btn")
      ) {
        Text("Import & Apply JSON", fontWeight = FontWeight.Bold)
      }
    }
  }
}
