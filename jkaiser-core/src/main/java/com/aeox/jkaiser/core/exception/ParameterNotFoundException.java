package com.aeox.jkaiser.core.exception;

public class ParameterNotFoundException extends KaiserException {
	private static final long serialVersionUID = 6167386626150587080L;
	
	public ParameterNotFoundException() {
		super("Required parameter not found in the context", 400);
	}
}
