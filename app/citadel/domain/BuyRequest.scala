package citadel.domain

/**
 * Represents a request to buy a stock.
 * @param timestamp Timestamp at which stock is being bought.
 * @param stockId ID of the stock.
 * @param quantity Quantity to buy.
 */
case class BuyRequest(timestamp: Long, stockId: String, quantity: Int)
