package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Product
import com.example.ui.theme.AccentRed
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.AntiqueGoldDark
import com.example.ui.theme.BorderGold
import com.example.ui.theme.DarkEspresso
import com.example.ui.theme.RoyalMaroon

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
              tint = AntiqueGold,
              modifier = Modifier.size(11.dp)
            )
            Text(
              text = product.rating.toString(),
              color = Color.White,
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
        // Category Label
        Text(
          text = product.category.uppercase(),
          color = AntiqueGoldDark,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp
        )

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
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text(
              text = "${product.price} ৳",
              color = RoyalMaroon,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = product.sku,
              color = Color.Gray,
              fontSize = 9.sp
            )
          }

          Button(
            onClick = { onAddToCart(product) },
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
              text = if (product.stock > 0) "অর্ডার" else "শেষ",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}
