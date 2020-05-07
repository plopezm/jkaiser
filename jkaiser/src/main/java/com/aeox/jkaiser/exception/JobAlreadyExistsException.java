package com.aeox.jkaiser.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.aeox.jkaiser.core.exception.RuntimeKaiserException;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class JobAlreadyExistsException extends RuntimeKaiserException {
	private static final long serialVersionUID = 8246932917279595768L;

	public JobAlreadyExistsException() {
		super("Job already exists");
	}
}
