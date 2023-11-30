package analysis;

import java.util.ArrayList;
import java.util.HashMap;

public class AnalysisResult {
	
	private String analysisName;
	private String[] methodNames;
	private ArrayList<HashMap<Number, Number>> analysisData;
	private String analysisResultString;
	private Number[] years;
	
	public void setResult(ArrayList<HashMap<Number, Number>> analysisData, String analysisName, String[] methodNames, String resultString, Number[] years) {
		this.analysisName = analysisName;
		this.methodNames = methodNames;
		this.analysisData = analysisData;
		this.analysisResultString  = resultString;
		this.years = years;
	}

}
