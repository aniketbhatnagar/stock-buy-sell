package citadel.services.impl

import citadel.domain.{Amount, BoughtStock, SoldStock}
import citadel.services.{ExchangeService, StockService, BuySellService}

import scala.concurrent.{ExecutionContext, Future}

class BuySellServiceImpl(stockService: StockService, exchangeService: ExchangeService,
                         boughtStockStore: BoughtStockStore)
                        (implicit executionContext: ExecutionContext = ExecutionContext.Implicits.global)
  extends BuySellService {
  /**
   * Buys a particular quantity of stock.
   * @param timestamp Timestamp at which stock is being bought.
   * @param stockId ID of the stock.
   * @param quantity Quantity to buy.
   * @return Future containing bought stock.
   */
  override def buyStock(timestamp: Long, stockId: String, quantity: Int): Future[BoughtStock] = {
    for {
      stockOpt     <- stockService.lookupStock(stockId)
      stock         = stockOpt.getOrElse(throw new IllegalArgumentException(s"Can't find stock with id $stockId"))
      stockPrice   <- stockService.lookupPrice(stock, timestamp)
      (exchangeRate, stockPriceUSD) <- exchangeService.exchange(timestamp, stockPrice.price, Data.USD)
      boughtStock   = BoughtStock(timestamp, stock, quantity, stockPrice.price, stockPriceUSD, exchangeRate)
      _            <- boughtStockStore.registerBought(boughtStock)
    } yield {
      boughtStock
    }
  }

  /**
   * Sells a particular quantity of stock.
   * @param timestamp Timestamp at which stock is being sold.
   * @param stockId ID of the stock.
   * @param quantity Quantity to sell.
   * @return Future containing sold stock.
   */
  override def sellStock(timestamp: Long, stockId: String, quantity: Int): Future[SoldStock] = {
    for {
      stockOpt        <- stockService.lookupStock(stockId)
      stock            = stockOpt.getOrElse(throw new IllegalArgumentException(s"Can't find stock with id $stockId"))
      stockPrice      <- stockService.lookupPrice(stock, timestamp)
      (exRate, stockPriceUSD) <- exchangeService.exchange(timestamp, stockPrice.price, Data.USD)
      boughtStockRow  <- boughtStockStore.registerSold(timestamp, stock, quantity)
    } yield {
      require(boughtStockRow.pricePerUnitUSD.currency.equals(Data.USD))
      val soldAmountUSD = quantity * exRate.exchangeRate * stockPrice.price.value
      val boughtAmountUSD = quantity * boughtStockRow.pricePerUnitUSD.value
      val netAmount = Amount(soldAmountUSD - boughtAmountUSD, Data.USD)
      SoldStock(timestamp, stock, quantity, stockPrice.price, stockPriceUSD, netAmount, exRate)
    }
  }
}
