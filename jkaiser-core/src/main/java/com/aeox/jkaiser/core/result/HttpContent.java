package com.aeox.jkaiser.core.result;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HttpContent {
	private int status;
	private Map<String, List<String>> headers;
	private byte[] body;		
}
