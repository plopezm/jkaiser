package com.aeox.jkaiser.engine;

import java.util.List;

import org.springframework.stereotype.Service;

import com.aeox.jkaiser.core.Job;
import com.aeox.jkaiser.core.JobContext;
import com.aeox.jkaiser.core.Result;
import com.aeox.jkaiser.core.exception.ParameterNotFoundException;

@Service
public class JobEngine {

	public List<Result<?>> run(final JobContext context, final Job job) throws ParameterNotFoundException {
		return job.run(context);
	}
	
}
