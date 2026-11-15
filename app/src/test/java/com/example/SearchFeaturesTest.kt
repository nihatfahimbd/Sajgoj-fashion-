package com.example

import com.example.data.model.Product
import com.example.util.VisualImageSearchHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchFeaturesTest {

  private val sampleProducts = listOf(
    Product(
      id = "p1",
      name = "রয়েল লাল কাতান শাড়ি",
      price = 4500,
      stock = 8,
      category = "শাড়ি",
      imageUrl = "",
      sku = "SJG-101",
      description = "ব্রাইডাল লাল কাতান শাড়ি",
      colors = listOf("লাল", "মেরুন")
    ),
    Product(
      id = "p2",
      name = "সোনালি কুন্দন ঝুমকা সেট",
      price = 1850,
      stock = 12,
      category = "জুয়েলারি",
      imageUrl = "",
      sku = "SJG-202",
      description = "গোল্ডেন পার্ল কুন্দন গহনা",
      colors = listOf("সোনালি")
    ),
    Product(
      id = "p3",
      name = "নেভি ব্লু ডেনিম প্যান্ট",
      price = 1400,
      stock = 15,
      category = "প্যান্ট",
      imageUrl = "",
      sku = "SJG-303",
      description = "আরামদায়ক নীল প্যান্ট",
      colors = listOf("নীল")
    )
  )

  @Test
  fun classifyColorFamily_identifiesPrimaryFashionTones() {
    assertEquals("red_maroon", VisualImageSearchHelper.classifyColorFamily(210, 25, 35).family)
    assertEquals("gold_yellow", VisualImageSearchHelper.classifyColorFamily(235, 195, 40).family)
    assertEquals("blue_navy", VisualImageSearchHelper.classifyColorFamily(28, 70, 210).family)
    assertEquals("green_emerald", VisualImageSearchHelper.classifyColorFamily(30, 175, 75).family)
    assertEquals("black_dark", VisualImageSearchHelper.classifyColorFamily(15, 15, 18).family)
    assertEquals("white_cream", VisualImageSearchHelper.classifyColorFamily(246, 245, 242).family)
  }

  @Test
  fun expandSearchAliases_bridgesBengaliAndEnglishVoiceTerms() {
    val sareeAliases = VisualImageSearchHelper.expandSearchAliases("saree")
    assertTrue(sareeAliases.contains("শাড়ি"))
    assertTrue(sareeAliases.contains("জামদানি"))

    val jewelryAliases = VisualImageSearchHelper.expandSearchAliases("জুয়েলারি")
    assertTrue(jewelryAliases.contains("jewelry"))
    assertTrue(jewelryAliases.contains("গহনা"))
  }

  @Test
  fun analyzeRgbAndTokens_ranksVisuallyMatchingProductsFirst() {
    val redSareeAnalysis = VisualImageSearchHelper.analyzeRgbAndTokens(
      avgR = 205,
      avgG = 24,
      avgB = 38,
      fileNameHint = "red_bridal_saree.jpg",
      products = sampleProducts
    )
    assertEquals("red_maroon", redSareeAnalysis.colorProfile.family)
    assertTrue(redSareeAnalysis.matchedProductIds.isNotEmpty())
    assertEquals("p1", redSareeAnalysis.matchedProductIds.first())

    val goldJewelryAnalysis = VisualImageSearchHelper.analyzeRgbAndTokens(
      avgR = 230,
      avgG = 190,
      avgB = 45,
      fileNameHint = "golden_jhumka.png",
      products = sampleProducts
    )
    assertEquals("gold_yellow", goldJewelryAnalysis.colorProfile.family)
    assertEquals("p2", goldJewelryAnalysis.matchedProductIds.first())
  }

  @Test
  fun findProductByScanPayload_resolvesDeepLinkAndSku() {
    val byHash = VisualImageSearchHelper.findProductByScanPayload("https://sajgoj.shop/#product-p2", sampleProducts)
    assertNotNull(byHash)
    assertEquals("p2", byHash?.id)

    val bySku = VisualImageSearchHelper.findProductByScanPayload("sjg-303", sampleProducts)
    assertNotNull(bySku)
    assertEquals("p3", bySku?.id)
  }
}
