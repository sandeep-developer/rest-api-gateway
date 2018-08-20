package org.bdp.fgwebservice.PostBookingClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONException;
import org.json.JSONObject;

public class TestPreBookingClientBenchmark {

	public static final String INTERNAL_BASE_URL = "http://lusazeattnat01/tms.greg/app.php/api/tms/";
	public static final String BASE_URL = "https://bdp-stage.freightgate.com/api";
	public static final String SESSION_LOGIN_PATH0 = "/session/";
	public static final String LOGOUT_PATH0 = "/logout/";
	public static final String FG_REPORTID = "/fclrates3";
	public static final String INT_REPORTID = "scenario";
	public static final String POST_METHOD = "POST";
	public static final String GET_METHOD = "GET";
	public static final int CONNECT_TIMEOUT_IN_MS = 90000;
	public static final String FG_AUTH_URL_USERNAME = "dw-0002098"; /// ADD UR FG USERNAME
	public static final String FG_AUTH_URL_PASSWORD = "927f4b0c97cd896308458462b56382de"; // ADD UR FG PASSWORD
	public static final int TOTAL_ITER_TEST_RUN = 5;
	static String jsonFileName = "C:\\Users\\smadaan\\Documents\\BDP Projects\\FG WS Integration\\test_scenario\\iWS_RouteScenario_Req.json";

	public static void main(String[] args) throws Exception {
		int i = 0;
		List<Long> fgCallIterTimesList = new LinkedList<Long>();
		List<Long> iWSIterTimesList = new LinkedList<Long>();
		List<Long> fgIterListLoginTimes = new LinkedList<Long>();
		List<Long> fgIterListQueryTimes = new LinkedList<Long>();
		List<Long> fgIterListLogoutTimes = new LinkedList<Long>();
		Date testRunStartTime = new Date();
		System.out.println("Benchmark Run iWs vs FG WS Call Started at:  " + testRunStartTime);
		while (i < TOTAL_ITER_TEST_RUN) {
			//benchMarkInternalWSCallExecution(i, iWSIterTimesList);
			benchMarkFGWSCallExecution(i, fgCallIterTimesList,fgIterListLoginTimes,fgIterListQueryTimes,fgIterListLogoutTimes);
			i++;
			// Thread.sleep(10000);

		}
		Date testRunEndTime = new Date();

		long totalRunTime = testRunEndTime.getTime() - testRunStartTime.getTime();

		
		///FG Individual Call (Login,Query.Logout)
		System.out.println("FG Call- Login Execution times List: " + fgIterListLoginTimes.toString());

		System.out.println("Average Execution time: for FG Login Calls Test Run of " + i + " Iterations: "
				+ calculateAverage(fgIterListLoginTimes) + "ms");
		System.out.println("Peak Execution time taken: for FG Login Calls in " + i + " run Iterations: "
				+ Collections.max(fgIterListLoginTimes) + "ms");
		System.out.println("Min Execution time taken: FG Login Calls in " + i + " run Iterations: "
				+ Collections.min(fgIterListLoginTimes) + "ms");
		
		System.out.println("FG Call- Query Execution times List: " + fgIterListQueryTimes.toString());
		System.out.println("Average Execution time: for FG Query Calls Test Run of " + i + " Iterations: "
				+ calculateAverage(fgIterListQueryTimes) + "ms");
		System.out.println("Peak Execution time taken: for FG Query Calls in " + i + " run Iterations: "
				+ Collections.max(fgIterListQueryTimes) + "ms");
		System.out.println("Min Execution time taken: FG Query Calls in " + i + " run Iterations: "
				+ Collections.min(fgIterListQueryTimes) + "ms");
		

		System.out.println("FG Call- Logout Execution times List: " + fgIterListLogoutTimes.toString());
		System.out.println("Average Execution time: for FG Logout Calls Test Run of " + i + " Iterations: "
				+ calculateAverage(fgIterListLogoutTimes) + "ms");
		System.out.println("Peak Execution time taken: for FG Logout Calls in " + i + " run Iterations: "
				+ Collections.max(fgIterListLogoutTimes) + "ms");
		System.out.println("Min Execution time taken: FG Logout Calls in " + i + " run Iterations: "
				+ Collections.min(fgIterListLogoutTimes) + "ms");

		///FG Total Execution for all consolidated Calls (Login,Query.Logout)

		System.out.println("Total Exceution for Test Run ( iWs vs FG WS) Iteration " + i + " Calls : "
				+ totalRunTime / (60 * 1000) + "mins");
		System.out.println("Benchmark Run iWs & FG WS Call Ended at:  " + testRunEndTime);

		System.out.println("FG Call- Execution times List: " + fgCallIterTimesList.toString());

		System.out.println("Average Execution time: for all FG Calls Test Run of " + i + " Iterations: "
				+ calculateAverage(fgCallIterTimesList) + "ms");
		System.out.println("Peak Execution time taken: FG Calls in " + i + " run Iterations: "
				+ Collections.max(fgCallIterTimesList) + "ms");
		System.out.println("Min Execution time taken: FG Calls in " + i + " run Iterations: "
				+ Collections.min(fgCallIterTimesList) + "ms");

		
		System.out.println("iWS Call- Execution times List: " + iWSIterTimesList.toString());
		System.out.println("Average Execution time taken: for all iWS Calls Test Run of " + i + " Iterations: "
				+ calculateAverage(iWSIterTimesList) + "ms");
		System.out.println("Peak Execution time taken: iWS Calls in " + i + " run Iterations: "
				+ Collections.max(iWSIterTimesList) + "ms");
		System.out.println("Min Execution time taken: iWS Calls in " + i + " run Iterations: "
				+ Collections.min(iWSIterTimesList) + "ms");

	}

	private static int calculateAverage(List<Long> executionTimes) {
		int sum = 0;
		for (int i = 0; i < executionTimes.size(); i++) {
			sum += executionTimes.get(i);
		}
		return sum / executionTimes.size();
	}

	/**
	 * @param i
	 * @param fgIterListLogoutTimes 
	 * @param fgIterListQueryTimes 
	 * @param fgIterListLoginTimes 
	 * @param fgCallIterTotalTime
	 * 
	 */
	private static void benchMarkFGWSCallExecution(int i, List<Long> fgIterTotalTimes, List<Long> fgIterListLoginTimes, List<Long> fgIterListQueryTimes, List<Long> fgIterListLogoutTimes) {
		long startTime = System.currentTimeMillis();
		testFGWSResponseTime(i,fgIterListLoginTimes,fgIterListQueryTimes,fgIterListLogoutTimes);
		long endTime = System.currentTimeMillis();
		long executionTime = endTime - startTime;
		System.out.println("Iter" + i + " Total execution time for FG Call: " + executionTime + "ms");
		fgIterTotalTimes.add(executionTime);
	}

	/**
	 * @param i
	 * 
	 */
	private static void benchMarkInternalWSCallExecution(int i, List<Long> iWSIterTotalTimes) {
		long startTime = System.currentTimeMillis();
		testInternalWSResponseTime();
		long endTime = System.currentTimeMillis();
		long executionTotalTime = endTime - startTime;
		System.out.println("Iter" + i + " Total execution time for iWS: " + executionTotalTime + "ms");
		iWSIterTotalTimes.add(executionTotalTime);
	}

	/**
	 * @param fgIterListLogoutTimes 
	 * @param fgIterListQueryTimes 
	 * @param fgIterListLoginTimes 
	 * 
	 */
	private static void testFGWSResponseTime(int iterationNumber, List<Long> fgIterListLoginTimes, List<Long> fgIterListQueryTimes, List<Long> fgIterListLogoutTimes) {
		String sessionID = null;
		JSONObject jsonResponseObj = null;
		String targetUrl = null;
		String httpResp = null;

		try {
			// GET - Call to login FG APi and Get the SessionId.
			long loginStartTime = System.currentTimeMillis();
			targetUrl = buildTargetResourceUrl("session", null);
			System.out.println("FG Session: Getting SessionId From " + targetUrl);

			httpResp = getHttpsResponse(targetUrl);
			System.out.println("Response" + httpResp);
			jsonResponseObj = new JSONObject(httpResp);
			if (jsonResponseObj.has("sessionId")) {
				sessionID = jsonResponseObj.getString("sessionId");
				System.out.println("Login Success: SessionId: " + sessionID);
			} else {
				System.out.println("Login Error: " + jsonResponseObj.getString("error"));
			}
			long loginEndTime = System.currentTimeMillis();
			long loginExecutionTime = loginEndTime - loginStartTime;
			fgIterListLoginTimes.add(loginExecutionTime);
			
			System.out.println("Iter" + iterationNumber + " Total execution time for Login FG: "
					+ loginExecutionTime + "ms");
			if (sessionID != null) {
				long searchQueryStartTime = System.currentTimeMillis();
				targetUrl = buildTargetResourceUrl("search", sessionID);
				System.out.println("Running FCL Rate Query Request on FG: " + targetUrl);
				httpResp = getHttpsResponse(targetUrl);
				System.out.println("Rate Query Response: " + httpResp);
				long searchQueryEndTime = System.currentTimeMillis();
				long searchQueryExecutionTime = searchQueryEndTime - searchQueryStartTime;
				fgIterListQueryTimes.add(searchQueryExecutionTime);
				
				System.out.println("Iter" + iterationNumber + " Total execution time for Search Query FG: "
						+ searchQueryExecutionTime + "ms");

				// GET- Call to Log Out FG Session
				long logoutStartTime = System.currentTimeMillis();

				targetUrl = buildTargetResourceUrl("logout", sessionID);
				System.out.println("Logout: Logging Out From Rate Request Session  " + targetUrl);
				httpResp = getHttpsResponse(targetUrl);
				System.out.println("Session Logout sucessfully " + sessionID);
				long logoutEndTime = System.currentTimeMillis();
				long logoutExecutionTime = logoutEndTime - logoutStartTime;
				fgIterListLogoutTimes.add(logoutExecutionTime);
			
				System.out.println("Iter" + iterationNumber + " Total execution time for Logout FG: "
						+ logoutExecutionTime + "ms");
			} else {
				System.out.println("Login Failed : SessionId ID expired or invalid!");
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(" FG WS call failed with IO_Protocol Exception!" + e.getMessage());

		} catch (JSONException e) {
			System.out.println(" FG WS call failed with Incorrect JSON Structure!" + e.getMessage());
		} catch (Exception e) {
			System.out.println("FG Request failed with exception:" + e.getMessage());

		}
	}

	/**
	 * 
	 */
	private static void testInternalWSResponseTime() {
		try {

			// String jsonFileName = "C:\\Users\\smadaan\\Documents\\BDP Projects\\FG WS
			// Integration\\test_scenario\\iWS_RouteScenario_Req.json";
			JSONObject jsonObject = buildJSONObject(jsonFileName);
			String targetUrl = null;
			String httpResp = null;

			// POST- Call to Internal WS to get Rates for Pre Booking data
			targetUrl = buildTargetResourceUrl("internal", null);
			System.out.println("Sending Pre Booking Request for rates to iWS: " + targetUrl);
			httpResp = getRouteRateResponseFromIntWebService(jsonObject, targetUrl);
			System.out.println("Rate Response from iWS(FG): " + httpResp);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(" FG WS call failed with IO_Protocol Exception!" + e.getMessage());

		} catch (JSONException e) {
			System.out.println(" FG WS call failed with Incorrect JSON Structure!" + e.getMessage());
		} catch (Exception e) {
			System.out.println("FG Request failed with exception:" + e.getMessage());
			e.printStackTrace();

		}
	}

	/**
	 * @param targetUrl
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ProtocolException
	 */
	private static String getHttpsResponse(String targetUrl)
			throws MalformedURLException, IOException, ProtocolException {
		HttpsURLConnection httpsConn;
		String httpResp;
		httpsConn = getRequestUrlConnection(targetUrl, GET_METHOD);
		BufferedReader bufferedReader = readHttpResponse(httpsConn);
		httpResp = buildHttpResponseInString(bufferedReader);
		return httpResp;
	}

	private static HttpsURLConnection getRequestUrlConnection(String urlString, String httpMethod)
			throws MalformedURLException, IOException, ProtocolException {
		// Create a Connection
		URL obj = new URL(urlString);
		HttpsURLConnection httpConn = (HttpsURLConnection) obj.openConnection();
		httpConn.setRequestMethod(httpMethod);
		if (httpMethod == "POST") {
			httpConn.setDoOutput(true);
			setHttpRequestHeader(httpConn);
		}
		if (httpMethod == "GET") {
			setHttpRequestHeader(httpConn);
		}
		return httpConn;
	}

	/**
	 * @param jsonRequestString
	 * @param targetUrl
	 * @return
	 * @throws JSONException
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ProtocolException
	 */
	private static String getRouteRateResponseFromIntWebService(JSONObject jsonRequestObj, String targetUrl)
			throws JSONException, MalformedURLException, IOException, ProtocolException {

		HttpURLConnection httpConn;
		String httpResp;
		BufferedReader bufferedReader;
		httpConn = getHttpUrlConnection(targetUrl, POST_METHOD);
		sendJsonPostRequest(jsonRequestObj, httpConn);
		bufferedReader = readHttpResponse(httpConn);
		httpResp = buildHttpResponseInString(bufferedReader);
		return httpResp;
	}

	private static void sendJsonPostRequest(JSONObject jsonRequestObj, HttpURLConnection httpConn) throws IOException {
		OutputStreamWriter out = new OutputStreamWriter(httpConn.getOutputStream());
		out.write(jsonRequestObj.toString());
		out.close();
	}

	private static String buildTargetResourceUrl(String resource, String sessionId)
			throws UnsupportedEncodingException {
		StringBuilder targetUrl = new StringBuilder();
		if (resource == "internal") {
			targetUrl.append(INTERNAL_BASE_URL).append(INT_REPORTID);
		} else if (resource == "session") {
			targetUrl.append(BASE_URL).append(SESSION_LOGIN_PATH0).append(getQueryStringSessionParameters());
		} else if (resource == "search") {
			targetUrl.append(BASE_URL).append("/json/").append(sessionId).append(FG_REPORTID).append("?")
					.append(getQueryStringSearchParameters());
		} else if (resource == "logout") {
			targetUrl.append(BASE_URL).append(LOGOUT_PATH0).append(sessionId);
		}

		return targetUrl.toString();
	}

	private static String getQueryStringSessionParameters() throws UnsupportedEncodingException {
		Map<String, String> httpParameters = getUrlSessionParameters();
		String queryString = getParamsUrlEncodedString(httpParameters);
		return queryString;
	}

	private static Map<String, String> getUrlSessionParameters() {
		// Add Session Request parameter
		Map<String, String> httpParameters = new HashMap<String, String>();
		httpParameters.put("auth_user_name", FG_AUTH_URL_USERNAME);
		httpParameters.put("auth_password", FG_AUTH_URL_PASSWORD);
		return httpParameters;
	}

	private static String getQueryStringSearchParameters() throws UnsupportedEncodingException {
		Map<String, String> searchParameters = getUrlSearchParameters();
		String queryString = getParamsUrlEncodedString(searchParameters);
		return queryString;
	}

	private static Map<String, String> getUrlSearchParameters() {
		// Add Search Request parameter
		Map<String, String> searchParameters = new HashMap<String, String>();

		searchParameters.put("search_origin_type", "PORT");
		searchParameters.put("search_origin_gw_code", "USHOU");
		searchParameters.put("search_destination_type", "PORT");
		searchParameters.put("search_destination_gw_code", "BEANR");
		searchParameters.put("search_equipment", "40DV");
		searchParameters.put("search_ETA", "2018-09-05...2018-09-10");
		searchParameters.put("search_CUTOFF", "2018-08-18");
		searchParameters.put("advanced", "1");
		searchParameters.put("search_als_percentage", ">0");
		searchParameters.put("search_cwf", "0.7");
		searchParameters.put("search_ttv", "2000");
		searchParameters.put("search_daa", "10000");
		searchParameters.put("sort_1", "score");
		searchParameters.put("sort_order_1", "ASC");
		searchParameters.put("start", "0");
		searchParameters.put("limit", "1");
		return searchParameters;
	}

	private static String buildHttpResponseInString(BufferedReader bufferedReader) throws IOException {
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = bufferedReader.readLine()) != null) {
			response.append(inputLine);
		}
		String httpResp = response.toString();
		return httpResp;
	}

	private static BufferedReader readHttpResponse(HttpURLConnection httpConn) throws IOException {
		int responseCode = httpConn.getResponseCode();
		BufferedReader bufferedReader;
		if (responseCode > 199 && responseCode < 300) {
			System.out
					.println("SUCCESS:: Response Code: " + responseCode + " Message: " + httpConn.getResponseMessage());
			bufferedReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
		} else {
			System.out
					.println("FAILURE:: Response Code: " + responseCode + " Message: " + httpConn.getResponseMessage());
			bufferedReader = new BufferedReader(new InputStreamReader(httpConn.getErrorStream()));
		}
		return bufferedReader;
	}

	private static HttpURLConnection getHttpUrlConnection(String urlString, String httpMethod)
			throws MalformedURLException, IOException, ProtocolException {
		// Create a Connection
		URL obj = new URL(urlString);
		HttpURLConnection httpConn = (HttpURLConnection) obj.openConnection();
		httpConn.setRequestMethod(httpMethod);
		if (httpMethod == "POST") {
			httpConn.setDoOutput(true);
			setHttpRequestHeader(httpConn);
		}
		if (httpMethod == "GET") {
			setHttpRequestHeader(httpConn);
		}
		return httpConn;
	}

	/**
	 * @param httpConn
	 */
	private static void setHttpRequestHeader(HttpURLConnection httpConn) {
		httpConn.setReadTimeout(CONNECT_TIMEOUT_IN_MS);
		httpConn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
		httpConn.setRequestProperty("username", "smadaan");
		httpConn.setRequestProperty("gtrace", "35");
		httpConn.setConnectTimeout(CONNECT_TIMEOUT_IN_MS);
	}

	public static void buildJSONMap() throws IOException {
		JsonFactory jsonFactory = new JsonFactory();
		ObjectMapper objectMapper = new ObjectMapper(jsonFactory);
		File file = new File(jsonFileName);
		TypeReference<HashMap<String, Object>> typeReference = new TypeReference<HashMap<String, Object>>() {
		};
		HashMap<String, Object> jsonMap = objectMapper.readValue(file, typeReference);
		System.out.println("JSON DATA: " + jsonMap);
	}

	/**
	 * Convert given Map of parameters to URL-encoded string
	 *
	 * @param parameters
	 *            request parameters
	 * @return URL-encoded parameters string
	 */
	public static String getParamsUrlEncodedString(Map<String, String> params) throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();

		for (Map.Entry<String, String> entry : params.entrySet()) {
			result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
			result.append("&");
		}
		String resultString = result.toString();
		return resultString.length() > 0 ? resultString.substring(0, resultString.length() - 1) : resultString;
	}

	private static JSONObject buildJSONObject(String filename) {
		InputStream bdpInputStream = null;
		try {
			bdpInputStream = new FileInputStream(filename);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String string = "";
		InputStreamReader bdpReader = new InputStreamReader(bdpInputStream);
		BufferedReader br = new BufferedReader(bdpReader);
		String line;
		try {
			while ((line = br.readLine()) != null) {
				string += line + "\n";
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JSONObject jsonObject = new JSONObject(string);

		return jsonObject;

	}
}