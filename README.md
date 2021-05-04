# Scapping_To_Kafka

In this app we will scapping data from [Egypt_stock_website : mubasher](https://www.mubasher.info/countries/eg/stock-prices) 
using selenium webdriver (Note : Selenium is slower than JSoup because it waits for ajax script and js ..) <br />
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
 
 lets go to extract data from mubasher
  ```java
  public static ArrayList<StockObject> crawlStockTable() 
	{
		
		ArrayList<StockObject> arrayOfStocks = new ArrayList<StockObject>();
		
		System.setProperty("webdriver.gecko.driver" ,"/home/hdpadmin/eclipse-workspace/geckodriver");
		WebDriver wdriver = new FirefoxDriver(); 		
		wdriver.manage().window().maximize();
				
		// url 
		wdriver.get("https://www.mubasher.info/countries/eg/stock-prices");
	
		// good it works well 
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		int rows_size = wdriver.findElements(By.xpath("//*[@class='mi-table']/tbody/tr")).size();
		System.out.println("Rows size = "+rows_size);
		
		int cols_size = wdriver.findElements(By.xpath("//*[@class='mi-table']/thead/tr/th")).size();
		System.out.println("Cols size = "+cols_size);
		
		for(int r = 1; r<= rows_size; r++) 
		{
		
			StringJoiner joiner = new StringJoiner(" , ");
			
			for(int col= 1 ; col<= cols_size; col++) 
			{
										
				joiner.add(wdriver
						.findElement(By.xpath("//*[@class='mi-table']/tbody/tr["+r+"]/td["+col+"]"))
						.getText());
				
				/*
				System.out.print(wdriver
						.findElement(By.xpath("//*[@class='mi-table']/tbody/tr["+r+"]/td["+col+"]"))
						.getText() +" ,");
				*/
				
			}
		
			
	         String [] splitter = joiner.toString().split(" , ");
	         StockObject myStock = new StockObject();		 
	         myStock.setStock_Name(splitter[0]);		 
	         myStock.setLast_Price(Double.parseDouble(splitter[1]));
		 myStock.setStock_Change_Percentage(splitter[2]);
		 myStock.setStock_Change(Double.parseDouble(splitter[3].replace("`", "")));
		 myStock.setVolume(Double.parseDouble(splitter[4].replaceAll(",", "")));
		 myStock.setQuantity(Double.parseDouble(splitter[5].replaceAll(",", "")));
		 myStock.setOpen_Price(Double.parseDouble(splitter[6]));
		 myStock.setHigh_price(Double.parseDouble(splitter[7]));
		 myStock.setLow_Price(Double.parseDouble(splitter[8]));
                 myStock.setTimeStamp(new Timestamp(System.currentTimeMillis()).toString());
		 
                 arrayOfStocks.add(myStock);
	
			
		}
		
		
		return arrayOfStocks;

		
	}
  
  ```
  
 casting data from kafka json format into datafame (Structure data)
 
 ```scala
 
     
    val spark = SparkSession.builder()
    .appName("Near-Realtime_Stock_Tracking-App")
    .master("local[2]")
    .getOrCreate()
    
    
    import spark.implicits._
   
    val df = spark.readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", "localhost:9092")
    .option("subscribe", "my_stock_topic")
    .option("startingOffsets", "earliest")
    .load()
    
    
   // val df_Jsoncast = df.selectExpr("CAST(value AS STRING)")
    
    
    // custom schema for stock    
    val mySchema = new StructType()
    .add("stock_Name" , StringType)
    .add("last_Price" , DoubleType)
    .add("stock_Change_Percentage" , StringType)
    .add("stock_Change" , DoubleType)
    .add("volume" , DoubleType)
    .add("quantity" , DoubleType)
    .add("open_Price" , DoubleType)
    .add("high_price" , DoubleType)
    .add("low_Price" , DoubleType)
    .add("timeStamp" ,  TimestampType)
    

    
   val stock_df = df.selectExpr("CAST(value AS STRING) as json")
                    .select( from_json($"json",mySchema).as("data"))
                    .select("data.*")
 
 
  
 ``` 
Then apache spark with get Avg for every window for tracking high_price stock for each stock_name between intraval time
 


  

