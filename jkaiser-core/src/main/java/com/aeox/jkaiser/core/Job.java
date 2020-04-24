package com.aeox.jkaiser.core;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.aeox.jkaiser.core.exception.ParameterNotFoundException;

import lombok.Data;

@Data
public abstract class Job {
	private String name;
	private String version;
	private TaskTreeNode entrypoint;
	private Status status;
	private Map<String, String> parameters;
	
	public List<Result<?>> run(final JobContext context) throws ParameterNotFoundException {
		if (!checkParameters(context)) {
			throw new ParameterNotFoundException();
		}
		
		final List<Result<?>> results = new LinkedList<>();
		TaskTreeNode taskToExecute = this.entrypoint;	
		while(taskToExecute != null) {
			final Result<?> result = taskToExecute.getCurrent().run(context);
			results.add(result);
			if (result.wasError()) {
				taskToExecute = taskToExecute.getOnFailure();
			} else {
				context.setPreviousResult(result);
				taskToExecute = taskToExecute.getOnSuccess();
			}
		}
		return results;		
	}
	
	private boolean checkParameters(final JobContext context) {
		for(final String param : this.parameters.keySet()) {
			if (!context.containsParameter(param)) {
				return false;
			}
		}
		return true;
	}
}
