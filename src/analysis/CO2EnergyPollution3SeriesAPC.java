package analysis;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dataFetcher.DataFetcher;
import dataFetcher.DataFetcherWB;
import utility.arrayOperation;

public class CO2EnergyPollution3SeriesAPC implements Analysis {
	
	public static final String analysisName = "APC for PM2.5 Air Pollution, CO2 Emissions, and Energy Use";
	private DataFetcher fetcher1;
	private DataFetcher fetcher2;
	private DataFetcher fetcher3;
	private String country;
	private String startYear;
	private Number[] years;
	private String endYear;
	private String methodCode1 = "EN.ATM.PM25.MC.M3"; // PM2.5 air pollution, mean annual exposure (micrograms per cubic meter)
	private String methodCode2 = "EN.ATM.CO2E.PC"; // CO2 emissions (metric tons per capita)
	private String methodCode3 = "EG.USE.PCAP.KG.OE"; // Energy use (kg of oil equivalent per capita)
	private static String[] methodNames = {"PM2.5 Air Pollution", "CO2 Emissions", "Energy Use"};
	
	private Number analysisResult[][];
	private ArrayList<HashMap<Number, Number>> analysisData;
	private AnalysisResult result = new AnalysisResult();
	
	public String performAnalysis(String country, int startYear, int endYear, String httpRequestDomain) {
		initialize(country, startYear, endYear, httpRequestDomain);
		years = arrayOperation.generateNumbersInDecRange(startYear, endYear, 1);
		result.setResult(analysisData, analysisName, methodNames, getDataAsString(), years);
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String json = gson.toJson(result); 
        return json;
	}
	
	private void initialize(String country, int startYear, int endYear, String httpRequestDomain){
		this.country = country;
		this.startYear = String.valueOf(startYear);
		this.endYear = String.valueOf(endYear);
		fetcher1 = DataFetcherWB.getInstance(country, methodCode1, startYear-1, endYear, httpRequestDomain);
		fetcher2 = DataFetcherWB.getInstance(country, methodCode2, startYear-1, endYear, httpRequestDomain);
		fetcher3 = DataFetcherWB.getInstance(country, methodCode3, startYear-1, endYear, httpRequestDomain);
		// setting the start year to a  previous year in order to be able to calculate the APC for the first data value
		createSeriesAnnualPercentageChange();
		generateAnalysisData();
	}

	private void createSeriesAnnualPercentageChange() {
		int startYear = Integer.parseInt(this.startYear);
		int endYear = Integer.parseInt(this.endYear);
		int seriesLength = (endYear - startYear) + 1;
		analysisResult = new Number[4][seriesLength];
		analysisResult[0] = arrayOperation.generateNumbersInDecRange(startYear, endYear, 1);
		analysisResult[1] = calculateAnnualPercentageChange(fetcher1);
		analysisResult[2] = calculateAnnualPercentageChange(fetcher2);
		analysisResult[3] = calculateAnnualPercentageChange(fetcher3);
	}
	
	private Number[] calculateAnnualPercentageChange(DataFetcher fetcher) {
		Number[] valueArray = fetcher.getValueArray();
		int valueArraysize = valueArray.length;
		int resultArraySize = valueArraysize - 1;
		Number[] resultArray = new Number[resultArraySize];
		float currentYearValue;
		float previousYearValue;
		for(int i = 0; i < resultArraySize; i++) {
			currentYearValue = valueArray[i].floatValue();
			previousYearValue = valueArray[i+1].floatValue();
			if(previousYearValue != 0) resultArray[i] = (currentYearValue - previousYearValue) / previousYearValue;
			//else resultArray[i] = null;
			else resultArray[i] = 0;
		}
		return resultArray;
	}
	
	private void generateAnalysisData() {
		analysisData = new ArrayList<HashMap<Number, Number>>();
		HashMap<Number, Number> series1 = new HashMap<Number, Number>();
		HashMap<Number, Number> series2 = new HashMap<Number, Number>();
		HashMap<Number, Number> series3 = new HashMap<Number, Number>();
		int size =  analysisResult[0].length;
		for(int i = 0; i < size; i++) {
			series1.put(analysisResult[0][i], analysisResult[1][i]);
			series2.put(analysisResult[0][i], analysisResult[2][i]);
			series3.put(analysisResult[0][i], analysisResult[3][i]);
		}
		analysisData.add(series1);
		analysisData.add(series2);
		analysisData.add(series3);
	}
	
	private String getDataAsString() {
		String reportMessage;
		int rows = analysisResult.length;
		int columns = analysisResult[0].length;
		String year;
		String seriesName;
		String seriesValue;
		Boolean isYearRow;
		
		reportMessage = analysisName + "\n==============================\n";
		for(int i = 0; i < columns ; i++) {
			isYearRow = true;
			for(int j = 0; j < rows; j++) {
				if(isYearRow) {
					year = analysisResult[j][i].toString();
					reportMessage = reportMessage + "Year " + year + ":";
					isYearRow = false;
				}
				else {
					seriesName = methodNames[j-1];
					seriesValue = analysisResult[j][i].toString();
					reportMessage = reportMessage + "\n\t" + seriesName +	" => " + seriesValue;			}
			}
			reportMessage = reportMessage + "\n\n";
		}
		return reportMessage;
	}
}
