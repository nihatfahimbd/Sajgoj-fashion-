package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.HomeSectionConfig
import com.example.data.model.Product
import com.example.data.repository.StoreRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class HomePageSettingTest {

  private lateinit var context: Context

  @Before
  fun setup() {
    context = ApplicationProvider.getApplicationContext()
    context.getSharedPreferences("sajgoj_store_prefs", Context.MODE_PRIVATE).edit().clear().commit()
  }

  @Test
  fun defaultSections_includeAllFourRequiredProductBlocksWithExpectedLayouts() {
    val repo = StoreRepository(context)
    val sections = repo.homeSections.value

    val newBlock = sections.find { it.id == "new_products" }!!
    val hotBlock = sections.find { it.id == "hot_sale" }!!
    val bestBlock = sections.find { it.id == "best_deals" }!!
    val allBlock = sections.find { it.id == "all_products" }!!

    assertEquals("নতুন প্রোডাক্ট", newBlock.title)
    assertEquals("slider3", newBlock.layout)

    assertEquals("Hot Sale", hotBlock.title)
    assertEquals("slider3", hotBlock.layout)

    assertEquals("সেরা ডিল", bestBlock.title)
    assertEquals("grid3x2", bestBlock.layout)
    assertEquals(6, bestBlock.maxItems)

    assertEquals("সব প্রোডাক্ট", allBlock.title)
    assertEquals("grid3x3", allBlock.layout)
    assertEquals(9, allBlock.maxItems)
  }

  @Test
  fun addProduct_stampsCreatedAtAndSyncsHotSaleAndBestDealFlags() {
    val repo = StoreRepository(context)
    val newProduct = Product(
      id = "p_new_hot",
      name = "নিউ ডিজাইনার জামদানি শাড়ি",
      price = 3950,
      stock = 15,
      category = "শাড়ি",
      imageUrl = "",
      sku = "SAJ-999",
      description = "নতুন কালেকশন",
      isHotSale = true,
      isBestDeal = true
    )

    repo.addProduct(newProduct)

    val saved = repo.products.value.first { it.id == "p_new_hot" }
    assertTrue(saved.createdAt > 0L)
    assertEquals(saved.id, repo.products.value.sortedByDescending { it.createdAt }.first().id)

    val hotSaleSection = repo.homeSections.value.first { it.id == "hot_sale" }
    val bestDealsSection = repo.homeSections.value.first { it.id == "best_deals" }
    assertTrue(hotSaleSection.productIds.contains("p_new_hot"))
    assertTrue(bestDealsSection.productIds.contains("p_new_hot"))
  }

  @Test
  fun homePageSetting_supportsToggleMoveAddCustomAndPersistence() {
    val repo = StoreRepository(context)

    // Toggle block OFF and verify
    repo.toggleHomeSection("best_deals")
    assertFalse(repo.homeSections.value.first { it.id == "best_deals" }.enabled)

    // Add custom promo banner & move it directly below hot_sale
    val customBanner = HomeSectionConfig(
      id = "custom_eid_banner",
      type = "promo_banner",
      title = "ঈদ ধামাকা বিজ্ঞাপন",
      subtitle = "ফ্ল্যাট ২০% ছাড়",
      layout = "banner",
      enabled = true,
      isCustom = true,
      bannerLinkCategory = "শাড়ি"
    )
    repo.addCustomHomeSection(customBanner)

    // Initially added at end -> move up until right after hot_sale
    val hotSaleIndexBefore = repo.homeSections.value.indexOfFirst { it.id == "hot_sale" }
    while (repo.homeSections.value.indexOfFirst { it.id == "custom_eid_banner" } > hotSaleIndexBefore + 1) {
      repo.moveHomeSection("custom_eid_banner", -1)
    }
    val updated = repo.homeSections.value
    assertEquals(hotSaleIndexBefore + 1, updated.indexOfFirst { it.id == "custom_eid_banner" })

    // Re-create repository to confirm persistence in SharedPreferences
    val reloadedRepo = StoreRepository(context)
    val reloadedSections = reloadedRepo.homeSections.value
    assertFalse(reloadedSections.first { it.id == "best_deals" }.enabled)
    assertEquals(hotSaleIndexBefore + 1, reloadedSections.indexOfFirst { it.id == "custom_eid_banner" })

    // Delete custom section
    reloadedRepo.deleteHomeSection("custom_eid_banner")
    assertTrue(reloadedRepo.homeSections.value.none { it.id == "custom_eid_banner" })
  }
}
