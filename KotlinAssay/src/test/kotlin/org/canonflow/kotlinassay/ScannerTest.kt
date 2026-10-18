package org.canonflow.kotlinassay

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ScannerTest {
    @Test
    fun `pure sealed outcome is clean`() {
        assertTrue(inspect("domain/Order.kt", "fun decide(): Decision = Decision.Accepted(1)").isEmpty())
    }

    @Test
    fun `selected hazards have stable locations`() {
        val source = "throw IllegalArgumentException()\nprintln(\"effect\")\nvar count = 0"
        val findings = inspect("domain/Order.kt", source)
        assertEquals(listOf("KTA-F001", "KTA-F002", "KTA-F003"), findings.map(Finding::ruleId))
        assertEquals(listOf(1, 2, 3), findings.map(Finding::line))
    }

    @Test
    fun `receipt bytes are deterministic`() {
        val findings = inspect("domain/Order.kt", "println(\"effect\")")
        assertEquals(
            renderReceipt("examples/clean", 1, findings),
            renderReceipt("examples/clean", 1, findings),
        )
    }

    @Test
    fun `valid order has deterministic total`() {
        val decision = createOrder("order-42", listOf(DraftLine("book", 2, 1_250)))
        val accepted = decision as OrderDecision.Accepted
        assertEquals(2_500, totalCents(accepted.order))
    }

    @Test
    fun `invalid order is a value`() {
        assertEquals(OrderDecision.Rejected("order identifier is required"), createOrder("", emptyList()))
    }
}
