package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ai.AiStylistSuggestion
import com.example.ai.AiTaskState
import com.example.data.model.Product
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.AntiqueGoldDark
import com.example.ui.theme.BorderGold
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.DarkEspresso
import com.example.ui.theme.RoyalMaroon

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiStylistDialog(
  initialPrompt: String = "",
  aiState: AiTaskState<AiStylistSuggestion>,
  products: List<Product>,
  onAskStylist: (prompt: String, occasion: String, bitmap: Bitmap?) -> Unit,
  onClearState: () -> Unit,
  onProductClick: (Product) -> Unit,
  onAddToCart: (Product) -> Unit,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  var promptInput by remember(initialPrompt) { mutableStateOf(initialPrompt) }
  var selectedOccasion by remember { mutableStateOf("ইদ ও উৎসব") }
  var attachedBitmap by remember { mutableStateOf<Bitmap?>(null) }
  var localError by remember { mutableStateOf<String?>(null) }

  val occasions = listOf(
    "ইদ ও উৎসব",
    "বিয়ে ও রিসেপশন",
    "হলুদ / মেহেদী",
    "অফিস ও ক্যাজুয়াল",
    "গিফট / কম্বো"
  )

  val quickQuestions = listOf(
    "৩৫০০ টাকার মধ্যে ইদের জন্য বেস্ট শাড়ি ও ম্যাচিং জুয়েলারি সাজেস্ট করো",
    "রিসেপশন পার্টির জন্য রয়েল লুকের শাড়ি ও গহনা কী মানাবে?",
    "অফিস ও রেগুলার পরার জন্য আরামদায়ক থ্রি-পিস বা কুর্তি দেখাও"
  )

  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      try {
        val rawBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
          val src = ImageDecoder.createSource(context.contentResolver, uri)
          ImageDecoder.decodeBitmap(src) { decoder, info, _ ->
            val maxDim = 1024
            val w = info.size.width
            val h = info.size.height
            if (w > maxDim || h > maxDim) {
              val scale = maxDim.toFloat() / maxOf(w, h)
              decoder.setTargetSize((w * scale).toInt().coerceAtLeast(1), (h * scale).toInt().coerceAtLeast(1))
            }
          }
        } else {
          @Suppress("DEPRECATION")
          MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
        attachedBitmap = rawBitmap
        localError = null
      } catch (e: Exception) {
        localError = "ছবি লোড করা যায়নি: ${e.localizedMessage ?: "আবার চেষ্টা করুন"}"
      }
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = Color.White,
      border = BorderStroke(1.5.dp, BorderGold),
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .padding(vertical = 18.dp)
        .testTag("ai_stylist_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        // Header
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.horizontalGradient(
                listOf(RoyalMaroon, Color(0xFF9F1239))
              )
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
          ) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(AntiqueGold),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "AI Stylist",
                tint = DarkEspresso,
                modifier = Modifier.size(20.dp)
              )
            }
            Column {
              Text(
                text = "Sajgoj স্টাইলিশ অ্যাসিস্ট্যান্ট",
                color = Color.White,
                fontSize = 15.5.sp,
                fontWeight = FontWeight.Black
              )
              Text(
                text = "আউটফিট, জুয়েলারি ম্যাচিং ও বাজেট সাজেশন",
                color = CreamSurface.copy(alpha = 0.9f),
                fontSize = 11.sp
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(Color.White.copy(alpha = 0.2f))
              .testTag("ai_stylist_close_button")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "বন্ধ করুন",
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // Occasion chips
          Text(
            text = "অনুষ্ঠান বা উপলক্ষ নির্বাচন করুন:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = RoyalMaroon
          )
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            occasions.forEach { occ ->
              val selected = selectedOccasion == occ
              Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (selected) RoyalMaroon else CreamSurface,
                border = BorderStroke(1.dp, if (selected) RoyalMaroon else BorderGold),
                modifier = Modifier.clickable { selectedOccasion = occ }
              ) {
                Text(
                  text = occ,
                  fontSize = 11.sp,
                  fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                  color = if (selected) Color.White else DarkEspresso,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
              }
            }
          }

          // Quick prompt chips
          Text(
            text = "দ্রুত প্রশ্ন বেছে নিন:",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = AntiqueGoldDark
          )
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            quickQuestions.forEach { q ->
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = CreamBackground,
                border = BorderStroke(1.dp, BorderGold.copy(alpha = 0.65f)),
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable { promptInput = q }
              ) {
                Text(
                  text = "💬 $q",
                  fontSize = 11.sp,
                  color = DarkEspresso,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                )
              }
            }
          }

          // Optional Reference Photo Picker
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedButton(
              onClick = {
                photoPickerLauncher.launch(
                  PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
              },
              shape = RoundedCornerShape(14.dp),
              border = BorderStroke(1.dp, RoyalMaroon),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = RoyalMaroon),
              modifier = Modifier
                .weight(1f)
                .heightIn(min = 48.dp)
                .testTag("ai_stylist_photo_button")
            ) {
              Icon(imageVector = Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (attachedBitmap == null) "ছবির সাথে ম্যাচ করুন (ঐচ্ছিক)" else "ছবি পরিবর্তন করুন",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold
              )
            }

            if (attachedBitmap != null) {
              Box(
                modifier = Modifier
                  .size(48.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .border(1.dp, BorderGold, RoundedCornerShape(10.dp))
              ) {
                Image(
                  bitmap = attachedBitmap!!.asImageBitmap(),
                  contentDescription = "নির্বাচিত রেফারেন্স ছবি",
                  contentScale = ContentScale.Crop,
                  modifier = Modifier.size(48.dp)
                )
                IconButton(
                  onClick = { attachedBitmap = null },
                  modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(18.dp)
                    .background(Color.Black.copy(alpha = 0.65f), CircleShape)
                ) {
                  Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "ছবি সরান",
                    tint = Color.White,
                    modifier = Modifier.size(11.dp)
                  )
                }
              }
            }
          }

          // Input field
          OutlinedTextField(
            value = promptInput,
            onValueChange = {
              promptInput = it
              localError = null
            },
            label = { Text("আপনার পছন্দ বা বাজেট লিখুন") },
            placeholder = { Text("যেমন: লাল কাতান শাড়ির সাথে গোল্ডেন ঝুমকা চাই...") },
            minLines = 2,
            maxLines = 4,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = RoyalMaroon,
              unfocusedBorderColor = BorderGold
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("ai_stylist_prompt_input")
          )

          if (localError != null) {
            Text(
              text = localError!!,
              color = Color(0xFFDC2626),
              fontSize = 11.5.sp,
              fontWeight = FontWeight.SemiBold
            )
          }

          Button(
            onClick = {
              val trimmed = promptInput.trim()
              if (trimmed.isEmpty() && attachedBitmap == null) {
                localError = "অনুগ্রহ করে আপনার পছন্দের কথা লিখুন অথবা একটি ছবি দিন।"
                return@Button
              }
              val effectivePrompt = trimmed.ifEmpty {
                "এই ছবির রঙ ও ডিজাইনের সাথে মানানসই শাড়ি ও জুয়েলারি সাজেস্ট করো।"
              }
              onAskStylist(effectivePrompt, selectedOccasion, attachedBitmap)
            },
            enabled = aiState !is AiTaskState.Loading,
            colors = ButtonDefaults.buttonColors(
              containerColor = RoyalMaroon,
              contentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("ai_stylist_submit_button")
          ) {
            if (aiState is AiTaskState.Loading) {
              CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.dp,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text("সাজেশন তৈরি হচ্ছে...", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            } else {
              Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(17.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("স্টাইলিং সাজেশন পান", fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
            }
          }

          // Result Panel
          when (aiState) {
            is AiTaskState.Idle -> Unit
            is AiTaskState.Loading -> {
              Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CreamBackground),
                border = BorderStroke(1.dp, BorderGold)
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  CircularProgressIndicator(color = RoyalMaroon, strokeWidth = 2.5.dp, modifier = Modifier.size(22.dp))
                  Text(
                    text = "ক্যাটালগ বিশ্লেষণ করে আপনার জন্য পারফেক্ট লুক খোঁজা হচ্ছে...",
                    fontSize = 12.sp,
                    color = DarkEspresso
                  )
                }
              }
            }
            is AiTaskState.Error -> {
              Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                border = BorderStroke(1.dp, Color(0xFFFCA5A5))
              ) {
                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                  verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Text(
                    text = aiState.message,
                    color = Color(0xFF991B1B),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                  )
                  Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                      onClick = {
                        val effectivePrompt = promptInput.trim().ifEmpty {
                          "এই উপলক্ষের জন্য সেরা আউটফিট ও জুয়েলারি সাজেস্ট করো।"
                        }
                        onAskStylist(effectivePrompt, selectedOccasion, attachedBitmap)
                      },
                      modifier = Modifier.testTag("ai_stylist_retry_button")
                    ) {
                      Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(15.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("আবার চেষ্টা করুন", fontSize = 11.5.sp)
                    }
                    OutlinedButton(onClick = onClearState) {
                      Text("মুছুন", fontSize = 11.5.sp)
                    }
                  }
                }
              }
            }
            is AiTaskState.Success -> {
              val suggestion = aiState.data
              val matchedProducts = remember(suggestion.recommendedProductIds, products) {
                suggestion.recommendedProductIds.mapNotNull { id -> products.find { it.id == id } }
              }

              Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CreamBackground),
                border = BorderStroke(1.dp, BorderGold),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("ai_stylist_result_card")
              ) {
                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                  verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  Text(
                    text = "✨ স্টাইলিশ সাজেশন",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Black,
                    color = RoyalMaroon
                  )
                  Text(
                    text = suggestion.adviceText,
                    fontSize = 12.5.sp,
                    lineHeight = 18.sp,
                    color = DarkEspresso
                  )
                  if (suggestion.stylingTip.isNotBlank()) {
                    Surface(
                      shape = RoundedCornerShape(10.dp),
                      color = CreamSurface,
                      border = BorderStroke(1.dp, BorderGold.copy(alpha = 0.7f))
                    ) {
                      Text(
                        text = "💡 স্টাইল টিপ: ${suggestion.stylingTip}",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkEspresso,
                        modifier = Modifier.padding(10.dp)
                      )
                    }
                  }

                  if (matchedProducts.isNotEmpty()) {
                    HorizontalDivider(color = BorderGold.copy(alpha = 0.6f))
                    Text(
                      text = "সাজেস্টেড পণ্যসমূহ (${matchedProducts.size}টি):",
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      color = RoyalMaroon
                    )
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                      horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                      matchedProducts.forEach { prod ->
                        Surface(
                          shape = RoundedCornerShape(14.dp),
                          color = Color.White,
                          border = BorderStroke(1.dp, BorderGold),
                          modifier = Modifier
                            .width(156.dp)
                            .clickable { onProductClick(prod) }
                        ) {
                          Column(
                            modifier = Modifier.padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                          ) {
                            AppImage(
                              model = prod.imageUrl,
                              contentDescription = prod.name,
                              modifier = Modifier
                                .fillMaxWidth()
                                .height(92.dp)
                                .clip(RoundedCornerShape(10.dp))
                            )
                            Text(
                              text = prod.name,
                              fontSize = 11.5.sp,
                              fontWeight = FontWeight.Bold,
                              color = DarkEspresso,
                              maxLines = 2,
                              overflow = TextOverflow.Ellipsis
                            )
                            Text(
                              text = "${prod.price} ৳",
                              fontSize = 12.5.sp,
                              fontWeight = FontWeight.ExtraBold,
                              color = RoyalMaroon
                            )
                            Button(
                              onClick = { onAddToCart(prod) },
                              enabled = prod.stock > 0,
                              colors = ButtonDefaults.buttonColors(containerColor = RoyalMaroon),
                              shape = RoundedCornerShape(10.dp),
                              modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 36.dp)
                            ) {
                              Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(13.dp))
                              Spacer(modifier = Modifier.width(4.dp))
                              Text("কার্টে নিন", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
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
      }
    }
  }
}
