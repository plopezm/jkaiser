package com.aeox.jkaiser.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@IdClass(DbJobId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DbJob {
	@Id
	private String name;
	@Id
	private String version;
	private String description;
	@NotNull
	@ManyToOne(cascade = CascadeType.REMOVE)
	private DbTaskNode entrypoint;
	
	public String getComposedId() {
		return name + ":" + version;
	}
}
