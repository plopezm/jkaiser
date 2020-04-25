package com.aeox.jkaiser.core.result;

import java.net.http.HttpResponse;

import com.aeox.jkaiser.core.Result;

public class HttpResponseResult implements Result<HttpContent> {

	private HttpContent result;
	private boolean isError;

	public HttpResponseResult(final HttpResponse<byte[]> response) {
		this.result = new HttpContent(response.statusCode(), response.headers().map(), response.body());
		this.isError = false;
	}

	public HttpResponseResult(final HttpResponse<byte[]> response, boolean isError) {
		this.result = new HttpContent(response.statusCode(), response.headers().map(), response.body());
		this.isError = isError;
	}

	@Override
	public HttpContent getResult() {
		return result;
	}

	@Override
	public boolean wasError() {
		return isError;
	}

}
