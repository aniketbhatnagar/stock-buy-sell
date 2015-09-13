package citadel.services.impl

import citadel.domain.{ExchangeRate, Amount, Currency}
import citadel.services.ExchangeService

import scala.concurrent.{ExecutionContext, Future}

/**
 * Implementation of exchange service that loads exchanges from provided data.
 * @param executionContext To compose async operations.
 */
class DataBasedExchangeService(implicit executionContext: ExecutionContext = ExecutionContext.Implicits.global)
  extends ExchangeService {

  private case class TimeRangedExchangeRow(startTimestamp: Long, endTimestamp: Long, exchange: ExchangeRate)
  private case class CurrencyExchangeKey(srcCurrency: Currency, dstCurrency: Currency)

  private val currencyExchanges: Map[CurrencyExchangeKey, List[TimeRangedExchangeRow]] = {
    val dayWiseExchanges = for {
      (dataRow1, dataRow2) <- Data.data.zip(Data.data.drop(1))
      exchangeRate         <- dataRow1.exchangeRates
    } yield {
      TimeRangedExchangeRow(dataRow1.date.getTime, dataRow2.date.getTime, exchangeRate)
    }
    dayWiseExchanges.groupBy(timeRangedExchangeRow => {
      CurrencyExchangeKey(timeRangedExchangeRow.exchange.srcCurrency, timeRangedExchangeRow.exchange.dstCurrency)
    }).map {
      case (currencyExKey, exchanges) => (currencyExKey, exchanges.sortBy(exchange => exchange.startTimestamp))
    }
  }

  /**
   * Exchanges an amount to specified currency.
   * @param timestamp Timestamp at which exchange is going to take place.
   * @param amount Amount to exchange.
   * @param currency Currency to which amount needs to exchanged.
   * @return Future containing exchanged amount.
   */
  override def exchange(timestamp: Long, amount: Amount, currency: Currency): Future[(ExchangeRate, Amount)] = {
    val rateFuture = exchangeRate(timestamp, amount.currency, currency)
    rateFuture.map(rate => {
      (rate, Amount(amount.value * rate.exchangeRate, currency))
    })
  }

  /**
   * Gets exchange rate for the currenct at the specified time.
   * @param timestamp Timestamp in milliseconds.
   * @param srcCurrency Currency that needs to be exchanged.
   * @param dstCurrency Currency to which srcCurrency needs to be exchanged.
   * @return Future containing exchange rate.
   */
  override def exchangeRate(timestamp: Long, srcCurrency: Currency, dstCurrency: Currency): Future[ExchangeRate] = {
    Future {
      if (srcCurrency.equals(dstCurrency)) {
        ExchangeRate(srcCurrency, dstCurrency, 1)
      } else {
        val exchanges = currencyExchanges(CurrencyExchangeKey(srcCurrency, dstCurrency))
        val exchangeRowOpt = exchanges.binarySearch(timeRangedExchangeRow =>  {
          if (timestamp < timeRangedExchangeRow.startTimestamp) {
            -1
          } else if (timestamp >= timeRangedExchangeRow.startTimestamp && timestamp < timeRangedExchangeRow.endTimestamp) {
            0
          } else {
            1
          }
        })
        exchangeRowOpt.map(exchangeRow => exchangeRow.exchange)
          .getOrElse(throw new IllegalArgumentException(s"No exchange found from $srcCurrency to $dstCurrency at $timestamp"))
      }
    }
  }
}
