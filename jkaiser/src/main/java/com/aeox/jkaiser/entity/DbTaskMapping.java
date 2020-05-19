package com.aeox.jkaiser.entity;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;

@Entity
@Data
public class DbTaskMapping {
	@Id 
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private UUID id;
	private String key;
	private Object value;
}
