package citadel.domain

/**
 * Represents a quantity of sold stock at a specific price resulting in 'net' profit/loss.
 * @param timestamp Timestamp (milliseconds since epoch at UTC) at which stock was sold.
 * @param stock Stock that was sold.
 * @param quantity Quantity that was sold.
 * @param pricePerUnit Price per unit at which it was sold
 * @param net Net profit/loss from selling the stock.
 */
case class SoldStock(timestamp: Long, stock: Stock, quantity: Int, pricePerUnit: Amount, pricePerUnitUSD: Amount, net: Amount, exchangeRate: ExchangeRate)
