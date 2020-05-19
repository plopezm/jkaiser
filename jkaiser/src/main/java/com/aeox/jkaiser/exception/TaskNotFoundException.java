package com.aeox.jkaiser.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.aeox.jkaiser.core.exception.RuntimeKaiserException;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class TaskNotFoundException extends RuntimeKaiserException {
	private static final long serialVersionUID = -2265854344570073367L;

	public TaskNotFoundException() {
		super("Task does not exist");
	}
}
