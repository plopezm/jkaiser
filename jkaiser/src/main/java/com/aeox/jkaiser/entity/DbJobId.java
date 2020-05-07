package com.aeox.jkaiser.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DbJobId implements Serializable {
	private static final long serialVersionUID = 632002373550832283L;
	private String name;
	private String version;
}
