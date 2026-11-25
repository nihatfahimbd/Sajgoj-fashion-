package com.example

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Order
import com.example.data.model.OrderItem
import com.example.data.model.Product
import com.example.data.model.StoreConfig
import com.example.data.repository.StoreRepository
import com.example.util.AdminQrLookupHelper
import com.example.util.AdminQrLookupResult
import com.example.util.VisualImageSearchHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class DomainIntegrationTest {

  @Test
  fun storeConfig_defaultsToSajgojWorkersDevDomainAndPersistsUpdates() {
    val context = ApplicationProvider.getApplicationContext<android.content.Context>()
    assertEquals("https://sajgoj.nfbd.workers.dev", StoreConfig().websiteUrl)

    val repo1 = StoreRepository(context)
    assertEquals("https://sajgoj.nfbd.workers.dev", repo1.storeConfig.value.websiteUrl)

    val updated = repo1.storeConfig.value.copy(websiteUrl = "https://sajgoj.nfbd.workers.dev")
    repo1.updateStoreConfig(updated)

    val repo2 = StoreRepository(context)
    assertEquals("https://sajgoj.nfbd.workers.dev", repo2.storeConfig.value.websiteUrl)
  }

  @Test
  fun domainUrls_resolveProductsAndOrdersCorrectly() {
    val products = listOf(
      Product(
        id = "p1",
        name = "কাতান শাড়ি",
        price = 3500,
        regularPrice = 4200,
        stock = 8,
        category = "শাড়ি",
        imageUrl = "",
        sku = "SAJ-001",
        description = "কাতান শাড়ি টেস্ট"
      )
    )
    val orders = listOf(
      Order(
        id = "ORD-1001",
        customerName = "সাদিয়া",
        phone = "01711223344",
        district = "Dhaka",
        thana = "Dhanmondi",
        address = "Road 4",
        items = listOf(OrderItem("p1", "কাতান শাড়ি", 3500, 1)),
        subtotal = 3500,
        deliveryCharge = 70,
        discount = 0,
        total = 3570,
        paymentMethod = "COD",
        status = "Pending",
        date = "2026-11-25",
        trackingId = "TRK-991"
      )
    )

    assertEquals(
      "p1",
      VisualImageSearchHelper.findProductByScanPayload("https://sajgoj.nfbd.workers.dev/#product-p1", products)?.id
    )
    assertEquals(
      "p1",
      VisualImageSearchHelper.findProductByScanPayload("https://sajgoj.nfbd.workers.dev/?product=p1", products)?.id
    )
    assertEquals(
      "p1",
      VisualImageSearchHelper.findProductByScanPayload("https://sajgoj.nfbd.workers.dev/product/p1", products)?.id
    )

    val hashOrderMatch = AdminQrLookupHelper.lookup("https://sajgoj.nfbd.workers.dev/#order-ORD-1001", products, orders)
    assertTrue(hashOrderMatch is AdminQrLookupResult.OrderMatch)
    assertEquals("ORD-1001", (hashOrderMatch as AdminQrLookupResult.OrderMatch).order.id)

    val pathOrderMatch = AdminQrLookupHelper.lookup("https://sajgoj.nfbd.workers.dev/order/ORD-1001", products, orders)
    assertTrue(pathOrderMatch is AdminQrLookupResult.OrderMatch)
    assertEquals("ORD-1001", (pathOrderMatch as AdminQrLookupResult.OrderMatch).order.id)

    val trackingMatch = AdminQrLookupHelper.lookup("https://sajgoj.nfbd.workers.dev/?tracking=TRK-991", products, orders)
    assertTrue(trackingMatch is AdminQrLookupResult.OrderMatch)
    assertEquals("ORD-1001", (trackingMatch as AdminQrLookupResult.OrderMatch).order.id)
  }

  @Test
  fun manifest_registersAppLinksForSajgojWorkersDevDomain() {
    val context = ApplicationProvider.getApplicationContext<android.content.Context>()
    val pm = context.packageManager

    listOf(
      "https://sajgoj.nfbd.workers.dev/",
      "https://sajgoj.nfbd.workers.dev/#product-p1",
      "http://sajgoj.nfbd.workers.dev/?order=ORD-1001"
    ).forEach { rawUrl ->
      val viewIntent = Intent(Intent.ACTION_VIEW, Uri.parse(rawUrl)).apply {
        addCategory(Intent.CATEGORY_BROWSABLE)
        `package` = context.packageName
      }
      val resolved = pm.queryIntentActivities(viewIntent, PackageManager.MATCH_DEFAULT_ONLY)
      val mainMatch = resolved.firstOrNull { it.activityInfo?.name == MainActivity::class.java.name }
      assertNotNull("Expected MainActivity to resolve $rawUrl", mainMatch)
    }
  }
}
