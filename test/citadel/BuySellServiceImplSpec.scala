package citadel

import citadel.domain._
import citadel.services.{StockService, ExchangeService, BuySellService}
import citadel.services.impl._
import org.scalatest.{Matchers, FlatSpec}

import scala.concurrent.duration.Duration
import scala.concurrent.Await

class BuySellServiceImplSpec extends FlatSpec with Matchers {
  import scala.concurrent.ExecutionContext.Implicits.global

  def buySellService(stockService: StockService = new DataBasedStockService(),
                     exchangeService: ExchangeService = new DataBasedExchangeService(),
                     boughtStockStore: BoughtStockStore = new InMemoryBoughtStockStore()) = {
    new BuySellServiceImpl(stockService, exchangeService, boughtStockStore)
  }

  it should "calculate profit while selling USD stock correctly" in {
    val (bought, sold) = buyAndSell("ABC", 100, 1438387200000L, 1438732800000L)
    bought.pricePerUnitUSD.currency should be (Data.USD)
    bought.pricePerUnitUSD.value should be (10D +- 0.0001D)
    bought.pricePerUnit.currency should be (Data.USD)
    bought.pricePerUnit.value should be (10D +- 0.0001D)
    bought.exchangeRate.srcCurrency should be (Data.USD)
    bought.exchangeRate.dstCurrency should be (Data.USD)
    bought.exchangeRate.exchangeRate should be (1D +- 0.0001D)
    sold.net.currency should be (Data.USD)
    sold.net.value should be (200D +- 0.0001D)
  }

  it should "calculate profit while selling GBP stock correctly" in {
    val (bought, sold) = buyAndSell("DEF", 75, 1438819200000L, 1439596800000L)
    bought.pricePerUnitUSD.currency should be (Data.USD)
    bought.pricePerUnitUSD.value should be (148.2D +- 0.0001D)
    bought.pricePerUnit.currency should be (Data.GBP)
    bought.pricePerUnit.value should be (95D +- 0.0001D)
    bought.exchangeRate.srcCurrency should be (Data.GBP)
    bought.exchangeRate.dstCurrency should be (Data.USD)
    bought.exchangeRate.exchangeRate should be (1.56D +- 0.0001D)
    sold.net.currency should be (Data.USD)
    sold.net.value should be (-1117.5D +- 0.0001D)
  }

  it should "calculate profit while selling EUR stock correctly" in {
    val (bought, sold) = buyAndSell("XYZ", 100, 1440028800000L, 1440460800000L)
    bought.pricePerUnitUSD.currency should be (Data.USD)
    bought.pricePerUnitUSD.value should be (102.08D +- 0.0001D)
    bought.pricePerUnit.currency should be (Data.EUR)
    bought.pricePerUnit.value should be (88D +- 0.0001D)
    bought.exchangeRate.srcCurrency should be (Data.EUR)
    bought.exchangeRate.dstCurrency should be (Data.USD)
    bought.exchangeRate.exchangeRate should be (1.16D +- 0.0001D)
    sold.net.currency should be (Data.USD)
    sold.net.value should be (1258D +- 0.0001D)
  }

  private def buyAndSell(stockId: String, quantity: Int, buyTimestamp: Long,
                         sellTimestamp: Long, service: BuySellService = buySellService()): (BoughtStock, SoldStock) = {
    val future = for {
      boughtStock <- service.buyStock(buyTimestamp, stockId, quantity)
      soldStock   <- service.sellStock(sellTimestamp, stockId, quantity)
    } yield {
      (boughtStock, soldStock)
    }
    Await.result(future, Duration.Inf)
  }
}
