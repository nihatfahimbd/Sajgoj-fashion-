package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.RoyalMaroon

@Composable
fun AppImage(
  model: String,
  contentDescription: String?,
  modifier: Modifier = Modifier,
  contentScale: ContentScale = ContentScale.Crop
) {
  val context = LocalContext.current

  if (model.startsWith("res://")) {
    val resId = model.removePrefix("res://").toIntOrNull()
    if (resId != null) {
      Image(
        painter = painterResource(id = resId),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale
      )
      return
    }
  }

  // Fallback or external URL
  if (model.isNotBlank()) {
    AsyncImage(
      model = ImageRequest.Builder(context)
        .data(model)
        .crossfade(true)
        .build(),
      contentDescription = contentDescription,
      modifier = modifier,
      contentScale = contentScale
    )
  } else {
    Box(
      modifier = modifier.background(CreamSurface),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.Diamond,
        contentDescription = contentDescription,
        tint = RoyalMaroon,
        modifier = Modifier.size(36.dp)
      )
    }
  }
}
