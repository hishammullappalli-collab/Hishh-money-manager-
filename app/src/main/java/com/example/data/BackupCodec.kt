package com.example.data

import com.example.util.DateUtils
import org.json.JSONArray
import org.json.JSONObject

data class BackupPayload(
    val transactions: List<TransactionEntity>,
    val goal: Double
)

object BackupCodec {
    fun encodeCsv(transactions: List<TransactionEntity>): String {
        val sb = StringBuilder()
        sb.appendLine("\"Date\",\"Type\",\"Category\",\"Description\",\"Amount\"")
        transactions.forEach { tx ->
            val fields = listOf(
                DateUtils.formatIsoUtc(tx.timestampMillis),
                tx.type,
                tx.category,
                tx.note,
                if (tx.amount % 1.0 == 0.0) tx.amount.toLong().toString() else tx.amount.toString()
            )
            sb.appendLine(fields.joinToString(",") { escapeCsv(it) })
        }
        return sb.toString()
    }

    fun encodeJson(transactions: List<TransactionEntity>, goal: Double): String {
        val root = JSONObject()
        val array = JSONArray()
        transactions.forEach { tx ->
            val obj = JSONObject()
            obj.put("id", tx.id)
            obj.put("type", tx.type)
            obj.put("amount", tx.amount)
            obj.put("category", tx.category)
            obj.put("note", tx.note)
            obj.put("date", DateUtils.formatIsoUtc(tx.timestampMillis))
            array.put(obj)
        }
        root.put("transactions", array)
        root.put("goal", goal)
        return root.toString(2)
    }

    fun decodeJson(rawJson: String): BackupPayload? {
        return try {
            val root = JSONObject(rawJson)
            val goal = root.optDouble("goal", GoalPreferencesRepository.DEFAULT_MONTHLY_GOAL)
                .takeIf { it.isFinite() && it >= 0.0 }
                ?: GoalPreferencesRepository.DEFAULT_MONTHLY_GOAL
            val txArray = root.optJSONArray("transactions") ?: JSONArray()
            val list = buildList {
                for (i in 0 until txArray.length()) {
                    val obj = txArray.optJSONObject(i) ?: continue
                    val amount = obj.optDouble("amount", -1.0)
                    if (!amount.isFinite() || amount <= 0.0) continue
                    val type = TransactionType.fromKey(obj.optString("type", "income")).key
                    val category = obj.optString("category", "").trim().ifEmpty {
                        if (type == TransactionType.INCOME.key) "Other Income" else "Other Expense"
                    }
                    val note = obj.optString("note", "").trim().ifEmpty { category }
                    val dateIso = obj.optString("date", "")
                    val millis = DateUtils.parseIsoUtc(dateIso) ?: System.currentTimeMillis()
                    add(
                        TransactionEntity(
                            type = type,
                            amount = amount,
                            category = category,
                            note = note,
                            timestampMillis = millis,
                            yearMonth = DateUtils.yearMonthFromMillis(millis)
                        )
                    )
                }
            }
            BackupPayload(transactions = list, goal = goal)
        } catch (_: Exception) {
            null
        }
    }

    private fun escapeCsv(value: String): String {
        val escaped = value.replace("\"", "\"\"")
        return "\"$escaped\""
    }
}
