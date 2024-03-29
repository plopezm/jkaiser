package com.aeox.jkaiser.core;

import lombok.Data;

@Data
public class TaskTreeNode {
	private Task<?> current;
	private TaskTreeNode onSuccess;
	private TaskTreeNode onFailure;
	
	public TaskTreeNode(Task<?> current) {
		super();
		this.current = current;
	}
	
}
