package com.aeox.jkaiser.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.aeox.jkaiser.core.exception.ParameterNotFoundException;

import lombok.Data;
import lombok.Setter;

@Data
public class Job {
	private String name;
	private String version;
	private TaskTreeNode entrypoint;
	private Status status;
	@Setter
	private Map<String, String> parameters;
	
	public Job(String name, String version, TaskTreeNode entrypoint) {
		super();
		this.name = name;
		this.version = version;
		this.entrypoint = entrypoint;
		this.parameters = new HashMap<>();
	}
	
	public List<Result<?>> run(final JobContext context) throws ParameterNotFoundException {
		List<String> parametersErrorList = this.checkParameters(context);
		if (!parametersErrorList.isEmpty()) {
			throw new ParameterNotFoundException(parametersErrorList);
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
	
	private List<String> checkParameters(final JobContext context) {
		List<String> errors = new LinkedList<String>();
		for(final String param : this.parameters.keySet()) {
			if (!context.containsParameter(param)) {
				errors.add(param);
			}
		}
		return errors;
	}
}
