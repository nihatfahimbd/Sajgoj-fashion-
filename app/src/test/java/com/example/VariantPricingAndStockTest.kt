package com.example

import com.example.data.model.Order
import com.example.data.model.OrderItem
import com.example.data.model.Product
import com.example.data.model.ProductVariantHelper
import com.example.data.model.ProductVariation
import com.example.data.sync.InventorySyncService
import com.example.ui.components.buildCartesianVariations
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VariantPricingAndStockTest {

  private fun sampleVariantProduct(): Product {
    val variations = listOf(
      ProductVariation(
        id = "v_s_black",
        size = "S",
        color = "Black",
        price = 800,
        specialPrice = 650,
        specialPriceStart = "2026-01-01",
        specialPriceEnd = "2026-12-31",
        stock = 5,
        enabled = true,
        sku = "SAJ-1"
      ),
      ProductVariation(
        id = "v_m_black",
        size = "M",
        color = "Black",
        price = 900,
        specialPrice = 0,
        stock = 10,
        enabled = true,
        sku = "SAJ-2"
      ),
      ProductVariation(
        id = "v_m_red",
        size = "M",
        color = "Red",
        price = 950,
        specialPrice = 750,
        specialPriceStart = "2026-01-01",
        specialPriceEnd = "2026-02-01",
        stock = 4,
        enabled = true,
        sku = "SAJ-3"
      ),
      ProductVariation(
        id = "v_xl_red_disabled",
        size = "XL",
        color = "Red",
        price = 700,
        specialPrice = 500,
        stock = 99,
        enabled = false,
        sku = "SAJ-4"
      )
    )
    return Product(
      id = "p_var_1",
      name = "কটন কুর্তি প্রিমিয়াম",
      price = 650,
      regularPrice = 800,
      offerPrice = 650,
      stock = 19,
      category = "জামা / কামিজ",
      imageUrl = "https://example.com/main.jpg",
      sku = "SAJ-VAR",
      description = "প্রিমিয়াম কটন কুর্তি",
      colors = listOf("Black", "Red"),
      sizes = listOf("S", "M", "XL"),
      variations = variations,
      variantImages = mapOf(
        "Black" to "https://example.com/black.jpg",
        "Red" to "https://example.com/red.jpg"
      ),
      variantOrder = listOf("Size", "Color Family"),
      variantImageType = "Color Family"
    )
  }

  @Test
  fun summarizeVariations_ignoresDisabledRowsAndComputesTotals() {
    val product = sampleVariantProduct()
    val summary = ProductVariantHelper.summarizeVariations(product.variations)

    assertEquals(19, summary.totalStock) // 5 + 10 + 4 (disabled XL ignored)
    assertEquals(800, summary.minRegularPrice)
    assertEquals(650, summary.minOfferPrice)
    assertEquals(650, summary.effectiveSellingPrice)
    assertTrue(summary.specialPriceEnabled)
  }

  @Test
  fun resolvePricing_respectsCombinationAndSpecialPriceDateWindow() {
    val product = sampleVariantProduct()

    // Black + S inside special price window
    val (sellBlackS, regBlackS) = ProductVariantHelper.resolvePricing(
      product = product,
      color = "Black",
      size = "S",
      todayIso = "2026-06-15"
    )
    assertEquals(650, sellBlackS)
    assertEquals(800, regBlackS)

    // Red + M within active date range vs after expiry
    val (sellRedActive, regRedActive) = ProductVariantHelper.resolvePricing(
      product = product,
      color = "Red",
      size = "M",
      todayIso = "2026-01-15"
    )
    assertEquals(750, sellRedActive)
    assertEquals(950, regRedActive)

    val (sellRedExpired, regRedExpired) = ProductVariantHelper.resolvePricing(
      product = product,
      color = "Red",
      size = "M",
      todayIso = "2026-05-01"
    )
    assertEquals(950, sellRedExpired)
    assertEquals(950, regRedExpired)
  }

  @Test
  fun resolveStockAndImage_returnVariantSpecificValues() {
    val product = sampleVariantProduct()

    assertEquals(5, ProductVariantHelper.resolveStock(product, color = "Black", size = "S"))
    assertEquals(10, ProductVariantHelper.resolveStock(product, color = "Black", size = "M"))
    assertEquals(4, ProductVariantHelper.resolveStock(product, color = "Red", size = "M"))
    assertEquals(0, ProductVariantHelper.resolveStock(product, color = "Red", size = "XL"))

    assertEquals("https://example.com/black.jpg", ProductVariantHelper.resolveImage(product, color = "Black"))
    assertEquals("https://example.com/red.jpg", ProductVariantHelper.resolveImage(product, color = "Red"))
    assertEquals("https://example.com/main.jpg", ProductVariantHelper.resolveImage(product, color = "Blue"))
  }

  @Test
  fun buildCartesianVariations_preservesExistingPriceAndStockWhenAddingOptions() {
    val existing = listOf(
      ProductVariation(
        id = "v1",
        size = "M",
        color = "Black",
        price = 920,
        specialPrice = 790,
        stock = 12,
        enabled = true
      )
    )
    val next = buildCartesianVariations(
      sizes = listOf("M", "L"),
      colors = listOf("Black", "White"),
      ageGroups = emptyList(),
      existing = existing,
      defaultPrice = 0,
      defaultStock = 0
    )

    assertEquals(4, next.size)
    val kept = next.first { it.size == "M" && it.color == "Black" }
    assertEquals(920, kept.price)
    assertEquals(790, kept.specialPrice)
    assertEquals(12, kept.stock)
    val newlyAdded = next.first { it.size == "L" && it.color == "White" }
    assertEquals(0, newlyAdded.price)
    assertEquals(0, newlyAdded.stock)
  }

  @Test
  fun saleDeductionAndOrderCancelRestock_updatesSpecificVariantAndRecalculatesTotal() {
    val service = InventorySyncService(firestore = null, clock = { 1_700_000_000_000L })
    val initialProducts = listOf(sampleVariantProduct())

    val afterSale = service.applySaleStockDeduction(
      products = initialProducts,
      items = listOf(
        OrderItem(
          productId = "p_var_1",
          name = "কটন কুর্তি প্রিমিয়াম",
          quantity = 3,
          price = 650,
          color = "Black",
          size = "S"
        ),
        OrderItem(
          productId = "p_var_1",
          name = "কটন কুর্তি প্রিমিয়াম",
          quantity = 2,
          price = 750,
          color = "Red",
          size = "M"
        )
      ),
      orderId = "ORD-777"
    )

    val pAfterSale = afterSale.first()
    // Black S: 5 -> 2; Black M: 10 unchanged; Red M: 4 -> 2 => total = 14
    assertEquals(14, pAfterSale.stock)
    assertEquals(2, ProductVariantHelper.resolveStock(pAfterSale, "Black", "S"))
    assertEquals(10, ProductVariantHelper.resolveStock(pAfterSale, "Black", "M"))
    assertEquals(2, ProductVariantHelper.resolveStock(pAfterSale, "Red", "M"))

    // Cancel order -> restock exact variations
    val cancelOrder = Order(
      id = "ORD-777",
      customerName = "আয়েশা",
      phone = "01700000000",
      district = "চাঁদপুর",
      thana = "সদর",
      address = "চাঁদপুর",
      items = listOf(
        OrderItem(productId = "p_var_1", name = "কটন কুর্তি প্রিমিয়াম", quantity = 3, price = 650, color = "Black", size = "S"),
        OrderItem(productId = "p_var_1", name = "কটন কুর্তি প্রিমিয়াম", quantity = 2, price = 750, color = "Red", size = "M")
      ),
      subtotal = 3450,
      deliveryCharge = 60,
      discount = 0,
      couponCode = "",
      total = 3510,
      paymentMethod = "COD",
      status = "Cancelled",
      date = "2026-11-22"
    )

    val afterRestock = service.applyOrderCancellationRestock(
      products = afterSale,
      order = cancelOrder,
      restoreStock = true
    )

    val pAfterRestock = afterRestock.first()
    assertEquals(19, pAfterRestock.stock)
    assertEquals(5, ProductVariantHelper.resolveStock(pAfterRestock, "Black", "S"))
    assertEquals(4, ProductVariantHelper.resolveStock(pAfterRestock, "Red", "M"))
    assertFalse(ProductVariantHelper.summarizeVariations(pAfterRestock.variations).totalStock == 0)
  }
}
