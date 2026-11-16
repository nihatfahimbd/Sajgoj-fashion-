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

  @Test
  fun computeLast7DaysRevenuePoints_sumsValidOrdersAndExcludesCancelled() {
    val cal = java.util.Calendar.getInstance()
    val fmt = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
    val todayStr = fmt.format(cal.time)
    cal.add(java.util.Calendar.DAY_OF_YEAR, -2)
    val twoDaysAgoStr = fmt.format(cal.time)

    val testOrders = listOf(
      com.example.data.model.Order(
        id = "O1", customerName = "A", phone = "017", district = "ঢাকা", thana = "ধানমন্ডি",
        address = "", items = emptyList(), subtotal = 1500, deliveryCharge = 0, discount = 0,
        total = 1500, paymentMethod = "bKash", status = "Delivered", date = todayStr
      ),
      com.example.data.model.Order(
        id = "O2", customerName = "B", phone = "018", district = "ঢাকা", thana = "গুলশান",
        address = "", items = emptyList(), subtotal = 2500, deliveryCharge = 0, discount = 0,
        total = 2500, paymentMethod = "COD", status = "Cancelled", date = todayStr
      ),
      com.example.data.model.Order(
        id = "O3", customerName = "C", phone = "019", district = "ঢাকা", thana = "বনানী",
        address = "", items = emptyList(), subtotal = 3200, deliveryCharge = 0, discount = 0,
        total = 3200, paymentMethod = "POS Cash", status = "Confirmed", date = twoDaysAgoStr
      )
    )

    val points = com.example.ui.components.computeLast7DaysRevenuePoints(testOrders)
    assertEquals(7, points.size)
    assertEquals("আজ", points.last().label)
    assertEquals(1500, points.last().revenue)
    assertEquals(1, points.last().orderCount)
    assertEquals(3200, points[4].revenue)
    assertEquals(1, points[4].orderCount)
  }

  @Test
  fun reelsShowcaseAndExpansionSeeds_areConfiguredProperly() {
    val repo = StoreRepository(context)
    assertTrue("Seed catalog should have >= 12 items so grid3x2 (6) and grid3x3 (9) expand", repo.products.value.size >= 12)

    val reelsBlock = repo.homeSections.value.find { it.type == "reels_showcase" }!!
    assertEquals("slider3", reelsBlock.layout)
    assertEquals(3, reelsBlock.columnsPerRow)
    assertEquals(1, reelsBlock.rowCount)
    assertEquals(3, reelsBlock.maxItems)
    assertEquals(3, com.example.ui.components.resolveBlockGridConfig(reelsBlock).defaultLimit)
    assertTrue(reelsBlock.enabled)

    val initialReels = repo.reels.value
    assertTrue(initialReels.size >= 4)
    val firstReel = initialReels.first()
    val beforeLikes = firstReel.likes
    repo.likeReel(firstReel.id)
    val afterLikes = repo.reels.value.first { it.id == firstReel.id }
    assertTrue(afterLikes.userLiked)
    assertEquals(beforeLikes + 1, afterLikes.likes)
  }

  @Test
  fun comboPacksAndB2G1BlocksAndAllCategoryCatalog_workProperly() {
    val repo = StoreRepository(context)
    val sections = repo.homeSections.value

    val comboBlock = sections.find { it.type == "combo_packs" }!!
    assertEquals("কম্বো প্যাক", comboBlock.title)
    assertEquals(3, comboBlock.columnsPerRow)
    assertTrue(comboBlock.enabled)

    val b2g1Block = sections.find { it.type == "b2g1_offers" }!!
    assertEquals("Buy 2 Get 1", b2g1Block.title)
    assertTrue(b2g1Block.productIds.isNotEmpty())
    assertTrue(b2g1Block.enabled)

    // Verify combos seed + addComboToCart
    assertTrue(repo.combos.value.size >= 3)
    val firstCombo = repo.combos.value.first()
    assertTrue(repo.addComboToCart(firstCombo))
    assertEquals(1, repo.cart.value.size)
    assertEquals(firstCombo.comboPrice, repo.cart.value.first().product.price)

    // Verify catalog includes All storefront categories including টি-শার্ট and প্যান্ট
    val categoriesPresent = repo.products.value.map { it.category }.toSet()
    assertTrue(categoriesPresent.contains("শাড়ি"))
    assertTrue(categoriesPresent.contains("জামা / কামিজ"))
    assertTrue(categoriesPresent.contains("ব্রাইডাল সেট"))
    assertTrue(categoriesPresent.contains("নেকলেস"))
    assertTrue(categoriesPresent.contains("বাচ্চাদের পোশাক"))
    assertTrue(categoriesPresent.contains("টি-শার্ট"))
    assertTrue(categoriesPresent.contains("প্যান্ট"))
  }

  @Test
  fun perBlockColumnsAndRowCount_persistAcrossReload() {
    val repo = StoreRepository(context)
    repo.updateHomeSection("hot_sale") {
      it.copy(columnsPerRow = 4, rowCount = 2, displayStyle = "grid", layout = "grid3x2", maxItems = 8)
    }
    repo.updateHomeSection("reels_showcase") {
      it.copy(columnsPerRow = 2, rowCount = 2, displayStyle = "grid", maxItems = 4)
    }

    val reloaded = StoreRepository(context)
    val hotSale = reloaded.homeSections.value.first { it.id == "hot_sale" }
    assertEquals(4, hotSale.columnsPerRow)
    assertEquals(2, hotSale.rowCount)
    assertEquals("grid", hotSale.displayStyle)
    assertEquals(8, com.example.ui.components.resolveBlockGridConfig(hotSale).defaultLimit)

    val reels = reloaded.homeSections.value.first { it.id == "reels_showcase" }
    assertEquals(2, reels.columnsPerRow)
    assertEquals(2, reels.rowCount)
    assertEquals(4, com.example.ui.components.resolveBlockGridConfig(reels).defaultLimit)
  }
}
