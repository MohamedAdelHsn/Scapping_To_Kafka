
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


public class Stock_Producer {

	public static void main(String[] args) {
		

		
		// scheduling task manual using java to produce data to kafka evey n time		
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
		
		

	}
	
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
		
		
		
	}


