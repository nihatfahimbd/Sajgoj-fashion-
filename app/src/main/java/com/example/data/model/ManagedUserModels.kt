package com.example.data.model

import android.os.Build
import com.example.firebase.AuthUserSummary

data class WalletTransaction(
  val id: String = "",
  val type: String = "credit", // "credit", "debit", "refund"
  val amount: Int = 0,
  val note: String = "",
  val orderId: String = "",
  val timestampMs: Long = System.currentTimeMillis()
)

data class ManagedUserProfile(
  val id: String = "",
  val name: String = "",
  val phone: String = "",
  val email: String = "",
  val address: String = "",
  val district: String = "",
  val thana: String = "",
  val isVerified: Boolean = false,
  val accountStatus: String = "active", // "active" | "blocked"
  val blockReason: String = "",
  val blockedAtMs: Long = 0L,
  val loginLocked: Boolean = false,
  val loginIssueNote: String = "",
  val failedLoginAttempts: Int = 0,
  val lastLoginAtMs: Long = 0L,
  val deviceInfo: String = "",
  val password: String = "",
  val tempPassword: String = "",
  val walletBalance: Int = 0,
  val walletHistory: List<WalletTransaction> = emptyList(),
  val updatedAtMs: Long = 0L
)

data class ManagedUserWithStats(
  val key: String,
  val profile: ManagedUserProfile,
  val orders: List<Order>,
  val returns: List<ReturnRecord>,
  val totalOrders: Int,
  val cancelledOrders: Int,
  val returnCount: Int,
  val totalSpend: Int,
  val lastOrderDate: String
)

object ManagedUserHelper {
  fun normalizePhone(raw: String): String {
    val digits = raw.filter { it.isDigit() }
    if (digits.isBlank()) return ""
    return when {
      digits.startsWith("880") && digits.length >= 13 -> "0" + digits.substring(3)
      digits.length == 10 && digits.startsWith("1") -> "0$digits"
      else -> digits
    }
  }

  fun resolveLookupKey(phone: String = "", email: String = "", fallbackId: String = ""): String {
    val normPhone = normalizePhone(phone)
    if (normPhone.length >= 6) return "p_$normPhone"
    val cleanEmail = email.trim().lowercase()
    if (cleanEmail.isNotBlank()) return "e_$cleanEmail"
    val cleanId = fallbackId.trim()
    return if (cleanId.isNotBlank()) "i_$cleanId" else "u_${System.currentTimeMillis()}"
  }

  fun currentDeviceInfo(): String {
    val mfg = Build.MANUFACTURER.orEmpty().trim()
    val model = Build.MODEL.orEmpty().trim()
    val release = Build.VERSION.RELEASE.orEmpty().trim()
    val combined = if (model.startsWith(mfg, ignoreCase = true)) model else "$mfg $model".trim()
    return "${combined.ifBlank { "Android Device" }} (Android ${release.ifBlank { "OS" }})"
  }

  fun mergeUsersWithStats(
    managedUsers: List<ManagedUserProfile>,
    orders: List<Order>,
    returns: List<ReturnRecord>,
    authUser: AuthUserSummary?
  ): List<ManagedUserWithStats> {
    val profileByKey = linkedMapOf<String, ManagedUserProfile>()
    val ordersByKey = linkedMapOf<String, MutableList<Order>>()
    val returnsByKey = linkedMapOf<String, MutableList<ReturnRecord>>()

    fun findMatchingKey(phone: String, email: String, id: String): String? {
      val np = normalizePhone(phone)
      val em = email.trim().lowercase()
      val cid = id.trim()
      profileByKey.entries.forEach { (k, u) ->
        if (np.length >= 6 && normalizePhone(u.phone) == np) return k
        if (em.isNotBlank() && u.email.trim().equals(em, ignoreCase = true)) return k
        if (cid.isNotBlank() && u.id == cid) return k
      }
      return null
    }

    managedUsers.forEach { u ->
      val key = findMatchingKey(u.phone, u.email, u.id)
        ?: resolveLookupKey(phone = u.phone, email = u.email, fallbackId = u.id)
      val existing = profileByKey[key]
      profileByKey[key] = if (existing == null) {
        u.copy(id = u.id.ifBlank { key })
      } else {
        existing.copy(
          name = u.name.ifBlank { existing.name },
          phone = u.phone.ifBlank { existing.phone },
          email = u.email.ifBlank { existing.email },
          address = u.address.ifBlank { existing.address },
          district = u.district.ifBlank { existing.district },
          thana = u.thana.ifBlank { existing.thana },
          isVerified = existing.isVerified || u.isVerified,
          accountStatus = if (u.accountStatus == "blocked" || existing.accountStatus == "blocked") "blocked" else "active",
          blockReason = u.blockReason.ifBlank { existing.blockReason },
          blockedAtMs = maxOf(u.blockedAtMs, existing.blockedAtMs),
          loginLocked = u.loginLocked || existing.loginLocked,
          loginIssueNote = u.loginIssueNote.ifBlank { existing.loginIssueNote },
          failedLoginAttempts = maxOf(u.failedLoginAttempts, existing.failedLoginAttempts),
          lastLoginAtMs = maxOf(u.lastLoginAtMs, existing.lastLoginAtMs),
          deviceInfo = u.deviceInfo.ifBlank { existing.deviceInfo },
          password = u.password.ifBlank { existing.password },
          tempPassword = u.tempPassword.ifBlank { existing.tempPassword },
          walletBalance = maxOf(u.walletBalance, existing.walletBalance),
          walletHistory = (u.walletHistory + existing.walletHistory)
            .distinctBy { it.id.ifBlank { "${it.timestampMs}_${it.amount}_${it.type}" } }
            .sortedByDescending { it.timestampMs },
          updatedAtMs = maxOf(u.updatedAtMs, existing.updatedAtMs)
        )
      }
    }

    if (authUser != null) {
      val key = findMatchingKey("", authUser.email, authUser.uid)
        ?: resolveLookupKey(email = authUser.email, fallbackId = authUser.uid)
      val existing = profileByKey[key]
      if (existing == null) {
        profileByKey[key] = ManagedUserProfile(
          id = authUser.uid.ifBlank { key },
          name = authUser.displayName.ifBlank { "গুগল ইউজার" },
          email = authUser.email,
          lastLoginAtMs = System.currentTimeMillis(),
          deviceInfo = currentDeviceInfo(),
          updatedAtMs = System.currentTimeMillis()
        )
      }
    }

    orders.forEach { ord ->
      val key = findMatchingKey(ord.phone, "", "")
        ?: resolveLookupKey(phone = ord.phone, fallbackId = ord.customerName)
      val existing = profileByKey[key]
      if (existing == null) {
        profileByKey[key] = ManagedUserProfile(
          id = key,
          name = ord.customerName.ifBlank { "সম্মানিত গ্রাহক" },
          phone = ord.phone,
          address = ord.address,
          district = ord.district,
          thana = ord.thana,
          updatedAtMs = ord.createdAt
        )
      } else {
        profileByKey[key] = existing.copy(
          name = existing.name.ifBlank { ord.customerName },
          phone = existing.phone.ifBlank { ord.phone },
          address = existing.address.ifBlank { ord.address },
          district = existing.district.ifBlank { ord.district },
          thana = existing.thana.ifBlank { ord.thana }
        )
      }
      ordersByKey.getOrPut(key) { mutableListOf() }.add(ord)
    }

    returns.forEach { ret ->
      val matchingOrder = orders.find { it.id == ret.orderId }
      val key = findMatchingKey(ret.phone.ifBlank { matchingOrder?.phone.orEmpty() }, "", "")
        ?: resolveLookupKey(phone = ret.phone.ifBlank { matchingOrder?.phone.orEmpty() }, fallbackId = ret.customerName)
      returnsByKey.getOrPut(key) { mutableListOf() }.add(ret)
    }

    return profileByKey.entries.map { (key, profile) ->
      val userOrders = (ordersByKey[key] ?: emptyList()).sortedWith(
        compareByDescending<Order> { it.createdAt }.thenByDescending { it.date }.thenByDescending { it.id }
      )
      val userReturns = returnsByKey[key] ?: emptyList()
      val cancelledCount = userOrders.count {
        it.status.equals("Cancelled", ignoreCase = true) ||
          it.status.equals("Cancellation", ignoreCase = true)
      }
      val totalSpend = userOrders
        .filterNot {
          it.status.equals("Cancelled", ignoreCase = true) ||
            it.status.equals("Cancellation", ignoreCase = true)
        }
        .sumOf { (it.total + it.walletPaidAmount).coerceAtLeast(0) }
      ManagedUserWithStats(
        key = key,
        profile = profile,
        orders = userOrders,
        returns = userReturns,
        totalOrders = userOrders.size,
        cancelledOrders = cancelledCount,
        returnCount = userReturns.size,
        totalSpend = totalSpend,
        lastOrderDate = userOrders.firstOrNull()?.date.orEmpty()
      )
    }.sortedWith(
      compareByDescending<ManagedUserWithStats> { it.profile.lastLoginAtMs }
        .thenByDescending { it.totalSpend }
        .thenByDescending { it.profile.walletBalance }
        .thenBy { it.profile.name }
    )
  }
}
