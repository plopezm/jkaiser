package com.aeox.jkaiser.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aeox.jkaiser.core.exception.KaiserException;
import com.aeox.jkaiser.core.exception.RuntimeKaiserException;
import com.aeox.jkaiser.entity.DbJob;
import com.aeox.jkaiser.entity.DbJobId;
import com.aeox.jkaiser.entity.DbTaskNode;
import com.aeox.jkaiser.exception.JobAlreadyExistsException;
import com.aeox.jkaiser.repository.DbJobRepository;
import com.aeox.jkaiser.repository.DbTaskNodeRepository;

@Service
public class DbJobService {
	private DbJobRepository dbJobRepository;
	private DbTaskNodeRepository dbTaskNodeRepository;

	@Autowired
	public DbJobService(final DbJobRepository dbJobRepository, final DbTaskNodeRepository dbTaskNodeRepository) {
		super();
		this.dbJobRepository = dbJobRepository;
		this.dbTaskNodeRepository = dbTaskNodeRepository;
	}

	@Transactional
	public DbJob create(final DbJob job) {
		final Optional<DbJob> existingJob = this.dbJobRepository.findById(new DbJobId(job.getName(), job.getVersion()));
		if (!existingJob.isPresent()) {
			throw new JobAlreadyExistsException();
		}
		createTaskNode(job.getEntrypoint());
		return this.dbJobRepository.save(job);
	}
		
	private DbTaskNode createTaskNode(final DbTaskNode node) {
		if (node.getOnSuccess() != null) {
			createTaskNode(node.getOnSuccess());
		}
		if (node.getOnFailure() != null) {
			createTaskNode(node.getOnFailure());
		}		
		return this.dbTaskNodeRepository.save(node);
	}
	
	public List<DbJob> getAll() {
		return this.dbJobRepository.findAll();
	}	
	
}
