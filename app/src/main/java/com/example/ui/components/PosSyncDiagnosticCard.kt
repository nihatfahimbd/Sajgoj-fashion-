package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SyncErrorLog
import com.example.data.model.SyncHealthState
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val DiagnosticAmber = Color(0xFFB45309)

@Composable
fun PosSyncDiagnosticCard(
  syncHealth: SyncHealthState,
  syncErrorLogs: List<SyncErrorLog>,
  onRunDiagnosticSync: () -> Unit,
  onClearErrorLogs: () -> Unit,
  modifier: Modifier = Modifier
) {
  val fullTimestampFormatter = remember {
    SimpleDateFormat("dd MMM yyyy, hh:mm:ss a", Locale.getDefault())
  }

  val hasActiveErrors = syncErrorLogs.any { it.severity == "ERROR" }
  val statusLabel = when {
    syncHealth.status == "ERROR" || hasActiveErrors -> "সিঙ্ক ত্রুটি সনাক্ত (${syncErrorLogs.size})"
    syncHealth.firestoreConnected -> "Firestore + লোকাল POS সংযুক্ত"
    syncHealth.status == "LOCAL_ONLY" -> "লোকাল POS মোড (ক্লাউড সংযুক্ত নয়)"
    else -> "রিয়েল-টাইম লিসেনার প্রস্তুত"
  }
  val statusAccent = when {
    syncHealth.status == "ERROR" || hasActiveErrors -> AccentRed
    syncHealth.firestoreConnected -> AccentGreen
    else -> DiagnosticAmber
  }

  GlossyAdminCard(
    cornerRadius = 20.dp,
    accentBorderColor = if (hasActiveErrors) AccentRed.copy(alpha = 0.55f) else AdminCardBorderWarm,
    contentPadding = 14.dp,
    modifier = modifier.testTag("pos_sync_diagnostic_card")
  ) {
    Column(
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Header + Connection Status Pill
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(statusAccent.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = when {
                hasActiveErrors -> Icons.Default.ErrorOutline
                syncHealth.firestoreConnected -> Icons.Default.CloudDone
                else -> Icons.Default.CloudOff
              },
              contentDescription = "POS Sync Diagnostics status",
              tint = statusAccent,
              modifier = Modifier.size(20.dp)
            )
          }
          Column {
            Text(
              text = "POS ↔ Firestore সিঙ্ক ডায়াগনস্টিক",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = AdminInkPrimary
            )
            Text(
              text = statusLabel,
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = statusAccent
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(10.dp),
          color = statusAccent.copy(alpha = 0.12f)
        ) {
          Text(
            text = syncHealth.status,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = statusAccent,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      // Timestamp Tiles: Local POS Storage vs Central Firestore
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        DiagnosticTimestampTile(
          title = "লোকাল POS স্টোরেজ সিঙ্ক",
          timestampText = if (syncHealth.lastLocalSyncTimestamp > 0L) {
            fullTimestampFormatter.format(Date(syncHealth.lastLocalSyncTimestamp))
          } else {
            "এখনো সংরক্ষিত হয়নি"
          },
          subtitle = "POS SKU: ${syncHealth.posProductCount} • স্টোরফ্রন্ট: ${syncHealth.storefrontProductCount}",
          icon = Icons.Default.Storage,
          accent = AdminV2CyanDeep,
          modifier = Modifier
            .weight(1f)
            .testTag("local_pos_sync_timestamp")
        )

        DiagnosticTimestampTile(
          title = "সেন্ট্রাল Firestore সিঙ্ক",
          timestampText = if (syncHealth.lastFirestoreSyncTimestamp > 0L) {
            fullTimestampFormatter.format(Date(syncHealth.lastFirestoreSyncTimestamp))
          } else if (syncHealth.status == "LOCAL_ONLY") {
            "ক্লাউড সংযুক্ত নয় (অফলাইন)"
          } else {
            "এখনো ক্লাউড সিঙ্ক হয়নি"
          },
          subtitle = if (syncHealth.firestoreConnected) "লাইভ লিসেনার সক্রিয়" else "লোকাল ব্রিজ ফলব্যাক",
          icon = if (syncHealth.firestoreConnected) Icons.Default.CloudDone else Icons.Default.CloudOff,
          accent = if (syncHealth.firestoreConnected) AccentGreen else DiagnosticAmber,
          modifier = Modifier
            .weight(1f)
            .testTag("firestore_sync_timestamp")
        )
      }

      // Diagnostic Actions
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = onRunDiagnosticSync,
          colors = ButtonDefaults.buttonColors(containerColor = AdminV2CyanDeep, contentColor = Color.White),
          shape = RoundedCornerShape(12.dp),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
          modifier = Modifier
            .weight(1f)
            .heightIn(min = 48.dp)
            .testTag("diagnostic_reconcile_button")
        ) {
          Icon(imageVector = Icons.Default.Sync, contentDescription = "এখনই ডায়াগনস্টিক সিঙ্ক চালান", modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("ডায়াগনস্টিক সিঙ্ক", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
          onClick = onClearErrorLogs,
          enabled = syncErrorLogs.isNotEmpty(),
          shape = RoundedCornerShape(12.dp),
          border = BorderStroke(1.dp, if (syncErrorLogs.isNotEmpty()) AccentRed else AdminCardBorderWarm),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
          modifier = Modifier
            .weight(1f)
            .heightIn(min = 48.dp)
            .testTag("clear_sync_errors_button")
        ) {
          Icon(
            imageVector = Icons.Default.DeleteSweep,
            contentDescription = "ত্রুটি লগ মুছুন",
            tint = if (syncErrorLogs.isNotEmpty()) AccentRed else AdminInkMuted,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "লগ মুছুন (${syncErrorLogs.size})",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (syncErrorLogs.isNotEmpty()) AccentRed else AdminInkMuted
          )
        }
      }

      HorizontalDivider(color = AdminCardBorderSoft)

      // Error Logs Section Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "সিঙ্ক ত্রুটি লগ (Local POS ↔ Firestore Error Logs)",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = AdminInkPrimary
        )
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (syncErrorLogs.isEmpty()) AccentGreen.copy(alpha = 0.14f) else AccentRed.copy(alpha = 0.14f)
        ) {
          Text(
            text = if (syncErrorLogs.isEmpty()) "0 ত্রুটি" else "${syncErrorLogs.size} টি লগ",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (syncErrorLogs.isEmpty()) AccentGreen else AccentRed,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }
      }

      // Error Log Rows or Healthy Empty State
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("pos_sync_error_logs_list"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        if (syncErrorLogs.isEmpty()) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = AccentGreen.copy(alpha = 0.08f),
            border = BorderStroke(1.dp, AccentGreen.copy(alpha = 0.35f)),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("pos_sync_no_errors_banner")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "No sync errors",
                tint = AccentGreen,
                modifier = Modifier.size(18.dp)
              )
              Text(
                text = "লোকাল POS স্টোরেজ ও সেন্ট্রাল Firestore-এর মধ্যে কোনো সিঙ্ক ত্রুটি পাওয়া যায়নি।",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = AdminInkPrimary
              )
            }
          }
        } else {
          syncErrorLogs.take(20).forEach { log ->
            val isError = log.severity == "ERROR"
            val rowAccent = if (isError) AccentRed else DiagnosticAmber
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = rowAccent.copy(alpha = 0.06f),
              border = BorderStroke(1.dp, rowAccent.copy(alpha = 0.35f)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(
                      imageVector = if (isError) Icons.Default.ErrorOutline else Icons.Default.Warning,
                      contentDescription = log.severity,
                      tint = rowAccent,
                      modifier = Modifier.size(14.dp)
                    )
                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = rowAccent
                    ) {
                      Text(
                        text = log.source,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                    if (log.code.isNotBlank()) {
                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = AdminV2IceSurface,
                        border = BorderStroke(1.dp, AdminCardBorderWarm)
                      ) {
                        Text(
                          text = log.code,
                          fontSize = 9.sp,
                          fontFamily = FontFamily.Monospace,
                          fontWeight = FontWeight.SemiBold,
                          color = AdminInkPrimary,
                          modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                      }
                    }
                  }
                  Text(
                    text = fullTimestampFormatter.format(Date(log.timestamp)),
                    fontSize = 9.sp,
                    color = AdminInkMuted
                  )
                }

                Text(
                  text = log.message,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = AdminInkPrimary
                )

                if (log.details.isNotBlank()) {
                  Text(
                    text = log.details,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = AdminInkSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                  )
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
private fun DiagnosticTimestampTile(
  title: String,
  timestampText: String,
  subtitle: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  accent: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(14.dp),
    color = AdminV2IceSurface,
    border = BorderStroke(1.dp, AdminCardBorderWarm)
  ) {
    Column(
      modifier = Modifier.padding(10.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
      ) {
        Icon(imageVector = icon, contentDescription = title, tint = accent, modifier = Modifier.size(14.dp))
        Text(
          text = title,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = AdminInkPrimary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
      Text(
        text = timestampText,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = accent,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )
      Text(
        text = subtitle,
        fontSize = 9.5.sp,
        color = AdminInkMuted,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}
