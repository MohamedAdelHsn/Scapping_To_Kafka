# Scapping_To_Kafka

In this app we will scapping data from [Egypt_stock_website : mubasher](https://www.mubasher.info/countries/eg/stock-prices) 
using selenium webdriver (Note : Selenium is slower than JSoup because it waits for ajax script and js ..) <br />
then serialize data into json objects to send it to apache kafka topic

It is a snapshot of data in the website  <br />
![2021-05-04 (2)](https://user-images.githubusercontent.com/58120325/116986630-4b35f580-acce-11eb-941c-eeac219d2d6c.png) <br /> 


it is the output data after transformation
 ```json
 {"stock_Name":"جلوبال تيلكوم","last_Price":4.99,"stock_Change_Percentage":"20.15%","stock_Change":0.8,"volume":49608.96,"quantity":10409.0,"open_Price":3.97,"high_price":5.0,"low_Price":2.5,"timeStamp":"2021-05-04 09:59:04.666"}
{"stock_Name":"سوهاج الوطنية","last_Price":3.99,"stock_Change_Percentage":"15.20%","stock_Change":0.5,"volume":80462.0,"quantity":21210.0,"open_Price":3.29,"high_price":4.0,"low_Price":3.72,"timeStamp":"2021-05-04 09:59:05.048"}
{"stock_Name":"الاسكندرية الوطنية ل","last_Price":7.74,"stock_Change_Percentage":"10.82%","stock_Change":0.69,"volume":11488.71,"quantity":1650.0,"open_Price":7.07,"high_price":7.74,"low_Price":6.58,"timeStamp":"2021-05-04 09:59:06.013"}
{"stock_Name":"مينا فارم للأدوية","last_Price":120.12,"stock_Change_Percentage":"9.96%","stock_Change":10.88,"volume":3124721.5,"quantity":26023.0,"open_Price":109.2,"high_price":120.12,"low_Price":119.5,"timeStamp":"2021-05-04 09:59:06.799"}
{"stock_Name":"الملتقى العربي للاست","last_Price":41.44,"stock_Change_Percentage":"8.81%","stock_Change":3.32,"volume":8534771.0,"quantity":208175.0,"open_Price":37.68,"high_price":41.44,"low_Price":39.5,"timeStamp":"2021-05-04 09:59:07.481"}
{"stock_Name":"مستشفى النزهه الدولي","last_Price":13.8,"stock_Change_Percentage":"8.64%","stock_Change":1.11,"volume":3317739.5,"quantity":237689.0,"open_Price":12.85,"high_price":14.13,"low_Price":13.5,"timeStamp":"2021-05-04 09:59:08.27"}
{"stock_Name":"ريماس","last_Price":7.84,"stock_Change_Percentage":"8.42%","stock_Change":0.6,"volume":1.1057083E7,"quantity":1431297.0,"open_Price":7.13,"high_price":7.84,"low_Price":7.48,"timeStamp":"2021-05-04 09:59:08.911"}

 ```

