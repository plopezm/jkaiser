package com.aeox.jkaiser.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.aeox.jkaiser.core.exception.KaiserException;
import com.aeox.jkaiser.core.exception.ParameterNotFoundException;
import com.aeox.jkaiser.core.result.ExceptionResult;

import lombok.Data;

@Data
public abstract class Task<R> {
	
	private Map<String, String> mappings;
	
	public Task() {
		this.mappings = new HashMap<>();
	}
	
	public Task(final Map<String, String> mappings) {
		this.mappings = mappings;
	}
	
	public abstract String getName();
	
	public abstract String getVersion();
	
	public abstract String getDescription();
	
	public abstract Map<String, ParameterType> getRequiredParameters(); 
	
	public abstract Result<R> onCall(final JobContext context) throws KaiserException;
	
	public List<String> checkParameters(final JobContext context) {
		List<String> errors = new LinkedList<>();
		for (String paramKey : this.getRequiredParameters().keySet()) {
			if (!context.containsParameter(paramKey)) {
				errors.add(paramKey);
			}
		}
		return errors;
	}
	
	public Result<?> run(final JobContext context) {
		Result<?> result;
		try {
			context.applyMappings(this.mappings);
			List<String> parametersErrorList = checkParameters(context);
			if (!parametersErrorList.isEmpty()) {
				throw new ParameterNotFoundException(parametersErrorList);
			}
			result = this.onCall(context);
		} catch (KaiserException e) {
			result = new ExceptionResult(e.getMessage()); 
		}
		return result;
	}
}
