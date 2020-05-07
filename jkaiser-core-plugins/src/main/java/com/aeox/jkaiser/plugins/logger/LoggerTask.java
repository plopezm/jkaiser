package com.aeox.jkaiser.plugins.logger;
import java.util.HashMap;
import java.util.Map;

import com.aeox.jkaiser.core.JobContext;
import com.aeox.jkaiser.core.ParameterMappings;
import com.aeox.jkaiser.core.ParameterType;
import com.aeox.jkaiser.core.Result;
import com.aeox.jkaiser.core.Task;
import com.aeox.jkaiser.core.exception.KaiserException;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class LoggerTask extends Task<String>{
	
	public LoggerTask() {
		super();
	}
	
	public LoggerTask(ParameterMappings mappings) {
		super(mappings);
	}
	
	@Override
	public Result<String> onCall(JobContext context) throws KaiserException {
		log.info("[{}] {}", context.getJobName(), context.getParameter("msg"));
		return new Result<String>() {

			@Override
			public String getResult() {
				return (String) context.getParameter("msg").toString();
			}

			@Override
			public boolean wasError() {
				return false;
			}
		};
	}

	@Override
	public String getName() {
		return "jkaiser-logger";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public String getDescription() {
		return "Logs all parameters stored in the context";
	}

	@Override
	public Map<String, ParameterType> getRequiredParameters() {
		final Map<String, ParameterType> params = new HashMap<>();
		params.put("msg", ParameterType.STRING);
		return params;
	}

	@Override
	public Map<String, ParameterType> getOptionalParameters() {
		return new HashMap<>();
	}	
}
