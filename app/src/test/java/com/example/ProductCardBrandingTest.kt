package com.example

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.example.data.model.Product
import com.example.data.model.ProductDiscountHelper
import com.example.data.model.StoreDesignConfig
import com.example.ui.components.CompactProductCard
import com.example.ui.components.ProductCard
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w411dp-h915dp-xxhdpi")
class ProductCardBrandingTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun compactProductCard_showsTitleAndCrossedRegularPlusOfferSideBySide_withoutBottomDetailsButton() {
    val product = Product(
      id = "p_reg_offer",
      name = "উড়ন্ত পরী ডিজাইনার ড্রেস",
      price = 400,
      regularPrice = 450,
      offerPrice = 400,
      stock = 12,
      category = "কিডস",
      imageUrl = "res://ic_app_icon_1792530102214",
      sku = "KID-01",
      description = "টেস্ট"
    )
    var clickedId: String? = null

    composeTestRule.setContent {
      Box(modifier = Modifier.width(180.dp)) {
        CompactProductCard(
          product = product,
          onProductClick = { clickedId = it.id },
          onAddToCart = {},
          design = StoreDesignConfig()
        )
      }
    }

    composeTestRule.onNodeWithTag("compact_add_to_cart_p_reg_offer_title", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("compact_add_to_cart_p_reg_offer_price_box", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("compact_add_to_cart_p_reg_offer").assertDoesNotExist()
    composeTestRule.onNodeWithText("ডিটেলস").assertDoesNotExist()
    composeTestRule.onNodeWithText("পন্যটি দেখুন").assertDoesNotExist()

    val regularBounds = composeTestRule.onNodeWithText("৳450", useUnmergedTree = true).assertIsDisplayed().getBoundsInRoot()
    val offerBounds = composeTestRule.onNodeWithText("৳400", useUnmergedTree = true).assertIsDisplayed().getBoundsInRoot()

    // Regular crossed price appears FIRST (to the left) and Offer price beside it on the SAME horizontal row
    assertTrue("Expected regular price to the left of offer price ($regularBounds vs $offerBounds)", regularBounds.right < offerBounds.left)
    assertTrue(
      "Expected regular and offer price on same horizontal line (reg=$regularBounds, offer=$offerBounds)",
      regularBounds.top < offerBounds.bottom && offerBounds.top < regularBounds.bottom
    )

    composeTestRule.onNodeWithTag("compact_product_card_p_reg_offer").performClick()
    assertEquals("p_reg_offer", clickedId)
  }

  @Test
  fun compactProductCard_percentDiscountMode_showsBoldOfferPriceAndPercentSideBySide() {
    val product = Product(
      id = "p_pct",
      name = "Custom Logo Sweatpants",
      price = 490,
      regularPrice = 890,
      offerPrice = 490,
      stock = 25,
      category = "ফ্যাশন",
      imageUrl = "res://ic_app_icon_1792530102214",
      sku = "SW-490",
      description = "টেস্ট",
      attributes = mapOf(
        ProductDiscountHelper.ATTR_DISCOUNT_MODE to "percent",
        ProductDiscountHelper.ATTR_DISCOUNT_PERCENT to "45"
      )
    )

    composeTestRule.setContent {
      Box(modifier = Modifier.width(180.dp)) {
        CompactProductCard(
          product = product,
          onProductClick = {},
          onAddToCart = {},
          design = StoreDesignConfig()
        )
      }
    }

    val offerBounds = composeTestRule.onNodeWithText("৳490", useUnmergedTree = true).assertIsDisplayed().getBoundsInRoot()
    val pctBounds = composeTestRule.onNodeWithText("-45%", useUnmergedTree = true).assertIsDisplayed().getBoundsInRoot()
    composeTestRule.onNodeWithText("৳890", useUnmergedTree = true).assertDoesNotExist()

    assertTrue("Expected selling price before -45% badge ($offerBounds vs $pctBounds)", offerBounds.right < pctBounds.left)
    assertTrue(
      "Expected selling price and -45% on same horizontal line",
      offerBounds.top < pctBounds.bottom && pctBounds.top < offerBounds.bottom
    )
  }

  @Test
  fun productCard_showsCleanTitleAndSideBySidePriceWithoutDetailsButton() {
    val product = Product(
      id = "p_grid",
      name = "রয়েল ব্রাইডাল পার্ল ও কুন্দন সেট",
      price = 3850,
      regularPrice = 4600,
      offerPrice = 3850,
      stock = 5,
      category = "জুয়েলারি",
      imageUrl = "res://ic_app_icon_1792530102214",
      sku = "JW-3850",
      description = "টেস্ট"
    )

    composeTestRule.setContent {
      Box(modifier = Modifier.width(190.dp)) {
        ProductCard(
          product = product,
          onProductClick = {},
          onAddToCart = {},
          design = StoreDesignConfig()
        )
      }
    }

    composeTestRule.onNodeWithTag("product_card_title_p_grid", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("product_card_price_p_grid", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithText("ডিটেলস").assertDoesNotExist()

    val regularBounds = composeTestRule.onNodeWithText("৳4600", useUnmergedTree = true).assertIsDisplayed().getBoundsInRoot()
    val offerBounds = composeTestRule.onNodeWithText("৳3850", useUnmergedTree = true).assertIsDisplayed().getBoundsInRoot()
    assertTrue(regularBounds.right < offerBounds.left)
    assertTrue(regularBounds.top < offerBounds.bottom && offerBounds.top < regularBounds.bottom)
  }
}
