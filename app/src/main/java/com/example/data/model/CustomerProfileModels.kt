package com.example.data.model

import com.example.firebase.AuthUserSummary
import kotlin.math.roundToInt

data class SavedAddress(
  val id: String,
  val label: String = "বাসা",
  val category: String = "Home", // "Home" | "Office"
  val recipientName: String,
  val phone: String,
  val district: String,
  val thana: String,
  val streetAddress: String,
  val landmark: String = "",
  val isDefault: Boolean = false,
  val updatedAt: Long = System.currentTimeMillis()
)

data class VipTierDefinition(
  val id: String,
  val name: String,
  val nameBn: String,
  val icon: String,
  val minSpend: Int,
  val discountPercent: Int,
  val perks: String,
  val accentHex: Long
)

data class CustomerLoyaltyStatus(
  val currentTier: VipTierDefinition,
  val nextTier: VipTierDefinition?,
  val lifetimeSpend: Int,
  val validOrderCount: Int,
  val earnedPoints: Int,
  val redeemedPoints: Int,
  val availablePoints: Int,
  val redeemableTaka: Int,
  val amountToNextTier: Int,
  val progressFraction: Float,
  val spendPerPoint: Int = 10,
  val pointsPerTaka: Int = 100,
  val allTiers: List<VipTierDefinition> = CustomerLoyaltyCalculator.defaultTiers
)

object CustomerLoyaltyCalculator {
  val defaultTiers: List<VipTierDefinition> = listOf(
    VipTierDefinition(
      id = "bronze",
      name = "Bronze",
      nameBn = "ব্রোঞ্জ মেম্বার",
      icon = "🥉",
      minSpend = 0,
      discountPercent = 0,
      perks = "স্বাগত মেম্বারশিপ ও প্রতিটি কেনাকাটায় রিওয়ার্ড পয়েন্ট অর্জন",
      accentHex = 0xFFB45309
    ),
    VipTierDefinition(
      id = "silver",
      name = "Silver",
      nameBn = "সিলভার মেম্বার",
      icon = "🥈",
      minSpend = 3000,
      discountPercent = 5,
      perks = "৫% স্পেশাল মেম্বারশিপ ছাড় + রিওয়ার্ড পয়েন্ট",
      accentHex = 0xFF475569
    ),
    VipTierDefinition(
      id = "gold",
      name = "Gold",
      nameBn = "গোল্ড মেম্বার",
      icon = "🥇",
      minSpend = 7000,
      discountPercent = 8,
      perks = "৮% স্পেশাল ভিআইপি ডিসকাউন্ট + প্রায়োরিটি ডেলিভারি",
      accentHex = 0xFFD97706
    ),
    VipTierDefinition(
      id = "diamond",
      name = "Diamond",
      nameBn = "ডায়মন্ড মেম্বার",
      icon = "💎",
      minSpend = 15000,
      discountPercent = 10,
      perks = "১০% ফ্ল্যাট ডিসকাউন্ট + বিশেষ উপহার",
      accentHex = 0xFF0284C7
    ),
    VipTierDefinition(
      id = "platinum",
      name = "Platinum",
      nameBn = "প্লাটিনাম মেম্বার",
      icon = "👑",
      minSpend = 30000,
      discountPercent = 12,
      perks = "১২% প্রিমিয়াম ভিআইপি ডিসকাউন্ট + ফ্রি এক্সপ্রেস ডেলিভারি",
      accentHex = 0xFF7C3AED
    ),
    VipTierDefinition(
      id = "platinum_elite",
      name = "Platinum Elite",
      nameBn = "প্লাটিনাম এলিট",
      icon = "⚜️",
      minSpend = 50000,
      discountPercent = 15,
      perks = "১৫% এক্সক্লুসিভ এলিট ডিসকাউন্ট + লাইফটাইম ভিআইপি কেয়ার",
      accentHex = 0xFF047857
    )
  )

  fun calculate(
    orders: List<Order>,
    authUser: AuthUserSummary? = null,
    spendPerPoint: Int = 10,
    pointsPerTaka: Int = 100
  ): CustomerLoyaltyStatus {
    val tiers = defaultTiers.sortedBy { it.minSpend }
    val validOrders = orders.filter { !it.status.equals("Cancelled", ignoreCase = true) }
    val lifetimeSpend = validOrders.sumOf { it.total.coerceAtLeast(0) }
    val validOrderCount = validOrders.size
    val safeSpendPerPoint = spendPerPoint.coerceAtLeast(1)
    val safePointsPerTaka = pointsPerTaka.coerceAtLeast(1)

    val earnedPoints = validOrders.sumOf { order ->
      val baseAmount = if (order.subtotal > 0) order.subtotal else order.total
      (baseAmount.coerceAtLeast(0) / safeSpendPerPoint)
    }
    // Welcome bonus for signed-in verified customer with zero orders yet
    val signInBonus = if (authUser != null) 50 else 0
    val totalEarned = earnedPoints + signInBonus
    val availablePoints = totalEarned.coerceAtLeast(0)
    val redeemableTaka = availablePoints / safePointsPerTaka

    var currentTier = tiers.first()
    var nextTier: VipTierDefinition? = tiers.getOrNull(1)
    for (i in tiers.indices.reversed()) {
      if (lifetimeSpend >= tiers[i].minSpend) {
        currentTier = tiers[i]
        nextTier = tiers.getOrNull(i + 1)
        break
      }
    }

    val amountToNext = if (nextTier != null) {
      (nextTier.minSpend - lifetimeSpend).coerceAtLeast(0)
    } else {
      0
    }
    val progress = if (nextTier != null) {
      val span = (nextTier.minSpend - currentTier.minSpend).coerceAtLeast(1)
      val done = (lifetimeSpend - currentTier.minSpend).coerceAtLeast(0)
      (done.toFloat() / span.toFloat()).coerceIn(0f, 1f)
    } else {
      1f
    }

    return CustomerLoyaltyStatus(
      currentTier = currentTier,
      nextTier = nextTier,
      lifetimeSpend = lifetimeSpend,
      validOrderCount = validOrderCount,
      earnedPoints = totalEarned,
      redeemedPoints = 0,
      availablePoints = availablePoints,
      redeemableTaka = redeemableTaka,
      amountToNextTier = amountToNext,
      progressFraction = (progress * 100f).roundToInt() / 100f,
      spendPerPoint = safeSpendPerPoint,
      pointsPerTaka = safePointsPerTaka,
      allTiers = tiers
    )
  }
}
