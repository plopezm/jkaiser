package com.aeox.jkaiser.service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aeox.jkaiser.core.Job;
import com.aeox.jkaiser.core.JobContext;
import com.aeox.jkaiser.core.ParameterMappings;
import com.aeox.jkaiser.core.Result;
import com.aeox.jkaiser.core.Task;
import com.aeox.jkaiser.core.TaskTreeNode;
import com.aeox.jkaiser.core.exception.ParameterNotFoundException;
import com.aeox.jkaiser.engine.JobEngine;
import com.aeox.jkaiser.entity.DbJob;
import com.aeox.jkaiser.entity.DbJobId;
import com.aeox.jkaiser.entity.DbTaskNode;
import com.aeox.jkaiser.exception.JobNotFoundException;
import com.aeox.jkaiser.exception.TaskNotFoundException;
import com.aeox.jkaiser.loader.TaskClassLoader;

@Service
public class EngineService {
	private TaskClassLoader pluginLoader;
	private DbJobService jobService;
	private JobEngine engine;
	
	@Autowired
	public EngineService(JobEngine engine, DbJobService jobService, TaskClassLoader pluginLoader) {
		super();
		this.engine = engine;
		this.jobService = jobService;
		this.pluginLoader = pluginLoader;
	}
	
	public List<Result<?>> executeJob(final JobContext context, final String name, final String version) throws ParameterNotFoundException {
		final DbJob job = this.jobService.getById(new DbJobId(name, version));
		if (job == null) {
			throw new JobNotFoundException();
		}
		return this.engine.run(context, this.buildJob(job));
	}
	
	protected Job buildJob(final DbJob dbJob) {
		return new Job(dbJob.getName(), dbJob.getVersion(), buildTaskTree(dbJob.getEntrypoint()));
	}
	
	private TaskTreeNode buildTaskTree(final DbTaskNode dbNode) {
		Task<?> task;
		try {
			ParameterMappings mappings = null;			
			if (dbNode.getMappings() != null) {
				mappings = (ParameterMappings) dbNode.getMappings();
			}
			task = this.pluginLoader.getTaskInstanceByTaskId(dbNode.getComposedId(), mappings);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			throw new TaskNotFoundException();
		}		
		final TaskTreeNode node = new TaskTreeNode(task);

		if (dbNode.getOnSuccess() != null) {
			node.setOnSuccess(this.buildTaskTree(dbNode.getOnSuccess()));
		}
		
		if (dbNode.getOnFailure() != null) {
			node.setOnFailure(this.buildTaskTree(dbNode.getOnFailure()));
		}
		
		return node;
	}

	public void setPluginLoader(TaskClassLoader pluginLoader) {
		this.pluginLoader = pluginLoader;
	}
}
