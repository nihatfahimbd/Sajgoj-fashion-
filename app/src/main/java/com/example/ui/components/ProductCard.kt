package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Product
import com.example.ui.theme.AccentRed
import com.example.ui.theme.AntiqueGoldDark
import com.example.ui.theme.BorderGold
import com.example.ui.theme.CollectionRedBorder
import com.example.ui.theme.CollectionRedEnd
import com.example.ui.theme.CollectionRedStart
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.DarkEspresso
import com.example.ui.theme.NavyBlue
import com.example.ui.theme.NavyBlueCardEnd
import com.example.ui.theme.RoyalMaroon
import com.example.ui.theme.SideRosePillEnd
import com.example.ui.theme.SideRosePillStart

@Composable
fun ProductCard(
  product: Product,
  onProductClick: (Product) -> Unit,
  onAddToCart: (Product) -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clickable { onProductClick(product) }
      .testTag("product_card_${product.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.dp, BorderGold),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column {
      // Product Image with Stock / Rating Badges
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(1f)
      ) {
        AppImage(
          model = product.imageUrl,
          contentDescription = product.name,
          modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
        )

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
          Column {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
              Text(
                text = "${product.price} ৳",
                color = RoyalMaroon,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
              )
              if (product.regularPrice > product.price) {
                Text(
                  text = "${product.regularPrice} ৳",
                  color = Color.Gray,
                  fontSize = 10.sp,
                  textDecoration = TextDecoration.LineThrough
                )
              }
            }
            Text(
              text = product.sku,
              color = Color.Gray,
              fontSize = 9.sp
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
  modifier: Modifier = Modifier
) {
  val hasDiscount = regularPrice > sellingPrice && sellingPrice > 0
  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(Brush.verticalGradient(listOf(NavyBlue, NavyBlueCardEnd)))
      .padding(horizontal = 6.dp, vertical = 6.dp),
    verticalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    // 1. Vivid Red "সকল কালেকশন" Style Title Box with White Text
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(Brush.horizontalGradient(listOf(CollectionRedStart, CollectionRedEnd)))
        .border(1.dp, CollectionRedBorder, RoundedCornerShape(8.dp))
        .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
      Text(
        text = title,
        color = Color.White,
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

    // 2. Tight Brand-Style Dual Price Box right under Title ("সকল কালেকশন" Red Style + White Text)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(Brush.horizontalGradient(listOf(CollectionRedStart, CollectionRedEnd)))
        .border(1.dp, CollectionRedBorder, RoundedCornerShape(8.dp))
        .padding(horizontal = 6.dp, vertical = 3.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      if (hasDiscount) {
        Text(
          text = "৳$regularPrice",
          color = Color.White.copy(alpha = 0.86f),
          fontSize = 8.5.sp,
          fontWeight = FontWeight.Bold,
          textDecoration = TextDecoration.LineThrough,
          maxLines = 1
        )
      } else {
        Spacer(modifier = Modifier.width(1.dp))
      }
      Text(
        text = "৳$sellingPrice",
        color = Color.White,
        fontSize = 11.5.sp,
        fontWeight = FontWeight.Black,
        maxLines = 1
      )
    }

    // 3. Rose-Red "পন্যটি দেখুন" Pill Button with White Text
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(50))
        .background(Brush.horizontalGradient(listOf(SideRosePillStart, SideRosePillEnd)))
        .border(1.dp, Color.White.copy(alpha = 0.72f), RoundedCornerShape(50))
        .clickable { onActionClick() }
        .padding(vertical = 5.dp)
        .testTag(buttonTestTag),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = if (inStock) "পন্যটি দেখুন" else "স্টক আউট",
        color = Color.White,
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
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .clickable { onProductClick(product) }
      .testTag("compact_product_card_${product.id}"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = NavyBlue),
    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.75f)),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(1f)
          .background(Color.White)
      ) {
        AppImage(
          model = product.imageUrl,
          contentDescription = product.name,
          modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
        )

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
      }

      StorefrontCardLowerNavyBody(
        title = product.name,
        sellingPrice = product.price,
        regularPrice = product.regularPrice,
        inStock = product.stock > 0,
        buttonTestTag = "compact_add_to_cart_${product.id}",
        onActionClick = { onProductClick(product) }
      )
    }
  }
}
