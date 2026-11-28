package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.model.ManagedUserWithStats
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object UserExportHelper {
  private fun formatTime(ms: Long): String {
    if (ms <= 0L) return "লগইন নেই"
    return try {
      SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(ms))
    } catch (_: Exception) {
      "লগইন নেই"
    }
  }

  private fun csvEscape(value: String): String {
    val clean = value.replace("\r", " ").replace("\n", " ")
    return "\"" + clean.replace("\"", "\"\"") + "\""
  }

  private fun htmlEscape(value: String): String {
    return value
      .replace("&", "&amp;")
      .replace("<", "&lt;")
      .replace(">", "&gt;")
      .replace("\"", "&quot;")
  }

  private fun exportDir(context: Context): File {
    return File(context.cacheDir, "exports").apply { mkdirs() }
  }

  private fun shareFile(context: Context, file: File, mimeType: String, chooserTitle: String) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
      type = mimeType
      putExtra(Intent.EXTRA_STREAM, uri)
      putExtra(Intent.EXTRA_SUBJECT, file.nameWithoutExtension)
      addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    val chooser = Intent.createChooser(intent, chooserTitle).apply {
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(chooser)
  }

  fun exportUsersCsv(context: Context, users: List<ManagedUserWithStats>): Result<File> = runCatching {
    val dir = exportDir(context)
    val stamp = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date())
    val file = File(dir, "sajgoj_users_$stamp.csv")
    val sb = StringBuilder()
    sb.append('\uFEFF')
    sb.append(
      listOf(
        "Name",
        "Verified",
        "Phone",
        "Email",
        "District",
        "Thana",
        "Address",
        "Status",
        "BlockReason",
        "LoginLocked",
        "LastLogin",
        "DeviceInfo",
        "TotalOrders",
        "CancelledOrders",
        "ReturnCount",
        "TotalSpend_BDT",
        "WalletBalance_BDT"
      ).joinToString(",")
    ).append('\n')

    users.forEach { item ->
      val u = item.profile
      sb.append(
        listOf(
          csvEscape(u.name),
          csvEscape(if (u.isVerified) "Verified" else "Unverified"),
          csvEscape(u.phone),
          csvEscape(u.email),
          csvEscape(u.district),
          csvEscape(u.thana),
          csvEscape(u.address),
          csvEscape(u.accountStatus),
          csvEscape(u.blockReason),
          csvEscape(if (u.loginLocked) "Locked" else "OK"),
          csvEscape(formatTime(u.lastLoginAtMs)),
          csvEscape(u.deviceInfo),
          item.totalOrders.toString(),
          item.cancelledOrders.toString(),
          item.returnCount.toString(),
          item.totalSpend.toString(),
          u.walletBalance.toString()
        ).joinToString(",")
      ).append('\n')
    }
    file.writeText(sb.toString(), Charsets.UTF_8)
    shareFile(context, file, "text/csv", "CSV ইউজার লিস্ট শেয়ার / সেভ করুন")
    file
  }

  fun exportUsersExcel(context: Context, users: List<ManagedUserWithStats>): Result<File> = runCatching {
    val dir = exportDir(context)
    val stamp = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date())
    val file = File(dir, "sajgoj_users_$stamp.xls")
    val rowsHtml = users.joinToString("\n") { item ->
      val u = item.profile
      """
        <tr>
          <td>${htmlEscape(u.name)}</td>
          <td>${if (u.isVerified) "Verified ✓" else "Unverified"}</td>
          <td>${htmlEscape(u.phone)}</td>
          <td>${htmlEscape(u.email)}</td>
          <td>${htmlEscape(u.district)}</td>
          <td>${htmlEscape(u.thana)}</td>
          <td>${htmlEscape(u.address)}</td>
          <td>${htmlEscape(u.accountStatus)}</td>
          <td>${htmlEscape(u.blockReason)}</td>
          <td>${htmlEscape(formatTime(u.lastLoginAtMs))}</td>
          <td>${htmlEscape(u.deviceInfo)}</td>
          <td>${item.totalOrders}</td>
          <td>${item.cancelledOrders}</td>
          <td>${item.returnCount}</td>
          <td>${item.totalSpend}</td>
          <td>${u.walletBalance}</td>
        </tr>
      """.trimIndent()
    }

    val html = """
      <html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel">
      <head><meta charset="UTF-8" /></head>
      <body>
        <table border="1" cellspacing="0" cellpadding="6">
          <thead>
            <tr style="background-color:#7A112A;color:#FFFFFF;font-weight:bold;">
              <th>Name</th><th>Verified</th><th>Phone</th><th>Email</th>
              <th>District</th><th>Thana</th><th>Address</th><th>Status</th>
              <th>Block Reason</th><th>Last Login</th><th>Device Info</th>
              <th>Total Orders</th><th>Cancelled</th><th>Returns</th><th>Total Spend</th><th>Wallet Balance</th>
            </tr>
          </thead>
          <tbody>$rowsHtml</tbody>
        </table>
      </body>
      </html>
    """.trimIndent()
    file.writeText(html, Charsets.UTF_8)
    shareFile(context, file, "application/vnd.ms-excel", "Excel ইউজার লিস্ট শেয়ার / সেভ করুন")
    file
  }

  fun exportUsersPdf(context: Context, users: List<ManagedUserWithStats>): Result<File> = runCatching {
    val dir = exportDir(context)
    val stamp = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date())
    val file = File(dir, "sajgoj_users_$stamp.pdf")

    val pdf = PdfDocument()
    val pageWidth = 595
    val pageHeight = 842
    val titlePaint = Paint().apply {
      color = AndroidColor.rgb(122, 17, 42)
      textSize = 15f
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    val subtitlePaint = Paint().apply {
      color = AndroidColor.rgb(71, 85, 105)
      textSize = 9.5f
    }
    val rowHeaderPaint = Paint().apply {
      color = AndroidColor.rgb(15, 23, 42)
      textSize = 10.5f
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    val bodyPaint = Paint().apply {
      color = AndroidColor.rgb(51, 65, 85)
      textSize = 9f
    }
    val linePaint = Paint().apply {
      color = AndroidColor.rgb(226, 232, 240)
      strokeWidth = 1f
    }

    var pageIndex = 1
    var page = pdf.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageIndex).create())
    var canvas = page.canvas
    var y = 40f

    fun drawPageHeader() {
      canvas.drawText("SAJGOJ FASHION — User Management Report", 28f, y, titlePaint)
      y += 16f
      val generated = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
      canvas.drawText("Total Exported Users: ${users.size}  |  Generated: $generated", 28f, y, subtitlePaint)
      y += 12f
      canvas.drawLine(28f, y, pageWidth - 28f, y, linePaint)
      y += 16f
    }

    drawPageHeader()

    users.forEachIndexed { idx, item ->
      if (y > pageHeight - 75f) {
        pdf.finishPage(page)
        pageIndex++
        page = pdf.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageIndex).create())
        canvas = page.canvas
        y = 40f
        drawPageHeader()
      }
      val u = item.profile
      val verifiedMark = if (u.isVerified) " [Verified]" else ""
      val blockMark = if (u.accountStatus == "blocked") " [BLOCKED: ${u.blockReason}]" else ""
      canvas.drawText(
        "${idx + 1}. ${u.name.ifBlank { "Customer" }}$verifiedMark$blockMark",
        28f,
        y,
        rowHeaderPaint
      )
      y += 13f
      canvas.drawText(
        "Phone: ${u.phone.ifBlank { "-" }}  |  District/Thana: ${u.district.ifBlank { "-" }} / ${u.thana.ifBlank { "-" }}",
        36f,
        y,
        bodyPaint
      )
      y += 12f
      canvas.drawText(
        "Orders: ${item.totalOrders} (Cancel: ${item.cancelledOrders}, Return: ${item.returnCount})  |  Spend: Tk ${item.totalSpend}  |  Wallet: Tk ${u.walletBalance}",
        36f,
        y,
        bodyPaint
      )
      y += 12f
      canvas.drawText(
        "Last Login: ${formatTime(u.lastLoginAtMs)}  |  Device: ${u.deviceInfo.ifBlank { "-" }}",
        36f,
        y,
        bodyPaint
      )
      y += 9f
      canvas.drawLine(28f, y, pageWidth - 28f, y, linePaint)
      y += 13f
    }

    pdf.finishPage(page)
    FileOutputStream(file).use { out -> pdf.writeTo(out) }
    pdf.close()

    shareFile(context, file, "application/pdf", "PDF ইউজার রিপোর্ট শেয়ার / সেভ করুন")
    file
  }
}
