package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.OpenableColumns
import com.example.data.model.Product
import java.io.File
import java.io.FileOutputStream
import java.net.URLEncoder

enum class ProductMediaKind {
  VIDEO_UPLOAD,
  VIDEO_YOUTUBE,
  VIDEO_FACEBOOK_REELS,
  IMAGE
}

data class ParsedVideoInfo(
  val kind: ProductMediaKind = ProductMediaKind.VIDEO_UPLOAD,
  val videoId: String = "",
  val isPortrait: Boolean = false,
  val thumbUrl: String = "",
  val embedUrl: String = "",
  val playableUrl: String = "",
  val isValid: Boolean = false
)

data class ProductMediaItem(
  val kind: ProductMediaKind,
  val url: String,
  val thumbUrl: String,
  val embedUrl: String = "",
  val isPortrait: Boolean = false,
  val label: String
) {
  val isVideo: Boolean
    get() = kind != ProductMediaKind.IMAGE
}

object ProductMediaHelper {

  private val YOUTUBE_REGEX = Regex(
    """(?:youtube\.com/(?:watch\?(?:.*&)?v=|shorts/|embed/|live/)|youtu\.be/)([A-Za-z0-9_-]{11})"""
  )

  private val FACEBOOK_DOMAIN_REGEX = Regex(
    """(?:facebook\.com|fb\.watch|fb\.com)""",
    RegexOption.IGNORE_CASE
  )

  fun parseVideoSource(rawUrl: String, preferredType: String = "upload"): ParsedVideoInfo {
    val url = rawUrl.trim()
    if (url.isBlank()) {
      return ParsedVideoInfo(
        kind = when (preferredType) {
          "youtube" -> ProductMediaKind.VIDEO_YOUTUBE
          "facebook_reels" -> ProductMediaKind.VIDEO_FACEBOOK_REELS
          else -> ProductMediaKind.VIDEO_UPLOAD
        },
        isValid = false
      )
    }

    val ytMatch = YOUTUBE_REGEX.find(url)
    if (ytMatch != null) {
      val vid = ytMatch.groupValues[1]
      val isShorts = url.contains("/shorts/", ignoreCase = true)
      return ParsedVideoInfo(
        kind = ProductMediaKind.VIDEO_YOUTUBE,
        videoId = vid,
        isPortrait = isShorts,
        thumbUrl = "https://img.youtube.com/vi/$vid/hqdefault.jpg",
        embedUrl = "https://www.youtube.com/embed/$vid?autoplay=1&rel=0&playsinline=1",
        playableUrl = url,
        isValid = true
      )
    }

    if (FACEBOOK_DOMAIN_REGEX.containsMatchIn(url) || preferredType == "facebook_reels") {
      val encoded = try {
        URLEncoder.encode(url, "UTF-8")
      } catch (_: Exception) {
        url
      }
      val isValidHttp = url.startsWith("http://", ignoreCase = true) ||
        url.startsWith("https://", ignoreCase = true)
      return ParsedVideoInfo(
        kind = ProductMediaKind.VIDEO_FACEBOOK_REELS,
        isPortrait = true,
        embedUrl = "https://www.facebook.com/plugins/video.php?href=$encoded&show_text=false&autoplay=true&t=0",
        playableUrl = url,
        isValid = isValidHttp
      )
    }

    val isPlayableLocalOrDirect = url.startsWith("http://", ignoreCase = true) ||
      url.startsWith("https://", ignoreCase = true) ||
      url.startsWith("file://", ignoreCase = true) ||
      url.startsWith("content://", ignoreCase = true) ||
      url.startsWith("res://", ignoreCase = true) ||
      url.startsWith("/")

    return ParsedVideoInfo(
      kind = if (preferredType == "youtube") ProductMediaKind.VIDEO_YOUTUBE else ProductMediaKind.VIDEO_UPLOAD,
      playableUrl = url,
      isValid = isPlayableLocalOrDirect && preferredType != "youtube"
    )
  }

  /**
   * Builds the product media gallery in strict order:
   * [Video First (if enabled & non-blank), Main Image, Additional Images...]
   */
  fun buildMediaGallery(product: Product): List<ProductMediaItem> {
    val images = buildList {
      if (product.imageUrl.isNotBlank()) add(product.imageUrl.trim())
      product.additionalImages.filter { it.isNotBlank() && it.trim() != product.imageUrl.trim() }.forEach {
        add(it.trim())
      }
    }
    val primaryImg = images.firstOrNull().orEmpty()
    val items = mutableListOf<ProductMediaItem>()

    val cleanVideoUrl = product.videoUrl.trim()
    if (product.videoEnabled && cleanVideoUrl.isNotBlank()) {
      val parsed = parseVideoSource(cleanVideoUrl, product.videoType)
      val thumb = product.videoThumbUrl.trim().ifBlank { parsed.thumbUrl.ifBlank { primaryImg } }
      val label = when (parsed.kind) {
        ProductMediaKind.VIDEO_YOUTUBE -> "YouTube ভিডিও"
        ProductMediaKind.VIDEO_FACEBOOK_REELS -> "ফেইসবুক রীলস"
        else -> "প্রোডাক্ট ভিডিও"
      }
      items.add(
        ProductMediaItem(
          kind = parsed.kind,
          url = parsed.playableUrl.ifBlank { cleanVideoUrl },
          thumbUrl = thumb,
          embedUrl = parsed.embedUrl,
          isPortrait = parsed.isPortrait,
          label = label
        )
      )
    }

    images.forEachIndexed { index, imgUrl ->
      items.add(
        ProductMediaItem(
          kind = ProductMediaKind.IMAGE,
          url = imgUrl,
          thumbUrl = imgUrl,
          label = "ছবি ${index + 1}"
        )
      )
    }

    return items
  }

  fun hasActiveVideo(product: Product): Boolean =
    product.videoEnabled && product.videoUrl.isNotBlank()

  fun cardPreviewThumbUrl(product: Product): String {
    if (hasActiveVideo(product)) {
      val parsed = parseVideoSource(product.videoUrl, product.videoType)
      return product.videoThumbUrl.trim()
        .ifBlank { parsed.thumbUrl }
        .ifBlank { product.imageUrl }
    }
    return product.imageUrl
  }

  data class PickedVideoResult(
    val localVideoUri: String,
    val localThumbUri: String,
    val displayName: String
  )

  fun copyPickedVideoToInternalStorage(context: Context, uri: Uri): PickedVideoResult? {
    return try {
      val videoDir = File(context.filesDir, "product_videos").apply { mkdirs() }
      val thumbDir = File(context.filesDir, "product_video_thumbs").apply { mkdirs() }
      val displayName = queryDisplayName(context, uri).ifBlank { "product_video.mp4" }
      val ext = displayName.substringAfterLast('.', "mp4").lowercase().takeIf { it.length in 2..5 } ?: "mp4"
      val baseId = "pv_${System.currentTimeMillis()}"
      val destVideoFile = File(videoDir, "$baseId.$ext")

      context.contentResolver.openInputStream(uri)?.use { input ->
        FileOutputStream(destVideoFile).use { output ->
          input.copyTo(output)
        }
      } ?: return null

      var thumbUriStr = ""
      val retriever = MediaMetadataRetriever()
      try {
        retriever.setDataSource(destVideoFile.absolutePath)
        val frame = retriever.getFrameAtTime(200_000L, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
          ?: retriever.frameAtTime
        if (frame != null) {
          val destThumb = File(thumbDir, "$baseId.jpg")
          FileOutputStream(destThumb).use { out ->
            frame.compress(Bitmap.CompressFormat.JPEG, 85, out)
          }
          thumbUriStr = Uri.fromFile(destThumb).toString()
        }
      } catch (_: Exception) {
      } finally {
        try {
          retriever.release()
        } catch (_: Exception) {}
      }

      PickedVideoResult(
        localVideoUri = Uri.fromFile(destVideoFile).toString(),
        localThumbUri = thumbUriStr,
        displayName = displayName
      )
    } catch (_: Exception) {
      null
    }
  }

  private fun queryDisplayName(context: Context, uri: Uri): String {
    return try {
      context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
          val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
          if (idx >= 0) cursor.getString(idx).orEmpty() else ""
        } else ""
      } ?: ""
    } catch (_: Exception) {
      ""
    }
  }
}
