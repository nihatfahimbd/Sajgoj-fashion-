package com.example.ai

import android.graphics.Bitmap
import android.util.Log
import com.example.data.model.Order
import com.example.data.model.Product
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

private const val TAG = "SajgojAiService"
private const val GEMINI_MODEL = "gemini-3.8-flash"
private const val AI_TIMEOUT_MS = 60_000L

data class AiStylistSuggestion(
  val adviceText: String,
  val stylingTip: String,
  val recommendedProductIds: List<String>
)

sealed interface AiTaskState<out T> {
  data object Idle : AiTaskState<Nothing>
  data object Loading : AiTaskState<Nothing>
  data class Success<T>(val data: T) : AiTaskState<T>
  data class Error(val message: String) : AiTaskState<Nothing>
}

/**
 * Firebase AI Logic (Gemini API) service for Sajgoj Fashion.
 * Requires App Check initialized via configureAppCheck() before invocation.
 */
object SajgojAiService {

  private val stylistSchema = Schema.obj(
    mapOf(
      "adviceText" to Schema.string("Warm, helpful fashion recommendation in Bengali matching the user request."),
      "stylingTip" to Schema.string("Short practical styling/pairing tip in Bengali (jewelry, color contrast, accessories)."),
      "recommendedProductIds" to Schema.array(
        Schema.string("Exact product id from the provided catalog list"),
        description = "Up to 4 matching in-stock product IDs from the provided Sajgoj catalog."
      )
    )
  )

  private val stylistModel by lazy {
    Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
      modelName = GEMINI_MODEL,
      generationConfig = generationConfig {
        responseMimeType = "application/json"
        responseSchema = stylistSchema
        temperature = 0.7f
      }
    )
  }

  private val copywriterModel by lazy {
    Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
      modelName = GEMINI_MODEL,
      generationConfig = generationConfig {
        temperature = 0.75f
      }
    )
  }

  suspend fun askFashionStylist(
    userPrompt: String,
    catalog: List<Product>,
    occasion: String = "",
    referenceImage: Bitmap? = null
  ): Result<AiStylistSuggestion> {
    return try {
      val compactCatalog = catalog.take(30).joinToString("\n") { p ->
        "- id=${p.id} | name=${p.name} | category=${p.category} | price=${p.price} BDT | stock=${p.stock} | colors=${p.colors.joinToString(",")} | sizes=${p.sizes.joinToString(",")}"
      }

      val promptText = buildString {
        appendLine("You are the official Fashion Stylist & Personal Shopper for SAJGOJ FASHION (a Bangladeshi women's ethnic, saree, jewelry, beauty & fashion boutique).")
        appendLine("Respond naturally in polite, stylish Bengali (with clear Bangladeshi currency ৳ when mentioning prices).")
        if (occasion.isNotBlank()) {
          appendLine("Selected occasion/theme: $occasion")
        }
        appendLine("Customer request: $userPrompt")
        if (referenceImage != null) {
          appendLine("The customer also attached an outfit/reference photo. Match colors, embroidery/style aesthetic, and accessories with items from our catalog.")
        }
        appendLine()
        appendLine("Available Sajgoj Fashion catalog (pick recommendedProductIds ONLY from these exact `id` values, preferring items with stock > 0):")
        appendLine(compactCatalog)
      }

      val response = withTimeout(AI_TIMEOUT_MS) {
        if (referenceImage != null) {
          stylistModel.generateContent(
            content {
              image(referenceImage)
              text(promptText)
            }
          )
        } else {
          stylistModel.generateContent(promptText)
        }
      }

      val rawJson = response.text?.trim()
        ?: return Result.failure(IllegalStateException("Gemini থেকে কোনো উত্তর পাওয়া যায়নি।"))

      val parsed = parseStylistJson(rawJson, catalog)
      Result.success(parsed)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Log.e(TAG, "askFashionStylist failed", e)
      Result.failure(Exception(friendlyAiError(e)))
    }
  }

  suspend fun generateProductDescription(
    productName: String,
    category: String,
    price: Int,
    colors: List<String>,
    sizes: List<String>,
    ageGroups: List<String>
  ): Result<String> {
    return try {
      val prompt = buildString {
        appendLine("You are an expert e-commerce copywriter for SAJGOJ FASHION in Bangladesh.")
        appendLine("Write an engaging, high-converting Bengali product description (3-4 concise sentences + 3 bullet highlights).")
        appendLine("Product Name: $productName")
        appendLine("Category: $category")
        if (price > 0) appendLine("Price: $price BDT")
        if (colors.isNotEmpty()) appendLine("Available Colors: ${colors.joinToString(", ")}")
        if (sizes.isNotEmpty()) appendLine("Sizes: ${sizes.joinToString(", ")}")
        if (ageGroups.isNotEmpty()) appendLine("Kids Age Groups: ${ageGroups.joinToString(", ")}")
        appendLine("Do not invent fake brand claims. Output only the clean Bengali description text.")
      }

      val response = withTimeout(AI_TIMEOUT_MS) {
        copywriterModel.generateContent(prompt)
      }
      val text = response.text?.trim().orEmpty()
      if (text.isBlank()) {
        Result.failure(IllegalStateException("AI বিবরণ তৈরি করতে পারেনি। অনুগ্রহ করে আবার চেষ্টা করুন।"))
      } else {
        Result.success(text)
      }
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Log.e(TAG, "generateProductDescription failed", e)
      Result.failure(Exception(friendlyAiError(e)))
    }
  }

  suspend fun generateAdminStoreInsights(
    products: List<Product>,
    orders: List<Order>
  ): Result<String> {
    return try {
      val lowStock = products.filter { it.stock <= 5 }.take(8).joinToString(", ") { "${it.name} (স্টক: ${it.stock})" }
      val totalRevenue = orders.filter { it.status != "Cancelled" }.sumOf { it.total }
      val pendingOrders = orders.count { it.status.equals("Pending", ignoreCase = true) }
      val categoriesBreakdown = products.groupBy { it.category }.entries
        .joinToString(", ") { "${it.key}: ${it.value.size}টি" }

      val prompt = buildString {
        appendLine("You are a retail Merchandising & Growth Analyst for SAJGOJ FASHION.")
        appendLine("Summarize actionable store insights and 1 ready-to-post Facebook/Eid promo caption in clean Bengali.")
        appendLine("Total Products: ${products.size} ($categoriesBreakdown)")
        appendLine("Low Stock Items (<=5): ${lowStock.ifBlank { "কোনো লো-স্টক নেই" }}")
        appendLine("Total Orders: ${orders.size}, Pending Dispatch: $pendingOrders, Total Revenue: $totalRevenue BDT")
        appendLine("Keep the output concise, scannable, and practical (under 180 words).")
      }

      val response = withTimeout(AI_TIMEOUT_MS) {
        copywriterModel.generateContent(prompt)
      }
      val text = response.text?.trim().orEmpty()
      if (text.isBlank()) {
        Result.failure(IllegalStateException("AI ইনসাইট খালি এসেছে। আবার চেষ্টা করুন।"))
      } else {
        Result.success(text)
      }
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Log.e(TAG, "generateAdminStoreInsights failed", e)
      Result.failure(Exception(friendlyAiError(e)))
    }
  }

  private fun parseStylistJson(rawJson: String, catalog: List<Product>): AiStylistSuggestion {
    val catalogIds = catalog.map { it.id }.toSet()
    return try {
      val clean = rawJson
        .removePrefix("```json")
        .removePrefix("```")
        .removeSuffix("```")
        .trim()
      val obj = JSONObject(clean)
      val advice = obj.optString("adviceText").ifBlank { rawJson }
      val tip = obj.optString("stylingTip")
      val idsArr = obj.optJSONArray("recommendedProductIds")
      val matchedIds = buildList {
        if (idsArr != null) {
          for (i in 0 until idsArr.length()) {
            val id = idsArr.optString(i)
            if (id.isNotBlank() && catalogIds.contains(id) && !contains(id)) {
              add(id)
            }
          }
        }
      }
      AiStylistSuggestion(
        adviceText = advice,
        stylingTip = tip,
        recommendedProductIds = matchedIds
      )
    } catch (_: Exception) {
      AiStylistSuggestion(
        adviceText = rawJson,
        stylingTip = "",
        recommendedProductIds = emptyList()
      )
    }
  }

  private fun friendlyAiError(e: Exception): String {
    val raw = e.localizedMessage.orEmpty()
    return when {
      raw.contains("403", ignoreCase = true) || raw.contains("AppCheck", ignoreCase = true) ->
        "AI ভেরিফিকেশন (App Check) সম্পন্ন হয়নি। অনুগ্রহ করে ইন্টারনেট সংযোগ ও অ্যাপ সেটআপ যাচাই করে আবার চেষ্টা করুন।"
      raw.contains("timeout", ignoreCase = true) ->
        "AI উত্তর দিতে সময় বেশি নিচ্ছে। অনুগ্রহ করে আবার চেষ্টা করুন।"
      raw.contains("network", ignoreCase = true) || raw.contains("unable to resolve host", ignoreCase = true) ->
        "ইন্টারনেট সংযোগ নেই। অনলাইনে যুক্ত হয়ে পুনরায় চেষ্টা করুন।"
      else -> "AI অ্যাসিস্ট্যান্ট চালু করতে সমস্যা হয়েছে: ${raw.ifBlank { "অনুগ্রহ করে পুনরায় চেষ্টা করুন।" }}"
    }
  }
}
