package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.BannerItem
import com.example.data.model.CartItem
import com.example.data.model.Category
import com.example.data.model.ComboPack
import com.example.data.model.Coupon
import com.example.data.model.HomeSectionConfig
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.ReelItem
import com.example.data.model.StoreConfig
import com.example.data.model.StoreDesignConfig
import com.example.ui.ImageSearchState
import com.example.ui.ProductSortOption
import com.example.ui.components.AppImage
import com.example.ui.components.BannerCarousel
import com.example.ui.components.CompactProductCard
import com.example.ui.components.FreeFaqChatbotDialog
import com.example.ui.components.FullSectionCollectionDialog
import com.example.ui.components.ProductCard
import com.example.ui.components.ProductSortDropdown
import com.example.ui.components.ReelVideoPlayerDialog
import com.example.ui.components.StorefrontComboBlockCard
import com.example.ui.components.StorefrontReelsBlockCard
import com.example.ui.components.VoucherCenterDialog
import com.example.ui.components.resolveBlockGridConfig
import com.example.ui.sortStorefrontProducts
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.AntiqueGoldDark
import com.example.ui.theme.BorderGold
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.DarkEspresso
import com.example.ui.theme.NavyBlue
import com.example.ui.theme.RoyalMaroon
import com.example.ui.theme.parseColorHex

private val fallbackDefaultSections = listOf(
  HomeSectionConfig(id = "top_banner", type = "top_banner", title = "হিরো ব্যানার ও স্লাইডার", layout = "banner", displayStyle = "banner", columnsPerRow = 1, rowCount = 1, enabled = true),
  HomeSectionConfig(id = "voucher_strip", type = "voucher_strip", title = "ভাউচার ডিসকাউন্ট কার্ড", layout = "strip", displayStyle = "strip", columnsPerRow = 1, rowCount = 1, enabled = true),
  HomeSectionConfig(id = "category_slider", type = "category_slider", title = "স্লাইডিং ক্যাটাগরি কার্ড", layout = "strip", displayStyle = "strip", columnsPerRow = 1, rowCount = 1, enabled = true),
  HomeSectionConfig(id = "new_products", type = "new_products", title = "নতুন প্রোডাক্ট", icon = "✨", badge = "NEW", layout = "slider3", columnsPerRow = 3, rowCount = 1, displayStyle = "slider", enabled = true, maxItems = 12),
  HomeSectionConfig(id = "hot_sale", type = "hot_sale", title = "Hot Sale", icon = "🔥", badge = "HOT 🔥", layout = "slider3", columnsPerRow = 3, rowCount = 1, displayStyle = "slider", enabled = true, maxItems = 12),
  HomeSectionConfig(id = "combo_packs", type = "combo_packs", title = "কম্বো প্যাক", icon = "🎁", badge = "COMBO", layout = "slider3", columnsPerRow = 3, rowCount = 1, displayStyle = "slider", enabled = true, maxItems = 12),
  HomeSectionConfig(id = "b2g1_offers", type = "b2g1_offers", title = "Buy 2 Get 1", icon = "🎉", badge = "BUY 2 GET 1", layout = "slider3", columnsPerRow = 3, rowCount = 1, displayStyle = "slider", enabled = true, maxItems = 12),
  HomeSectionConfig(id = "best_deals", type = "best_deals", title = "সেরা ডিল", icon = "⭐", badge = "সেরা ডিল", layout = "grid3x2", columnsPerRow = 3, rowCount = 2, displayStyle = "grid", enabled = true, maxItems = 6),
  HomeSectionConfig(id = "reels_showcase", type = "reels_showcase", title = "Product Reels", subtitle = "পন্যের ভিডিও দেখে অর্ডার করুন", icon = "🎬", badge = "", layout = "slider3", columnsPerRow = 3, rowCount = 1, displayStyle = "slider", enabled = true, maxItems = 3),
  HomeSectionConfig(id = "all_products", type = "all_products", title = "সব প্রোডাক্ট", icon = "🛍️", badge = "সকল পণ্য", layout = "grid3x3", columnsPerRow = 3, rowCount = 3, displayStyle = "grid", enabled = true, maxItems = 9)
)

@Composable
fun StorefrontScreen(
  products: List<Product>,
  banners: List<BannerItem>,
  config: StoreConfig,
  categories: List<Category> = emptyList(),
  homeSections: List<HomeSectionConfig> = emptyList(),
  reels: List<ReelItem> = emptyList(),
  combos: List<ComboPack> = emptyList(),
  orders: List<Order> = emptyList(),
  coupons: List<Coupon> = emptyList(),
  cartItemCount: Int = 0,
  cartItems: List<CartItem> = emptyList(),
  selectedCategory: String = "All",
  searchQuery: String = "",
  sortOption: ProductSortOption = ProductSortOption.DEFAULT,
  onCategorySelected: (String) -> Unit = {},
  onSortOptionSelected: (ProductSortOption) -> Unit = {},
  onResetFilters: () -> Unit = {},
  imageSearchState: ImageSearchState? = null,
  onClearImageSearch: () -> Unit = {},
  scrollToTopTrigger: Int = 0,
  onProductClick: (Product) -> Unit,
  onAddToCart: (Product) -> Unit,
  onAddComboToCart: (ComboPack) -> Unit = {},
  onLikeReel: (String) -> Unit = {},
  onBannerClick: (BannerItem) -> Unit,
  onOpenAiStylist: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val listState = rememberLazyListState()
  var showVoucherCenter by remember { mutableStateOf(false) }
  var voucherCenterTab by remember { mutableStateOf("all") }
  var collectedVoucherIds by remember { mutableStateOf(setOf<String>()) }
  var expandedSectionIds by remember { mutableStateOf(setOf<String>()) }
  var collectionDialogSection by remember { mutableStateOf<HomeSectionConfig?>(null) }
  var activeReelForPlayback by remember { mutableStateOf<ReelItem?>(null) }
  var allProductsInlineCat by remember { mutableStateOf("All") }
  var wishlistIds by remember { mutableStateOf(setOf<String>()) }
  var activeFooterTopic by remember { mutableStateOf<String?>(null) }
  var showFaqChatbot by remember { mutableStateOf(false) }
  val design = config.design

  BackHandler(enabled = showFaqChatbot) {
    showFaqChatbot = false
  }
  val toggleWishlist: (String) -> Unit = { pid ->
    wishlistIds = if (wishlistIds.contains(pid)) wishlistIds - pid else wishlistIds + pid
  }

  val isFilteredMode = selectedCategory != "All" ||
    searchQuery.isNotBlank() ||
    imageSearchState != null ||
    sortOption != ProductSortOption.DEFAULT
  val activeStorefrontProducts = remember(products) {
    products.filter { !it.isDraft && it.enabledForWeb }
  }
  val sortedDisplayProducts = remember(activeStorefrontProducts, sortOption) {
    sortStorefrontProducts(activeStorefrontProducts, sortOption)
  }
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
    val activeProducts = products.filter { !it.isDraft && it.enabledForWeb }
    return when (section.type) {
      "new_products" -> {
        val pinned = section.productIds.mapNotNull { pid -> activeProducts.find { it.id == pid } }
        val rest = activeProducts
          .filter { p -> section.productIds.none { it == p.id } }
          .sortedByDescending { it.createdAt }
        (pinned + rest)
      }
      "hot_sale" -> {
        val idSet = section.productIds.toSet()
        val picked = activeProducts.filter { it.isHotSale || idSet.contains(it.id) }
        if (picked.isNotEmpty()) picked else activeProducts.take(6)
      }
      "b2g1_offers" -> {
        val b2g1 = config.b2g1
        if (!b2g1.enabled || b2g1.eligibleProductIds.isEmpty()) {
          emptyList()
        } else {
          b2g1.eligibleProductIds.mapNotNull { pid -> activeProducts.find { it.id == pid } }
        }
      }
      "best_deals" -> {
        val idSet = section.productIds.toSet()
        val pinned = section.productIds.mapNotNull { pid -> activeProducts.find { it.id == pid } }
        val dealMarked = activeProducts.filter { it.isBestDeal && !idSet.contains(it.id) }
        val chosenIds = (pinned + dealMarked).map { it.id }.toSet()
        val extras = activeProducts.filterNot { chosenIds.contains(it.id) }.sortedByDescending { it.rating }
        val combined = pinned + dealMarked + extras
        if (combined.isNotEmpty()) combined else activeProducts
      }
      "all_products" -> {
        // Round-robin interleave across ALL categories so every category appears on first view
        val baseList = if (section.productIds.isNotEmpty()) {
          val idSet = section.productIds.toSet()
          val pinned = section.productIds.mapNotNull { pid -> activeProducts.find { it.id == pid } }
          pinned + activeProducts.filterNot { idSet.contains(it.id) }
        } else {
          val byCategory = activeProducts.groupBy { it.category.trim().ifBlank { "অন্যান্য" } }
          val buckets = byCategory.values.map { it.toMutableList() }
          buildList {
            var remaining = true
            while (remaining) {
              remaining = false
              buckets.forEach { bucket ->
                if (bucket.isNotEmpty()) {
                  add(bucket.removeAt(0))
                  remaining = true
                }
              }
            }
          }
        }
        if (allProductsInlineCat == "All") baseList
        else baseList.filter { it.category.equals(allProductsInlineCat, ignoreCase = true) }
      }
      "category_products" -> {
        val cat = section.categoryName.trim()
        val byCat = if (cat.isBlank() || cat == "All") {
          activeProducts
        } else {
          activeProducts.filter {
            it.category.equals(cat, ignoreCase = true) ||
              it.category.contains(cat, ignoreCase = true) ||
              cat.contains(it.category, ignoreCase = true)
          }
        }
        val extra = section.productIds.mapNotNull { pid -> activeProducts.find { it.id == pid } }
        (extra + byCat).distinctBy { it.id }
      }
      "custom_products" -> {
        val pinned = section.productIds.mapNotNull { pid -> activeProducts.find { it.id == pid } }
        if (pinned.isNotEmpty()) pinned else activeProducts.take(6)
      }
      else -> activeProducts
    }
  }

  val pageModifier = if (design.useCustomPageBg) {
    val startCol = parseColorHex(design.pageBgStart, CreamBackground)
    val endCol = parseColorHex(design.pageBgEnd, CreamSurface)
    modifier
      .fillMaxSize()
      .background(Brush.verticalGradient(listOf(startCol, endCol)))
  } else {
    modifier.fillMaxSize()
  }

  Box(modifier = pageModifier) {
    LazyColumn(
      state = listState,
      contentPadding = PaddingValues(start = 10.dp, end = 10.dp, top = 4.dp, bottom = 84.dp),
      verticalArrangement = Arrangement.spacedBy(6.dp),
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
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 2.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = when {
                    imageSearchState != null -> "🖼️ ছবির সাথে মিল পাওয়া পণ্য"
                    searchQuery.isNotBlank() -> "🔍 \"$searchQuery\" এর ফলাফল"
                    selectedCategory != "All" -> "🏷️ $selectedCategory কালেকশন"
                    else -> "✨ ${sortOption.label}"
                  },
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold,
                  color = RoyalMaroon,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Text(
                  text = "মোট ${sortedDisplayProducts.size} টি প্রিমিয়াম পণ্য পাওয়া গেছে",
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
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
              }
            }

            StorefrontCatalogSortBar(
              productCount = sortedDisplayProducts.size,
              sortOption = sortOption,
              onSortOptionSelected = onSortOptionSelected
            )
          }
        }

        if (sortedDisplayProducts.isEmpty()) {
          item {
            EmptySearchResultBox()
          }
        } else {
          items(sortedDisplayProducts.chunked(2)) { rowItems ->
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              rowItems.forEach { product ->
                ProductCard(
                  product = product,
                  onProductClick = onProductClick,
                  onAddToCart = onAddToCart,
                  design = design,
                  isWishlisted = wishlistIds.contains(product.id),
                  onToggleWishlist = { toggleWishlist(product.id) },
                  onMediaClick = onProductClick,
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
        var sortBarInserted = false
        groupedBlocks.forEach { blockGroup ->
          val isHeroStack = blockGroup.size > 1 ||
            blockGroup.first().type in setOf("top_banner", "voucher_strip", "category_slider")

          if (!isHeroStack && !sortBarInserted) {
            sortBarInserted = true
            item(key = "storefront_sort_bar") {
              StorefrontCatalogSortBar(
                productCount = activeStorefrontProducts.size,
                sortOption = sortOption,
                onSortOptionSelected = onSortOptionSelected
              )
            }
          }

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
            if (!sortBarInserted) {
              sortBarInserted = true
              item(key = "storefront_sort_bar") {
                StorefrontCatalogSortBar(
                  productCount = activeStorefrontProducts.size,
                  sortOption = sortOption,
                  onSortOptionSelected = onSortOptionSelected
                )
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
            } else if (section.type == "reels_showcase") {
              item(key = "sec_${section.id}") {
                val isExpanded = expandedSectionIds.contains(section.id)
                StorefrontReelsBlockCard(
                  section = section,
                  reels = reels,
                  products = products,
                  isExpanded = isExpanded,
                  onSeeMoreClick = {
                    expandedSectionIds = if (isExpanded) {
                      expandedSectionIds - section.id
                    } else {
                      expandedSectionIds + section.id
                    }
                  },
                  onReelClick = { reel -> activeReelForPlayback = reel },
                  onAddToCart = onAddToCart,
                  design = design
                )
              }
            } else if (section.type == "combo_packs") {
              item(key = "sec_${section.id}") {
                val isExpanded = expandedSectionIds.contains(section.id)
                StorefrontComboBlockCard(
                  section = section,
                  combos = combos,
                  isExpanded = isExpanded,
                  onSeeMoreClick = {
                    expandedSectionIds = if (isExpanded) {
                      expandedSectionIds - section.id
                    } else {
                      expandedSectionIds + section.id
                    }
                  },
                  onAddComboToCart = onAddComboToCart,
                  onComboClick = { combo ->
                    onProductClick(
                      Product(
                        id = "combo_${combo.id}",
                        name = "🎁 ${combo.title}",
                        price = combo.comboPrice,
                        regularPrice = combo.regularPrice,
                        offerPrice = combo.comboPrice,
                        stock = combo.stock,
                        category = "কম্বো প্যাক",
                        imageUrl = combo.imageUrl,
                        sku = combo.id.uppercase(),
                        description = combo.description,
                        rating = 4.9f,
                        reviewsCount = 24
                      )
                    )
                  },
                  design = design
                )
              }
            } else if (section.type == "b2g1_offers" && (!config.b2g1.enabled || config.b2g1.eligibleProductIds.isEmpty())) {
              // Hide Buy 2 Get 1 section when disabled or no eligible products are selected by Admin
            } else {
              item(key = "sec_${section.id}") {
                val sectionProducts = remember(products, section, allProductsInlineCat, config.b2g1) { resolveSectionProducts(section) }
                val isExpanded = expandedSectionIds.contains(section.id)
                val allProductCategories = remember(products) {
                  products.map { it.category.trim() }.filter { it.isNotBlank() }.distinct()
                }
                val eligibleSet = remember(config.b2g1.eligibleProductIds) { config.b2g1.eligibleProductIds.toSet() }
                val b2g1CartCount = remember(cartItems, eligibleSet) {
                  cartItems.filter { !it.isFreeGift && eligibleSet.contains(it.product.id) }.sumOf { it.quantity }
                }
                val freeGiftName = remember(products, config.b2g1.freeProductId) {
                  products.find { it.id == config.b2g1.freeProductId }?.name.orEmpty()
                }
                StorefrontProductBlockCard(
                  section = section,
                  products = sectionProducts,
                  isExpanded = isExpanded,
                  cartItemCount = if (section.type == "b2g1_offers") b2g1CartCount else cartItemCount,
                  b2g1MinItems = config.b2g1.minItems.coerceAtLeast(1),
                  b2g1FreeGiftName = freeGiftName,
                  allCategoriesList = if (section.type == "all_products") allProductCategories else emptyList(),
                  selectedInlineCategory = allProductsInlineCat,
                  onSelectInlineCategory = { allProductsInlineCat = it },
                  onToggleExpand = { defaultLimit ->
                    if (!isExpanded && sectionProducts.size <= defaultLimit) {
                      collectionDialogSection = section
                    } else {
                      expandedSectionIds = if (isExpanded) {
                        expandedSectionIds - section.id
                      } else {
                        expandedSectionIds + section.id
                      }
                    }
                  },
                  onOpenFullCollection = { collectionDialogSection = section },
                  onProductClick = onProductClick,
                  onAddToCart = onAddToCart,
                  design = design,
                  wishlistIds = wishlistIds,
                  onToggleWishlist = toggleWishlist,
                  onMediaClick = onProductClick
                )
              }
            }
          }
        }
      }

      // Interactive Storefront Google Map Location Section
      if (config.showGoogleMap) {
        item(key = "storefront_google_map") {
          StorefrontGoogleMapCard(config = config)
        }
      }

      // Storefront Footer
      item(key = "storefront_footer") {
        StorefrontFooterCard(
          config = config,
          onOpenTopic = { topic -> activeFooterTopic = topic }
        )
      }
    }

    // Floating Action Stack: AI Fashion Stylist + WhatsApp Order Help
    Column(
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(16.dp),
      horizontalAlignment = Alignment.End,
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      FloatingActionButton(
        onClick = onOpenAiStylist,
        containerColor = RoyalMaroon,
        contentColor = Color.White,
        shape = CircleShape,
        modifier = Modifier.testTag("ai_stylist_fab")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "AI স্টাইলিস্ট", tint = AntiqueGold, modifier = Modifier.size(18.dp))
          Text(text = "স্টাইলিস্ট", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }

      FloatingActionButton(
        onClick = { showFaqChatbot = true },
        containerColor = NavyBlue,
        contentColor = Color.White,
        shape = CircleShape,
        modifier = Modifier.testTag("faq_chatbot_fab")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            imageVector = Icons.Default.SupportAgent,
            contentDescription = "সাজগোজ চ্যাটবট",
            tint = AntiqueGold,
            modifier = Modifier.size(20.dp)
          )
          Text(text = "চ্যাটবট", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
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
    if (collectionDialogSection != null) {
      val modalSec = collectionDialogSection!!
      val modalProducts = remember(products, modalSec) { resolveSectionProducts(modalSec) }
      FullSectionCollectionDialog(
        section = modalSec,
        sectionProducts = modalProducts,
        onDismiss = { collectionDialogSection = null },
        onProductClick = onProductClick,
        onAddToCart = onAddToCart,
        design = design,
        wishlistIds = wishlistIds,
        onToggleWishlist = toggleWishlist,
        onMediaClick = {
          collectionDialogSection = null
          onProductClick(it)
        }
      )
    }

    if (activeReelForPlayback != null) {
      ReelVideoPlayerDialog(
        initialReel = activeReelForPlayback!!,
        allReels = reels,
        products = products,
        onDismiss = { activeReelForPlayback = null },
        onLikeReel = onLikeReel,
        onAddToCart = onAddToCart,
        onProductClick = onProductClick
      )
    }

    if (activeFooterTopic != null) {
      FooterPolicyInfoDialog(
        initialTopic = activeFooterTopic!!,
        config = config,
        orders = orders,
        coupons = coupons,
        onDismiss = { activeFooterTopic = null },
        onOpenWhatsApp = { openWhatsApp() }
      )
    }

    if (showFaqChatbot) {
      FreeFaqChatbotDialog(
        config = config,
        orders = orders,
        coupons = coupons,
        products = products,
        onProductClick = { prod ->
          showFaqChatbot = false
          onProductClick(prod)
        },
        onOpenStoreMap = {
          showFaqChatbot = false
          activeFooterTopic = "contact"
        },
        onDismiss = { showFaqChatbot = false }
      )
    }
  }
}

@Composable
private fun StorefrontProductBlockCard(
  section: HomeSectionConfig,
  products: List<Product>,
  isExpanded: Boolean,
  cartItemCount: Int = 0,
  b2g1MinItems: Int = 2,
  b2g1FreeGiftName: String = "",
  allCategoriesList: List<String> = emptyList(),
  selectedInlineCategory: String = "All",
  onSelectInlineCategory: (String) -> Unit = {},
  onToggleExpand: (Int) -> Unit,
  onOpenFullCollection: () -> Unit,
  onProductClick: (Product) -> Unit,
  onAddToCart: (Product) -> Unit,
  design: StoreDesignConfig = StoreDesignConfig(),
  wishlistIds: Set<String> = emptySet(),
  onToggleWishlist: ((String) -> Unit)? = null,
  onMediaClick: ((Product) -> Unit)? = null
) {
  val cfg = remember(section) { resolveBlockGridConfig(section) }
  val defaultLimit = cfg.defaultLimit
  val displayProducts = remember(products, isExpanded, cfg) {
    if (isExpanded) products
    else if (cfg.isSlider) products.take(cfg.defaultLimit)
    else products.take(cfg.cols * cfg.rows)
  }

  val cardBadge = when (section.type) {
    "new_products" -> "NEW"
    "hot_sale" -> "HOT"
    "b2g1_offers" -> "B2G1"
    "best_deals" -> "DEAL"
    else -> null
  }
  val blockBg = if (design.useCustomBlockBg) parseColorHex(design.blockBgColor, Color.White) else Color.White
  val blockBorder = if (design.useCustomBlockBg) parseColorHex(design.blockBorderColor, BorderGold) else BorderGold.copy(alpha = 0.7f)
  val titleBg = parseColorHex(design.blockTitleBg, NavyBlue)
  val titleText = parseColorHex(design.blockTitleText, Color.White)
  val seeMoreBg = parseColorHex(design.seeMoreBg, NavyBlue)
  val seeMoreText = parseColorHex(design.seeMoreText, Color.White)

  Surface(
    shape = RoundedCornerShape(16.dp),
    color = blockBg,
    border = BorderStroke(1.dp, blockBorder),
    shadowElevation = 1.dp,
    modifier = Modifier
      .fillMaxWidth()
      .testTag("home_section_${section.id}")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      // Unified Pill Title Header + Matching "আরো দেখুন ›" Action
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(50),
          color = titleBg,
          shadowElevation = 2.dp,
          modifier = Modifier.weight(1f, fill = false)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 6.dp)
          ) {
            Text(text = section.icon.ifBlank { "✨" }, fontSize = 14.sp, color = titleText)
            Text(
              text = section.title,
              fontSize = 14.sp,
              fontWeight = FontWeight.Black,
              color = titleText,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        Spacer(modifier = Modifier.width(8.dp))

        if (products.isNotEmpty() || selectedInlineCategory != "All") {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            if (isExpanded) {
              Surface(
                shape = RoundedCornerShape(50),
                color = CreamSurface,
                border = BorderStroke(1.dp, NavyBlue.copy(alpha = 0.35f)),
                modifier = Modifier
                  .clip(RoundedCornerShape(50))
                  .clickable { onOpenFullCollection() }
                  .testTag("collection_modal_btn_${section.id}")
              ) {
                Text(
                  text = "সকল ফিল্টার ›",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = NavyBlue,
                  modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                )
              }
            }
            Surface(
              shape = RoundedCornerShape(50),
              color = seeMoreBg,
              shadowElevation = 2.dp,
              modifier = Modifier
                .clip(RoundedCornerShape(50))
                .clickable { onToggleExpand(defaultLimit) }
                .testTag("see_more_${section.id}")
            ) {
              Text(
                text = if (isExpanded) "কম দেখুন ▴" else "আরো দেখুন ›",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Black,
                color = seeMoreText,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
              )
            }
          }
        }
      }

      // Live B2G1 cart progress pill (counts strictly Admin-selected eligible items)
      if (section.type == "b2g1_offers") {
        val unlocked = cartItemCount >= b2g1MinItems
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = if (unlocked) Color(0xFFECFDF5) else Color(0xFFFFF8E1),
          border = BorderStroke(1.dp, if (unlocked) AccentGreen else BorderGold),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 8.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = if (unlocked) {
                if (b2g1FreeGiftName.isNotBlank()) "🎉 আনলকড! ফ্রি উপহার: $b2g1FreeGiftName" else "🎉 অভিনন্দন! Buy 2 Get 1 অফার আনলক হয়েছে!"
              } else {
                if (b2g1FreeGiftName.isNotBlank()) "🎁 নির্বাচিত ${b2g1MinItems}টি পণ্য নিলে ফ্রি: $b2g1FreeGiftName" else "🎁 নির্বাচিত ${b2g1MinItems}টি পণ্য নিলেই ১টি ফ্রি!"
              },
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = if (unlocked) Color(0xFF047857) else DarkEspresso,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (unlocked) AccentGreen else RoyalMaroon
            ) {
              Text(
                text = if (unlocked) "✓ ফ্রি আনলকড" else "${cartItemCount.coerceAtMost(b2g1MinItems)}/${b2g1MinItems}টি কার্টে",
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        }
      }

      // Inline Category Filter Chips for "সব প্রোডাক্ট" showcasing ALL categories
      if (section.type == "all_products" && allCategoriesList.isNotEmpty()) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .testTag("all_products_category_chips"),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          val chipList = listOf("All" to "🌟 সব ক্যাটাগরি (${products.size})") +
            allCategoriesList.map { it to it }
          chipList.forEach { (catKey, catLabel) ->
            val selected = selectedInlineCategory == catKey
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (selected) RoyalMaroon else CreamSurface,
              border = BorderStroke(1.dp, if (selected) RoyalMaroon else BorderGold),
              modifier = Modifier
                .clickable { onSelectInlineCategory(catKey) }
                .testTag("all_prod_chip_$catKey")
            ) {
              Text(
                text = if (catKey == "All") "🌟 সব ক্যাটাগরি" else catLabel,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (selected) Color.White else DarkEspresso,
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
              )
            }
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
      } else if (!isExpanded && cfg.isSlider) {
        // Horizontal slider matching exact configured columnsPerRow (2, 3, or 4 cards visible)
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
          val cardWidth = (maxWidth - (8.dp * (cfg.cols - 1))) / cfg.cols.toFloat()
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
                design = design,
                isWishlisted = wishlistIds.contains(prod.id),
                onToggleWishlist = onToggleWishlist?.let { toggle -> { toggle(prod.id) } },
                onMediaClick = onMediaClick,
                modifier = Modifier.width(cardWidth)
              )
            }
          }
        }
      } else {
        // Dynamic 2 / 3 / 4-Column Multi-Row Grid
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          displayProducts.chunked(cfg.cols).forEach { rowProducts ->
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
                  design = design,
                  isWishlisted = wishlistIds.contains(prod.id),
                  onToggleWishlist = onToggleWishlist?.let { toggle -> { toggle(prod.id) } },
                  onMediaClick = onMediaClick,
                  modifier = Modifier.weight(1f)
                )
              }
              repeat(cfg.cols - rowProducts.size) {
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
private fun StorefrontFooterCard(
  config: StoreConfig,
  onOpenTopic: (String) -> Unit = {}
) {
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

    val context = LocalContext.current
    var showMapPreviewDialog by remember { mutableStateOf(false) }

    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .clip(RoundedCornerShape(8.dp))
        .clickable { showMapPreviewDialog = true }
        .padding(vertical = 4.dp)
        .testTag("footer_google_map_link")
    ) {
      Icon(imageVector = Icons.Default.LocationOn, contentDescription = "গুগল ম্যাপ লোকেশন", tint = AntiqueGold, modifier = Modifier.size(14.dp))
      Spacer(modifier = Modifier.width(6.dp))
      Text(text = "${config.address} (ম্যাপ দেখুন ›)", fontSize = 11.sp, color = CreamSurface, maxLines = 2)
    }

    if (showMapPreviewDialog) {
      StoreGoogleMapDialog(
        config = config,
        onDismiss = { showMapPreviewDialog = false },
        onOpenExternalMap = { openStoreGoogleMap(context, config, directions = false) },
        onOpenDirections = { openStoreGoogleMap(context, config, directions = true) }
      )
    }

    Spacer(modifier = Modifier.height(14.dp))
    HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
    Spacer(modifier = Modifier.height(12.dp))

    // (1) Information, (2) Support, (3) Consumer Policy Columns
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.Top
    ) {
      FooterLinkColumn(
        title = "Information",
        items = listOf(
          "about" to "About us",
          "contact" to "Contact us",
          "terms" to "Terms & Conditions",
          "privacy" to "Privacy Policy"
        ),
        onItemClick = onOpenTopic,
        modifier = Modifier.weight(1f)
      )
      FooterLinkColumn(
        title = "Support",
        items = listOf(
          "tracking" to "Order Tracking",
          "payment" to "Payment",
          "shipping" to "Shipping",
          "faq" to "FAQ"
        ),
        onItemClick = onOpenTopic,
        modifier = Modifier.weight(1f)
      )
      FooterLinkColumn(
        title = "Consumer Policy",
        items = listOf(
          "refund" to "Refund Policy",
          "exchange" to "Exchange",
          "cancellation" to "Cancellation",
          "discount" to "Extra Discount"
        ),
        onItemClick = onOpenTopic,
        modifier = Modifier.weight(1f)
      )
    }

    Spacer(modifier = Modifier.height(12.dp))
    HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
    Spacer(modifier = Modifier.height(10.dp))

    Text(
      text = "নিরাপদ পেমেন্ট মেথড:",
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      color = AntiqueGold
    )

    Spacer(modifier = Modifier.height(6.dp))

    @OptIn(ExperimentalLayoutApi::class)
    FlowRow(
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      if (config.bkashEnabled) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFE2136E))
            .padding(horizontal = 8.dp, vertical = 3.dp)
            .testTag("footer_badge_bkash")
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
            .testTag("footer_badge_nagad")
        ) {
          Text(text = "Nagad", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
      }
      if (config.rocketEnabled) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF8C3494))
            .padding(horizontal = 8.dp, vertical = 3.dp)
            .testTag("footer_badge_rocket")
        ) {
          Text(text = "Rocket", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
      }
      if (config.codEnabled) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .padding(horizontal = 8.dp, vertical = 3.dp)
            .testTag("footer_badge_cod")
        ) {
          Text(text = "Cash on Delivery", color = DarkEspresso, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
      }
      if (config.visaEnabled) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1E3A8A))
            .padding(horizontal = 8.dp, vertical = 3.dp)
            .testTag("footer_badge_visa")
        ) {
          Text(text = "Visa / Mastercard", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
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

@Composable
private fun FooterLinkColumn(
  title: String,
  items: List<Pair<String, String>>,
  onItemClick: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
    Text(
      text = title,
      fontSize = 11.5.sp,
      fontWeight = FontWeight.Bold,
      color = AntiqueGold,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
    Spacer(modifier = Modifier.height(4.dp))
    items.forEach { (key, label) ->
      Text(
        text = "▪ $label",
        fontSize = 10.5.sp,
        color = CreamSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(6.dp))
          .clickable { onItemClick(key) }
          .padding(vertical = 5.dp)
          .testTag("footer_link_$key")
      )
    }
  }
}

@Composable
private fun FooterPolicyInfoDialog(
  initialTopic: String,
  config: StoreConfig,
  orders: List<Order>,
  coupons: List<Coupon>,
  onDismiss: () -> Unit,
  onOpenWhatsApp: () -> Unit
) {
  val context = LocalContext.current
  var selectedTopic by remember(initialTopic) { mutableStateOf(initialTopic) }
  var trackingQuery by remember { mutableStateOf("") }

  val topics = listOf(
    "about" to "About us",
    "contact" to "Contact us",
    "terms" to "Terms & Conditions",
    "privacy" to "Privacy Policy",
    "tracking" to "Order Tracking",
    "payment" to "Payment",
    "shipping" to "Shipping",
    "faq" to "FAQ",
    "refund" to "Refund Policy",
    "exchange" to "Exchange",
    "cancellation" to "Cancellation",
    "discount" to "Extra Discount"
  )
  val currentTitle = topics.find { it.first == selectedTopic }?.second ?: "Store Info"

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = Color.White,
      border = BorderStroke(1.dp, BorderGold),
      modifier = Modifier
        .fillMaxWidth()
        .heightIn(max = 580.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = config.storeName,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = AntiqueGoldDark
            )
            Text(
              text = currentTitle,
              fontSize = 17.sp,
              fontWeight = FontWeight.Black,
              color = RoyalMaroon
            )
          }
          IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "বন্ধ করুন", tint = DarkEspresso)
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          topics.forEach { (key, label) ->
            val isOn = key == selectedTopic
            Surface(
              shape = RoundedCornerShape(16.dp),
              color = if (isOn) RoyalMaroon else CreamSurface,
              border = BorderStroke(1.dp, if (isOn) RoyalMaroon else BorderGold),
              modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .clickable { selectedTopic = key }
            ) {
              Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isOn) Color.White else RoyalMaroon,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
          modifier = Modifier
            .weight(1f, fill = false)
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          when (selectedTopic) {
            "about" -> {
              Text(
                text = "${config.storeName} — আধুনিক ও রুচিশীল নারীদের জন্য প্রিমিয়াম ডিজাইনার শাড়ি ও রাজকীয় জুয়েলারি কালেকশন। আমাদের প্রতিটি পণ্য গুণগত মান যাচাই করে সরাসরি ক্রেতার হাতে পৌঁছে দেওয়া হয়।",
                fontSize = 12.5.sp,
                color = DarkEspresso,
                lineHeight = 18.sp
              )
            }
            "contact" -> {
              Text(text = "📞 হটলাইন: ${config.phone}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkEspresso)
              Text(text = "💬 WhatsApp: ${config.whatsapp}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkEspresso)
              Text(text = "📍 শোরুম: ${config.address}", fontSize = 12.sp, color = DarkEspresso)
              Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                  onClick = {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${config.phone}"))
                    context.startActivity(intent)
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon),
                  modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                ) {
                  Text("কল করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                  onClick = onOpenWhatsApp,
                  colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                  modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                ) {
                  Text("WhatsApp", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
              }
              Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                  onClick = { openStoreGoogleMap(context, config, directions = false) },
                  colors = ButtonDefaults.buttonColors(containerColor = NavyBlue),
                  modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
                    .testTag("contact_dialog_open_map_btn")
                ) {
                  Text("🗺️ গুগল ম্যাপ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                  onClick = { openStoreGoogleMap(context, config, directions = true) },
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                  modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
                    .testTag("contact_dialog_directions_btn")
                ) {
                  Text("🧭 ডিরেকশন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
            "terms" -> {
              Text(
                text = "• অর্ডার কনফার্ম করতে সঠিক নাম, মোবাইল নম্বর ও ঠিকানা প্রদান আবশ্যক।\n• স্টক প্রাপ্যতা অনুযায়ী ডেলিভারি নিশ্চিত করা হয়।\n• যেকোনো ভুয়া অর্ডার বা অপব্যবহারের ক্ষেত্রে স্টোর কর্তৃপক্ষ অর্ডার বাতিলের অধিকার সংরক্ষণ করে।",
                fontSize = 12.5.sp,
                color = DarkEspresso,
                lineHeight = 19.sp
              )
            }
            "privacy" -> {
              Text(
                text = "• ক্রেতার নাম, ফোন নম্বর ও ডেলিভারি ঠিকানা শুধুমাত্র অর্ডার প্রক্রিয়াকরণ ও কাস্টমার কেয়ারের জন্য ব্যবহার করা হয়।\n• ব্যক্তিগত তথ্য কোনো তৃতীয় পক্ষের কাছে বিক্রি বা শেয়ার করা হয় না।",
                fontSize = 12.5.sp,
                color = DarkEspresso,
                lineHeight = 19.sp
              )
            }
            "tracking" -> {
              OutlinedTextField(
                value = trackingQuery,
                onValueChange = { trackingQuery = it },
                label = { Text("অর্ডার ID, ট্র্যাকিং কোড বা ফোন নম্বর") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
              )
              val q = trackingQuery.trim().lowercase()
              val matched = if (q.isBlank()) emptyList() else orders.filter {
                it.id.lowercase().contains(q) ||
                  it.phone.lowercase().contains(q) ||
                  it.trackingId.lowercase().contains(q)
              }
              if (q.isBlank()) {
                Text(
                  text = "অর্ডার ID বা ফোন নম্বর লিখে আপনার পার্সেলের বর্তমান অবস্থা দেখুন।",
                  fontSize = 12.sp,
                  color = AntiqueGoldDark
                )
              } else if (matched.isEmpty()) {
                Text(
                  text = "\"$trackingQuery\" দিয়ে কোনো অর্ডার পাওয়া যায়নি।",
                  fontSize = 12.sp,
                  color = RoyalMaroon,
                  fontWeight = FontWeight.Bold
                )
              } else {
                matched.forEach { ord ->
                  Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CreamSurface,
                    border = BorderStroke(1.dp, BorderGold),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                      Text(text = "#${ord.id} • ${ord.status}", fontSize = 13.sp, fontWeight = FontWeight.Black, color = RoyalMaroon)
                      Text(text = "ক্রেতা: ${ord.customerName} • মোট: ৳${ord.total}", fontSize = 12.sp, color = DarkEspresso)
                      if (ord.trackingId.isNotBlank()) {
                        Text(text = "🚚 ট্র্যাকিং ID: ${ord.trackingId}", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = NavyBlue)
                      }
                      Spacer(modifier = Modifier.height(2.dp))
                      Button(
                        onClick = {
                          val smsMsg = "Notify me via SMS — সাজগোজ অর্ডার #${ord.id}" +
                            (if (ord.trackingId.isNotBlank()) " (ট্র্যাকিং: ${ord.trackingId})" else "") +
                            " | বর্তমান অবস্থা: ${ord.status} | ক্রেতার নম্বর: ${ord.phone}"
                          val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("smsto:${config.phone}")
                            putExtra("sms_body", smsMsg)
                          }
                          try {
                            context.startActivity(smsIntent)
                          } catch (_: Exception) {
                            Toast.makeText(context, "SMS অ্যাপ পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
                          }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyBlue),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                          .fillMaxWidth()
                          .heightIn(min = 48.dp)
                          .testTag("order_tracking_sms_btn_${ord.id}")
                      ) {
                        Text(
                          text = "📩 Notify me via SMS",
                          fontSize = 11.5.sp,
                          fontWeight = FontWeight.Bold
                        )
                      }
                    }
                  }
                }
              }
            }
            "payment" -> {
              Text(
                text = "উপলব্ধ পেমেন্ট মাধ্যম: ${listOfNotNull(
                  if (config.bkashEnabled) "bKash" else null,
                  if (config.nagadEnabled) "Nagad" else null,
                  if (config.rocketEnabled) "Rocket" else null,
                  if (config.codEnabled) "Cash on Delivery" else null,
                  if (config.visaEnabled) "Visa / Mastercard" else null
                ).joinToString(", ")}।\n\nসারা দেশে পণ্য হাতে পেয়ে ক্যাশ অন ডেলিভারিতে মূল্য পরিশোধ করা যায়।",
                fontSize = 12.5.sp,
                color = DarkEspresso,
                lineHeight = 19.sp
              )
            }
            "shipping" -> {
              Text(
                text = "🚚 ঢাকার ভেতরে ডেলিভারি চার্জ: ৳৭০ (১-২ কার্যদিবস)\n📦 ঢাকার বাইরে ডেলিভারি চার্জ: ৳১৩০ (২-৪ কার্যদিবস)\n🎁 ২০০০+ টাকার অর্ডারে ফ্রি হোম ডেলিভারি ও ${config.returnPolicyDays} দিনের রিটার্ন সুবিধা!",
                fontSize = 12.5.sp,
                color = DarkEspresso,
                lineHeight = 20.sp
              )
            }
            "faq" -> {
              Text(
                text = "প্রশ্ন: ডেলিভারি ম্যানের সামনে পণ্য চেক করা যাবে?\nউত্তর: হ্যাঁ, ডেলিভারি ম্যানের সামনেই যাচাই করে নিতে পারবেন।\n\nপ্রশ্ন: সাইজ বা ডিজাইন পরিবর্তন করা যাবে?\nউত্তর: হ্যাঁ, ৭ দিনের মধ্যে সহজ এক্সচেঞ্জ সুবিধা আছে।",
                fontSize = 12.5.sp,
                color = DarkEspresso,
                lineHeight = 19.sp
              )
            }
            "refund" -> {
              Text(
                text = "• ডেলিভারি পাওয়ার ৭ দিনের মধ্যে ত্রুটিপূর্ণ বা ভুল পণ্যে ১০০% রিফান্ড সুবিধা পাওয়া যাবে।\n• রিটার্ন যাচাইয়ের পর ২-৩ কর্মদিবসে bKash/Nagad/ব্যাংকে অর্থ ফেরত দেওয়া হয়।",
                fontSize = 12.5.sp,
                color = DarkEspresso,
                lineHeight = 19.sp
              )
            }
            "exchange" -> {
              Text(
                text = "• ডেলিভারির ৭ দিনের মধ্যে সাইজ বা কালার পরিবর্তনের জন্য হটলাইন (${config.phone}) বা WhatsApp-এ যোগাযোগ করুন।\n• আমাদের ভুলের কারণে এক্সচেঞ্জ হলে ডেলিভারি চার্জ ফ্রি।",
                fontSize = 12.5.sp,
                color = DarkEspresso,
                lineHeight = 19.sp
              )
            }
            "cancellation" -> {
              Text(
                text = "• কুরিয়ারে পার্সেল পাঠানোর আগে যেকোনো সময় অর্ডার বাতিল করা যাবে।\n• অর্ডার বাতিলের জন্য দ্রুত হটলাইন বা WhatsApp-এ অর্ডার ID জানান।",
                fontSize = 12.5.sp,
                color = DarkEspresso,
                lineHeight = 19.sp
              )
            }
            "discount" -> {
              val active = coupons.filter { it.isActive }
              if (active.isEmpty()) {
                Text(
                  text = "বর্তমানে Buy 2 Get 1 এবং ভাউচার ডিসকাউন্ট কার্ড থেকে অতিরিক্ত ছাড় উপভোগ করতে পারবেন!",
                  fontSize = 12.5.sp,
                  color = DarkEspresso
                )
              } else {
                active.forEach { cp ->
                  Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CreamSurface,
                    border = BorderStroke(1.dp, BorderGold),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Row(
                      modifier = Modifier.padding(10.dp),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column {
                        Text(text = cp.code, fontSize = 13.sp, fontWeight = FontWeight.Black, color = RoyalMaroon)
                        Text(
                          text = if (cp.type == "%" || cp.type.equals("percent", ignoreCase = true)) "${cp.value}% ছাড়" else "৳${cp.value} ছাড়",
                          fontSize = 11.5.sp,
                          color = DarkEspresso
                        )
                      }
                      Text(text = "সক্রিয় ✓", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AccentGreen)
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun StorefrontCatalogSortBar(
  productCount: Int,
  sortOption: ProductSortOption,
  onSortOptionSelected: (ProductSortOption) -> Unit
) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = Color.White,
    border = BorderStroke(1.dp, BorderGold.copy(alpha = 0.75f)),
    shadowElevation = 1.dp,
    modifier = Modifier
      .fillMaxWidth()
      .testTag("storefront_sort_bar")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = "🛍️ সকল পণ্য সাজান",
          fontSize = 12.5.sp,
          fontWeight = FontWeight.Bold,
          color = RoyalMaroon,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = "$productCount টি প্রিমিয়াম কালেকশন",
          fontSize = 10.5.sp,
          color = AntiqueGoldDark,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }

      Spacer(modifier = Modifier.width(8.dp))

      ProductSortDropdown(
        selectedOption = sortOption,
        onOptionSelected = onSortOptionSelected
      )
    }
  }
}

private fun resolveStoreMapQuery(config: StoreConfig): String =
  config.googleMapQuery.trim().ifBlank { config.address.trim().ifBlank { "Eastern Plaza, Hatirpool, Dhaka-1205" } }

private fun buildStoreMapOpenUrl(config: StoreConfig): String {
  val custom = config.googleMapUrl.trim()
  if (custom.startsWith("http")) return custom
  return "https://www.google.com/maps/search/?api=1&query=${Uri.encode(resolveStoreMapQuery(config))}"
}

private fun buildStoreMapDirectionsUrl(config: StoreConfig): String =
  "https://www.google.com/maps/dir/?api=1&destination=${Uri.encode(resolveStoreMapQuery(config))}"

private fun buildStoreMapEmbedUrl(config: StoreConfig): String =
  "https://maps.google.com/maps?q=${Uri.encode(resolveStoreMapQuery(config))}&t=&z=16&ie=UTF8&iwloc=&output=embed"

private fun openStoreGoogleMap(context: Context, config: StoreConfig, directions: Boolean) {
  val url = if (directions) buildStoreMapDirectionsUrl(config) else buildStoreMapOpenUrl(config)
  try {
    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
  } catch (_: Exception) {
    Toast.makeText(context, "Google Map খোলা সম্ভব হয়নি", Toast.LENGTH_SHORT).show()
  }
}

@Composable
private fun StorefrontGoogleMapCard(
  config: StoreConfig
) {
  val context = LocalContext.current
  var showLiveMapDialog by remember { mutableStateOf(false) }

  Surface(
    shape = RoundedCornerShape(18.dp),
    color = Color.White,
    border = BorderStroke(1.2.dp, BorderGold),
    shadowElevation = 2.dp,
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 12.dp)
      .testTag("storefront_google_map_card")
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
              Brush.linearGradient(
                listOf(RoyalMaroon, NavyBlue)
              )
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "শোরুম গুগল ম্যাপ লোকেশন",
            tint = AntiqueGold,
            modifier = Modifier.size(24.dp)
          )
        }
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "📍 Google Map Location — আমাদের শোরুম",
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Black,
            color = RoyalMaroon
          )
          Text(
            text = config.address,
            fontSize = 11.5.sp,
            color = DarkEspresso,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      Surface(
        shape = RoundedCornerShape(12.dp),
        color = CreamSurface,
        border = BorderStroke(1.dp, BorderGold.copy(alpha = 0.8f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(10.dp),
          verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
          Text(
            text = "🕒 প্রতিদিন সকাল ১০:০০ – রাত ০৯:৩০ • হটলাইন: ${config.phone}",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = DarkEspresso
          )
          Text(
            text = "🗺️ পিন: ${resolveStoreMapQuery(config)}",
            fontSize = 10.5.sp,
            color = AntiqueGoldDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = { showLiveMapDialog = true },
          colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon),
          shape = RoundedCornerShape(12.dp),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
          modifier = Modifier
            .weight(1f)
            .heightIn(min = 48.dp)
            .testTag("storefront_live_map_btn")
        ) {
          Text("লাইভ ম্যাপ", fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        }
        Button(
          onClick = { openStoreGoogleMap(context, config, directions = false) },
          colors = ButtonDefaults.buttonColors(containerColor = NavyBlue),
          shape = RoundedCornerShape(12.dp),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
          modifier = Modifier
            .weight(1f)
            .heightIn(min = 48.dp)
            .testTag("storefront_open_gmaps_btn")
        ) {
          Text("গুগল ম্যাপ ↗", fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        }
        Button(
          onClick = { openStoreGoogleMap(context, config, directions = true) },
          colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
          shape = RoundedCornerShape(12.dp),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
          modifier = Modifier
            .weight(1f)
            .heightIn(min = 48.dp)
            .testTag("storefront_directions_btn")
        ) {
          Text("ডিরেকশন", fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        }
      }
    }
  }

  if (showLiveMapDialog) {
    StoreGoogleMapDialog(
      config = config,
      onDismiss = { showLiveMapDialog = false },
      onOpenExternalMap = { openStoreGoogleMap(context, config, directions = false) },
      onOpenDirections = { openStoreGoogleMap(context, config, directions = true) }
    )
  }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun StoreGoogleMapDialog(
  config: StoreConfig,
  onDismiss: () -> Unit,
  onOpenExternalMap: () -> Unit,
  onOpenDirections: () -> Unit
) {
  val embedUrl = remember(config.googleMapQuery, config.address) { buildStoreMapEmbedUrl(config) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = CreamBackground,
      border = BorderStroke(1.2.dp, BorderGold),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("store_google_map_dialog")
    ) {
      Column(
        modifier = Modifier.padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "📍 ${config.storeName}",
              fontSize = 14.sp,
              fontWeight = FontWeight.Black,
              color = RoyalMaroon
            )
            Text(
              text = config.address,
              fontSize = 11.sp,
              color = DarkEspresso,
              maxLines = 2,
              overflow = TextOverflow.Ellipsis
            )
          }
          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(48.dp)
          ) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "বন্ধ করুন", tint = RoyalMaroon)
          }
        }

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, BorderGold, RoundedCornerShape(14.dp))
        ) {
          AndroidView(
            factory = { ctx ->
              WebView(ctx).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                webViewClient = WebViewClient()
                loadUrl(embedUrl)
              }
            },
            update = { webView ->
              if (webView.url != embedUrl) {
                webView.loadUrl(embedUrl)
              }
            },
            modifier = Modifier.fillMaxSize()
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Button(
            onClick = onOpenExternalMap,
            colors = ButtonDefaults.buttonColors(containerColor = NavyBlue),
            modifier = Modifier
              .weight(1f)
              .heightIn(min = 48.dp)
          ) {
            Text("গুগল ম্যাপ অ্যাপে খুলুন", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, maxLines = 1)
          }
          Button(
            onClick = onOpenDirections,
            colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
            modifier = Modifier
              .weight(1f)
              .heightIn(min = 48.dp)
          ) {
            Text("🧭 ডিরেকশন নিন", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, maxLines = 1)
          }
        }
      }
    }
  }
}


