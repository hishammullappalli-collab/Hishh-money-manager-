package com.example.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.abs

object CurrencyFormatter {
    fun formatInr(amount: Double): String {
        val formatted = formatIndianNumber(abs(amount))
        return if (amount < -0.004) "−₹$formatted" else "₹$formatted"
    }

    fun formatSignedInr(amount: Double, isIncome: Boolean): String {
        val formatted = formatIndianNumber(abs(amount))
        return if (isIncome) "+₹$formatted" else "−₹$formatted"
    }

    private fun formatIndianNumber(value: Double): String {
        val rounded = kotlin.math.round(value * 100.0) / 100.0
        val whole = rounded.toLong()
        val fracCents = kotlin.math.round((rounded - whole) * 100.0).toInt()
        val wholeStr = whole.toString()
        val groupedWhole = if (wholeStr.length <= 3) {
            wholeStr
        } else {
            val lastThree = wholeStr.takeLast(3)
            val rest = wholeStr.dropLast(3)
            val restGrouped = rest.reversed().chunked(2).joinToString(",").reversed()
            "$restGrouped,$lastThree"
        }
        return when {
            fracCents <= 0 -> groupedWhole
            fracCents % 10 == 0 -> "$groupedWhole.${fracCents / 10}"
            else -> "$groupedWhole.${fracCents.toString().padStart(2, '0')}"
        }
    }
}

object DateUtils {
    private val inLocale = Locale("en", "IN")

    fun currentYearMonth(): String = yearMonthFromMillis(System.currentTimeMillis())

    fun yearMonthFromMillis(millis: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = millis }
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        return String.format(Locale.US, "%04d-%02d", year, month)
    }

    fun formatMonthLabel(yearMonth: String): String {
        val parts = yearMonth.split("-")
        if (parts.size != 2) return yearMonth
        val year = parts[0].toIntOrNull() ?: return yearMonth
        val month = parts[1].toIntOrNull() ?: return yearMonth
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val sdf = SimpleDateFormat("MMMM yyyy", inLocale)
        return sdf.format(cal.time)
    }

    fun shiftMonth(yearMonth: String, deltaMonths: Int): String {
        val parts = yearMonth.split("-")
        if (parts.size != 2) return currentYearMonth()
        val year = parts[0].toIntOrNull() ?: return currentYearMonth()
        val month = parts[1].toIntOrNull() ?: return currentYearMonth()
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.MONTH, deltaMonths)
        }
        return yearMonthFromMillis(cal.timeInMillis)
    }

    fun formatTransactionDate(millis: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", inLocale)
        return sdf.format(Date(millis))
    }

    fun formatIsoUtc(millis: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        return sdf.format(Date(millis))
    }

    fun parseIsoUtc(iso: String): Long? {
        val patterns = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd"
        )
        for (pattern in patterns) {
            try {
                val sdf = SimpleDateFormat(pattern, Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                val parsed = sdf.parse(iso)
                if (parsed != null) return parsed.time
            } catch (_: Exception) {
                // try next pattern
            }
        }
        return null
    }
}
