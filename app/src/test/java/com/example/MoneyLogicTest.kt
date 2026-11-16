package com.example

import com.example.data.BackupCodec
import com.example.data.TransactionEntity
import com.example.util.CurrencyFormatter
import com.example.util.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class MoneyLogicTest {

    @Test
    fun `currency formatter uses Indian grouping and rupee sign`() {
        assertEquals("₹0", CurrencyFormatter.formatInr(0.0))
        assertEquals("₹1,24,500", CurrencyFormatter.formatInr(124500.0))
        assertEquals("−₹500", CurrencyFormatter.formatInr(-500.0))
        assertEquals("+₹2,500.5", CurrencyFormatter.formatSignedInr(2500.5, isIncome = true))
        assertEquals("−₹750", CurrencyFormatter.formatSignedInr(750.0, isIncome = false))
    }

    @Test
    fun `month shifting handles year boundaries`() {
        assertEquals("2026-12", DateUtils.shiftMonth("2027-01", -1))
        assertEquals("2027-01", DateUtils.shiftMonth("2026-12", 1))
    }

    @Test
    fun `csv export escapes quotes and includes header`() {
        val tx = TransactionEntity(
            id = 1L,
            type = "income",
            amount = 1500.0,
            category = "Freelancing / Editing",
            note = "Logo \"Gold\" design",
            timestampMillis = 1794853846000L,
            yearMonth = "2026-11"
        )
        val csv = BackupCodec.encodeCsv(listOf(tx))
        assertTrue(csv.startsWith("\"Date\",\"Type\",\"Category\",\"Description\",\"Amount\""))
        assertTrue(csv.contains("\"Logo \"\"Gold\"\" design\""))
        assertTrue(csv.contains("\"1500\""))
    }

    @Test
    fun `json backup round trips transactions and goal`() {
        val tx = TransactionEntity(
            id = 7L,
            type = "expense",
            amount = 320.5,
            category = "Food",
            note = "Coffee & snacks",
            timestampMillis = 1794853846000L,
            yearMonth = "2026-11"
        )
        val json = BackupCodec.encodeJson(listOf(tx), goal = 12000.0)
        val decoded = BackupCodec.decodeJson(json)
        assertNotNull(decoded)
        assertEquals(12000.0, decoded!!.goal, 0.001)
        assertEquals(1, decoded.transactions.size)
        assertEquals("expense", decoded.transactions[0].type)
        assertEquals(320.5, decoded.transactions[0].amount, 0.001)
        assertEquals("Food", decoded.transactions[0].category)
        assertEquals("Coffee & snacks", decoded.transactions[0].note)
    }

    @Test
    fun `invalid json backup returns null`() {
        assertNull(BackupCodec.decodeJson("not valid json"))
    }
}
