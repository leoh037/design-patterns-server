package httpProxyServer;

import analysis.Analysis;

public class RequestedAnalysis {
	
	private Analysis analysisMethod;
	
	public void setAnalysisMethod(Analysis newMethod) {
		this.analysisMethod = newMethod;
	}
	
	public String analyze(String country, int startYear, int endYear, String httpRequestDomain) {
		return analysisMethod.performAnalysis(country, startYear, endYear, httpRequestDomain);
	}

}