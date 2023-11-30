package httpProxyServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import analysis.Analysis;
import analysis.CO2EnergyPollution3SeriesAPC;
import analysis.CO2PerGDPRatio;
import analysis.CovidCases;
import analysis.EducationExpenditureAverage;
import analysis.EducationHealth2SeriesAPC;
import analysis.ForestAirPollution2SeriesAPC;
import analysis.ForestAreaAverage;
import analysis.HealthExpenditureCapitaPerHospitalBedsRatio;
import analysis.HospitalBeds1SeriesAPC;

import java.util.HashMap;

import java.util.Map;

public class Server {
	
	public static void main(String[] args) throws Exception {
		int port = 8000;
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		System.out.println("server started at " + server);
		server.createContext("/Analysis", new AnalysisServerHandler());
		server.setExecutor(null); // creates a default executor
		server.start();
	}

	static class AnalysisServerHandler implements HttpHandler {
		
		private Analysis analysis;
		private RequestedAnalysis requestedAnalysis;
		private String analysisResponse;
		private static HashMap<String, Analysis> analysisMethods = new HashMap<String, Analysis>();
		
		public void handle(HttpExchange t) throws IOException {
			
			analysisMethods.put("APC.Hospital.Beds", new HospitalBeds1SeriesAPC());
			analysisMethods.put("APC.Air.Pollution+CO2.Emissions+Energy.Use", new CO2EnergyPollution3SeriesAPC());
			analysisMethods.put("APC.Education+Health.Expenditure", new EducationHealth2SeriesAPC());
			analysisMethods.put("Forest.Area+Air.Pollution", new ForestAirPollution2SeriesAPC());
			analysisMethods.put("Ratio.CO2.emissions.Per.GDP", new CO2PerGDPRatio());
			analysisMethods.put("Ratio.Current.Health.Expenditure.Per.Hospial.Beds", new HealthExpenditureCapitaPerHospitalBedsRatio());
			analysisMethods.put("Average.Government.Expenditure.Education", new EducationExpenditureAverage());
			analysisMethods.put("Average.Forest.Area", new ForestAreaAverage());
			analysisMethods.put("Covid.Cases", new CovidCases());
			
			String QueryComponentURI = t.getRequestURI().getQuery();
			Map<String, String> queryParameters = queryToMap(QueryComponentURI);
			
			String analysisCode = queryParameters.get("analysis_method");
			String countryCode = queryParameters.get("country");
			String yearRange = queryParameters.get("year_range");
			String startYear = yearRange.substring(0,yearRange.indexOf(':'));
			String endYear = yearRange.substring(yearRange.indexOf(':')+1);
			String httpRequestDomain = queryParameters.get("request_domain");
			
			//System.out.println("methodCode = "+analysisCode+", country code = "+countryCode+" , start year = "+startYear+", end year = "+endYear);
	
			analysis = analysisMethods.get(analysisCode);
			
			requestedAnalysis = new RequestedAnalysis();
			requestedAnalysis.setAnalysisMethod(analysis);
			analysisResponse = requestedAnalysis.analyze(countryCode, Integer.parseInt(startYear), Integer.parseInt(endYear), httpRequestDomain);
			
			t.sendResponseHeaders(200, analysisResponse.length());
			OutputStream os = t.getResponseBody();
			os.write(analysisResponse.getBytes());
			os.close();
		}

		public Map<String, String> queryToMap(String query) {
			if (query == null) {
				return null;
			}
			Map<String, String> result = new HashMap<String, String>();
			String[] parameters = query.split("&"); //generates an array of strings, with each String element being the substrings that were separated by the '&' character
			for (String param : parameters) {
				String[] entry = param.split("="); //generates an array of strings, with each String element being the substrings that were separated by the '=' character
				if (entry.length > 1) {
					result.put(entry[0], entry[1]);
				} else {
					result.put(entry[0], "");
				}
			}
			return result;
		}
	}
}
