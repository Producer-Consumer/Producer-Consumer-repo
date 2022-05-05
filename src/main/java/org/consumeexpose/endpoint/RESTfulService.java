package org.consumeexpose.endpoint;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

public class RESTfulService {
	
	private static final String URL = "Url";
	private static final String METHOD = "Method";
	private static final String REQUEST_HEADERS = "Request Headers";
	private static final String RESPONSE_HEADERS = "Response Headers";
	private static final String REQUEST_BODY = "Request Body";
	private static final String QUERY_PARAMS = "Query Params";
	private static final String RESPONSE_BODIES = "Response Body/Bodies";	
	
	private static final String TABLE_TD = "<td>";
	private static final String TABLE_TD_C = "</td>";
	private static final String TABLE_TR = "<tr>";
	private static final String TABLE_TR_C = "</tr>";
	private static final String TABLE = "<table>";
	private static final String TABLE_C = "</table>";
	private static final String TABLE_CAPTION = "<caption>";
	private static final String TABLE_CAPTION_C = "</caption>";
	
	private static final String PRE = "<pre>";
	private static final String PRE_C = "</pre>";
	
	private String service;
	private String url;
	private String body;
	private HashMap<String,String> queryParams;
	private HashMap<String,String> responseHeaders;
	private HashMap<String,String> requestHeaders;
	private HashMap<Integer,String> response;
	
	private String method;

	public RESTfulService(String service,String url, String method) {
		super();
		this.service = service;
		this.url = url;
		this.method = method;
	}
	
	
	
	

	public String getService() {
		return service;
	}





	public void setService(String service) {
		this.service = service;
	}





	public HashMap<Integer, String> getResponse() {
		return response;
	}





	public void setResponse(HashMap<Integer, String> response) {
		this.response = response;
	}





	public HashMap<String, String> getRequestHeaders() {
		return requestHeaders;
	}





	public void setRequestHeaders(HashMap<String, String> requestHeaders) {
		this.requestHeaders = requestHeaders;
	}





	public HashMap<String, String> getResponseHeaders() {
		return responseHeaders;
	}



	public void setResponseHeaders(HashMap<String, String> responseHeaders) {
		this.responseHeaders = responseHeaders;
	}



	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public HashMap<String, String> getQueryParams() {
		return queryParams;
	}

	public void setQueryParams(HashMap<String, String> queryParams) {
		this.queryParams = queryParams;
	}

	

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}
	
	public String getHTMLString() {
		
		StringBuilder sb = new StringBuilder();
		sb.append(TABLE);
		sb.append(TABLE_CAPTION+this.service+TABLE_CAPTION_C);
		sb.append(TABLE_TR+TABLE_TD+URL+TABLE_TD_C+TABLE_TD+this.url+TABLE_TD_C+TABLE_TR_C);
		sb.append(TABLE_TR+TABLE_TD+METHOD+TABLE_TD_C+TABLE_TD+this.method+TABLE_TD_C+TABLE_TR_C);
		if(this.requestHeaders!=null) {
			sb.append(TABLE_TR+TABLE_TD+REQUEST_HEADERS+TABLE_TD_C+TABLE_TD+getHashMapHTMLString(this.requestHeaders)+TABLE_TD_C+TABLE_TR_C);
		}
		if(this.body!=null) {
			sb.append(TABLE_TR+TABLE_TD+REQUEST_BODY+TABLE_TD_C+TABLE_TD+PRE+this.body+PRE_C+TABLE_TD_C+TABLE_TR_C);
		}
		if(this.queryParams!=null) {
			sb.append(TABLE_TR+TABLE_TD+QUERY_PARAMS+TABLE_TD_C+TABLE_TD+PRE+getHashMapHTMLString(this.queryParams)+PRE_C+TABLE_TD_C+TABLE_TR_C);
		}
		if(this.responseHeaders!=null) {
			sb.append(TABLE_TR+TABLE_TD+RESPONSE_HEADERS+TABLE_TD_C+TABLE_TD+PRE+getHashMapHTMLString(this.responseHeaders)+PRE_C+TABLE_TD_C+TABLE_TR_C);
		}
		if(this.response!=null) {
			sb.append(TABLE_TR+TABLE_TD+RESPONSE_BODIES+TABLE_TD_C+TABLE_TD+PRE+getResponseHTMLString(this.response)+PRE_C+TABLE_TD_C+TABLE_TR_C);
		}
		
		sb.append(TABLE_C);
		return sb.toString();
		
	}
	
	private static String getResponseHTMLString(HashMap<Integer,String> map) {

		JSONObject payload = new JSONObject();
		for(Map.Entry<Integer,String> entry : map.entrySet()) 
			payload.put(entry.getKey()+"", new JSONObject(entry.getValue()));
		
		
		return payload.toString(1);
	}
	 
	private  static String getHashMapHTMLString(HashMap<String,String> map) {

		JSONObject payload = new JSONObject();
		for(Map.Entry<String,String> entry : map.entrySet()) {
			
			payload.put(entry.getKey(), entry.getValue());
		}
		
		return payload.toString(1);
	}

	@Override
	public String toString() {
		return "RESTfulService [service=" + service + ", url=" + url + ", body=" + body + ", queryParams=" + queryParams
				+ ", responseHeaders=" + responseHeaders + ", requestHeaders=" + requestHeaders + ", response="
				+ response + ", method=" + method + "]";
	}
	
	

}
