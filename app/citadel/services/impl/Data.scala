package citadel.services.impl

import java.text.SimpleDateFormat
import java.util.{TimeZone, Date}

import citadel.domain._

import scala.io.Source

object Data {
  val ABC = Stock("ABC")
  val DEF = Stock("DEF")
  val XYZ = Stock("XYZ")
  val USD = Currency("USD", "$")
  val GBP = Currency("GBP", "£")
  val EUR = Currency("EUR", "€")

  private val dateParser = {
    val parser = new SimpleDateFormat("dd-MMM-yy")
    parser.setTimeZone(TimeZone.getTimeZone("UTC"))
    parser
  }
  private val dataFileStream = this.getClass.getClassLoader.getResourceAsStream("data.txt")
  private val dataTSV = Source.fromInputStream(dataFileStream).getLines()
  val data = dataTSV.drop(1).map(dataLine => {
    val lineSplits = dataLine.split("\t")
    val date = dateParser.parse(lineSplits(0))
    val prices = List(
      StockPrice(ABC, parseAmount(lineSplits(1))),
      StockPrice(DEF, parseAmount(lineSplits(2))),
      StockPrice(XYZ, parseAmount(lineSplits(3)))
    )
    val exchanges = List(
      ExchangeRate(GBP, USD, lineSplits(4).toDouble),
      ExchangeRate(EUR, USD, lineSplits(5).toDouble)
    )
    DataRow(date, prices, exchanges)
  }).toList.sortBy(dataRow => dataRow.date.getTime)

  private def parseAmount(amountStr: String): Amount = {
    val amountValue = amountStr.tail.toDouble
    val currency = amountStr.head match {
      case '$' => USD
      case '£' => GBP
      case '€' => EUR
    }
    Amount(amountValue, currency)
  }
}

case class DataRow(date: Date, stockPrices: List[StockPrice], exchangeRates: List[ExchangeRate])
