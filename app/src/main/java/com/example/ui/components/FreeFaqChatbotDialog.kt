package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Coupon
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.StoreConfig
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.BorderGold
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.DarkEspresso
import com.example.ui.theme.NavyBlue
import com.example.ui.theme.NavyBlueCardEnd
import com.example.ui.theme.RoyalMaroon
import com.example.ui.theme.SlateTextSecondary
import java.net.URLEncoder
import java.util.UUID

data class FaqQuickTopic(
  val label: String,
  val query: String
)

data class FaqChatMessage(
  val id: String = UUID.randomUUID().toString(),
  val isFromUser: Boolean,
  val text: String,
  val coupons: List<Coupon> = emptyList(),
  val matchedOrders: List<Order> = emptyList(),
  val matchedProducts: List<Product> = emptyList(),
  val showSupportActions: Boolean = false,
  val showMapAction: Boolean = false,
  val handoffQuery: String = "",
  val followUps: List<String> = emptyList()
)

object FaqBotEngine {

  val quickTopics = listOf(
    FaqQuickTopic("🔄 রিটার্ন ও রিফান্ড", "রিটার্ন ও রিফান্ড পলিসি কী?"),
    FaqQuickTopic("🚚 ডেলিভারি চার্জ", "ডেলিভারি চার্জ ও সময় কত?"),
    FaqQuickTopic("📦 অর্ডার ট্র্যাকিং", "আমার অর্ডার ট্র্যাক করতে চাই"),
    FaqQuickTopic("💳 পেমেন্ট ও COD", "পেমেন্ট মাধ্যম ও ক্যাশ অন ডেলিভারি"),
    FaqQuickTopic("🎁 কুপন ও ছাড়", "কুপন কোড ও বিশেষ অফার কী আছে?"),
    FaqQuickTopic("📞 যোগাযোগ ও ঠিকানা", "শোরুম ঠিকানা ও যোগাযোগ নম্বর")
  )

  fun welcomeMessage(config: StoreConfig): FaqChatMessage {
    return FaqChatMessage(
      isFromUser = false,
      text = "আসসালামু আলাইকুম! আমি ${config.storeName}-এর ১০০% ফ্রি ও আনলিমিটেড স্মার্ট সহায়িকা। " +
        "রিটার্ন পলিসি, ডেলিভারি চার্জ, অর্ডার ট্র্যাকিং বা কুপন সম্পর্কে নিচের বাটনে ট্যাপ করুন অথবা সরাসরি প্রশ্ন লিখুন।",
      followUps = listOf("৭ দিনের রিটার্ন নিয়ম", "ডেলিভারি চার্জ কত?", "সক্রিয় কুপন দেখুন")
    )
  }

  fun reply(
    rawQuery: String,
    config: StoreConfig,
    orders: List<Order>,
    coupons: List<Coupon>,
    products: List<Product>
  ): FaqChatMessage {
    val q = rawQuery.trim()
    val lower = q.lowercase()

    if (q.isEmpty()) {
      return FaqChatMessage(
        isFromUser = false,
        text = "অনুগ্রহ করে আপনার প্রশ্নটি লিখুন অথবা উপরের দ্রুত বাটনগুলো থেকে একটি বিষয় বেছে নিন।"
      )
    }

    // 1. Direct Order ID / Tracking Code / Phone Lookup
    val normalizedDigits = q.replace(Regex("[^0-9]"), "")
    val looksLikeOrderLookup = lower.contains("ord-") ||
      lower.contains("trk") ||
      (normalizedDigits.length >= 6 && !lower.contains("টাকা") && !lower.contains("দাম"))

    if (looksLikeOrderLookup) {
      val matched = orders.filter { ord ->
        ord.id.lowercase().contains(lower) ||
          ord.trackingId.lowercase().contains(lower) ||
          (normalizedDigits.length >= 6 && ord.phone.replace(Regex("[^0-9]"), "").contains(normalizedDigits))
      }.take(4)

      if (matched.isNotEmpty()) {
        return FaqChatMessage(
          isFromUser = false,
          text = "আপনার তথ্যের সাথে মিলে যাওয়া ${matched.size}টি অর্ডারের বর্তমান অবস্থান নিচে দেওয়া হলো:",
          matchedOrders = matched,
          showSupportActions = true,
          handoffQuery = "আমার অর্ডার #${matched.first().id} সম্পর্কে জানতে চাই।"
        )
      }
      return FaqChatMessage(
        isFromUser = false,
        text = "\"$q\" দিয়ে কোনো অর্ডার পাওয়া যায়নি। সঠিক অর্ডার ID (যেমন ORD-1002) অথবা অর্ডার করার মোবাইল নম্বরটি আবার যাচাই করুন।",
        showSupportActions = true,
        handoffQuery = "হ্যালো, আমি আমার অর্ডার ($q) ট্র্যাক করতে চাই।"
      )
    }

    // 2. Greeting
    if (matchesAny(lower, listOf("হাই", "হ্যালো", "সালাম", "আসসালামু", "নমস্কার", "hi", "hello", "hey", "salam"))) {
      return FaqChatMessage(
        isFromUser = false,
        text = "ওয়ালাইকুম আসসালাম! ${config.storeName}-এ স্বাগতম। আজকে আপনাকে কীভাবে সাহায্য করতে পারি? " +
          "আপনি ডেলিভারি চার্জ, রিটার্ন/এক্সচেঞ্জ পলিসি, কুপন বা পণ্যের দাম সম্পর্কে জানতে পারেন।",
        followUps = listOf("রিটার্ন ও রিফান্ড নিয়ম", "ডেলিভারি চার্জ কত?", "কুপন ও ছাড়")
      )
    }

    // 3. Return, Refund & Exchange
    if (matchesAny(lower, listOf("রিটার্ন", "রিফান্ড", "ফেরত", "এক্সচেঞ্জ", "বদল", "পরিবর্তন", "ভাঙা", "নষ্ট", "ত্রুটি", "return", "refund", "exchange", "damage", "faulty", "broken"))) {
      val days = if (config.returnPolicyDays > 0) config.returnPolicyDays else 7
      return FaqChatMessage(
        isFromUser = false,
        text = "🔄 রিটার্ন, এক্সচেঞ্জ ও রিফান্ড নীতিমালা (${days} দিন):\n\n" +
          "• ডেলিভারি পাওয়ার ${days} দিনের মধ্যে ত্রুটিপূর্ণ, ভুল বা ক্ষতিগ্রস্ত পণ্য ১০০% রিটার্ন/রিফান্ড সুবিধা পাবেন।\n" +
          "• পণ্যটি অবশ্যই অব্যবহৃত থাকতে হবে এবং অরিজিনাল ট্যাগ ও প্যাকেজিং অক্ষত থাকতে হবে।\n" +
          "• সাইজ বা কালার পরিবর্তনের (Exchange) ক্ষেত্রে আমাদের ভুল হলে ডেলিভারি চার্জ সম্পূর্ণ ফ্রি।\n" +
          "• রিটার্ন যাচাইয়ের ২-৩ কর্মদিবসের মধ্যে bKash, Nagad বা ব্যাংক অ্যাকাউন্টে রিফান্ড পাঠানো হয়।",
        showSupportActions = true,
        handoffQuery = "হ্যালো, আমার একটি অর্ডারের রিটার্ন/এক্সচেঞ্জ সম্পর্কে সাহায্য প্রয়োজন।",
        followUps = listOf("অর্ডার বাতিলের নিয়ম", "যোগাযোগ ও হটলাইন")
      )
    }

    // 4. Order Cancellation
    if (matchesAny(lower, listOf("বাতিল", "ক্যানসেল", "cancel", "cancellation"))) {
      return FaqChatMessage(
        isFromUser = false,
        text = "✖️ অর্ডার বাতিলের নিয়ম:\n\n" +
          "• পার্সেল কুরিয়ারে হস্তান্তরের (Shipped) আগে যেকোনো সময় আপনার অর্ডারটি বাতিল করা যাবে।\n" +
          "• দ্রুত অর্ডার বাতিল করতে আপনার অর্ডার ID সহ আমাদের হটলাইন (${config.phone}) বা WhatsApp-এ মেসেজ দিন।",
        showSupportActions = true,
        handoffQuery = "হ্যালো, আমি আমার একটি অর্ডার বাতিল (Cancel) করতে চাচ্ছি।"
      )
    }

    // 5. Order Tracking Prompt
    if (matchesAny(lower, listOf("ট্র্যাক", "অর্ডার", "পার্সেল", "কোথায়", "কবে পাব", "অবস্থা", "track", "tracking", "status", "order"))) {
      return FaqChatMessage(
        isFromUser = false,
        text = "📦 লাইভ অর্ডার ট্র্যাকিং:\n\n" +
          "আপনার অর্ডার ID (যেমন: ORD-1002), কুরিয়ার ট্র্যাকিং কোড অথবা অর্ডারের মোবাইল নম্বরটি নিচে লিখে পাঠান—আমি সাথে সাথে অর্ডারের অবস্থা দেখিয়ে দেব!",
        showSupportActions = true,
        handoffQuery = "হ্যালো সাজগোজ, আমি আমার অর্ডারের বর্তমান অবস্থা জানতে চাই।"
      )
    }

    // 6. Shipping & Delivery Charges
    if (matchesAny(lower, listOf("ডেলিভারি", "চার্জ", "কুরিয়ার", "শিপিং", "চাঁদপুর", "ঢাকা", "সময়", "ফ্রি ডেলিভারি", "delivery", "shipping", "courier", "fee", "express", "standard"))) {
      val stdOpt = config.shippingInfo.options.firstOrNull { it.deliveryType.equals("Standard", ignoreCase = true) }
      val expOpt = config.shippingInfo.options.firstOrNull { it.deliveryType.equals("Express", ignoreCase = true) }
      val chandpurStdFee = config.shippingInfo.chandpurStandardFee
      val outsideFee = stdOpt?.fee ?: 120
      val freeAbove = stdOpt?.freeAbove ?: 2000
      val expFee = expOpt?.fee ?: 200
      val expDays = expOpt?.estimatedDays?.ifBlank { "২৪ ঘন্টা / একই দিন" } ?: "২৪ ঘন্টা / একই দিন"
      val stdDays = stdOpt?.estimatedDays?.ifBlank { "২-৪ কার্যদিবস" } ?: "২-৪ কার্যদিবস"

      return FaqChatMessage(
        isFromUser = false,
        text = "🚚 ডেলিভারি চার্জ ও সময়সূচি:\n\n" +
          "• চাঁদপুর জেলা: Standard ৳$chandpurStdFee · Express ৳$expFee ($expDays)\n" +
          "• সারা বাংলাদেশে ৬৪ জেলা (Standard): ৳$outsideFee ($stdDays)\n" +
          (if (freeAbove > 0) "• 🎁 ফ্রি ডেলিভারি: ৳$freeAbove+ টাকার Standard অর্ডারে ডেলিভারি চার্জ সম্পূর্ণ ফ্রি!\n" else "") +
          "• ডেলিভারি ম্যানের সামনে প্যাকেট খুলে পণ্য যাচাই করার সুবিধা রয়েছে।",
        followUps = listOf("পেমেন্ট মাধ্যমসমূহ", "রিটার্ন ও রিফান্ড নিয়ম")
      )
    }

    // 7. Payment & COD
    if (matchesAny(lower, listOf("পেমেন্ট", "বিকাশ", "নগদ", "রকেট", "ক্যাশ", "কার্ড", "অগ্রিম", "bkash", "nagad", "rocket", "cod", "cash", "payment", "visa", "mastercard"))) {
      val methods = listOfNotNull(
        if (config.bkashEnabled) "bKash" else null,
        if (config.nagadEnabled) "Nagad" else null,
        if (config.rocketEnabled) "Rocket" else null,
        if (config.codEnabled) "Cash on Delivery (COD)" else null,
        if (config.visaEnabled) "Visa / Mastercard" else null
      ).joinToString(", ")

      return FaqChatMessage(
        isFromUser = false,
        text = "💳 পেমেন্ট সুবিধাসমূহ:\n\n" +
          "• আমাদের স্টোরে উপলব্ধ মাধ্যম: ${methods.ifBlank { "Cash on Delivery, bKash, Nagad" }}\n" +
          "• সারা দেশে পণ্য হাতে পেয়ে ও চেক করে ক্যাশ অন ডেলিভারিতে (COD) মূল্য পরিশোধ করতে পারবেন।",
        followUps = listOf("ডেলিভারি চার্জ কত?", "কুপন ও ছাড়")
      )
    }

    // 8. Coupons, Vouchers, B2G1 & Discounts
    if (matchesAny(lower, listOf("কুপন", "ছাড়", "ডিসকাউন্ট", "ভাউচার", "অফার", "ফ্রি গিফট", "b2g1", "coupon", "discount", "voucher", "promo", "offer"))) {
      val activeCoupons = coupons.filter { it.isActive }.take(5)
      val b2g1Line = if (config.b2g1.enabled) {
        "\n🎁 ${config.b2g1.title}: ${config.b2g1.subtitle}"
      } else {
        ""
      }
      val text = if (activeCoupons.isNotEmpty()) {
        "🏷️ বর্তমানে সক্রিয় কুপন ও বিশেষ অফারসমূহ (চেকআউটে ব্যবহার করুন):$b2g1Line"
      } else {
        "🎁 আমাদের স্টোরের অফার:$b2g1Line\n• ডিসকাউন্ট ভাউচার স্ট্রিপ ও ভাগ্যচাকা স্পিন থেকে নিয়মিত বিশেষ ছাড় ও কুপন সংগ্রহ করতে পারবেন।"
      }
      return FaqChatMessage(
        isFromUser = false,
        text = text,
        coupons = activeCoupons,
        followUps = listOf("ডেলিভারি চার্জ কত?", "প্রোডাক্ট খুঁজুন")
      )
    }

    // 9. Contact, Showroom Address & Map
    if (matchesAny(lower, listOf("যোগাযোগ", "ঠিকানা", "শোরুম", "দোকান", "ম্যাপ", "নম্বর", "ফোন", "হটলাইন", "হোয়াটসঅ্যাপ", "ইমেইল", "contact", "address", "location", "map", "phone", "whatsapp", "email", "helpline"))) {
      return FaqChatMessage(
        isFromUser = false,
        text = "📞 যোগাযোগ ও শোরুম ঠিকানা:\n\n" +
          "• হটলাইন: ${config.phone}\n" +
          "• WhatsApp: ${config.whatsapp}\n" +
          "• ইমেইল: ${config.email}\n" +
          "• ঠিকানা: ${config.address}",
        showSupportActions = true,
        showMapAction = config.showGoogleMap,
        handoffQuery = "আসসালামু আলাইকুম, আমি ${config.storeName} সম্পর্কে জানতে চাই।"
      )
    }

    // 10. Product Price / Stock Lookup
    val activeProducts = products.filter { !it.isDraft && it.enabledForWeb }
    val matchingProducts = activeProducts.filter { p ->
      val pName = p.name.lowercase()
      val pCat = p.category.lowercase()
      val words = lower.split(Regex("\\s+")).filter { it.length >= 2 }
      words.any { w -> pName.contains(w) || pCat.contains(w) }
    }.take(3)

    if (matchingProducts.isNotEmpty()) {
      return FaqChatMessage(
        isFromUser = false,
        text = "🛍️ আপনার খোঁজের সাথে মিলে যাওয়া কিছু কালেকশন (বিস্তারিত দেখতে কার্ডে ট্যাপ করুন):",
        matchedProducts = matchingProducts
      )
    }

    if (matchesAny(lower, listOf("দাম", "প্রাইস", "স্টক", "শাড়ি", "জুয়েলারি", "গয়না", "আংটি", "চুড়ি", "পণ্য", "প্রোডাক্ট", "price", "stock", "saree", "jewelry", "product"))) {
      val sample = activeProducts.take(3)
      return FaqChatMessage(
        isFromUser = false,
        text = "🛍️ নির্দিষ্ট কোনো পণ্যের দাম বা স্টক জানতে পণ্যের নাম লিখুন (যেমন: 'শাড়ি' বা 'চুড়ি')। আমাদের কিছু জনপ্রিয় কালেকশন:",
        matchedProducts = sample
      )
    }

    // 11. Smart Fallback
    return FaqChatMessage(
      isFromUser = false,
      text = "আমি আপনার প্রশ্নটি পুরোপুরি বুঝতে পারিনি, তবে রিটার্ন পলিসি, ডেলিভারি চার্জ, পেমেন্ট, কুপন বা অর্ডার ট্র্যাকিং বিষয়ে সাথে সাথে সাহায্য করতে পারি। " +
        "চাইলে নিচের বাটনে ট্যাপ করে সরাসরি আমাদের কাস্টমার কেয়ারে কথা বলতে পারেন।",
      showSupportActions = true,
      handoffQuery = q,
      followUps = listOf("রিটার্ন ও রিফান্ড নিয়ম", "ডেলিভারি চার্জ কত?", "আমার অর্ডার ট্র্যাক করুন")
    )
  }

  private fun matchesAny(text: String, keywords: List<String>): Boolean {
    return keywords.any { kw -> text.contains(kw) }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FreeFaqChatbotDialog(
  config: StoreConfig,
  orders: List<Order>,
  coupons: List<Coupon>,
  products: List<Product>,
  onProductClick: (Product) -> Unit,
  onOpenStoreMap: () -> Unit,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val messages = remember {
    mutableStateListOf(FaqBotEngine.welcomeMessage(config))
  }
  var inputText by remember { mutableStateOf("") }
  val listState = rememberLazyListState()

  LaunchedEffect(messages.size) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  fun sendQuery(queryText: String) {
    val clean = queryText.trim()
    if (clean.isEmpty()) return
    messages.add(FaqChatMessage(isFromUser = true, text = clean))
    val botReply = FaqBotEngine.reply(
      rawQuery = clean,
      config = config,
      orders = orders,
      coupons = coupons,
      products = products
    )
    messages.add(botReply)
    inputText = ""
  }

  fun openWhatsAppWithText(customMsg: String) {
    val cleanDigits = config.whatsapp.ifBlank { config.phone }.replace(Regex("[^0-9]"), "")
    val encoded = try {
      URLEncoder.encode(customMsg.ifBlank { "আসসালামু আলাইকুম, আমি ${config.storeName} সম্পর্কে জানতে চাই।" }, "UTF-8")
    } catch (_: Exception) {
      ""
    }
    try {
      val uri = Uri.parse("https://wa.me/$cleanDigits?text=$encoded")
      context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    } catch (_: Exception) {
      val dial = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${config.phone}"))
      context.startActivity(dial)
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = Color.White,
      border = BorderStroke(1.dp, BorderGold),
      tonalElevation = 6.dp,
      modifier = Modifier
        .padding(horizontal = 12.dp, vertical = 20.dp)
        .fillMaxWidth()
        .widthIn(max = 560.dp)
        .fillMaxHeight(0.88f)
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        // Header
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.horizontalGradient(listOf(NavyBlue, NavyBlueCardEnd))
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp),
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(40.dp)
                  .clip(CircleShape)
                  .background(Color.White.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.SupportAgent,
                  contentDescription = "সাজগোজ ফ্রি সহায়িকা",
                  tint = AntiqueGold,
                  modifier = Modifier.size(22.dp)
                )
              }
              Column {
                Text(
                  text = "সাজগোজ স্মার্ট সহায়িকা",
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Black,
                  color = Color.White,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AccentGreen
                  ) {
                    Text(
                      text = "১০০% আজীবন ফ্রি",
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color.White,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                  Text(
                    text = "• তাৎক্ষণিক উত্তর",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.88f)
                  )
                }
              }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              IconButton(
                onClick = {
                  messages.clear()
                  messages.add(FaqBotEngine.welcomeMessage(config))
                },
                modifier = Modifier
                  .size(48.dp)
                  .testTag("faq_chatbot_reset_btn")
              ) {
                Icon(
                  imageVector = Icons.Default.Refresh,
                  contentDescription = "চ্যাট রিসেট করুন",
                  tint = Color.White
                )
              }
              IconButton(
                onClick = onDismiss,
                modifier = Modifier
                  .size(48.dp)
                  .testTag("faq_chatbot_close_btn")
              ) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "চ্যাটবট বন্ধ করুন",
                  tint = Color.White
                )
              }
            }
          }
        }

        // Quick Topic Chips Strip
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(CreamBackground)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          FaqBotEngine.quickTopics.forEach { topic ->
            Surface(
              shape = RoundedCornerShape(18.dp),
              color = Color.White,
              border = BorderStroke(1.dp, BorderGold),
              modifier = Modifier
                .heightIn(min = 38.dp)
                .clip(RoundedCornerShape(18.dp))
                .clickable { sendQuery(topic.query) }
            ) {
              Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
              ) {
                Text(
                  text = topic.label,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = RoyalMaroon
                )
              }
            }
          }
        }

        HorizontalDivider(color = BorderGold)

        // Messages List
        LazyColumn(
          state = listState,
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .background(Color(0xFFFAFBFD)),
          contentPadding = PaddingValues(12.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(messages, key = { it.id }) { msg ->
            Column(
              modifier = Modifier.fillMaxWidth(),
              horizontalAlignment = if (msg.isFromUser) Alignment.End else Alignment.Start
            ) {
              Surface(
                shape = RoundedCornerShape(
                  topStart = 18.dp,
                  topEnd = 18.dp,
                  bottomStart = if (msg.isFromUser) 18.dp else 4.dp,
                  bottomEnd = if (msg.isFromUser) 4.dp else 18.dp
                ),
                color = if (msg.isFromUser) RoyalMaroon else CreamSurface,
                border = if (msg.isFromUser) null else BorderStroke(1.dp, BorderGold),
                modifier = Modifier.widthIn(max = 360.dp)
              ) {
                Column(
                  modifier = Modifier.padding(12.dp),
                  verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Text(
                    text = msg.text,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = if (msg.isFromUser) Color.White else DarkEspresso
                  )

                  // Inline Matched Orders
                  msg.matchedOrders.forEach { ord ->
                    Surface(
                      shape = RoundedCornerShape(12.dp),
                      color = Color.White,
                      border = BorderStroke(1.dp, BorderGold),
                      modifier = Modifier.fillMaxWidth()
                    ) {
                      Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                      ) {
                        Row(
                          modifier = Modifier.fillMaxWidth(),
                          horizontalArrangement = Arrangement.SpaceBetween,
                          verticalAlignment = Alignment.CenterVertically
                        ) {
                          Text(
                            text = "#${ord.id}",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Black,
                            color = RoyalMaroon
                          )
                          Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AccentGreen.copy(alpha = 0.12f)
                          ) {
                            Text(
                              text = ord.status,
                              fontSize = 11.sp,
                              fontWeight = FontWeight.Bold,
                              color = AccentGreen,
                              modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                          }
                        }
                        Text(
                          text = "ক্রেতা: ${ord.customerName} • মোট: ৳${ord.total}",
                          fontSize = 11.5.sp,
                          color = SlateTextSecondary
                        )
                        if (ord.trackingId.isNotBlank()) {
                          Text(
                            text = "🚚 ট্র্যাকিং কোড: ${ord.trackingId}",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyBlue
                          )
                        }
                      }
                    }
                  }

                  // Inline Active Coupons
                  msg.coupons.forEach { cp ->
                    Surface(
                      shape = RoundedCornerShape(12.dp),
                      color = Color.White,
                      border = BorderStroke(1.dp, BorderGold),
                      modifier = Modifier.fillMaxWidth()
                    ) {
                      Row(
                        modifier = Modifier
                          .fillMaxWidth()
                          .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                      ) {
                        Column(modifier = Modifier.weight(1f)) {
                          Text(
                            text = cp.code,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = RoyalMaroon
                          )
                          val discLabel = if (cp.type == "%") "${cp.value}% ছাড়" else "৳${cp.value} ছাড়"
                          Text(
                            text = "$discLabel • ন্যূনতম ৳${cp.minOrder}",
                            fontSize = 11.sp,
                            color = SlateTextSecondary
                          )
                        }
                        OutlinedButton(
                          onClick = {
                            val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            cb.setPrimaryClip(ClipData.newPlainText("Coupon", cp.code))
                            Toast.makeText(context, "কুপন \"${cp.code}\" কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                          },
                          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                          modifier = Modifier.heightIn(min = 36.dp)
                        ) {
                          Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "কপি করুন",
                            modifier = Modifier.size(14.dp),
                            tint = RoyalMaroon
                          )
                          Spacer(modifier = Modifier.width(4.dp))
                          Text("কপি", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)
                        }
                      }
                    }
                  }

                  // Inline Matched Products
                  msg.matchedProducts.forEach { prod ->
                    Surface(
                      shape = RoundedCornerShape(12.dp),
                      color = Color.White,
                      border = BorderStroke(1.dp, BorderGold),
                      modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                          onDismiss()
                          onProductClick(prod)
                        }
                    ) {
                      Row(
                        modifier = Modifier
                          .fillMaxWidth()
                          .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                      ) {
                        Column(modifier = Modifier.weight(1f)) {
                          Text(
                            text = prod.name,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkEspresso,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                          )
                          Text(
                            text = "৳${prod.price} • ${if (prod.stock > 0) "স্টকে আছে (${prod.stock})" else "স্টক শেষ"}",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (prod.stock > 0) AccentGreen else RoyalMaroon
                          )
                        }
                        Text(
                          text = "দেখুন →",
                          fontSize = 11.5.sp,
                          fontWeight = FontWeight.Black,
                          color = NavyBlue
                        )
                      }
                    }
                  }

                  // Follow-up suggestion chips
                  if (msg.followUps.isNotEmpty()) {
                    FlowRow(
                      horizontalArrangement = Arrangement.spacedBy(6.dp),
                      verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                      msg.followUps.forEach { suggestion ->
                        Surface(
                          shape = RoundedCornerShape(14.dp),
                          color = Color.White,
                          border = BorderStroke(1.dp, NavyBlue.copy(alpha = 0.28f)),
                          modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { sendQuery(suggestion) }
                        ) {
                          Text(
                            text = suggestion,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyBlue,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                          )
                        }
                      }
                    }
                  }

                  // Support Actions (WhatsApp, Phone, Google Map)
                  if (msg.showSupportActions || msg.showMapAction) {
                    FlowRow(
                      horizontalArrangement = Arrangement.spacedBy(8.dp),
                      verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                      if (msg.showSupportActions) {
                        Button(
                          onClick = { openWhatsAppWithText(msg.handoffQuery) },
                          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                          modifier = Modifier.heightIn(min = 40.dp)
                        ) {
                          Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "WhatsApp",
                            modifier = Modifier.size(15.dp)
                          )
                          Spacer(modifier = Modifier.width(5.dp))
                          Text("WhatsApp", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                          onClick = {
                            val dial = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${config.phone}"))
                            context.startActivity(dial)
                          },
                          colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon),
                          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                          modifier = Modifier.heightIn(min = 40.dp)
                        ) {
                          Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "কল করুন",
                            modifier = Modifier.size(15.dp)
                          )
                          Spacer(modifier = Modifier.width(5.dp))
                          Text("কল করুন", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                      }
                      if (msg.showMapAction) {
                        Button(
                          onClick = {
                            onDismiss()
                            onOpenStoreMap()
                          },
                          colors = ButtonDefaults.buttonColors(containerColor = NavyBlue),
                          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                          modifier = Modifier.heightIn(min = 40.dp)
                        ) {
                          Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = "ম্যাপ দেখুন",
                            modifier = Modifier.size(15.dp)
                          )
                          Spacer(modifier = Modifier.width(5.dp))
                          Text("ম্যাপ দেখুন", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }

        HorizontalDivider(color = BorderGold)

        // Input Composer
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 12.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            placeholder = {
              Text(
                text = "প্রশ্ন বা অর্ডার ID লিখুন...",
                fontSize = 13.sp,
                color = SlateTextSecondary
              )
            },
            singleLine = true,
            shape = RoundedCornerShape(22.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = NavyBlue,
              unfocusedBorderColor = BorderGold,
              focusedContainerColor = CreamBackground,
              unfocusedContainerColor = CreamBackground
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { sendQuery(inputText) }),
            modifier = Modifier
              .weight(1f)
              .testTag("faq_chatbot_input")
          )

          Surface(
            shape = CircleShape,
            color = if (inputText.isNotBlank()) NavyBlue else NavyBlue.copy(alpha = 0.4f),
            modifier = Modifier
              .size(48.dp)
              .clip(CircleShape)
              .clickable(enabled = inputText.isNotBlank()) { sendQuery(inputText) }
              .testTag("faq_chatbot_send_btn")
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "পাঠান",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }
      }
    }
  }
}
