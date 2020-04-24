package com.aeox.jkaiser.core.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class KaiserException extends Exception {
	private static final long serialVersionUID = -2177918427795427718L;
	
	private String msg;
	private int httpCode;
}
