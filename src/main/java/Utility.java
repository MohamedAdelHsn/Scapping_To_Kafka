
public class Utility {

	
	public static double currencyMatcher(String amount) 
	{
		double result = 0.00; 
		
	    if(amount.contains("K")) 
	    {
	    	result = Double.parseDouble(amount.replace("K", "")) * 1000.000;
	    	
	    }
	    else if (amount.contains("M")) {
	      
	    	result = Double.parseDouble(amount.replace("M", "")) * 1000000.000;
	    	
	    }
	    else 
	    {
	    	result = Double.parseDouble(amount);
	    	
	    }

		
		return result;
		
	}
	
	
	
}
	
