package citadel.domain

/**
 * Represents a request to sell a stock.
 * @param timestamp Timestamp at which stock is being sold.
 * @param stockId ID of the stock.
 * @param quantity Quantity to sell.
 */
case class SellRequest(timestamp: Long, stockId: String, quantity: Int)