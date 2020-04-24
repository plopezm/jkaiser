package com.aeox.jkaiser.plugins.logger;
import java.util.HashMap;
import java.util.Map;

import com.aeox.jkaiser.core.JobContext;
import com.aeox.jkaiser.core.ParameterType;
import com.aeox.jkaiser.core.Result;
import com.aeox.jkaiser.core.Task;
import com.aeox.jkaiser.core.exception.KaiserException;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class LoggerTask extends Task<String>{
	
	@Override
	public Result<String> onCall(JobContext context) throws KaiserException {
		context.getKeys().forEach((key) -> log.info("Key {} - Value {}", key, context.getParameter(key)));
		return new Result<String>() {

			@Override
			public String getResult() {
				return context.getKeys().stream().map((key) -> key + ":" + context.getParameter(key)).toString();
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
}
