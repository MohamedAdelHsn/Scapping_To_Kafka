
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringJoiner;
import org.apache.kafka.common.serialization.StringSerializer;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

public class Stock_Producer {

	public static void main(String [] args ) 
	{
		
		// Initialize kafka properties to prepare producer		
		Properties props = new Properties();
		props.put(ProducerConfig.CLIENT_ID_CONFIG, "my-partition-examples");
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		
		
		KafkaProducer<String,StockObject> producer = new KafkaProducer(props);				
       // produce selenium messages into kafka topics and serialize it to json object 
		
		while(true) {
		
	    Iterator i = crawlStockTable().iterator();
    	System.out.println("records iterators ");

	    while(i.hasNext())
	    {
	    	
	    	ProducerRecord<String, StockObject> record = 
	    			new ProducerRecord<String, StockObject>("my_stock_topic" , (StockObject) i.next());
	    	
	    	producer.send(record);
	    	  try {
	   			Thread.sleep(500);
	   		} catch (InterruptedException e) {
	   			// TODO Auto-generated catch block
	   			e.printStackTrace();
	   		}
	  	    
	  		}
	
	    	

	    try {
 			Thread.sleep(1000);
 		} catch (InterruptedException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
	    
	}

	    
	}
		
		 
	
	
	
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
	
	
}
