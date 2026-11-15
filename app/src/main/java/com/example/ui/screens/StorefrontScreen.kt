package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.BannerItem
import com.example.data.model.Category
import com.example.data.model.HomeSectionConfig
import com.example.data.model.Product
import com.example.data.model.StoreConfig
import com.example.ui.ImageSearchState
import com.example.ui.components.AppImage
import com.example.ui.components.BannerCarousel
import com.example.ui.components.CompactProductCard
import com.example.ui.components.ProductCard
import com.example.ui.components.VoucherCenterDialog
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.AntiqueGoldDark
import com.example.ui.theme.BorderGold
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.DarkEspresso
import com.example.ui.theme.RoyalMaroon

private val fallbackDefaultSections = listOf(
  HomeSectionConfig(id = "top_banner", type = "top_banner", title = "হিরো ব্যানার ও স্লাইডার", layout = "banner", enabled = true),
  HomeSectionConfig(id = "voucher_strip", type = "voucher_strip", title = "ভাউচার ডিসকাউন্ট কার্ড", layout = "strip", enabled = true),
  HomeSectionConfig(id = "category_slider", type = "category_slider", title = "স্লাইডিং ক্যাটাগরি কার্ড", layout = "strip", enabled = true),
  HomeSectionConfig(id = "new_products", type = "new_products", title = "নতুন প্রোডাক্ট", icon = "✨", badge = "NEW", layout = "slider3", enabled = true, maxItems = 12),
  HomeSectionConfig(id = "hot_sale", type = "hot_sale", title = "Hot Sale", icon = "🔥", badge = "HOT 🔥", layout = "slider3", enabled = true, maxItems = 12),
  HomeSectionConfig(id = "best_deals", type = "best_deals", title = "সেরা ডিল", icon = "⭐", badge = "সেরা ডিল", layout = "grid3x2", enabled = true, maxItems = 6),
  HomeSectionConfig(id = "all_products", type = "all_products", title = "সব প্রোডাক্ট", icon = "🛍️", badge = "সকল পণ্য", layout = "grid3x3", enabled = true, maxItems = 9)
)

@Composable
fun StorefrontScreen(
  products: List<Product>,
  banners: List<BannerItem>,
  config: StoreConfig,
  categories: List<Category> = emptyList(),
  homeSections: List<HomeSectionConfig> = emptyList(),
  selectedCategory: String = "All",
  searchQuery: String = "",
  onCategorySelected: (String) -> Unit = {},
  onResetFilters: () -> Unit = {},
  imageSearchState: ImageSearchState? = null,
  onClearImageSearch: () -> Unit = {},
  scrollToTopTrigger: Int = 0,
  onProductClick: (Product) -> Unit,
  onAddToCart: (Product) -> Unit,
  onBannerClick: (BannerItem) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val listState = rememberLazyListState()
  var showVoucherCenter by remember { mutableStateOf(false) }
  var voucherCenterTab by remember { mutableStateOf("all") }
  var collectedVoucherIds by remember { mutableStateOf(setOf<String>()) }
  var expandedSectionIds by remember { mutableStateOf(setOf<String>()) }

  val isFilteredMode = selectedCategory != "All" || searchQuery.isNotBlank() || imageSearchState != null
  val activeSections = remember(homeSections) {
    (if (homeSections.isNotEmpty()) homeSections else fallbackDefaultSections).filter { it.enabled }
  }

  // Group top hero blocks so Banner -> Voucher -> Category keep their tight 4.dp vertical spacing
  val groupedBlocks = remember(activeSections) {
    val result = mutableListOf<List<HomeSectionConfig>>()
    val heroTypes = setOf("top_banner", "voucher_strip", "category_slider")
    var i = 0
    while (i < activeSections.size) {
      if (activeSections[i].type in heroTypes) {
        val group = mutableListOf<HomeSectionConfig>()
        while (i < activeSections.size && activeSections[i].type in heroTypes) {
          group.add(activeSections[i])
          i++
        }
        result.add(group)
      } else {
        result.add(listOf(activeSections[i]))
        i++
      }
    }
    result
  }

  LaunchedEffect(scrollToTopTrigger) {
    if (scrollToTopTrigger > 0) {
      expandedSectionIds = emptySet()
      listState.animateScrollToItem(0)
    }
  }

  LaunchedEffect(imageSearchState) {
    if (imageSearchState != null) {
      listState.animateScrollToItem(0)
    }
  }

  fun openWhatsApp() {
    try {
      val cleanNumber = config.whatsapp.replace("+", "").replace("-", "")
      val uri = Uri.parse("https://api.whatsapp.com/send?phone=$cleanNumber&text=আসসালামু%20আলাইকুম,%20আমি%20সজগোজ%20ফ্যাশন%20থেকে%20অর্ডার%20করতে%20চাই।")
      context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    } catch (_: Exception) {
      val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${config.phone}"))
      context.startActivity(dialIntent)
    }
  }

  fun resolveSectionProducts(section: HomeSectionConfig): List<Product> {
    return when (section.type) {
      "new_products" -> {
        val pinned = section.productIds.mapNotNull { pid -> products.find { it.id == pid } }
        val rest = products
          .filter { p -> section.productIds.none { it == p.id } }
          .sortedByDescending { it.createdAt }
        (pinned + rest)
      }
      "hot_sale" -> {
        val idSet = section.productIds.toSet()
        val picked = products.filter { it.isHotSale || idSet.contains(it.id) }
        if (picked.isNotEmpty()) picked else products.take(6)
      }
      "best_deals" -> {
        val idSet = section.productIds.toSet()
        val picked = products.filter { idSet.contains(it.id) || it.isBestDeal }
        if (picked.isNotEmpty()) picked else products.take(6)
      }
      "all_products" -> {
        if (section.productIds.isNotEmpty()) {
          val idSet = section.productIds.toSet()
          val pinned = section.productIds.mapNotNull { pid -> products.find { it.id == pid } }
          pinned + products.filterNot { idSet.contains(it.id) }
        } else {
          products
        }
      }
      "category_products" -> {
        val cat = section.categoryName.trim()
        val byCat = if (cat.isBlank() || cat == "All") {
          products
        } else {
          products.filter {
            it.category.equals(cat, ignoreCase = true) ||
              it.category.contains(cat, ignoreCase = true) ||
              cat.contains(it.category, ignoreCase = true)
          }
        }
        val extra = section.productIds.mapNotNull { pid -> products.find { it.id == pid } }
        (extra + byCat).distinctBy { it.id }
      }
      "custom_products" -> {
        val pinned = section.productIds.mapNotNull { pid -> products.find { it.id == pid } }
        if (pinned.isNotEmpty()) pinned else products.take(6)
      }
      else -> products
    }
  }

  Box(modifier = modifier.fillMaxSize()) {
    LazyColumn(
      state = listState,
      contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 84.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier
        .fillMaxSize()
        .testTag("storefront_grid")
    ) {
      if (isFilteredMode) {
        // Show Hero / Categories concisely at top when browsing category or searching
        if (imageSearchState != null) {
          item {
            ImageSearchResultBanner(
              imageSearchState = imageSearchState,
              matchCount = products.size,
              onClearImageSearch = onClearImageSearch
            )
          }
        }

        if (categories.isNotEmpty()) {
          item {
            CategorySliderCard(
              categories = categories,
              selectedCategory = selectedCategory,
              onCategorySelected = onCategorySelected
            )
          }
        }

        item {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = when {
                  imageSearchState != null -> "🖼️ ছবির সাথে মিল পাওয়া পণ্য"
                  searchQuery.isNotBlank() -> "🔍 \"$searchQuery\" এর ফলাফল"
                  else -> "🏷️ $selectedCategory কালেকশন"
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = RoyalMaroon,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Text(
                text = "মোট ${products.size} টি প্রিমিয়াম পণ্য পাওয়া গেছে",
                fontSize = 11.sp,
                color = AntiqueGoldDark
              )
            }

            Surface(
              shape = RoundedCornerShape(12.dp),
              color = CreamSurface,
              border = BorderStroke(1.dp, BorderGold),
              modifier = Modifier
                .clickable { onResetFilters() }
                .testTag("reset_filter_pill")
            ) {
              Text(
                text = "✕ সব দেখুন",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = RoyalMaroon,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
              )
            }
          }
        }

        if (products.isEmpty()) {
          item {
            EmptySearchResultBox()
          }
        } else {
          items(products.chunked(2)) { rowItems ->
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              rowItems.forEach { product ->
                ProductCard(
                  product = product,
                  onProductClick = onProductClick,
                  onAddToCart = onAddToCart,
                  modifier = Modifier.weight(1f)
                )
              }
              if (rowItems.size == 1) {
                Spacer(modifier = Modifier.weight(1f))
              }
            }
          }
        }
      } else {
        // Modular Admin-Controlled Storefront Sections
        groupedBlocks.forEach { blockGroup ->
          val isHeroStack = blockGroup.size > 1 ||
            blockGroup.first().type in setOf("top_banner", "voucher_strip", "category_slider")

          if (isHeroStack) {
            item(key = "hero_${blockGroup.joinToString("_") { it.id }}") {
              Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                blockGroup.forEach { sec ->
                  when (sec.type) {
                    "top_banner" -> {
                      BannerCarousel(
                        banners = banners,
                        onBannerClick = onBannerClick
                      )
                    }
                    "voucher_strip" -> {
                      VoucherStripCard(
                        onOpenVoucher = { tab ->
                          voucherCenterTab = tab
                          showVoucherCenter = true
                        }
                      )
                    }
                    "category_slider" -> {
                      if (categories.isNotEmpty()) {
                        CategorySliderCard(
                          categories = categories,
                          selectedCategory = selectedCategory,
                          onCategorySelected = onCategorySelected
                        )
                      }
                    }
                  }
                }
              }
            }
          } else {
            val section = blockGroup.first()
            if (section.type == "promo_banner") {
              item(key = "sec_${section.id}") {
                PromoBannerSectionCard(
                  section = section,
                  onClick = {
                    if (section.bannerLinkCategory.isNotBlank() && section.bannerLinkCategory != "All") {
                      onCategorySelected(section.bannerLinkCategory)
                    } else {
                      Toast.makeText(context, "${section.title} অফার চালু আছে!", Toast.LENGTH_SHORT).show()
                    }
                  }
                )
              }
            } else {
              item(key = "sec_${section.id}") {
                val sectionProducts = remember(products, section) { resolveSectionProducts(section) }
                val isExpanded = expandedSectionIds.contains(section.id)
                StorefrontProductBlockCard(
                  section = section,
                  products = sectionProducts,
                  isExpanded = isExpanded,
                  onToggleExpand = {
                    expandedSectionIds = if (isExpanded) {
                      expandedSectionIds - section.id
                    } else {
                      expandedSectionIds + section.id
                    }
                  },
                  onProductClick = onProductClick,
                  onAddToCart = onAddToCart
                )
              }
            }
          }
        }
      }

      // Storefront Footer
      item(key = "storefront_footer") {
        StorefrontFooterCard(config = config)
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

    if (showVoucherCenter) {
      VoucherCenterDialog(
        collectedVoucherIds = collectedVoucherIds,
        initialTab = voucherCenterTab,
        onCollectVoucher = { id ->
          if (collectedVoucherIds.contains(id)) {
            collectedVoucherIds = collectedVoucherIds - id
            Toast.makeText(context, "ভাউচার আনসিলেক্ট করা হয়েছে", Toast.LENGTH_SHORT).show()
          } else {
            collectedVoucherIds = collectedVoucherIds + id
            Toast.makeText(context, "ভাউচার কালেক্ট করা হয়েছে! ✓ (আবার ট্যাপ করলে আনসিলেক্ট হবে)", Toast.LENGTH_SHORT).show()
          }
        },
        onCollectAll = { ids ->
          val allAlready = ids.isNotEmpty() && ids.all { collectedVoucherIds.contains(it) }
          if (allAlready) {
            collectedVoucherIds = collectedVoucherIds - ids.toSet()
            Toast.makeText(context, "সেকশনের ভাউচারগুলো আনসিলেক্ট করা হয়েছে", Toast.LENGTH_SHORT).show()
          } else {
            collectedVoucherIds = collectedVoucherIds + ids.toSet()
            Toast.makeText(context, "সকল ভাউচার সফলভাবে কালেক্ট করা হয়েছে! 🎟️", Toast.LENGTH_SHORT).show()
          }
        },
        onDismiss = { showVoucherCenter = false }
      )
    }
  }
}

@Composable
private fun StorefrontProductBlockCard(
  section: HomeSectionConfig,
  products: List<Product>,
  isExpanded: Boolean,
  onToggleExpand: () -> Unit,
  onProductClick: (Product) -> Unit,
  onAddToCart: (Product) -> Unit
) {
  val defaultLimit = when (section.layout) {
    "grid3x2" -> 6
    "grid3x3" -> 9
    else -> section.maxItems.coerceAtLeast(3)
  }
  val displayProducts = remember(products, isExpanded, section.layout, defaultLimit) {
    if (isExpanded) products
    else if (section.layout == "slider3") products.take(section.maxItems.coerceAtLeast(6))
    else products.take(defaultLimit)
  }

  val cardBadge = when (section.type) {
    "new_products" -> "NEW"
    "hot_sale" -> "HOT"
    "best_deals" -> "DEAL"
    else -> null
  }

  Surface(
    shape = RoundedCornerShape(16.dp),
    color = Color.White,
    border = BorderStroke(1.dp, BorderGold.copy(alpha = 0.7f)),
    shadowElevation = 1.dp,
    modifier = Modifier
      .fillMaxWidth()
      .testTag("home_section_${section.id}")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp, vertical = 10.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Section Title Header + Small "আরো দেখুন ›" Action at the end
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
          Text(text = section.icon.ifBlank { "✨" }, fontSize = 15.sp)
          Text(
            text = section.title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            color = RoyalMaroon,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          if (section.badge.isNotBlank()) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (section.type == "hot_sale") Color(0xFFFFEBEE) else CreamSurface,
              border = BorderStroke(
                1.dp,
                if (section.type == "hot_sale") Color(0xFFEF4444) else BorderGold
              )
            ) {
              Text(
                text = section.badge,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Black,
                color = if (section.type == "hot_sale") Color(0xFFC62828) else DarkEspresso,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
              )
            }
          }
        }

        if (products.isNotEmpty()) {
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .clickable { onToggleExpand() }
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

      if (displayProducts.isEmpty()) {
        Text(
          text = "এই ব্লকে এখনো কোনো প্রোডাক্ট যুক্ত করা হয়নি। অ্যাডমিন Home Page Setting থেকে প্রোডাক্ট সিলেক্ট করুন।",
          fontSize = 11.sp,
          color = Color.Gray,
          modifier = Modifier.padding(vertical = 8.dp)
        )
      } else if (!isExpanded && section.layout == "slider3") {
        // Horizontal 3-card per viewport touch slider (Dynamic width via BoxWithConstraints)
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
          val cardWidth = (maxWidth - 16.dp) / 3f
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(displayProducts, key = { "${section.id}_${it.id}" }) { prod ->
              CompactProductCard(
                product = prod,
                onProductClick = onProductClick,
                onAddToCart = onAddToCart,
                badgeText = cardBadge,
                modifier = Modifier.width(cardWidth)
              )
            }
          }
        }
      } else {
        // 3-Column Multi-Row Grid (2 lines = 6 items for best_deals, 3 lines = 9 items for all_products, or all when expanded)
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          displayProducts.chunked(3).forEach { rowProducts ->
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              rowProducts.forEach { prod ->
                CompactProductCard(
                  product = prod,
                  onProductClick = onProductClick,
                  onAddToCart = onAddToCart,
                  badgeText = cardBadge,
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

@Composable
private fun PromoBannerSectionCard(
  section: HomeSectionConfig,
  onClick: () -> Unit
) {
  CardShapePromo(section = section, onClick = onClick)
}

@Composable
private fun CardShapePromo(
  section: HomeSectionConfig,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(
        Brush.horizontalGradient(
          listOf(RoyalMaroon, DarkEspresso, Color(0xFF4A0E0E))
        )
      )
      .border(1.5.dp, AntiqueGold, RoundedCornerShape(16.dp))
      .clickable { onClick() }
      .padding(14.dp)
      .testTag("promo_banner_${section.id}")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        if (section.badge.isNotBlank()) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = AntiqueGold
          ) {
            Text(
              text = section.badge,
              fontSize = 9.sp,
              fontWeight = FontWeight.Black,
              color = DarkEspresso,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
        Text(
          text = "${section.icon} ${section.title}",
          fontSize = 15.sp,
          fontWeight = FontWeight.Black,
          color = Color.White
        )
        if (section.subtitle.isNotBlank()) {
          Text(
            text = section.subtitle,
            fontSize = 11.sp,
            color = CreamSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      Spacer(modifier = Modifier.width(10.dp))

      Surface(
        shape = RoundedCornerShape(12.dp),
        color = AntiqueGold
      ) {
        Text(
          text = "${section.bannerButtonText.ifBlank { "অফার দেখুন" }} →",
          fontSize = 11.sp,
          fontWeight = FontWeight.Black,
          color = DarkEspresso,
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
        )
      }
    }
  }
}

@Composable
private fun VoucherStripCard(
  onOpenVoucher: (String) -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(
        Brush.horizontalGradient(
          listOf(Color(0xFFFFF3F3), Color(0xFFFFF8F5), Color(0xFFF0FBF9))
        )
      )
      .border(1.dp, Color(0xFFFFCDD2), RoundedCornerShape(14.dp))
      .padding(horizontal = 8.dp, vertical = 6.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 2.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Text(
          text = "Claim Vouchers to Save More!",
          fontSize = 11.5.sp,
          fontWeight = FontWeight.Black,
          color = DarkEspresso
        )
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(RoyalMaroon)
            .padding(horizontal = 5.dp, vertical = 1.dp)
        ) {
          Text(
            text = "11.11 / SAJGOJ",
            fontSize = 7.5.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
          )
        }
      }

      Row(
        modifier = Modifier.clickable { onOpenVoucher("all") },
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "More Voucher",
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = RoyalMaroon
        )
        Text(
          text = " ›",
          fontSize = 13.sp,
          fontWeight = FontWeight.Black,
          color = RoyalMaroon
        )
      }
    }

    Spacer(modifier = Modifier.height(4.dp))

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .background(Color.White)
        .border(1.dp, Color(0xFFFFEBEE), RoundedCornerShape(10.dp))
        .padding(horizontal = 8.dp, vertical = 5.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(
        modifier = Modifier
          .weight(1f)
          .clickable { onOpenVoucher("max") }
      ) {
        Text(
          text = "৳২,২৫০",
          fontSize = 13.5.sp,
          fontWeight = FontWeight.Black,
          color = Color(0xFFE53935)
        )
        Text(
          text = "Voucher Max",
          fontSize = 9.sp,
          fontWeight = FontWeight.Medium,
          color = Color.Gray
        )
      }

      Box(
        modifier = Modifier
          .width(1.dp)
          .height(22.dp)
          .background(Color(0xFFEEEEEE))
      )

      Column(
        modifier = Modifier
          .weight(1f)
          .padding(start = 8.dp)
          .clickable { onOpenVoucher("shipping") }
      ) {
        Text(
          text = "৳৪৫",
          fontSize = 13.5.sp,
          fontWeight = FontWeight.Black,
          color = Color(0xFF00897B)
        )
        Text(
          text = "Free shipping",
          fontSize = 9.sp,
          fontWeight = FontWeight.Medium,
          color = Color.Gray
        )
      }

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(Color(0xFFFF8F00))
          .border(1.dp, Color(0xFFFFB300), RoundedCornerShape(8.dp))
          .clickable { onOpenVoucher("all") }
          .padding(horizontal = 10.dp, vertical = 5.dp)
      ) {
        Text(
          text = "Collect All ›",
          fontSize = 10.sp,
          fontWeight = FontWeight.Black,
          color = Color.White
        )
      }
    }
  }
}

@Composable
private fun CategorySliderCard(
  categories: List<Category>,
  selectedCategory: String,
  onCategorySelected: (String) -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(Color.White)
      .padding(vertical = 6.dp, horizontal = 4.dp)
  ) {
    LazyRow(
      contentPadding = PaddingValues(horizontal = 6.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      item {
        val isSelected = selectedCategory == "All"
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier
            .width(60.dp)
            .clickable { onCategorySelected("All") }
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(if (isSelected) RoyalMaroon else CreamSurface)
              .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) AntiqueGold else BorderGold,
                shape = RoundedCornerShape(12.dp)
              ),
            contentAlignment = Alignment.Center
          ) {
            Text(text = "🌟", fontSize = 20.sp)
          }
          Spacer(modifier = Modifier.height(3.dp))
          Text(
            text = "সকল পণ্য",
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) RoyalMaroon else DarkEspresso,
            textAlign = TextAlign.Center,
            maxLines = 1
          )
        }
      }

      val displayCats = categories.filter { it.type != "sub" && it.type != "parent" }
        .ifEmpty { categories }
      items(displayCats) { cat ->
        val isSelected = selectedCategory == cat.name
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier
            .width(60.dp)
            .clickable { onCategorySelected(cat.name) }
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(if (isSelected) CreamSurface else Color.White)
              .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) RoyalMaroon else BorderGold,
                shape = RoundedCornerShape(12.dp)
              ),
            contentAlignment = Alignment.Center
          ) {
            if (cat.imageUrl.isNotBlank()) {
              AppImage(
                model = cat.imageUrl,
                contentDescription = cat.name,
                modifier = Modifier
                  .fillMaxSize()
                  .clip(RoundedCornerShape(11.dp))
              )
            } else {
              Text(
                text = cat.icon.ifBlank { "✨" },
                fontSize = 20.sp
              )
            }

            if (cat.badge.isNotBlank()) {
              Surface(
                color = RoyalMaroon,
                shape = RoundedCornerShape(bottomStart = 6.dp),
                modifier = Modifier.align(Alignment.TopEnd)
              ) {
                Text(
                  text = cat.badge,
                  color = Color.White,
                  fontSize = 7.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                )
              }
            }
          }
          Spacer(modifier = Modifier.height(3.dp))
          Text(
            text = cat.name,
            fontSize = 9.5.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) RoyalMaroon else DarkEspresso,
            textAlign = TextAlign.Center,
            maxLines = 1
          )
        }
      }
    }
  }
}

@Composable
private fun ImageSearchResultBanner(
  imageSearchState: ImageSearchState,
  matchCount: Int,
  onClearImageSearch: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(16.dp),
    color = Color(0xFFFFEBEE),
    border = BorderStroke(1.dp, Color(0xFFEF4444)),
    shadowElevation = 2.dp,
    modifier = Modifier
      .fillMaxWidth()
      .testTag("visual_image_search_banner")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.weight(1f)
      ) {
        AsyncImage(
          model = Uri.parse(imageSearchState.imageUri),
          contentDescription = "নির্বাচিত ছবির প্রিভিউ",
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(1.5.dp, Color.White, RoundedCornerShape(10.dp))
        )
        Column(modifier = Modifier.weight(1f)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = "🖼️ ইমেজ সার্চ সক্রিয়",
              fontSize = 12.sp,
              fontWeight = FontWeight.Black,
              color = Color.Black
            )
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = Color.White
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color(imageSearchState.colorHex))
                )
                Text(
                  text = imageSearchState.colorLabelBn,
                  fontSize = 9.5.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.Black
                )
              }
            }
          }
          Text(
            text = "${imageSearchState.fileName} • $matchCount টি মিল পাওয়া গেছে",
            fontSize = 10.5.sp,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.Black,
        modifier = Modifier
          .clickable { onClearImageSearch() }
          .testTag("clear_image_search_button")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "ইমেজ সার্চ মুছুন",
            tint = Color.White,
            modifier = Modifier.size(13.dp)
          )
          Text(
            text = "মুছুন",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
      }
    }
  }
}

@Composable
private fun EmptySearchResultBox() {
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

@Composable
private fun StorefrontFooterCard(config: StoreConfig) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 12.dp)
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
