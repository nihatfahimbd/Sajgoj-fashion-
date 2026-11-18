package com.example

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.hasTestTag
import com.example.data.model.HomeSectionConfig
import com.example.data.model.Product
import com.example.data.model.StoreConfig
import com.example.ui.ProductSortOption
import com.example.ui.screens.StorefrontScreen
import com.example.ui.sortStorefrontProducts
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ProductSortingTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  private val sampleProducts = listOf(
    Product(
      id = "p_mid_old",
      name = "ক্লাসিক জামদানি শাড়ি",
      price = 2500,
      stock = 5,
      category = "শাড়ি",
      imageUrl = "",
      sku = "SKU-1",
      description = "Mid price oldest",
      createdAt = 1000L
    ),
    Product(
      id = "p_cheap_newest",
      name = "ড্রপ শোল্ডার টি-শার্ট",
      price = 550,
      stock = 12,
      category = "টি-শার্ট",
      imageUrl = "",
      sku = "SKU-2",
      description = "Lowest price newest",
      createdAt = 3000L
    ),
    Product(
      id = "p_expensive_mid",
      name = "রয়েল ব্রাইডাল লেহেঙ্গা",
      price = 6200,
      stock = 3,
      category = "ব্রাইডাল",
      imageUrl = "",
      sku = "SKU-3",
      description = "Highest price mid",
      createdAt = 2000L
    )
  )

  @Test
  fun sortStorefrontProducts_ordersByLowToHighHighToLowAndNewest() {
    val lowToHigh = sortStorefrontProducts(sampleProducts, ProductSortOption.PRICE_LOW_TO_HIGH)
    assertEquals(listOf("p_cheap_newest", "p_mid_old", "p_expensive_mid"), lowToHigh.map { it.id })

    val highToLow = sortStorefrontProducts(sampleProducts, ProductSortOption.PRICE_HIGH_TO_LOW)
    assertEquals(listOf("p_expensive_mid", "p_mid_old", "p_cheap_newest"), highToLow.map { it.id })

    val newest = sortStorefrontProducts(sampleProducts, ProductSortOption.NEWEST_ARRIVALS)
    assertEquals(listOf("p_cheap_newest", "p_expensive_mid", "p_mid_old"), newest.map { it.id })

    val defaultOrder = sortStorefrontProducts(sampleProducts, ProductSortOption.DEFAULT)
    assertEquals(sampleProducts.map { it.id }, defaultOrder.map { it.id })
  }

  @Test
  fun storefrontSortDropdown_allowsSelectingAllSortOptionsAndUpdatesDisplay() {
    var activeSort by mutableStateOf(ProductSortOption.DEFAULT)

    composeTestRule.setContent {
      StorefrontScreen(
        products = sampleProducts,
        banners = emptyList(),
        config = StoreConfig(),
        homeSections = listOf(
          HomeSectionConfig(
            id = "all_products",
            type = "all_products",
            title = "সব প্রোডাক্ট",
            layout = "grid3x3",
            columnsPerRow = 3,
            rowCount = 3,
            displayStyle = "grid",
            enabled = true,
            maxItems = 9
          )
        ),
        sortOption = activeSort,
        onSortOptionSelected = { activeSort = it },
        onProductClick = {},
        onAddToCart = {},
        onBannerClick = {}
      )
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("storefront_grid")
      .performScrollToNode(hasTestTag("product_sort_dropdown"))

    composeTestRule.onNodeWithTag("product_sort_dropdown").assertIsDisplayed().performClick()
    composeTestRule.waitForIdle()

    // Verify all three requested options appear in the dropdown
    composeTestRule.onNodeWithText("Price: Low to High").assertIsDisplayed()
    composeTestRule.onNodeWithText("Price: High to Low").assertIsDisplayed()
    composeTestRule.onNodeWithText("Newest Arrivals").assertIsDisplayed()

    // Select Price: Low to High
    composeTestRule.onNodeWithTag("product_sort_option_PRICE_LOW_TO_HIGH").performClick()
    composeTestRule.waitForIdle()
    assertEquals(ProductSortOption.PRICE_LOW_TO_HIGH, activeSort)

    // Switch to Price: High to Low
    composeTestRule.onNodeWithTag("product_sort_dropdown").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("product_sort_option_PRICE_HIGH_TO_LOW").performClick()
    composeTestRule.waitForIdle()
    assertEquals(ProductSortOption.PRICE_HIGH_TO_LOW, activeSort)

    // Switch to Newest Arrivals
    composeTestRule.onNodeWithTag("product_sort_dropdown").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("product_sort_option_NEWEST_ARRIVALS").performClick()
    composeTestRule.waitForIdle()
    assertEquals(ProductSortOption.NEWEST_ARRIVALS, activeSort)

    // Verify sorted grid displays all products
    composeTestRule.onNodeWithTag("product_card_p_cheap_newest").assertIsDisplayed()
    composeTestRule.onNodeWithTag("product_card_p_expensive_mid").assertIsDisplayed()
  }
}
