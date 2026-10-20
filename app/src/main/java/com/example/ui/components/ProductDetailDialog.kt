package com.example.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Product
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.AntiqueGoldDark
import com.example.ui.theme.BorderGold
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.DarkEspresso
import com.example.ui.theme.RoyalMaroon

@Composable
fun ProductDetailDialog(
  product: Product,
  onDismiss: () -> Unit,
  onAddToCart: (Product, Int) -> Unit
) {
  var quantity by remember { mutableIntStateOf(1) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = Color.White,
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .padding(vertical = 24.dp)
        .testTag("product_detail_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        // Top Header with Close
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
          Text(
            text = "পণ্যের বিবরণ",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = RoyalMaroon,
            modifier = Modifier.align(Alignment.CenterStart)
          )

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .align(Alignment.CenterEnd)
              .size(32.dp)
              .clip(CircleShape)
              .background(CreamSurface)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = DarkEspresso,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        // Product Large Image
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
        ) {
          AppImage(
            model = product.imageUrl,
            contentDescription = product.name,
            modifier = Modifier
              .fillMaxWidth()
              .aspectRatio(1.2f)
              .clip(RoundedCornerShape(18.dp))
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Product Details
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "${product.price} ৳",
              fontSize = 22.sp,
              fontWeight = FontWeight.Bold,
              color = RoyalMaroon
            )

            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp),
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(CreamSurface)
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = AntiqueGold,
                modifier = Modifier.size(14.dp)
              )
              Text(
                text = "${product.rating} (${product.reviewsCount} রিভিউ)",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkEspresso
              )
            }
          }

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = product.name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = DarkEspresso
          )

          Text(
            text = "SKU: ${product.sku}  |  ক্যাটাগরি: ${product.category}",
            fontSize = 11.sp,
            color = AntiqueGoldDark,
            fontWeight = FontWeight.Medium
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Description Card
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(CreamBackground)
              .padding(12.dp)
          ) {
            Text(
              text = product.description,
              fontSize = 13.sp,
              color = DarkEspresso,
              lineHeight = 18.sp
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Stock Info & Quantity Selector
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = if (product.stock > 0) "স্টক আছে (${product.stock} পিস)" else "স্টক আউট",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = if (product.stock > 0) Color(0xFF2E7D32) else Color.Red
            )

            if (product.stock > 0) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                  .clip(RoundedCornerShape(20.dp))
                  .border(1.dp, BorderGold, RoundedCornerShape(20.dp))
                  .padding(4.dp)
              ) {
                IconButton(
                  onClick = { if (quantity > 1) quantity-- },
                  modifier = Modifier.size(28.dp)
                ) {
                  Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                }

                Text(
                  text = quantity.toString(),
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 6.dp)
                )

                IconButton(
                  onClick = { if (quantity < product.stock) quantity++ },
                  modifier = Modifier.size(28.dp)
                ) {
                  Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Add to cart button
          Button(
            onClick = {
              onAddToCart(product, quantity)
              onDismiss()
            },
            enabled = product.stock > 0,
            colors = ButtonDefaults.buttonColors(
              containerColor = RoyalMaroon,
              contentColor = Color.White
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("detail_add_to_cart_button")
          ) {
            Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "কার্টে যোগ করুন (${product.price * quantity} ৳)",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(16.dp))
        }
      }
    }
  }
}
