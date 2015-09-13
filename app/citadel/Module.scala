package citadel

import citadel.services.impl._
import citadel.services.{ExchangeService, BuySellService, StockService}

trait Module {
  def stockService: StockService
  def buySellService: BuySellService
  def exchangeService: ExchangeService
}

object DefaultModule extends Module {

  private lazy val boughtStockStore: BoughtStockStore = new InMemoryBoughtStockStore()

  override lazy val stockService: StockService = new DataBasedStockService()

  override lazy val exchangeService: ExchangeService = new DataBasedExchangeService()

  override lazy val buySellService: BuySellService = new BuySellServiceImpl(stockService, exchangeService, boughtStockStore)
}
