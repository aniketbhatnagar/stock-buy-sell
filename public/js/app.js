$( document ).ready(function() {

   function td(column) {
       return "<td>" + column + "</td>";
   }

   function tr(columns) {
       var trLine = "<tr>";
       for (i in columns) {
           var column = columns[i];
           trLine += td(column);
       }
       trLine += "</tr>";
       return trLine;
   }

   function dateToTimestamp(date) {
      var dateSplits = date.split("-")
      var numbers = [];
      for (i in dateSplits) {
          var split = dateSplits[i];
          numbers.push(parseInt(split))
      }
      numbers[1]--;
      var timestamp = moment.utc(numbers).valueOf();
      return timestamp;
   }

   $('.input-group.date').datepicker({
     format: "yyyy-mm-dd"
   });

   $("#buyForm").submit(function() {
        var stockId = $("#buyStockId").val();
        var quantity = $("#buyStockQuantity").val();
        var date = $("#buyDate").val();
        var timestamp = dateToTimestamp(date);
        var buyRequest = {
                         	"timestamp": timestamp,
                         	"stockId": stockId,
                         	"quantity": quantity
                         }
        $.ajax({
          type: "POST",
          url: "/services/buy",
          data: JSON.stringify(buyRequest),
          dataType: "json",
          contentType: "application/json"
        }).then(function(response) {
          var serviceResponse = response;
          if (serviceResponse.status.code != 200) {
            alert("Service error - " + serviceResponse.payload)
          } else {
            var boughtStock = serviceResponse.payload;
            var pricePerUnit = boughtStock.pricePerUnit.value;
            var currency = boughtStock.pricePerUnit.currency;
            var totalPriceUSD = boughtStock.pricePerUnitUSD.value * quantity;
            var columns = [ stockId,
                            "Buy",
                            quantity,
                            date,
                            currency.currencyId,
                            currency.symbol + "" + pricePerUnit,
                            "$" + totalPriceUSD,
                            boughtStock.exchangeRate.exchangeRate,
                            ""];
            $("#log").append(tr(columns));
          }
        });
        return false;
   });

   $("#sellForm").submit(function() {
       var stockId = $("#sellStockId").val();
       var quantity = $("#sellStockQuantity").val();
       var date = $("#sellDate").val();
       var timestamp = dateToTimestamp(date);
       var sellRequest = {
                            "timestamp": timestamp,
                            "stockId": stockId,
                            "quantity": quantity
                        }
       $.ajax({
         type: "POST",
         url: "/services/sell",
         data: JSON.stringify(sellRequest),
         dataType: "json",
         contentType: "application/json"
       }).then(function(response) {
         var serviceResponse = response;
         if (serviceResponse.status.code != 200) {
           alert("Service error - " + serviceResponse.payload)
         } else {
           var soldStock = serviceResponse.payload;
           var pricePerUnit = soldStock.pricePerUnit.value;
           var currency = soldStock.pricePerUnit.currency;
           var totalPriceUSD = soldStock.pricePerUnitUSD.value * quantity;
           var columns = [ stockId,
                           "Sell",
                           quantity,
                           date,
                           currency.currencyId,
                           currency.symbol + "" + pricePerUnit,
                           "$" + totalPriceUSD,
                           soldStock.exchangeRate.exchangeRate,
                           soldStock.net.currency.symbol + "" + soldStock.net.value];
           $("#log").append(tr(columns));
         }
       });
       return false;
  });
});