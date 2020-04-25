package com.aeox.jkaiser.core.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class KaiserException extends Exception {
	private static final long serialVersionUID = -2177918427795427718L;
	
	private int httpCode;
	
	public KaiserException(String msg, int httpCode) {
		super(msg);
		this.httpCode = httpCode;
	}
	
	
}
