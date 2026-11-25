package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.data.model.Product
import com.example.data.model.ProductDiscountHelper
import com.example.data.model.StoreDesignConfig
import com.example.ui.theme.AccentRed
import com.example.ui.theme.BorderGold
import com.example.ui.theme.ReelsRedBadge
import com.example.ui.theme.RoyalMaroon
import com.example.ui.theme.parseColorHex
import com.example.util.ProductMediaHelper

private val BrandOfferOrange = Color(0xFFF85606)
private val CleanCardBorder = Color(0xFFE5E7EB)
private val CleanTitleInk = Color(0xFF1F2937)
private val MutedRegularSlate = Color(0xFF64748B)

@Composable
fun CleanCardPriceRow(
  sellingPrice: Int,
  regularPrice: Int,
  isPercentDiscount: Boolean = false,
  discountPercent: Int = 0,
  design: StoreDesignConfig = StoreDesignConfig(),
  testTag: String? = null,
  modifier: Modifier = Modifier
) {
  val hasRegularDiscount = regularPrice > sellingPrice && sellingPrice > 0
  val computedPct = when {
    discountPercent in 1..99 -> discountPercent
    hasRegularDiscount && regularPrice > 0 ->
      (((regularPrice - sellingPrice) * 100) / regularPrice).coerceIn(1, 99)
    else -> 0
  }
  val showPercentStyle = when (design.cardPriceDiscountStyle) {
    "percent" -> computedPct > 0
    "crossed" -> false
    else -> isPercentDiscount && computedPct > 0
  }

  val offerColor = parseColorHex(design.cardOfferPriceColor, BrandOfferOrange)
  val regularColor = if (design.cardRegularPriceColor.equals("#0F294A", ignoreCase = true)) {
    MutedRegularSlate
  } else {
    parseColorHex(design.cardRegularPriceColor, MutedRegularSlate)
  }

  BoxWithConstraints(
    modifier = modifier
      .fillMaxWidth()
      .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
  ) {
    val isUltraNarrow = maxWidth < 96.dp
    val isCompact = maxWidth < 135.dp
    val offerFontSize = when {
      isUltraNarrow -> 13.5.sp
      isCompact -> 15.sp
      else -> 16.5.sp
    }
    val secondaryFontSize = when {
      isUltraNarrow -> 10.sp
      isCompact -> 11.5.sp
      else -> 12.sp
    }

    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Start,
      modifier = Modifier.fillMaxWidth()
    ) {
      if (showPercentStyle) {
        // % Discount Style: Bold large selling price followed by gap and -XX%
        Text(
          text = "৳$sellingPrice",
          color = offerColor,
          fontSize = offerFontSize,
          fontWeight = FontWeight.Black,
          maxLines = 1,
          softWrap = false,
          overflow = TextOverflow.Clip
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "-$computedPct%",
          color = offerColor,
          fontSize = secondaryFontSize,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          softWrap = false
        )
      } else if (hasRegularDiscount) {
        // Regular + Offer Style: Crossed regular price FIRST, horizontal gap, then bold larger offer price
        Text(
          text = "৳$regularPrice",
          color = regularColor,
          fontSize = secondaryFontSize,
          fontWeight = FontWeight.Medium,
          textDecoration = TextDecoration.LineThrough,
          maxLines = 1,
          softWrap = false
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "৳$sellingPrice",
          color = offerColor,
          fontSize = offerFontSize,
          fontWeight = FontWeight.Black,
          maxLines = 1,
          softWrap = false,
          overflow = TextOverflow.Clip
        )
      } else {
        Text(
          text = "৳$sellingPrice",
          color = offerColor,
          fontSize = offerFontSize,
          fontWeight = FontWeight.Black,
          maxLines = 1,
          softWrap = false,
          overflow = TextOverflow.Clip
        )
      }
    }
  }
}

@Composable
fun ProductCard(
  product: Product,
  onProductClick: (Product) -> Unit,
  onAddToCart: (Product) -> Unit,
  design: StoreDesignConfig = StoreDesignConfig(),
  isWishlisted: Boolean = false,
  onToggleWishlist: (() -> Unit)? = null,
  onMediaClick: ((Product) -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val hasActiveVideo = ProductMediaHelper.hasActiveVideo(product)
  val cardMediaModel = ProductMediaHelper.cardPreviewThumbUrl(product)
  val interactionSource = remember { MutableInteractionSource() }
  val isHovered by interactionSource.collectIsHoveredAsState()
  val isPressed by interactionSource.collectIsPressedAsState()
  val cardScale by animateFloatAsState(
    targetValue = when {
      isPressed -> 0.985f
      isHovered -> 1.02f
      else -> 1f
    },
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioNoBouncy,
      stiffness = Spring.StiffnessMediumLow
    ),
    label = "product_card_hover_scale"
  )
  val cardElevation by animateDpAsState(
    targetValue = if (isHovered) 8.dp else 2.dp,
    animationSpec = tween(durationMillis = 200),
    label = "product_card_hover_elevation"
  )

  Card(
    modifier = modifier
      .fillMaxWidth()
      .zIndex(if (isHovered) 1f else 0f)
      .graphicsLayer {
        scaleX = cardScale
        scaleY = cardScale
      }
      .hoverable(interactionSource = interactionSource)
      .clickable(
        interactionSource = interactionSource,
        indication = LocalIndication.current
      ) { onProductClick(product) }
      .testTag("product_card_${product.id}"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(if (isHovered) 1.2.dp else 0.8.dp, CleanCardBorder),
    elevation = CardDefaults.cardElevation(
      defaultElevation = cardElevation,
      hoveredElevation = 8.dp,
      pressedElevation = 3.dp
    )
  ) {
    Column {
      // Product Image with Video-First Thumbnail & Touch-to-Open Details
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(1f)
          .clickable {
            if (onMediaClick != null) onMediaClick(product) else onProductClick(product)
          }
          .testTag("product_card_media_${product.id}")
      ) {
        AppImage(
          model = cardMediaModel,
          contentDescription = product.name,
          modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
        )

        if (hasActiveVideo) {
          Box(
            modifier = Modifier
              .align(Alignment.Center)
              .size(38.dp)
              .clip(CircleShape)
              .background(Color.Black.copy(alpha = 0.58f))
              .border(1.5.dp, Color.White.copy(alpha = 0.9f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = "ভিডিও দেখুন",
              tint = Color.White,
              modifier = Modifier.size(20.dp)
            )
          }

          Surface(
            shape = RoundedCornerShape(50),
            color = Color.Black.copy(alpha = 0.78f),
            border = BorderStroke(0.8.dp, Color.White.copy(alpha = 0.35f)),
            modifier = Modifier
              .align(Alignment.BottomEnd)
              .padding(6.dp)
          ) {
            Text(
              text = "🎬 ভিডিও • ছবি",
              color = Color.White,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        if (product.stock == 0) {
          Box(
            modifier = Modifier
              .padding(8.dp)
              .align(Alignment.TopStart)
              .clip(RoundedCornerShape(6.dp))
              .background(AccentRed)
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = "স্টক আউট",
              color = Color.White,
              fontSize = 9.5.sp,
              fontWeight = FontWeight.Bold
            )
          }
        } else if (product.stock <= 5) {
          Box(
            modifier = Modifier
              .padding(8.dp)
              .align(Alignment.TopStart)
              .clip(RoundedCornerShape(6.dp))
              .background(Color(0xFFD97706))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = "বাকি: ${product.stock}",
              color = Color.White,
              fontSize = 9.5.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        if (onToggleWishlist != null) {
          Box(
            modifier = Modifier
              .padding(8.dp)
              .align(Alignment.TopEnd)
              .size(26.dp)
              .clip(CircleShape)
              .background(
                if (isWishlisted) parseColorHex(design.cardWishlistActiveBg, ReelsRedBadge)
                else parseColorHex(design.cardWishlistCircleBg, Color.White).copy(alpha = 0.92f)
              )
              .border(0.8.dp, CleanCardBorder, CircleShape)
              .clickable { onToggleWishlist() }
              .testTag("wishlist_btn_${product.id}"),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = if (isWishlisted) design.cardWishlistIcon.ifBlank { "❤️" } else "🤍",
              fontSize = 11.sp
            )
          }
        } else {
          Box(
            modifier = Modifier
              .padding(8.dp)
              .align(Alignment.TopEnd)
              .clip(RoundedCornerShape(10.dp))
              .background(Color.Black.copy(alpha = 0.6f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Rating",
                tint = Color(0xFFFACC15),
                modifier = Modifier.size(11.dp)
              )
              Text(
                text = product.rating.toString(),
                color = Color(0xFFFACC15),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      // Clean 2-Item Daraz-Style Lower Block: Product Title + Single-Line Price Row (No boxes, no bottom button)
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White)
          .padding(horizontal = 10.dp, vertical = 9.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
      ) {
        Text(
          text = product.name,
          color = CleanTitleInk,
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          lineHeight = 17.sp,
          modifier = Modifier
            .fillMaxWidth()
            .height(34.dp)
            .testTag("product_card_title_${product.id}")
        )

        CleanCardPriceRow(
          sellingPrice = product.price,
          regularPrice = product.regularPrice,
          isPercentDiscount = ProductDiscountHelper.isPercentDiscountMode(product),
          discountPercent = ProductDiscountHelper.effectiveDiscountPercent(product),
          design = design,
          testTag = "product_card_price_${product.id}"
        )
      }
    }
  }
}

@Composable
fun StorefrontCardLowerNavyBody(
  title: String,
  sellingPrice: Int,
  regularPrice: Int,
  inStock: Boolean,
  buttonTestTag: String,
  onActionClick: () -> Unit,
  design: StoreDesignConfig = StoreDesignConfig(),
  isPercentDiscount: Boolean = false,
  discountPercent: Int = 0,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(Color.White)
      .padding(horizontal = 8.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    // 1. Clean Product Title (No pill/bordered container)
    Text(
      text = title,
      color = CleanTitleInk,
      fontSize = 12.sp,
      fontWeight = FontWeight.Medium,
      maxLines = 2,
      overflow = TextOverflow.Ellipsis,
      lineHeight = 15.5.sp,
      modifier = Modifier
        .fillMaxWidth()
        .height(31.dp)
        .testTag("${buttonTestTag}_title")
    )

    // 2. Single-Line Price Row (No separate box, no bottom Details button)
    CleanCardPriceRow(
      sellingPrice = sellingPrice,
      regularPrice = regularPrice,
      isPercentDiscount = isPercentDiscount,
      discountPercent = discountPercent,
      design = design,
      testTag = "${buttonTestTag}_price_box"
    )
  }
}

@Composable
fun CompactProductCard(
  product: Product,
  onProductClick: (Product) -> Unit,
  onAddToCart: (Product) -> Unit,
  badgeText: String? = null,
  design: StoreDesignConfig = StoreDesignConfig(),
  isWishlisted: Boolean = false,
  onToggleWishlist: (() -> Unit)? = null,
  onMediaClick: ((Product) -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val hasActiveVideo = ProductMediaHelper.hasActiveVideo(product)
  val cardMediaModel = ProductMediaHelper.cardPreviewThumbUrl(product)
  val interactionSource = remember { MutableInteractionSource() }
  val isHovered by interactionSource.collectIsHoveredAsState()
  val isPressed by interactionSource.collectIsPressedAsState()
  val cardScale by animateFloatAsState(
    targetValue = when {
      isPressed -> 0.985f
      isHovered -> 1.02f
      else -> 1f
    },
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioNoBouncy,
      stiffness = Spring.StiffnessMediumLow
    ),
    label = "compact_card_hover_scale"
  )
  val cardElevation by animateDpAsState(
    targetValue = if (isHovered) 8.dp else 1.5.dp,
    animationSpec = tween(durationMillis = 200),
    label = "compact_card_hover_elevation"
  )

  Card(
    modifier = modifier
      .zIndex(if (isHovered) 1f else 0f)
      .graphicsLayer {
        scaleX = cardScale
        scaleY = cardScale
      }
      .hoverable(interactionSource = interactionSource)
      .clickable(
        interactionSource = interactionSource,
        indication = LocalIndication.current
      ) { onProductClick(product) }
      .testTag("compact_product_card_${product.id}"),
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(
      width = if (isHovered) 1.2.dp else 0.8.dp,
      color = CleanCardBorder
    ),
    elevation = CardDefaults.cardElevation(
      defaultElevation = cardElevation,
      hoveredElevation = 8.dp,
      pressedElevation = 3.dp
    )
  ) {
    Column {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(1f)
          .background(Color.White)
          .clickable {
            if (onMediaClick != null) onMediaClick(product) else onProductClick(product)
          }
          .testTag("compact_card_media_${product.id}")
      ) {
        AppImage(
          model = cardMediaModel,
          contentDescription = product.name,
          modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
        )

        if (hasActiveVideo) {
          Box(
            modifier = Modifier
              .align(Alignment.Center)
              .size(34.dp)
              .clip(CircleShape)
              .background(Color.Black.copy(alpha = 0.58f))
              .border(1.2.dp, Color.White.copy(alpha = 0.9f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = "ভিডিও দেখুন",
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
          }

          Surface(
            shape = RoundedCornerShape(50),
            color = Color.Black.copy(alpha = 0.78f),
            border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.35f)),
            modifier = Modifier
              .align(Alignment.BottomEnd)
              .padding(4.dp)
          ) {
            Text(
              text = "🎬 ভিডিও",
              color = Color.White,
              fontSize = 8.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
            )
          }
        }

        if (product.stock == 0) {
          Box(
            modifier = Modifier
              .padding(4.dp)
              .align(Alignment.TopStart)
              .clip(RoundedCornerShape(6.dp))
              .background(AccentRed)
              .padding(horizontal = 4.dp, vertical = 1.dp)
          ) {
            Text(
              text = "শেষ",
              color = Color.White,
              fontSize = 8.sp,
              fontWeight = FontWeight.Bold
            )
          }
        } else if (product.stock <= 5) {
          Box(
            modifier = Modifier
              .padding(4.dp)
              .align(Alignment.TopStart)
              .clip(RoundedCornerShape(6.dp))
              .background(Color(0xFFD97706))
              .padding(horizontal = 4.dp, vertical = 1.dp)
          ) {
            Text(
              text = "বাকি ${product.stock}",
              color = Color.White,
              fontSize = 8.sp,
              fontWeight = FontWeight.Bold
            )
          }
        } else if (!badgeText.isNullOrBlank()) {
          Box(
            modifier = Modifier
              .padding(4.dp)
              .align(Alignment.TopStart)
              .clip(RoundedCornerShape(6.dp))
              .background(RoyalMaroon)
              .padding(horizontal = 4.dp, vertical = 1.dp)
          ) {
            Text(
              text = badgeText,
              color = Color.White,
              fontSize = 8.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        if (onToggleWishlist != null) {
          Box(
            modifier = Modifier
              .padding(4.dp)
              .align(Alignment.TopEnd)
              .size(24.dp)
              .clip(CircleShape)
              .background(
                if (isWishlisted) parseColorHex(design.cardWishlistActiveBg, ReelsRedBadge)
                else parseColorHex(design.cardWishlistCircleBg, Color.White).copy(alpha = 0.92f)
              )
              .border(1.dp, BorderGold.copy(alpha = 0.4f), CircleShape)
              .clickable { onToggleWishlist() }
              .testTag("wishlist_btn_${product.id}"),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = if (isWishlisted) design.cardWishlistIcon.ifBlank { "❤️" } else "🤍",
              fontSize = 10.sp
            )
          }
        }
      }

      StorefrontCardLowerNavyBody(
        title = product.name,
        sellingPrice = product.price,
        regularPrice = product.regularPrice,
        inStock = product.stock > 0,
        buttonTestTag = "compact_add_to_cart_${product.id}",
        onActionClick = { onProductClick(product) },
        design = design,
        isPercentDiscount = ProductDiscountHelper.isPercentDiscountMode(product),
        discountPercent = ProductDiscountHelper.effectiveDiscountPercent(product)
      )
    }
  }
}
