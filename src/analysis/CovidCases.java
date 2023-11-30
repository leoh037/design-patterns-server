package analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dataFetcher.DataFetcherCD;
import utility.arrayOperation;

public class CovidCases implements Analysis{
	
	public static final String analysisName = "Total Covid Cases";
	private DataFetcherCD fetcher;
	private String country;
	private String startYear;
	private String endYear;
	private Number[] years;
	private String methodCode = "C19.DAY.CASE"; // Total Covid Cases (daily)
	private static String[] methodNames = {"Annual Covid-19 Cases"};
	
	private Number analysisResult[][];
	private ArrayList<HashMap<Number, Number>> analysisData;
	private AnalysisResult result = new AnalysisResult();
	
	public String performAnalysis(String country, int startYear, int endYear, String requestUrlDomain) {
		initialize(country, startYear, endYear, requestUrlDomain);
		years = arrayOperation.generateNumbersInDecRange(startYear, endYear, 1);
		result.setResult(analysisData, analysisName, methodNames, getDataAsString(), years);
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String json = gson.toJson(result); 
        return json;
	}
	
	private void initialize(String country, int startYear, int endYear, String requestUrlDomain){
		this.country = country;
		this.startYear = String.valueOf(startYear);
		this.endYear = String.valueOf(endYear);
		fetcher = DataFetcherCD.getInstance(country, methodCode, startYear, endYear, requestUrlDomain);
		createSeriesCovidCases();
		generateAnalysisData();
	}
	
	private Number[] calculateAnnualCovidCases(DataFetcherCD fetcher) {

		String[][] data = fetcher.getData();
		int dataSize = data[0].length;
		
		Number[] years = arrayOperation.generateNumbersInDecRange(Integer.parseInt(this.startYear), Integer.parseInt(this.endYear), 1);
		int numberOfYears = years.length;
		
		Number[] cases = new Number[numberOfYears];
		Arrays.fill(cases, 0);
		
		int year;
		double value;
		Boolean foundYear;
		for(int i = 0; i < dataSize; i++) {
			year = Integer.parseInt(data[0][i].substring(0,data[0][i].indexOf('-')));
			value = Double.parseDouble(data[1][i]);
			foundYear = false;
			for(int j = 0; j<numberOfYears; j++) {
				if(year == years[j].intValue() && !foundYear) {
					cases[j] = cases[j].doubleValue() + value;
					foundYear = true;
				}
			}
		}
		return cases;
	}
	
	private void createSeriesCovidCases() {
		int startYear = Integer.parseInt(this.startYear);
		int endYear = Integer.parseInt(this.endYear);
		int seriesLength = (endYear - startYear) + 1;
		analysisResult = new Number[2][seriesLength];
		analysisResult[0] = arrayOperation.generateNumbersInDecRange(startYear, endYear, 1);
		analysisResult[1] = calculateAnnualCovidCases(fetcher);
	}
	
	private void generateAnalysisData() {
		analysisData = new ArrayList<HashMap<Number, Number>>();
		HashMap<Number, Number> series = new HashMap<Number, Number>();
		int size =  analysisResult[0].length;
		for(int i = 0; i < size; i++) {
			series.put(analysisResult[0][i], analysisResult[1][i]);
		}
		analysisData.add(series);
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
