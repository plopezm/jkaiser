package com.aeox.jkaiser.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class JobContext {
	private static final SpelExpressionParser parser = new SpelExpressionParser();
	private static final String PARAMS = "$params.";
	private static final String PREV_RESULT = "$result.";
	
	private String jobName;
	private Map<String, Object> params;
	private Result<?> previousResult;
	
	public JobContext() {
		this.jobName = null;
		this.previousResult = null;
		this.params = new HashMap<>();
	}
	
	public JobContext(final Map<String, Object> params) {
		this.jobName = null;
		this.previousResult = null;
		this.params = params;
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
	
	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public void applyMappings(final Map<String, Object> mappings) {
		mappings.forEach((key, value) -> {
			this.params.put(key, mapValues(value));
		});
	}
	
	private Object mapValues(final Object target) {
		if (this.isPrimitive(target)) {
			return target;
			
		}
		if (target instanceof String) {
			return this.getValue((String) target);
		} else if (target instanceof List<?>) {
			List<?> listTarget = (List<?>) target;
			return listTarget.stream().map((item) -> {
				return this.mapValues(item);
			}).collect(Collectors.toList());
		} else if (target instanceof Map<?,?>) {
			Map<?, ?> mapTarget = (Map<?, ?>) target;
			return mapTarget.entrySet().stream().map((entry) -> {
				return new DefaultMapEntry<Object, Object>(entry.getKey(), this.getValue((String) target));
			}).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		}
		return target;
	}
	
	private Object getValue(final String exp) {
		String strValue = null;
		if (exp instanceof String) {
			strValue = (String) exp;
		}
		if (strValue == null) {
			return exp;
		}		
		
		EvaluationContext context;
		if (strValue.startsWith(PREV_RESULT)) {
			strValue = strValue.substring(PREV_RESULT.length());
			context = new StandardEvaluationContext(this.previousResult.getResult());
		} else if (strValue.startsWith(PARAMS)) {
			strValue = strValue.substring(PARAMS.length());
			context = new StandardEvaluationContext(this.params);
		} else {
			return exp;
		}		
		final Expression expResult = parser.parseExpression(strValue);
		return expResult.getValue(context);
	}
	
	private boolean isPrimitive(final Object obj) {
		return obj.getClass().isPrimitive() || getWrapperTypes().contains(obj);
	}
	
	private static Set<Class<?>> getWrapperTypes()
    {
        Set<Class<?>> ret = new HashSet<Class<?>>();
        ret.add(Boolean.class);
        ret.add(Character.class);
        ret.add(Byte.class);
        ret.add(Short.class);
        ret.add(Integer.class);
        ret.add(Long.class);
        ret.add(Float.class);
        ret.add(Double.class);
        ret.add(Void.class);
        return ret;
    }
		
	final class DefaultMapEntry<K, V> implements Map.Entry<K, V> {
	    private final K key;
	    private V value;

	    public DefaultMapEntry(K key, V value) {
	        this.key = key;
	        this.value = value;
	    }

	    @Override
	    public K getKey() {
	        return key;
	    }

	    @Override
	    public V getValue() {
	        return value;
	    }

	    @Override
	    public V setValue(V value) {
	        V old = this.value;
	        this.value = value;
	        return old;
	    }
	}
}
