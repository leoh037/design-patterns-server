package analysis;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dataFetcher.DataFetcher;
import dataFetcher.DataFetcherWB;
import utility.arrayOperation;

public class HealthExpenditureCapitaPerHospitalBedsRatio implements Analysis {
	
	private static final String analysisName = "Ratio of Current Health Expenditure per Hospial Beds";
	private DataFetcher numeratorFetcher;
	private DataFetcher denominatorFetcher;
	private String country;
	private String startYear;
	private String endYear;
	private Number[] years;
	private String numeratorIndicator = "SH.XPD.CHEX.PC.CD"; // Current health expenditure per capita (current US$) 
	private String denominatorIndicator = "SH.MED.BEDS.ZS"; // Hospital beds (per 1,000 people) 
	private static String[] methodNames = {"Current Health Expenditure per Capita", "Hospital Beds (per 1,000 people)"};
	
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
		numeratorFetcher = DataFetcherWB.getInstance(country, numeratorIndicator, startYear, endYear, httpRequestDomain);
		denominatorFetcher = DataFetcherWB.getInstance(country, denominatorIndicator, startYear, endYear, httpRequestDomain);
		calculateRatio();
		generateAnalysisData();
	}
	
	protected void calculateRatio() {
		Number[] numeratorArray = this.numeratorFetcher.getValueArray();
		Number[] denominatorArray = this.denominatorFetcher.getValueArray();
		int size = numeratorArray.length;
		analysisResult = new Number[2][size];
		int startYear = Integer.parseInt(this.startYear);
		int endYear = Integer.parseInt(this.endYear);
		analysisResult[0] = arrayOperation.generateNumbersInDecRange(startYear, endYear, 1);
		for(int i = 0; i < size; i++) {
			if(denominatorArray[i].floatValue() == 0) analysisResult[1][i] = 0;
			else analysisResult[1][i] = numeratorArray[i].floatValue() / denominatorArray[i].floatValue();
		}
	}
	
	private void generateAnalysisData() {
		analysisData = new ArrayList<HashMap<Number, Number>>();
		HashMap<Number, Number> series1 = new HashMap<Number, Number>();
		int size =  analysisResult[0].length;
		for(int i = 0; i < size; i++) {
			series1.put(analysisResult[0][i], analysisResult[1][i]);
		}
		analysisData.add(series1);
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
