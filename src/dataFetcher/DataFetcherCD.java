package dataFetcher;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DataFetcherCD extends DataFetcher{
	
	public static void main(String[] args) {
		System.out.println("Executing main...\n");
		int startYear = 2021;
		int endYear = 2022;
		String countryId = "can";
		String requestUrlDomain = "https://api.opencovid.ca/summary?";
		
		DataFetcherCD fetcher = getInstance("can", "COVID.19.DATA", startYear, endYear, requestUrlDomain);
	}

	protected DataFetcherCD(String countryId, String indicator, int startYear, int endYear, String httpRequestDomain) {
		super(countryId, indicator, startYear, endYear);
		setURL(httpRequestDomain);
		
	}
	
	public static DataFetcherCD getInstance(String countryId, String indicator, int startYear, int endYear, String requestUrlDomain) {
		String key = countryId + indicator;
		DataFetcherCD instance = (DataFetcherCD) instances.get(key);
		if(instance == null) {
			instance = new DataFetcherCD(countryId, indicator, startYear, endYear, requestUrlDomain);
			instances.put(key, instance);
		}
		else {
			int instanceStartYear = Integer.parseInt(instance.startYear);
			int instanceEndYear = Integer.parseInt(instance.endYear);
			if(instanceStartYear != startYear && instanceEndYear != endYear){
				instance.setStartYear(startYear);
				instance.setEndYear(endYear);
				instance.setURL(requestUrlDomain);
			}
			else if(instanceStartYear != startYear) {
				instance.setStartYear(startYear);
				instance.setURL(requestUrlDomain);
			}
			else if(instanceEndYear != endYear) {
				instance.setEndYear(endYear);
				instance.setURL(requestUrlDomain);
			}
		}
		instance.fetchData();
		return instance;
	}
	
	protected void setURL(String httpRequestDomain) {
		this.urlString = String.format(httpRequestDomain + "geo=%s&after=%s-01-01&before=%s-12-31&fill=true&version=true&pt_names=short&hr_names=hruid&fmt=json", this.countryId.toLowerCase(), this.startYear, this.endYear);
	}
	
	private String getJsonString() {
		return getJsonResponse();
	}
	
	private JsonArray getJsonArray() {
		String jsonObjectString = getJsonResponse();
		JsonObject unprocessedObject = new JsonParser().parse(jsonObjectString).getAsJsonObject();
		JsonArray jsonArray = unprocessedObject.get("data").getAsJsonArray();
		return jsonArray;
	}
	
	private void fetchData() {
		JsonArray jsonArray = getJsonArray();
		int arraySize = jsonArray.size();
		JsonElement value;
		String date;
		instanceData = new String[2][arraySize];
		
		for (int i = 0; i < arraySize; i++) {
			date = jsonArray.get(i).getAsJsonObject().get("date").getAsString();
			value = jsonArray.get(i).getAsJsonObject().get("cases_daily");
			instanceData[0][i] = date;
//			System.out.println("date = " + instanceData[0][i]);
//			System.out.println("year = " + Integer.parseInt(instanceData[i][0].substring(0,instanceData[i][0].indexOf('-'))));
			// checking if a value exists for the given year
			if (value.isJsonNull()) {
				instanceData[1][i] = "0.0";
			} else {
				instanceData[1][i] = value.getAsString();
			}
//			System.out.println("value = " + instanceData[1][i]);
		}
	}
	
	

}
