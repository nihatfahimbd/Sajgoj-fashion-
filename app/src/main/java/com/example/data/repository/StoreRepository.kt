package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.R
import com.example.data.BangladeshLocations
import com.example.data.model.BannerItem
import com.example.data.model.CartItem
import com.example.data.model.Category
import com.example.data.model.ComboPack
import com.example.data.model.Coupon
import com.example.data.model.CourierConfig
import com.example.data.model.ExchangeItem
import com.example.data.model.HomeSectionConfig
import com.example.data.model.InventorySyncEvent
import com.example.data.model.Order
import com.example.data.model.OrderItem
import com.example.data.model.PixelConfig
import com.example.data.model.PosInventoryItem
import com.example.data.model.Product
import com.example.data.model.ProductReview
import com.example.data.model.ReelItem
import com.example.data.model.ReturnItem
import com.example.data.model.ReturnRecord
import com.example.data.model.ShippingInfoConfig
import com.example.data.model.ShippingOptionItem
import com.example.data.model.SmsQueueItem
import com.example.data.model.StoreConfig
import com.example.data.model.StoreDesignConfig
import com.example.data.model.SyncErrorLog
import com.example.data.model.SyncHealthState
import com.example.data.sync.InventorySyncService
import com.example.data.sync.LocalInventoryStorageSync
import com.example.sms.SmsHelper
import com.example.firebase.GoogleAuthHelper
import com.example.firebase.OperationType
import com.example.firebase.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class StoreRepository(private val context: Context) {
  private val prefs: SharedPreferences = context.getSharedPreferences("sajgoj_store_prefs", Context.MODE_PRIVATE)
  private val localStorageSync = LocalInventoryStorageSync(prefs)

  // Initial Seed Data
  private val initialProducts = listOf(
    Product(
      id = "p1",
      name = "এক্সক্লুসিভ কাতান ডিজাইনার শাড়ি (Royal Red)",
      price = 3500,
      regularPrice = 4200,
      offerPrice = 3500,
      stock = 8,
      category = "শাড়ি",
      imageUrl = "res://banner_eid_special_1792530116809",
      sku = "SAJ-001",
      description = "খাঁটি বেনারসি ও কাতানের মেলবন্ধনে তৈরি সোনালী জরি পাড়যুক্ত জমকালো ডিজাইনার শাড়ি। উৎসব এবং বিয়ের অনুষ্ঠানের জন্য আদর্শ কালেকশন। সাথে রয়েছে আনস্টিচড ম্যাচিং ব্লাউজ পিস।",
      colors = listOf("রয়েল রেড", "মেরুন", "কালো", "গোল্ডেন"),
      sizes = listOf("Free Size"),
      isHotSale = true,
      isBestDeal = true,
      isB2G1 = true,
      createdAt = 1729500001000L
    ),
    Product(
      id = "p2",
      name = "রাজওয়াড়ি কুন্দন ব্রাইডাল চোকার সেট",
      price = 2999,
      regularPrice = 3650,
      offerPrice = 2999,
      stock = 14,
      category = "নেকলেস",
      imageUrl = "res://banner_jewelry_royal_1792530130914",
      sku = "SAJ-002",
      description = "অ্যান্টিক গোল্ড প্লেটেড রয়্যাল কুন্দন চোকার, সাথে ম্যাচিং স্টেটমেন্ট ঝুমকা এবং টিকলি। রুচিশীল রত্নপাথর ও মুক্তোর কারুকার্যময় কাজ।",
      colors = listOf("রয়্যাল গোল্ডেন", "অ্যান্টিক গোল্ড", "রুবী রেড"),
      isHotSale = true,
      isBestDeal = true,
      isB2G1 = true,
      createdAt = 1729500002000L
    ),
    Product(
      id = "p3",
      name = "সিটি গোল্ড রাজকীয় বালা ও চুড়ি সেট (৪ পিস)",
      price = 1250,
      regularPrice = 1550,
      offerPrice = 1250,
      stock = 4,
      category = "চুড়ি",
      imageUrl = "res://ic_app_icon_1792530102214",
      sku = "SAJ-003",
      description = "লং লাস্টিং টেকসই প্রিমিয়াম সিটি গোল্ড চুড়ি সেট (৪ পিস)। প্রতিদিনের ব্যবহার এবং পার্টিতে পরার জন্য দারুণ ফিনিশিং।",
      sizes = listOf("2.4", "2.6", "2.8"),
      isHotSale = false,
      isBestDeal = true,
      createdAt = 1729500003000L
    ),
    Product(
      id = "p4",
      name = "রুবি স্টোন ক্রিস্টাল ব্রেসলেট পেয়ার",
      price = 890,
      regularPrice = 1150,
      offerPrice = 890,
      stock = 20,
      category = "ব্রেসলেট",
      imageUrl = "res://banner_jewelry_royal_1792530130914",
      sku = "SAJ-004",
      description = "হালকা ও আকর্ষণীয় রুবি ক্রিস্টাল পাথরে খচিত অ্যাডজাস্টেবল ব্রেসলেট জোড়া। যেকোনো পোশাকের সাথে দারুণ মানানসই।",
      colors = listOf("রুবী রেড", "পান্না সবুজ", "রয়েল ব্লু"),
      isHotSale = true,
      isBestDeal = true,
      isB2G1 = true,
      createdAt = 1729500004000L
    ),
    Product(
      id = "p5",
      name = "জামদানি কারুকাজ করা সুতি ও সিল্ক শাড়ি",
      price = 4200,
      regularPrice = 4990,
      offerPrice = 4200,
      stock = 6,
      category = "শাড়ি",
      imageUrl = "res://banner_eid_special_1792530116809",
      sku = "SAJ-005",
      description = "হাতে বোনা ঐতিহ্যবাহী জামদানি মোটিফ এবং নরম আরামদায়ক সিল্ক-সুতি টেক্সচার। আভিজাত্যে ভরপুর বিশেষ সংগ্রহ।",
      colors = listOf("হলুদ ও লাল", "কালো ও সোনালী", "রানি গোলাপি"),
      sizes = listOf("Free Size"),
      isHotSale = true,
      isBestDeal = true,
      createdAt = 1729500005000L
    ),
    Product(
      id = "p6",
      name = "মীনাকারি ঝুমকো ও নোলক সেট",
      price = 1450,
      regularPrice = 1850,
      offerPrice = 1450,
      stock = 2,
      category = "নেকলেস",
      imageUrl = "res://ic_app_icon_1792530102214",
      sku = "SAJ-006",
      description = "রঙিন এনামেল কারুকাজ সমৃদ্ধ ঐতিহ্যবাহী মীনাকারি কানের দুল ও নোলক। উৎসবের সাজে দেবে এক রাজকীয় মাত্রা।",
      isHotSale = true,
      isBestDeal = true,
      isB2G1 = true,
      createdAt = 1729500006000L
    ),
    Product(
      id = "p7",
      name = "বাচ্চাদের ডিজাইনার পার্টি ফ্রক ও গাউন (Kids Frock)",
      price = 1450,
      regularPrice = 1790,
      offerPrice = 1450,
      stock = 12,
      category = "বাচ্চাদের পোশাক",
      imageUrl = "res://banner_eid_special_1792530116809",
      sku = "KID-001",
      description = "বাচ্চাদের জন্য আকর্ষণীয় সফট নেট ও সুতি কাপড়ে তৈরি আরামদায়ক ডিজাইনার পার্টি ফ্রক। জন্মদিন এবং উৎসব অনুষ্ঠানের জন্য সেরা পছন্দ।",
      colors = listOf("গোলাপী", "রয়েল রেড", "স্কাই ব্লু", "সাদা"),
      sizes = listOf("20", "22", "24", "26", "28"),
      ageGroups = listOf("১-২ বছর", "২-৩ বছর", "৩-৪ বছর", "৪-৫ বছর", "৫-৬ বছর"),
      isHotSale = true,
      isBestDeal = false,
      createdAt = 1729500007000L
    ),
    Product(
      id = "p8",
      name = "বাচ্চাদের রয়্যাল সুতি পাঞ্জাবি ও পায়জামা সেট",
      price = 1190,
      regularPrice = 1490,
      offerPrice = 1190,
      stock = 10,
      category = "বাচ্চাদের পোশাক",
      imageUrl = "res://ic_app_icon_1792530102214",
      sku = "KID-002",
      description = "১০০% প্রিমিয়াম সুতি কাপড়ে তৈরি আরামদায়ক বাচ্চাদের পাঞ্জাবি সেট। গলায় সূক্ষ্ম সুতার এমব্রয়ডারি কাজ করা।",
      colors = listOf("অফ হোয়াইট", "মেরুন", "রয়েল ব্লু", "সবুজ"),
      sizes = listOf("18", "20", "22", "24", "26", "28"),
      ageGroups = listOf("০-৬ মাস", "৬-১২ মাস", "১-২ বছর", "২-৩ বছর", "৩-৪ বছর", "৫-৬ বছর"),
      isHotSale = false,
      isBestDeal = false,
      createdAt = 1729500008000L
    ),
    Product(
      id = "p9",
      name = "কিউট পার্টি ফ্রক ও বেবি ড্রেস (বাচ্চাদের ড্রেস)",
      price = 990,
      regularPrice = 1290,
      offerPrice = 990,
      stock = 20,
      category = "বাচ্চাদের পোশাক",
      imageUrl = "res://banner_eid_special_1792530116809",
      sku = "SAJ-KD05",
      description = "বাচ্চাদের কোমল ত্বকের জন্য সম্পূর্ণ নিরাপদ, নরম সুতি লাইনিংযুক্ত প্রিমিয়াম পার্টি ফ্রক। উৎসব ও জন্মদিনের জন্য সেরা পছন্দ।",
      rating = 4.9f,
      reviewsCount = 22,
      colors = listOf("বেবি পিংক", "লাল", "আকাশি", "হলুদ"),
      sizes = listOf("১৬", "১৮", "২০", "২২", "২৪", "২৬", "২৮"),
      ageGroups = listOf("১-২ বছর", "২-৩ বছর", "৩-৪ বছর", "৫-৬ বছর"),
      isHotSale = true,
      isBestDeal = true,
      isB2G1 = true,
      createdAt = 1729500009000L
    ),
    Product(
      id = "p10",
      name = "ঐতিহ্যবাহী ঢাকাই জামদানি শাড়ি (Golden Zari)",
      price = 4500,
      regularPrice = 5400,
      offerPrice = 4500,
      stock = 10,
      category = "শাড়ি",
      imageUrl = "res://banner_eid_special_1792530116809",
      sku = "SAJ-SR10",
      description = "হাতে বোনা আসল দেশি ঢাকাই জামদানি শাড়ি, সোনালী জরির সূক্ষ্ম নকশা সমৃদ্ধ। বিয়ে ও উৎসবের সাজে অনন্য।",
      rating = 4.9f,
      reviewsCount = 31,
      colors = listOf("রয়েল রেড", "গোল্ডেন", "অফ হোয়াইট"),
      sizes = listOf("Free Size"),
      isHotSale = true,
      isBestDeal = true,
      createdAt = 1729500010000L
    ),
    Product(
      id = "p11",
      name = "রয়েল ব্রাইডাল পার্ল ও কুন্দন সেট (Full Set)",
      price = 3850,
      regularPrice = 4600,
      offerPrice = 3850,
      stock = 7,
      category = "ব্রাইডাল সেট",
      imageUrl = "res://banner_jewelry_royal_1792530130914",
      sku = "SAJ-BR11",
      description = "সীতাহার, চোকার, ঝুমকা ও টিকলিসহ সম্পূর্ণ রয়্যাল ব্রাইডাল জুয়েলারি সেট।",
      rating = 5.0f,
      reviewsCount = 27,
      colors = listOf("গোল্ডেন ও মুক্তা", "রুবি গোল্ড"),
      sizes = listOf("Free Size"),
      isHotSale = false,
      isBestDeal = true,
      isB2G1 = true,
      createdAt = 1729500011000L
    ),
    Product(
      id = "p12",
      name = "ডিজাইনার লং কটন থ্রি-পিস কালেকশন",
      price = 1890,
      regularPrice = 2350,
      offerPrice = 1890,
      stock = 18,
      category = "জামা / কামিজ",
      imageUrl = "res://banner_eid_special_1792530116809",
      sku = "SAJ-KM12",
      description = "পিওর কটন কাপড়ের ওপর নিখুঁত কাজের প্রিমিয়াম থ্রি-পিস ও ওড়না সেট।",
      rating = 4.8f,
      reviewsCount = 16,
      colors = listOf("মেরুন", "নীল", "জলপাই"),
      sizes = listOf("৩৮", "৪০", "৪২", "৪৪"),
      isHotSale = true,
      isBestDeal = true,
      createdAt = 1729500012000L
    ),
    Product(
      id = "p13",
      name = "প্রিমিয়াম ড্রপ-শোল্ডার কটন টি-শার্ট (Premium Tee)",
      price = 550,
      regularPrice = 750,
      offerPrice = 550,
      stock = 24,
      category = "টি-শার্ট",
      imageUrl = "res://ic_app_icon_1792530102214",
      sku = "SAJ-TS13",
      description = "২২০ জিএসএম ১০০% কম্বড কটন ড্রপ-শোল্ডার টি-শার্ট। প্রতিদিনের আরামদায়ক ও স্টাইলিশ ব্যবহারের জন্য সেরা।",
      rating = 4.9f,
      reviewsCount = 24,
      colors = listOf("কালো", "মেরুন", "অফ হোয়াইট", "নেভি ব্লু"),
      sizes = listOf("M", "L", "XL", "XXL"),
      isHotSale = true,
      isBestDeal = true,
      createdAt = 1729500013000L
    ),
    Product(
      id = "p14",
      name = "কম্ফোর্ট স্ট্রেচ চিনো ও গ্যাবার্ডিন প্যান্ট",
      price = 1190,
      regularPrice = 1550,
      offerPrice = 1190,
      stock = 16,
      category = "প্যান্ট",
      imageUrl = "res://banner_eid_special_1792530116809",
      sku = "SAJ-PN14",
      description = "স্লিম-কম্ফোর্ট ফিট স্ট্রেচ কটন টুইল প্যান্ট। অফিস ও ক্যাজুয়াল দুই ক্ষেত্রেই দারুণ মানানসই।",
      rating = 4.8f,
      reviewsCount = 19,
      colors = listOf("কালো", "নেভি ব্লু", "খাকি"),
      sizes = listOf("30", "32", "34", "36"),
      isHotSale = false,
      isBestDeal = true,
      createdAt = 1729500014000L
    ),
    Product(
      id = "p15",
      name = "মুক্তা ও স্টোন খচিত রাজকীয় চুড়ি-বালা কালেকশন",
      price = 1390,
      regularPrice = 1750,
      offerPrice = 1390,
      stock = 14,
      category = "চুড়ি ও বালা",
      imageUrl = "res://banner_jewelry_royal_1792530130914",
      sku = "SAJ-BG15",
      description = "আধুনিক ও ঐতিহ্যবাহী ডিজাইনের মুক্তা বসানো বালা ও চুড়ি সেট। যেকোনো উৎসবে হাতে দেবে রাজকীয় আভা।",
      rating = 4.9f,
      reviewsCount = 21,
      colors = listOf("গোল্ডেন", "রোজ গোল্ড"),
      sizes = listOf("2.4", "2.6", "2.8"),
      isHotSale = true,
      isBestDeal = true,
      isB2G1 = true,
      createdAt = 1729500015000L
    )
  )

  private val initialCombos = listOf(
    ComboPack(
      id = "combo-1",
      title = "রয়েল ব্রাইডাল শাড়ি + কুন্দন চোকার কম্বো",
      subtitle = "ঈদ ও বিয়ের রাজকীয় সেট",
      description = "এক্সক্লুসিভ কাতান শাড়ি এবং রাজওয়াড়ি কুন্দন ব্রাইডাল চোকার সেট একসাথে স্পেশাল কম্বো মূল্যে।",
      badge = "ব্রাইডাল কম্বো ✨",
      imageUrl = "res://banner_eid_special_1792530116809",
      productIds = listOf("p1", "p2"),
      regularPrice = 6499,
      comboPrice = 5499,
      savings = 1000,
      isActive = true,
      stock = 8
    ),
    ComboPack(
      id = "combo-2",
      title = "জামদানি শাড়ি + ঝুমকো & বালা উৎসব কম্বো",
      subtitle = "৩টি প্রিমিয়াম পণ্যের সেট",
      description = "জামদানি শাড়ি, মীনাকারি ঝুমকো ও সিটি গোল্ড চুড়ি সেট — উৎসবের সম্পূর্ণ সাজ এক প্যাকেজে।",
      badge = "হট কম্বো 🔥",
      imageUrl = "res://banner_jewelry_royal_1792530130914",
      productIds = listOf("p5", "p6", "p3"),
      regularPrice = 6900,
      comboPrice = 5850,
      savings = 1050,
      isActive = true,
      stock = 6
    ),
    ComboPack(
      id = "combo-3",
      title = "ফ্যামিলি কটন টি-শার্ট + চিনো প্যান্ট কম্বো",
      subtitle = "স্মার্ট ক্যাজুয়াল ডুয়ো",
      description = "প্রিমিয়াম ড্রপ-শোল্ডার কটন টি-শার্ট ও কম্ফোর্ট চিনো প্যান্ট কম্বো ডিল।",
      badge = "সুপার সেভ 💰",
      imageUrl = "res://ic_app_icon_1792530102214",
      productIds = listOf("p13", "p14"),
      regularPrice = 1740,
      comboPrice = 1490,
      savings = 250,
      isActive = true,
      stock = 12
    ),
    ComboPack(
      id = "combo-4",
      title = "লং কটন থ্রি-পিস + ক্রিস্টাল ব্রেসলেট কম্বো",
      subtitle = "ডেইলি ও পার্টি স্টাইল",
      description = "ডিজাইনার কটন থ্রি-পিস সাথে ফ্রি-ম্যাচিং রুবি ক্রিস্টাল ব্রেসলেট পেয়ার।",
      badge = "মেগা অফার 🎁",
      imageUrl = "res://banner_eid_special_1792530116809",
      productIds = listOf("p12", "p4"),
      regularPrice = 2780,
      comboPrice = 2350,
      savings = 430,
      isActive = true,
      stock = 10
    )
  )

  private val initialHomeSections = listOf(
    HomeSectionConfig(id = "top_banner", type = "top_banner", title = "হিরো ব্যানার ও স্লাইডার", subtitle = "হোম পেজের প্রধান ব্যানার স্লাইডার", icon = "🖼️", layout = "banner", displayStyle = "banner", columnsPerRow = 1, rowCount = 1, enabled = true),
    HomeSectionConfig(id = "voucher_strip", type = "voucher_strip", title = "ভাউচার ডিসকাউন্ট কার্ড", subtitle = "Claim Vouchers to Save More!", icon = "🎟️", layout = "strip", displayStyle = "strip", columnsPerRow = 1, rowCount = 1, enabled = true),
    HomeSectionConfig(id = "category_slider", type = "category_slider", title = "স্লাইডিং ক্যাটাগরি কার্ড", subtitle = "ব্যানারের নিচের স্লাইডিং ক্যাটাগরি", icon = "🏷️", layout = "strip", displayStyle = "strip", columnsPerRow = 1, rowCount = 1, enabled = true),
    HomeSectionConfig(id = "new_products", type = "new_products", title = "নতুন প্রোডাক্ট", subtitle = "নতুন আপলোড হওয়া প্রোডাক্ট", icon = "✨", badge = "NEW", layout = "slider3", columnsPerRow = 3, rowCount = 1, displayStyle = "slider", enabled = true, maxItems = 12),
    HomeSectionConfig(id = "hot_sale", type = "hot_sale", title = "Hot Sale", subtitle = "হট সেল প্রোডাক্ট ডিসপ্লে", icon = "🔥", badge = "HOT 🔥", layout = "slider3", columnsPerRow = 3, rowCount = 1, displayStyle = "slider", enabled = true, productIds = listOf("p9", "p6", "p5", "p4", "p2", "p1", "p13", "p15"), maxItems = 12),
    HomeSectionConfig(id = "combo_packs", type = "combo_packs", title = "কম্বো প্যাক", subtitle = "বান্ডেল কিনে বিশাল সেভ করুন", icon = "🎁", badge = "COMBO", layout = "slider3", columnsPerRow = 3, rowCount = 1, displayStyle = "slider", enabled = true, maxItems = 12),
    HomeSectionConfig(id = "b2g1_offers", type = "b2g1_offers", title = "Buy 2 Get 1", subtitle = "যেকোনো ২টি নিলেই ১টি ফ্রি", icon = "🎉", badge = "BUY 2 GET 1", layout = "slider3", columnsPerRow = 3, rowCount = 1, displayStyle = "slider", enabled = true, productIds = listOf("p1", "p2", "p4", "p6", "p9", "p11", "p15"), maxItems = 12),
    HomeSectionConfig(id = "best_deals", type = "best_deals", title = "সেরা ডিল", subtitle = "২ লাইনে ৬টি প্রোডাক্ট ডিসপ্লে", icon = "⭐", badge = "সেরা ডিল", layout = "grid3x2", columnsPerRow = 3, rowCount = 2, displayStyle = "grid", enabled = true, productIds = listOf("p1", "p2", "p3", "p4", "p5", "p6", "p9", "p10", "p11", "p12"), maxItems = 6),
    HomeSectionConfig(id = "reels_showcase", type = "reels_showcase", title = "Product Reels", subtitle = "পন্যের ভিডিও দেখে অর্ডার করুন", icon = "🎬", badge = "", layout = "slider3", columnsPerRow = 3, rowCount = 1, displayStyle = "slider", enabled = true, maxItems = 3),
    HomeSectionConfig(id = "all_products", type = "all_products", title = "সব প্রোডাক্ট", subtitle = "সব ক্যাটাগরির প্রোডাক্ট ডিসপ্লে", icon = "🛍️", badge = "সকল পণ্য", layout = "grid3x3", columnsPerRow = 3, rowCount = 3, displayStyle = "grid", enabled = true, maxItems = 9)
  )

  private val initialReels = listOf(
    ReelItem(
      id = "reel-1",
      title = "রাজকীয় কুন্দন চোকার ও ঝুমকা সেট আনবক্সিং ✨",
      caption = "প্রিমিয়াম ফিনিশিং ও নিখুঁত কারুকার্যের বিয়ের স্পেশাল কালেকশন।",
      videoUrl = "https://assets.mixkit.co/videos/preview/mixkit-hands-holding-a-gold-necklace-41380-large.mp4",
      posterUrl = "res://banner_jewelry_royal_1792530130914",
      productId = "p2",
      views = "2.8k",
      likes = 142,
      badge = "হট ট্রেন্ডিং 🔥",
      price = 2999
    ),
    ReelItem(
      id = "reel-2",
      title = "এক্সক্লুসিভ পিওর জামদানি শাড়ি ড্র্যাপ ও লুক 🥻",
      caption = "আসল দেশি তাঁতের বোনা কাতান ও জামদানি শাড়ি কালেকশন।",
      videoUrl = "https://assets.mixkit.co/videos/preview/mixkit-traditional-oriental-fabric-moving-slowly-41656-large.mp4",
      posterUrl = "res://banner_eid_special_1792530116809",
      productId = "p1",
      views = "5.1k",
      likes = 236,
      badge = "বেস্টসেলার ⭐",
      price = 3500
    ),
    ReelItem(
      id = "reel-3",
      title = "এন্টিক গোল্ড প্লেটেড বালা ও চুড়ি সেট 👑",
      caption = "রয়েল ব্রাইডাল সাজের আসল আভিজাত্য। নিখুঁত গোল্ডেন ফিনিশিং।",
      videoUrl = "https://assets.mixkit.co/videos/preview/mixkit-delicate-gold-jewelry-pieces-on-display-42044-large.mp4",
      posterUrl = "res://ic_app_icon_1792530102214",
      productId = "p3",
      views = "3.4k",
      likes = 189,
      badge = "ব্রাইডাল 💍",
      price = 1250
    ),
    ReelItem(
      id = "reel-4",
      title = "ডিজাইনার রেশম কাতান ও জমকালো শাড়ি কালেকশন ✨",
      caption = "হাতে বোনা গর্জিয়াস শাড়ির রিয়েল ভিডিও প্রিভিউ।",
      videoUrl = "https://assets.mixkit.co/videos/preview/mixkit-hands-touching-silk-textile-42004-large.mp4",
      posterUrl = "res://banner_eid_special_1792530116809",
      productId = "p10",
      views = "2.1k",
      likes = 112,
      badge = "নতুন 🌸",
      price = 4500
    )
  )

  private val initialCategories = listOf(
    // Parent Categories
    Category("pcat-women", "মহিলাদের ফ্যাশন", "👗", type = "parent", badge = "জনপ্রিয়"),
    Category("pcat-jewelry", "গহনা ও জুয়েলারি", "💍", type = "parent", badge = "রাজকীয়"),
    Category("pcat-kids", "বাচ্চাদের আয়োজন", "👶", type = "parent", badge = "স্পেশাল"),
    Category("pcat-men", "পুরুষদের কালেকশন", "👕", type = "parent", badge = "ট্রেন্ডি"),

    // Main Categories (Shown in Slider)
    Category("c1", "শাড়ি", "👗", type = "main", parentId = "pcat-women", badge = "হট 🔥", showInSlider = true),
    Category("c2", "জামা / কামিজ", "🥻", type = "main", parentId = "pcat-women", badge = "নতুন", showInSlider = true),
    Category("c5", "ব্রাইডাল সেট", "👑", type = "main", parentId = "pcat-jewelry", badge = "বেস্ট", showInSlider = true),
    Category("c3", "নেকলেস", "📿", type = "main", parentId = "pcat-jewelry", badge = "অফার", showInSlider = true),
    Category("c4", "চুড়ি ও বালা", "💍", type = "main", parentId = "pcat-jewelry", badge = "", showInSlider = true),
    Category("c6", "বাচ্চাদের পোশাক", "👶", type = "main", parentId = "pcat-kids", badge = "কিউট", showInSlider = true),
    Category("c7", "টি-শার্ট", "👕", type = "main", parentId = "pcat-men", badge = "", showInSlider = true),
    Category("c8", "প্যান্ট", "👖", type = "main", parentId = "pcat-men", badge = "", showInSlider = true),

    // Sub Categories
    Category("sub-c1-1", "কাতান শাড়ি", "✨", type = "sub", parentId = "c1"),
    Category("sub-c1-2", "জামদানি শাড়ি", "🥻", type = "sub", parentId = "c1"),
    Category("sub-c6-1", "০-২ বছর বয়স", "🍼", type = "sub", parentId = "c6"),
    Category("sub-c6-2", "৩-৫ বছর বয়স", "🎈", type = "sub", parentId = "c6"),
    Category("sub-c6-3", "৬-১০ বছর বয়স", "🎒", type = "sub", parentId = "c6"),
    Category("sub-c6-4", "পার্টি ফ্রক", "🎀", type = "sub", parentId = "c6")
  )

  private val initialBanners = listOf(
    BannerItem(
      id = "b1",
      title = "Eid Collection 2026",
      subtitle = "এক্সক্লুসিভ ডিজাইনার শাড়ি ও রাজকীয় জুয়েলারি কালেকশন",
      buttonText = "কালেকশন দেখুন",
      imageUrl = "res://banner_eid_special_1792530116809",
      isActive = true
    ),
    BannerItem(
      id = "b2",
      title = "রাজওয়াড়ি ব্রাইডাল কালেকশন",
      subtitle = "ফ্ল্যাট ২০% ছাড় • খাঁটি কুন্দন ও মুক্তোর নিখুঁত কাজ",
      buttonText = "অফার লুফে নিন",
      imageUrl = "res://banner_jewelry_royal_1792530130914",
      isActive = true
    )
  )

  private val initialCoupons = listOf(
    Coupon("cp1", "EID2026", "%", 10, 2000, "2026-06-30", true),
    Coupon("cp2", "NEW100", "Flat", 100, 500, "2026-12-31", true),
    Coupon("cp3", "SAJGOJ20", "%", 20, 4000, "2026-12-31", true)
  )

  private val initialOrders = listOf(
    Order(
      id = "ORD-1001",
      customerName = "ফাতেমা আক্তার",
      phone = "01712345678",
      district = "ঢাকা (Dhaka)",
      thana = "ধানমন্ডি",
      address = "বাসা ১২, রোড ৫, ধানমন্ডি",
      items = listOf(OrderItem("p1", "এক্সক্লুসিভ কাতান ডিজাইনার শাড়ি (Royal Red)", 1, 3500)),
      subtotal = 3500,
      deliveryCharge = 0,
      discount = 0,
      total = 3500,
      paymentMethod = "Cash on Delivery",
      status = "Pending",
      date = "2026-10-18",
      trackingId = "",
      orderType = "Normal",
      deliveryType = "Standard",
      deliveredBy = "",
      addressCategory = "Home",
      createdAt = System.currentTimeMillis() - 2L * 24L * 3600L * 1000L
    ),
    Order(
      id = "ORD-1002",
      customerName = "নুসরাত জাহান",
      phone = "01813987654",
      district = "চাঁদপুর (Chandpur)",
      thana = "হাজীগঞ্জ",
      address = "হাজীগঞ্জ বাজার রোড, চাঁদপুর",
      items = listOf(
        OrderItem("p2", "রাজওয়াড়ি কুন্দন ব্রাইডাল চোকার সেট", 1, 2999),
        OrderItem("p4", "রুবি স্টোন ক্রিস্টাল ব্রেসলেট পেয়ার", 1, 890)
      ),
      subtotal = 3889,
      deliveryCharge = 100,
      discount = 388,
      couponCode = "EID2026",
      total = 3601,
      paymentMethod = "bKash",
      status = "Shipped",
      date = "2026-10-19",
      trackingId = "ST-884219",
      orderType = "Normal",
      deliveryType = "Express",
      deliveredBy = "SteadFast",
      addressCategory = "Home",
      createdAt = System.currentTimeMillis() - 12L * 24L * 3600L * 1000L
    )
  )

  private val initialReviews = listOf(
    ProductReview(
      id = "rev-1",
      productId = "p1",
      customerName = "সুমাইয়া রহমান",
      rating = 5,
      comment = "শাড়িটা সামনাসামনি আরও অনেক সুন্দর! কাপড়ের কোয়ালিটি এবং জরি ও সুতার কাজ অসাধারণ। প্যাকেজিংও খুব প্রিমিয়াম ছিল।",
      date = "2026-10-15",
      verified = true
    ),
    ProductReview(
      id = "rev-2",
      productId = "p1",
      customerName = "তানজিনা ইসলাম",
      rating = 5,
      comment = "অসাধারণ কালেকশন! ঈদের জন্য নিয়েছিলাম, পরিবারের সবাই খুব প্রশংসা করেছে। ডেলিভারিও খুব দ্রুত পেয়েছি।",
      date = "2026-10-17",
      verified = true
    ),
    ProductReview(
      id = "rev-3",
      productId = "p1",
      customerName = "মেহজাবিন চৌধুরী",
      rating = 4,
      comment = "রংটা ছবিতে যেমন ছিল হুবহু তেমন। ব্লাউজ পিসটাও চমৎকার। সাজগোজকে ধন্যবাদ।",
      date = "2026-10-19",
      verified = true
    ),
    ProductReview(
      id = "rev-4",
      productId = "p2",
      customerName = "আফসানা মিমি",
      rating = 5,
      comment = "চোকার সেটটি রাজকীয় লুক দেয়! কুন্দনের ফিনিশিং অসম্ভব সুন্দর এবং ভারি মনে হলেও পরতে খুব আরামদায়ক।",
      date = "2026-10-12",
      verified = true
    ),
    ProductReview(
      id = "rev-5",
      productId = "p2",
      customerName = "নাদিয়া হাসান",
      rating = 5,
      comment = "ব্রাইডাল সাজের জন্য পারফেক্ট একটা সেট। ঝুমকা দুটোও চমৎকার মানিয়েছে। হাইলি রেকমেন্ডেড!",
      date = "2026-10-18",
      verified = true
    ),
    ProductReview(
      id = "rev-6",
      productId = "p3",
      customerName = "রোকসানা পারভীন",
      rating = 5,
      comment = "সিটি গোল্ডের ফিনিশিং একদম খাঁটি সোনার মতো চকচক করে। ৪ পিস বালা পেয়ে খুবই খুশি।",
      date = "2026-10-16",
      verified = true
    ),
    ProductReview(
      id = "rev-7",
      productId = "p4",
      customerName = "সাদিয়া আফরিন",
      rating = 5,
      comment = "রুবি স্টোনগুলো খুব উজ্জ্বল এবং ব্রেসলেটের সাইজ খুব সহজেই অ্যাডজাস্ট করা যায়। দারুণ উপহার হতে পারে।",
      date = "2026-10-14",
      verified = true
    )
  )

  // StateFlows
  private val _products = MutableStateFlow<List<Product>>(loadProducts())
  val products: StateFlow<List<Product>> = _products.asStateFlow()

  private val _reviews = MutableStateFlow<List<ProductReview>>(initialReviews)
  val reviews: StateFlow<List<ProductReview>> = _reviews.asStateFlow()

  private val _categories = MutableStateFlow<List<Category>>(initialCategories)
  val categories: StateFlow<List<Category>> = _categories.asStateFlow()

  private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
  val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

  private val _orders = MutableStateFlow<List<Order>>(initialOrders)
  val orders: StateFlow<List<Order>> = _orders.asStateFlow()

  private val _coupons = MutableStateFlow<List<Coupon>>(initialCoupons)
  val coupons: StateFlow<List<Coupon>> = _coupons.asStateFlow()

  private val _banners = MutableStateFlow<List<BannerItem>>(initialBanners)
  val banners: StateFlow<List<BannerItem>> = _banners.asStateFlow()

  private val _storeConfig = MutableStateFlow(loadStoreConfig())
  val storeConfig: StateFlow<StoreConfig> = _storeConfig.asStateFlow()

  private val _courierConfig = MutableStateFlow(CourierConfig())
  val courierConfig: StateFlow<CourierConfig> = _courierConfig.asStateFlow()

  private val _pixelConfig = MutableStateFlow(PixelConfig())
  val pixelConfig: StateFlow<PixelConfig> = _pixelConfig.asStateFlow()

  private val _homeSections = MutableStateFlow<List<HomeSectionConfig>>(loadHomeSections())
  val homeSections: StateFlow<List<HomeSectionConfig>> = _homeSections.asStateFlow()

  private val _reels = MutableStateFlow<List<ReelItem>>(initialReels)
  val reels: StateFlow<List<ReelItem>> = _reels.asStateFlow()

  private val _combos = MutableStateFlow<List<ComboPack>>(initialCombos)
  val combos: StateFlow<List<ComboPack>> = _combos.asStateFlow()

  private val initialReturns = listOf(
    ReturnRecord(
      id = "RET-884211",
      orderId = "ORD-1002",
      customerName = "নুসরাত জাহান",
      phone = "01813987654",
      date = "2026-10-21",
      returnedItems = listOf(
        ReturnItem(
          productId = "p4",
          name = "রুবি স্টোন ক্রিস্টাল ব্রেসলেট পেয়ার",
          price = 890,
          quantity = 1,
          condition = "ভালো (বিক্রিযোগ্য)",
          restock = true
        )
      ),
      totalReturnValue = 890,
      isExchange = true,
      exchangeItems = listOf(
        ExchangeItem(
          productId = "p1",
          name = "এক্সক্লুসিভ কাতান ডিজাইনার শাড়ি (Royal Red)",
          price = 3500,
          quantity = 1,
          color = "Royal Red",
          size = "Free Size",
          sku = "KT-889"
        )
      ),
      totalExchangeValue = 3500,
      deliveryFee = 0,
      priceDifference = 2610,
      differenceType = "customer_paid_extra",
      settlementMethod = "bKash",
      settlementTrxId = "TRX9938120",
      settlementStatus = "paid",
      reason = "পণ্য পরিবর্তন / অপছন্দ",
      note = "ব্রেসলেট ফেরত দিয়ে শাড়ি এক্সচেঞ্জ নেওয়া হয়েছে। অতিরিক্ত ২৬১০ টাকা বিকাশে গ্রহণ করা হয়েছে।"
    )
  )

  private val _returns = MutableStateFlow<List<ReturnRecord>>(initialReturns)
  val returns: StateFlow<List<ReturnRecord>> = _returns.asStateFlow()

  private val _smsQueue = MutableStateFlow<List<SmsQueueItem>>(loadSmsQueueFromPrefs())
  val smsQueue: StateFlow<List<SmsQueueItem>> = _smsQueue.asStateFlow()

  private val firestore: FirebaseFirestore? by lazy {
    try {
      GoogleAuthHelper.createFirestore(context)
    } catch (_: Exception) {
      null
    }
  }

  private var ordersListener: ListenerRegistration? = null
  private var returnsListener: ListenerRegistration? = null
  private var smsQueueListener: ListenerRegistration? = null

  private fun currentAuthUid(): String? = try {
    Firebase.auth.currentUser?.uid
  } catch (_: Exception) {
    null
  }

  private fun isVerifiedAdmin(): Boolean = try {
    GoogleAuthHelper.toSummary(Firebase.auth.currentUser)?.isAdmin == true
  } catch (_: Exception) {
    false
  }

  val inventorySyncService: InventorySyncService by lazy {
    InventorySyncService(firestore = firestore, localStorageSync = localStorageSync)
  }

  val posInventory: StateFlow<Map<String, PosInventoryItem>>
    get() = inventorySyncService.posInventory

  val syncEvents: StateFlow<List<InventorySyncEvent>>
    get() = inventorySyncService.syncEvents

  val syncHealth: StateFlow<SyncHealthState>
    get() = inventorySyncService.syncHealth

  val syncErrorLogs: StateFlow<List<SyncErrorLog>>
    get() = inventorySyncService.syncErrorLogs

  init {
    localStorageSync.errorReporter = { source, code, message, details ->
      inventorySyncService.recordSyncError(
        source = source,
        severity = "ERROR",
        code = code,
        message = message,
        details = details,
        products = _products.value
      )
    }
    inventorySyncService.mirrorCatalogToPos(_products.value, source = "INITIAL_SEED", pushToCloud = false)
    persistLocalSyncSnapshot()
    localStorageSync.startListening(
      getCurrentProducts = { _products.value },
      onSynchronizedProducts = { updatedProducts, changedKey ->
        _products.value = updatedProducts
        val source = if (changedKey == LocalInventoryStorageSync.KEY_POS_PRODUCTS) {
          "POS_LOCAL_STORAGE"
        } else {
          "WEBSITE_LOCAL_STORAGE"
        }
        inventorySyncService.markLocalSync(products = updatedProducts)
        inventorySyncService.mirrorCatalogToPos(updatedProducts, source = source, pushToCloud = true)
      }
    )
    initFirestoreSync()
  }

  private fun persistLocalSyncSnapshot() {
    localStorageSync.persistBoth(
      products = _products.value,
      posMap = inventorySyncService.posInventory.value
    )
    inventorySyncService.markLocalSync(products = _products.value)
  }

  private fun initFirestoreSync() {
    val db = firestore ?: return
    try {
      db.collection("products").addSnapshotListener { snapshot, error ->
        if (error != null) {
          handleFirestoreError(error, OperationType.LIST, "products")
          inventorySyncService.recordSyncError(
            source = "FIRESTORE_PRODUCTS_LISTENER",
            severity = "ERROR",
            code = error.code.name,
            message = error.localizedMessage ?: "Firestore products লিসেনার ব্যর্থ হয়েছে",
            details = "collection=products",
            products = _products.value
          )
          return@addSnapshotListener
        }
        if (snapshot != null && !snapshot.isEmpty) {
          val list = snapshot.documents.mapNotNull { doc ->
            try {
              val ratingVal = (doc.getDouble("rating") ?: doc.getLong("rating")?.toDouble() ?: 4.8).toFloat()
              val reviewsCountVal = (doc.getLong("reviewsCount") ?: doc.getLong("reviews") ?: 12L).toInt()
              val colorsList = (doc.get("colors") as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
              val sizesList = (doc.get("sizes") as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
              val ageGroupsList = (doc.get("ageGroups") as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
              val docId = doc.getString("id") ?: doc.id
              val docSku = doc.getString("sku") ?: ""
              val docPrice = (doc.getLong("price") ?: 0L).toInt()
              val seedMatch = initialProducts.find { it.id == docId || (docSku.isNotBlank() && it.sku.equals(docSku, ignoreCase = true)) }
              val parsedReg = (doc.getLong("regularPrice") ?: 0L).toInt()
              val regularPriceVal = if (parsedReg > 0) parsedReg else (seedMatch?.regularPrice ?: 0)
              val parsedOffer = (doc.getLong("offerPrice") ?: 0L).toInt()
              val offerPriceVal = when {
                parsedOffer > 0 -> parsedOffer
                regularPriceVal > docPrice && docPrice > 0 -> docPrice
                else -> (seedMatch?.offerPrice ?: 0)
              }
              val extraImages = (doc.get("images") as? List<*>)?.mapNotNull { it?.toString() }?.drop(1)
                ?: (doc.get("additionalImages") as? List<*>)?.mapNotNull { it?.toString() }
                ?: emptyList()
              val rawImageUrl = doc.getString("imageUrl") ?: (doc.get("images") as? List<*>)?.firstOrNull()?.toString() ?: ""
              val resolvedImageUrl = if (rawImageUrl.startsWith("res://") && rawImageUrl.removePrefix("res://").toIntOrNull() != null) {
                seedMatch?.imageUrl ?: "res://banner_eid_special_1792530116809"
              } else {
                rawImageUrl
              }
              Product(
                id = docId,
                name = doc.getString("name") ?: "",
                price = docPrice,
                stock = (doc.getLong("stock") ?: 0L).toInt(),
                category = doc.getString("category") ?: "শাড়ি",
                imageUrl = resolvedImageUrl,
                sku = docSku,
                description = doc.getString("description") ?: "",
                rating = ratingVal,
                reviewsCount = reviewsCountVal,
                sourceUrl = doc.getString("sourceUrl") ?: "",
                colors = colorsList,
                sizes = sizesList,
                ageGroups = ageGroupsList,
                stockUpdatedAt = doc.getLong("stockUpdatedAt") ?: 0L,
                lastSyncSource = doc.getString("lastSyncSource") ?: "",
                isHotSale = doc.getBoolean("isHotSale") ?: false,
                isBestDeal = doc.getBoolean("isBestDeal") ?: false,
                isB2G1 = doc.getBoolean("isB2G1") ?: false,
                createdAt = doc.getTimestamp("createdAt")?.toDate()?.time
                  ?: (doc.get("createdAt") as? Number)?.toLong()
                  ?: 0L,
                regularPrice = regularPriceVal,
                offerPrice = offerPriceVal,
                videoEnabled = doc.getBoolean("videoEnabled") ?: false,
                videoType = doc.getString("videoType") ?: "upload",
                videoUrl = doc.getString("videoUrl") ?: "",
                videoThumbUrl = doc.getString("videoThumbUrl") ?: "",
                additionalImages = extraImages
              )
            } catch (e: Exception) {
              inventorySyncService.recordSyncError(
                source = "FIRESTORE_PRODUCTS_PARSE",
                severity = "WARNING",
                code = e.javaClass.simpleName,
                message = "Firestore প্রোডাক্ট ডকুমেন্ট পার্স করতে ব্যর্থ",
                details = "doc=${doc.id} • ${e.localizedMessage ?: e.toString()}"
              )
              null
            }
          }
          if (list.isNotEmpty()) {
            _products.value = list
            inventorySyncService.markFirestoreSync(products = list)
            inventorySyncService.mirrorCatalogToPos(list, source = "STOREFRONT_CLOUD", pushToCloud = true)
            persistLocalSyncSnapshot()
          }
        }
      }

      db.collection("reviews").addSnapshotListener { snapshot, error ->
        if (error != null) {
          handleFirestoreError(error, OperationType.LIST, "reviews")
          return@addSnapshotListener
        }
        if (snapshot != null && !snapshot.isEmpty) {
          val list = snapshot.documents.mapNotNull { doc ->
            try {
              ProductReview(
                id = doc.getString("id") ?: doc.id,
                productId = doc.getString("productId") ?: "",
                customerName = doc.getString("customerName") ?: "",
                rating = (doc.getLong("rating") ?: 5L).toInt(),
                comment = doc.getString("comment") ?: "",
                date = doc.getString("date") ?: "",
                createdAt = doc.getTimestamp("createdAt")?.toDate()?.time
                  ?: (doc.get("createdAt") as? Number)?.toLong()
                  ?: System.currentTimeMillis(),
                verified = doc.getBoolean("verified") ?: true
              )
            } catch (e: Exception) {
              null
            }
          }.sortedWith(compareByDescending<ProductReview> { it.date }.thenByDescending { it.createdAt })
          if (list.isNotEmpty()) {
            _reviews.value = list
          }
        }
      }
    } catch (e: Exception) {
      inventorySyncService.recordSyncError(
        source = "FIRESTORE_INIT",
        severity = "ERROR",
        code = e.javaClass.simpleName,
        message = e.localizedMessage ?: "Firestore রিয়েল-টাইম সিঙ্ক শুরু করতে ব্যর্থ",
        details = e.toString(),
        products = _products.value
      )
    }
  }

  /**
   * Starts or tears down auth-gated Firestore listeners (orders, returns, POS sync)
   * strictly aligned with zero-trust rules and the signed-in Google account.
   */
  fun onAuthSessionChanged(user: FirebaseUser?) {
    ordersListener?.remove()
    ordersListener = null
    returnsListener?.remove()
    returnsListener = null
    smsQueueListener?.remove()
    smsQueueListener = null

    val db = firestore ?: return
    if (user == null) {
      inventorySyncService.stopListening()
      return
    }

    val isAdmin = GoogleAuthHelper.toSummary(user)?.isAdmin == true
    val ordersQuery = if (isAdmin) {
      db.collection("orders")
    } else {
      db.collection("orders").whereEqualTo("userId", user.uid)
    }

    ordersListener = ordersQuery.addSnapshotListener { snapshot, error ->
      if (error != null) {
        handleFirestoreError(error, OperationType.LIST, "orders")
        return@addSnapshotListener
      }
      if (snapshot != null && !snapshot.isEmpty) {
        val list = snapshot.documents.mapNotNull { doc ->
          try {
            val itemsList = (doc.get("items") as? List<*>)?.mapNotNull { itemMap ->
              val m = itemMap as? Map<*, *> ?: return@mapNotNull null
              OrderItem(
                productId = m["productId"]?.toString() ?: "",
                name = m["name"]?.toString() ?: "",
                quantity = (m["quantity"] as? Number)?.toInt() ?: 1,
                price = (m["price"] as? Number)?.toInt() ?: 0,
                color = m["color"]?.toString() ?: "",
                size = m["size"]?.toString() ?: "",
                ageGroup = m["ageGroup"]?.toString() ?: ""
              )
            } ?: emptyList()

            Order(
              id = doc.getString("id") ?: doc.id,
              customerName = doc.getString("customer") ?: doc.getString("customerName") ?: "",
              phone = doc.getString("phone") ?: "",
              district = doc.getString("district") ?: "ঢাকা (Dhaka)",
              thana = doc.getString("thana") ?: "সদর",
              address = doc.getString("address") ?: "",
              items = itemsList,
              subtotal = (doc.getLong("subtotal") ?: 0L).toInt(),
              deliveryCharge = (doc.getLong("deliveryCharge") ?: 0L).toInt(),
              discount = (doc.getLong("discount") ?: 0L).toInt(),
              couponCode = doc.getString("couponCode") ?: "",
              total = (doc.getLong("total") ?: 0L).toInt(),
              paymentMethod = doc.getString("paymentMethod") ?: "Cash on Delivery",
              status = doc.getString("status") ?: "Pending",
              date = doc.getString("date") ?: "",
              trackingId = doc.getString("trackingId") ?: "",
              stockRestockedOnCancel = doc.getBoolean("stockRestockedOnCancel") ?: false,
              orderType = doc.getString("orderType") ?: "Normal",
              deliveryType = doc.getString("deliveryType") ?: "Standard",
              deliveredBy = doc.getString("deliveredBy") ?: "",
              landmark = doc.getString("landmark") ?: "",
              addressCategory = doc.getString("addressCategory") ?: "Home",
              defaultShippingAddress = doc.getBoolean("defaultShippingAddress") ?: false,
              createdAt = doc.getTimestamp("createdAt")?.toDate()?.time
                ?: doc.getLong("createdAtMs")
                ?: 0L
            )
          } catch (_: Exception) {
            null
          }
        }
        if (list.isNotEmpty()) {
          _orders.value = list
        }
      }
    }

    if (isAdmin) {
      returnsListener = db.collection("returns").addSnapshotListener { snapshot, error ->
        if (error != null) {
          handleFirestoreError(error, OperationType.LIST, "returns")
          return@addSnapshotListener
        }
        if (snapshot != null && !snapshot.isEmpty) {
          val list = snapshot.documents.mapNotNull { doc ->
            try {
              val retItems = (doc.get("returnedItems") as? List<*>)?.mapNotNull { itemMap ->
                val m = itemMap as? Map<*, *> ?: return@mapNotNull null
                ReturnItem(
                  productId = m["productId"]?.toString() ?: "",
                  name = m["name"]?.toString() ?: "",
                  price = (m["price"] as? Number)?.toInt() ?: 0,
                  quantity = (m["qty"] as? Number)?.toInt() ?: (m["quantity"] as? Number)?.toInt() ?: 1,
                  condition = m["condition"]?.toString() ?: "ভালো (বিক্রিযোগ্য)",
                  restock = (m["restock"] as? Boolean) ?: true,
                  color = m["color"]?.toString() ?: "",
                  size = m["size"]?.toString() ?: ""
                )
              } ?: emptyList()

              val exItems = (doc.get("exchangeItems") as? List<*>)?.mapNotNull { itemMap ->
                val m = itemMap as? Map<*, *> ?: return@mapNotNull null
                ExchangeItem(
                  productId = m["productId"]?.toString() ?: "",
                  name = m["name"]?.toString() ?: "",
                  price = (m["price"] as? Number)?.toInt() ?: 0,
                  quantity = (m["qty"] as? Number)?.toInt() ?: (m["quantity"] as? Number)?.toInt() ?: 1,
                  color = m["color"]?.toString() ?: "",
                  size = m["size"]?.toString() ?: "",
                  sku = m["sku"]?.toString() ?: ""
                )
              } ?: emptyList()

              ReturnRecord(
                id = doc.getString("id") ?: doc.id,
                orderId = doc.getString("orderId") ?: "",
                customerName = doc.getString("customerName") ?: doc.getString("customer") ?: "",
                phone = doc.getString("phone") ?: "",
                date = doc.getString("date") ?: "",
                returnedItems = retItems,
                totalReturnValue = (doc.getLong("totalReturnValue") ?: 0L).toInt(),
                isExchange = doc.getBoolean("isExchange") ?: exItems.isNotEmpty(),
                exchangeItems = exItems,
                totalExchangeValue = (doc.getLong("totalExchangeValue") ?: 0L).toInt(),
                deliveryFee = (doc.getLong("deliveryFee") ?: doc.getLong("exchangeDeliveryFee") ?: 0L).toInt(),
                priceDifference = (doc.getLong("priceDifference") ?: 0L).toInt(),
                differenceType = doc.getString("differenceType") ?: "even_exchange",
                settlementMethod = doc.getString("settlementMethod") ?: "Cash",
                settlementTrxId = doc.getString("settlementTrxId") ?: "",
                settlementStatus = doc.getString("settlementStatus") ?: "paid",
                reason = doc.getString("reason") ?: "পণ্য পরিবর্তন / অপছন্দ",
                note = doc.getString("note") ?: ""
              )
            } catch (_: Exception) {
              null
            }
          }
          if (list.isNotEmpty()) {
            _returns.value = list
          }
        }
      }

      inventorySyncService.startListening(
        getCurrentProducts = { _products.value },
        onRemotePosUpdateProducts = { updatedProducts ->
          _products.value = updatedProducts
          persistLocalSyncSnapshot()
        }
      )

      smsQueueListener = db.collection("sms_queue").addSnapshotListener { snapshot, error ->
        if (error != null) {
          handleFirestoreError(error, OperationType.LIST, "sms_queue")
          return@addSnapshotListener
        }
        if (snapshot != null) {
          val remoteItems = snapshot.documents.mapNotNull { doc ->
            try {
              SmsQueueItem(
                id = doc.getString("id") ?: doc.id,
                phone = doc.getString("phone") ?: return@mapNotNull null,
                customerName = doc.getString("customerName") ?: "",
                message = doc.getString("message") ?: return@mapNotNull null,
                orderId = doc.getString("orderId") ?: "",
                type = doc.getString("type") ?: "custom",
                status = doc.getString("status") ?: "pending",
                channel = doc.getString("channel") ?: "sim_gateway",
                createdAt = (doc.getLong("createdAt") ?: System.currentTimeMillis()),
                updatedAt = (doc.getLong("updatedAt") ?: System.currentTimeMillis()),
                sentAt = (doc.getLong("sentAt") ?: 0L),
                sentByDevice = doc.getString("sentByDevice") ?: "",
                errorReason = doc.getString("errorReason") ?: ""
              )
            } catch (_: Exception) {
              null
            }
          }
          val localOnly = _smsQueue.value.filter { local -> remoteItems.none { it.id == local.id } }
          val merged = (remoteItems + localOnly).sortedByDescending { it.createdAt }
          _smsQueue.value = merged
          saveSmsQueueToPrefs(merged)
        }
      }
    } else {
      inventorySyncService.stopListening()
    }
  }

  private fun loadProducts(): List<Product> {
    return localStorageSync.loadAndReconcile(initialProducts)
  }

  // Cart Operations
  fun addToCart(
    product: Product,
    quantity: Int = 1,
    color: String = "",
    size: String = "",
    ageGroup: String = ""
  ): Boolean {
    if (product.stock <= 0) return false
    val current = _cart.value.toMutableList()
    val targetKey = "${product.id}_${color}_${size}_${ageGroup}"
    val index = current.indexOfFirst { it.itemKey == targetKey }
    if (index >= 0) {
      val existing = current[index]
      val newQty = (existing.quantity + quantity).coerceAtMost(product.stock)
      current[index] = existing.copy(quantity = newQty)
    } else {
      current.add(
        CartItem(
          product = product,
          quantity = quantity.coerceAtMost(product.stock),
          selectedColor = color,
          selectedSize = size,
          selectedAgeGroup = ageGroup
        )
      )
    }
    _cart.value = current
    return true
  }

  fun addComboToCart(combo: ComboPack): Boolean {
    if (!combo.isActive || combo.stock <= 0) return false
    val comboProduct = Product(
      id = combo.id,
      name = "🎁 ${combo.title}",
      price = combo.comboPrice,
      stock = combo.stock,
      category = "কম্বো প্যাক",
      imageUrl = combo.imageUrl.ifBlank { "res://banner_eid_special_1792530116809" },
      sku = combo.id.uppercase(),
      description = combo.description.ifBlank { combo.subtitle },
      rating = 5.0f,
      reviewsCount = 28
    )
    return addToCart(
      product = comboProduct,
      quantity = 1,
      color = "কম্বো সেট",
      size = "${combo.productIds.size}টি পণ্য",
      ageGroup = ""
    )
  }

  fun updateCartQuantity(itemKey: String, delta: Int) {
    val current = _cart.value.toMutableList()
    val index = current.indexOfFirst { it.itemKey == itemKey }
    if (index >= 0) {
      val item = current[index]
      val newQty = item.quantity + delta
      if (newQty <= 0) {
        current.removeAt(index)
      } else {
        val maxStock = item.product.stock
        current[index] = item.copy(quantity = newQty.coerceAtMost(maxStock))
      }
      _cart.value = current
    }
  }

  fun removeFromCart(itemKey: String) {
    _cart.value = _cart.value.filterNot { it.itemKey == itemKey }
  }

  fun clearCart() {
    _cart.value = emptyList()
  }

  // Order Placement
  fun placeOrder(
    customerName: String,
    phone: String,
    district: String,
    thana: String,
    address: String,
    paymentMethod: String,
    appliedCoupon: Coupon?,
    orderType: String = "Normal",
    deliveryType: String = "Standard",
    landmark: String = "",
    addressCategory: String = "Home",
    defaultShippingAddress: Boolean = false
  ): Order? {
    val cartItems = _cart.value
    if (cartItems.isEmpty()) return null

    val subtotal = cartItems.sumOf { it.product.price * it.quantity }
    val resolvedDeliveryType = if (deliveryType.equals("Express", ignoreCase = true) &&
      BangladeshLocations.isChandpurDistrict(district)
    ) {
      "Express"
    } else {
      "Standard"
    }
    val deliveryCharge = BangladeshLocations.calculateShippingFee(
      subtotal = subtotal,
      district = district,
      deliveryType = resolvedDeliveryType,
      shippingInfo = _storeConfig.value.shippingInfo
    )

    var discount = 0
    if (appliedCoupon != null) {
      discount = if (appliedCoupon.type == "%") {
        (subtotal * appliedCoupon.value) / 100
      } else {
        appliedCoupon.value
      }
    }

    val total = (subtotal - discount).coerceAtLeast(0) + deliveryCharge
    val orderId = "ORD-${Random.nextInt(1000, 9999)}"
    val nowMs = System.currentTimeMillis()
    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(nowMs))
    val safeOrderType = if (orderType.equals("Custom", ignoreCase = true)) "Custom" else "Normal"
    val safeCategory = if (addressCategory.equals("Office", ignoreCase = true)) "Office" else "Home"

    val newOrder = Order(
      id = orderId,
      customerName = customerName,
      phone = phone,
      district = district,
      thana = thana.ifBlank { "সদর" },
      address = address,
      items = cartItems.map {
        OrderItem(
          productId = it.product.id,
          name = it.product.name,
          quantity = it.quantity,
          price = it.product.price,
          color = it.selectedColor,
          size = it.selectedSize,
          ageGroup = it.selectedAgeGroup
        )
      },
      subtotal = subtotal,
      deliveryCharge = deliveryCharge,
      discount = discount,
      couponCode = appliedCoupon?.code ?: "",
      total = total,
      paymentMethod = paymentMethod,
      status = "Pending",
      date = currentDate,
      trackingId = "",
      orderType = safeOrderType,
      deliveryType = resolvedDeliveryType,
      deliveredBy = "",
      landmark = landmark.trim(),
      addressCategory = safeCategory,
      defaultShippingAddress = defaultShippingAddress,
      createdAt = nowMs
    )

    // Deduct stock across Storefront + POS Inventory + Firestore in real time
    val updatedProducts = inventorySyncService.applySaleStockDeduction(
      products = _products.value,
      items = newOrder.items,
      orderId = newOrder.id
    )
    _products.value = updatedProducts
    persistLocalSyncSnapshot()

    // Deduct stock on any combo packs included in the order
    val comboDeductions = newOrder.items.filter { it.productId.startsWith("combo-") }
    if (comboDeductions.isNotEmpty()) {
      _combos.value = _combos.value.map { combo ->
        val qty = comboDeductions.filter { it.productId == combo.id }.sumOf { it.quantity }
        if (qty > 0) combo.copy(stock = (combo.stock - qty).coerceAtLeast(0)) else combo
      }
    }

    // Add to orders
    _orders.value = listOf(newOrder) + _orders.value

    try {
      val uid = currentAuthUid()
      if (uid != null) {
        val itemsMaps = newOrder.items.map {
          mapOf(
            "productId" to it.productId,
            "name" to it.name,
            "quantity" to it.quantity,
            "price" to it.price,
            "color" to it.color,
            "size" to it.size,
            "ageGroup" to it.ageGroup
          )
        }

        val orderPath = "orders/${newOrder.id}"
        firestore?.collection("orders")?.document(newOrder.id)?.set(
          mapOf(
            "userId" to uid,
            "customer" to newOrder.customerName,
            "customerName" to newOrder.customerName,
            "phone" to newOrder.phone,
            "district" to newOrder.district,
            "thana" to newOrder.thana,
            "address" to newOrder.address,
            "landmark" to newOrder.landmark,
            "addressCategory" to newOrder.addressCategory,
            "defaultShippingAddress" to newOrder.defaultShippingAddress,
            "orderType" to newOrder.orderType,
            "deliveryType" to newOrder.deliveryType,
            "deliveredBy" to newOrder.deliveredBy,
            "items" to itemsMaps,
            "subtotal" to newOrder.subtotal,
            "deliveryCharge" to newOrder.deliveryCharge,
            "discount" to newOrder.discount,
            "couponCode" to newOrder.couponCode,
            "total" to newOrder.total,
            "paymentMethod" to newOrder.paymentMethod,
            "status" to newOrder.status,
            "date" to newOrder.date,
            "trackingId" to newOrder.trackingId,
            "stockRestockedOnCancel" to false,
            "createdAtMs" to nowMs,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
          )
        )?.addOnFailureListener { e ->
          handleFirestoreError(e, OperationType.CREATE, orderPath)
        }
      }
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, "orders/${newOrder.id}")
    }

    // Clear cart
    clearCart()

    return newOrder
  }

  /**
   * Completes an in-store POS sale and synchronizes stock deductions bidirectionally
   * to the Storefront `products` collection, `pos_inventory`, and `inventory_sync_events`.
   */
  fun recordPosSale(
    items: List<OrderItem>,
    customerName: String = "কাউন্টার ক্রেতা (POS)",
    phone: String = "N/A",
    paymentMethod: String = "POS Cash",
    discount: Int = 0
  ): Order? {
    if (items.isEmpty()) return null
    val saleId = "POS-${Random.nextInt(100000, 999999)}"
    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val subtotal = items.sumOf { it.price * it.quantity }
    val total = (subtotal - discount).coerceAtLeast(0)

    val posOrder = Order(
      id = saleId,
      customerName = customerName.ifBlank { "কাউন্টার ক্রেতা (POS)" },
      phone = phone.ifBlank { "N/A" },
      district = "ঢাকা",
      thana = "শোরুম",
      address = "শোরুম POS কাউন্টার (ইন-স্টোর সেলস)",
      items = items,
      subtotal = subtotal,
      deliveryCharge = 0,
      discount = discount,
      couponCode = "",
      total = total,
      paymentMethod = paymentMethod,
      status = "Delivered",
      date = currentDate,
      trackingId = ""
    )

    val updatedProducts = inventorySyncService.applyPosSaleDeduction(
      products = _products.value,
      items = items,
      saleId = saleId
    )
    _products.value = updatedProducts
    persistLocalSyncSnapshot()
    _orders.value = listOf(posOrder) + _orders.value

    try {
      val uid = currentAuthUid()
      if (uid != null && isVerifiedAdmin()) {
        val itemsMaps = items.map {
          mapOf(
            "productId" to it.productId,
            "name" to it.name,
            "quantity" to it.quantity,
            "price" to it.price,
            "color" to it.color,
            "size" to it.size,
            "ageGroup" to it.ageGroup
          )
        }
        firestore?.collection("orders")?.document(posOrder.id)?.set(
          mapOf(
            "userId" to uid,
            "customer" to posOrder.customerName,
            "customerName" to posOrder.customerName,
            "phone" to posOrder.phone,
            "district" to posOrder.district,
            "thana" to posOrder.thana,
            "address" to posOrder.address,
            "items" to itemsMaps,
            "subtotal" to posOrder.subtotal,
            "deliveryCharge" to 0,
            "discount" to posOrder.discount,
            "couponCode" to "",
            "total" to posOrder.total,
            "paymentMethod" to posOrder.paymentMethod,
            "status" to posOrder.status,
            "date" to posOrder.date,
            "trackingId" to "",
            "stockRestockedOnCancel" to false,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
          )
        )?.addOnSuccessListener {
          inventorySyncService.markFirestoreSync(products = _products.value)
        }?.addOnFailureListener { e ->
          handleFirestoreError(e, OperationType.CREATE, "orders/${posOrder.id}")
          inventorySyncService.recordSyncError(
            source = "FIRESTORE_POS_ORDER_WRITE",
            severity = "ERROR",
            code = e.javaClass.simpleName,
            message = e.localizedMessage ?: "POS অর্ডার Firestore-এ সেভ করতে ব্যর্থ",
            details = "collection=orders doc=${posOrder.id}",
            products = _products.value
          )
        }
      }
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, "orders/${posOrder.id}")
      inventorySyncService.recordSyncError(
        source = "FIRESTORE_POS_ORDER_WRITE",
        severity = "ERROR",
        code = e.javaClass.simpleName,
        message = e.localizedMessage ?: "POS অর্ডার Firestore-এ লিখতে ব্যতিক্রম",
        details = "collection=orders doc=${posOrder.id}",
        products = _products.value
      )
    }

    return posOrder
  }

  /**
   * Simulates or applies an external POS terminal stock change and syncs bidirectionally.
   */
  fun applyRemotePosStockUpdate(
    productId: String,
    newStock: Int,
    source: String = "POS_TERMINAL",
    referenceId: String = "POS-SYNC"
  ) {
    val target = _products.value.find { it.id == productId } ?: return
    val delta = newStock.coerceAtLeast(0) - target.stock
    if (delta == 0) return
    _products.value = inventorySyncService.applyStockDeltaBatch(
      products = _products.value,
      deltas = listOf(
        com.example.data.sync.StockDeltaRequest(
          productId = productId,
          productName = target.name,
          sku = target.sku,
          delta = delta,
          reason = "POS সিঙ্ক আপডেট ($referenceId)"
        )
      ),
      source = source,
      reason = "POS টার্মিনাল স্টক আপডেট",
      referenceId = referenceId
    )
    persistLocalSyncSnapshot()
  }

  fun reconcileInventoryNow(): Int {
    val (reconciledProducts, count) = inventorySyncService.reconcileStorefrontAndPos(_products.value)
    _products.value = reconciledProducts
    persistLocalSyncSnapshot()
    return count
  }

  fun clearSyncErrorLogs() {
    inventorySyncService.clearSyncErrorLogs(_products.value)
  }

  // Order Management
  fun updateOrderStatus(orderId: String, newStatus: String) {
    val targetOrder = _orders.value.find { it.id == orderId }
    var nextRestockedFlag = targetOrder?.stockRestockedOnCancel ?: false

    if (targetOrder != null) {
      if (newStatus == "Cancelled" && targetOrder.status != "Cancelled" && !targetOrder.stockRestockedOnCancel) {
        _products.value = inventorySyncService.applyOrderCancellationRestock(_products.value, targetOrder, restoreStock = true)
        nextRestockedFlag = true
        persistLocalSyncSnapshot()
      } else if (targetOrder.status == "Cancelled" && newStatus != "Cancelled" && targetOrder.stockRestockedOnCancel) {
        _products.value = inventorySyncService.applyOrderCancellationRestock(_products.value, targetOrder, restoreStock = false)
        nextRestockedFlag = false
        persistLocalSyncSnapshot()
      }
    }

    _orders.value = _orders.value.map {
      if (it.id == orderId) it.copy(status = newStatus, stockRestockedOnCancel = nextRestockedFlag) else it
    }
    if (isVerifiedAdmin()) {
      try {
        firestore?.collection("orders")?.document(orderId)?.update(
          mapOf(
            "status" to newStatus,
            "stockRestockedOnCancel" to nextRestockedFlag,
            "updatedAt" to FieldValue.serverTimestamp()
          )
        )?.addOnFailureListener { e ->
          handleFirestoreError(e, OperationType.UPDATE, "orders/$orderId")
        }
      } catch (e: Exception) {
        handleFirestoreError(e, OperationType.UPDATE, "orders/$orderId")
      }
    }
    if (_storeConfig.value.smsAutoSendOnOrderStatus) {
      val updatedOrder = _orders.value.find { it.id == orderId }
      if (updatedOrder != null && SmsHelper.isValidPhone(updatedOrder.phone)) {
        enqueueSms(
          phone = updatedOrder.phone,
          customerName = updatedOrder.customerName,
          message = SmsHelper.buildOrderStatusSms(updatedOrder, _storeConfig.value.storeName),
          orderId = updatedOrder.id,
          type = "status_update",
          channel = "sim_gateway"
        )
      }
    }
  }

  fun dispatchToCourier(orderId: String, provider: String): String {
    val cleanProvider = when {
      provider.contains("Pathao", true) -> "Pathao"
      provider.contains("Stead", true) -> "SteadFast"
      provider.contains("Digital", true) -> "Digital"
      else -> provider.ifBlank { "SteadFast" }
    }
    val prefix = when (cleanProvider) {
      "Pathao" -> "PTH-"
      "Digital" -> "DGT-"
      else -> "ST-"
    }
    val tracking = prefix + Random.nextInt(100000, 999999)
    _orders.value = _orders.value.map {
      if (it.id == orderId) it.copy(status = "Shipped", trackingId = tracking, deliveredBy = cleanProvider) else it
    }
    if (isVerifiedAdmin()) {
      try {
        firestore?.collection("orders")?.document(orderId)?.update(
          mapOf(
            "status" to "Shipped",
            "trackingId" to tracking,
            "deliveredBy" to cleanProvider,
            "updatedAt" to FieldValue.serverTimestamp()
          )
        )?.addOnFailureListener { e ->
          handleFirestoreError(e, OperationType.UPDATE, "orders/$orderId")
        }
      } catch (e: Exception) {
        handleFirestoreError(e, OperationType.UPDATE, "orders/$orderId")
      }
    }
    if (_storeConfig.value.smsAutoSendOnOrderStatus) {
      val updatedOrder = _orders.value.find { it.id == orderId }
      if (updatedOrder != null && SmsHelper.isValidPhone(updatedOrder.phone)) {
        enqueueSms(
          phone = updatedOrder.phone,
          customerName = updatedOrder.customerName,
          message = SmsHelper.buildOrderStatusSms(updatedOrder, _storeConfig.value.storeName),
          orderId = updatedOrder.id,
          type = "status_update",
          channel = "sim_gateway"
        )
      }
    }
    return tracking
  }

  fun updateOrderDeliveryPartner(orderId: String, deliveredBy: String) {
    _orders.value = _orders.value.map {
      if (it.id == orderId) it.copy(deliveredBy = deliveredBy) else it
    }
    if (isVerifiedAdmin()) {
      try {
        firestore?.collection("orders")?.document(orderId)?.update(
          mapOf(
            "deliveredBy" to deliveredBy,
            "updatedAt" to FieldValue.serverTimestamp()
          )
        )?.addOnFailureListener { e ->
          handleFirestoreError(e, OperationType.UPDATE, "orders/$orderId")
        }
      } catch (e: Exception) {
        handleFirestoreError(e, OperationType.UPDATE, "orders/$orderId")
      }
    }
  }

  // Returns and Exchanges
  fun addReturnRecord(record: ReturnRecord) {
    _returns.value = listOf(record) + _returns.value

    // Synchronize return restocks & exchange deductions across Storefront + POS Inventory
    _products.value = inventorySyncService.applyReturnAndExchange(_products.value, record)
    persistLocalSyncSnapshot()

    val uid = currentAuthUid()
    if (uid != null && isVerifiedAdmin()) {
      try {
        val returnedMaps = record.returnedItems.map {
          mapOf(
            "productId" to it.productId,
            "name" to it.name,
            "price" to it.price,
            "qty" to it.quantity,
            "condition" to it.condition,
            "restock" to it.restock,
            "color" to it.color,
            "size" to it.size
          )
        }
        val exchangeMaps = record.exchangeItems.map {
          mapOf(
            "productId" to it.productId,
            "name" to it.name,
            "price" to it.price,
            "qty" to it.quantity,
            "color" to it.color,
            "size" to it.size,
            "sku" to it.sku
          )
        }
        firestore?.collection("returns")?.document(record.id)?.set(
          mapOf(
            "userId" to uid,
            "orderId" to record.orderId,
            "customer" to record.customerName,
            "customerName" to record.customerName,
            "phone" to record.phone,
            "date" to record.date,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp(),
            "returnedItems" to returnedMaps,
            "isExchange" to record.isExchange,
            "exchangeItems" to exchangeMaps,
            "totalReturnValue" to record.totalReturnValue,
            "totalExchangeValue" to record.totalExchangeValue,
            "deliveryFee" to record.deliveryFee,
            "priceDifference" to record.priceDifference,
            "differenceType" to record.differenceType,
            "settlementMethod" to record.settlementMethod,
            "settlementTrxId" to record.settlementTrxId,
            "settlementStatus" to record.settlementStatus,
            "reason" to record.reason,
            "note" to record.note
          )
        )?.addOnFailureListener { e ->
          handleFirestoreError(e, OperationType.CREATE, "returns/${record.id}")
        }
      } catch (e: Exception) {
        handleFirestoreError(e, OperationType.CREATE, "returns/${record.id}")
      }
    }
  }

  // Product CRUD
  fun addProduct(product: Product) {
    val ts = System.currentTimeMillis()
    val stamped = product.copy(
      stockUpdatedAt = ts,
      lastSyncSource = "ADMIN_ADD",
      createdAt = if (product.createdAt > 0L) product.createdAt else ts
    )
    _products.value = listOf(stamped) + _products.value
    syncProductFlagsToHomeSections(stamped)
    inventorySyncService.mirrorCatalogToPos(_products.value, source = "ADMIN_ADD", pushToCloud = true)
    persistLocalSyncSnapshot()
    if (isVerifiedAdmin()) {
      try {
        firestore?.collection("products")?.document(stamped.id)?.set(
          mapOf(
            "name" to stamped.name,
            "price" to stamped.price,
            "stock" to stamped.stock,
            "category" to stamped.category,
            "imageUrl" to stamped.imageUrl,
            "sku" to stamped.sku,
            "description" to stamped.description,
            "colors" to stamped.colors,
            "sizes" to stamped.sizes,
            "ageGroups" to stamped.ageGroups,
            "stockUpdatedAt" to stamped.stockUpdatedAt,
            "lastSyncSource" to stamped.lastSyncSource,
            "isHotSale" to stamped.isHotSale,
            "isBestDeal" to stamped.isBestDeal,
            "isB2G1" to stamped.isB2G1,
            "createdAt" to stamped.createdAt,
            "regularPrice" to stamped.regularPrice,
            "offerPrice" to stamped.offerPrice,
            "videoEnabled" to stamped.videoEnabled,
            "videoType" to stamped.videoType,
            "videoUrl" to stamped.videoUrl,
            "videoThumbUrl" to stamped.videoThumbUrl,
            "additionalImages" to stamped.additionalImages,
            "updatedAt" to FieldValue.serverTimestamp()
          )
        )?.addOnFailureListener { e ->
          handleFirestoreError(e, OperationType.CREATE, "products/${stamped.id}")
        }
      } catch (e: Exception) {
        handleFirestoreError(e, OperationType.CREATE, "products/${stamped.id}")
      }
    }
  }

  fun updateProduct(product: Product) {
    val existing = _products.value.find { it.id == product.id }
    val ts = System.currentTimeMillis()
    val stamped = product.copy(
      stockUpdatedAt = ts,
      lastSyncSource = "ADMIN_EDIT",
      createdAt = if (product.createdAt > 0L) product.createdAt else (existing?.createdAt ?: ts)
    )
    if (existing != null && existing.stock != stamped.stock) {
      val delta = stamped.stock - existing.stock
      val updatedWithoutStock = _products.value.map { if (it.id == stamped.id) stamped.copy(stock = existing.stock) else it }
      _products.value = inventorySyncService.applyStockDeltaBatch(
        products = updatedWithoutStock,
        deltas = listOf(
          com.example.data.sync.StockDeltaRequest(
            productId = stamped.id,
            productName = stamped.name,
            sku = stamped.sku,
            delta = delta,
            reason = "অ্যাডমিন ইনভেন্টরি স্টক আপডেট"
          )
        ),
        source = "ADMIN_EDIT",
        reason = "অ্যাডমিন প্রোডাক্ট আপডেট",
        referenceId = stamped.id
      )
      persistLocalSyncSnapshot()
    } else {
      _products.value = _products.value.map { if (it.id == stamped.id) stamped else it }
      inventorySyncService.mirrorCatalogToPos(_products.value, source = "ADMIN_EDIT", pushToCloud = true)
      persistLocalSyncSnapshot()
    }
    syncProductFlagsToHomeSections(stamped)
    if (isVerifiedAdmin()) {
      try {
        firestore?.collection("products")?.document(stamped.id)?.set(
          mapOf(
            "name" to stamped.name,
            "price" to stamped.price,
            "stock" to stamped.stock,
            "category" to stamped.category,
            "imageUrl" to stamped.imageUrl,
            "sku" to stamped.sku,
            "description" to stamped.description,
            "colors" to stamped.colors,
            "sizes" to stamped.sizes,
            "ageGroups" to stamped.ageGroups,
            "stockUpdatedAt" to stamped.stockUpdatedAt,
            "lastSyncSource" to stamped.lastSyncSource,
            "isHotSale" to stamped.isHotSale,
            "isBestDeal" to stamped.isBestDeal,
            "isB2G1" to stamped.isB2G1,
            "createdAt" to stamped.createdAt,
            "regularPrice" to stamped.regularPrice,
            "offerPrice" to stamped.offerPrice,
            "videoEnabled" to stamped.videoEnabled,
            "videoType" to stamped.videoType,
            "videoUrl" to stamped.videoUrl,
            "videoThumbUrl" to stamped.videoThumbUrl,
            "additionalImages" to stamped.additionalImages,
            "updatedAt" to FieldValue.serverTimestamp()
          ),
          SetOptions.merge()
        )?.addOnFailureListener { e ->
          handleFirestoreError(e, OperationType.UPDATE, "products/${stamped.id}")
        }
      } catch (e: Exception) {
        handleFirestoreError(e, OperationType.UPDATE, "products/${stamped.id}")
      }
    }
  }

  fun deleteProduct(productId: String) {
    _products.value = _products.value.filterNot { it.id == productId }
    inventorySyncService.removeProductFromPos(productId)
    persistLocalSyncSnapshot()
    if (isVerifiedAdmin()) {
      try {
        firestore?.collection("products")?.document(productId)?.delete()
          ?.addOnFailureListener { e ->
            handleFirestoreError(e, OperationType.DELETE, "products/$productId")
          }
      } catch (e: Exception) {
        handleFirestoreError(e, OperationType.DELETE, "products/$productId")
      }
    }
  }

  // Category CRUD
  fun addCategory(category: Category) {
    val existingIndex = _categories.value.indexOfFirst { it.id == category.id }
    _categories.value = if (existingIndex >= 0) {
      _categories.value.toMutableList().apply { set(existingIndex, category) }
    } else {
      _categories.value + category
    }
  }

  fun deleteCategory(categoryId: String) {
    _categories.value = _categories.value.filterNot { it.id == categoryId }
  }

  // Coupon CRUD
  fun addCoupon(coupon: Coupon) {
    _coupons.value = listOf(coupon) + _coupons.value
  }

  fun toggleCoupon(couponId: String) {
    _coupons.value = _coupons.value.map {
      if (it.id == couponId) it.copy(isActive = !it.isActive) else it
    }
  }

  fun deleteCoupon(couponId: String) {
    _coupons.value = _coupons.value.filterNot { it.id == couponId }
  }

  // Banner CRUD
  fun addBanner(banner: BannerItem) {
    _banners.value = _banners.value + banner
  }

  fun toggleBanner(bannerId: String) {
    _banners.value = _banners.value.map {
      if (it.id == bannerId) it.copy(isActive = !it.isActive) else it
    }
  }

  fun deleteBanner(bannerId: String) {
    _banners.value = _banners.value.filterNot { it.id == bannerId }
  }

  // Store Configuration
  private fun loadStoreConfig(): StoreConfig {
    val raw = prefs.getString("sajgoj_store_config", null) ?: return StoreConfig()
    return try {
      val o = JSONObject(raw)
      val dObj = o.optJSONObject("design")
      val defDesign = StoreDesignConfig()
      val design = if (dObj != null) {
        StoreDesignConfig(
          useCustomPageBg = dObj.optBoolean("useCustomPageBg", defDesign.useCustomPageBg),
          pageBgStart = dObj.optString("pageBgStart", defDesign.pageBgStart),
          pageBgEnd = dObj.optString("pageBgEnd", defDesign.pageBgEnd),
          useCustomBlockBg = dObj.optBoolean("useCustomBlockBg", defDesign.useCustomBlockBg),
          blockBgColor = dObj.optString("blockBgColor", defDesign.blockBgColor),
          blockBorderColor = dObj.optString("blockBorderColor", defDesign.blockBorderColor),
          blockTitleBg = dObj.optString("blockTitleBg", defDesign.blockTitleBg),
          blockTitleText = dObj.optString("blockTitleText", defDesign.blockTitleText),
          seeMoreBg = dObj.optString("seeMoreBg", defDesign.seeMoreBg),
          seeMoreText = dObj.optString("seeMoreText", defDesign.seeMoreText),
          reelsCaptionBg = dObj.optString("reelsCaptionBg", defDesign.reelsCaptionBg),
          reelsCaptionText = dObj.optString("reelsCaptionText", defDesign.reelsCaptionText),
          reelsCaptionLabel = dObj.optString("reelsCaptionLabel", defDesign.reelsCaptionLabel),
          cardBodyBg = dObj.optString("cardBodyBg", defDesign.cardBodyBg),
          cardBodyBgEnd = dObj.optString("cardBodyBgEnd", defDesign.cardBodyBgEnd),
          cardBorderColor = dObj.optString("cardBorderColor", defDesign.cardBorderColor),
          cardTitleBg = dObj.optString("cardTitleBg", defDesign.cardTitleBg),
          cardTitleBgEnd = dObj.optString("cardTitleBgEnd", defDesign.cardTitleBgEnd),
          cardTitleText = dObj.optString("cardTitleText", defDesign.cardTitleText),
          cardPriceBg = dObj.optString("cardPriceBg", defDesign.cardPriceBg),
          cardPriceBorder = dObj.optString("cardPriceBorder", defDesign.cardPriceBorder),
          cardRegularPriceColor = dObj.optString("cardRegularPriceColor", defDesign.cardRegularPriceColor),
          cardOfferPriceColor = dObj.optString("cardOfferPriceColor", defDesign.cardOfferPriceColor),
          cardBtnStyle = dObj.optString("cardBtnStyle", defDesign.cardBtnStyle),
          cardBtnBg = dObj.optString("cardBtnBg", defDesign.cardBtnBg),
          cardBtnBgEnd = dObj.optString("cardBtnBgEnd", defDesign.cardBtnBgEnd),
          cardBtnText = dObj.optString("cardBtnText", defDesign.cardBtnText),
          cardActionLabel = dObj.optString("cardActionLabel", defDesign.cardActionLabel),
          cardWishlistCircleBg = dObj.optString("cardWishlistCircleBg", defDesign.cardWishlistCircleBg),
          cardWishlistActiveBg = dObj.optString("cardWishlistActiveBg", defDesign.cardWishlistActiveBg),
          cardWishlistIcon = dObj.optString("cardWishlistIcon", defDesign.cardWishlistIcon),
          useCustomHeaderColors = dObj.optBoolean("useCustomHeaderColors", defDesign.useCustomHeaderColors),
          announcementBg = dObj.optString("announcementBg", defDesign.announcementBg),
          announcementTextColor = dObj.optString("announcementTextColor", defDesign.announcementTextColor),
          headerBg = dObj.optString("headerBg", defDesign.headerBg),
          headerTextColor = dObj.optString("headerTextColor", defDesign.headerTextColor),
          cartBg = dObj.optString("cartBg", defDesign.cartBg),
          cartTextColor = dObj.optString("cartTextColor", defDesign.cartTextColor),
          cartBadgeBg = dObj.optString("cartBadgeBg", defDesign.cartBadgeBg),
          cartBadgeText = dObj.optString("cartBadgeText", defDesign.cartBadgeText),
          wishlistBg = dObj.optString("wishlistBg", defDesign.wishlistBg),
          wishlistTextColor = dObj.optString("wishlistTextColor", defDesign.wishlistTextColor)
        )
      } else {
        defDesign
      }
      val sObj = o.optJSONObject("shippingInfo")
      val defShip = ShippingInfoConfig()
      val shippingInfo = if (sObj != null) {
        val optArr = sObj.optJSONArray("options")
        val opts = if (optArr != null) {
          buildList {
            for (i in 0 until optArr.length()) {
              val itemObj = optArr.optJSONObject(i) ?: continue
              val customDistArr = itemObj.optJSONArray("customDistricts")
              val customDists = buildList {
                if (customDistArr != null) {
                  for (j in 0 until customDistArr.length()) {
                    val d = customDistArr.optString(j)
                    if (d.isNotBlank()) add(d)
                  }
                }
              }
              add(
                ShippingOptionItem(
                  id = itemObj.optString("id", "ship-$i"),
                  title = itemObj.optString("title", "Delivery Option"),
                  subtitle = itemObj.optString("subtitle", ""),
                  badge = itemObj.optString("badge", ""),
                  fee = itemObj.optInt("fee", 100),
                  freeAbove = itemObj.optInt("freeAbove", 0),
                  estimatedDays = itemObj.optString("estimatedDays", "২-৪ কার্যদিবস"),
                  deliveryType = itemObj.optString("deliveryType", "Standard"),
                  districtRule = itemObj.optString("districtRule", "ALL"),
                  customDistricts = customDists,
                  enabled = itemObj.optBoolean("enabled", true)
                )
              )
            }
          }
        } else {
          defShip.options
        }
        ShippingInfoConfig(
          sectionTitle = sObj.optString("sectionTitle", defShip.sectionTitle),
          chandpurExpressNotice = sObj.optString("chandpurExpressNotice", defShip.chandpurExpressNotice),
          sectionBgColor = sObj.optString("sectionBgColor", defShip.sectionBgColor),
          blockBgColor = sObj.optString("blockBgColor", defShip.blockBgColor),
          borderColor = sObj.optString("borderColor", defShip.borderColor),
          textColor = sObj.optString("textColor", defShip.textColor),
          buttonBgColor = sObj.optString("buttonBgColor", defShip.buttonBgColor),
          buttonTextColor = sObj.optString("buttonTextColor", defShip.buttonTextColor),
          options = if (opts.isNotEmpty()) opts else defShip.options
        )
      } else {
        defShip
      }
      val defCfg = StoreConfig()
      StoreConfig(
        storeName = o.optString("storeName", defCfg.storeName),
        tagline = o.optString("tagline", defCfg.tagline),
        announcementText = o.optString("announcementText", defCfg.announcementText),
        isAnnouncementEnabled = o.optBoolean("isAnnouncementEnabled", defCfg.isAnnouncementEnabled),
        phone = o.optString("phone", defCfg.phone),
        whatsapp = o.optString("whatsapp", defCfg.whatsapp),
        email = o.optString("email", defCfg.email),
        address = o.optString("address", defCfg.address),
        showGoogleMap = o.optBoolean("showGoogleMap", defCfg.showGoogleMap),
        googleMapQuery = o.optString("googleMapQuery", defCfg.googleMapQuery),
        googleMapUrl = o.optString("googleMapUrl", defCfg.googleMapUrl),
        bkashEnabled = o.optBoolean("bkashEnabled", defCfg.bkashEnabled),
        nagadEnabled = o.optBoolean("nagadEnabled", defCfg.nagadEnabled),
        rocketEnabled = o.optBoolean("rocketEnabled", defCfg.rocketEnabled),
        codEnabled = o.optBoolean("codEnabled", defCfg.codEnabled),
        visaEnabled = o.optBoolean("visaEnabled", defCfg.visaEnabled),
        returnPolicyDays = o.optInt("returnPolicyDays", defCfg.returnPolicyDays),
        smsGatewayDeviceEnabled = o.optBoolean("smsGatewayDeviceEnabled", defCfg.smsGatewayDeviceEnabled),
        smsAutoSendOnOrderStatus = o.optBoolean("smsAutoSendOnOrderStatus", defCfg.smsAutoSendOnOrderStatus),
        smsDelaySeconds = o.optInt("smsDelaySeconds", defCfg.smsDelaySeconds).coerceIn(2, 15),
        design = design,
        shippingInfo = shippingInfo
      )
    } catch (_: Exception) {
      StoreConfig()
    }
  }

  private fun saveStoreConfigToPrefs(config: StoreConfig) {
    val d = config.design
    val dObj = JSONObject().apply {
      put("useCustomPageBg", d.useCustomPageBg)
      put("pageBgStart", d.pageBgStart)
      put("pageBgEnd", d.pageBgEnd)
      put("useCustomBlockBg", d.useCustomBlockBg)
      put("blockBgColor", d.blockBgColor)
      put("blockBorderColor", d.blockBorderColor)
      put("blockTitleBg", d.blockTitleBg)
      put("blockTitleText", d.blockTitleText)
      put("seeMoreBg", d.seeMoreBg)
      put("seeMoreText", d.seeMoreText)
      put("reelsCaptionBg", d.reelsCaptionBg)
      put("reelsCaptionText", d.reelsCaptionText)
      put("reelsCaptionLabel", d.reelsCaptionLabel)
      put("cardBodyBg", d.cardBodyBg)
      put("cardBodyBgEnd", d.cardBodyBgEnd)
      put("cardBorderColor", d.cardBorderColor)
      put("cardTitleBg", d.cardTitleBg)
      put("cardTitleBgEnd", d.cardTitleBgEnd)
      put("cardTitleText", d.cardTitleText)
      put("cardPriceBg", d.cardPriceBg)
      put("cardPriceBorder", d.cardPriceBorder)
      put("cardRegularPriceColor", d.cardRegularPriceColor)
      put("cardOfferPriceColor", d.cardOfferPriceColor)
      put("cardBtnStyle", d.cardBtnStyle)
      put("cardBtnBg", d.cardBtnBg)
      put("cardBtnBgEnd", d.cardBtnBgEnd)
      put("cardBtnText", d.cardBtnText)
      put("cardActionLabel", d.cardActionLabel)
      put("cardWishlistCircleBg", d.cardWishlistCircleBg)
      put("cardWishlistActiveBg", d.cardWishlistActiveBg)
      put("cardWishlistIcon", d.cardWishlistIcon)
      put("useCustomHeaderColors", d.useCustomHeaderColors)
      put("announcementBg", d.announcementBg)
      put("announcementTextColor", d.announcementTextColor)
      put("headerBg", d.headerBg)
      put("headerTextColor", d.headerTextColor)
      put("cartBg", d.cartBg)
      put("cartTextColor", d.cartTextColor)
      put("cartBadgeBg", d.cartBadgeBg)
      put("cartBadgeText", d.cartBadgeText)
      put("wishlistBg", d.wishlistBg)
      put("wishlistTextColor", d.wishlistTextColor)
    }
    val s = config.shippingInfo
    val sObj = JSONObject().apply {
      put("sectionTitle", s.sectionTitle)
      put("chandpurExpressNotice", s.chandpurExpressNotice)
      put("sectionBgColor", s.sectionBgColor)
      put("blockBgColor", s.blockBgColor)
      put("borderColor", s.borderColor)
      put("textColor", s.textColor)
      put("buttonBgColor", s.buttonBgColor)
      put("buttonTextColor", s.buttonTextColor)
      val optArr = JSONArray()
      s.options.forEach { opt ->
        optArr.put(
          JSONObject().apply {
            put("id", opt.id)
            put("title", opt.title)
            put("subtitle", opt.subtitle)
            put("badge", opt.badge)
            put("fee", opt.fee)
            put("freeAbove", opt.freeAbove)
            put("estimatedDays", opt.estimatedDays)
            put("deliveryType", opt.deliveryType)
            put("districtRule", opt.districtRule)
            put("customDistricts", JSONArray(opt.customDistricts))
            put("enabled", opt.enabled)
          }
        )
      }
      put("options", optArr)
    }
    val root = JSONObject().apply {
      put("storeName", config.storeName)
      put("tagline", config.tagline)
      put("announcementText", config.announcementText)
      put("isAnnouncementEnabled", config.isAnnouncementEnabled)
      put("phone", config.phone)
      put("whatsapp", config.whatsapp)
      put("email", config.email)
      put("address", config.address)
      put("showGoogleMap", config.showGoogleMap)
      put("googleMapQuery", config.googleMapQuery)
      put("googleMapUrl", config.googleMapUrl)
      put("bkashEnabled", config.bkashEnabled)
      put("nagadEnabled", config.nagadEnabled)
      put("rocketEnabled", config.rocketEnabled)
      put("codEnabled", config.codEnabled)
      put("visaEnabled", config.visaEnabled)
      put("returnPolicyDays", config.returnPolicyDays)
      put("smsGatewayDeviceEnabled", config.smsGatewayDeviceEnabled)
      put("smsAutoSendOnOrderStatus", config.smsAutoSendOnOrderStatus)
      put("smsDelaySeconds", config.smsDelaySeconds.coerceIn(2, 15))
      put("design", dObj)
      put("shippingInfo", sObj)
    }
    prefs.edit().putString("sajgoj_store_config", root.toString()).apply()
  }

  fun updateStoreConfig(config: StoreConfig) {
    _storeConfig.value = config
    saveStoreConfigToPrefs(config)
  }

  // -------------------- SMS GATEWAY QUEUE MANAGEMENT --------------------

  private fun loadSmsQueueFromPrefs(): List<SmsQueueItem> {
    val raw = prefs.getString("sajgoj_sms_queue", null) ?: return emptyList()
    return try {
      val arr = JSONArray(raw)
      buildList {
        for (i in 0 until arr.length()) {
          val o = arr.optJSONObject(i) ?: continue
          val phone = o.optString("phone", "")
          val msg = o.optString("message", "")
          if (phone.isBlank() || msg.isBlank()) continue
          add(
            SmsQueueItem(
              id = o.optString("id", "SMS-$i"),
              phone = phone,
              customerName = o.optString("customerName", ""),
              message = msg,
              orderId = o.optString("orderId", ""),
              type = o.optString("type", "custom"),
              status = o.optString("status", "pending"),
              channel = o.optString("channel", "sim_gateway"),
              createdAt = o.optLong("createdAt", System.currentTimeMillis()),
              updatedAt = o.optLong("updatedAt", System.currentTimeMillis()),
              sentAt = o.optLong("sentAt", 0L),
              sentByDevice = o.optString("sentByDevice", ""),
              errorReason = o.optString("errorReason", "")
            )
          )
        }
      }.sortedByDescending { it.createdAt }
    } catch (_: Exception) {
      emptyList()
    }
  }

  private fun saveSmsQueueToPrefs(list: List<SmsQueueItem>) {
    val arr = JSONArray()
    list.take(200).forEach { s ->
      arr.put(
        JSONObject().apply {
          put("id", s.id)
          put("phone", s.phone)
          put("customerName", s.customerName)
          put("message", s.message)
          put("orderId", s.orderId)
          put("type", s.type)
          put("status", s.status)
          put("channel", s.channel)
          put("createdAt", s.createdAt)
          put("updatedAt", s.updatedAt)
          put("sentAt", s.sentAt)
          put("sentByDevice", s.sentByDevice)
          put("errorReason", s.errorReason)
        }
      )
    }
    prefs.edit().putString("sajgoj_sms_queue", arr.toString()).apply()
  }

  fun enqueueSms(
    phone: String,
    customerName: String = "",
    message: String,
    orderId: String = "",
    type: String = "custom",
    channel: String = "sim_gateway"
  ): SmsQueueItem? {
    val normalizedPhone = SmsHelper.normalizePhone(phone)
    val cleanMsg = message.trim().take(1000)
    if (!SmsHelper.isValidPhone(normalizedPhone) || cleanMsg.isEmpty()) return null
    val now = System.currentTimeMillis()
    val safeType = if (type in setOf("order_alert", "status_update", "bulk_promo", "custom")) type else "custom"
    val safeChannel = if (channel in setOf("sim_gateway", "intent_manual")) channel else "sim_gateway"
    val item = SmsQueueItem(
      id = "SMS-${now}-${Random.nextInt(100, 999)}",
      phone = normalizedPhone.take(30),
      customerName = customerName.trim().take(120),
      message = cleanMsg,
      orderId = orderId.trim().take(128),
      type = safeType,
      status = "pending",
      channel = safeChannel,
      createdAt = now,
      updatedAt = now,
      sentAt = 0L,
      sentByDevice = "",
      errorReason = ""
    )
    val next = (listOf(item) + _smsQueue.value).sortedByDescending { it.createdAt }
    _smsQueue.value = next
    saveSmsQueueToPrefs(next)

    if (isVerifiedAdmin()) {
      try {
        firestore?.collection("sms_queue")?.document(item.id)?.set(
          mapOf(
            "id" to item.id,
            "phone" to item.phone,
            "customerName" to item.customerName,
            "message" to item.message,
            "orderId" to item.orderId,
            "type" to item.type,
            "status" to item.status,
            "channel" to item.channel,
            "createdAt" to item.createdAt,
            "updatedAt" to item.updatedAt,
            "sentAt" to item.sentAt,
            "sentByDevice" to item.sentByDevice,
            "errorReason" to item.errorReason
          )
        )?.addOnFailureListener { e ->
          handleFirestoreError(e, OperationType.CREATE, "sms_queue/${item.id}")
        }
      } catch (e: Exception) {
        handleFirestoreError(e, OperationType.CREATE, "sms_queue/${item.id}")
      }
    }
    return item
  }

  fun enqueueBulkSms(
    recipients: List<Pair<String, String>>,
    messageTemplate: String,
    type: String = "bulk_promo"
  ): Int {
    val storeName = _storeConfig.value.storeName
    val uniqueRecipients = recipients
      .map { (name, phone) -> name.trim() to SmsHelper.normalizePhone(phone) }
      .filter { (_, phone) -> SmsHelper.isValidPhone(phone) }
      .distinctBy { (_, phone) -> phone }
    var count = 0
    uniqueRecipients.forEach { (name, phone) ->
      val body = SmsHelper.formatTemplate(
        template = messageTemplate,
        customerName = name,
        storeName = storeName
      )
      if (enqueueSms(
          phone = phone,
          customerName = name,
          message = body,
          orderId = "",
          type = type,
          channel = "sim_gateway"
        ) != null
      ) {
        count++
      }
    }
    return count
  }

  fun updateSmsQueueStatus(
    smsId: String,
    status: String,
    channel: String? = null,
    sentByDevice: String = "",
    errorReason: String = ""
  ) {
    val safeStatus = if (status in setOf("pending", "sending", "sent", "failed", "cancelled")) status else "pending"
    val now = System.currentTimeMillis()
    val next = _smsQueue.value.map { item ->
      if (item.id != smsId) item
      else item.copy(
        status = safeStatus,
        channel = channel ?: item.channel,
        updatedAt = now,
        sentAt = if (safeStatus == "sent") now else item.sentAt,
        sentByDevice = if (sentByDevice.isNotBlank()) sentByDevice.take(120) else item.sentByDevice,
        errorReason = errorReason.take(300)
      )
    }
    _smsQueue.value = next
    saveSmsQueueToPrefs(next)

    if (isVerifiedAdmin()) {
      try {
        val updates = mutableMapOf<String, Any>(
          "status" to safeStatus,
          "updatedAt" to now,
          "errorReason" to errorReason.take(300)
        )
        if (channel != null && channel in setOf("sim_gateway", "intent_manual")) {
          updates["channel"] = channel
        }
        if (safeStatus == "sent") {
          updates["sentAt"] = now
        }
        if (sentByDevice.isNotBlank()) {
          updates["sentByDevice"] = sentByDevice.take(120)
        }
        firestore?.collection("sms_queue")?.document(smsId)?.update(updates)
          ?.addOnFailureListener { e ->
            handleFirestoreError(e, OperationType.UPDATE, "sms_queue/$smsId")
          }
      } catch (e: Exception) {
        handleFirestoreError(e, OperationType.UPDATE, "sms_queue/$smsId")
      }
    }
  }

  fun deleteSmsQueueItem(smsId: String) {
    val next = _smsQueue.value.filterNot { it.id == smsId }
    _smsQueue.value = next
    saveSmsQueueToPrefs(next)
    if (isVerifiedAdmin()) {
      try {
        firestore?.collection("sms_queue")?.document(smsId)?.delete()
          ?.addOnFailureListener { e ->
            handleFirestoreError(e, OperationType.DELETE, "sms_queue/$smsId")
          }
      } catch (e: Exception) {
        handleFirestoreError(e, OperationType.DELETE, "sms_queue/$smsId")
      }
    }
  }

  fun clearCompletedSmsQueue() {
    val toRemove = _smsQueue.value.filter { it.status == "sent" || it.status == "cancelled" }
    val next = _smsQueue.value.filterNot { it.status == "sent" || it.status == "cancelled" }
    _smsQueue.value = next
    saveSmsQueueToPrefs(next)
    if (isVerifiedAdmin()) {
      toRemove.forEach { item ->
        try {
          firestore?.collection("sms_queue")?.document(item.id)?.delete()
        } catch (_: Exception) {
        }
      }
    }
  }

  fun updateCourierConfig(config: CourierConfig) {
    _courierConfig.value = config
  }

  fun updatePixelConfig(config: PixelConfig) {
    _pixelConfig.value = config
  }

  // Backup & JSON Export
  fun exportBackupJson(): String {
    val root = JSONObject()
    root.put("version", "1.0-ANDROID")
    root.put("storeName", _storeConfig.value.storeName)
    root.put("totalProducts", _products.value.size)
    root.put("totalOrders", _orders.value.size)
    root.put("exportedAt", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))
    return root.toString(2)
  }

  // Customer Reviews
  fun addReview(review: ProductReview) {
    val updated = listOf(review) + _reviews.value
    _reviews.value = updated

    // Recalculate product rating
    val productReviews = updated.filter { it.productId == review.productId }
    if (productReviews.isNotEmpty()) {
      val avgRating = productReviews.map { it.rating }.average().toFloat()
      val roundedAvg = (Math.round(avgRating * 10.0) / 10.0).toFloat()
      val count = productReviews.size
      _products.value = _products.value.map { p ->
        if (p.id == review.productId) {
          p.copy(rating = roundedAvg, reviewsCount = count)
        } else {
          p
        }
      }

      val db = firestore
      if (db != null && isVerifiedAdmin()) {
        try {
          db.collection("products").document(review.productId).set(
            mapOf(
              "rating" to roundedAvg,
              "reviewsCount" to count,
              "updatedAt" to FieldValue.serverTimestamp()
            ),
            SetOptions.merge()
          ).addOnFailureListener { e ->
            handleFirestoreError(e, OperationType.UPDATE, "products/${review.productId}")
          }
        } catch (e: Exception) {
          handleFirestoreError(e, OperationType.UPDATE, "products/${review.productId}")
        }
      }
    }

    val db = firestore ?: return
    val uid = currentAuthUid() ?: return
    try {
      db.collection("reviews").document(review.id).set(
        mapOf(
          "userId" to uid,
          "productId" to review.productId,
          "customerName" to review.customerName,
          "rating" to review.rating,
          "comment" to review.comment,
          "date" to review.date,
          "createdAt" to FieldValue.serverTimestamp(),
          "verified" to review.verified
        )
      ).addOnFailureListener { e ->
        handleFirestoreError(e, OperationType.CREATE, "reviews/${review.id}")
      }
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, "reviews/${review.id}")
    }
  }

  fun resetToDefaults() {
    _products.value = initialProducts
    inventorySyncService.mirrorCatalogToPos(initialProducts, source = "RESET_DEFAULTS", pushToCloud = true)
    persistLocalSyncSnapshot()
    _reviews.value = initialReviews
    _categories.value = initialCategories
    _orders.value = initialOrders
    _coupons.value = initialCoupons
    _banners.value = initialBanners
    _storeConfig.value = StoreConfig()
    saveHomeSections(initialHomeSections)
  }

  // Home Page Sections Management
  private fun syncProductFlagsToHomeSections(product: Product) {
    val next = _homeSections.value.map { sec ->
      when (sec.id) {
        "hot_sale" -> {
          val updatedIds = if (product.isHotSale) {
            if (sec.productIds.contains(product.id)) sec.productIds else listOf(product.id) + sec.productIds
          } else {
            sec.productIds.filterNot { it == product.id }
          }
          sec.copy(productIds = updatedIds)
        }
        "best_deals" -> {
          val updatedIds = if (product.isBestDeal) {
            if (sec.productIds.contains(product.id)) sec.productIds else listOf(product.id) + sec.productIds
          } else {
            sec.productIds.filterNot { it == product.id }
          }
          sec.copy(productIds = updatedIds)
        }
        "b2g1_offers" -> {
          val updatedIds = if (product.isB2G1) {
            if (sec.productIds.contains(product.id)) sec.productIds else listOf(product.id) + sec.productIds
          } else {
            sec.productIds.filterNot { it == product.id }
          }
          sec.copy(productIds = updatedIds)
        }
        else -> sec
      }
    }
    saveHomeSections(next)
  }

  private fun normalizeSectionConfig(s: HomeSectionConfig): HomeSectionConfig {
    val inferredCols = when {
      s.columnsPerRow in 2..4 -> s.columnsPerRow
      else -> 3
    }
    val inferredRows = when {
      s.rowCount in 1..4 -> s.rowCount
      s.layout == "grid3x2" -> 2
      s.layout == "grid3x3" -> 3
      else -> 1
    }
    val inferredStyle = when {
      s.type in setOf("top_banner", "promo_banner") -> "banner"
      s.type in setOf("voucher_strip", "category_slider") -> "strip"
      s.displayStyle == "grid" || s.displayStyle == "slider" -> s.displayStyle
      s.layout == "grid3x2" || s.layout == "grid3x3" -> "grid"
      else -> "slider"
    }
    val fixedMax = if (s.type == "reels_showcase" && inferredStyle == "slider" && inferredRows == 1 && s.maxItems == 12) {
      inferredCols
    } else if (inferredStyle == "grid") {
      inferredCols * inferredRows
    } else {
      s.maxItems
    }
    val migratedTitle = if (s.type == "reels_showcase" && (s.title.isBlank() || s.title == "রিলস ভিডিও শপ" || s.title.contains("সাজগোজ রিলস"))) {
      "Product Reels"
    } else {
      s.title
    }
    val migratedSubtitle = if (s.type == "reels_showcase") {
      "পন্যের ভিডিও দেখে অর্ডার করুন"
    } else {
      s.subtitle
    }
    return s.copy(
      title = migratedTitle,
      subtitle = migratedSubtitle,
      columnsPerRow = inferredCols,
      rowCount = inferredRows,
      displayStyle = inferredStyle,
      maxItems = fixedMax
    )
  }

  private fun loadHomeSections(): List<HomeSectionConfig> {
    val raw = prefs.getString("sajgoj_home_sections", null) ?: return initialHomeSections
    val isCustomized = prefs.getBoolean("sajgoj_home_sections_customized", false)
    return try {
      val arr = JSONArray(raw)
      val list = buildList {
        for (i in 0 until arr.length()) {
          val o = arr.optJSONObject(i) ?: continue
          val pidArr = o.optJSONArray("productIds")
          val pids = buildList {
            if (pidArr != null) {
              for (j in 0 until pidArr.length()) {
                val idVal = pidArr.optString(j)
                if (idVal.isNotBlank()) add(idVal)
              }
            }
          }
          val legacyLayout = o.optString("layout", "slider3")
          val defaultCols = 3
          val defaultRows = when (legacyLayout) {
            "grid3x2" -> 2
            "grid3x3" -> 3
            else -> 1
          }
          val defaultStyle = when (legacyLayout) {
            "grid3x2", "grid3x3" -> "grid"
            "banner" -> "banner"
            "strip" -> "strip"
            else -> "slider"
          }
          add(
            normalizeSectionConfig(
              HomeSectionConfig(
                id = o.optString("id"),
                type = o.optString("type"),
                title = o.optString("title"),
                subtitle = o.optString("subtitle"),
                icon = o.optString("icon", "✨"),
                badge = o.optString("badge", ""),
                layout = legacyLayout,
                columnsPerRow = if (o.has("columnsPerRow")) o.optInt("columnsPerRow", defaultCols) else defaultCols,
                rowCount = if (o.has("rowCount")) o.optInt("rowCount", defaultRows) else defaultRows,
                displayStyle = if (o.has("displayStyle")) o.optString("displayStyle", defaultStyle) else defaultStyle,
                enabled = o.optBoolean("enabled", true),
                isCustom = o.optBoolean("isCustom", false),
                categoryName = o.optString("categoryName", ""),
                productIds = pids,
                imageUrl = o.optString("imageUrl", ""),
                bannerButtonText = o.optString("bannerButtonText", "অফার দেখুন"),
                bannerLinkCategory = o.optString("bannerLinkCategory", "All"),
                maxItems = o.optInt("maxItems", 12)
              )
            )
          )
        }
      }
      if (isCustomized) {
        list
      } else if (list.isNotEmpty()) {
        val copy = list.toMutableList()
        if (copy.none { it.type == "combo_packs" }) {
          initialHomeSections.find { it.type == "combo_packs" }?.let { comboSec ->
            val hotIdx = copy.indexOfFirst { it.id == "hot_sale" }
            if (hotIdx >= 0) copy.add(hotIdx + 1, comboSec) else copy.add(comboSec)
          }
        }
        if (copy.none { it.type == "b2g1_offers" }) {
          initialHomeSections.find { it.type == "b2g1_offers" }?.let { b2g1Sec ->
            val comboIdx = copy.indexOfFirst { it.type == "combo_packs" }
            if (comboIdx >= 0) copy.add(comboIdx + 1, b2g1Sec) else copy.add(b2g1Sec)
          }
        }
        if (copy.none { it.type == "reels_showcase" }) {
          initialHomeSections.find { it.type == "reels_showcase" }?.let { reelsSec ->
            val dealsIdx = copy.indexOfFirst { it.id == "best_deals" }
            if (dealsIdx >= 0) copy.add(dealsIdx + 1, reelsSec) else copy.add(reelsSec)
          }
        }
        copy
      } else {
        initialHomeSections
      }
    } catch (_: Exception) {
      initialHomeSections
    }
  }

  private fun persistHomeSectionsInternal(sections: List<HomeSectionConfig>, markCustomized: Boolean) {
    val normalized = sections.map { normalizeSectionConfig(it) }
    _homeSections.value = normalized
    val arr = JSONArray()
    normalized.forEach { s ->
      arr.put(
        JSONObject().apply {
          put("id", s.id)
          put("type", s.type)
          put("title", s.title)
          put("subtitle", s.subtitle)
          put("icon", s.icon)
          put("badge", s.badge)
          put("layout", s.layout)
          put("columnsPerRow", s.columnsPerRow)
          put("rowCount", s.rowCount)
          put("displayStyle", s.displayStyle)
          put("enabled", s.enabled)
          put("isCustom", s.isCustom)
          put("categoryName", s.categoryName)
          put("productIds", JSONArray(s.productIds))
          put("imageUrl", s.imageUrl)
          put("bannerButtonText", s.bannerButtonText)
          put("bannerLinkCategory", s.bannerLinkCategory)
          put("maxItems", s.maxItems)
        }
      )
    }
    prefs.edit()
      .putString("sajgoj_home_sections", arr.toString())
      .putBoolean("sajgoj_home_sections_customized", markCustomized)
      .apply()
  }

  fun saveHomeSections(sections: List<HomeSectionConfig>) {
    persistHomeSectionsInternal(sections, markCustomized = true)
  }

  fun toggleHomeSection(sectionId: String) {
    val next = _homeSections.value.map { if (it.id == sectionId) it.copy(enabled = !it.enabled) else it }
    saveHomeSections(next)
  }

  fun moveHomeSection(sectionId: String, delta: Int) {
    val list = _homeSections.value.toMutableList()
    val idx = list.indexOfFirst { it.id == sectionId }
    if (idx < 0) return
    val targetIdx = idx + delta
    if (targetIdx !in list.indices) return
    val item = list.removeAt(idx)
    list.add(targetIdx, item)
    saveHomeSections(list)
  }

  fun updateHomeSection(sectionId: String, transform: (HomeSectionConfig) -> HomeSectionConfig) {
    val next = _homeSections.value.map { if (it.id == sectionId) transform(it) else it }
    saveHomeSections(next)
  }

  fun toggleProductInHomeSection(sectionId: String, productId: String) {
    val next = _homeSections.value.map { sec ->
      if (sec.id != sectionId) sec
      else {
        val exists = sec.productIds.contains(productId)
        val updatedIds = if (exists) sec.productIds.filterNot { it == productId } else listOf(productId) + sec.productIds
        sec.copy(productIds = updatedIds)
      }
    }
    saveHomeSections(next)

    if (sectionId == "hot_sale" || sectionId == "best_deals" || sectionId == "b2g1_offers") {
      _products.value = _products.value.map { p ->
        if (p.id != productId) p
        else when (sectionId) {
          "hot_sale" -> p.copy(isHotSale = !p.isHotSale)
          "best_deals" -> p.copy(isBestDeal = !p.isBestDeal)
          else -> p.copy(isB2G1 = !p.isB2G1)
        }
      }
      persistLocalSyncSnapshot()
    }
  }

  fun addCustomHomeSection(section: HomeSectionConfig) {
    saveHomeSections(_homeSections.value + normalizeSectionConfig(section))
  }

  fun deleteHomeSection(sectionId: String) {
    saveHomeSections(_homeSections.value.filterNot { it.id == sectionId })
  }

  fun resetHomeSectionsToDefault() {
    persistHomeSectionsInternal(initialHomeSections, markCustomized = false)
  }

  fun likeReel(reelId: String) {
    _reels.value = _reels.value.map { reel ->
      if (reel.id != reelId) reel
      else {
        val nextLiked = !reel.userLiked
        reel.copy(
          userLiked = nextLiked,
          likes = if (nextLiked) reel.likes + 1 else (reel.likes - 1).coerceAtLeast(0)
        )
      }
    }
  }
}
