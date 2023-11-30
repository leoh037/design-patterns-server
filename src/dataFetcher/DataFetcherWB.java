package dataFetcher;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class DataFetcherWB extends DataFetcher{

	
	public DataFetcherWB(String countryId, String indicator, int startYear, int endYear, String httpRequestDomain) {
		super(countryId, indicator, startYear, endYear);
		setURL(httpRequestDomain);
	}
	
	public static DataFetcher getInstance(String countryId, String indicator, int startYear, int endYear, String httpRequestDomain) {
		if(startYear >= endYear) {
			System.out.println("The start year must be strictly smaller than the end year");
			return null;
		}
		String key = countryId + indicator;
		DataFetcherWB instance = (DataFetcherWB) instances.get(key);
		if(instance == null) {
			instance = new DataFetcherWB(countryId, indicator, startYear, endYear, httpRequestDomain);
			instances.put(key, instance);
		}
		else {
			int instanceStartYear = Integer.parseInt(instance.startYear);
			int instanceEndYear = Integer.parseInt(instance.endYear);
			if(instanceStartYear != startYear && instanceEndYear != endYear){
				instance.setStartYear(startYear);
				instance.setEndYear(endYear);
				instance.setURL(httpRequestDomain);
			}
			else if(instanceStartYear != startYear) {
				instance.setStartYear(startYear);
				instance.setURL(httpRequestDomain);
			}
			else if(instanceEndYear != endYear) {
				instance.setEndYear(endYear);
				instance.setURL(httpRequestDomain);
			}
		}
		instance.fetchData();
		return instance;
	}
	
	protected void setURL(String httpRequestDomain) {
		this.urlString = String.format(httpRequestDomain + "/country/%s/indicator/%s?date=%s:%s&format=json",
				this.countryId, this.indicator, this.startYear, this.endYear);
	}
	
	private JsonArray getJsonArray() {
		String jsonArrayString = getJsonResponse();
		JsonArray unprocessedArray = new JsonParser().parse(jsonArrayString).getAsJsonArray();
		JsonArray jsonArray = unprocessedArray.get(1).getAsJsonArray();
		return jsonArray;
	}
	
	
	private void fetchData() {
		JsonArray jsonArray = getJsonArray();
		int arraySize = jsonArray.size();

		setCategory(jsonArray.get(0).getAsJsonObject().get("indicator").getAsJsonObject().get("value").toString());
		setCountryName(jsonArray.get(0).getAsJsonObject().get("country").getAsJsonObject().get("value").toString());

		JsonElement value;
		String date;
		instanceData = new String[arraySize][2];

		for (int i = 0; i < arraySize; i++) {
			date = jsonArray.get(i).getAsJsonObject().get("date").getAsString();
			value = jsonArray.get(i).getAsJsonObject().get("value");
			instanceData[i][0] = date;

			// checking if a value exists for the given year
			if (value.isJsonNull()) {
				instanceData[i][1] = "0.0";
			} else {
				instanceData[i][1] = value.getAsString();
			}
		}
	}
	
	public String getDataAsString() {
		int size = this.instanceData.length;
		String dataString = "";
		
		dataString = dataString + "For the country: \"" + getCountryName() + "\" and the category: \""
				+ getCategory() + "\", we have the following:";

		for (int i = 0; i < size; i++) {
			dataString = dataString + "\nFor the year " + instanceData[i][0] + " the value is " + instanceData[i][1];
		}	
		return dataString;
	}

}
