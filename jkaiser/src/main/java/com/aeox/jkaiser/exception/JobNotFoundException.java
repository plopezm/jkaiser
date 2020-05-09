package com.aeox.jkaiser.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.aeox.jkaiser.core.exception.RuntimeKaiserException;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class JobNotFoundException extends RuntimeKaiserException {
	private static final long serialVersionUID = 1142160553766225494L;

	public JobNotFoundException() {
		super("Job does not exist");
	}
}
