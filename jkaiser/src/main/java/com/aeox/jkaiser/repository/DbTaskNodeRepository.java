package com.aeox.jkaiser.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aeox.jkaiser.entity.DbTaskNode;

public interface DbTaskNodeRepository extends JpaRepository<DbTaskNode, UUID>{
}
