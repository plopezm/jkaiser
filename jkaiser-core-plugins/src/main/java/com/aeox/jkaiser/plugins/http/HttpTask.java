package com.aeox.jkaiser.plugins.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.HashMap;
import java.util.Map;

import com.aeox.jkaiser.core.JobContext;
import com.aeox.jkaiser.core.ParameterType;
import com.aeox.jkaiser.core.Result;
import com.aeox.jkaiser.core.Task;
import com.aeox.jkaiser.core.exception.KaiserException;

public class HttpTask extends Task<HttpResponse<String>>{

	@Override
	public String getName() {
		return "jkaiser-http";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public String getDescription() {
		return "Performs http requests";
	}

	@Override
	public Map<String, ParameterType> getRequiredParameters() {
		final Map<String, ParameterType> params = new HashMap<>();
		params.put("headers", ParameterType.MAP);
		params.put("url", ParameterType.STRING);
		params.put("method", ParameterType.STRING);
		params.put("body", ParameterType.STRING);
		params.put("expectedResponseCode", ParameterType.INTEGER);
		return params;
	}

	@Override
	public Result<HttpResponse<String>> onCall(JobContext context) throws KaiserException {
		HttpClient client = HttpClient.newHttpClient();
				
	    HttpRequest request = HttpRequest.newBuilder()
	          .uri(URI.create((String) context.getParameter("url")))
	          .method(((String)context.getParameter("method")).toUpperCase(), BodyPublishers.ofString((String)context.getParameter("body")))
	          .build();

	    try {
			final HttpResponse<String> response =
			      client.send(request, BodyHandlers.ofString());
			
			final Integer expectedResponseCode = (Integer) context.getParameter("expectedResponseCode");
			if (expectedResponseCode != response.statusCode()) {
				throw new KaiserException("Received code was " + response.statusCode() + " buy expected code is " + expectedResponseCode, 400);
			}
			
			return new Result<HttpResponse<String>>() {			
				@Override
				public boolean wasError() {
					return false;
				}
				
				@Override
				public HttpResponse<String> getResult() {
					return response;
				}
			};
		} catch (IOException | InterruptedException e) {
			throw new KaiserException(e.getMessage(), 400);
		}
	    
	}

}
