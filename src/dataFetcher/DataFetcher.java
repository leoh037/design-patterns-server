package dataFetcher;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public abstract class DataFetcher {

//	public static void main(String[] args) {
//		String httpRequestDomain = "http://api.worldbank.org/v2";
//		System.out.println("Executing main...\n");
//		DataFetcher fetcher1 = DataFetcher.getInstance("CAN", "SH.MED.BEDS.ZS", 2000, 2010, httpRequestDomain);
//		System.out.print(fetcher1.getDataAsString());
//	}

	protected String countryId;
	protected String indicator;
	protected String startYear;
	protected String endYear;
	protected String urlString;
	protected String category;
	protected String countryName;
	protected String[][] instanceData;
	
	protected static HashMap<String, DataFetcher> instances = new HashMap<String, DataFetcher>();
	
	protected DataFetcher(String countryId, String indicator, int startYear, int endYear) {
		this.countryId = countryId;
		this.indicator = indicator;
		setStartYear(startYear);
		setEndYear(endYear);
	}
	
//	public static DataFetcher getInstance(String countryId, String indicator, int startYear, int endYear, String httpRequestDomain) {
//		if(startYear >= endYear) {
//			System.out.println("The start year must be strictly smaller than the end year");
//			return null;
//		}
//		String key = countryId + indicator;
//		DataFetcher instance = instances.get(key);
//		if(instance == null) {
//			instance = new DataFetcher(countryId, indicator, startYear, endYear, httpRequestDomain);
//			instances.put(key, instance);
//		}
//		else {
//			int instanceStartYear = Integer.parseInt(instance.startYear);
//			int instanceEndYear = Integer.parseInt(instance.endYear);
//			if(instanceStartYear != startYear && instanceEndYear != endYear){
//				instance.setStartYear(startYear);
//				instance.setEndYear(endYear);
//				instance.setURL(httpRequestDomain);
//			}
//			else if(instanceStartYear != startYear) {
//				instance.setStartYear(startYear);
//				instance.setURL(httpRequestDomain);
//			}
//			else if(instanceEndYear != endYear) {
//				instance.setEndYear(endYear);
//				instance.setURL(httpRequestDomain);
//			}
//		}
//		instance.fetchData();
//		return instance;
//	}

	public String[][] getData() {
		return instanceData;
	}
	
	public void printData() {
		int size = instanceData.length;

		System.out.println("For the country: \"" + getCountryName() + "\" and the category: \""
				+ getCategory() + "\", we have the following:\n");

		for (int i = 0; i < size; i++) {
			System.out.println("For the year " + instanceData[i][0] + " the value is " + instanceData[i][1]);
		}	
	}
	
//	public String getDataAsString() {
//		int size = this.instanceData.length;
//		String dataString = "";
//		
//		dataString = dataString + "For the country: \"" + getCountryName() + "\" and the category: \""
//				+ getCategory() + "\", we have the following:";
//
//		for (int i = 0; i < size; i++) {
//			dataString = dataString + "\nFor the year " + instanceData[i][0] + " the value is " + instanceData[i][1];
//		}	
//		return dataString;
//	}

	public Number[] getValueArray() {
		String[][] dataArray = getData();
		int arraySize = dataArray.length;
		Number[] array = new Number[arraySize];

		for (int i = 0; i < array.length; i++) {
			array[i] = Float.parseFloat(dataArray[i][1]);
		}
		return array;
	}

//	public Number[] getYearArray() {
//		String[][] dataArray = getData();
//		int arraySize = dataArray.length;
//		String[] array = new Number[arraySize];
//
//		for (int i = 0; i < array.length; i++) {
//			array[i] = dataArray[i][0];
//		}
//		return array;
//	}

	protected void setCategory(String category) {
		this.category = category.substring(1, category.length() - 1);
	}

	public String getCategory() {
		return this.category;
	}

	protected void setCountryName(String countryName) {
		this.countryName = countryName.substring(1, countryName.length() - 1);
	}

	public String getCountryName() {
		return this.countryName;
	}
	
	protected void setStartYear(int startYear) {
		this.startYear = String.valueOf(startYear);
	}
	
	protected void setEndYear(int endYear) {
		this.endYear = String.valueOf(endYear);
	}
	
	
	protected abstract void setURL(String httpRequestDomain);
	
//	private void setURL(String httpRequestDomain) {
//		this.urlString = String.format(httpRequestDomain + "/country/%s/indicator/%s?date=%s:%s&format=json",
//				this.countryId, this.indicator, this.startYear, this.endYear);
//	}

//	private JsonArray getJsonArray() {
//		String jsonArrayString = getJsonArrayString();
//		JsonArray unprocessedArray = new JsonParser().parse(jsonArrayString).getAsJsonArray();
//		JsonArray jsonArray = unprocessedArray.get(1).getAsJsonArray();
//		return jsonArray;
//	}

	protected String getJsonResponse() {
		String jsonResponse = "";
		// making API request
		try {
			URL url = new URL(this.urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.connect();
			int responsecode = conn.getResponseCode();
			// if the response is 200 OK get the line with the results
			if (responsecode == 200) {
				Scanner sc = new Scanner(url.openStream());
				while (sc.hasNext()) {
					jsonResponse += sc.nextLine();
				}
				sc.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonResponse;
	}
//	
//	private void fetchData() {
//		JsonArray jsonArray = getJsonArray();
//		int arraySize = jsonArray.size();
//
//		setCategory(jsonArray.get(0).getAsJsonObject().get("indicator").getAsJsonObject().get("value").toString());
//		setCountryName(jsonArray.get(0).getAsJsonObject().get("country").getAsJsonObject().get("value").toString());
//
//		JsonElement value;
//		int date;
//		instanceData = new Number[arraySize][2];
//
//		for (int i = 0; i < arraySize; i++) {
//			date = jsonArray.get(i).getAsJsonObject().get("date").getAsInt();
//			value = jsonArray.get(i).getAsJsonObject().get("value");
//			instanceData[i][0] = date;
//
//			// checking if a value exists for the given year
//			if (value.isJsonNull()) {
//				instanceData[i][1] = 0.0;
//			} else {
//				instanceData[i][1] = value.getAsDouble();
//			}
//		}
//	}
}
