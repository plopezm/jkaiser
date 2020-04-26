package com.aeox.jkaiser.engine;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.aeox.jkaiser.core.Job;
import com.aeox.jkaiser.core.JobContext;
import com.aeox.jkaiser.core.ParameterMappings;
import com.aeox.jkaiser.core.Result;
import com.aeox.jkaiser.core.Task;
import com.aeox.jkaiser.core.TaskTreeNode;
import com.aeox.jkaiser.core.exception.ParameterNotFoundException;
import com.aeox.jkaiser.loader.TaskClassLoader;

import lombok.extern.log4j.Log4j2;

@Log4j2
class JobEngineTest {
	
	final TaskClassLoader loader = new TaskClassLoader();
	final JobEngine engine = new JobEngine();

	@BeforeEach
	void setUp() throws Exception {
		loader.loadTasks();
		
	}

	@Test
	void testRunErrorInFirstTaskBecauseParametersNotFound() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ParameterNotFoundException {		
		Task<?> httpTask = loader.findTaskClass("jkaiser-http:1.0").getDeclaredConstructor().newInstance();	
		Task<?> loggerTask = loader.findTaskClass("jkaiser-logger:1.0").getDeclaredConstructor().newInstance();		
		
		TaskTreeNode onFailureTreeNode = new TaskTreeNode(loggerTask);
		TaskTreeNode onSuccessTreeNode = new TaskTreeNode(loggerTask);

		TaskTreeNode entrypoint = new TaskTreeNode(httpTask);
		entrypoint.setOnSuccess(onSuccessTreeNode);
		entrypoint.setOnFailure(onFailureTreeNode);
		
		final Job testJob = new Job("testjob", "1.0", entrypoint);		
		final JobContext jobContext = new JobContext();
		List<Result<?>> results = engine.run(jobContext, testJob);
		
		results.forEach((result) -> {
			log.info("Result: {}", result);
		});
		assertTrue(results.get(0).wasError());
		assertTrue(results.get(1).wasError());
	}
	
	@Test
	void testRunErrorInSecondTaskBecauseParametersNotFound() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ParameterNotFoundException {		
		Task<?> httpTask = loader.findTaskClass("jkaiser-http:1.0").getDeclaredConstructor().newInstance();	
		Task<?> loggerTask = loader.findTaskClass("jkaiser-logger:1.0").getDeclaredConstructor().newInstance();		
		
		TaskTreeNode onFailureTreeNode = new TaskTreeNode(loggerTask);
		TaskTreeNode onSuccessTreeNode = new TaskTreeNode(loggerTask);

		TaskTreeNode entrypoint = new TaskTreeNode(httpTask);
		entrypoint.setOnSuccess(onSuccessTreeNode);
		entrypoint.setOnFailure(onFailureTreeNode);
		
		final Job testJob = new Job("testjob", "1.0", entrypoint);		
		final JobContext jobContext = new JobContext();
		jobContext.addParameter("url", "https://postman-echo.com/get?foo1=bar1&foo2=bar2");
		jobContext.addParameter("method", "get");
		jobContext.addParameter("expectedResponseCode", 200);
		jobContext.addParameter("body", "");
				
		
		List<Result<?>> results = engine.run(jobContext, testJob);

		results.forEach((result) -> {
			log.info("Result: {}", result.getResult());
		});
		assertFalse(results.get(0).wasError());
		assertTrue(results.get(1).wasError());
	}
	
	@Test
	void testRunOKResponseIsMapped() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ParameterNotFoundException {		
		Task<?> httpTask = loader.findTaskClass("jkaiser-http:1.0").getDeclaredConstructor().newInstance();	
		Task<?> loggerTask = loader.findTaskClass("jkaiser-logger:1.0").getDeclaredConstructor().newInstance();
		final ParameterMappings parameterMappings = new ParameterMappings();
		parameterMappings.put("msg", "$result.new String(body)");		
		Task<?> loggerTaskWithMappings = loader.findTaskClass("jkaiser-logger:1.0").getDeclaredConstructor(ParameterMappings.class).newInstance(parameterMappings);
		
		TaskTreeNode onFailureTreeNode = new TaskTreeNode(loggerTask);
		TaskTreeNode onSuccessTreeNode = new TaskTreeNode(loggerTaskWithMappings);
		TaskTreeNode entrypoint = new TaskTreeNode(httpTask);
		
		entrypoint.setOnSuccess(onSuccessTreeNode);
		entrypoint.setOnFailure(onFailureTreeNode);
		
		final Job testJob = new Job("testjob", "1.0", entrypoint);		
		final JobContext jobContext = new JobContext();
		jobContext.addParameter("url", "https://postman-echo.com/get?foo1=bar1&foo2=bar2");
		jobContext.addParameter("method", "get");				
		
		List<Result<?>> results = engine.run(jobContext, testJob);

		results.forEach((result) -> {
			log.info("Result: {}", result.getResult());
		});
		assertFalse(results.get(0).wasError());
		assertFalse(results.get(1).wasError());
	}

}
