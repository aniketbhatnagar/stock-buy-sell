package citadel.controllers

import citadel.DefaultModule
import citadel.domain.{SellRequest, BuyRequest}
import citadel.services.BuySellService
import play.api.mvc.Action

trait BuySellController extends BaseController {

  import scala.concurrent.ExecutionContext.Implicits.global

  def buySellService: BuySellService

  def buyStock = Action.async(jackson(classOf[BuyRequest])) {
    request => {
      val boughtStockFuture = buySellService.buyStock(request.body)
      buildResult(boughtStockFuture)
    }
  }

  def sellStock = Action.async(jackson(classOf[SellRequest])) {
    request => {
      val soldStockFuture = buySellService.sellStock(request.body)
      buildResult(soldStockFuture)
    }
  }
}

object BuySellController extends BuySellController {
  override val buySellService: BuySellService = DefaultModule.buySellService
}
