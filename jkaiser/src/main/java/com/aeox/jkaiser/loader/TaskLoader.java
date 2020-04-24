package com.aeox.jkaiser.loader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.aeox.jkaiser.core.Task;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class TaskLoader {

	public static final String pluginsFolder = "./plugins";
	
	private Map<String, Task<?>> tasks;

	public TaskLoader() {
		final File file = new File(pluginsFolder);
		file.mkdir();
		tasks = new ConcurrentHashMap<String, Task<?>>();
	}

	@Scheduled(initialDelay = 0, fixedDelay = 60000)
	public void loadTasks() throws IOException {
		log.info("Loading new jars from folder...");
		final List<File> paths = this.getJarPlugins();
		paths.forEach((jarFile) -> {
			log.info("Checking jar {}", jarFile);
			try {
				this.loadPlugin(jarFile).forEach((task) -> {
					final String taskName = task.getName()+":"+task.getVersion(); 				
					if (!this.tasks.containsKey(taskName)) {
						this.tasks.put(taskName, task);
					}					
				});
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException | IOException e) {
				e.printStackTrace();
			}
		});
	}

	private List<File> getJarPlugins() throws IOException {
		try (Stream<Path> walk = Files.walk(Paths.get(pluginsFolder))) {
			return walk.filter((path) -> {
				return Files.isRegularFile(path)
						&& new File(path.toAbsolutePath().toString()).getAbsolutePath().endsWith(".jar");
			}).map(path -> {
				return new File(path.toAbsolutePath().toString());
			}).collect(Collectors.toList());
		} catch (IOException e) {
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	private List<Task<?>> loadPlugin(final File file) throws IOException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		final List<Task<?>> pluginTasks = new LinkedList<>();
		try (final JarFile jarFile = new JarFile(file)) {
			final ClassLoader loader = URLClassLoader.newInstance(new URL[] { file.toURI().toURL() },
					getClass().getClassLoader());

			final Enumeration<JarEntry> allEntries = jarFile.entries();
			while (allEntries.hasMoreElements()) {
				JarEntry entry = (JarEntry) allEntries.nextElement();
				String name = entry.getName();
				if (!entry.isDirectory() && name.endsWith(".class")) {
					final String className = name.substring(0, name.lastIndexOf('.')).replace('/', '.');
					Class<? extends Task<?>> cls;

					try {
						cls = (Class<? extends Task<?>>) Class.forName(className, true, loader);						
					} catch (ClassNotFoundException | ClassCastException e) {
						continue;
					}

					if ((cls.getModifiers() & Modifier.ABSTRACT) != 0) { // Only instanciable classes
						continue;
					}

					if (!Task.class.isAssignableFrom(cls)) {
						continue;
					}

					final Constructor<? extends Task<?>> defaultConstructor = cls.getDeclaredConstructor();
					if (defaultConstructor == null) {
						continue;
					}

					pluginTasks.add(cls.getDeclaredConstructor().newInstance());
					log.info("Added class: {}", className);
				}
			}
		}
		return pluginTasks;
	}
	
	
	public Task<?> findTask(String id) {
		return this.tasks.get(id);
	}
}
