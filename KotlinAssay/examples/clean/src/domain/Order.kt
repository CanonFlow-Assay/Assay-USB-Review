sealed interface Decision {
    data class Accepted(val quantity: Int) : Decision
    data class Rejected(val reason: String) : Decision
}

fun decide(quantity: Int): Decision =
    if (quantity > 0) Decision.Accepted(quantity) else Decision.Rejected("quantity must be positive")
