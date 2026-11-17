package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.VideoView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Product
import com.example.ui.theme.RoyalMaroon
import com.example.util.ProductMediaHelper
import com.example.util.ProductMediaKind

@Composable
fun ProductMediaViewerDialog(
  product: Product,
  initialIndex: Int = 0,
  onDismiss: () -> Unit,
  onOpenProductDetail: (Product) -> Unit,
  onAddToCart: (Product) -> Unit
) {
  BackHandler { onDismiss() }

  val context = LocalContext.current
  val gallery = remember(product) { ProductMediaHelper.buildMediaGallery(product) }
  var currentIndex by remember(product.id, initialIndex) {
    mutableIntStateOf(initialIndex.coerceIn(0, (gallery.size - 1).coerceAtLeast(0)))
  }

  val activeItem = gallery.getOrNull(currentIndex) ?: gallery.firstOrNull()

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      color = Color(0xFF09090B),
      modifier = Modifier
        .fillMaxSize()
        .testTag("product_media_viewer_dialog")
    ) {
      Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        // Top Header Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.85f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Surface(
                shape = RoundedCornerShape(50),
                color = if (activeItem?.isVideo == true) Color(0xFFEA580C) else RoyalMaroon
              ) {
                Text(
                  text = if (activeItem?.isVideo == true) "🎬 ${activeItem.label}" else "🖼️ অরিজিনাল ইমেজ",
                  color = Color.White,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Black,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
              }
              Text(
                text = "${currentIndex + 1} / ${gallery.size.coerceAtLeast(1)}",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = product.name,
              color = Color.White,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }

          Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedButton(
              onClick = {
                onDismiss()
                onOpenProductDetail(product)
              },
              border = BorderStroke(1.dp, Color.White.copy(alpha = 0.45f)),
              shape = RoundedCornerShape(50),
              modifier = Modifier.testTag("viewer_open_detail_btn")
            ) {
              Text("পণ্যটি দেখুন", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            IconButton(
              onClick = onDismiss,
              modifier = Modifier
                .size(48.dp)
                .testTag("viewer_close_btn")
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "বন্ধ করুন",
                tint = Color.White
              )
            }
          }
        }

        // Center Uncropped Original-Size Display Area
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .background(Color.Black),
          contentAlignment = Alignment.Center
        ) {
          if (activeItem != null) {
            key(currentIndex, activeItem.url) {
              when (activeItem.kind) {
                ProductMediaKind.VIDEO_UPLOAD -> {
                  OriginalSizeNativeVideoPlayer(
                    videoUrl = activeItem.url,
                    modifier = Modifier.fillMaxSize()
                  )
                }

                ProductMediaKind.VIDEO_YOUTUBE,
                ProductMediaKind.VIDEO_FACEBOOK_REELS -> {
                  Column(
                    modifier = Modifier
                      .fillMaxSize()
                      .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                  ) {
                    val ratio = if (activeItem.isPortrait) 9f / 16f else 16f / 9f
                    Box(
                      modifier = Modifier
                        .weight(1f, fill = false)
                        .fillMaxWidth(if (activeItem.isPortrait) 0.78f else 1f)
                        .aspectRatio(ratio)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.Black)
                    ) {
                      OriginalEmbedWebView(
                        embedUrl = activeItem.embedUrl.ifBlank { activeItem.url },
                        modifier = Modifier.fillMaxSize()
                      )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                      onClick = {
                        try {
                          context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(activeItem.url)))
                        } catch (_: Exception) {}
                      },
                      colors = ButtonDefaults.buttonColors(
                        containerColor = if (activeItem.kind == ProductMediaKind.VIDEO_FACEBOOK_REELS) Color(0xFF1877F2) else Color(0xFFDC2626)
                      ),
                      shape = RoundedCornerShape(50)
                    ) {
                      Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                      )
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(
                        text = if (activeItem.kind == ProductMediaKind.VIDEO_FACEBOOK_REELS) "ফেইসবুক অ্যাপে দেখুন" else "YouTube অ্যাপে দেখুন",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                      )
                    }
                  }
                }

                ProductMediaKind.IMAGE -> {
                  AppImage(
                    model = activeItem.url,
                    contentDescription = product.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                      .fillMaxSize()
                      .padding(6.dp)
                  )
                }
              }
            }
          }

          if (gallery.size > 1) {
            IconButton(
              onClick = {
                currentIndex = if (currentIndex == 0) gallery.lastIndex else currentIndex - 1
              },
              modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 6.dp)
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.65f))
                .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "পূর্ববর্তী",
                tint = Color.White
              )
            }

            IconButton(
              onClick = {
                currentIndex = (currentIndex + 1) % gallery.size
              },
              modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 6.dp)
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.65f))
                .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "পরবর্তী",
                tint = Color.White
              )
            }
          }
        }

        // Footer Strip: [Video First, Images Next] + Quick Add to Cart
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.92f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          if (gallery.size > 1) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
              gallery.forEachIndexed { idx, item ->
                val isSelected = idx == currentIndex
                Box(
                  modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(
                      width = if (isSelected) 2.dp else 1.dp,
                      color = if (isSelected) Color(0xFFF97316) else Color.White.copy(alpha = 0.3f),
                      shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { currentIndex = idx }
                ) {
                  AppImage(
                    model = item.thumbUrl,
                    contentDescription = item.label,
                    modifier = Modifier.fillMaxSize()
                  )
                  if (item.isVideo) {
                    Box(
                      modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f)),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "ভিডিও",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                      )
                    }
                  }
                }
              }
            }
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(
                text = "৳${product.price}",
                color = Color(0xFFFB7185),
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
              )
              if (product.regularPrice > product.price) {
                Text(
                  text = "৳${product.regularPrice}",
                  color = Color.LightGray,
                  fontSize = 12.sp,
                  textDecoration = TextDecoration.LineThrough
                )
              }
            }

            Button(
              onClick = { onAddToCart(product) },
              enabled = product.stock > 0,
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
              shape = RoundedCornerShape(50),
              modifier = Modifier.height(48.dp)
            ) {
              Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (product.stock > 0) "কার্টে নিন" else "স্টক আউট",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun OriginalSizeNativeVideoPlayer(
  videoUrl: String,
  modifier: Modifier = Modifier
) {
  var videoAspectRatio by remember(videoUrl) { mutableFloatStateOf(9f / 16f) }
  var isMuted by remember(videoUrl) { mutableStateOf(false) }
  var isPlaying by remember(videoUrl) { mutableStateOf(true) }
  var videoViewRef by remember { mutableStateOf<VideoView?>(null) }
  var mediaPlayerRef by remember { mutableStateOf<MediaPlayer?>(null) }

  DisposableEffect(videoUrl) {
    onDispose {
      try {
        videoViewRef?.stopPlayback()
      } catch (_: Exception) {}
      mediaPlayerRef = null
    }
  }

  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    AndroidView(
      factory = { ctx ->
        VideoView(ctx).apply {
          videoViewRef = this
          setOnPreparedListener { mp ->
            mediaPlayerRef = mp
            mp.isLooping = true
            val vol = if (isMuted) 0f else 1f
            mp.setVolume(vol, vol)
            if (mp.videoWidth > 0 && mp.videoHeight > 0) {
              videoAspectRatio = mp.videoWidth.toFloat() / mp.videoHeight.toFloat()
            }
            if (isPlaying) start()
          }
          setVideoURI(Uri.parse(videoUrl))
        }
      },
      update = { view ->
        try {
          val vol = if (isMuted) 0f else 1f
          mediaPlayerRef?.setVolume(vol, vol)
          if (isPlaying && !view.isPlaying) view.start()
          else if (!isPlaying && view.isPlaying) view.pause()
        } catch (_: Exception) {}
      },
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(videoAspectRatio.coerceIn(0.35f, 2.5f))
    )

    Row(
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(12.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      IconButton(
        onClick = { isPlaying = !isPlaying },
        modifier = Modifier
          .size(48.dp)
          .clip(CircleShape)
          .background(Color.Black.copy(alpha = 0.65f))
      ) {
        Icon(
          imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
          contentDescription = if (isPlaying) "Pause" else "Play",
          tint = Color.White
        )
      }

      IconButton(
        onClick = { isMuted = !isMuted },
        modifier = Modifier
          .size(48.dp)
          .clip(CircleShape)
          .background(Color.Black.copy(alpha = 0.65f))
      ) {
        Icon(
          imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
          contentDescription = if (isMuted) "Unmute" else "Mute",
          tint = Color.White
        )
      }
    }
  }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun OriginalEmbedWebView(
  embedUrl: String,
  modifier: Modifier = Modifier
) {
  AndroidView(
    factory = { ctx ->
      WebView(ctx).apply {
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.mediaPlaybackRequiresUserGesture = false
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true
        webViewClient = WebViewClient()
        webChromeClient = WebChromeClient()
        loadUrl(embedUrl)
      }
    },
    update = { webView ->
      if (webView.url != embedUrl) {
        webView.loadUrl(embedUrl)
      }
    },
    modifier = modifier
  )
}
