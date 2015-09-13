package citadel.domain

/**
 * Represents the price of a stock.
 * @param stock Stock whose price is represented.
 * @param price Price of the stock in local currency.
 */
case class StockPrice(stock: Stock, price: Amount)