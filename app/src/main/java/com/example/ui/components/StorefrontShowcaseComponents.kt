package com.example.ui.components

import android.media.MediaPlayer
import android.net.Uri
import android.widget.VideoView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.HomeSectionConfig
import com.example.data.model.Product
import com.example.data.model.ReelItem
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.BorderGold
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.DarkEspresso
import com.example.ui.theme.RoyalMaroon

fun resolveReelLinkedProduct(reel: ReelItem, products: List<Product>): Product {
  val found = products.find { it.id == reel.productId }
  return found ?: Product(
    id = reel.productId.ifBlank { reel.id },
    name = reel.title,
    price = reel.price.takeIf { it > 0 } ?: 1490,
    stock = 12,
    category = "রিলস কালেকশন",
    imageUrl = reel.posterUrl,
    sku = reel.id.uppercase(),
    description = reel.caption.ifBlank { reel.title },
    rating = 4.9f,
    reviewsCount = reel.likes.coerceAtLeast(15)
  )
}

@Composable
fun StorefrontReelsBlockCard(
  section: HomeSectionConfig,
  reels: List<ReelItem>,
  products: List<Product>,
  isExpanded: Boolean,
  onSeeMoreClick: () -> Unit,
  onReelClick: (ReelItem) -> Unit,
  onAddToCart: (Product) -> Unit,
  modifier: Modifier = Modifier
) {
  val activeReels = remember(reels) { reels.filter { it.active } }
  val defaultLimit = when (section.layout) {
    "grid3x2" -> 6
    "grid3x3" -> 9
    else -> section.maxItems.coerceAtLeast(3)
  }
  val displayReels = remember(activeReels, isExpanded, section.layout, defaultLimit) {
    if (isExpanded) activeReels
    else if (section.layout == "slider3") activeReels.take(section.maxItems.coerceAtLeast(6))
    else activeReels.take(defaultLimit)
  }

  Surface(
    shape = RoundedCornerShape(16.dp),
    color = Color.White,
    border = BorderStroke(1.dp, BorderGold.copy(alpha = 0.7f)),
    shadowElevation = 1.dp,
    modifier = modifier
      .fillMaxWidth()
      .testTag("home_section_${section.id}")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      // Section Header matching all Home Page product blocks
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.weight(1f)
        ) {
          Text(text = section.icon.ifBlank { "🎬" }, fontSize = 15.sp)
          Text(
            text = section.title.ifBlank { "রিলস ভিডিও শপ" },
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            color = RoyalMaroon,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFFFEBEE),
            border = BorderStroke(1.dp, Color(0xFFEF4444))
          ) {
            Text(
              text = section.badge.ifBlank { "REELS 🎥" },
              fontSize = 8.5.sp,
              fontWeight = FontWeight.Black,
              color = Color(0xFFC62828),
              modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
            )
          }
        }

        if (activeReels.isNotEmpty()) {
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .clickable { onSeeMoreClick() }
              .padding(horizontal = 6.dp, vertical = 3.dp)
              .testTag("see_more_${section.id}"),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = if (isExpanded) "কম দেখুন ▴" else "আরো দেখুন ›",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = RoyalMaroon
            )
          }
        }
      }

      if (displayReels.isEmpty()) {
        Text(
          text = "এখনো কোনো রিলস ভিডিও যুক্ত করা হয়নি।",
          fontSize = 11.sp,
          color = Color.Gray,
          modifier = Modifier.padding(vertical = 8.dp)
        )
      } else if (!isExpanded && section.layout == "slider3") {
        // Exact 3-card width matching StorefrontProductBlockCard
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
          val cardWidth = (maxWidth - 16.dp) / 3f
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(displayReels, key = { "${section.id}_${it.id}" }) { reel ->
              val linkedProduct = remember(reel, products) { resolveReelLinkedProduct(reel, products) }
              CompactReelVideoCard(
                reel = reel,
                linkedProduct = linkedProduct,
                onVideoClick = { onReelClick(reel) },
                onAddToCart = { onAddToCart(linkedProduct) },
                modifier = Modifier.width(cardWidth)
              )
            }
          }
        }
      } else {
        // 3-Column Grid View matching all product grids
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          displayReels.chunked(3).forEach { rowReels ->
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              rowReels.forEach { reel ->
                val linkedProduct = remember(reel, products) { resolveReelLinkedProduct(reel, products) }
                CompactReelVideoCard(
                  reel = reel,
                  linkedProduct = linkedProduct,
                  onVideoClick = { onReelClick(reel) },
                  onAddToCart = { onAddToCart(linkedProduct) },
                  modifier = Modifier.weight(1f)
                )
              }
              repeat(3 - rowReels.size) {
                Spacer(modifier = Modifier.weight(1f))
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun CompactReelVideoCard(
  reel: ReelItem,
  linkedProduct: Product,
  onVideoClick: () -> Unit,
  onAddToCart: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .clickable { onVideoClick() }
      .testTag("compact_reel_card_${reel.id}"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.dp, BorderGold),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
  ) {
    Column {
      // Top 1:1 Media Thumbnail + Play Overlay (Same aspectRatio as CompactProductCard)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(1f)
          .background(Color(0xFF1A0A0A))
      ) {
        AppImage(
          model = reel.posterUrl.ifBlank { linkedProduct.imageUrl },
          contentDescription = reel.title,
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
        )

        // Subtle dark scrim for play button legibility
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                listOf(Color.Black.copy(alpha = 0.25f), Color.Transparent, Color.Black.copy(alpha = 0.45f))
              )
            )
        )

        // Top-left REELS badge
        Box(
          modifier = Modifier
            .padding(4.dp)
            .align(Alignment.TopStart)
            .clip(RoundedCornerShape(6.dp))
            .background(RoyalMaroon.copy(alpha = 0.9f))
            .padding(horizontal = 4.dp, vertical = 1.dp)
        ) {
          Text(
            text = "▶ REEL",
            color = Color.White,
            fontSize = 7.5.sp,
            fontWeight = FontWeight.Black
          )
        }

        // Top-right Views + Yellow Rating Pill
        Box(
          modifier = Modifier
            .padding(4.dp)
            .align(Alignment.TopEnd)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black.copy(alpha = 0.65f))
            .padding(horizontal = 4.dp, vertical = 1.dp)
        ) {
          Text(
            text = "👁 ${reel.views}",
            color = Color.White,
            fontSize = 7.5.sp,
            fontWeight = FontWeight.Bold
          )
        }

        // Centered Play Circle
        Box(
          modifier = Modifier
            .size(34.dp)
            .align(Alignment.Center)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.6f))
            .border(1.5.dp, Color.White.copy(alpha = 0.9f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "ভিডিও চালান",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
        }

        // Bottom-left Yellow Star Rating Pill
        Row(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(4.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color.Black.copy(alpha = 0.65f))
            .padding(horizontal = 4.dp, vertical = 1.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = Color(0xFFFACC15),
            modifier = Modifier.size(8.5.dp)
          )
          Text(
            text = "${linkedProduct.rating}",
            color = Color(0xFFFACC15),
            fontSize = 8.sp,
            fontWeight = FontWeight.Black
          )
        }
      }

      // Bottom Info Box: Title + Price + Add to Cart Button
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 6.dp, vertical = 6.dp)
      ) {
        Text(
          text = linkedProduct.name.ifBlank { reel.title },
          color = DarkEspresso,
          fontSize = 10.5.sp,
          fontWeight = FontWeight.SemiBold,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          lineHeight = 13.sp,
          modifier = Modifier.height(27.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "৳${linkedProduct.price}",
            color = RoyalMaroon,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            maxLines = 1
          )

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (linkedProduct.stock > 0) RoyalMaroon else Color(0xFFE0E0E0),
            modifier = Modifier
              .clickable(enabled = linkedProduct.stock > 0) { onAddToCart() }
              .testTag("reel_add_to_cart_${reel.id}")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
              Icon(
                imageVector = Icons.Default.AddShoppingCart,
                contentDescription = "কার্টে যোগ করুন",
                tint = if (linkedProduct.stock > 0) Color.White else Color.Gray,
                modifier = Modifier.size(10.dp)
              )
              Text(
                text = if (linkedProduct.stock > 0) "কার্ট" else "শেষ",
                color = if (linkedProduct.stock > 0) Color.White else Color.Gray,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun ReelVideoPlayerDialog(
  initialReel: ReelItem,
  allReels: List<ReelItem>,
  products: List<Product>,
  onDismiss: () -> Unit,
  onLikeReel: (String) -> Unit,
  onAddToCart: (Product) -> Unit,
  onProductClick: (Product) -> Unit
) {
  BackHandler { onDismiss() }

  val activeReels = remember(allReels) { allReels.filter { it.active }.ifEmpty { listOf(initialReel) } }
  var currentReelId by remember(initialReel.id) { mutableStateOf(initialReel.id) }
  val currentIndex = remember(activeReels, currentReelId) {
    activeReels.indexOfFirst { it.id == currentReelId }.takeIf { it >= 0 } ?: 0
  }
  val currentReel = activeReels[currentIndex]
  val linkedProduct = remember(currentReel, products) { resolveReelLinkedProduct(currentReel, products) }

  var isPlaying by remember(currentReel.id) { mutableStateOf(true) }
  var isMuted by remember { mutableStateOf(false) }
  var isBuffering by remember(currentReel.id) { mutableStateOf(true) }
  var videoAspectRatio by remember(currentReel.id) { mutableFloatStateOf(9f / 16f) }
  var mediaPlayerRef by remember(currentReel.id) { mutableStateOf<MediaPlayer?>(null) }
  var videoViewRef by remember(currentReel.id) { mutableStateOf<VideoView?>(null) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp, vertical = 14.dp)
        .testTag("reel_original_player_dialog"),
      shape = RoundedCornerShape(22.dp),
      color = Color(0xFF0E0507),
      border = BorderStroke(1.5.dp, AntiqueGold.copy(alpha = 0.65f))
    ) {
      Column(
        modifier = Modifier.fillMaxWidth()
      ) {
        // Top Header Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1A0A0E))
            .padding(horizontal = 12.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
          ) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = RoyalMaroon
            ) {
              Text(
                text = currentReel.badge.ifBlank { "REELS 🎬" },
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
              )
            }
            Text(
              text = currentReel.title,
              color = Color.White,
              fontSize = 12.5.sp,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = {
                isMuted = !isMuted
                try {
                  val vol = if (isMuted) 0f else 1f
                  mediaPlayerRef?.setVolume(vol, vol)
                } catch (_: Exception) {
                }
              },
              modifier = Modifier.minimumInteractiveComponentSize()
            ) {
              Icon(
                imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                contentDescription = if (isMuted) "Unmute" else "Mute",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }

            IconButton(
              onClick = onDismiss,
              modifier = Modifier
                .minimumInteractiveComponentSize()
                .testTag("close_reel_player_button")
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "বন্ধ করুন",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
              )
            }
          }
        }

        // Original Size Uncropped Video Canvas (dynamically maintains true natural videoWidth/videoHeight ratio)
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 240.dp, max = 460.dp)
            .background(Color.Black),
          contentAlignment = Alignment.Center
        ) {
          // Poster fallback visible while video loads
          AppImage(
            model = currentReel.posterUrl.ifBlank { linkedProduct.imageUrl },
            contentDescription = currentReel.title,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
          )

          if (currentReel.videoUrl.isNotBlank()) {
            AndroidView(
              factory = { ctx ->
                VideoView(ctx).apply {
                  videoViewRef = this
                  setVideoURI(Uri.parse(currentReel.videoUrl))
                  setOnPreparedListener { mp ->
                    mediaPlayerRef = mp
                    isBuffering = false
                    mp.isLooping = true
                    val vol = if (isMuted) 0f else 1f
                    mp.setVolume(vol, vol)
                    if (mp.videoWidth > 0 && mp.videoHeight > 0) {
                      videoAspectRatio = (mp.videoWidth.toFloat() / mp.videoHeight.toFloat()).coerceIn(0.45f, 2.2f)
                    }
                    mp.setOnVideoSizeChangedListener { _, width, height ->
                      if (width > 0 && height > 0) {
                        videoAspectRatio = (width.toFloat() / height.toFloat()).coerceIn(0.45f, 2.2f)
                      }
                    }
                    if (isPlaying) start()
                  }
                  setOnErrorListener { _, _, _ ->
                    isBuffering = false
                    true
                  }
                }
              },
              update = { view ->
                val tagUrl = view.tag as? String
                if (tagUrl != currentReel.videoUrl) {
                  view.tag = currentReel.videoUrl
                  isBuffering = true
                  view.setVideoURI(Uri.parse(currentReel.videoUrl))
                  if (isPlaying) view.start()
                }
              },
              modifier = Modifier
                .aspectRatio(videoAspectRatio)
                .heightIn(max = 460.dp)
                .clickable {
                  val vv = videoViewRef
                  if (vv != null) {
                    if (vv.isPlaying) {
                      vv.pause()
                      isPlaying = false
                    } else {
                      vv.start()
                      isPlaying = true
                    }
                  }
                }
            )
          }

          if (isBuffering) {
            CircularProgressIndicator(
              color = AntiqueGold,
              modifier = Modifier.size(36.dp)
            )
          }

          if (!isPlaying && !isBuffering) {
            Box(
              modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.65f))
                .border(2.dp, Color.White, CircleShape)
                .clickable {
                  videoViewRef?.start()
                  isPlaying = true
                },
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                tint = Color.White,
                modifier = Modifier.size(34.dp)
              )
            }
          }

          // Floating Overlay Controls: Like + Prev/Next + Play/Pause
          Column(
            modifier = Modifier
              .align(Alignment.BottomEnd)
              .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Surface(
              shape = CircleShape,
              color = Color.Black.copy(alpha = 0.6f),
              modifier = Modifier
                .minimumInteractiveComponentSize()
                .clickable { onLikeReel(currentReel.id) }
            ) {
              Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Icon(
                  imageVector = if (currentReel.userLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                  contentDescription = "Like",
                  tint = if (currentReel.userLiked) Color(0xFFEF4444) else Color.White,
                  modifier = Modifier.size(20.dp)
                )
                Text(
                  text = "${currentReel.likes}",
                  color = Color.White,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            Surface(
              shape = CircleShape,
              color = Color.Black.copy(alpha = 0.6f),
              modifier = Modifier
                .minimumInteractiveComponentSize()
                .clickable {
                  val vv = videoViewRef
                  if (vv != null) {
                    if (vv.isPlaying) {
                      vv.pause()
                      isPlaying = false
                    } else {
                      vv.start()
                      isPlaying = true
                    }
                  }
                }
            ) {
              Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = Color.White,
                modifier = Modifier.padding(10.dp).size(20.dp)
              )
            }
          }

          if (activeReels.size > 1) {
            Row(
              modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(10.dp),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.6f),
                modifier = Modifier
                  .minimumInteractiveComponentSize()
                  .clickable {
                    val prevIdx = (currentIndex - 1 + activeReels.size) % activeReels.size
                    currentReelId = activeReels[prevIdx].id
                  }
              ) {
                Icon(
                  imageVector = Icons.Default.SkipPrevious,
                  contentDescription = "আগের ভিডিও",
                  tint = Color.White,
                  modifier = Modifier.padding(8.dp).size(20.dp)
                )
              }

              Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.6f),
                modifier = Modifier
                  .minimumInteractiveComponentSize()
                  .clickable {
                    val nextIdx = (currentIndex + 1) % activeReels.size
                    currentReelId = activeReels[nextIdx].id
                  }
              ) {
                Icon(
                  imageVector = Icons.Default.SkipNext,
                  contentDescription = "পরের ভিডিও",
                  tint = Color.White,
                  modifier = Modifier.padding(8.dp).size(20.dp)
                )
              }
            }
          }
        }

        // Bottom Shoppable Product Card (Price + Add to Cart + Quick Order)
        Surface(
          color = Color.White,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              AppImage(
                model = linkedProduct.imageUrl.ifBlank { currentReel.posterUrl },
                contentDescription = linkedProduct.name,
                modifier = Modifier
                  .size(54.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .border(1.dp, BorderGold, RoundedCornerShape(10.dp))
              )
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = linkedProduct.name,
                  color = DarkEspresso,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  maxLines = 2,
                  overflow = TextOverflow.Ellipsis
                )
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Text(
                    text = "৳${linkedProduct.price}",
                    color = RoyalMaroon,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                  )
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.Star,
                      contentDescription = null,
                      tint = Color(0xFFFACC15),
                      modifier = Modifier.size(13.dp)
                    )
                    Text(
                      text = "${linkedProduct.rating}",
                      color = Color(0xFFB45309),
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              }
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Button(
                onClick = { onAddToCart(linkedProduct) },
                enabled = linkedProduct.stock > 0,
                colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .weight(1f)
                  .height(44.dp)
                  .testTag("reel_modal_add_to_cart")
              ) {
                Icon(
                  imageVector = Icons.Default.AddShoppingCart,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = if (linkedProduct.stock > 0) "🛒 কার্টে নিন" else "স্টক শেষ",
                  fontSize = 12.5.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              OutlinedButton(
                onClick = {
                  onDismiss()
                  onProductClick(linkedProduct)
                },
                border = BorderStroke(1.dp, RoyalMaroon),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .weight(1f)
                  .height(44.dp)
                  .testTag("reel_modal_view_product")
              ) {
                Text(
                  text = "⚡ বিস্তারিত / অর্ডার",
                  color = RoyalMaroon,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun FullSectionCollectionDialog(
  section: HomeSectionConfig,
  sectionProducts: List<Product>,
  onDismiss: () -> Unit,
  onProductClick: (Product) -> Unit,
  onAddToCart: (Product) -> Unit
) {
  BackHandler { onDismiss() }

  var sortBy by remember(section.id) { mutableStateOf("featured") }
  val sortedProducts = remember(sectionProducts, sortBy) {
    when (sortBy) {
      "price_asc" -> sectionProducts.sortedBy { it.price }
      "price_desc" -> sectionProducts.sortedByDescending { it.price }
      "rating" -> sectionProducts.sortedByDescending { it.rating }
      else -> sectionProducts
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 8.dp, vertical = 16.dp)
        .testTag("full_collection_dialog_${section.id}"),
      shape = RoundedCornerShape(22.dp),
      color = CreamSurface,
      border = BorderStroke(1.5.dp, BorderGold)
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 14.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
          ) {
            Text(text = section.icon.ifBlank { "🛍️" }, fontSize = 18.sp)
            Column {
              Text(
                text = section.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = RoyalMaroon,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Text(
                text = "মোট ${sortedProducts.size} টি পণ্য পাওয়া গেছে",
                fontSize = 11.sp,
                color = Color.Gray
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .minimumInteractiveComponentSize()
              .testTag("close_collection_dialog")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "বন্ধ করুন",
              tint = RoyalMaroon
            )
          }
        }

        // Sort Filter Strip
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          val sortOptions = listOf(
            "featured" to "জনপ্রিয়",
            "price_asc" to "কম দাম ৳",
            "price_desc" to "বেশি দাম ৳",
            "rating" to "টপ রেটিং ★"
          )
          sortOptions.forEach { (key, label) ->
            val selected = sortBy == key
            Surface(
              shape = RoundedCornerShape(14.dp),
              color = if (selected) RoyalMaroon else CreamSurface,
              border = BorderStroke(1.dp, if (selected) RoyalMaroon else BorderGold),
              modifier = Modifier.clickable { sortBy = key }
            ) {
              Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (selected) Color.White else DarkEspresso,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
              )
            }
          }
        }

        HorizontalDivider(color = BorderGold.copy(alpha = 0.5f))

        LazyColumn(
          contentPadding = PaddingValues(10.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxSize()
        ) {
          items(sortedProducts.chunked(3)) { rowProducts ->
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              rowProducts.forEach { prod ->
                CompactProductCard(
                  product = prod,
                  onProductClick = {
                    onDismiss()
                    onProductClick(it)
                  },
                  onAddToCart = onAddToCart,
                  modifier = Modifier.weight(1f)
                )
              }
              repeat(3 - rowProducts.size) {
                Spacer(modifier = Modifier.weight(1f))
              }
            }
          }
        }
      }
    }
  }
}
