package com.example.util

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor

data class QrMatrix(
  val size: Int,
  val modules: BooleanArray
) {
  fun get(x: Int, y: Int): Boolean = modules[y * size + x]

  fun toBitmap(pixelSize: Int = 360, quietZoneModules: Int = 3): Bitmap {
    val totalModules = size + quietZoneModules * 2
    val scale = (pixelSize / totalModules).coerceAtLeast(2)
    val bmpSize = totalModules * scale
    val pixels = IntArray(bmpSize * bmpSize) { AndroidColor.WHITE }

    for (my in 0 until size) {
      for (mx in 0 until size) {
        if (get(mx, my)) {
          val startX = (mx + quietZoneModules) * scale
          val startY = (my + quietZoneModules) * scale
          for (py in startY until startY + scale) {
            val rowOffset = py * bmpSize
            for (px in startX until startX + scale) {
              pixels[rowOffset + px] = AndroidColor.BLACK
            }
          }
        }
      }
    }

    return Bitmap.createBitmap(pixels, bmpSize, bmpSize, Bitmap.Config.ARGB_8888)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is QrMatrix) return false
    return size == other.size && modules.contentEquals(other.modules)
  }

  override fun hashCode(): Int = 31 * size + modules.contentHashCode()
}

/**
 * Zero-dependency ISO/IEC 18004 QR Code encoder (Versions 1..9, EC Level M, Byte mode).
 * Generates scannable QR matrices and Bitmaps completely offline for shipping labels.
 */
object QrCodeGenerator {

  private data class VersionSpec(
    val version: Int,
    val totalCodewords: Int,
    val ecPerBlock: Int,
    val group1Blocks: Int,
    val group1DataPerBlock: Int,
    val group2Blocks: Int = 0,
    val group2DataPerBlock: Int = 0,
    val alignmentPositions: IntArray = intArrayOf()
  ) {
    val totalDataCodewords: Int
      get() = group1Blocks * group1DataPerBlock + group2Blocks * group2DataPerBlock

    val maxBytePayload: Int
      get() = ((totalDataCodewords * 8 - 12) / 8).coerceAtLeast(1)
  }

  // ISO/IEC 18004 Error Correction Level M specifications for Versions 1..9
  private val VERSION_SPECS_M = listOf(
    VersionSpec(1, 26, 10, 1, 16, alignmentPositions = intArrayOf()),
    VersionSpec(2, 44, 16, 1, 28, alignmentPositions = intArrayOf(6, 18)),
    VersionSpec(3, 70, 26, 1, 44, alignmentPositions = intArrayOf(6, 22)),
    VersionSpec(4, 100, 18, 2, 32, alignmentPositions = intArrayOf(6, 26)),
    VersionSpec(5, 134, 24, 2, 43, alignmentPositions = intArrayOf(6, 30)),
    VersionSpec(6, 172, 16, 4, 27, alignmentPositions = intArrayOf(6, 34)),
    VersionSpec(7, 196, 18, 4, 31, alignmentPositions = intArrayOf(6, 22, 38)),
    VersionSpec(8, 242, 22, 2, 38, 2, 39, alignmentPositions = intArrayOf(6, 24, 42)),
    VersionSpec(9, 292, 22, 3, 36, 2, 37, alignmentPositions = intArrayOf(6, 26, 46))
  )

  private val GF_EXP = IntArray(512)
  private val GF_LOG = IntArray(256)

  init {
    var x = 1
    for (i in 0 until 255) {
      GF_EXP[i] = x
      GF_LOG[x] = i
      x = x shl 1
      if ((x and 0x100) != 0) {
        x = x xor 0x11D
      }
    }
    for (i in 255 until 512) {
      GF_EXP[i] = GF_EXP[i - 255]
    }
  }

  private fun gfMul(a: Int, b: Int): Int {
    if (a == 0 || b == 0) return 0
    return GF_EXP[GF_LOG[a] + GF_LOG[b]]
  }

  private fun rsGeneratorPoly(degree: Int): IntArray {
    var poly = intArrayOf(1)
    for (i in 0 until degree) {
      val next = IntArray(poly.size + 1)
      val factor = GF_EXP[i]
      for (j in poly.indices) {
        next[j] = next[j] xor poly[j]
        next[j + 1] = next[j + 1] xor gfMul(poly[j], factor)
      }
      poly = next
    }
    return poly
  }

  private fun rsComputeRemainder(data: IntArray, ecLength: Int): IntArray {
    val gen = rsGeneratorPoly(ecLength)
    val result = IntArray(ecLength)
    for (b in data) {
      val factor = b xor result[0]
      System.arraycopy(result, 1, result, 0, ecLength - 1)
      result[ecLength - 1] = 0
      for (j in 0 until ecLength) {
        result[j] = result[j] xor gfMul(gen[j + 1], factor)
      }
    }
    return result
  }

  fun encode(text: String): QrMatrix {
    var rawBytes = text.toByteArray(Charsets.UTF_8)
    val maxSupported = VERSION_SPECS_M.last().maxBytePayload
    if (rawBytes.size > maxSupported) {
      rawBytes = rawBytes.copyOf(maxSupported)
    }

    val spec = VERSION_SPECS_M.first { rawBytes.size <= it.maxBytePayload }
    val size = 17 + 4 * spec.version

    // 1. Encode data bits (Byte mode 0100 + 8-bit char count + bytes + terminator + padding)
    val totalDataBits = spec.totalDataCodewords * 8
    val bits = ArrayList<Int>(totalDataBits)
    fun appendBits(value: Int, length: Int) {
      for (i in length - 1 downTo 0) {
        bits.add((value ushr i) and 1)
      }
    }

    appendBits(0b0100, 4) // Byte mode
    appendBits(rawBytes.size, 8)
    for (byte in rawBytes) {
      appendBits(byte.toInt() and 0xFF, 8)
    }
    val terminatorLen = minOf(4, totalDataBits - bits.size)
    appendBits(0, terminatorLen)
    while (bits.size % 8 != 0) {
      bits.add(0)
    }
    var padIndex = 0
    while (bits.size < totalDataBits) {
      appendBits(if (padIndex % 2 == 0) 0xEC else 0x11, 8)
      padIndex++
    }

    val dataCodewords = IntArray(spec.totalDataCodewords) { idx ->
      var v = 0
      for (b in 0 until 8) {
        v = (v shl 1) or bits[idx * 8 + b]
      }
      v
    }

    // 2. Split into RS blocks & compute EC codewords
    val totalBlocks = spec.group1Blocks + spec.group2Blocks
    val dataBlocks = Array(totalBlocks) { IntArray(0) }
    val ecBlocks = Array(totalBlocks) { IntArray(0) }
    var offset = 0
    for (i in 0 until totalBlocks) {
      val blockLen = if (i < spec.group1Blocks) spec.group1DataPerBlock else spec.group2DataPerBlock
      val slice = dataCodewords.copyOfRange(offset, offset + blockLen)
      offset += blockLen
      dataBlocks[i] = slice
      ecBlocks[i] = rsComputeRemainder(slice, spec.ecPerBlock)
    }

    // 3. Interleave data & EC codewords
    val interleaved = ArrayList<Int>(spec.totalCodewords)
    val maxDataLen = maxOf(spec.group1DataPerBlock, spec.group2DataPerBlock)
    for (col in 0 until maxDataLen) {
      for (blk in 0 until totalBlocks) {
        if (col < dataBlocks[blk].size) interleaved.add(dataBlocks[blk][col])
      }
    }
    for (col in 0 until spec.ecPerBlock) {
      for (blk in 0 until totalBlocks) {
        interleaved.add(ecBlocks[blk][col])
      }
    }

    val finalBits = ArrayList<Int>(interleaved.size * 8 + 7)
    for (cw in interleaved) {
      for (i in 7 downTo 0) {
        finalBits.add((cw ushr i) and 1)
      }
    }

    // 4. Place function patterns
    val baseModules = BooleanArray(size * size)
    val isFunction = BooleanArray(size * size)

    fun setFunction(x: Int, y: Int, dark: Boolean) {
      baseModules[y * size + x] = dark
      isFunction[y * size + x] = true
    }

    fun drawFinderPattern(cx: Int, cy: Int) {
      for (dy in -4..4) {
        for (dx in -4..4) {
          val x = cx + dx
          val y = cy + dy
          if (x !in 0 until size || y !in 0 until size) continue
          val dist = maxOf(kotlin.math.abs(dx), kotlin.math.abs(dy))
          val dark = dist != 2 && dist != 4
          setFunction(x, y, dark)
        }
      }
    }

    drawFinderPattern(3, 3)
    drawFinderPattern(size - 4, 3)
    drawFinderPattern(3, size - 4)

    // Alignment patterns
    val alignPos = spec.alignmentPositions
    for (ay in alignPos) {
      for (ax in alignPos) {
        val overlapsFinder =
          (ax <= 8 && ay <= 8) ||
            (ax >= size - 9 && ay <= 8) ||
            (ax <= 8 && ay >= size - 9)
        if (!overlapsFinder) {
          for (dy in -2..2) {
            for (dx in -2..2) {
              val dist = maxOf(kotlin.math.abs(dx), kotlin.math.abs(dy))
              setFunction(ax + dx, ay + dy, dist != 1)
            }
          }
        }
      }
    }

    // Timing patterns
    for (i in 8 until size - 8) {
      val dark = i % 2 == 0
      if (!isFunction[6 * size + i]) setFunction(i, 6, dark)
      if (!isFunction[i * size + 6]) setFunction(6, i, dark)
    }

    // Dark module & reserve format info strips
    setFunction(8, size - 8, true)
    for (i in 0..8) {
      if (i != 6) {
        isFunction[8 * size + i] = true
        isFunction[i * size + 8] = true
      }
    }
    for (i in 0..7) {
      isFunction[8 * size + (size - 1 - i)] = true
      if (i < 7) isFunction[(size - 1 - i) * size + 8] = true
    }

    // Reserve & write Version Info (V7+)
    if (spec.version >= 7) {
      val versionBits = computeVersionInfo(spec.version)
      for (i in 0 until 18) {
        val bit = ((versionBits ushr i) and 1) == 1
        val a = size - 11 + (i % 3)
        val b = i / 3
        setFunction(a, b, bit)
        setFunction(b, a, bit)
      }
    }

    // 5. Place data bits in zig-zag order
    var bitIdx = 0
    var upward = true
    var right = size - 1
    while (right >= 1) {
      if (right == 6) right--
      for (step in 0 until size) {
        val y = if (upward) size - 1 - step else step
        for (dx in 0..1) {
          val x = right - dx
          if (!isFunction[y * size + x]) {
            val bit = if (bitIdx < finalBits.size) finalBits[bitIdx] == 1 else false
            baseModules[y * size + x] = bit
            bitIdx++
          }
        }
      }
      upward = !upward
      right -= 2
    }

    // 6. Try all 8 masks & pick lowest ISO penalty score
    var bestMask = 0
    var bestScore = Int.MAX_VALUE
    var bestModules = baseModules

    for (mask in 0 until 8) {
      val candidate = baseModules.copyOf()
      for (y in 0 until size) {
        for (x in 0 until size) {
          if (!isFunction[y * size + x] && shouldFlipMask(mask, x, y)) {
            candidate[y * size + x] = !candidate[y * size + x]
          }
        }
      }
      applyFormatInfo(candidate, size, mask)
      val score = computePenaltyScore(candidate, size)
      if (score < bestScore) {
        bestScore = score
        bestMask = mask
        bestModules = candidate
      }
    }

    return QrMatrix(size = size, modules = bestModules)
  }

  private fun shouldFlipMask(mask: Int, x: Int, y: Int): Boolean = when (mask) {
    0 -> (x + y) % 2 == 0
    1 -> y % 2 == 0
    2 -> x % 3 == 0
    3 -> (x + y) % 3 == 0
    4 -> ((y / 2) + (x / 3)) % 2 == 0
    5 -> ((x * y) % 2 + (x * y) % 3) == 0
    6 -> (((x * y) % 2) + ((x * y) % 3)) % 2 == 0
    7 -> (((x + y) % 2) + ((x * y) % 3)) % 2 == 0
    else -> false
  }

  private fun computeFormatInfo(mask: Int): Int {
    // Error correction Level M = 00
    val data = mask and 0x7
    var rem = data shl 10
    val poly = 0x537
    for (i in 14 downTo 10) {
      if ((rem ushr i) and 1 != 0) {
        rem = rem xor (poly shl (i - 10))
      }
    }
    return ((data shl 10) or rem) xor 0x5412
  }

  private fun computeVersionInfo(version: Int): Int {
    var rem = version shl 12
    val poly = 0x1F25
    for (i in 17 downTo 12) {
      if ((rem ushr i) and 1 != 0) {
        rem = rem xor (poly shl (i - 12))
      }
    }
    return (version shl 12) or rem
  }

  private fun applyFormatInfo(modules: BooleanArray, size: Int, mask: Int) {
    val fmt = computeFormatInfo(mask)
    for (i in 0 until 15) {
      val bit = ((fmt ushr i) and 1) == 1
      // First copy around top-left finder
      when {
        i < 6 -> modules[8 * size + i] = bit
        i < 8 -> modules[8 * size + (i + 1)] = bit
        else -> modules[(14 - i) * size + 8] = bit
      }
      // Second copy split bottom-left (7 bits: i=0..6) & top-right (8 bits: i=7..14)
      if (i < 7) {
        modules[(size - 1 - i) * size + 8] = bit
      } else {
        modules[8 * size + (size - 15 + i)] = bit
      }
    }
  }

  private fun computePenaltyScore(modules: BooleanArray, size: Int): Int {
    var penalty = 0

    // Rule 1: 5+ consecutive same-color modules in row or column
    for (y in 0 until size) {
      var runColor = modules[y * size]
      var runLen = 1
      for (x in 1 until size) {
        val c = modules[y * size + x]
        if (c == runColor) {
          runLen++
        } else {
          if (runLen >= 5) penalty += 3 + (runLen - 5)
          runColor = c
          runLen = 1
        }
      }
      if (runLen >= 5) penalty += 3 + (runLen - 5)
    }
    for (x in 0 until size) {
      var runColor = modules[x]
      var runLen = 1
      for (y in 1 until size) {
        val c = modules[y * size + x]
        if (c == runColor) {
          runLen++
        } else {
          if (runLen >= 5) penalty += 3 + (runLen - 5)
          runColor = c
          runLen = 1
        }
      }
      if (runLen >= 5) penalty += 3 + (runLen - 5)
    }

    // Rule 2: 2x2 blocks of same color
    for (y in 0 until size - 1) {
      for (x in 0 until size - 1) {
        val c = modules[y * size + x]
        if (c == modules[y * size + x + 1] &&
          c == modules[(y + 1) * size + x] &&
          c == modules[(y + 1) * size + x + 1]
        ) {
          penalty += 3
        }
      }
    }

    // Rule 4: Dark/light ratio
    var darkCount = 0
    for (m in modules) if (m) darkCount++
    val total = modules.size
    val percent = (darkCount * 100) / total
    val k = kotlin.math.abs(percent - 50) / 5
    penalty += k * 10

    return penalty
  }
}
