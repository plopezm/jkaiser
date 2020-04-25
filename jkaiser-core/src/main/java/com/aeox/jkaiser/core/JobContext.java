package com.aeox.jkaiser.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class JobContext {
	private static final SpelExpressionParser parser = new SpelExpressionParser();
	
	private Map<String, Object> params;
	private Result<?> previousResult;
	
	public JobContext() {
		this.params = new HashMap<>();
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
	
	public void applyMappings(final Map<String, String> mappings) {
		mappings.forEach((key, value) -> {
			if (value.startsWith("$")) {
				this.params.put(key, this.getValue(value, this.previousResult));
			} else {
				this.params.put(key, value);
			}
		});
	}
	
	private Object getValue(String exp, Object target) {
		final EvaluationContext context = new StandardEvaluationContext(target);
		final Expression expResult = parser.parseExpression(exp);
		return expResult.getValue(context);
	}
	
	
		
}
