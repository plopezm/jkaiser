package com.aeox.jkaiser.core.exception;

import java.util.List;
import java.util.stream.Collectors;

public class ParameterNotFoundException extends KaiserException {
	private static final long serialVersionUID = 6167386626150587080L;
	
	public ParameterNotFoundException(List<String> params) {
		super("Required parameter not found in the context: " + params.stream().collect(Collectors.joining(",")), 400);
	}
}
