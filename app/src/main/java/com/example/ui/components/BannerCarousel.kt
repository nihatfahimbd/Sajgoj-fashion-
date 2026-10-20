package com.example.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BannerItem
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.DarkEspresso
import kotlinx.coroutines.delay

@Composable
fun BannerCarousel(
  banners: List<BannerItem>,
  onBannerClick: (BannerItem) -> Unit,
  modifier: Modifier = Modifier
) {
  val activeBanners = banners.filter { it.isActive }
  if (activeBanners.isEmpty()) return

  val pagerState = rememberPagerState(pageCount = { activeBanners.size })

  // Auto scroll effect
  LaunchedEffect(activeBanners.size) {
    if (activeBanners.size > 1) {
      while (true) {
        delay(4000)
        val nextPage = (pagerState.currentPage + 1) % activeBanners.size
        pagerState.animateScrollToPage(nextPage, animationSpec = tween(600))
      }
    }
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .height(180.dp),
    shape = RoundedCornerShape(20.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    colors = CardDefaults.cardColors(containerColor = DarkEspresso)
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
      ) { page ->
        val banner = activeBanners[page]
        Box(
          modifier = Modifier
            .fillMaxSize()
            .clickable { onBannerClick(banner) }
        ) {
          // Banner Image
          AppImage(
            model = banner.imageUrl,
            contentDescription = banner.title,
            modifier = Modifier.fillMaxSize()
          )

          // Gradient overlay for contrast
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(
                Brush.horizontalGradient(
                  colors = listOf(
                    Color.Black.copy(alpha = 0.75f),
                    Color.Black.copy(alpha = 0.40f),
                    Color.Transparent
                  )
                )
              )
          )

          // Text Content
          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(16.dp),
            verticalArrangement = Arrangement.Center
          ) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(AntiqueGold)
                .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
              Text(
                text = "EXCLUSIVE OFFER",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = DarkEspresso
              )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = banner.title,
              color = Color.White,
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              maxLines = 1
            )

            Text(
              text = banner.subtitle,
              color = CreamSurface,
              fontSize = 11.sp,
              maxLines = 2,
              lineHeight = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
              onClick = { onBannerClick(banner) },
              colors = ButtonDefaults.buttonColors(
                containerColor = AntiqueGold,
                contentColor = DarkEspresso
              ),
              shape = RoundedCornerShape(16.dp),
              contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
              modifier = Modifier.height(28.dp)
            ) {
              Text(
                text = "${banner.buttonText} →",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      // Page indicators (dots)
      if (activeBanners.size > 1) {
        Row(
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(12.dp),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          repeat(activeBanners.size) { index ->
            val isCurrent = pagerState.currentPage == index
            Box(
              modifier = Modifier
                .size(width = if (isCurrent) 16.dp else 6.dp, height = 6.dp)
                .clip(CircleShape)
                .background(if (isCurrent) AntiqueGold else Color.White.copy(alpha = 0.5f))
            )
          }
        }
      }
    }
  }
}
