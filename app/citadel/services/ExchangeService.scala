package citadel.services

import citadel.domain.{ExchangeRate, Currency, Amount}

import scala.concurrent.Future

/**
 * Provides exchange rates between currency.
 */
trait ExchangeService {
  /**
   * Exchanges an amount to specified currency.
   * @param timestamp Timestamp at which exchange is going to take place.
   * @param amount Amount to exchange.
   * @param currency Currency to which amount needs to exchanged.
   * @return Future containing exchanged amount and exchange rate.
   */
  def exchange(timestamp: Long, amount: Amount, currency: Currency): Future[(ExchangeRate, Amount)]

  /**
   * Gets exchange rate for the currenct at the specified time.
   * @param timestamp Timestamp in milliseconds.
   * @param srcCurrency Currency that needs to be exchanged.
   * @param dstCurrency Currency to which srcCurrency needs to be exchanged.
   * @return Future containing exchange rate.
   */
  def exchangeRate(timestamp: Long, srcCurrency: Currency, dstCurrency: Currency): Future[ExchangeRate]
}
