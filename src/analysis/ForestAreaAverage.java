package analysis;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dataFetcher.DataFetcher;
import dataFetcher.DataFetcherWB;
//package statsVisualiser.gui;


public class ForestAreaAverage implements Analysis{
	
	private static String analysisName = "Average Forest Area (as % of land area)";
	Number[][] resultArray;
	private DataFetcher fetcher;
	private String country;
	private String startYear;
	private String endYear;
	private Number[] years;
	private String methodCode = "AG.LND.FRST.ZS"; // Forest area (% of land area)
	private static String[] methodNames = {"Forest Area (% of land area)"};
	
	private Number analysisResult[][];
	private ArrayList<HashMap<Number, Number>> analysisData;
	private AnalysisResult result = new AnalysisResult();

	public String performAnalysis(String country, int startYear, int endYear, String httpRequestDomain) {
		initialize(country, startYear, endYear, httpRequestDomain);
		years = new Number[2];
		years[0] = Integer.parseInt(this.startYear);
		years[1] = Integer.parseInt(this.endYear);
		result.setResult(analysisData, analysisName, methodNames, getDataAsString(), years);
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String json = gson.toJson(result); 
        return json;
	}
	
	private void initialize(String country, int startYear, int endYear, String httpRequestDomain){
		this.country = country;
		this.startYear = String.valueOf(startYear);
		this.endYear = String.valueOf(endYear);
		fetcher = DataFetcherWB.getInstance(country, methodCode, startYear-1, endYear, httpRequestDomain);
		createAverage();
		generateAnalysisData();
	}
	
	private void createAverage() {
		int startYear = Integer.parseInt(this.startYear);
		int endYear = Integer.parseInt(this.endYear);
		analysisResult = calculateAverage();
	}
	
	public Number[][] calculateAverage(){
		
		String data[][] =  fetcher.getData();
		int size = data.length;
		Number totalvalue = 0;	
		
		for(int i=0; i<size; i++) {
			totalvalue = totalvalue.doubleValue() + Double.parseDouble(data[i][1]);
		}
		
		Number[][] result = new Number[1][2];
		
		double target = totalvalue.doubleValue()/size;
		String str = String.format("%.2f", target);
		result[0][0] = Double.valueOf(str);
		
		double other = 100.0 - target;
		String str1 = String.format("%.2f", other);
		result[0][1] = Double.valueOf(str1);
		
		return result;
	}
	
	private void generateAnalysisData() {
		analysisData = new ArrayList<HashMap<Number, Number>>();
		HashMap<Number, Number> series1 = new HashMap<Number, Number>();
		HashMap<Number, Number> series2 = new HashMap<Number, Number>();
		series1.put(Integer.parseInt(this.startYear), analysisResult[0][0]);
		series1.put(Integer.parseInt(this.endYear), analysisResult[0][0]);
		series2.put(Integer.parseInt(this.startYear), analysisResult[0][1]);
		series2.put(Integer.parseInt(this.endYear), analysisResult[0][1]);
		analysisData.add(series1);
		analysisData.add(series2);
	}
	
	private String getDataAsString() {
		String reportMessage;
		reportMessage = "Average forest area (as % of land area) \n";
		reportMessage = reportMessage + "For the years " + this.startYear + "-" + this.endYear + " => " + analysisResult[0][0];
		reportMessage = reportMessage + "\n==============================\n";
		reportMessage = reportMessage +  "Average area for all other uses (as % of land area)\n";
		reportMessage = reportMessage + "For the years " + this.startYear + "-" + this.endYear + " => " + analysisResult[0][1];
		reportMessage = reportMessage + "\n\n";
		return reportMessage;
	}
}
