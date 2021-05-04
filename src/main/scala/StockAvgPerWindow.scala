
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{StructType , StringType ,  DoubleType ,  TimestampType}
import org.apache.spark.sql.functions._

object StockWinowStream {
  
  def main(args :Array[String]):Unit =
  {
    
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
                    
   //stock_df.printSchema()
   
   val tumblingWindowAggregations = stock_df
     .withWatermark("timeStamp", "10 minutes")
      .groupBy(
        window(col("timeStamp"), "20 seconds"),
        col("stock_Name")
      )
      .agg(avg(col("high_price")))
      
    /*
    val stock_df = df.select(from_json($"value".cast(StringType),mySchema))
    stock_df.printSchema()
   
    */
    
   
    // spark.conf.set("spark.sql.streaming.checkpointLocation", "/target/chkpoint_dir")             
     tumblingWindowAggregations.writeStream
      .format("json")
      .option("truncate", "false")
      .outputMode("append")
      .option("checkpointLocation", s"chkpoint/${System.currentTimeMillis()}")
      .start("src/main/resources/")
      .awaitTermination()
      
          
  }
   
 
}
