package com.example

import com.example.data.model.Product
import com.example.util.ProductMediaHelper
import com.example.util.ProductMediaKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductMediaHelperTest {

  private fun sampleProduct(
    videoEnabled: Boolean = false,
    videoType: String = "upload",
    videoUrl: String = "",
    videoThumbUrl: String = "",
    imageUrl: String = "https://cdn.example.com/main.jpg",
    additionalImages: List<String> = emptyList()
  ) = Product(
    id = "p_test",
    name = "জামদানি শাড়ি",
    price = 2500,
    stock = 10,
    category = "শাড়ি",
    imageUrl = imageUrl,
    sku = "SAJ-999",
    description = "টেস্ট",
    videoEnabled = videoEnabled,
    videoType = videoType,
    videoUrl = videoUrl,
    videoThumbUrl = videoThumbUrl,
    additionalImages = additionalImages
  )

  @Test
  fun youtubeLinksExtractVideoIdThumbnailAndEmbed() {
    val watch = ProductMediaHelper.parseVideoSource("https://www.youtube.com/watch?v=dQw4w9WgXcQ", "youtube")
    assertEquals(ProductMediaKind.VIDEO_YOUTUBE, watch.kind)
    assertEquals("dQw4w9WgXcQ", watch.videoId)
    assertEquals("https://img.youtube.com/vi/dQw4w9WgXcQ/hqdefault.jpg", watch.thumbUrl)
    assertTrue(watch.isValid)
    assertFalse(watch.isPortrait)

    val shorts = ProductMediaHelper.parseVideoSource("https://youtube.com/shorts/AbCdEf12345?feature=share", "youtube")
    assertEquals(ProductMediaKind.VIDEO_YOUTUBE, shorts.kind)
    assertEquals("AbCdEf12345", shorts.videoId)
    assertTrue(shorts.isPortrait)
  }

  @Test
  fun facebookReelsLinksAreDetectedAndEncodedIntoEmbed() {
    val fb = ProductMediaHelper.parseVideoSource("https://www.facebook.com/reel/1234567890123", "facebook_reels")
    assertEquals(ProductMediaKind.VIDEO_FACEBOOK_REELS, fb.kind)
    assertTrue(fb.isValid)
    assertTrue(fb.isPortrait)
    assertTrue(fb.embedUrl.contains("facebook.com/plugins/video.php"))
  }

  @Test
  fun buildMediaGalleryPlacesEnabledVideoFirstFollowedByImages() {
    val productWithVideo = sampleProduct(
      videoEnabled = true,
      videoType = "youtube",
      videoUrl = "https://youtu.be/dQw4w9WgXcQ",
      additionalImages = listOf("https://cdn.example.com/side.jpg")
    )

    val gallery = ProductMediaHelper.buildMediaGallery(productWithVideo)
    assertEquals(3, gallery.size)
    assertTrue("First media slot must be Video", gallery[0].isVideo)
    assertEquals(ProductMediaKind.VIDEO_YOUTUBE, gallery[0].kind)
    assertEquals(ProductMediaKind.IMAGE, gallery[1].kind)
    assertEquals("https://cdn.example.com/main.jpg", gallery[1].url)
    assertEquals(ProductMediaKind.IMAGE, gallery[2].kind)
    assertEquals("https://cdn.example.com/side.jpg", gallery[2].url)
  }

  @Test
  fun disabledVideoDoesNotAppearInGallery() {
    val productDisabledVideo = sampleProduct(
      videoEnabled = false,
      videoType = "facebook_reels",
      videoUrl = "https://www.facebook.com/reel/987654321"
    )

    val gallery = ProductMediaHelper.buildMediaGallery(productDisabledVideo)
    assertEquals(1, gallery.size)
    assertFalse(gallery[0].isVideo)
    assertEquals(ProductMediaKind.IMAGE, gallery[0].kind)
    assertFalse(ProductMediaHelper.hasActiveVideo(productDisabledVideo))
  }
}
