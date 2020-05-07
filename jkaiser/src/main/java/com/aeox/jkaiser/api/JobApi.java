package com.aeox.jkaiser.api;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aeox.jkaiser.entity.DbJob;
import com.aeox.jkaiser.service.DbJobService;

@RestController
@RequestMapping(path = "/jobs")
public class JobApi {
	private DbJobService dbJobService;
	
	@Autowired
	public JobApi(final DbJobService dbJobService) {
		this.dbJobService = dbJobService;
	}

	@GetMapping
	public List<DbJob> getJobs() {
		return this.dbJobService.getAll();
	}
	
	@PostMapping
	public DbJob createJob(@Valid @RequestBody final DbJob job) {
		return this.dbJobService.create(job);
	}
	
}
