package citadel.domain

/**
 * Represents a quantity of stock bought at per unit price.
 * @param timestamp Timestamp (milliseconds since epoch at UTC) at which stock was bought.
 * @param stock Stock that was bought.
 * @param quantity Quantity at which stock was bought.
 * @param pricePerUnit Price per unit at which stock was bought.
 */
case class BoughtStock(timestamp: Long, stock: Stock, quantity: Int, pricePerUnit: Amount, pricePerUnitUSD: Amount, exchangeRate: ExchangeRate)