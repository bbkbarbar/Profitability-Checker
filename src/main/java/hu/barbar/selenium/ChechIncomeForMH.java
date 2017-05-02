package hu.barbar.selenium;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import hu.barbar.util.FileHandler;

public class ChechIncomeForMH {

	public static final String LOCATION_OF_CHROME_DRIVER = "c:\\Tools\\chromedriver.exe";
	
	public static int DEFAULT_WAIT_TIME_IN_SEC = 10;
	
	public static final String fileNameBase = "C:\\tmp\\ETH";
	public static final String extension = ".dat";
	
	
	public static final SimpleDateFormat df = new SimpleDateFormat("YYYY.MM.dd.");
	public static final SimpleDateFormat tf = new SimpleDateFormat("HH:mm:ss");
	
    public static void main(String[] args) {
    	// Optional, if not specified, WebDriver will search your path for chromedriver.
    	System.setProperty("webdriver.chrome.driver", LOCATION_OF_CHROME_DRIVER);
    	
    	long waitTime = DEFAULT_WAIT_TIME_IN_SEC;
    	
    	if(args.length >= 1){
    		String param1 = args[0];
    		waitTime = Integer.valueOf(param1);
    	}
    	
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMdd");
    	
    	String fileName = fileNameBase + "_" + sdf.format(new Date()) + extension;
    	
    	while(true){
	    	String line = getDataLine();
	    	FileHandler.appendToFile(fileName, line);
	    	System.out.println(line);
	    	try {
				Thread.sleep(waitTime * 1000);
			} catch (InterruptedException e) {}
    	}
    	/**/
    }
    
    
    /**
     * 
     * @param currency e.g.: eth
     * @param hashingPower in MH
     * @param powerConsumption in kWh
     * @param energyCostInUSD as a float value
     * @return an integer value as a float with the estimated income for given parameters
     */
    public static float getIncomeValuePerMonthFor(String currency, float hashingPower, float powerConsumption, float energyCostInUSD){
    	/*
		Sample URL:
    		https://www.cryptocompare.com/mining/calculator/eth?HashingPower=1000&HashingUnit=MH%2Fs&PowerConsumption=550&CostPerkWh=1.56
    	 */
    	String urlBase = "https://www.cryptocompare.com/mining/calculator/";
    	String urlPart2 = "?HashingPower=";
    	String urlPart3 = "&HashingUnit=MH%2Fs&PowerConsumption=";
    	String urlPart4 = "&CostPerkWh=";
    	
    	String composedUrl = urlBase + currency.toLowerCase() + urlPart2 + hashingPower + urlPart3 + powerConsumption + urlPart4 + energyCostInUSD;
    	
    	ArrayList<String> lines = getWebContentBodyFrom(composedUrl);
    	
    	String priceStr = "";
    	for(int i=0; i<lines.size(); i++){
    		if(lines.get(i).contains("Profit per month")){
    			priceStr = lines.get(i+1);
    			//System.out.println(lines.get(i) + ": |" + lines.get(i+1) + "|");
    		}
    	}
    	
    	priceStr = (priceStr.split(" ")[1]);
    	priceStr = (priceStr.substring(0,priceStr.indexOf('.'))).replaceAll(",", "");
    	float income = Float.valueOf(priceStr);
    	
    	return income;
    }
    
    
    /**
     * Get data from web with request for calculation with hashingPower of 1000 MH and 0 power consumption  <br>
     * and divide the given value with 1000 to get accurate income for 1 MH without any power cost.
     * @return the date of enquiry and the estimated income for 1 MH hashingPower without any power consumption
     */
    public static String getDataLine(){
    	return (getCurrentDateStr() + "\t" + Float.toString( getIncomeValuePerMonthFor("eth", 1f, 0, 0.15f) ));
    }
    
    
    public static ArrayList<String> getWebContentBodyFrom(String url){
    	System.setProperty("webdriver.chrome.driver", "c:\\Tools\\chromedriver.exe");
    	ArrayList<String> lines = null;
    	
    	try {
	    	
    		
	    	WebDriver driver = new ChromeDriver();
	    	driver.get(url);
    	
			//Thread.sleep(50);
		
	    	WebElement resultbox = driver.findElement(By.tagName("body"));
	    	
	    	String str = resultbox.getText();
	    	String[] array = str.split("\n");
	    	lines = new ArrayList<String>(Arrays.asList(array));
	    	driver.quit();
	    	
    	} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return lines;
    }
    
    public static String getCurrentDateStr(){
    	Date now = new Date();
    	//return df.format(now) + " " + tf.format(now);
    	return tf.format(now);
    }
    
    
}