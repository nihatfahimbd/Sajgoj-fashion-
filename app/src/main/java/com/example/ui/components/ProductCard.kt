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
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import com.example.util.ProductMediaHelper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.data.model.Product
import com.example.data.model.StoreDesignConfig
import com.example.ui.theme.AccentRed
import com.example.ui.theme.AntiqueGoldDark
import com.example.ui.theme.BorderGold
import com.example.ui.theme.CollectionRedBorder
import com.example.ui.theme.CollectionRedEnd
import com.example.ui.theme.CollectionRedStart
import com.example.ui.theme.CreamPriceBorder
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.DarkEspresso
import com.example.ui.theme.NavyBlue
import com.example.ui.theme.NavyBlueCardEnd
import com.example.ui.theme.ReelsRedBadge
import com.example.ui.theme.RoyalMaroon
import com.example.ui.theme.SideRosePillEnd
import com.example.ui.theme.SideRosePillStart
import com.example.ui.theme.parseColorHex

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
      isHovered -> 1.03f
      else -> 1f
    },
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioNoBouncy,
      stiffness = Spring.StiffnessMediumLow
    ),
    label = "product_card_hover_scale"
  )
  val cardElevation by animateDpAsState(
    targetValue = if (isHovered) 10.dp else 2.dp,
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
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(if (isHovered) 1.5.dp else 1.dp, BorderGold),
    elevation = CardDefaults.cardElevation(
      defaultElevation = cardElevation,
      hoveredElevation = 10.dp,
      pressedElevation = 4.dp
    )
  ) {
    Column {
      // Product Image with Video-First Thumbnail & Touch-to-Original-Size
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
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
        )

        if (hasActiveVideo) {
          Box(
            modifier = Modifier
              .align(Alignment.Center)
              .size(40.dp)
              .clip(CircleShape)
              .background(Color.Black.copy(alpha = 0.58f))
              .border(1.5.dp, Color.White.copy(alpha = 0.9f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = "ভিডিও দেখুন",
              tint = Color.White,
              modifier = Modifier.size(22.dp)
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

        // Low stock / Stock out badge
        if (product.stock == 0) {
          Box(
            modifier = Modifier
              .padding(8.dp)
              .align(Alignment.TopStart)
              .clip(RoundedCornerShape(8.dp))
              .background(AccentRed)
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = "স্টক আউট",
              color = Color.White,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }
        } else if (product.stock <= 5) {
          Box(
            modifier = Modifier
              .padding(8.dp)
              .align(Alignment.TopStart)
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFFD97706))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = "স্টক বাকি: ${product.stock}",
              color = Color.White,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        // Rating Badge
        Box(
          modifier = Modifier
            .padding(8.dp)
            .align(Alignment.TopEnd)
            .clip(RoundedCornerShape(12.dp))
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

      // Information Block
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(10.dp)
      ) {
        // Category Label and Variant Badges
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = product.category.uppercase(),
            color = AntiqueGoldDark,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
          )

          val hasVariants = product.colors.isNotEmpty() || product.sizes.isNotEmpty() || product.ageGroups.isNotEmpty()
          if (hasVariants) {
            val variantLabel = when {
              product.ageGroups.isNotEmpty() -> "👶 বয়স"
              product.colors.isNotEmpty() && product.sizes.isNotEmpty() -> "🎨📏 ভ্যারিয়েন্ট"
              product.colors.isNotEmpty() -> "🎨 কালার"
              else -> "📏 সাইজ"
            }
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = CreamSurface,
              border = BorderStroke(1.dp, BorderGold.copy(alpha = 0.6f))
            ) {
              Text(
                text = variantLabel,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = RoyalMaroon,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Product Name
        Text(
          text = product.name,
          color = DarkEspresso,
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          lineHeight = 16.sp,
          modifier = Modifier.height(34.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Price & Quick Order Button
        val hasOptions = product.colors.isNotEmpty() || product.sizes.isNotEmpty() || product.ageGroups.isNotEmpty()
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(parseColorHex(design.cardPriceBg, CreamSurface))
              .border(1.dp, parseColorHex(design.cardPriceBorder, CreamPriceBorder), RoundedCornerShape(8.dp))
              .padding(horizontal = 8.dp, vertical = 3.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            if (product.regularPrice > product.price) {
              Text(
                text = "৳${product.regularPrice}",
                color = parseColorHex(design.cardRegularPriceColor, NavyBlue),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.LineThrough
              )
            }
            Text(
              text = "৳${product.price}",
              color = parseColorHex(design.cardOfferPriceColor, ReelsRedBadge),
              fontSize = 14.sp,
              fontWeight = FontWeight.Black
            )
            Text(
              text = product.sku,
              color = Color.Gray,
              fontSize = 8.5.sp
            )
          }

          Button(
            onClick = {
              if (hasOptions) {
                onProductClick(product)
              } else {
                onAddToCart(product)
              }
            },
            enabled = product.stock > 0,
            colors = ButtonDefaults.buttonColors(
              containerColor = RoyalMaroon,
              contentColor = Color.White,
              disabledContainerColor = Color(0xFFE0E0E0),
              disabledContentColor = Color.Gray
            ),
            shape = RoundedCornerShape(14.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier
              .height(34.dp)
              .testTag("add_to_cart_${product.id}")
          ) {
            Icon(
              imageVector = Icons.Default.AddShoppingCart,
              contentDescription = "Add",
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
              text = if (product.stock > 0) (if (hasOptions) "অপশন" else "অর্ডার") else "শেষ",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
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
  modifier: Modifier = Modifier
) {
  val hasDiscount = regularPrice > sellingPrice && sellingPrice > 0
  val bodyStart = parseColorHex(design.cardBodyBg, NavyBlue)
  val bodyEnd = parseColorHex(design.cardBodyBgEnd, NavyBlueCardEnd)
  val titleBg = parseColorHex(design.cardTitleBg, CollectionRedStart)
  val titleEnd = parseColorHex(design.cardTitleBgEnd, CollectionRedEnd)
  val titleText = parseColorHex(design.cardTitleText, Color.White)
  val priceBg = parseColorHex(design.cardPriceBg, CreamSurface)
  val priceBorder = parseColorHex(design.cardPriceBorder, CreamPriceBorder)
  val regularPriceColor = parseColorHex(design.cardRegularPriceColor, NavyBlue)
  val offerPriceColor = parseColorHex(design.cardOfferPriceColor, ReelsRedBadge)
  val btnBg = parseColorHex(design.cardBtnBg, SideRosePillStart)
  val btnEnd = parseColorHex(design.cardBtnBgEnd, SideRosePillEnd)
  val btnText = parseColorHex(design.cardBtnText, Color.White)
  val btnShape = when (design.cardBtnStyle) {
    "rounded" -> RoundedCornerShape(10.dp)
    "square" -> RoundedCornerShape(4.dp)
    else -> RoundedCornerShape(50)
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(Brush.verticalGradient(listOf(bodyStart, bodyEnd)))
      .padding(horizontal = 6.dp, vertical = 6.dp),
    verticalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    // 1. Title Box
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(Brush.horizontalGradient(listOf(titleBg, titleEnd)))
        .border(1.dp, CollectionRedBorder.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
        .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
      Text(
        text = title,
        color = titleText,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        lineHeight = 12.5.sp,
        modifier = Modifier
          .fillMaxWidth()
          .height(25.dp)
      )
    }

    // 2. Cream Price Box: Top Line Navy Blue Crossed Regular Price + Bottom Line Red Bold Offer Price
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(priceBg)
        .border(1.dp, priceBorder, RoundedCornerShape(8.dp))
        .padding(horizontal = 6.dp, vertical = 3.dp)
        .testTag("${buttonTestTag}_price_box"),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      if (hasDiscount) {
        Text(
          text = "৳$regularPrice",
          color = regularPriceColor,
          fontSize = 8.5.sp,
          fontWeight = FontWeight.Bold,
          textDecoration = TextDecoration.LineThrough,
          textAlign = TextAlign.Center,
          maxLines = 1
        )
      }
      Text(
        text = "৳$sellingPrice",
        color = offerPriceColor,
        fontSize = 11.5.sp,
        fontWeight = FontWeight.Black,
        textAlign = TextAlign.Center,
        maxLines = 1
      )
    }

    // 3. Action Button (supports pill / rounded / square / outline)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(btnShape)
        .then(
          if (design.cardBtnStyle == "outline") {
            Modifier
              .background(Color.Transparent)
              .border(1.5.dp, btnBg, btnShape)
          } else {
            Modifier
              .background(Brush.horizontalGradient(listOf(btnBg, btnEnd)))
              .border(1.dp, Color.White.copy(alpha = 0.72f), btnShape)
          }
        )
        .clickable { onActionClick() }
        .padding(vertical = 5.dp)
        .testTag(buttonTestTag),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = if (inStock) design.cardActionLabel.ifBlank { "পন্যটি দেখুন" } else "স্টক আউট",
        color = if (design.cardBtnStyle == "outline") btnBg else btnText,
        fontSize = 9.5.sp,
        fontWeight = FontWeight.Black,
        maxLines = 1
      )
    }
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
      isHovered -> 1.03f
      else -> 1f
    },
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioNoBouncy,
      stiffness = Spring.StiffnessMediumLow
    ),
    label = "compact_card_hover_scale"
  )
  val cardElevation by animateDpAsState(
    targetValue = if (isHovered) 10.dp else 2.dp,
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
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = parseColorHex(design.cardBodyBg, NavyBlue)),
    border = BorderStroke(
      width = if (isHovered) 1.5.dp else 1.dp,
      color = parseColorHex(design.cardBorderColor, Color.White).copy(alpha = if (isHovered) 0.95f else 0.75f)
    ),
    elevation = CardDefaults.cardElevation(
      defaultElevation = cardElevation,
      hoveredElevation = 10.dp,
      pressedElevation = 4.dp
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
            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
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
              .border(1.dp, BorderGold.copy(alpha = 0.5f), CircleShape)
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
        design = design
      )
    }
  }
}
