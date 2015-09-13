package citadel.services.impl

import java.util.concurrent.ConcurrentHashMap

import citadel.domain._

import scala.concurrent.{Future, ExecutionContext, blocking}

/**
 * In memory implementation of bought stock store.
 */
class InMemoryBoughtStockStore(implicit executionContext: ExecutionContext = ExecutionContext.Implicits.global)
  extends BoughtStockStore {

  private val boughtStore = new ConcurrentHashMap[Stock, BoughtStockRow]()

  /**
   * Registers that a specific quantity of stock was bought at a particular price.
   */
  override def registerBought(boughtStock: BoughtStock): Future[Unit] = {
    Future {
      blocking {
        val stock = boughtStock.stock
        stock.id.synchronized {
          if (boughtStore.containsKey(stock)) {
            val boughtStockRow = boughtStore.get(stock)
            val updatedRow = add(boughtStockRow, BoughtStockRow(boughtStock.quantity, boughtStock.pricePerUnitUSD))
            boughtStore.put(stock, updatedRow)
          } else {
            boughtStore.put(stock, BoughtStockRow(boughtStock.quantity, boughtStock.pricePerUnitUSD))
          }
        }
      }
    }
  }

  /**
   * Looks up bought stock.
   * @param stock stock to lookup.
   *              Future containing quantity of stock bought and its price at which it was bought.
   */
  override def lookupBought(stock: Stock): Future[BoughtStockRow] = Future {
    blocking {
      stock.id.synchronized {
        if (boughtStore.containsKey(stock)) {
          boughtStore.get(stock)
        } else {
          throw new IllegalArgumentException(s"Stock not bought: ${stock.id}")
        }
      }
    }
  }

  /**
   * Registers that a specific quantity of stock was sold at a particular price.
   * An registration for stock sold at higher quantity than it was bought earlier will result in error in the future.
   */
  override def registerSold(timestamp: Long, stock: Stock, quantity: Int): Future[BoughtStockRow] = {
    Future {
      blocking {
        stock.id.synchronized {
          if (boughtStore.containsKey(stock)) {
            val boughtStockRow = boughtStore.get(stock)
            if (boughtStockRow.quantity >= quantity) {
              val updatedRow = boughtStockRow.copy(quantity = boughtStockRow.quantity - quantity)
              boughtStore.put(stock, updatedRow)
              updatedRow
            } else {
              throw new IllegalArgumentException(s"Not enough quantity for : ${stock.id}. Have - ${boughtStockRow.quantity}. Requested - $quantity")
            }
          } else {
            throw new IllegalArgumentException(s"Stock not bought: ${stock.id}")
          }
        }
      }
    }
  }

  private def add(row1: BoughtStockRow, row2: BoughtStockRow): BoughtStockRow = {
    val quantity1 = row1.quantity
    val quantity2 = row2.quantity
    val price1 = row1.pricePerUnitUSD.value
    val price2 = row2.pricePerUnitUSD.value
    val newQuantity = quantity1 + quantity2
    val newPrice = (price1 * quantity1 + price2 * quantity2) / newQuantity
    BoughtStockRow(newQuantity, row1.pricePerUnitUSD.copy(value = newPrice))
  }
}
