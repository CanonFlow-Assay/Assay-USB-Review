package org.canonflow.kotlinassay

data class DraftLine(
    val sku: String,
    val quantity: Int,
    val unitPriceCents: Long,
)

data class Order(
    val identifier: String,
    val lines: List<DraftLine>,
)

sealed interface OrderDecision {
    data class Accepted(val order: Order) : OrderDecision
    data class Rejected(val reason: String) : OrderDecision
}

fun createOrder(identifier: String, lines: List<DraftLine>): OrderDecision = when {
    identifier.isBlank() -> OrderDecision.Rejected("order identifier is required")
    lines.isEmpty() -> OrderDecision.Rejected("an order needs at least one line")
    lines.any { it.sku.isBlank() } -> OrderDecision.Rejected("each line needs a SKU")
    lines.any { it.quantity <= 0 } -> OrderDecision.Rejected("quantity must be positive")
    lines.any { it.unitPriceCents < 0 } -> OrderDecision.Rejected("unit price cannot be negative")
    else -> OrderDecision.Accepted(Order(identifier, lines.toList()))
}

fun totalCents(order: Order): Long = order.lines.fold(0L) { total, line ->
    total + line.quantity * line.unitPriceCents
}
