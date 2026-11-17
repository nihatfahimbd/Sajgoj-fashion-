package com.example.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkEspresso
import com.example.ui.theme.RoyalMaroon
import com.example.util.ProductMediaHelper
import com.example.util.ProductMediaKind
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val SellerOrange = Color(0xFFF97316)

@Composable
fun ProductVideoFormSection(
  videoEnabled: Boolean,
  videoType: String,
  videoUrl: String,
  videoThumbUrl: String,
  onVideoChanged: (enabled: Boolean, type: String, url: String, thumbUrl: String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  var isCopyingVideo by remember { mutableStateOf(false) }
  var videoError by remember { mutableStateOf<String?>(null) }

  val videoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    if (uri != null) {
      isCopyingVideo = true
      videoError = null
      coroutineScope.launch {
        val result = withContext(Dispatchers.IO) {
          ProductMediaHelper.copyPickedVideoToInternalStorage(context, uri)
        }
        isCopyingVideo = false
        if (result != null) {
          onVideoChanged(true, "upload", result.localVideoUri, result.localThumbUri)
        } else {
          videoError = "ভিডিও ফাইলটি ইমপোর্ট করা যায়নি। আবার চেষ্টা করুন।"
        }
      }
    }
  }

  val parsedPreview = remember(videoUrl, videoType) {
    ProductMediaHelper.parseVideoSource(videoUrl, videoType)
  }

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
    modifier = modifier
      .fillMaxWidth()
      .testTag("product_video_form_section")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Header row: "Video" + Video-First chip + Switch
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = "Video",
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF0F172A)
          )
          Surface(
            shape = RoundedCornerShape(50),
            color = SellerOrange.copy(alpha = 0.14f)
          ) {
            Text(
              text = "প্রথমে ভিডিও, পরে ছবি",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFFEA580C),
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
        }

        Switch(
          checked = videoEnabled,
          onCheckedChange = { checked ->
            onVideoChanged(checked, videoType, videoUrl, videoThumbUrl)
          },
          colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = SellerOrange,
            uncheckedThumbColor = Color.White,
            uncheckedTrackColor = Color(0xFFCBD5E1)
          ),
          modifier = Modifier.testTag("product_video_toggle")
        )
      }

      // 3 Radio options matching Seller Center screenshot + Facebook Reels Link
      val options = listOf(
        "upload" to "Upload Video",
        "youtube" to "Youtube Link",
        "facebook_reels" to "ফেইসবুক রীলস ভিডিও লিংক"
      )
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        options.forEach { (id, label) ->
          val selected = videoType == id
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .heightIn(min = 48.dp)
              .clip(RoundedCornerShape(10.dp))
              .clickable {
                videoError = null
                onVideoChanged(true, id, if (videoType == id) videoUrl else "", if (videoType == id) videoThumbUrl else "")
              }
              .padding(end = 6.dp)
              .testTag("video_type_radio_$id")
          ) {
            RadioButton(
              selected = selected,
              onClick = {
                videoError = null
                onVideoChanged(true, id, if (videoType == id) videoUrl else "", if (videoType == id) videoThumbUrl else "")
              },
              colors = RadioButtonDefaults.colors(
                selectedColor = SellerOrange,
                unselectedColor = Color(0xFF64748B)
              )
            )
            Text(
              text = label,
              fontSize = 12.sp,
              fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
              color = Color(0xFF1E293B),
              maxLines = 1
            )
          }
        }
      }

      Text(
        text = "Use video to show your real product.",
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF64748B)
      )

      when (videoType) {
        "upload" -> {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Dashed '+' Upload Box matching Seller Center screenshot
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier
                .size(88.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF8FAFC))
                .clickable(enabled = !isCopyingVideo) {
                  videoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                  )
                }
                .testTag("product_video_upload_tile")
            ) {
              Canvas(modifier = Modifier.fillMaxSize()) {
                drawRoundRect(
                  color = Color(0xFFCBD5E1),
                  cornerRadius = CornerRadius(12.dp.toPx()),
                  style = Stroke(
                    width = 1.6.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                  )
                )
              }
              if (isCopyingVideo) {
                CircularProgressIndicator(
                  color = SellerOrange,
                  strokeWidth = 2.5.dp,
                  modifier = Modifier.size(26.dp)
                )
              } else {
                Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.VideoCall,
                    contentDescription = "ভিডিও আপলোড করুন",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(30.dp)
                  )
                  Text(
                    text = "ভিডিও দিন",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                  )
                }
              }
            }

            // Live attached video preview
            if (videoUrl.isNotBlank()) {
              Box(
                modifier = Modifier
                  .width(120.dp)
                  .height(88.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .border(1.5.dp, SellerOrange, RoundedCornerShape(12.dp))
                  .background(Color.Black)
              ) {
                if (videoThumbUrl.isNotBlank()) {
                  AppImage(
                    model = videoThumbUrl,
                    contentDescription = "সংযুক্ত ভিডিও",
                    modifier = Modifier.fillMaxSize()
                  )
                }
                Box(
                  modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.25f)),
                  contentAlignment = Alignment.Center
                ) {
                  Box(
                    modifier = Modifier
                      .size(30.dp)
                      .clip(CircleShape)
                      .background(Color.Black.copy(alpha = 0.65f)),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = Icons.Default.PlayArrow,
                      contentDescription = null,
                      tint = Color.White,
                      modifier = Modifier.size(18.dp)
                    )
                  }
                }
                IconButton(
                  onClick = { onVideoChanged(false, "upload", "", "") },
                  modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(26.dp)
                    .padding(3.dp)
                    .clip(CircleShape)
                    .background(Color.Red)
                ) {
                  Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "ভিডিও মুছুন",
                    tint = Color.White,
                    modifier = Modifier.size(13.dp)
                  )
                }
                Text(
                  text = "ভিডিও যুক্ত হয়েছে ✓",
                  color = Color.White,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }
          }

          OutlinedTextField(
            value = if (videoUrl.startsWith("file://")) "" else videoUrl,
            onValueChange = { url ->
              onVideoChanged(url.isNotBlank(), "upload", url, videoThumbUrl)
            },
            label = { Text("অথবা সরাসরি ভিডিও লিংক (.mp4 CDN URL)", fontSize = 11.sp) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = adminOutlinedTextFieldColors(),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("video_direct_url_input")
          )
        }

        "youtube" -> {
          OutlinedTextField(
            value = videoUrl,
            onValueChange = { url ->
              val parsed = ProductMediaHelper.parseVideoSource(url, "youtube")
              onVideoChanged(url.isNotBlank(), "youtube", url, parsed.thumbUrl)
            },
            label = { Text("YouTube Video বা Shorts লিংক দিন", fontSize = 11.sp) },
            placeholder = { Text("https://youtube.com/watch?v=... বা shorts/...", fontSize = 11.sp) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = adminOutlinedTextFieldColors(),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("video_youtube_url_input")
          )

          if (videoUrl.isNotBlank() && parsedPreview.kind == ProductMediaKind.VIDEO_YOUTUBE && parsedPreview.thumbUrl.isNotBlank()) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF1F5F9))
                .padding(8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              AppImage(
                model = parsedPreview.thumbUrl,
                contentDescription = "YouTube Thumbnail",
                modifier = Modifier
                  .size(width = 72.dp, height = 50.dp)
                  .clip(RoundedCornerShape(8.dp))
              )
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "✓ YouTube ভিডিও সংযুক্ত",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF047857)
                )
                Text(
                  text = "ID: ${parsedPreview.videoId}",
                  fontSize = 10.sp,
                  color = DarkEspresso
                )
              }
              IconButton(onClick = { onVideoChanged(false, "youtube", "", "") }) {
                Icon(Icons.Default.Close, contentDescription = "মুছুন", tint = Color.Red)
              }
            }
          }
        }

        else -> {
          OutlinedTextField(
            value = videoUrl,
            onValueChange = { url ->
              onVideoChanged(url.isNotBlank(), "facebook_reels", url, videoThumbUrl)
            },
            label = { Text("ফেইসবুক রীলস ভিডিও লিংক দিন", fontSize = 11.sp) },
            placeholder = { Text("https://www.facebook.com/reel/... বা https://fb.watch/...", fontSize = 11.sp) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = adminOutlinedTextFieldColors(),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("video_facebook_reels_url_input")
          )

          if (videoUrl.isNotBlank()) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFEFF6FF))
                .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(12.dp))
                .padding(10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "📘 ফেইসবুক রীলস লিংক সংযুক্ত",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF1E40AF)
                )
                Text(
                  text = videoUrl,
                  fontSize = 10.sp,
                  color = Color(0xFF3B82F6),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
              IconButton(onClick = { onVideoChanged(false, "facebook_reels", "", "") }) {
                Icon(Icons.Default.Close, contentDescription = "মুছুন", tint = Color.Red)
              }
            }
          }
        }
      }

      if (videoError != null) {
        Text(
          text = videoError!!,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = Color.Red
        )
      }
    }
  }
}
