package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BannerItem
import com.example.data.model.Product
import com.example.data.model.StoreConfig
import com.example.ui.components.BannerCarousel
import com.example.ui.components.ProductCard
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.AntiqueGoldDark
import com.example.ui.theme.BorderGold
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.DarkEspresso
import com.example.ui.theme.RoyalMaroon

@Composable
fun StorefrontScreen(
  products: List<Product>,
  banners: List<BannerItem>,
  config: StoreConfig,
  onProductClick: (Product) -> Unit,
  onAddToCart: (Product) -> Unit,
  onBannerClick: (BannerItem) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  fun openWhatsApp() {
    try {
      val cleanNumber = config.whatsapp.replace("+", "").replace("-", "")
      val uri = Uri.parse("https://api.whatsapp.com/send?phone=$cleanNumber&text=আসসালামু%20আলাইকুম,%20আমি%20সজগোজ%20ফ্যাশন%20থেকে%20অর্ডার%20করতে%20চাই।")
      val intent = Intent(Intent.ACTION_VIEW, uri)
      context.startActivity(intent)
    } catch (e: Exception) {
      // Fallback to dialer
      val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${config.phone}"))
      context.startActivity(dialIntent)
    }
  }

  Box(modifier = modifier.fillMaxSize()) {
    LazyVerticalGrid(
      columns = GridCells.Fixed(2),
      contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 80.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
      modifier = Modifier
        .fillMaxSize()
        .testTag("storefront_grid")
    ) {
      // 1. Hero Banners Section
      item(span = { GridItemSpan(2) }) {
        BannerCarousel(
          banners = banners,
          onBannerClick = onBannerClick,
          modifier = Modifier.padding(bottom = 6.dp)
        )
      }

      // 2. Section Header
      item(span = { GridItemSpan(2) }) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "এক্সক্লুসিভ কালেকশন",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = RoyalMaroon
            )
            Text(
              text = "মোট ${products.size} টি প্রিমিয়াম পণ্য প্রদর্শিত হচ্ছে",
              fontSize = 11.sp,
              color = AntiqueGoldDark
            )
          }

          Surface(
            shape = RoundedCornerShape(12.dp),
            color = CreamSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderGold)
          ) {
            Text(
              text = "১০০% খাঁটি পণ্য",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = RoyalMaroon,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }

      // 3. Products
      if (products.isEmpty()) {
        item(span = { GridItemSpan(2) }) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(40.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "🔍", fontSize = 36.sp)
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "কোনো পণ্য খুঁজে পাওয়া যায়নি!",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = DarkEspresso
              )
              Text(
                text = "অন্য কোনো নাম বা ক্যাটাগরি দিয়ে চেষ্টা করুন।",
                fontSize = 12.sp,
                color = Color.Gray
              )
            }
          }
        }
      } else {
        items(products, key = { it.id }) { product ->
          ProductCard(
            product = product,
            onProductClick = onProductClick,
            onAddToCart = onAddToCart
          )
        }
      }

      // 4. Storefront Footer
      item(span = { GridItemSpan(2) }) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(DarkEspresso)
            .padding(16.dp)
        ) {
          Text(
            text = config.storeName,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = AntiqueGold
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "আধুনিক ও রুচিশীল নারীদের জন্য প্রিমিয়াম ডিজাইনার শাড়ি ও রাজকীয় জুয়েলারি কালেকশন। খাঁটি কারুকার্য এবং নির্ভরতার সেরা ঠিকানা।",
            fontSize = 11.sp,
            color = CreamSurface,
            lineHeight = 15.sp
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Contact info
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = AntiqueGold, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "হটলাইন: ${config.phone}", fontSize = 11.sp, color = Color.White)
          }

          Spacer(modifier = Modifier.height(4.dp))

          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = AntiqueGold, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = config.address, fontSize = 11.sp, color = CreamSurface, maxLines = 1)
          }

          Spacer(modifier = Modifier.height(14.dp))
          HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
          Spacer(modifier = Modifier.height(10.dp))

          // Payment badges
          Text(
            text = "নিরাপদ পেমেন্ট মেথড:",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = AntiqueGold
          )

          Spacer(modifier = Modifier.height(6.dp))

          Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            if (config.bkashEnabled) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0xFFE2136E))
                  .padding(horizontal = 8.dp, vertical = 3.dp)
              ) {
                Text(text = "bKash", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
              }
            }
            if (config.nagadEnabled) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0xFFF6921E))
                  .padding(horizontal = 8.dp, vertical = 3.dp)
              ) {
                Text(text = "Nagad", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
              }
            }
            if (config.codEnabled) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color.White)
                  .padding(horizontal = 8.dp, vertical = 3.dp)
              ) {
                Text(text = "Cash on Delivery", color = DarkEspresso, fontSize = 10.sp, fontWeight = FontWeight.Bold)
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = "© 2026 SAJGOJ FASHION. All Rights Reserved.",
            fontSize = 10.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    }

    // Floating WhatsApp Order Help Button
    FloatingActionButton(
      onClick = { openWhatsApp() },
      containerColor = Color(0xFF25D366),
      contentColor = Color.White,
      shape = CircleShape,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(16.dp)
        .testTag("whatsapp_fab")
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Icon(imageVector = Icons.Default.Chat, contentDescription = "WhatsApp", modifier = Modifier.size(20.dp))
        Text(text = "WhatsApp", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}
