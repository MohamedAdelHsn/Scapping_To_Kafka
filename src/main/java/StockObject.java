public class StockObject {

	
  private String stock_Name;
  private double last_Price;
  private double stock_Change_Percentage;
  private double stock_Change;
  private double volume;
  private double high_price;
  private double low_Price;
  private String time;
  private String timeStamp;
    
    
            
    StockObject(){}
    
    StockObject(String stock_Name ,double last_Price  
    		, double stock_Change_Percentage
    		, double stock_Change
    		, double volume
    		, double high_price 
    		, double low_Price
    		, String time
    		, String timeStamp
    		)
    {
    	
    	this.stock_Name = stock_Name;
    	this.last_Price = last_Price;
    	this.high_price = high_price;
    	this.low_Price = low_Price;
    	this.volume = volume;    	
    	this.stock_Change = stock_Change;
    	this.stock_Change_Percentage = stock_Change_Percentage;
    	this.time = time;
    	this.timeStamp = timeStamp;
    	
    	
    	
    }
    
	public String getStock_Name() {
		return stock_Name;
	}

	public void setStock_Name(String stock_Name) {
		this.stock_Name = stock_Name;
	}

	public double getLast_Price() {
		return last_Price;
	}

	public void setLast_Price(double last_Price) {
		this.last_Price = last_Price;
	}

	public double getHigh_price() {
		return high_price;
	}

	public void setHigh_price(double high_price) {
		this.high_price = high_price;
	}

	public double getLow_Price() {
		return low_Price;
	}

	public void setLow_Price(double low_Price) {
		this.low_Price = low_Price;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}


	public double getStock_Change() {
		return stock_Change;
	}

	public void setStock_Change(double stock_Change) {
		this.stock_Change = stock_Change;
	}

	public double getStock_Change_Percentage() {
		return stock_Change_Percentage;
	}

	public void setStock_Change_Percentage(double stock_Change_Percentage) {
		this.stock_Change_Percentage = stock_Change_Percentage;
	}
	
	public String getTimeStamp() {
		return timeStamp;
	}
	
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	public String getTime() {
		return time;
	}
	
	public void setTime(String time) {
		this.time = time;
	}

	
	
}
