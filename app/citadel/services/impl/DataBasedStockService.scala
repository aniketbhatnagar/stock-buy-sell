package citadel.services.impl

import citadel.domain.{StockPrice, Stock}
import citadel.services.StockService

import scala.concurrent.{ExecutionContext, Future}

/**
 * Implementation of service that loads stock prices from provided data.
 */
class DataBasedStockService(implicit executionContext: ExecutionContext = ExecutionContext.Implicits.global)
  extends StockService {

  private case class TimeRangedStockPrice(startTimestamp: Long, endTimestamp: Long, stockPrice: StockPrice)

  private val stockPrices: Map[Stock, List[TimeRangedStockPrice]] = {
    val dayWisePrices = for {
      (dataRow1, dataRow2) <- Data.data.zip(Data.data.drop(1))
      price                <- dataRow1.stockPrices
    } yield {
      TimeRangedStockPrice(dataRow1.date.getTime, dataRow2.date.getTime, price)
    }
    dayWisePrices.groupBy(timeRangedStockPrice => timeRangedStockPrice.stockPrice.stock)
                 .map {
                   case (stock, prices) => (stock, prices.sortBy(price => price.startTimestamp))
                 }
  }

  /**
   * Looks up stock by ID.
   * @param stockId ID of the stock.
   * @return Future containing optional stock.
   */
  override def lookupStock(stockId: String): Future[Option[Stock]] = {
    Future {
      stockId match {
        case "ABC" => Some(Data.ABC)
        case "DEF" => Some(Data.DEF)
        case "XYZ" => Some(Data.XYZ)
        case _     => None
      }
    }
  }

  /**
   * Looks up stock price at a particular time.
   * @param stock Stock whose price needs to be looked up.
   * @param timestamp Timestamp at which stock price needs to be looked up.
   * @return Future containing current stock price.
   */
  override def lookupPrice(stock: Stock, timestamp: Long): Future[StockPrice] = {
    Future {
      val priceOpt = for {
        prices <- stockPrices.get(stock)
        price  <- prices.binarySearch(price => {
          if (timestamp < price.startTimestamp) {
            -1
          } else if (timestamp >= price.startTimestamp && timestamp < price.endTimestamp) {
            0
          } else {
            1
          }
        })
      } yield {
        price.stockPrice
      }
      priceOpt.getOrElse(throw new IllegalArgumentException(s"stock ${stock.id}'s price not found for $timestamp"))
    }
  }
}
