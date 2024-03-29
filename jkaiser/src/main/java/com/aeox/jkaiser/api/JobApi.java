package com.aeox.jkaiser.api;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aeox.jkaiser.core.JobContext;
import com.aeox.jkaiser.core.Result;
import com.aeox.jkaiser.core.exception.ParameterNotFoundException;
import com.aeox.jkaiser.entity.DbJob;
import com.aeox.jkaiser.entity.DbJobId;
import com.aeox.jkaiser.service.DbJobService;
import com.aeox.jkaiser.service.EngineService;

@RestController
@RequestMapping(path = "/jobs")
public class JobApi {
	private DbJobService dbJobService;
	private EngineService engineService;
	
	@Autowired
	public JobApi(final DbJobService dbJobService, final EngineService engineService) {
		this.dbJobService = dbJobService;
		this.engineService = engineService;
	}

	@GetMapping
	public List<DbJob> getJobs() {
		return this.dbJobService.getAll();
	}

	@GetMapping(path = "/{name}/{version}")
	public DbJob getJobById(@PathVariable final String name, @PathVariable final String version) {
		return this.dbJobService.getById(new DbJobId(name, version));
	}
	
	@PostMapping
	public DbJob createJob(@Valid @RequestBody final DbJob job) {
		return this.dbJobService.create(job);
	}
	
	@DeleteMapping(path = "/{name}/{version}")
	public void deleteJob(@PathVariable final String name, @PathVariable final String version) {
		this.dbJobService.delete(new DbJobId(name, version));
	}
	
	@PostMapping(path = "/{name}/{version}")
	public List<Result<?>> executeJob(@PathVariable final String name, @PathVariable final String version, @RequestBody final Map<String, Object> params) throws ParameterNotFoundException {
		return this.engineService.executeJob(new JobContext(params), name, version);
	}
	
}
