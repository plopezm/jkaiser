package com.aeox.jkaiser.plugins.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.HashMap;
import java.util.Map;

import com.aeox.jkaiser.core.JobContext;
import com.aeox.jkaiser.core.ParameterMappings;
import com.aeox.jkaiser.core.ParameterType;
import com.aeox.jkaiser.core.Result;
import com.aeox.jkaiser.core.Task;
import com.aeox.jkaiser.core.exception.KaiserException;
import com.aeox.jkaiser.core.result.HttpContent;
import com.aeox.jkaiser.core.result.HttpResponseResult;

public class HttpTask extends Task<HttpContent>{
	
	public HttpTask() {
		super();
	}
	
	public HttpTask(ParameterMappings mappings) {
		super(mappings);
	}

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
		params.put("url", ParameterType.STRING);
		params.put("method", ParameterType.STRING);
		return params;
	}


	@Override
	public Map<String, ParameterType> getOptionalParameters() {
		final Map<String, ParameterType> params = new HashMap<>();
		params.put("headers", ParameterType.STRING_MAP);
		params.put("body", ParameterType.STRING);
		params.put("expectedResponseCode", ParameterType.INTEGER);
		return params;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Result<HttpContent> onCall(JobContext context) throws KaiserException {
		final String body = context.getParameter("body") != null ? (String) context.getParameter("body") : "";
		Map<String, String> headers = null;
		try {
			headers = context.getParameter("headers") != null ? (Map<String, String>) context.getParameter("headers") : null;
		} catch (ClassCastException e) {
		}
		
		HttpClient client = HttpClient.newHttpClient();
	    Builder builder = HttpRequest.newBuilder()
	          .uri(URI.create((String) context.getParameter("url")))
	          .method(((String)context.getParameter("method")).toUpperCase(), BodyPublishers.ofString(body));

	    if (headers != null) {
	    	headers.forEach((key, value) -> {
	    		builder.header(key, value);
	    	});
	    }
	    
	    HttpRequest request = builder.build();
	    try {
			final HttpResponse<byte[]> response =
			      client.send(request, BodyHandlers.ofByteArray());
			
			final Integer expectedResponseCode = (Integer) context.getParameter("expectedResponseCode");
			if (expectedResponseCode != null && expectedResponseCode != response.statusCode()) {
				throw new KaiserException("Received code was " + response.statusCode() + " but expected code is " + expectedResponseCode, 400);
			}
			
			return new HttpResponseResult(response);
		} catch (IOException | InterruptedException e) {
			throw new KaiserException(e.getMessage(), 400);
		}
	    
	}

}
