# Scraping_To_Kafka_Demo

In this demo we will scraping data from [Egypt_stock_website](https://www.investing.com/equities/egypt) 
using Jsoup (Note :  you can use Selenium but it is slower than JSoup because it waits for ajax script and renders) <br />
then serialize data into json objects to send it to apache kafka topic

It is a snapshot of data in the website <br />

 ![2021-05-04 (2)](https://user-images.githubusercontent.com/58120325/116986630-4b35f580-acce-11eb-941c-eeac219d2d6c.png) <br /> 


It is the output data after transformation
 ```json
 {"stock_Name":"جلوبال تيلكوم","last_Price":4.99,"stock_Change_Percentage":"20.15%","stock_Change":0.8,"volume":49608.96,"quantity":10409.0,"open_Price":3.97,"high_price":5.0,"low_Price":2.5,"timeStamp":"2021-05-04 09:59:04.666"}
{"stock_Name":"سوهاج الوطنية","last_Price":3.99,"stock_Change_Percentage":"15.20%","stock_Change":0.5,"volume":80462.0,"quantity":21210.0,"open_Price":3.29,"high_price":4.0,"low_Price":3.72,"timeStamp":"2021-05-04 09:59:05.048"}
{"stock_Name":"الاسكندرية الوطنية ل","last_Price":7.74,"stock_Change_Percentage":"10.82%","stock_Change":0.69,"volume":11488.71,"quantity":1650.0,"open_Price":7.07,"high_price":7.74,"low_Price":6.58,"timeStamp":"2021-05-04 09:59:06.013"}
{"stock_Name":"مينا فارم للأدوية","last_Price":120.12,"stock_Change_Percentage":"9.96%","stock_Change":10.88,"volume":3124721.5,"quantity":26023.0,"open_Price":109.2,"high_price":120.12,"low_Price":119.5,"timeStamp":"2021-05-04 09:59:06.799"}
{"stock_Name":"الملتقى العربي للاست","last_Price":41.44,"stock_Change_Percentage":"8.81%","stock_Change":3.32,"volume":8534771.0,"quantity":208175.0,"open_Price":37.68,"high_price":41.44,"low_Price":39.5,"timeStamp":"2021-05-04 09:59:07.481"}
{"stock_Name":"مستشفى النزهه الدولي","last_Price":13.8,"stock_Change_Percentage":"8.64%","stock_Change":1.11,"volume":3317739.5,"quantity":237689.0,"open_Price":12.85,"high_price":14.13,"low_Price":13.5,"timeStamp":"2021-05-04 09:59:08.27"}
{"stock_Name":"ريماس","last_Price":7.84,"stock_Change_Percentage":"8.42%","stock_Change":0.6,"volume":1.1057083E7,"quantity":1431297.0,"open_Price":7.13,"high_price":7.84,"low_Price":7.48,"timeStamp":"2021-05-04 09:59:08.911"}

 ```
 
 lets go to extract data from stock_website
  ```java
  
 	private static ArrayList<StockObject> crawlStockTable() {

		final String url = "https://www.investing.com/equities/egypt";

		ArrayList<StockObject> objects = new ArrayList<StockObject>();

		try {

			final Document document = Jsoup.connect(url).timeout(50 * 1000).get();

			for (Element row : document.select("#cross_rate_markets_stocks_1 > tbody:nth-child(2) tr")) {

				final String Stock_name = row.select("td:nth-child(2) > a:nth-child(1)").text();

				final double last_price = Double.parseDouble(row.select("td:nth-child(3)").text());

				final double high_price = Double.parseDouble(row.select("td:nth-child(4)").text());

				final double low_price = Double.parseDouble(row.select("td:nth-child(5)").text());

				final double stock_Change = Double.parseDouble(row.select("td:nth-child(6)").text());

				final double stock_change_perc = Double
						.parseDouble(row.select("td:nth-child(7)").text().replaceAll("%", ""));

				final double vol = Utility.currencyMatcher(row.select("td:nth-child(8)").text());

				final String time = row.select("td:nth-child(9)").text();

				final String timeStamp = new Timestamp(System.currentTimeMillis()).toString();

				StockObject myStock = new StockObject(Stock_name, last_price, stock_change_perc, stock_Change, vol,
						high_price, low_price, time, timeStamp);

				objects.add(myStock);

			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return objects;

	}	
  
  ```
  
 producing data to kafka topic with scheduling task manually using java
 
 ```java
 
 Timer timer = new Timer();

		TimerTask mytask1 = new TimerTask() {

			@Override
			public void run() {

				Properties props = new Properties();
				props.put(ProducerConfig.CLIENT_ID_CONFIG, "my-partition-examples");
				props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
				props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
				props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

				KafkaProducer<String, StockObject> producer = new KafkaProducer(props);
				Iterator i = crawlStockTable().iterator();

				while (i.hasNext()) {

					ProducerRecord<String, StockObject> record = new ProducerRecord<String, StockObject>(
							"my_stock_topic", (StockObject) i.next());

					producer.send(record);

					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}
		};

		timer.schedule(mytask1, 0, 15 * 1000);
		mytask1.run();
     
   
  
 ``` 


final outputs are as the same stored in filesystem as json format

```json

{"stock_Name":"Hermes Holding Co","last_Price":14.2,"stock_Change_Percentage":0.0,"stock_Change":0.0,"volume":0.0,"high_price":14.35,"low_Price":14.06,"time":"07:29:43","timeStamp":"2021-05-05 16:07:03.99"}
{"stock_Name":"Hermes Holding Co","last_Price":14.2,"stock_Change_Percentage":0.0,"stock_Change":0.0,"volume":0.0,"high_price":14.35,"low_Price":14.06,"time":"07:29:43","timeStamp":"2021-05-05 16:07:03.925"}
{"stock_Name":"SODIC","last_Price":16.6,"stock_Change_Percentage":0.0,"stock_Change":0.0,"volume":0.0,"high_price":17.0,"low_Price":16.57,"time":"07:29:25","timeStamp":"2021-05-05 16:07:03.996"}
{"stock_Name":"SODIC","last_Price":16.6,"stock_Change_Percentage":0.0,"stock_Change":0.0,"volume":0.0,"high_price":17.0,"low_Price":16.57,"time":"07:29:25","timeStamp":"2021-05-05 16:07:03.926"}
{"stock_Name":"Palm Hills Develop","last_Price":1.589,"stock_Change_Percentage":0.0,"stock_Change":0.0,"volume":0.0,"high_price":1.6,"low_Price":1.572,"time":"07:29:58","timeStamp":"2021-05-05 16:07:03.997"}
{"stock_Name":"Palm Hills Develop","last_Price":1.589,"stock_Change_Percentage":0.0,"stock_Change":0.0,"volume":0.0,"high_price":1.6,"low_Price":1.572,"time":"07:29:58","timeStamp":"2021-05-05 16:07:03.927"}
{"stock_Name":"Pioneers Holding","last_Price":4.1,"stock_Change_Percentage":0.0,"stock_Change":0.0,"volume":0.0,"high_price":4.11,"low_Price":3.9,"time":"07:29:41","timeStamp":"2021-05-05 16:07:03.997"}
{"stock_Name":"Pioneers Holding","last_Price":4.1,"stock_Change_Percentage":0.0,"stock_Change":0.0,"volume":0.0,"high_price":4.11,"low_Price":3.9,"time":"07:29:41","timeStamp":"2021-05-05 16:07:03.928"}
{"stock_Name":"Sidi Kerir","last_Price":10.32,"stock_Change_Percentage":0.0,"stock_Change":0.0,"volume":0.0,"high_price":10.45,"low_Price":10.1,"time":"07:26:57","timeStamp":"2021-05-05 16:07:04.007"}
{"stock_Name":"Sidi Kerir","last_Price":10.32,"stock_Change_Percentage":0.0,"stock_Change":0.0,"volume":0.0,"high_price":10.45,"low_Price":10.1,"time":"07:26:57","timeStamp":"2021-05-05 16:07:03.938"}
{"stock_Name":"El Sewedy Electric","last_Price":8.17,"stock_Change_Percentage":0.0,"stock_Change":0.0,"volume":0.0,"high_price":8.25,"low_Price":8.06,"time":"07:29:07","timeStamp":"2021-05-05 16:07:04.008"}
{"stock_Name":"El Sewedy Electric","last_Price":8.17,"stock_Change_Percentage":0.0,"stock_Change":0.0,"volume":0.0,"high_price":8.25,"low_Price":8.06,"time":"07:29:07","timeStamp":"2021-05-05 16:07:03.939"}
{"stock_Name":"T M G Holding","last_Price":5.72,"stock_Change_Percentage":0.0,"stock_Change":0.0,"volume":0.0,"high_price":5.72,"low_Price":5.65,"time":"07:29:52","timeStamp":"2021-05-05 16:07:04.008"}
{"stock_Name":"T M G Holding","last_Price":5.72,"stock_Change_Percentage":0.0,"stock_Change":0.0,"volume":0.0,"high_price":5.72,"low_Price":5.65,"time":"07:29:52","timeStamp":"2021-05-05 16:07:03.94"}
{"stock_Name":"GB AUTO","last_Price":3.28,"stock_Change_Percentage":0.0,"stock_Change":0.0,"volume":0.0,"high_price":3.4,"low_Price":3.27,"time":"07:29:53","timeStamp":"2021-05-05 16:07:04.009"}
{"stock_Name":"GBAUTO","last_Price":3.28,"stock_Change_Percentage":0.0,"stock_Change":0.0,"volume":0.0,"high_price":3.4,"low_Price":3.27,"time":"07:29:53","timeStamp":"2021-05-05 16:07:03.941"}

```

What are maven repos you need to do this demo ? 

```xml

  <dependencies>
        <dependency>
            <groupId>org.apache.kafka</groupId>
            <artifactId>kafka-clients</artifactId>
            <version>2.4.0</version>
        </dependency>
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-slf4j-impl</artifactId>
            <version>2.11.0</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.11.0</version>
            
           </dependency>
            <!-- https://mvnrepository.com/artifact/org.jsoup/jsoup -->
     <dependency>
       <groupId>org.jsoup</groupId>
       <artifactId>jsoup</artifactId>
       <version>1.13.1</version>
     </dependency>
     
 </dependencies>


```


 


  

