package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import com.example.data.model.ProductReview
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
  reviews: List<ProductReview> = emptyList(),
  onDismiss: () -> Unit,
  onAddToCart: (Product, Int) -> Unit,
  onAddReview: (rating: Int, customerName: String, comment: String) -> Unit = { _, _, _ -> }
) {
  var quantity by remember { mutableIntStateOf(1) }

  // Review Form States
  var isReviewFormOpen by remember { mutableStateOf(false) }
  var reviewRating by remember { mutableIntStateOf(5) }
  var reviewerName by remember { mutableStateOf("") }
  var reviewComment by remember { mutableStateOf("") }
  var reviewError by remember { mutableStateOf<String?>(null) }

  // Calculate review metrics
  val totalReviews = reviews.size
  val avgRating = if (totalReviews > 0) {
    (reviews.map { it.rating }.average() * 10).toInt() / 10f
  } else {
    product.rating
  }

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
                text = "$avgRating (${if (totalReviews > 0) totalReviews else product.reviewsCount} রিভিউ)",
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

          Spacer(modifier = Modifier.height(24.dp))
          HorizontalDivider(color = BorderGold.copy(alpha = 0.5f))
          Spacer(modifier = Modifier.height(20.dp))

          // ==========================================
          // CUSTOMER REVIEWS & RATINGS SECTION
          // ==========================================
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "গ্রাহক রিভিউ ও রেটিং",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = RoyalMaroon
              )
              Text(
                text = "${totalReviews} জন ক্রেতার মূল্যায়ন",
                fontSize = 11.sp,
                color = DarkEspresso.copy(alpha = 0.7f)
              )
            }

            OutlinedButton(
              onClick = { isReviewFormOpen = !isReviewFormOpen },
              colors = ButtonDefaults.outlinedButtonColors(
                contentColor = RoyalMaroon
              ),
              border = androidx.compose.foundation.BorderStroke(1.dp, RoyalMaroon),
              shape = RoundedCornerShape(20.dp),
              modifier = Modifier.testTag("write_review_button")
            ) {
              Icon(
                imageVector = Icons.Default.RateReview,
                contentDescription = null,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (isReviewFormOpen) "বন্ধ করুন" else "রিভিউ দিন",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Overall Rating Box & Breakdown
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .background(CreamBackground)
              .border(1.dp, BorderGold.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
              .padding(14.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
              // Score column
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(90.dp)
              ) {
                Text(
                  text = "%.1f".format(avgRating),
                  fontSize = 32.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = RoyalMaroon
                )
                Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                  val rounded = avgRating.toInt()
                  for (i in 1..5) {
                    Icon(
                      imageVector = if (i <= rounded) Icons.Default.Star else Icons.Outlined.StarBorder,
                      contentDescription = null,
                      tint = AntiqueGold,
                      modifier = Modifier.size(14.dp)
                    )
                  }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "সর্বমোট $totalReviews রিভিউ",
                  fontSize = 10.sp,
                  color = DarkEspresso.copy(alpha = 0.7f),
                  fontWeight = FontWeight.Medium
                )
              }

              // Rating distribution bars
              Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                for (star in 5 downTo 1) {
                  val starCount = reviews.count { it.rating == star }
                  val progress = if (totalReviews > 0) starCount.toFloat() / totalReviews else 0f
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Text(
                      text = "${star}★",
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = DarkEspresso,
                      modifier = Modifier.width(18.dp)
                    )
                    LinearProgressIndicator(
                      progress = { progress },
                      modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                      color = AntiqueGold,
                      trackColor = Color.White
                    )
                    Text(
                      text = starCount.toString(),
                      fontSize = 10.sp,
                      color = DarkEspresso.copy(alpha = 0.6f),
                      modifier = Modifier.width(18.dp)
                    )
                  }
                }
              }
            }
          }

          // Expandable Review Submission Form
          AnimatedVisibility(
            visible = isReviewFormOpen,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(CreamSurface)
                .border(1.dp, BorderGold, RoundedCornerShape(18.dp))
                .padding(16.dp)
                .testTag("review_submission_form")
            ) {
              Text(
                text = "আপনার মতামত ও রেটিং দিন",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = RoyalMaroon
              )

              Spacer(modifier = Modifier.height(10.dp))

              // Star Picker (1-5)
              Text(
                text = "রেটিং নির্বাচন করুন:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = DarkEspresso
              )
              Spacer(modifier = Modifier.height(6.dp))
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 4.dp)
              ) {
                for (star in 1..5) {
                  Box(
                    modifier = Modifier
                      .size(36.dp)
                      .clip(CircleShape)
                      .background(if (star <= reviewRating) AntiqueGold.copy(alpha = 0.15f) else Color.Transparent)
                      .clickable { reviewRating = star }
                      .testTag("star_rate_$star"),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = if (star <= reviewRating) Icons.Default.Star else Icons.Outlined.StarBorder,
                      contentDescription = "$star Stars",
                      tint = if (star <= reviewRating) AntiqueGold else Color.Gray,
                      modifier = Modifier.size(24.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = when (reviewRating) {
                    5 -> "অসাধারণ! (৫/৫)"
                    4 -> "খুব ভালো (৪/৫)"
                    3 -> "ভালো (৩/৫)"
                    2 -> "সন্তোষজনক নয় (২/৫)"
                    else -> "খারাপ (১/৫)"
                  },
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = AntiqueGoldDark
                )
              }

              Spacer(modifier = Modifier.height(10.dp))

              // Name Input
              OutlinedTextField(
                value = reviewerName,
                onValueChange = { 
                  reviewerName = it
                  reviewError = null 
                },
                label = { Text("আপনার নাম (ঐচ্ছিক)") },
                placeholder = { Text("উদাঃ ফাতেমা বেগম") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = RoyalMaroon,
                  unfocusedBorderColor = BorderGold,
                  focusedContainerColor = Color.White,
                  unfocusedContainerColor = Color.White
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("review_name_input")
              )

              Spacer(modifier = Modifier.height(10.dp))

              // Comment Input
              OutlinedTextField(
                value = reviewComment,
                onValueChange = { 
                  reviewComment = it
                  reviewError = null
                },
                label = { Text("মতামত বা মন্তব্য লিখুন *") },
                placeholder = { Text("প্রোডাক্টের কাপড়ের কোয়ালিটি, ফিনিশিং কেমন লেগেছে?") },
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = RoyalMaroon,
                  unfocusedBorderColor = BorderGold,
                  focusedContainerColor = Color.White,
                  unfocusedContainerColor = Color.White
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("review_comment_input")
              )

              if (reviewError != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = reviewError!!,
                  color = Color.Red,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium
                )
              }

              Spacer(modifier = Modifier.height(14.dp))

              Button(
                onClick = {
                  if (reviewComment.isBlank()) {
                    reviewError = "অনুগ্রহ করে আপনার মন্তব্য বা রিভিউ লিখুন!"
                    return@Button
                  }
                  onAddReview(reviewRating, reviewerName.trim(), reviewComment.trim())
                  // Reset form
                  reviewerName = ""
                  reviewComment = ""
                  reviewRating = 5
                  reviewError = null
                  isReviewFormOpen = false
                },
                colors = ButtonDefaults.buttonColors(
                  containerColor = RoyalMaroon,
                  contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(44.dp)
                  .testTag("submit_review_button")
              ) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "রিভিউ জমা দিন",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Customer Reviews List
          if (reviews.isEmpty()) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(CreamBackground)
                .padding(20.dp),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = "💬",
                  fontSize = 28.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = "এখনও কোনো রিভিউ যুক্ত হয়নি",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = DarkEspresso
                )
                Text(
                  text = "প্রথম রিভিউটি দিয়ে অন্যদের সিদ্ধান্ত নিতে সাহায্য করুন!",
                  fontSize = 11.sp,
                  color = DarkEspresso.copy(alpha = 0.6f)
                )
              }
            }
          } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              reviews.forEach { rev ->
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, BorderGold.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .padding(12.dp)
                    .testTag("review_item_${rev.id}")
                ) {
                  Column {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                      ) {
                        // Avatar Initial
                        Box(
                          modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(RoyalMaroon),
                          contentAlignment = Alignment.Center
                        ) {
                          Text(
                            text = rev.customerName.firstOrNull()?.toString() ?: "ক",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                          )
                        }

                        Column {
                          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                              text = rev.customerName.ifBlank { "ভেরিফাইড ক্রেতা" },
                              fontSize = 13.sp,
                              fontWeight = FontWeight.Bold,
                              color = DarkEspresso
                            )
                            if (rev.verified) {
                              Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified",
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(12.dp)
                              )
                            }
                          }
                          Text(
                            text = if (rev.verified) "ভেরিফাইড ক্রেতা" else "সাধারণ গ্রাহক",
                            fontSize = 10.sp,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Medium
                          )
                        }
                      }

                      // Date & Stars
                      Column(horizontalAlignment = Alignment.End) {
                        Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                          for (i in 1..5) {
                            Icon(
                              imageVector = if (i <= rev.rating) Icons.Default.Star else Icons.Outlined.StarBorder,
                              contentDescription = null,
                              tint = AntiqueGold,
                              modifier = Modifier.size(13.dp)
                            )
                          }
                        }
                        if (rev.date.isNotBlank()) {
                          Text(
                            text = rev.date,
                            fontSize = 10.sp,
                            color = DarkEspresso.copy(alpha = 0.5f)
                          )
                        }
                      }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                      text = rev.comment,
                      fontSize = 12.sp,
                      color = DarkEspresso,
                      lineHeight = 17.sp
                    )
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(20.dp))
        }
      }
    }
  }
}
