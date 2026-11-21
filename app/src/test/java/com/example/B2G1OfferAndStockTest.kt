package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.B2G1Config
import com.example.data.repository.StoreRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class B2G1OfferAndStockTest {

  private lateinit var context: Context

  @Before
  fun setup() {
    context = ApplicationProvider.getApplicationContext()
    context.getSharedPreferences("sajgoj_store_prefs", Context.MODE_PRIVATE).edit().clear().commit()
  }

  @Test
  fun nonSelectedProducts_neverTriggerFreeGift_evenWhenAddingThreeOrMoreItems() {
    val repo = StoreRepository(context)
    val p1 = repo.products.value.first { it.id == "p1" }
    val p3 = repo.products.value.first { it.id == "p3" }
    val p5 = repo.products.value.first { it.id == "p5" }

    // Default storeConfig has empty eligibleProductIds
    assertTrue(repo.storeConfig.value.b2g1.eligibleProductIds.isEmpty())

    assertTrue(repo.addToCart(p1, quantity = 1))
    assertTrue(repo.addToCart(p3, quantity = 1))
    assertTrue(repo.addToCart(p5, quantity = 1))

    assertEquals(3, repo.cart.value.size)
    assertFalse("No free gift should be added unless Admin explicitly selects eligible products", repo.cart.value.any { it.isFreeGift })
  }

  @Test
  fun adminSelectedProducts_unlockFreeGiftAtThreshold_andDeductStockForAllThreeProducts() {
    val repo = StoreRepository(context)
    val p1 = repo.products.value.first { it.id == "p1" }
    val p2 = repo.products.value.first { it.id == "p2" }
    val p3 = repo.products.value.first { it.id == "p3" }
    val p4 = repo.products.value.first { it.id == "p4" }

    val p1InitialStock = p1.stock
    val p2InitialStock = p2.stock
    val p3InitialStock = p3.stock
    val p4InitialStock = p4.stock

    // Admin selects ONLY p1 and p2 for B2G1, with p4 as the free gift
    val currentCfg = repo.storeConfig.value
    repo.updateStoreConfig(
      currentCfg.copy(
        b2g1 = B2G1Config(
          enabled = true,
          minItems = 2,
          freeProductId = "p4",
          eligibleProductIds = listOf("p1", "p2")
        )
      )
    )

    // Add 1 eligible (p1) + 1 non-eligible (p3) -> MUST NOT trigger free gift
    repo.addToCart(p1, quantity = 1)
    repo.addToCart(p3, quantity = 1)
    assertEquals(2, repo.cart.value.size)
    assertFalse("Non-eligible p3 must not count toward B2G1 threshold", repo.cart.value.any { it.isFreeGift })

    // Add 2nd eligible product (p2) -> MUST auto-add p4 as free gift (price 0)
    repo.addToCart(p2, quantity = 1)
    assertEquals(4, repo.cart.value.size)
    val freeGift = repo.cart.value.find { it.isFreeGift }
    assertNotNull("Free gift item should be auto-added when 2 eligible products are in cart", freeGift)
    assertEquals("p4", freeGift!!.product.id)
    assertEquals("p4", freeGift.freeGiftOriginId)
    assertEquals(0, freeGift.product.price)
    assertEquals(1, freeGift.quantity)

    // Remove p3 (non-eligible) -> free gift must remain since p1 + p2 are still in cart
    val p3Key = repo.cart.value.first { it.product.id == "p3" }.itemKey
    repo.removeFromCart(p3Key)
    assertEquals(3, repo.cart.value.size)
    assertTrue(repo.cart.value.any { it.isFreeGift && it.product.id == "p4" })

    // Place order -> verify subtotal excludes free gift and ALL 3 products lose stock
    val order = repo.placeOrder(
      customerName = "আয়েশা",
      phone = "01711000000",
      district = "ঢাকা (Dhaka)",
      thana = "ধানমন্ডি",
      address = "বাড়ি ১০",
      paymentMethod = "Cash on Delivery",
      appliedCoupon = null
    )
    assertNotNull(order)
    assertEquals(p1.price + p2.price, order!!.subtotal)
    assertEquals(3, order.items.size)
    assertTrue(order.items.any { it.isFreeGift && it.productId == "p4" && it.price == 0 })

    val updatedProducts = repo.products.value
    assertEquals(p1InitialStock - 1, updatedProducts.first { it.id == "p1" }.stock)
    assertEquals(p2InitialStock - 1, updatedProducts.first { it.id == "p2" }.stock)
    assertEquals(p4InitialStock - 1, updatedProducts.first { it.id == "p4" }.stock)
    assertEquals(p3InitialStock, updatedProducts.first { it.id == "p3" }.stock)

    // Verify B2G1 config persisted across reload
    val reloadedRepo = StoreRepository(context)
    assertEquals(listOf("p1", "p2"), reloadedRepo.storeConfig.value.b2g1.eligibleProductIds)
    assertEquals("p4", reloadedRepo.storeConfig.value.b2g1.freeProductId)
  }

  @Test
  fun sameProductAsEligibleAndFreeGift_deductsThreeUnitsTotal() {
    val repo = StoreRepository(context)
    val p4 = repo.products.value.first { it.id == "p4" }
    val initialStock = p4.stock
    assertTrue(initialStock >= 5)

    repo.updateStoreConfig(
      repo.storeConfig.value.copy(
        b2g1 = B2G1Config(
          enabled = true,
          minItems = 2,
          freeProductId = "p4",
          eligibleProductIds = listOf("p4")
        )
      )
    )

    repo.addToCart(p4, quantity = 2)
    assertEquals(2, repo.cart.value.size)
    val paidItem = repo.cart.value.first { !it.isFreeGift }
    val freeItem = repo.cart.value.first { it.isFreeGift }
    assertEquals(2, paidItem.quantity)
    assertEquals(1, freeItem.quantity)
    assertFalse(paidItem.itemKey == freeItem.itemKey)

    val order = repo.placeOrder(
      customerName = "সাদিয়া",
      phone = "01811000000",
      district = "চাঁদপুর (Chandpur)",
      thana = "চাঁদপুর সদর",
      address = "নতুন বাজার",
      paymentMethod = "Cash on Delivery",
      appliedCoupon = null
    )
    assertNotNull(order)
    assertEquals(p4.price * 2, order!!.subtotal)

    val afterStock = repo.products.value.first { it.id == "p4" }.stock
    assertEquals(initialStock - 3, afterStock)
  }
}
