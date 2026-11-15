package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.provider.OpenableColumns
import com.example.data.model.Product
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

data class ColorFamilyProfile(
  val family: String,
  val labelBn: String,
  val colorHex: Long,
  val keywords: List<String>
)

data class VisualSearchAnalysis(
  val colorProfile: ColorFamilyProfile,
  val matchedProductIds: List<String>,
  val scores: Map<String, Int>
)

object VisualImageSearchHelper {

  fun classifyColorFamily(r: Int, g: Int, b: Int): ColorFamilyProfile {
    val rf = r.coerceIn(0, 255) / 255f
    val gf = g.coerceIn(0, 255) / 255f
    val bf = b.coerceIn(0, 255) / 255f
    val maxVal = max(rf, max(gf, bf))
    val minVal = min(rf, min(gf, bf))
    val delta = maxVal - minVal
    val v = maxVal
    val s = if (maxVal == 0f) 0f else delta / maxVal

    var h = 0f
    if (delta != 0f) {
      h = when (maxVal) {
        rf -> (((gf - bf) / delta) + (if (gf < bf) 6f else 0f)) * 60f
        gf -> (((bf - rf) / delta) + 2f) * 60f
        else -> (((rf - gf) / delta) + 4f) * 60f
      }
    }

    val packedHex = ((0xFFL shl 24) or
      (r.coerceIn(0, 255).toLong() shl 16) or
      (g.coerceIn(0, 255).toLong() shl 8) or
      b.coerceIn(0, 255).toLong())

    return when {
      v < 0.22f -> ColorFamilyProfile(
        family = "black_dark",
        labelBn = "কালো / ডার্ক",
        colorHex = packedHex,
        keywords = listOf("কালো", "black", "dark", "প্যান্ট", "টি-শার্ট", "বোরকা", "আবায়া")
      )
      s < 0.16f && v > 0.78f -> ColorFamilyProfile(
        family = "white_cream",
        labelBn = "সাদা / ক্রিম",
        colorHex = packedHex,
        keywords = listOf("সাদা", "ক্রিম", "অফ হোয়াইট", "white", "cream", "ivory", "pearl", "মুক্তা", "পাঞ্জাবি", "জামদানি")
      )
      s < 0.22f -> ColorFamilyProfile(
        family = "neutral_silver",
        labelBn = "সিলভার / অ্যাশ",
        colorHex = packedHex,
        keywords = listOf("সিলভার", "অ্যাশ", "silver", "grey", "gray", "প্যান্ট", "জুয়েলারি")
      )
      h >= 342f || h < 18f -> ColorFamilyProfile(
        family = "red_maroon",
        labelBn = "লাল / মেরুন",
        colorHex = packedHex,
        keywords = listOf("লাল", "মেরুন", "সিঁদুর", "red", "maroon", "crimson", "bridal", "ব্রাইডাল", "বধূ", "বেনারসি", "জামদানি", "কাতান", "শাড়ি")
      )
      h in 18f..41.99f -> ColorFamilyProfile(
        family = "orange_peach",
        labelBn = "কমলা / পিচ",
        colorHex = packedHex,
        keywords = listOf("কমলা", "পিচ", "কপার", "orange", "peach", "coral", "শাড়ি", "কামিজ")
      )
      h in 42f..71.99f -> ColorFamilyProfile(
        family = "gold_yellow",
        labelBn = "সোনালি / হলুদ",
        colorHex = packedHex,
        keywords = listOf("সোনালি", "গোল্ডেন", "হলুদ", "gold", "golden", "yellow", "কুন্দন", "জুয়েলারি", "ঝুমকা", "নেকলেস", "হার", "চুড়ি", "ব্রাইডাল")
      )
      h in 72f..164.99f -> ColorFamilyProfile(
        family = "green_emerald",
        labelBn = "সবুজ / পান্না",
        colorHex = packedHex,
        keywords = listOf("সবুজ", "মেহেদি", "টিয়া", "পান্না", "green", "emerald", "olive", "শাড়ি", "পাঞ্জাবি", "জামা")
      )
      h in 165f..254.99f -> ColorFamilyProfile(
        family = "blue_navy",
        labelBn = "নীল / নেভি",
        colorHex = packedHex,
        keywords = listOf("নীল", "আকাশি", "নেভি", "blue", "navy", "sky", "royal", "ডেনিম", "প্যান্ট", "টি-শার্ট", "জামা")
      )
      h in 255f..311.99f -> ColorFamilyProfile(
        family = "purple_violet",
        labelBn = "বেগুনি / পার্পল",
        colorHex = packedHex,
        keywords = listOf("বেগুনি", "পার্পল", "জাম", "purple", "violet", "lavender", "জর্জেট", "শাড়ি")
      )
      else -> ColorFamilyProfile(
        family = "pink_rose",
        labelBn = "গোলাপি / রানি",
        colorHex = packedHex,
        keywords = listOf("গোলাপি", "রানি", "পিংক", "pink", "rose", "magenta", "মিষ্টি", "কামিজ", "ফ্রক", "লেহেঙ্গা", "শাড়ি")
      )
    }
  }

  fun expandSearchAliases(raw: String): List<String> {
    val q = raw.trim().lowercase()
    if (q.isEmpty()) return emptyList()
    val tokens = linkedSetOf(q)
    val aliasGroups = listOf(
      listOf("শাড়ি", "saree", "shari", "sharee", "jamdani", "জামদানি", "katan", "কাতান"),
      listOf("জুয়েলারি", "গহনা", "jewelry", "jewellery", "ornament", "necklace", "jhumka", "ঝুমকা", "হার", "চুড়ি", "bangle"),
      listOf("জামা", "কামিজ", "থ্রিপিস", "dress", "kurti", "kameez", "salwar", "frock", "ফ্রক", "গাউন"),
      listOf("টি-শার্ট", "টি শার্ট", "t-shirt", "tshirt", "shirt", "শার্ট", "polo"),
      listOf("প্যান্ট", "pant", "pants", "jeans", "trouser", "denim"),
      listOf("ব্রাইডাল", "বধূ", "বিয়ে", "bridal", "wedding", "lehenga", "লেহেঙ্গা"),
      listOf("পাঞ্জাবি", "panjabi", "punjabi", "kurta"),
      listOf("লাল", "red", "maroon", "মেরুন"),
      listOf("সোনালি", "gold", "golden", "হলুদ", "yellow"),
      listOf("গোলাপি", "pink", "rose"),
      listOf("সবুজ", "green", "emerald"),
      listOf("নীল", "blue", "navy"),
      listOf("কালো", "black"),
      listOf("সাদা", "white", "cream")
    )
    aliasGroups.forEach { group ->
      if (group.any { term -> q.contains(term) || term.contains(q) }) {
        tokens.addAll(group)
      }
    }
    return tokens.toList()
  }

  fun analyzeRgbAndTokens(
    avgR: Int,
    avgG: Int,
    avgB: Int,
    fileNameHint: String,
    products: List<Product>
  ): VisualSearchAnalysis {
    val colorProfile = classifyColorFamily(avgR, avgG, avgB)
    val fileTokens = fileNameHint
      .replace(Regex("\\.[^.]+$"), "")
      .lowercase()
      .split(Regex("[^a-z0-9\\u0980-\\u09ff]+"))
      .filter { it.length >= 2 }
      .flatMap { expandSearchAliases(it) }
      .distinct()

    val scores = mutableMapOf<String, Int>()
    products.forEach { product ->
      var score = 0
      val haystack = "${product.name} ${product.category} ${product.description} ${product.sku} ${product.colors.joinToString(" ")}".lowercase()

      fileTokens.forEach { token ->
        if (haystack.contains(token)) score += 18
      }

      colorProfile.keywords.forEachIndexed { index, kw ->
        if (haystack.contains(kw.lowercase())) {
          score += if (index < 5) 16 else 9
        }
      }

      if (product.colors.isNotEmpty()) {
        val colorText = product.colors.joinToString(" ").lowercase()
        if (colorProfile.keywords.any { kw -> colorText.contains(kw.lowercase()) }) {
          score += 20
        }
      }

      if (colorProfile.keywords.any { kw -> product.category.lowercase().contains(kw.lowercase()) }) {
        score += 12
      }

      if (score > 0) {
        scores[product.id] = score
      }
    }

    val matchedIds = if (scores.isNotEmpty()) {
      scores.entries.sortedByDescending { it.value }.map { it.key }
    } else {
      products.take(6).mapIndexed { index, product ->
        scores[product.id] = 6 - index
        product.id
      }
    }

    return VisualSearchAnalysis(
      colorProfile = colorProfile,
      matchedProductIds = matchedIds,
      scores = scores
    )
  }

  fun analyzeBitmapForProducts(
    bitmap: Bitmap,
    fileNameHint: String,
    products: List<Product>
  ): VisualSearchAnalysis {
    val scaled = Bitmap.createScaledBitmap(bitmap, 48, 48, true)
    var weightedR = 0.0
    var weightedG = 0.0
    var weightedB = 0.0
    var totalWeight = 0.0
    var rawR = 0L
    var rawG = 0L
    var rawB = 0L
    var rawCount = 0

    for (y in 0 until scaled.height) {
      for (x in 0 until scaled.width) {
        val pixel = scaled.getPixel(x, y)
        val alpha = Color.alpha(pixel)
        if (alpha < 100) continue
        val r = Color.red(pixel)
        val g = Color.green(pixel)
        val b = Color.blue(pixel)
        rawR += r
        rawG += g
        rawB += b
        rawCount++

        val maxChannel = max(r, max(g, b))
        val minChannel = min(r, min(g, b))
        val sat = if (maxChannel == 0) 0.0 else (maxChannel - minChannel).toDouble() / maxChannel.toDouble()
        val isLightNeutralBg = maxChannel > 235 && sat < 0.1
        val weight = if (isLightNeutralBg) 0.15 else (1.0 + sat * 3.5)
        weightedR += r * weight
        weightedG += g * weight
        weightedB += b * weight
        totalWeight += weight
      }
    }

    val avgR = if (totalWeight > 0) (weightedR / totalWeight).roundToInt() else if (rawCount > 0) (rawR / rawCount).toInt() else 180
    val avgG = if (totalWeight > 0) (weightedG / totalWeight).roundToInt() else if (rawCount > 0) (rawG / rawCount).toInt() else 60
    val avgB = if (totalWeight > 0) (weightedB / totalWeight).roundToInt() else if (rawCount > 0) (rawB / rawCount).toInt() else 60

    if (scaled != bitmap) {
      scaled.recycle()
    }

    return analyzeRgbAndTokens(avgR, avgG, avgB, fileNameHint, products)
  }

  fun resolveDisplayName(context: Context, uri: Uri): String {
    return try {
      context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
          val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
          if (idx >= 0) return@use cursor.getString(idx) ?: "selected_image.jpg"
        }
        "selected_image.jpg"
      } ?: (uri.lastPathSegment ?: "selected_image.jpg")
    } catch (_: Exception) {
      uri.lastPathSegment ?: "selected_image.jpg"
    }
  }

  fun analyzeImageUriForProducts(
    context: Context,
    uri: Uri,
    products: List<Product>
  ): Pair<String, VisualSearchAnalysis>? {
    val fileName = resolveDisplayName(context, uri)
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    context.contentResolver.openInputStream(uri)?.use { stream ->
      BitmapFactory.decodeStream(stream, null, bounds)
    }
    var sampleSize = 1
    while (bounds.outWidth / sampleSize > 256 || bounds.outHeight / sampleSize > 256) {
      sampleSize *= 2
    }
    val decodeOpts = BitmapFactory.Options().apply { inSampleSize = sampleSize }
    val bitmap = context.contentResolver.openInputStream(uri)?.use { stream ->
      BitmapFactory.decodeStream(stream, null, decodeOpts)
    } ?: return null

    return try {
      val analysis = analyzeBitmapForProducts(bitmap, fileName, products)
      fileName to analysis
    } finally {
      bitmap.recycle()
    }
  }

  fun findProductByScanPayload(rawText: String, products: List<Product>): Product? {
    val text = rawText.trim()
    if (text.isEmpty()) return null
    val hashMatch = Regex("#product-([a-zA-Z0-9_-]+)").find(text)?.groupValues?.getOrNull(1)
    val queryMatch = Regex("[?&]product=([a-zA-Z0-9_-]+)").find(text)?.groupValues?.getOrNull(1)
    val extractedId = hashMatch ?: queryMatch

    if (!extractedId.isNullOrBlank()) {
      products.find { it.id.equals(extractedId, ignoreCase = true) }?.let { return it }
    }
    products.find { it.id.equals(text, ignoreCase = true) }?.let { return it }
    products.find { it.sku.equals(text, ignoreCase = true) }?.let { return it }
    return products.find {
      text.contains(it.id, ignoreCase = true) ||
        (it.sku.isNotBlank() && text.contains(it.sku, ignoreCase = true))
    }
  }
}
