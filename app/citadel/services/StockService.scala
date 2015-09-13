package citadel.services

import citadel.domain.{StockPrice, Stock}

import scala.concurrent.Future

/**
 * Looks up stocks and stock prices.
 * The implementation will most likely hit stock exchange for lookups.
 */
trait StockService {

  /**
   * Looks up stock by ID.
   * @param stockId ID of the stock.
   * @return Future containing optional stock.
   */
  def lookupStock(stockId: String): Future[Option[Stock]]

  /**
   * Looks up stock price at a particular time.
   * @param stock Stock whose price needs to be looked up.
   * @param timestamp Timestamp at which stock price needs to be looked up.
   * @return Future containing current stock price.
   */
  def lookupPrice(stock: Stock, timestamp: Long): Future[StockPrice]

}
