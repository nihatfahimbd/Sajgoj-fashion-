package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Coupon
import com.example.data.model.FooterPageContentOverride
import com.example.data.model.HomePageConfig
import com.example.data.model.Order
import com.example.data.model.StoreConfig
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.BorderGold
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.DarkEspresso
import com.example.ui.theme.NavyBlue
import com.example.ui.theme.RoyalMaroon
import com.example.ui.theme.parseColorHex
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

data class FooterHubTopicMeta(
  val key: String,
  val groupLabel: String,
  val defaultLabel: String
)

val FOOTER_HUB_TOPICS = listOf(
  FooterHubTopicMeta("about", "INFORMATION", "About us"),
  FooterHubTopicMeta("contact", "INFORMATION", "Contact us"),
  FooterHubTopicMeta("terms", "INFORMATION", "Terms & Conditions"),
  FooterHubTopicMeta("privacy", "INFORMATION", "Privacy Policy"),
  FooterHubTopicMeta("data_deletion", "INFORMATION", "User Data Deletion"),
  FooterHubTopicMeta("tracking", "SUPPORT", "Order Tracking"),
  FooterHubTopicMeta("payment", "SUPPORT", "Payment"),
  FooterHubTopicMeta("shipping", "SUPPORT", "Shipping"),
  FooterHubTopicMeta("faq", "SUPPORT", "FAQ"),
  FooterHubTopicMeta("refund", "CONSUMER POLICY", "Refund Policy"),
  FooterHubTopicMeta("exchange", "CONSUMER POLICY", "Exchange Policy"),
  FooterHubTopicMeta("cancellation", "CONSUMER POLICY", "Cancellation"),
  FooterHubTopicMeta("cookie", "CONSUMER POLICY", "Cookie Policy"),
  FooterHubTopicMeta("discount", "CONSUMER POLICY", "Extra Discount")
)

fun defaultFooterPageContent(topicKey: String, config: StoreConfig): FooterPageContentOverride {
  val paymentNames = listOfNotNull(
    if (config.bkashEnabled) "bKash" else null,
    if (config.nagadEnabled) "Nagad" else null,
    if (config.rocketEnabled) "Rocket" else null,
    if (config.codEnabled) "Cash on Delivery" else null,
    if (config.visaEnabled) "Visa / Mastercard" else null
  ).joinToString(", ").ifBlank { "Cash on Delivery" }

  return when (topicKey) {
    "about" -> FooterPageContentOverride(
      title = "About Us — আমাদের সম্পর্কে",
      subtitle = config.storeName,
      body = "${config.storeName} — আধুনিক ও রুচিশীল নারীদের জন্য প্রিমিয়াম ডিজাইনার শাড়ি ও রাজকীয় জুয়েলারি কালেকশন। আমাদের প্রতিটি পণ্য গুণগত মান যাচাই করে সরাসরি ক্রেতার হাতে পৌঁছে দেওয়া হয়।"
    )
    "contact" -> FooterPageContentOverride(
      title = "Contact Us — যোগাযোগ করুন",
      subtitle = "সরাসরি হটলাইন, WhatsApp ও শোরুম ঠিকানা",
      body = "📞 হটলাইন: ${config.phone}\n💬 WhatsApp: ${config.whatsapp}\n✉️ ইমেইল: ${config.email}\n📍 শোরুম: ${config.address}"
    )
    "terms" -> FooterPageContentOverride(
      title = "Terms & Conditions — শর্তাবলী",
      subtitle = "${config.storeName} ব্যবহারের নিয়মাবলী",
      body = "• অর্ডার কনফার্ম করতে সঠিক নাম, মোবাইল নম্বর ও ঠিকানা প্রদান আবশ্যক।\n• স্টক প্রাপ্যতা অনুযায়ী ডেলিভারি নিশ্চিত করা হয়।\n• যেকোনো ভুয়া অর্ডার বা অপব্যবহারের ক্ষেত্রে স্টোর কর্তৃপক্ষ অর্ডার বাতিলের অধিকার সংরক্ষণ করে।"
    )
    "privacy" -> FooterPageContentOverride(
      title = "Privacy Policy — গোপনীয়তা নীতি",
      subtitle = "আপনার ব্যক্তিগত তথ্যের সুরক্ষা",
      body = "• আপনার নাম, মোবাইল নম্বর, ইমেইল ও ডেলিভারি ঠিকানা শুধুমাত্র অর্ডার প্রক্রিয়াকরণ, অ্যাকাউন্ট লগইন ও কাস্টমার সার্ভিস প্রদানের জন্য ব্যবহৃত হয়।\n• Google / Facebook লগইন ব্যবহার করলে শুধুমাত্র প্রোফাইল নাম, ইমেইল ও অ্যাকাউন্ট সিঙ্কের জন্য তথ্য সংরক্ষিত হয়।\n• ক্রেতার ব্যক্তিগত তথ্য তৃতীয় কোনো মার্কেটিং সংস্থার কাছে বিক্রি বা শেয়ার করা হয় না।\n• কুরিয়ার পার্টনার (Steadfast / Pathao) শুধুমাত্র পার্সেল পৌঁছে দেওয়ার জন্য ডেলিভারি তথ্য ব্যবহার করে।"
    )
    "data_deletion" -> FooterPageContentOverride(
      title = "User Data Deletion — ডেটা মুছে ফেলার নীতিমালা",
      subtitle = "Facebook Login ও অ্যাকাউন্ট ডেটা ডিলিট নির্দেশনা",
      body = "• Facebook Settings → Apps and Websites → ${config.storeName} থেকে Remove করে ডেটা ডিলিশন রিকোয়েস্ট পাঠাতে পারবেন।\n• অথবা আপনার অ্যাকাউন্টের সমস্ত তথ্য স্থায়ীভাবে মুছে ফেলতে ${config.email}-এ ইমেইল করুন। ৪৮ ঘণ্টার মধ্যে আপনার তথ্য সম্পূর্ণ মুছে ফেলা হবে।"
    )
    "tracking" -> FooterPageContentOverride(
      title = "Order Tracking — অর্ডার ট্র্যাকিং",
      subtitle = "অর্ডার ID বা মোবাইল নম্বর দিয়ে পার্সেল যাচাই করুন",
      body = "আপনার অর্ডার ID, ট্র্যাকিং কোড অথবা মোবাইল নম্বর দিয়ে পার্সেলের বর্তমান অবস্থা তাৎক্ষণিকভাবে দেখে নিন।"
    )
    "payment" -> FooterPageContentOverride(
      title = "Payment Methods — পেমেন্ট পদ্ধতি",
      subtitle = "নিরাপদ ও সহজ মূল্য পরিশোধ",
      body = "উপলব্ধ পেমেন্ট মাধ্যম: $paymentNames।\n\nসারা দেশে পণ্য হাতে পেয়ে ক্যাশ অন ডেলিভারি অথবা অগ্রিম ডিজিটাল পেমেন্টের মাধ্যমে মূল্য পরিশোধ করা যায়।"
    )
    "shipping" -> FooterPageContentOverride(
      title = "Shipping Policy — ডেলিভারি নীতিমালা",
      subtitle = "সারা বাংলাদেশে দ্রুততম হোম ডেলিভারি",
      body = "🚚 ঢাকার ভেতরে ডেলিভারি চার্জ: ৳৭০ (১-২ কার্যদিবস)\n📦 ঢাকার বাইরে ডেলিভারি চার্জ: ৳১৩০ (২-৪ কার্যদিবস)\n🎁 ২,০০০+ টাকার অর্ডারে ফ্রি হোম ডেলিভারি ও ${config.returnPolicyDays} দিনের রিটার্ন সুবিধা!"
    )
    "faq" -> FooterPageContentOverride(
      title = "FAQ — সাধারণ জিজ্ঞাসা",
      subtitle = "ক্রেতাদের সর্বাধিক জিজ্ঞাসিত প্রশ্ন ও উত্তর",
      body = "প্রশ্ন: ডেলিভারি ম্যানের সামনে পণ্য চেক করা যাবে?\nউত্তর: হ্যাঁ, ডেলিভারি ম্যানের সামনেই যাচাই করে নিতে পারবেন।\n\nপ্রশ্ন: সাইজ বা ডিজাইন পরিবর্তন করা যাবে?\nউত্তর: হ্যাঁ, ${config.returnPolicyDays} দিনের মধ্যে সহজ এক্সচেঞ্জ সুবিধা আছে।"
    )
    "refund" -> FooterPageContentOverride(
      title = "Refund Policy — রিফান্ড নীতিমালা",
      subtitle = "সহজ ও স্বচ্ছ অর্থ ফেরত প্রক্রিয়া",
      body = "• ডেলিভারি পাওয়ার ${config.returnPolicyDays} দিনের মধ্যে ত্রুটিপূর্ণ বা ভুল পণ্যে ১০০% রিফান্ড সুবিধা পাওয়া যাবে।\n• রিটার্ন যাচাইয়ের পর ২-৩ কর্মদিবসে bKash / Nagad / ব্যাংকে অর্থ ফেরত দেওয়া হয়।"
    )
    "exchange" -> FooterPageContentOverride(
      title = "Exchange Policy — এক্সচেঞ্জ নীতিমালা",
      subtitle = "সাইজ ও পণ্য পরিবর্তনের নিয়ম",
      body = "• ডেলিভারির ${config.returnPolicyDays} দিনের মধ্যে সাইজ বা কালার পরিবর্তনের জন্য হটলাইন (${config.phone}) বা WhatsApp-এ যোগাযোগ করুন।\n• আমাদের ভুলের কারণে এক্সচেঞ্জ হলে ডেলিভারি চার্জ সম্পূর্ণ ফ্রি।"
    )
    "cancellation" -> FooterPageContentOverride(
      title = "Cancellation Policy — অর্ডার বাতিল নীতি",
      subtitle = "কখন ও কীভাবে অর্ডার বাতিল করবেন",
      body = "• কুরিয়ারে পার্সেল পাঠানোর আগে যেকোনো সময় অর্ডার বাতিল করা যাবে।\n• অর্ডার বাতিলের জন্য দ্রুত হটলাইন (${config.phone}) বা WhatsApp-এ অর্ডার ID জানান।"
    )
    "cookie" -> FooterPageContentOverride(
      title = "Cookie Policy — কুকি ও স্টোরেজ নীতি",
      subtitle = "সেশন ও কার্ট সংরক্ষণ তথ্য",
      body = "• আপনার কার্ট, উইশলিস্ট ও নিরাপদ লগইন সেশন ধরে রাখতে শুধুমাত্র প্রয়োজনীয় অ্যাপ স্টোরেজ ও কুকি ব্যবহৃত হয়।\n• আপনি যেকোনো সময় ডিভাইস সেটিংস বা অ্যাপ ডেটা ক্লিয়ার করে এটি মুছে ফেলতে পারেন।"
    )
    "discount" -> FooterPageContentOverride(
      title = "Extra Discount & Offers — বিশেষ ছাড়",
      subtitle = "চলমান কুপন কোড ও প্রমোশনাল অফার",
      body = "Buy 2 Get 1 অফার এবং সক্রিয় কুপন কোড ব্যবহার করে অতিরিক্ত ছাড় উপভোগ করুন।"
    )
    else -> FooterPageContentOverride(
      title = "Store Information",
      subtitle = config.storeName,
      body = config.tagline
    )
  }
}

fun resolvedFooterPageContent(
  topicKey: String,
  hp: HomePageConfig,
  config: StoreConfig
): FooterPageContentOverride {
  val def = defaultFooterPageContent(topicKey, config)
  val ov = hp.footerPageContents[topicKey] ?: return def
  return FooterPageContentOverride(
    title = ov.title.ifBlank { def.title },
    subtitle = ov.subtitle.ifBlank { def.subtitle },
    body = ov.body.ifBlank { def.body }
  )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CleanFooterInfoPageScreen(
  topicKey: String,
  config: StoreConfig,
  orders: List<Order>,
  coupons: List<Coupon>,
  onBack: () -> Unit,
  onOpenWhatsApp: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler(onBack = onBack)

  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val hp = config.pageConfig.homePage
  val supportEmail = remember(config.email) {
    config.email.trim().takeIf {
      it.isNotBlank() &&
        !it.equals("contact@sajgoj.com", ignoreCase = true) &&
        !it.equals("support@sajgoj.com", ignoreCase = true) &&
        !it.equals("admin@sajgoj.com", ignoreCase = true)
    } ?: "nihatfahimbd@gmail.com"
  }
  val effectiveConfig = remember(config, supportEmail) {
    if (config.email == supportEmail) config else config.copy(email = supportEmail)
  }
  val meta = FOOTER_HUB_TOPICS.find { it.key == topicKey } ?: FooterHubTopicMeta(topicKey, "INFORMATION", "Info")
  val content = remember(topicKey, hp, effectiveConfig) {
    resolvedFooterPageContent(topicKey, hp, effectiveConfig)
  }

  val pageBg = parseColorHex(hp.footerPageBg, Color(0xFFFFFAF5))
  val cardBg = parseColorHex(hp.footerPageCardBg, Color.White)
  val borderCol = parseColorHex(hp.footerPageBorderColor, Color(0xFFF3E2D0))
  val headingCol = parseColorHex(hp.footerPageHeadingColor, RoyalMaroon)
  val textCol = parseColorHex(hp.footerPageTextColor, DarkEspresso)
  val btnBg = parseColorHex(hp.footerPageBtnBg, RoyalMaroon)
  val btnTxt = parseColorHex(hp.footerPageBtnText, Color.White)
  val cardRadius = hp.footerPageRadiusPx.coerceIn(0, 32).dp

  var trackingQuery by remember(topicKey) { mutableStateOf("") }
  var deletionEmail by remember(topicKey) { mutableStateOf("") }
  var contactName by remember(topicKey) { mutableStateOf("") }
  var contactSender by remember(topicKey) { mutableStateOf("") }
  var contactSubject by remember(topicKey) { mutableStateOf("") }
  var contactMessage by remember(topicKey) { mutableStateOf("") }
  var contactSending by remember(topicKey) { mutableStateOf(false) }
  var contactValidationError by remember(topicKey) { mutableStateOf<String?>(null) }
  var contactStatusOk by remember(topicKey) { mutableStateOf(false) }
  var contactStatusMsg by remember(topicKey) { mutableStateOf<String?>(null) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(pageBg)
      .testTag("clean_footer_page_$topicKey"),
    contentAlignment = Alignment.TopCenter
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .widthIn(max = 680.dp)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 14.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Minimal Back Button Only (zero cross-link pills, zero URL copy bar)
      Button(
        onClick = onBack,
        colors = ButtonDefaults.buttonColors(containerColor = btnBg, contentColor = btnTxt),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
        modifier = Modifier
          .heightIn(min = 48.dp)
          .testTag("clean_footer_page_back_btn")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "হোমে ফিরুন",
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(text = "হোমে ফিরুন", fontSize = 13.sp, fontWeight = FontWeight.Bold)
      }

      // Clean Single-Topic Card
      Surface(
        shape = RoundedCornerShape(cardRadius),
        color = cardBg,
        border = BorderStroke(1.2.dp, borderCol),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Text(
            text = meta.groupLabel,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.ExtraBold,
            color = headingCol.copy(alpha = 0.72f),
            letterSpacing = 1.sp
          )

          Text(
            text = content.title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = headingCol,
            lineHeight = 26.sp
          )

          if (content.subtitle.isNotBlank()) {
            Text(
              text = content.subtitle,
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              color = textCol.copy(alpha = 0.76f)
            )
          }

          HorizontalDivider(color = borderCol.copy(alpha = 0.8f))

          if (content.body.isNotBlank()) {
            Text(
              text = content.body,
              fontSize = 13.5.sp,
              color = textCol,
              lineHeight = 22.sp
            )
          }

          when (topicKey) {
            "contact" -> {
              Spacer(modifier = Modifier.height(4.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Button(
                  onClick = {
                    runCatching {
                      context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${config.phone}")))
                    }
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = btnBg, contentColor = btnTxt),
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
                ) {
                  Text("📞 কল করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                  onClick = onOpenWhatsApp,
                  colors = ButtonDefaults.buttonColors(containerColor = AccentGreen, contentColor = Color.White),
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
                ) {
                  Text("💬 WhatsApp", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
              }
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Button(
                  onClick = {
                    val mapUrl = config.googleMapUrl.ifBlank {
                      "https://maps.google.com/?q=${Uri.encode(config.googleMapQuery.ifBlank { config.address })}"
                    }
                    runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl))) }
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = NavyBlue, contentColor = Color.White),
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
                ) {
                  Text("🗺️ গুগল ম্যাপ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                  onClick = {
                    val dest = Uri.encode(config.googleMapQuery.ifBlank { config.address })
                    val dirUrl = "https://www.google.com/maps/dir/?api=1&destination=$dest"
                    runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(dirUrl))) }
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E), contentColor = Color.White),
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
                ) {
                  Text("🧭 ডিরেকশন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
              }

              Spacer(modifier = Modifier.height(6.dp))

              Surface(
                shape = RoundedCornerShape(14.dp),
                color = CreamSurface.copy(alpha = 0.72f),
                border = BorderStroke(1.dp, borderCol),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("contact_us_form_card")
              ) {
                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                  verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  Text(
                    text = "✉️ সরাসরি সাপোর্ট ইমেইলে বার্তা পাঠান",
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Black,
                    color = headingCol
                  )
                  Text(
                    text = "প্রাপক: $supportEmail",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textCol.copy(alpha = 0.78f)
                  )

                  OutlinedTextField(
                    value = contactName,
                    onValueChange = {
                      contactName = it
                      contactValidationError = null
                    },
                    label = { Text("আপনার নাম *") },
                    placeholder = { Text("যেমন: তানিয়া আক্তার") },
                    singleLine = true,
                    modifier = Modifier
                      .fillMaxWidth()
                      .testTag("contact_form_name")
                  )

                  OutlinedTextField(
                    value = contactSender,
                    onValueChange = {
                      contactSender = it
                      contactValidationError = null
                    },
                    label = { Text("আপনার ইমেইল বা মোবাইল *") },
                    placeholder = { Text("name@gmail.com বা 01XXXXXXXXX") },
                    singleLine = true,
                    modifier = Modifier
                      .fillMaxWidth()
                      .testTag("contact_form_contact")
                  )

                  OutlinedTextField(
                    value = contactSubject,
                    onValueChange = { contactSubject = it },
                    label = { Text("বিষয় (Subject)") },
                    placeholder = { Text("যেমন: অর্ডার সংক্রান্ত তথ্য") },
                    singleLine = true,
                    modifier = Modifier
                      .fillMaxWidth()
                      .testTag("contact_form_subject")
                  )

                  OutlinedTextField(
                    value = contactMessage,
                    onValueChange = {
                      contactMessage = it
                      contactValidationError = null
                    },
                    label = { Text("আপনার বার্তা *") },
                    placeholder = { Text("আপনার প্রশ্ন বা বিস্তারিত বার্তা এখানে লিখুন...") },
                    minLines = 3,
                    maxLines = 6,
                    modifier = Modifier
                      .fillMaxWidth()
                      .testTag("contact_form_message")
                  )

                  if (!contactValidationError.isNullOrBlank()) {
                    Text(
                      text = contactValidationError!!,
                      fontSize = 12.sp,
                      fontWeight = FontWeight.SemiBold,
                      color = Color(0xFFB91C1C),
                      modifier = Modifier.testTag("contact_form_error")
                    )
                  }

                  fun openMailClientFallback(
                    cleanName: String,
                    cleanSender: String,
                    cleanSubject: String,
                    cleanMsg: String
                  ) {
                    val subjectText = cleanSubject.ifBlank { "${config.storeName} — কাস্টমার বার্তা" }
                    val bodyText = buildString {
                      if (cleanName.isNotBlank()) appendLine("নাম: $cleanName")
                      if (cleanSender.isNotBlank()) appendLine("যোগাযোগ (ইমেইল/মোবাইল): $cleanSender")
                      if (cleanName.isNotBlank() || cleanSender.isNotBlank()) appendLine()
                      appendLine("বার্তা:")
                      append(cleanMsg)
                    }
                    val mailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$supportEmail")).apply {
                      putExtra(Intent.EXTRA_SUBJECT, subjectText)
                      putExtra(Intent.EXTRA_TEXT, bodyText)
                    }
                    runCatching { context.startActivity(mailIntent) }.onFailure {
                      Toast.makeText(context, "ইমেইল পাঠান: $supportEmail", Toast.LENGTH_LONG).show()
                    }
                  }

                  Button(
                    onClick = {
                      val cleanName = contactName.trim()
                      val cleanSender = contactSender.trim()
                      val cleanSubject = contactSubject.trim().ifBlank { "${config.storeName} — কাস্টমার বার্তা" }
                      val cleanMsg = contactMessage.trim()
                      if (cleanName.isBlank() || cleanSender.isBlank() || cleanMsg.isBlank()) {
                        contactValidationError = "অনুগ্রহ করে নাম, ইমেইল/মোবাইল এবং আপনার বার্তা লিখুন।"
                        return@Button
                      }
                      contactSending = true
                      contactStatusMsg = null
                      scope.launch {
                        val sentRemotely = withContext(Dispatchers.IO) {
                          runCatching {
                            val endpoint = "https://formsubmit.co/ajax/${URLEncoder.encode(supportEmail, "UTF-8")}"
                            val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                              requestMethod = "POST"
                              connectTimeout = 10000
                              readTimeout = 12000
                              doOutput = true
                              setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                              setRequestProperty("Accept", "application/json")
                            }
                            val payload = JSONObject().apply {
                              put("name", cleanName)
                              put("contact", cleanSender)
                              if (cleanSender.contains("@")) {
                                put("email", cleanSender)
                                put("_replyto", cleanSender)
                              }
                              put("subject", cleanSubject)
                              put("message", cleanMsg)
                              put("_subject", "[${config.storeName}] $cleanSubject")
                              put("_template", "table")
                              put("_captcha", "false")
                            }.toString()
                            conn.outputStream.use { os ->
                              os.write(payload.toByteArray(Charsets.UTF_8))
                            }
                            val code = conn.responseCode
                            val respBody = runCatching {
                              val stream = if (code in 200..299) conn.inputStream else conn.errorStream
                              stream?.bufferedReader()?.use { it.readText() }.orEmpty()
                            }.getOrDefault("")
                            conn.disconnect()
                            if (code in 200..299) {
                              val json = runCatching { JSONObject(respBody) }.getOrNull()
                              val okFlag = json?.opt("success")
                              okFlag != false && okFlag?.toString()?.equals("false", ignoreCase = true) != true
                            } else {
                              false
                            }
                          }.getOrDefault(false)
                        }
                        contactSending = false
                        if (sentRemotely) {
                          contactStatusOk = true
                          contactStatusMsg = "✅ আপনার বার্তা সরাসরি $supportEmail-এ পাঠানো হয়েছে! আমাদের টিম শীঘ্রই উত্তর দেবে।"
                          contactName = ""
                          contactSender = ""
                          contactSubject = ""
                          contactMessage = ""
                          Toast.makeText(context, "বার্তা $supportEmail-এ পাঠানো হয়েছে!", Toast.LENGTH_SHORT).show()
                        } else {
                          contactStatusOk = false
                          contactStatusMsg = "ℹ️ সরাসরি $supportEmail-এ পাঠাতে আপনার ইমেইল অ্যাপ ওপেন করা হচ্ছে..."
                          openMailClientFallback(cleanName, cleanSender, cleanSubject, cleanMsg)
                        }
                      }
                    },
                    enabled = !contactSending,
                    colors = ButtonDefaults.buttonColors(containerColor = btnBg, contentColor = btnTxt),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                      .fillMaxWidth()
                      .heightIn(min = 48.dp)
                      .testTag("contact_form_submit")
                  ) {
                    if (contactSending) {
                      CircularProgressIndicator(
                        color = btnTxt,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp)
                      )
                      Spacer(modifier = Modifier.size(8.dp))
                      Text("পাঠানো হচ্ছে...", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    } else {
                      Text("✉️ বার্তা পাঠান", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                  }

                  OutlinedButton(
                    onClick = {
                      openMailClientFallback(
                        contactName.trim(),
                        contactSender.trim(),
                        contactSubject.trim(),
                        contactMessage.trim()
                      )
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, borderCol),
                    modifier = Modifier
                      .fillMaxWidth()
                      .heightIn(min = 48.dp)
                      .testTag("contact_form_mailto_btn")
                  ) {
                    Text(
                      text = "📧 ইমেইল অ্যাপ দিয়ে পাঠান ($supportEmail)",
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      color = headingCol
                    )
                  }

                  if (!contactStatusMsg.isNullOrBlank()) {
                    Surface(
                      shape = RoundedCornerShape(12.dp),
                      color = if (contactStatusOk) Color(0xFFECFDF5) else Color(0xFFFFFBEB),
                      border = BorderStroke(1.dp, if (contactStatusOk) Color(0xFF10B981) else Color(0xFFF59E0B)),
                      modifier = Modifier
                        .fillMaxWidth()
                        .testTag("contact_form_status")
                    ) {
                      Text(
                        text = contactStatusMsg!!,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (contactStatusOk) Color(0xFF065F46) else Color(0xFF92400E),
                        modifier = Modifier.padding(12.dp)
                      )
                    }
                  }
                }
              }
            }

            "data_deletion" -> {
              Spacer(modifier = Modifier.height(4.dp))
              OutlinedTextField(
                value = deletionEmail,
                onValueChange = { deletionEmail = it },
                label = { Text("আপনার ইমেইল বা ফোন নম্বর") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
              )
              Button(
                onClick = {
                  val mailBody = "User Data Deletion Request for ${config.storeName}\nAccount: ${deletionEmail.ifBlank { "(Not specified)" }}"
                  val mailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${config.email}")).apply {
                    putExtra(Intent.EXTRA_SUBJECT, "Data Deletion Request - ${config.storeName}")
                    putExtra(Intent.EXTRA_TEXT, mailBody)
                  }
                  runCatching { context.startActivity(mailIntent) }.onFailure {
                    Toast.makeText(context, "ইমেইল পাঠান: ${config.email}", Toast.LENGTH_LONG).show()
                  }
                },
                colors = ButtonDefaults.buttonColors(containerColor = btnBg, contentColor = btnTxt),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .heightIn(min = 48.dp)
              ) {
                Text("🗑️ ডেটা ডিলিশন রিকোয়েস্ট পাঠান", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
              }
            }

            "tracking" -> {
              Spacer(modifier = Modifier.height(4.dp))
              OutlinedTextField(
                value = trackingQuery,
                onValueChange = { trackingQuery = it },
                label = { Text("অর্ডার ID, ট্র্যাকিং কোড বা ফোন নম্বর") },
                singleLine = true,
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("clean_tracking_input")
              )
              val q = trackingQuery.trim().lowercase()
              val matched = if (q.isBlank()) emptyList() else orders.filter {
                it.id.lowercase().contains(q) ||
                  it.phone.lowercase().contains(q) ||
                  it.trackingId.lowercase().contains(q)
              }
              if (q.isNotBlank() && matched.isEmpty()) {
                Text(
                  text = "\"$trackingQuery\" দিয়ে কোনো অর্ডার পাওয়া যায়নি।",
                  fontSize = 12.5.sp,
                  fontWeight = FontWeight.Bold,
                  color = headingCol
                )
              } else {
                matched.forEach { ord ->
                  Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CreamSurface,
                    border = BorderStroke(1.dp, BorderGold),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Column(
                      modifier = Modifier.padding(12.dp),
                      verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                      Text(
                        text = "#${ord.id} • ${ord.status}",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Black,
                        color = headingCol
                      )
                      Text(
                        text = "ক্রেতা: ${ord.customerName} • মোট: ৳${ord.total}",
                        fontSize = 12.sp,
                        color = textCol
                      )
                      if (ord.trackingId.isNotBlank()) {
                        Text(
                          text = "🚚 ট্র্যাকিং ID: ${ord.trackingId}",
                          fontSize = 11.5.sp,
                          fontWeight = FontWeight.Bold,
                          color = NavyBlue
                        )
                      }
                      Button(
                        onClick = {
                          val smsMsg = "Notify me via SMS — সাজগোজ অর্ডার #${ord.id}" +
                            (if (ord.trackingId.isNotBlank()) " (ট্র্যাকিং: ${ord.trackingId})" else "") +
                            " | বর্তমান অবস্থা: ${ord.status} | ক্রেতার নম্বর: ${ord.phone}"
                          val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("smsto:${config.phone}")
                            putExtra("sms_body", smsMsg)
                          }
                          runCatching { context.startActivity(smsIntent) }.onFailure {
                            Toast.makeText(context, "SMS অ্যাপ পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
                          }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyBlue, contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                          .fillMaxWidth()
                          .heightIn(min = 48.dp)
                      ) {
                        Text("📩 Notify me via SMS", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                      }
                    }
                  }
                }
              }
            }

            "payment" -> {
              FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                if (config.bkashEnabled) PaymentMethodChip("bKash", Color(0xFFE2136E))
                if (config.nagadEnabled) PaymentMethodChip("Nagad", Color(0xFFF6921E))
                if (config.rocketEnabled) PaymentMethodChip("Rocket", Color(0xFF8C3494))
                if (config.codEnabled) PaymentMethodChip("Cash on Delivery", Color(0xFF047857))
                if (config.visaEnabled) PaymentMethodChip("Visa / Mastercard", Color(0xFF1E3A8A))
              }
            }

            "discount" -> {
              val activeCoupons = coupons.filter { it.isActive }
              if (activeCoupons.isNotEmpty()) {
                activeCoupons.forEach { cp ->
                  Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CreamSurface,
                    border = BorderStroke(1.dp, BorderGold),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Row(
                      modifier = Modifier.padding(12.dp),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column {
                        Text(text = cp.code, fontSize = 13.5.sp, fontWeight = FontWeight.Black, color = headingCol)
                        Text(
                          text = if (cp.type == "%" || cp.type.equals("percent", ignoreCase = true)) {
                            "${cp.value}% ছাড় (ন্যূনতম ৳${cp.minOrder})"
                          } else {
                            "৳${cp.value} ছাড় (ন্যূনতম ৳${cp.minOrder})"
                          },
                          fontSize = 12.sp,
                          color = textCol
                        )
                      }
                      Text(text = "সক্রিয় ✓", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = AccentGreen)
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

@Composable
private fun PaymentMethodChip(label: String, bg: Color) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .background(bg)
      .padding(horizontal = 10.dp, vertical = 5.dp)
  ) {
    Text(text = label, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
  }
}
