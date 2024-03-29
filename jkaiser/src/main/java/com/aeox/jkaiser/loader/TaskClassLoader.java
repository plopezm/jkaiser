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
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.aeox.jkaiser.core.ParameterMappings;
import com.aeox.jkaiser.core.Task;
import com.aeox.jkaiser.exception.TaskNotFoundException;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class TaskClassLoader {

	public static final String pluginsFolder = "./plugins";
	
	private Map<String, Class<? extends Task<?>>> taskClasses;

	public TaskClassLoader() throws IOException {
		final File file = new File(pluginsFolder);
		file.mkdir();
		taskClasses = new ConcurrentHashMap<>();
		this.scan();
	}
	
	public Class<? extends Task<?>> findTaskClass(final String id) {
		return this.taskClasses.get(id);
	}
	
	public Task<?> getTaskInstanceByTaskId(final String id, final ParameterMappings parameterMappings) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		final Class<? extends Task<?>> clazz = this.taskClasses.get(id);
		if (clazz == null) {
			throw new TaskNotFoundException();
		}
		if (parameterMappings != null) {
			return clazz.getDeclaredConstructor(ParameterMappings.class).newInstance(parameterMappings);
		} else {
			return clazz.getDeclaredConstructor().newInstance();
		}
	}

	public void scan() throws IOException {
		log.info("Loading new jars from folder...");
		final List<File> paths = this.getJarPlugins();
		paths.forEach((jarFile) -> {
			log.info("Checking jar {}", jarFile);
			try {
				this.loadPlugin(jarFile).forEach((taskClass) -> {
					Task<?> taskInstance;
					try {
						taskInstance = taskClass.getDeclaredConstructor().newInstance();
						final String taskName = taskInstance.getName()+":"+taskInstance.getVersion(); 				
						this.taskClasses.put(taskName, taskClass);
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException | NoSuchMethodException | SecurityException e) {
						log.error("Class {} has not a default constructor and it is mandatory", taskClass.getName());
						e.printStackTrace();
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
	private List<Class<? extends Task<?>>> loadPlugin(final File file) throws IOException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		final List<Class<? extends Task<?>>> pluginTasks = new LinkedList<>();
		try (final JarFile jarFile = new JarFile(file)) {
			final ClassLoader loader = URLClassLoader.newInstance(new URL[] { file.toURI().toURL() },
					getClass().getClassLoader());
			
			final Enumeration<JarEntry> allEntries = jarFile.entries();
			while (allEntries.hasMoreElements()) {
				JarEntry entry = (JarEntry) allEntries.nextElement();
				String name = entry.getName();
				if (!entry.isDirectory() && name.endsWith(".class")) {
					final String className = name.substring(0, name.lastIndexOf('.')).replace('/', '.');
					Class<?> cls;
					try {
						cls = Class.forName(className, true, loader);						
					} catch (ClassNotFoundException | ClassCastException | NoClassDefFoundError e) {
						continue;
					}

					if (this.isDriverClass(cls)) {
						try {
							loader.loadClass(className);
						} catch (ClassNotFoundException e1) {
							e1.printStackTrace();
						}
						Driver driver = (Driver) cls.getDeclaredConstructor().newInstance();
						try {
							DriverManager.registerDriver(new DriverShim(driver));
						} catch (SQLException e) {
							e.printStackTrace();
						}
						log.info("Driver {} has been added to DriverManager", driver.getClass().getName());
						continue;
					}					
					
					if (this.isTaskClass(cls)) {
						pluginTasks.add((Class<? extends Task<?>>) cls);
						log.info("Task class added: {}", className);
					}
				}
			}
		}
		return pluginTasks;
	}
		
	private boolean isDriverClass(final Class<?> cls) throws NoSuchMethodException, SecurityException {
		if ((cls.getModifiers() & Modifier.ABSTRACT) != 0) { // Only instanciable classes
			return false;
		}

		if (!Driver.class.isAssignableFrom(cls)) {
			return false;
		}
		return true;
	}
	
	private boolean isTaskClass(final Class<?> cls) throws NoSuchMethodException, SecurityException {
		if ((cls.getModifiers() & Modifier.ABSTRACT) != 0) { // Only instanciable classes
			return false;
		}

		if (!Task.class.isAssignableFrom(cls)) {
			return false;
		}

		final Constructor<?> defaultConstructor = (Constructor<?>) cls.getDeclaredConstructor();
		if (defaultConstructor == null) {
			return false;
		}
		return true;
	}

	public List<Task<?>> getRegisteredTasks() {
		return this.taskClasses.entrySet().stream()
		.filter((entry) -> {
			try {
				return entry.getValue() != null && entry.getValue().getDeclaredConstructor() != null;
			} catch (NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
				return false;
			}
		})
		.map((entry) -> {
			try {
				return entry.getValue().getDeclaredConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
				return null;
			}
		}).collect(Collectors.toList());
	}
}
