package com.aeox.jkaiser.api;

import java.io.IOException;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aeox.jkaiser.core.Task;
import com.aeox.jkaiser.loader.TaskClassLoader;

@RestController
@RequestMapping(path = "/tasks")
public class TaskApi {

	private TaskClassLoader loader;
	
	public TaskApi(TaskClassLoader loader) {
		super();
		this.loader = loader;
	}

	@GetMapping
	public List<Task<?>> getAvailableTasks() {
		return loader.getRegisteredTasks();
	}
	
	@PostMapping
	public void loadPlugins() throws IOException {
		loader.scan();
	}
	
}
