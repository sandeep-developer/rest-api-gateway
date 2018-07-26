package org.san.webservice.api.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

public class RestApiClient {

	public static final String BASE_URL = "BASE_URL";
	public static final String SESSION_LOGIN_PATH0 = "/session/";
	public static final String RESOURCE_PATH = "/RESOURCE";
	public static final String ADD_PATH0 = "/add/";
	public static final String DELETE_PATH0 = "/del/";
	public static final String LOGOUT_PATH0 = "/logout/";
	public static final String UPDATE_PATH0 = "/update/";
	public static final String POST_METHOD = "POST";
	public static final String GET_METHOD = "GET";
	public static final int CONNECT_TIMEOUT_IN_MS = 7000;

	public static void main(String[] args) throws Exception {

		try {

			String jsonAddString = "{    \"entry_id\":21}";
			String jsonUpdateString = "{ \"entry_id\": 20}";
			String jsonDeleteString = "{ \"entry_id\": 20}";
			String sessionID = null;
			JSONObject jsonResponseObj = null;
			String targetUrl = null;
			String httpResp = null;

			// First Call to Get the SessionId
			targetUrl = buildTargetResourceUrl("session", null);
			System.out.println("FG Session: Getting SessionId From " + targetUrl);

			httpResp = getHttpResponse(targetUrl);
			System.out.println("Response" + httpResp);
			jsonResponseObj = buildJSONResponseObject(httpResp);
			if (jsonResponseObj.has("sessionId")) {
				sessionID = jsonResponseObj.getString("sessionId");
				System.out.println("Login Success: SessionId: " + sessionID);
			} else {
				System.out.println("Login Error: " + jsonResponseObj.getString("error"));
			}

			// Second Call to use as Session ID and Send Booking request to FG

			targetUrl = buildTargetResourceUrl("add", sessionID);
			System.out.println("Sending New Request to Rest Service: " + targetUrl);
			// targetUrl = BASE_URL + ADD_PATH0 + sessionID + REPORTID;
			httpResp = getJsonResponseStringFromPOSTRequest(jsonAddString, targetUrl);
			System.out.println("New Request posted: " + httpResp);

			// Call to use as Session ID and Update Booking request to FG
			targetUrl = buildTargetResourceUrl("update", sessionID);
			System.out.println("Sending Update Request: " + targetUrl);
			httpResp = getJsonResponseStringFromPOSTRequest(jsonUpdateString, targetUrl);
			System.out.println("Request Updated: " + httpResp);

			targetUrl = buildTargetResourceUrl("delete", sessionID);
			System.out.println("Delete Request: " + targetUrl);
			httpResp = getJsonResponseStringFromPOSTRequest(jsonDeleteString, targetUrl);
			System.out.println("Request Deleted Sucessfully: " + httpResp);

			// targetUrl = BASE_URL + LOGOUT_PATH0 + sessionID;
			targetUrl = buildTargetResourceUrl("logout", sessionID);
			System.out.println("Logout: Logging Out fromSession  " + targetUrl);
			httpResp = getHttpResponse(targetUrl);
			System.out.println("Session Logout sucessfully" + sessionID);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(" WS call failed with IO_Protocol Exception!" + e.getMessage());

		} catch (JSONException e) {
			System.out.println(" WS call failed with Incorrect JSON Structure!" + e.getMessage());
		} catch (Exception e) {
			System.out.println("WS Request failed with exception:" + e.getMessage());

		}
	}

	/**
	 * @param targetUrl
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ProtocolException
	 */
	private static String getHttpResponse(String targetUrl)
			throws MalformedURLException, IOException, ProtocolException {
		HttpsURLConnection httpConn;
		String httpResp;
		httpConn = getRequestUrlConnection(targetUrl, GET_METHOD);
		BufferedReader bufferedReader = readHttpResponse(httpConn);
		httpResp = buildHttpResponseInString(bufferedReader);
		return httpResp;
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
	private static String getJsonResponseStringFromPOSTRequest(String jsonRequestString, String targetUrl)
			throws JSONException, MalformedURLException, IOException, ProtocolException {
		JSONObject jsonRequestObj;
		HttpsURLConnection httpConn;
		String httpResp;
		BufferedReader bufferedReader;
		jsonRequestObj = new JSONObject(jsonRequestString);
		httpConn = getRequestUrlConnection(targetUrl, POST_METHOD);
		sendJsonPostRequest(jsonRequestObj, httpConn);
		bufferedReader = readHttpResponse(httpConn);
		httpResp = buildHttpResponseInString(bufferedReader);
		return httpResp;
	}

	private static void sendJsonPostRequest(JSONObject jsonRequestObj, HttpsURLConnection httpConn) throws IOException {
		OutputStreamWriter out = new OutputStreamWriter(httpConn.getOutputStream());
		out.write(jsonRequestObj.toString());
		out.close();
	}

	private static String buildTargetResourceUrl(String resource, String sessionId)
			throws UnsupportedEncodingException {
		StringBuilder targetUrl = new StringBuilder();
		if (resource == "session") {
			targetUrl.append(BASE_URL).append(SESSION_LOGIN_PATH0).append(getQueryStringParameters());
		} else if (resource == "add") {
			targetUrl.append(BASE_URL).append(ADD_PATH0).append(sessionId).append(RESOURCE_PATH);
		} else if (resource == "update") {
			targetUrl.append(BASE_URL).append(UPDATE_PATH0).append(sessionId).append(RESOURCE_PATH);
		} else if (resource == "delete") {
			targetUrl.append(BASE_URL).append(DELETE_PATH0).append(sessionId).append(RESOURCE_PATH);
		} else if (resource == "logout") {
			targetUrl.append(BASE_URL).append(LOGOUT_PATH0).append(sessionId);
		}
		return targetUrl.toString();
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

	private static BufferedReader readHttpResponse(HttpsURLConnection httpConn) throws IOException {
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

	private static JSONObject buildJSONResponseObject(String response) {
		// print result
		JSONObject jsonObject = new JSONObject(response);
		return jsonObject;
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
	 * @param httpConn
	 */
	private static void setHttpRequestHeader(HttpsURLConnection httpConn) {
		httpConn.setReadTimeout(CONNECT_TIMEOUT_IN_MS);
		httpConn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
		httpConn.setConnectTimeout(CONNECT_TIMEOUT_IN_MS);
	}

	private static String getQueryStringParameters() throws UnsupportedEncodingException {
		Map<String, String> httpParameters = getUrlParameters();
		String queryString = getParamsUrlEncodedString(httpParameters);
		return queryString;
	}

	private static Map<String, String> getUrlParameters() {
		// Add Request parameter
		Map<String, String> httpParameters = new HashMap<String, String>();
		httpParameters.put("auth_user_name", "");
		httpParameters.put("auth_password", "");
		return httpParameters;
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
}