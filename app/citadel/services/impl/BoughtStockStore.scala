/**
 * SapientNitro (c) 2015.
 */
package citadel.services.impl

import citadel.domain._

import scala.concurrent.Future

/**
 * Maintains a log of buy/sell transactions.
 */
trait BoughtStockStore {
  /**
   * Registers that a specific quantity of stock was bought at a particular price.
   */
  def registerBought(boughtStock: BoughtStock): Future[Unit]

  /**
   * Looks up bought stock.
   * @param stock stock to lookup.
   * Future containing quantity of stock bought and its price at which it was bought.
   */
  def lookupBought(stock: Stock): Future[BoughtStockRow]

  /**
   * Registers that a specific quantity of stock was sold at a particular price.
   * An registration for stock sold at higher quantity than it was bought earlier will result in error in the future.
   */
  def registerSold(timestamp: Long, stock: Stock, quantity: Int): Future[BoughtStockRow]
}

case class BoughtStockRow(quantity: Long, pricePerUnitUSD: Amount)