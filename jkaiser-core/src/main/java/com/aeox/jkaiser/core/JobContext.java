package com.aeox.jkaiser.core;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class JobContext {
	private Map<String, Object> params;
	private Result<?> previousResult;
	
	public JobContext() {
		this.params = new ConcurrentHashMap<>();
	}
	
	public Set<String> getKeys() {
		return this.params.keySet();
	}
	
	public void addParameter(final String key, final Object value) {
		this.params.put(key, value);
	}
	
	public Object getParameter(final String key) {
		return this.params.get(key);
	}
	
	public boolean containsParameter(final String key) {
		return this.params.containsKey(key);
	}

	public Result<?> getPreviousResult() {
		return previousResult;
	}

	public void setPreviousResult(Result<?> previousResult) {
		this.previousResult = previousResult;
	}
		
}
