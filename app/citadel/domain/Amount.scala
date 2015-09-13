package citadel.domain

/**
 * Represents an amount in a specific currency.
 * @param value The value of the amount (in the currency).
 * @param currency The currency.
 */
case class Amount(value: Double, currency: Currency)
