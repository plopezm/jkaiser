package com.aeox.jkaiser.entity;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DbTaskNode {
	@Id 
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private UUID id;
	private String name;
	private String version;
	@ManyToOne(cascade = CascadeType.REMOVE)
	private DbTaskNode onSuccess;
	@ManyToOne(cascade = CascadeType.REMOVE)
	private DbTaskNode onFailure;
}
