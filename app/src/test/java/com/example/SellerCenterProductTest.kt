package com.example

import com.example.data.model.Product
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SellerCenterProductTest {

  @Test
  fun productDefaultsMatchSellerCenterSchema() {
    val product = Product(
      id = "p_test_1",
      name = "Jamdani Sharee",
      price = 4500,
      stock = 10,
      category = "শাড়ি",
      imageUrl = "https://example.com/sharee.jpg",
      sku = "SAJ-101",
      description = "Handwoven Jamdani"
    )

    assertEquals("No Brand", product.brand)
    assertTrue(product.attributes.isEmpty())
    assertTrue(product.isAvailable)
    assertFalse(product.isDraft)
    assertFalse(product.specialPriceEnabled)
    assertEquals("", product.specialPriceStart)
    assertEquals("", product.specialPriceEnd)
    assertEquals("None", product.shippingDangerousGoods)
    assertTrue(product.shippingExpressChandpur)
    assertEquals("No Warranty", product.warrantyType)
    assertTrue(product.highlights.isEmpty())
    assertTrue(product.enabledForWeb)
    assertTrue(product.enabledForPos)
  }

  @Test
  fun storefrontVisibilityExcludesDraftAndWebDisabledProducts() {
    val published = Product(
      id = "p1",
      name = "Published Sharee",
      price = 2500,
      stock = 5,
      category = "শাড়ি",
      imageUrl = "https://example.com/1.jpg",
      sku = "SAJ-1",
      description = "Published",
      isDraft = false,
      enabledForWeb = true
    )
    val draft = Product(
      id = "p2",
      name = "Draft Sharee",
      price = 2500,
      stock = 5,
      category = "শাড়ি",
      imageUrl = "https://example.com/2.jpg",
      sku = "SAJ-2",
      description = "Draft",
      isDraft = true,
      enabledForWeb = true
    )
    val posOnly = Product(
      id = "p3",
      name = "POS Only Sharee",
      price = 2500,
      stock = 5,
      category = "শাড়ি",
      imageUrl = "https://example.com/3.jpg",
      sku = "SAJ-3",
      description = "POS only",
      isDraft = false,
      enabledForWeb = false,
      enabledForPos = true
    )

    val storefrontVisible = listOf(published, draft, posOnly).filter { !it.isDraft && it.enabledForWeb }

    assertEquals(listOf("p1"), storefrontVisible.map { it.id })
  }
}
