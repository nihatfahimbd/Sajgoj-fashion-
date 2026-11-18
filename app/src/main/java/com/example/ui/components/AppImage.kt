package com.example.ui.components

import android.content.Context
import android.util.TypedValue
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
import androidx.compose.runtime.remember
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

private val KNOWN_APP_DRAWABLE_IDS = setOf(
  R.drawable.banner_eid_special_1792530116809,
  R.drawable.banner_jewelry_royal_1792530130914,
  R.drawable.ic_app_icon_1792530102214,
  R.drawable.app_icon_logo
)

private fun isRenderableAppDrawable(context: Context, resId: Int): Boolean {
  if (resId == 0) return false
  return try {
    if (context.resources.getResourcePackageName(resId) != context.packageName) return false
    if (context.resources.getResourceTypeName(resId) != "drawable") return false
    val tv = TypedValue()
    context.resources.getValue(resId, tv, true)
    val path = tv.string?.toString().orEmpty()
    path.endsWith(".png", ignoreCase = true) ||
      path.endsWith(".jpg", ignoreCase = true) ||
      path.endsWith(".jpeg", ignoreCase = true) ||
      path.endsWith(".webp", ignoreCase = true) ||
      path.endsWith(".xml", ignoreCase = true)
  } catch (_: Exception) {
    false
  }
}

internal fun resolveSafeDrawableResId(context: Context, model: String): Int {
  val spec = model.substring(6).trim()
  val named = when (spec) {
    "banner_eid_special_1792530116809", "banner_eid_special" ->
      R.drawable.banner_eid_special_1792530116809
    "banner_jewelry_royal_1792530130914", "banner_jewelry_royal" ->
      R.drawable.banner_jewelry_royal_1792530130914
    "ic_app_icon_1792530102214", "ic_app_icon" ->
      R.drawable.ic_app_icon_1792530102214
    "app_icon_logo" ->
      R.drawable.app_icon_logo
    else -> 0
  }
  if (named != 0 && isRenderableAppDrawable(context, named)) return named

  val numId = spec.toIntOrNull()
  if (numId != null) {
    if (numId in KNOWN_APP_DRAWABLE_IDS && isRenderableAppDrawable(context, numId)) {
      return numId
    }
    // Stale numeric R.drawable ID from an older build (e.g. renumbered after dependency changes)
    return R.drawable.banner_eid_special_1792530116809
  }

  if (spec.isNotBlank()) {
    val dynamicId = context.resources.getIdentifier(spec, "drawable", context.packageName)
    if (isRenderableAppDrawable(context, dynamicId)) {
      return dynamicId
    }
  }
  return R.drawable.banner_eid_special_1792530116809
}

@Composable
fun AppImage(
  model: String,
  contentDescription: String?,
  modifier: Modifier = Modifier,
  contentScale: ContentScale = ContentScale.Crop
) {
  val context = LocalContext.current

  if (model.startsWith("res://", ignoreCase = true)) {
    val resId = remember(model) { resolveSafeDrawableResId(context, model) }
    Image(
      painter = painterResource(id = resId),
      contentDescription = contentDescription,
      modifier = modifier,
      contentScale = contentScale
    )
    return
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
