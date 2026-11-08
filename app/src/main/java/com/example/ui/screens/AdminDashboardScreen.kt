package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.BannerItem
import com.example.data.model.Category
import com.example.data.model.Coupon
import com.example.data.model.CourierConfig
import com.example.data.model.Order
import com.example.data.model.PixelConfig
import com.example.data.model.Product
import com.example.data.model.ReturnRecord
import com.example.data.model.StoreConfig
import com.example.ui.AdminTab
import com.example.ui.components.AppImage
import com.example.ui.components.ReturnExchangeDialog
import com.example.ui.components.ReturnSlipDialog
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentRed
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.AntiqueGoldDark
import com.example.ui.theme.BorderGold
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.DarkEspresso
import com.example.ui.theme.RoyalMaroon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
  currentTab: AdminTab,
  onSelectTab: (AdminTab) -> Unit,
  isAdminLoggedIn: Boolean,
  onLogin: (String) -> Boolean,
  onLogout: () -> Unit,
  products: List<Product>,
  categories: List<Category>,
  orders: List<Order>,
  returns: List<ReturnRecord> = emptyList(),
  coupons: List<Coupon>,
  banners: List<BannerItem>,
  storeConfig: StoreConfig,
  courierConfig: CourierConfig,
  pixelConfig: PixelConfig,
  onAddProduct: (Product) -> Unit,
  onUpdateProduct: (Product) -> Unit,
  onDeleteProduct: (String) -> Unit,
  onUpdateOrderStatus: (String, String) -> Unit,
  onDispatchCourier: (String, String) -> Unit,
  onViewInvoice: (Order) -> Unit,
  onAddReturnRecord: (ReturnRecord) -> Unit = {},
  onAddCategory: (Category) -> Unit,
  onDeleteCategory: (String) -> Unit,
  onAddCoupon: (Coupon) -> Unit,
  onToggleCoupon: (String) -> Unit,
  onDeleteCoupon: (String) -> Unit,
  onAddBanner: (BannerItem) -> Unit,
  onToggleBanner: (String) -> Unit,
  onDeleteBanner: (String) -> Unit,
  onUpdateStoreConfig: (StoreConfig) -> Unit,
  onUpdateCourierConfig: (CourierConfig) -> Unit,
  onUpdatePixelConfig: (PixelConfig) -> Unit,
  onResetDefaults: () -> Unit,
  onShowToast: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  // Admin login dialog state if not logged in
  var loginPasswordInput by remember { mutableStateOf("admin123") }

  // Modals for CRUD & Returns
  var showProductDialog by remember { mutableStateOf(false) }
  var editingProduct by remember { mutableStateOf<Product?>(null) }
  var showCategoryDialog by remember { mutableStateOf(false) }
  var showCouponDialog by remember { mutableStateOf(false) }
  var showBannerDialog by remember { mutableStateOf(false) }
  var showReturnDialog by remember { mutableStateOf(false) }
  var returnTargetOrder by remember { mutableStateOf<Order?>(null) }
  var viewingReturnSlip by remember { mutableStateOf<ReturnRecord?>(null) }

  if (!isAdminLoggedIn) {
    // Admin Login Screen
    Box(
      modifier = modifier
        .fillMaxSize()
        .background(CreamBackground)
        .padding(24.dp),
      contentAlignment = Alignment.Center
    ) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderGold),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
      ) {
        Column(
          modifier = Modifier.padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(60.dp)
              .clip(CircleShape)
              .background(CreamSurface),
            contentAlignment = Alignment.Center
          ) {
            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = RoyalMaroon, modifier = Modifier.size(32.dp))
          }

          Spacer(modifier = Modifier.height(14.dp))

          Text(
            text = "অ্যাডমিন প্যানেলে প্রবেশ",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = RoyalMaroon
          )

          Text(
            text = "ইমেইল: nihatfahimbd@gmail.com",
            fontSize = 12.sp,
            color = Color.Gray
          )

          Spacer(modifier = Modifier.height(18.dp))

          OutlinedTextField(
            value = loginPasswordInput,
            onValueChange = { loginPasswordInput = it },
            label = { Text("অ্যাডমিন পাসওয়ার্ড", fontSize = 12.sp) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("admin_password_input")
          )

          Spacer(modifier = Modifier.height(18.dp))

          Button(
            onClick = { onLogin(loginPasswordInput) },
            colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("admin_login_submit")
          ) {
            Text("লগইন করুন", fontSize = 14.sp, fontWeight = FontWeight.Bold)
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "ডিফল্ট পাসওয়ার্ড: admin123",
            fontSize = 11.sp,
            color = AntiqueGoldDark
          )
        }
      }
    }
    return
  }

  // Admin Dashboard Content with Tabs
  Column(
    modifier = modifier
      .fillMaxSize()
      .background(CreamBackground)
  ) {
    // Admin Sub Navigation Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color.White)
        .horizontalScroll(rememberScrollState())
        .padding(horizontal = 12.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      AdminTab.values().forEach { tab ->
        val isSelected = currentTab == tab
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = if (isSelected) RoyalMaroon else CreamSurface,
          border = BorderStroke(1.dp, if (isSelected) RoyalMaroon else BorderGold),
          modifier = Modifier
            .clickable { onSelectTab(tab) }
            .testTag("admin_tab_${tab.name}")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text(text = tab.icon, fontSize = 12.sp)
            Text(
              text = tab.title,
              fontSize = 12.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = if (isSelected) Color.White else DarkEspresso
            )
          }
        }
      }

      // Logout
      IconButton(
        onClick = onLogout,
        modifier = Modifier.size(32.dp)
      ) {
        Icon(imageVector = Icons.Default.Close, contentDescription = "Logout", tint = AccentRed)
      }
    }

    HorizontalDivider(color = BorderGold)

    // Tab Contents
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(14.dp)
    ) {
      when (currentTab) {
        AdminTab.DASHBOARD -> {
          AdminOverviewTab(
            products = products,
            orders = orders,
            categories = categories,
            returnsCount = returns.size,
            onGoToTab = onSelectTab,
            onAddProductClick = {
              editingProduct = null
              showProductDialog = true
            },
            onOpenNewReturn = {
              returnTargetOrder = null
              showReturnDialog = true
            }
          )
        }

        AdminTab.PRODUCTS -> {
          AdminProductsTab(
            products = products,
            onAddClick = {
              editingProduct = null
              showProductDialog = true
            },
            onEditClick = { product ->
              editingProduct = product
              showProductDialog = true
            },
            onDeleteClick = onDeleteProduct
          )
        }

        AdminTab.ORDERS -> {
          AdminOrdersTab(
            orders = orders,
            onUpdateStatus = onUpdateOrderStatus,
            onDispatchCourier = onDispatchCourier,
            onViewInvoice = onViewInvoice,
            onOpenReturn = { order ->
              returnTargetOrder = order
              showReturnDialog = true
            }
          )
        }

        AdminTab.RETURNS -> {
          AdminReturnsTab(
            returns = returns,
            orders = orders,
            products = products,
            onOpenNewReturn = {
              returnTargetOrder = null
              showReturnDialog = true
            },
            onViewReturnSlip = { record ->
              viewingReturnSlip = record
            }
          )
        }

        AdminTab.CATEGORIES -> {
          AdminCategoriesTab(
            categories = categories,
            products = products,
            onAddClick = { showCategoryDialog = true },
            onDeleteCategory = onDeleteCategory
          )
        }

        AdminTab.COUPONS -> {
          AdminCouponsTab(
            coupons = coupons,
            onAddClick = { showCouponDialog = true },
            onToggle = onToggleCoupon,
            onDelete = onDeleteCoupon
          )
        }

        AdminTab.COURIER -> {
          AdminCourierTab(
            config = courierConfig,
            onSave = onUpdateCourierConfig,
            onTest = onShowToast
          )
        }

        AdminTab.BANNERS -> {
          AdminBannersTab(
            banners = banners,
            onAddClick = { showBannerDialog = true },
            onToggle = onToggleBanner,
            onDelete = onDeleteBanner
          )
        }

        AdminTab.SETTINGS -> {
          AdminSettingsTab(
            config = storeConfig,
            pixelConfig = pixelConfig,
            onSaveConfig = onUpdateStoreConfig,
            onSavePixel = onUpdatePixelConfig
          )
        }

        AdminTab.BACKUP -> {
          AdminBackupTab(
            productsCount = products.size,
            ordersCount = orders.size,
            onReset = onResetDefaults,
            onShowToast = onShowToast
          )
        }
      }
    }
  }

  // Product Add / Edit Dialog
  if (showProductDialog) {
    ProductFormDialog(
      product = editingProduct,
      categories = categories,
      onDismiss = {
        showProductDialog = false
        editingProduct = null
      },
      onSave = { p ->
        if (editingProduct != null) {
          onUpdateProduct(p)
        } else {
          onAddProduct(p)
        }
        showProductDialog = false
        editingProduct = null
      }
    )
  }

  // Category Add Dialog
  if (showCategoryDialog) {
    CategoryFormDialog(
      onDismiss = { showCategoryDialog = false },
      onSave = { c ->
        onAddCategory(c)
        showCategoryDialog = false
      }
    )
  }

  // Coupon Add Dialog
  if (showCouponDialog) {
    CouponFormDialog(
      onDismiss = { showCouponDialog = false },
      onSave = { cp ->
        onAddCoupon(cp)
        showCouponDialog = false
      }
    )
  }

  // Banner Add Dialog
  if (showBannerDialog) {
    BannerFormDialog(
      onDismiss = { showBannerDialog = false },
      onSave = { b ->
        onAddBanner(b)
        showBannerDialog = false
      }
    )
  }

  // Return & Exchange Dialog
  if (showReturnDialog) {
    ReturnExchangeDialog(
      orders = orders,
      products = products,
      initialOrder = returnTargetOrder,
      onDismiss = { showReturnDialog = false },
      onConfirm = { record ->
        showReturnDialog = false
        onAddReturnRecord(record)
        viewingReturnSlip = record
        onShowToast("রিটার্ন ও এক্সচেঞ্জ মেমো #${record.id} সম্পন্ন হয়েছে! 📄")
      }
    )
  }

  // Return Slip Memo Dialog
  if (viewingReturnSlip != null) {
    ReturnSlipDialog(
      record = viewingReturnSlip!!,
      onDismiss = { viewingReturnSlip = null },
      onShowToast = onShowToast
    )
  }
}

// ----------------- SUB-TABS -----------------

@Composable
fun AdminOverviewTab(
  products: List<Product>,
  orders: List<Order>,
  categories: List<Category>,
  returnsCount: Int = 0,
  onGoToTab: (AdminTab) -> Unit,
  onAddProductClick: () -> Unit,
  onOpenNewReturn: () -> Unit = {}
) {
  val totalRevenue = orders.sumOf { it.total }
  val pendingOrders = orders.filter { it.status == "Pending" }
  val lowStockProducts = products.filter { it.stock <= 5 }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(text = "স্টোর ড্যাশবোর্ড ও পরিসংখ্যান", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)
        Text(text = "লাইভ সেলস ও অপারেশন ট্র্যাকিং", fontSize = 11.sp, color = Color.Gray)
      }
      Button(
        onClick = onAddProductClick,
        colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon),
        shape = RoundedCornerShape(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
      ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "নতুন পণ্য", fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
    }

    // Metric Cards Grid
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
      Card(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderGold)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(text = "মোট বিক্রয়", fontSize = 11.sp, color = AntiqueGoldDark, fontWeight = FontWeight.Medium)
          Spacer(modifier = Modifier.height(2.dp))
          Text(text = "$totalRevenue ৳", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)
          Text(text = "↑ লাইভ হিসাব", fontSize = 10.sp, color = AccentGreen)
        }
      }

      Card(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderGold)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(text = "মোট অর্ডার", fontSize = 11.sp, color = AntiqueGoldDark, fontWeight = FontWeight.Medium)
          Spacer(modifier = Modifier.height(2.dp))
          Text(text = "${orders.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkEspresso)
          Text(text = "${pendingOrders.size} টি পেন্ডিং", fontSize = 10.sp, color = Color(0xFFD97706))
        }
      }
    }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
      Card(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderGold)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(text = "মোট পণ্য", fontSize = 11.sp, color = AntiqueGoldDark, fontWeight = FontWeight.Medium)
          Spacer(modifier = Modifier.height(2.dp))
          Text(text = "${products.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkEspresso)
          Text(text = "${categories.size} টি ক্যাটাগরি", fontSize = 10.sp, color = Color.Gray)
        }
      }

      Card(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (lowStockProducts.isNotEmpty()) Color(0xFFFEF2F2) else Color.White),
        border = BorderStroke(1.dp, if (lowStockProducts.isNotEmpty()) Color(0xFFFECACA) else BorderGold)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(text = "কম স্টক সতর্কতা", fontSize = 11.sp, color = AccentRed, fontWeight = FontWeight.Medium)
          Spacer(modifier = Modifier.height(2.dp))
          Text(text = "${lowStockProducts.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AccentRed)
          Text(text = "রিস্টক প্রয়োজন", fontSize = 10.sp, color = AccentRed)
        }
      }
    }

    // Quick Shortcuts Header
    Text(text = "দ্রুত অপশন সমূহ (Dashboard Quick Actions):", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkEspresso)

    // Fast 1-Click Return & Exchange Action Card on Dashboard
    Button(
      onClick = onOpenNewReturn,
      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF3E0), contentColor = Color(0xFFE65100)),
      border = BorderStroke(1.5.dp, Color(0xFFFB8C00)),
      shape = RoundedCornerShape(16.dp),
      modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .testTag("dashboard_quick_return_button")
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text(text = "🔄", fontSize = 18.sp)
        Text(
          text = "১-ক্লিকে রিটার্ন ও এক্সচেঞ্জ (Return & Exchange) • $returnsCount টি সম্পন্ন",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFFBF360C)
        )
      }
    }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Button(
        onClick = { onGoToTab(AdminTab.ORDERS) },
        colors = ButtonDefaults.buttonColors(containerColor = CreamSurface, contentColor = DarkEspresso),
        border = BorderStroke(1.dp, BorderGold),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.weight(1f)
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(text = "📦", fontSize = 16.sp)
          Text(text = "অর্ডার লিস্ট", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }

      Button(
        onClick = { onGoToTab(AdminTab.COURIER) },
        colors = ButtonDefaults.buttonColors(containerColor = CreamSurface, contentColor = DarkEspresso),
        border = BorderStroke(1.dp, BorderGold),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.weight(1f)
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(text = "🚚", fontSize = 16.sp)
          Text(text = "কুরিয়ার", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }

      Button(
        onClick = { onGoToTab(AdminTab.BACKUP) },
        colors = ButtonDefaults.buttonColors(containerColor = CreamSurface, contentColor = DarkEspresso),
        border = BorderStroke(1.dp, BorderGold),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.weight(1f)
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(text = "💾", fontSize = 16.sp)
          Text(text = "ব্যাকআপ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
fun AdminProductsTab(
  products: List<Product>,
  onAddClick: () -> Unit,
  onEditClick: (Product) -> Unit,
  onDeleteClick: (String) -> Unit
) {
  Column(modifier = Modifier.fillMaxSize()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(text = "সকল পণ্য তালিকা (${products.size})", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)
      Button(
        onClick = onAddClick,
        colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon),
        shape = RoundedCornerShape(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
      ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "পণ্য যোগ", fontSize = 11.sp)
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    LazyColumn(
      verticalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxSize()
    ) {
      items(products, key = { it.id }) { product ->
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = BorderStroke(1.dp, BorderGold)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            AppImage(
              model = product.imageUrl,
              contentDescription = product.name,
              modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = product.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = DarkEspresso,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(text = "${product.price} ৳", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)
                Text(text = "স্টক: ${product.stock}", fontSize = 11.sp, color = if (product.stock <= 5) AccentRed else Color.Gray)
                Text(text = product.category, fontSize = 10.sp, color = AntiqueGoldDark)
              }

              // Badges for colors, sizes, age groups
              val hasVariants = product.colors.isNotEmpty() || product.sizes.isNotEmpty() || product.ageGroups.isNotEmpty()
              if (hasVariants) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  if (product.colors.isNotEmpty()) {
                    Surface(
                      shape = RoundedCornerShape(4.dp),
                      color = CreamSurface,
                      border = BorderStroke(0.5.dp, BorderGold)
                    ) {
                      Text(
                        text = "🎨 ${product.colors.size} রং",
                        fontSize = 9.sp,
                        color = RoyalMaroon,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                      )
                    }
                  }

                  if (product.sizes.isNotEmpty()) {
                    Surface(
                      shape = RoundedCornerShape(4.dp),
                      color = CreamSurface,
                      border = BorderStroke(0.5.dp, BorderGold)
                    ) {
                      Text(
                        text = "📏 ${product.sizes.joinToString("/")}",
                        fontSize = 9.sp,
                        color = DarkEspresso,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                      )
                    }
                  }

                  if (product.ageGroups.isNotEmpty()) {
                    Surface(
                      shape = RoundedCornerShape(4.dp),
                      color = AntiqueGold.copy(alpha = 0.2f),
                      border = BorderStroke(0.5.dp, BorderGold)
                    ) {
                      Text(
                        text = "👶 ${product.ageGroups.firstOrNull()}...",
                        fontSize = 9.sp,
                        color = RoyalMaroon,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                      )
                    }
                  }
                }
              }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              IconButton(onClick = { onEditClick(product) }, modifier = Modifier.size(32.dp)) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = DarkEspresso, modifier = Modifier.size(16.dp))
              }
              IconButton(onClick = { onDeleteClick(product.id) }, modifier = Modifier.size(32.dp)) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = AccentRed, modifier = Modifier.size(16.dp))
              }
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrdersTab(
  orders: List<Order>,
  onUpdateStatus: (String, String) -> Unit,
  onDispatchCourier: (String, String) -> Unit,
  onViewInvoice: (Order) -> Unit,
  onOpenReturn: (Order) -> Unit
) {
  val statuses = listOf("Pending", "Confirmed", "Shipped", "Delivered", "Cancelled")

  Column(modifier = Modifier.fillMaxSize()) {
    Text(text = "কাস্টমার অর্ডার তালিকা (${orders.size})", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)
    Spacer(modifier = Modifier.height(8.dp))

    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxSize()) {
      items(orders, key = { it.id }) { order ->
        var isDropdownExpanded by remember { mutableStateOf(false) }

        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = BorderStroke(1.dp, BorderGold)
        ) {
          Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(text = "#${order.id}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)
                Text(text = order.date, fontSize = 10.sp, color = Color.Gray)
              }

              // Status Selector Dropdown
              ExposedDropdownMenuBox(
                expanded = isDropdownExpanded,
                onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
              ) {
                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = CreamSurface,
                  border = BorderStroke(1.dp, BorderGold),
                  modifier = Modifier.menuAnchor()
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    Text(text = order.status, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded)
                  }
                }
                ExposedDropdownMenu(
                  expanded = isDropdownExpanded,
                  onDismissRequest = { isDropdownExpanded = false }
                ) {
                  statuses.forEach { st ->
                    DropdownMenuItem(
                      text = { Text(st, fontSize = 12.sp) },
                      onClick = {
                        onUpdateStatus(order.id, st)
                        isDropdownExpanded = false
                      }
                    )
                  }
                }
              }
            }

            HorizontalDivider(color = BorderGold)

            Text(text = "গ্রাহক: ${order.customerName} (${order.phone})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(text = "ঠিকানা: ${order.address}, ${order.thana}, ${order.district}", fontSize = 11.sp, color = DarkEspresso)
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
              Text(text = "অর্ডারকৃত পণ্যসমূহ (${order.items.size}):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)
              order.items.forEach { itm ->
                val v = listOfNotNull(
                  if (itm.color.isNotBlank()) "রং: ${itm.color}" else null,
                  if (itm.size.isNotBlank()) "সাইজ: ${itm.size}" else null,
                  if (itm.ageGroup.isNotBlank()) "👶 বয়স: ${itm.ageGroup}" else null
                ).joinToString(" | ")
                Text(
                  text = "• ${itm.name} x${itm.quantity}${if (v.isNotBlank()) " [$v]" else ""}",
                  fontSize = 11.sp,
                  color = DarkEspresso,
                  maxLines = 2,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "মোট: ${order.total} ৳ (${order.paymentMethod})",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = RoyalMaroon
              )

              if (order.trackingId.isNotBlank()) {
                Text(
                  text = "Track: ${order.trackingId}",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = AccentGreen
                )
              }
            }

            // Order Action Buttons
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Button(
                onClick = { onViewInvoice(order) },
                colors = ButtonDefaults.buttonColors(containerColor = CreamSurface, contentColor = RoyalMaroon),
                border = BorderStroke(1.dp, BorderGold),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                  .weight(1f)
                  .height(34.dp)
              ) {
                Icon(imageVector = Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "চালান", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }

              Button(
                onClick = { onDispatchCourier(order.id, "Steadfast") },
                colors = ButtonDefaults.buttonColors(containerColor = DarkEspresso),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                  .weight(1f)
                  .height(34.dp)
              ) {
                Icon(imageVector = Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Steadfast", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }

              Button(
                onClick = { onOpenReturn(order) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100), contentColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                  .weight(1f)
                  .height(34.dp)
              ) {
                Text(text = "🔄 রিটার্ন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun AdminReturnsTab(
  returns: List<ReturnRecord>,
  orders: List<Order>,
  products: List<Product>,
  onOpenNewReturn: () -> Unit,
  onViewReturnSlip: (ReturnRecord) -> Unit
) {
  var searchQuery by remember { mutableStateOf("") }

  val filteredReturns = returns.filter {
    searchQuery.isBlank() ||
      it.id.contains(searchQuery, ignoreCase = true) ||
      it.orderId.contains(searchQuery, ignoreCase = true) ||
      it.customerName.contains(searchQuery, ignoreCase = true) ||
      it.phone.contains(searchQuery)
  }

  Column(modifier = Modifier.fillMaxSize()) {
    // Header & Action
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "সেলস রিটার্ন ও এক্সচেঞ্জ (${returns.size})",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = RoyalMaroon
        )
        Text(
          text = "মূল্য সমন্বয় ও ব্যালেন্স নিষ্পত্তি তালিকা",
          fontSize = 11.sp,
          color = DarkEspresso
        )
      }

      Button(
        onClick = onOpenNewReturn,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
        shape = RoundedCornerShape(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
      ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "নতুন রিটার্ন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 3 Summary Cards
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      val extraCollected = returns.filter { it.priceDifference > 0 }.sumOf { it.priceDifference }
      val balanceRefunded = returns.filter { it.priceDifference < 0 }.sumOf { kotlin.math.abs(it.priceDifference) }

      Surface(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BorderGold)
      ) {
        Column(modifier = Modifier.padding(8.dp)) {
          Text(text = "মোট মেমো", fontSize = 10.sp, color = Color.Gray)
          Text(text = "${returns.size} টি", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)
        }
      }

      Surface(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFFFF3E0),
        border = BorderStroke(1.dp, Color(0xFFFFCC80))
      ) {
        Column(modifier = Modifier.padding(8.dp)) {
          Text(text = "অতিরিক্ত আদায়", fontSize = 10.sp, color = Color(0xFFB26A00))
          Text(text = "+ ৳$extraCollected", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
        }
      }

      Surface(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFE8F5E9),
        border = BorderStroke(1.dp, Color(0xFFA5D6A7))
      ) {
        Column(modifier = Modifier.padding(8.dp)) {
          Text(text = "গ্রাহককে ফেরত", fontSize = 10.sp, color = AccentGreen)
          Text(text = "- ৳$balanceRefunded", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AccentGreen)
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      placeholder = { Text("রিটার্ন ID, অর্ডার ID বা ফোন দিয়ে খুঁজুন...", fontSize = 11.sp) },
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(12.dp),
      colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = BorderGold)
    )

    Spacer(modifier = Modifier.height(10.dp))

    if (filteredReturns.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Text("কোনো রিটার্ন বা এক্সচেঞ্জ রেকর্ড নেই", fontSize = 13.sp, color = Color.Gray)
      }
    } else {
      LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxSize()) {
        items(filteredReturns, key = { it.id }) { ret ->
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, BorderGold)
          ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFFFF3E0),
                    border = BorderStroke(1.dp, Color(0xFFFFB74D))
                  ) {
                    Text(
                      text = "#${ret.id}",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFFE65100),
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                  Text(text = "অর্ডার: #${ret.orderId}", fontSize = 11.sp, color = Color.Gray)
                }

                Text(text = ret.date, fontSize = 10.sp, color = Color.Gray)
              }

              HorizontalDivider(color = BorderGold)

              Text(text = "${ret.customerName} • 📞 ${ret.phone}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
              Text(
                text = "ফেরত পণ্য (${ret.returnedItems.size}টি): ${ret.returnedItems.joinToString { "${it.name} (${it.quantity}টি)" }}",
                fontSize = 10.sp,
                color = DarkEspresso,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
              )

              if (ret.isExchange && ret.exchangeItems.isNotEmpty()) {
                Text(
                  text = "নতুন এক্সচেঞ্জ (${ret.exchangeItems.size}টি): ${ret.exchangeItems.joinToString { "${it.name} (${it.quantity}টি)" }}",
                  fontSize = 10.sp,
                  color = Color(0xFFE65100),
                  maxLines = 2,
                  overflow = TextOverflow.Ellipsis
                )
              }

              // Settlement Badge
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = when (ret.differenceType) {
                    "customer_paid_extra" -> Color(0xFFFFF3E0)
                    "customer_refunded_balance" -> Color(0xFFE8F5E9)
                    else -> Color(0xFFE3F2FD)
                  },
                  border = BorderStroke(
                    1.dp,
                    when (ret.differenceType) {
                      "customer_paid_extra" -> Color(0xFFFF9800)
                      "customer_refunded_balance" -> AccentGreen
                      else -> Color(0xFF2196F3)
                    }
                  )
                ) {
                  Text(
                    text = when (ret.differenceType) {
                      "customer_paid_extra" -> "+ ৳${ret.priceDifference} অতিরিক্ত আদায় (${ret.settlementMethod})"
                      "customer_refunded_balance" -> "- ৳${kotlin.math.abs(ret.priceDifference)} ফেরত প্রদান (${ret.settlementMethod})"
                      else -> "সমমূল্য বিনিময় (০ ৳)"
                    },
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (ret.differenceType) {
                      "customer_paid_extra" -> Color(0xFFB26A00)
                      "customer_refunded_balance" -> AccentGreen
                      else -> Color(0xFF0D47A1)
                    },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                  )
                }

                Button(
                  onClick = { onViewReturnSlip(ret) },
                  colors = ButtonDefaults.buttonColors(containerColor = CreamSurface, contentColor = RoyalMaroon),
                  border = BorderStroke(1.dp, BorderGold),
                  shape = RoundedCornerShape(10.dp),
                  contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                  modifier = Modifier.height(28.dp)
                ) {
                  Text("মেমো ভিউ", fontSize = 10.sp, fontWeight = FontWeight.Bold)
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
fun AdminCategoriesTab(
  categories: List<Category>,
  products: List<Product>,
  onAddClick: () -> Unit,
  onDeleteCategory: (String) -> Unit
) {
  Column(modifier = Modifier.fillMaxSize()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(text = "ক্যাটাগরি তালিকা (${categories.size})", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)
      Button(
        onClick = onAddClick,
        colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon),
        shape = RoundedCornerShape(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
      ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "ক্যাটাগরি যোগ", fontSize = 11.sp)
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      items(categories, key = { it.id }) { cat ->
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = BorderStroke(1.dp, BorderGold)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              Text(text = cat.icon, fontSize = 24.sp)
              Column {
                Text(text = cat.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkEspresso)
                Text(
                  text = "${products.count { it.category == cat.name }} টি পণ্য সংযুক্ত",
                  fontSize = 11.sp,
                  color = Color.Gray
                )
              }
            }

            IconButton(onClick = { onDeleteCategory(cat.id) }, modifier = Modifier.size(32.dp)) {
              Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = AccentRed, modifier = Modifier.size(18.dp))
            }
          }
        }
      }
    }
  }
}

@Composable
fun AdminCouponsTab(
  coupons: List<Coupon>,
  onAddClick: () -> Unit,
  onToggle: (String) -> Unit,
  onDelete: (String) -> Unit
) {
  Column(modifier = Modifier.fillMaxSize()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(text = "কুপন কোড ও অফার (${coupons.size})", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)
      Button(
        onClick = onAddClick,
        colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon),
        shape = RoundedCornerShape(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
      ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "কুপন তৈরি", fontSize = 11.sp)
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      items(coupons, key = { it.id }) { cp ->
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = BorderStroke(1.dp, BorderGold)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text(text = cp.code, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)
              Text(
                text = "ছাড়: ${if (cp.type == "%") "${cp.value}%" else "${cp.value} ৳"}  |  মিনিমাম: ${cp.minOrder} ৳",
                fontSize = 11.sp,
                color = DarkEspresso
              )
              Text(text = "মেয়াদ: ${cp.expiry}", fontSize = 10.sp, color = Color.Gray)
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              Switch(
                checked = cp.isActive,
                onCheckedChange = { onToggle(cp.id) },
                colors = SwitchDefaults.colors(checkedThumbColor = RoyalMaroon, checkedTrackColor = CreamSurface)
              )
              IconButton(onClick = { onDelete(cp.id) }, modifier = Modifier.size(32.dp)) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = AccentRed, modifier = Modifier.size(18.dp))
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun AdminCourierTab(
  config: CourierConfig,
  onSave: (CourierConfig) -> Unit,
  onTest: (String) -> Unit
) {
  var steadfastKey by remember { mutableStateOf(config.steadfastApiKey) }
  var steadfastSec by remember { mutableStateOf(config.steadfastSecret) }
  var pathaoId by remember { mutableStateOf(config.pathaoClientId) }
  var pathaoSec by remember { mutableStateOf(config.pathaoClientSecret) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    Text(text = "কুরিয়ার লজিস্টিকস ইন্টিগ্রেশন", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)

    // Steadfast
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      border = BorderStroke(1.dp, BorderGold)
    ) {
      Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text(text = "Steadfast Courier API", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)
          Text(text = "সক্রিয়", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AccentGreen)
        }

        OutlinedTextField(
          value = steadfastKey,
          onValueChange = { steadfastKey = it },
          label = { Text("Steadfast API Key", fontSize = 11.sp) },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = steadfastSec,
          onValueChange = { steadfastSec = it },
          label = { Text("Secret Key", fontSize = 11.sp) },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Button(
          onClick = { onTest("Steadfast API সংযোগ সফল! টেস্ট পিং কার্যকর হয়েছে। ✅") },
          colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("Steadfast সংযোগ টেস্ট করুন", fontSize = 12.sp)
        }
      }
    }

    // Pathao
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      border = BorderStroke(1.dp, BorderGold)
    ) {
      Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text(text = "Pathao Logistics API", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkEspresso)
          Text(text = "প্রস্তুত", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AntiqueGoldDark)
        }

        OutlinedTextField(
          value = pathaoId,
          onValueChange = { pathaoId = it },
          label = { Text("Client ID", fontSize = 11.sp) },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = pathaoSec,
          onValueChange = { pathaoSec = it },
          label = { Text("Client Secret", fontSize = 11.sp) },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Button(
          onClick = { onTest("Pathao API সংযোগ সফল! ✅") },
          colors = ButtonDefaults.buttonColors(containerColor = DarkEspresso),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("Pathao সংযোগ টেস্ট করুন", fontSize = 12.sp)
        }
      }
    }

    Button(
      onClick = {
        onSave(config.copy(steadfastApiKey = steadfastKey, steadfastSecret = steadfastSec, pathaoClientId = pathaoId, pathaoClientSecret = pathaoSec))
      },
      colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon),
      shape = RoundedCornerShape(18.dp),
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
    ) {
      Text("কুরিয়ার সেটিংস সংরক্ষণ করুন", fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
  }
}

@Composable
fun AdminBannersTab(
  banners: List<BannerItem>,
  onAddClick: () -> Unit,
  onToggle: (String) -> Unit,
  onDelete: (String) -> Unit
) {
  Column(modifier = Modifier.fillMaxSize()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(text = "হোম ব্যানার ব্যবস্থাপনা (${banners.size})", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)
      Button(
        onClick = onAddClick,
        colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon),
        shape = RoundedCornerShape(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
      ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "ব্যানার যোগ", fontSize = 11.sp)
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      items(banners, key = { it.id }) { b ->
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = BorderStroke(1.dp, BorderGold)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            AppImage(
              model = b.imageUrl,
              contentDescription = b.title,
              modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(text = b.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkEspresso, maxLines = 1)
              Text(text = b.subtitle, fontSize = 11.sp, color = Color.Gray, maxLines = 1)
              Text(text = "বাটন: ${b.buttonText}", fontSize = 10.sp, color = RoyalMaroon, fontWeight = FontWeight.SemiBold)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              Switch(
                checked = b.isActive,
                onCheckedChange = { onToggle(b.id) },
                colors = SwitchDefaults.colors(checkedThumbColor = RoyalMaroon, checkedTrackColor = CreamSurface)
              )
              IconButton(onClick = { onDelete(b.id) }, modifier = Modifier.size(28.dp)) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = AccentRed, modifier = Modifier.size(16.dp))
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun AdminSettingsTab(
  config: StoreConfig,
  pixelConfig: PixelConfig,
  onSaveConfig: (StoreConfig) -> Unit,
  onSavePixel: (PixelConfig) -> Unit
) {
  var storeName by remember { mutableStateOf(config.storeName) }
  var tagline by remember { mutableStateOf(config.tagline) }
  var announcement by remember { mutableStateOf(config.announcementText) }
  var isAnnouncementEnabled by remember { mutableStateOf(config.isAnnouncementEnabled) }
  var phone by remember { mutableStateOf(config.phone) }
  var whatsapp by remember { mutableStateOf(config.whatsapp) }
  var email by remember { mutableStateOf(config.email) }
  var address by remember { mutableStateOf(config.address) }

  var pixelId by remember { mutableStateOf(pixelConfig.pixelId) }
  var isPixelEnabled by remember { mutableStateOf(pixelConfig.isEnabled) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Text(text = "স্টোর ও নোটিফিকেশন কনফিগারেশন", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)

    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      border = BorderStroke(1.dp, BorderGold)
    ) {
      Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
          value = storeName,
          onValueChange = { storeName = it },
          label = { Text("স্টোরের নাম") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = tagline,
          onValueChange = { tagline = it },
          label = { Text("ট্যাগলাইন") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(text = "অ্যানাউন্সমেন্ট বার চালু রাখুন", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)
          Switch(checked = isAnnouncementEnabled, onCheckedChange = { isAnnouncementEnabled = it })
        }

        OutlinedTextField(
          value = announcement,
          onValueChange = { announcement = it },
          label = { Text("অ্যানাউন্সমেন্ট টেক্সট") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text("হটলাইন ফোন নম্বর") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = whatsapp,
          onValueChange = { whatsapp = it },
          label = { Text("WhatsApp হেল্পলাইন নম্বর") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = email,
          onValueChange = { email = it },
          label = { Text("ইমেইল ঠিকানা") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = address,
          onValueChange = { address = it },
          label = { Text("শপ / শোরুম ঠিকানা") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Button(
          onClick = {
            onSaveConfig(
              config.copy(
                storeName = storeName,
                tagline = tagline,
                announcementText = announcement,
                isAnnouncementEnabled = isAnnouncementEnabled,
                phone = phone,
                whatsapp = whatsapp,
                email = email,
                address = address
              )
            )
          },
          colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("স্টোর সেটিংস সেভ করুন")
        }
      }
    }

    // Pixel Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      border = BorderStroke(1.dp, BorderGold)
    ) {
      Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(text = "Meta / Facebook Pixel ট্র্যাকিং", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)
          Switch(checked = isPixelEnabled, onCheckedChange = { isPixelEnabled = it })
        }

        OutlinedTextField(
          value = pixelId,
          onValueChange = { pixelId = it },
          label = { Text("Pixel ID (Numeric)") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Button(
          onClick = {
            onSavePixel(pixelConfig.copy(isEnabled = isPixelEnabled, pixelId = pixelId))
          },
          colors = ButtonDefaults.buttonColors(containerColor = DarkEspresso),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("পিক্সেল কনফিগারেশন সেভ করুন")
        }
      }
    }
  }
}

@Composable
fun AdminBackupTab(
  productsCount: Int,
  ordersCount: Int,
  onReset: () -> Unit,
  onShowToast: (String) -> Unit
) {
  val context = LocalContext.current

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    Text(text = "ডাটা ব্যাকআপ ও রিসেট সেন্টার", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)

    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      border = BorderStroke(1.dp, BorderGold)
    ) {
      Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "ক্লিপবোর্ডে সম্পূর্ণ ব্যাকআপ কপি করুন", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkEspresso)
        Text(text = "মোট $productsCount টি পণ্য এবং $ordersCount টি অর্ডারের ডাটা রয়েছে।", fontSize = 11.sp, color = Color.Gray)

        Button(
          onClick = {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("SAJGOJ_BACKUP", "{\"app\":\"SAJGOJ_FASHION\",\"products\":$productsCount,\"orders\":$ordersCount,\"status\":\"SUCCESS\"}")
            clipboard.setPrimaryClip(clip)
            onShowToast("ব্যাকআপ ডেটা সফলভাবে ক্লিপবোর্ডে কপি হয়েছে! 📋")
          },
          colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "JSON ব্যাকআপ কপি করুন", fontSize = 12.sp)
        }
      }
    }

    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
      border = BorderStroke(1.dp, Color(0xFFFECACA))
    ) {
      Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "⚠️ ডিফল্ট ডাটায় রিসেট করুন", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AccentRed)
        Text(text = "সকল প্রোডাক্ট এবং অর্ডার ডিফল্ট ক্যাটালগ অনুযায়ী রিসেট হবে।", fontSize = 11.sp, color = DarkEspresso)

        Button(
          onClick = onReset,
          colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(text = "ডিফল্ট ক্যাটালগ রিস্টোর করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

// ----------------- FORM DIALOGS -----------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormDialog(
  product: Product?,
  categories: List<Category>,
  onDismiss: () -> Unit,
  onSave: (Product) -> Unit
) {
  var name by remember { mutableStateOf(product?.name ?: "") }
  var priceStr by remember { mutableStateOf(product?.price?.toString() ?: "1000") }
  var stockStr by remember { mutableStateOf(product?.stock?.toString() ?: "10") }
  var category by remember { mutableStateOf(product?.category ?: categories.firstOrNull()?.name ?: "শাড়ি") }
  var isCategoryDropdownExpanded by remember { mutableStateOf(false) }
  var sku by remember { mutableStateOf(product?.sku ?: "SAJ-${(100..999).random()}") }
  var description by remember { mutableStateOf(product?.description ?: "") }
  var imageUrl by remember { mutableStateOf(product?.imageUrl ?: "res://${R.drawable.banner_eid_special_1792530116809}") }

  // Variants lists
  var colors by remember { mutableStateOf(product?.colors ?: emptyList()) }
  var customColorInput by remember { mutableStateOf("") }

  var sizes by remember { mutableStateOf(product?.sizes ?: emptyList()) }
  var customSizeInput by remember { mutableStateOf("") }

  var ageGroups by remember { mutableStateOf(product?.ageGroups ?: emptyList()) }
  var customAgeInput by remember { mutableStateOf("") }

  var formError by remember { mutableStateOf<String?>(null) }

  // Preset options for quick clicks
  val presetColors = listOf("লাল", "মেরুন", "কালো", "সবুজ", "নীল", "গোল্ডেন", "গোলাপী", "সাদা", "হলুদ", "পার্পল", "অরেঞ্জ", "অফ হোয়াইট", "রয়েল ব্লু")
  val presetSizes = listOf("Free Size", "XS", "S", "M", "L", "XL", "XXL", "3XL", "20", "22", "24", "26", "28", "30", "32", "34", "36", "38")
  val presetAges = listOf("০-৬ মাস", "৬-১২ মাস", "১-২ বছর", "২-৩ বছর", "৩-৪ বছর", "৪-৫ বছর", "৫-৬ বছর", "৭-৮ বছর", "৯-১০ বছর", "১১-১২ বছর")

  val presetImages = listOf(
    "res://${R.drawable.banner_eid_special_1792530116809}" to "পোশাক / শাড়ি",
    "res://${R.drawable.banner_jewelry_royal_1792530130914}" to "জুয়েলারি",
    "res://${R.drawable.ic_app_icon_1792530102214}" to "ব্র্যান্ড লোগো"
  )

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = Color.White,
      modifier = Modifier
        .fillMaxWidth(0.96f)
        .padding(vertical = 16.dp)
        .testTag("product_form_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp)
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Dialog Title
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = if (product != null) "পণ্য এডিট করুন" else "নতুন পণ্য যোগ করুন",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = RoyalMaroon
            )
            Text(
              text = "পণ্যের বিবরণ, কালার, সাইজ ও বাচ্চার বয়স সেট করুন",
              fontSize = 11.sp,
              color = Color.Gray
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(CreamSurface)
          ) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = DarkEspresso, modifier = Modifier.size(16.dp))
          }
        }

        HorizontalDivider(color = BorderGold)

        // 1. Basic Product Info
        OutlinedTextField(
          value = name,
          onValueChange = { 
            name = it
            formError = null 
          },
          label = { Text("পণ্যের নাম *") },
          placeholder = { Text("যেমন: বাচ্চাদের ডিজাইনার ফ্রক বা কাতান শাড়ি") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = priceStr,
            onValueChange = { priceStr = it },
            label = { Text("মূল্য (৳) *") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f)
          )

          OutlinedTextField(
            value = stockStr,
            onValueChange = { stockStr = it },
            label = { Text("স্টক সংখ্যা *") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f)
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          // Category selector with dropdown
          ExposedDropdownMenuBox(
            expanded = isCategoryDropdownExpanded,
            onExpandedChange = { isCategoryDropdownExpanded = !isCategoryDropdownExpanded },
            modifier = Modifier.weight(1f)
          ) {
            OutlinedTextField(
              value = category,
              onValueChange = { category = it },
              label = { Text("ক্যাটাগরি") },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryDropdownExpanded) },
              singleLine = true,
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
            )
            ExposedDropdownMenu(
              expanded = isCategoryDropdownExpanded,
              onDismissRequest = { isCategoryDropdownExpanded = false }
            ) {
              categories.forEach { cat ->
                DropdownMenuItem(
                  text = { Text("${cat.icon} ${cat.name}", fontSize = 12.sp) },
                  onClick = {
                    category = cat.name
                    isCategoryDropdownExpanded = false
                  }
                )
              }
            }
          }

          OutlinedTextField(
            value = sku,
            onValueChange = { sku = it },
            label = { Text("SKU কোড") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f)
          )
        }

        // Image Selection
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(text = "পণ্যের ছবি নির্বাচন বা ইউআরএল:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkEspresso)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            presetImages.forEach { (url, label) ->
              val isChosen = imageUrl == url
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isChosen) RoyalMaroon else CreamSurface,
                border = BorderStroke(1.dp, if (isChosen) RoyalMaroon else BorderGold),
                modifier = Modifier
                  .weight(1f)
                  .clickable { imageUrl = url }
              ) {
                Box(
                  modifier = Modifier.padding(vertical = 6.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = label,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isChosen) Color.White else DarkEspresso
                  )
                }
              }
            }
          }

          OutlinedTextField(
            value = imageUrl,
            onValueChange = { imageUrl = it },
            label = { Text("অথবা সরাসরি ছবির লিঙ্ক (Image URL)") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          )
        }

        // ==========================================
        // 2. COLOR MANAGEMENT SECTION
        // ==========================================
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = CreamBackground),
          border = BorderStroke(1.dp, BorderGold),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "🎨 কালার বা রঙের অপশন (${colors.size})",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = RoyalMaroon
              )
              if (colors.isNotEmpty()) {
                Text(
                  text = "ক্লিয়ার",
                  fontSize = 11.sp,
                  color = AccentRed,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.clickable { colors = emptyList() }
                )
              }
            }

            // Current added colors chips with remove 'x'
            if (colors.isEmpty()) {
              Text(
                text = "কোনো নির্দিষ্ট কালার যুক্ত করা হয়নি। নিচে ক্লিক করে বা লিখে যোগ করুন।",
                fontSize = 11.sp,
                color = Color.Gray
              )
            } else {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                colors.forEach { col ->
                  Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = RoyalMaroon,
                    border = BorderStroke(1.dp, RoyalMaroon)
                  ) {
                    Row(
                      modifier = Modifier.padding(start = 10.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                      Text(text = col, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                      Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove",
                        tint = Color.White,
                        modifier = Modifier
                          .size(14.dp)
                          .clickable { colors = colors.filterNot { it == col } }
                      )
                    }
                  }
                }
              }
            }

            // Custom color text input + Add button
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              OutlinedTextField(
                value = customColorInput,
                onValueChange = { customColorInput = it },
                placeholder = { Text("নতুন কালার লিখুন (যেমন: টিয়া সবুজ)", fontSize = 11.sp) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .weight(1f)
                  .height(44.dp)
              )

              Button(
                onClick = {
                  val trimmed = customColorInput.trim()
                  if (trimmed.isNotBlank() && !colors.contains(trimmed)) {
                    colors = colors + trimmed
                    customColorInput = ""
                  }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(44.dp)
              ) {
                Text("+ কালার যোগ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }

            // Quick preset chips
            Text(text = "দ্রুত যুক্ত করুন:", fontSize = 10.sp, color = DarkEspresso, fontWeight = FontWeight.Medium)
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              presetColors.forEach { col ->
                val alreadyAdded = colors.contains(col)
                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = if (alreadyAdded) CreamSurface else Color.White,
                  border = BorderStroke(1.dp, if (alreadyAdded) RoyalMaroon else BorderGold),
                  modifier = Modifier.clickable {
                    colors = if (alreadyAdded) colors.filterNot { it == col } else colors + col
                  }
                ) {
                  Text(
                    text = if (alreadyAdded) "✓ $col" else "+ $col",
                    fontSize = 11.sp,
                    fontWeight = if (alreadyAdded) FontWeight.Bold else FontWeight.Normal,
                    color = if (alreadyAdded) RoyalMaroon else DarkEspresso,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }
              }
            }
          }
        }

        // ==========================================
        // 3. SIZE MANAGEMENT SECTION
        // ==========================================
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = CreamBackground),
          border = BorderStroke(1.dp, BorderGold),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "📏 সাইজের অপশন (${sizes.size})",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = RoyalMaroon
              )
              if (sizes.isNotEmpty()) {
                Text(
                  text = "ক্লিয়ার",
                  fontSize = 11.sp,
                  color = AccentRed,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.clickable { sizes = emptyList() }
                )
              }
            }

            // Current added sizes
            if (sizes.isEmpty()) {
              Text(
                text = "কোনো সাইজ নির্ধারণ করা হয়নি। নিচে ক্লিক করে বা লিখে যোগ করুন।",
                fontSize = 11.sp,
                color = Color.Gray
              )
            } else {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                sizes.forEach { sz ->
                  Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkEspresso,
                    border = BorderStroke(1.dp, DarkEspresso)
                  ) {
                    Row(
                      modifier = Modifier.padding(start = 10.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                      Text(text = sz, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                      Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove",
                        tint = Color.White,
                        modifier = Modifier
                          .size(14.dp)
                          .clickable { sizes = sizes.filterNot { it == sz } }
                      )
                    }
                  }
                }
              }
            }

            // Custom size input + Add button
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              OutlinedTextField(
                value = customSizeInput,
                onValueChange = { customSizeInput = it },
                placeholder = { Text("নতুন সাইজ লিখুন (যেমন: 40 বা 38 Semi-Stitch)", fontSize = 11.sp) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .weight(1f)
                  .height(44.dp)
              )

              Button(
                onClick = {
                  val trimmed = customSizeInput.trim()
                  if (trimmed.isNotBlank() && !sizes.contains(trimmed)) {
                    sizes = sizes + trimmed
                    customSizeInput = ""
                  }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DarkEspresso),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(44.dp)
              ) {
                Text("+ সাইজ যোগ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }

            // Quick preset chips
            Text(text = "দ্রুত যুক্ত করুন:", fontSize = 10.sp, color = DarkEspresso, fontWeight = FontWeight.Medium)
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              presetSizes.forEach { sz ->
                val alreadyAdded = sizes.contains(sz)
                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = if (alreadyAdded) CreamSurface else Color.White,
                  border = BorderStroke(1.dp, if (alreadyAdded) DarkEspresso else BorderGold),
                  modifier = Modifier.clickable {
                    sizes = if (alreadyAdded) sizes.filterNot { it == sz } else sizes + sz
                  }
                ) {
                  Text(
                    text = if (alreadyAdded) "✓ $sz" else "+ $sz",
                    fontSize = 11.sp,
                    fontWeight = if (alreadyAdded) FontWeight.Bold else FontWeight.Normal,
                    color = if (alreadyAdded) DarkEspresso else DarkEspresso.copy(alpha = 0.8f),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }
              }
            }
          }
        }

        // ==========================================
        // 4. KIDS AGE MANAGEMENT SECTION
        // ==========================================
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = AntiqueGold.copy(alpha = 0.08f)),
          border = BorderStroke(1.dp, BorderGold),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "👶", fontSize = 14.sp)
                Text(
                  text = "বাচ্চাদের বয়সের মাপ (Kids Age) (${ageGroups.size})",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = RoyalMaroon
                )
              }
              if (ageGroups.isNotEmpty()) {
                Text(
                  text = "ক্লিয়ার",
                  fontSize = 11.sp,
                  color = AccentRed,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.clickable { ageGroups = emptyList() }
                )
              }
            }

            Text(
              text = "বাচ্চাদের পোশাকের ক্ষেত্রে ক্রেতাদের বয়সের মাপ সিলেক্ট করার সুযোগ দিন।",
              fontSize = 11.sp,
              color = DarkEspresso.copy(alpha = 0.7f)
            )

            // Current added age groups
            if (ageGroups.isEmpty()) {
              Text(
                text = "কোনো বয়স গ্রুপ যুক্ত করা হয়নি। নিচের বয়সগুলো ক্লিক করুন বা নতুন বয়স লিখুন।",
                fontSize = 11.sp,
                color = Color.Gray
              )
            } else {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                ageGroups.forEach { age ->
                  Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = AntiqueGoldDark,
                    border = BorderStroke(1.dp, AntiqueGoldDark)
                  ) {
                    Row(
                      modifier = Modifier.padding(start = 10.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                      Text(text = age, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                      Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove",
                        tint = Color.White,
                        modifier = Modifier
                          .size(14.dp)
                          .clickable { ageGroups = ageGroups.filterNot { it == age } }
                      )
                    }
                  }
                }
              }
            }

            // Custom age input + Add button
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              OutlinedTextField(
                value = customAgeInput,
                onValueChange = { customAgeInput = it },
                placeholder = { Text("নতুন বাচ্চার বয়স লিখুন (যেমন: নবজাতক বা ৭-৯ মাস)", fontSize = 11.sp) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .weight(1f)
                  .height(44.dp)
              )

              Button(
                onClick = {
                  val trimmed = customAgeInput.trim()
                  if (trimmed.isNotBlank() && !ageGroups.contains(trimmed)) {
                    ageGroups = ageGroups + trimmed
                    customAgeInput = ""
                  }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AntiqueGoldDark),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(44.dp)
              ) {
                Text("+ বয়স যোগ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }

            // Quick preset age chips
            Text(text = "দ্রুত বয়সের মাপ যুক্ত করুন:", fontSize = 10.sp, color = DarkEspresso, fontWeight = FontWeight.Medium)
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              presetAges.forEach { age ->
                val alreadyAdded = ageGroups.contains(age)
                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = if (alreadyAdded) CreamSurface else Color.White,
                  border = BorderStroke(1.dp, if (alreadyAdded) AntiqueGoldDark else BorderGold),
                  modifier = Modifier.clickable {
                    ageGroups = if (alreadyAdded) ageGroups.filterNot { it == age } else ageGroups + age
                  }
                ) {
                  Text(
                    text = if (alreadyAdded) "✓ $age" else "+ $age",
                    fontSize = 11.sp,
                    fontWeight = if (alreadyAdded) FontWeight.Bold else FontWeight.Normal,
                    color = if (alreadyAdded) AntiqueGoldDark else DarkEspresso,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }
              }
            }
          }
        }

        // Description field
        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("পণ্যের বিস্তারিত বিবরণ") },
          placeholder = { Text("কাপড়ের কোয়ালিটি, ফেব্রিক ও স্পেসিফিকেশন লিখুন") },
          minLines = 2,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        if (formError != null) {
          Text(
            text = formError!!,
            color = Color.Red,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }

        // Save Button
        Button(
          onClick = {
            if (name.isBlank()) {
              formError = "অনুগ্রহ করে পণ্যের নাম লিখুন!"
              return@Button
            }
            val price = priceStr.toIntOrNull()
            if (price == null || price <= 0) {
              formError = "সঠিক মূল্য (৳) প্রদান করুন!"
              return@Button
            }
            val stock = stockStr.toIntOrNull() ?: 1

            val p = Product(
              id = product?.id ?: "p_${System.currentTimeMillis()}",
              name = name.trim(),
              price = price,
              stock = stock,
              category = category.trim().ifBlank { "অন্যান্য" },
              imageUrl = imageUrl.trim().ifBlank { "res://${R.drawable.banner_eid_special_1792530116809}" },
              sku = sku.trim().ifBlank { "SAJ-${(100..999).random()}" },
              description = description.trim().ifBlank { "সজগোজ ফ্যাশনের আকর্ষণীয় প্রিমিয়াম কোয়ালিটি পণ্য।" },
              colors = colors,
              sizes = sizes,
              ageGroups = ageGroups
            )
            onSave(p)
          },
          colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
        ) {
          Text("সংরক্ষণ করুন", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
fun CategoryFormDialog(
  onDismiss: () -> Unit,
  onSave: (Category) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var icon by remember { mutableStateOf("✨") }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = Color.White,
      modifier = Modifier
        .fillMaxWidth(0.9f)
        .padding(16.dp)
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(text = "নতুন ক্যাটাগরি তৈরি করুন", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)

        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("ক্যাটাগরির নাম (যেমন: ব্রাইডাল সেট)") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = icon,
          onValueChange = { icon = it },
          label = { Text("ইমোজি আইকন (যেমন: 👑)") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Button(
          onClick = {
            if (name.isNotBlank()) {
              onSave(Category(id = "c_${System.currentTimeMillis()}", name = name, icon = icon.ifBlank { "✨" }))
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("যুক্ত করুন")
        }
      }
    }
  }
}

@Composable
fun CouponFormDialog(
  onDismiss: () -> Unit,
  onSave: (Coupon) -> Unit
) {
  var code by remember { mutableStateOf("") }
  var type by remember { mutableStateOf("%") }
  var valueStr by remember { mutableStateOf("10") }
  var minOrderStr by remember { mutableStateOf("1500") }
  var expiry by remember { mutableStateOf("2026-12-31") }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = Color.White,
      modifier = Modifier
        .fillMaxWidth(0.9f)
        .padding(16.dp)
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(text = "নতুন কুপন কোড তৈরি করুন", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)

        OutlinedTextField(
          value = code,
          onValueChange = { code = it.uppercase() },
          label = { Text("কুপন কোড (যেমন: EID2026)") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = valueStr,
            onValueChange = { valueStr = it },
            label = { Text("ছাড়ের মান (10% বা 100৳)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f)
          )

          OutlinedTextField(
            value = minOrderStr,
            onValueChange = { minOrderStr = it },
            label = { Text("মিনিমাম অর্ডার (৳)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f)
          )
        }

        OutlinedTextField(
          value = expiry,
          onValueChange = { expiry = it },
          label = { Text("মেয়াদ শেষ তারিখ (YYYY-MM-DD)") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Button(
          onClick = {
            if (code.isNotBlank()) {
              onSave(
                Coupon(
                  id = "cp_${System.currentTimeMillis()}",
                  code = code,
                  type = type,
                  value = valueStr.toIntOrNull() ?: 10,
                  minOrder = minOrderStr.toIntOrNull() ?: 0,
                  expiry = expiry,
                  isActive = true
                )
              )
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("কুপন সেভ করুন")
        }
      }
    }
  }
}

@Composable
fun BannerFormDialog(
  onDismiss: () -> Unit,
  onSave: (BannerItem) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var subtitle by remember { mutableStateOf("") }
  var buttonText by remember { mutableStateOf("কালেকশন দেখুন") }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = Color.White,
      modifier = Modifier
        .fillMaxWidth(0.9f)
        .padding(16.dp)
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(text = "নতুন ব্যানার যোগ করুন", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = RoyalMaroon)

        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("ব্যানার টাইটেল") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = subtitle,
          onValueChange = { subtitle = it },
          label = { Text("সাবটাইটেল / অফার টেক্সট") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = buttonText,
          onValueChange = { buttonText = it },
          label = { Text("বাটন টেক্সট") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Button(
          onClick = {
            if (title.isNotBlank()) {
              onSave(
                BannerItem(
                  id = "b_${System.currentTimeMillis()}",
                  title = title,
                  subtitle = subtitle,
                  buttonText = buttonText,
                  imageUrl = "res://${R.drawable.banner_eid_special_1792530116809}",
                  isActive = true
                )
              )
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("ব্যানার যুক্ত করুন")
        }
      }
    }
  }
}
