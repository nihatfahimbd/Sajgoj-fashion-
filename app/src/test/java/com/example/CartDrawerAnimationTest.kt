package com.example

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import com.example.data.model.CartItem
import com.example.data.model.Product
import com.example.ui.components.CartAndCheckoutDialog
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CartDrawerAnimationTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun cartDrawer_animatesOpenAndCloseViaCloseButtonAndBackdrop() {
    var isOpen by mutableStateOf(false)
    val sampleItem = CartItem(
      product = Product(
        id = "p_cart_1",
        name = "জামদানি শাড়ি",
        price = 3200,
        stock = 5,
        category = "শাড়ি",
        imageUrl = "",
        sku = "SAJ-01",
        description = "Test"
      ),
      quantity = 1
    )

    composeTestRule.setContent {
      CartAndCheckoutDialog(
        visible = isOpen,
        cartItems = listOf(sampleItem),
        coupons = emptyList(),
        onDismiss = { isOpen = false },
        onUpdateQty = { _, _ -> },
        onRemoveItem = {},
        onPlaceOrder = { _, _, _, _, _, _, _ -> },
        onShowToast = {}
      )
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("cart_dialog").assertDoesNotExist()

    // Open cart drawer
    isOpen = true
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("cart_drawer_backdrop").assertIsDisplayed()
    composeTestRule.onNodeWithTag("cart_dialog").assertIsDisplayed()

    // Close via close button
    composeTestRule.onNodeWithTag("cart_drawer_close_button").performClick()
    composeTestRule.waitForIdle()
    assertFalse(isOpen)
    composeTestRule.onNodeWithTag("cart_dialog").assertDoesNotExist()

    // Re-open and close via backdrop scrim tap
    isOpen = true
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("cart_dialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("cart_drawer_backdrop").performTouchInput {
      click(Offset(8f, centerY))
    }
    composeTestRule.waitForIdle()
    assertFalse(isOpen)
    composeTestRule.onNodeWithTag("cart_dialog").assertDoesNotExist()
  }
}
