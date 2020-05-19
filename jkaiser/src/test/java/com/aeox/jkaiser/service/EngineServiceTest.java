package com.aeox.jkaiser.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.aeox.jkaiser.core.Job;
import com.aeox.jkaiser.engine.JobEngine;
import com.aeox.jkaiser.entity.DbJob;
import com.aeox.jkaiser.entity.DbTaskNode;
import com.aeox.jkaiser.exception.TaskNotFoundException;
import com.aeox.jkaiser.loader.TaskClassLoader;

@RunWith(MockitoJUnitRunner.class)
public class EngineServiceTest {
	
	private TaskClassLoader pluginLoader;
	@Mock
	private DbJobService jobService;
	@Mock
	private JobEngine engine;
	
	@InjectMocks
	private EngineService underTest;

	@Before
	public void setUp() throws Exception {
		pluginLoader = new TaskClassLoader();
		pluginLoader.scanTaskPlugins();
		this.underTest.setPluginLoader(pluginLoader);
	}

	@Test(expected = TaskNotFoundException.class)
	public void buildJobTaskNotFound() {
		final DbJob dbJob = new DbJob();
		dbJob.setName("buildJobTaskNotFound");
		dbJob.setVersion("1.0");
		
		dbJob.setEntrypoint(new DbTaskNode(UUID.randomUUID(), "testTask", "1.0", null, null, null));
		
		this.underTest.buildJob(dbJob);
	}


	@Test(expected = TaskNotFoundException.class)
	public void buildJobTaskNotFound2() {
		final DbJob dbJob = new DbJob();
		dbJob.setName("buildJobTaskNotFound");
		dbJob.setVersion("1.0");
		
		final DbTaskNode fakeNode = new DbTaskNode(UUID.randomUUID(), "testTask", "2.0", null, null, null);	
		final DbTaskNode entry = new DbTaskNode(UUID.randomUUID(), "jkaiser-logger", "1.0", null, null, null);
		entry.setOnSuccess(fakeNode);
		entry.setOnFailure(fakeNode);
		
		dbJob.setEntrypoint(entry);
		
		this.underTest.buildJob(dbJob);
	}
	

	@Test
	public void buildJob() {
		final DbJob dbJob = new DbJob();
		dbJob.setName("buildJobTaskNotFound");
		dbJob.setVersion("1.0");
		
		final DbTaskNode entry = new DbTaskNode(UUID.randomUUID(), "jkaiser-logger", "1.0", null, null, null);
		
		dbJob.setEntrypoint(entry);
		
		final Job jobBuilt = this.underTest.buildJob(dbJob);
		assertNotNull(jobBuilt.getEntrypoint());
		assertEquals(0, jobBuilt.getParameters().size());
		assertEquals("jkaiser-logger", jobBuilt.getEntrypoint().getCurrent().getName());
		assertNull(jobBuilt.getEntrypoint().getOnSuccess());
		assertNull(jobBuilt.getEntrypoint().getOnFailure());
	}

	@Test
	public void buildJobWithChilds() {
		final DbJob dbJob = new DbJob();
		dbJob.setName("buildJobTaskNotFound");
		dbJob.setVersion("1.0");
		
		final DbTaskNode entry = new DbTaskNode(UUID.randomUUID(), "jkaiser-logger", "1.0", null, null, null);
		final DbTaskNode other = new DbTaskNode(UUID.randomUUID(), "jkaiser-logger", "1.0", null, null, null);
		dbJob.setEntrypoint(entry);
		entry.setOnFailure(other);
		entry.setOnSuccess(other);
		
		final Job jobBuilt = this.underTest.buildJob(dbJob);
		assertNotNull(jobBuilt.getEntrypoint());
		assertEquals(0, jobBuilt.getParameters().size());
		assertEquals("jkaiser-logger", jobBuilt.getEntrypoint().getCurrent().getName());
		assertNotNull(jobBuilt.getEntrypoint().getOnSuccess());
		assertNotNull(jobBuilt.getEntrypoint().getOnFailure());
		assertNotNull(jobBuilt.getEntrypoint().getOnSuccess().getCurrent());
		assertNotNull(jobBuilt.getEntrypoint().getOnFailure().getCurrent());
		assertNull(jobBuilt.getEntrypoint().getOnSuccess().getOnSuccess());
		assertNull(jobBuilt.getEntrypoint().getOnFailure().getOnFailure());
	}
}
