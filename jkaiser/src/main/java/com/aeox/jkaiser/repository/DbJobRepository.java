package com.aeox.jkaiser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aeox.jkaiser.entity.DbJob;
import com.aeox.jkaiser.entity.DbJobId;

@Repository
public interface DbJobRepository extends JpaRepository<DbJob, DbJobId>{
}
