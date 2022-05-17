package org.producerconsumer.response;

public class Response {

	private int statusCode;
	private String responseBody;
	
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public String getResponseBody() {
		return responseBody;
	}
	public Response setResponseBody(String responseBody) {
		this.responseBody = responseBody;
		return this;
	}
	
	public Response(int statusCode, String responseBody) {
		super();
		this.statusCode = statusCode;
		this.responseBody = responseBody;
	}
	
	public static Response setStatus(int code) {
		return new Response(code,null);
	}
	
	
	
}
