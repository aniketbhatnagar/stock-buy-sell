package citadel.services

import citadel.domain.{SoldStock, SellRequest, BoughtStock, BuyRequest}

import scala.concurrent.Future

/**
 * Service to buy/sell stocks.
 */
trait BuySellService {

  /**
   * Buys a particular quantity of stock.
   * @param request Request containing stock that is being bought and quantity.
   * @return Future containing bought stock.
   */
  def buyStock(request: BuyRequest): Future[BoughtStock] = {
    buyStock(request.timestamp, request.stockId, request.quantity)
  }

  /**
   * Buys a particular quantity of stock.
   * @param timestamp Timestamp at which stock is being bought.
   * @param stockId ID of the stock.
   * @param quantity Quantity to buy.
   * @return Future containing bought stock.
   */
  def buyStock(timestamp: Long, stockId: String, quantity: Int): Future[BoughtStock]

  /**
   * Sells a particular quantity of stock.
   * @param timestamp Timestamp at which stock is being sold.
   * @param stockId ID of the stock.
   * @param quantity Quantity to sell.
   * @return Future containing sold stock.
   */
  def sellStock(timestamp: Long, stockId: String, quantity: Int): Future[SoldStock]

  /**
   * Sells a particular quantity of stock.
   * @param request Request for selling a stock.
   * @return Future containing sold stock.
   */
  def sellStock(request: SellRequest): Future[SoldStock] = {
    sellStock(request.timestamp, request.stockId, request.quantity)
  }
}
