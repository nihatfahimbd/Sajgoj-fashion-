package com.example

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.data.model.Product
import com.example.data.model.ProductVariation
import com.example.data.model.StoreConfig
import com.example.ui.components.ProductDetailDialog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w411dp-h915dp-xxhdpi")
class ProductDetailBottomBarTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  private fun sampleProduct(): Product = Product(
    id = "p-kids-1",
    name = "বেবি প্রিন্টেড কটন ড্রেস",
    price = 400,
    regularPrice = 500,
    stock = 20,
    category = "বাচ্চাদের পোশাক",
    sku = "KID-400",
    colors = listOf("Purple", "Orange"),
    sizes = listOf("3-4yrs", "4-5 yrs"),
    variations = listOf(
      ProductVariation(id = "v1", color = "Purple", size = "3-4yrs", price = 400, stock = 10, enabled = true),
      ProductVariation(id = "v2", color = "Orange", size = "4-5 yrs", price = 400, stock = 10, enabled = true)
    ),
    description = "প্রিমিয়াম কটন ফেব্রিক এবং আরামদায়ক ডিজাইন।"
  )

  @Test
  fun productDetailDialog_showsStickyBottomBarOnlyWhileOpenAndRemovesInlineButton() {
    val product = sampleProduct()
    var showDetail by mutableStateOf(true)
    var whatsAppClicked = false
    var buyNowCallCount = 0
    var addToCartCallCount = 0
    var lastColor = ""
    var lastSize = ""

    composeTestRule.setContent {
      if (showDetail) {
        ProductDetailDialog(
          product = product,
          storeConfig = StoreConfig(whatsapp = "01712345678"),
          onDismiss = { showDetail = false },
          onAddToCart = { _, _, color, size, _ ->
            addToCartCallCount++
            lastColor = color
            lastSize = size
          },
          onBuyNow = { _, _, color, size, _ ->
            buyNowCallCount++
            lastColor = color
            lastSize = size
            showDetail = false
          },
          onWhatsAppClick = { _, _, color, size, _ ->
            whatsAppClicked = true
            lastColor = color
            lastSize = size
          }
        )
      }
    }

    composeTestRule.waitForIdle()
    // Inline button must be removed
    composeTestRule.onNodeWithTag("detail_add_to_cart_button").assertDoesNotExist()

    // Sticky bottom menu with 3 actions must be displayed
    composeTestRule.onNodeWithTag("product_detail_bottom_bar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("detail_bottom_whatsapp_button").assertIsDisplayed()
    composeTestRule.onNodeWithTag("detail_bottom_buy_now_button").assertIsDisplayed()
    composeTestRule.onNodeWithTag("detail_bottom_add_to_cart_button").assertIsDisplayed()
    composeTestRule.onNodeWithText("Buy Now").assertIsDisplayed()
    composeTestRule.onNodeWithText("Add To Cart").assertIsDisplayed()

    // Click WhatsApp Logo
    composeTestRule.onNodeWithTag("detail_bottom_whatsapp_button").performClick()
    assertTrue(whatsAppClicked)
    assertEquals("Purple", lastColor)
    assertEquals("3-4yrs", lastSize)

    // Click Add To Cart -> adds to cart & dismisses dialog + bottom bar disappears
    composeTestRule.onNodeWithTag("detail_bottom_add_to_cart_button").performClick()
    composeTestRule.waitForIdle()
    assertEquals(1, addToCartCallCount)
    composeTestRule.onNodeWithTag("product_detail_bottom_bar").assertDoesNotExist()

    // Re-open and test Buy Now
    showDetail = true
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("product_detail_bottom_bar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("detail_bottom_buy_now_button").performClick()
    composeTestRule.waitForIdle()
    assertEquals(1, buyNowCallCount)
    composeTestRule.onNodeWithTag("product_detail_bottom_bar").assertDoesNotExist()
  }
}
