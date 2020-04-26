package com.aeox.jkaiser.plugins.postgres;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.aeox.jkaiser.core.JobContext;
import com.aeox.jkaiser.core.ParameterType;
import com.aeox.jkaiser.core.Result;
import com.aeox.jkaiser.core.Task;
import com.aeox.jkaiser.core.exception.KaiserException;

public class PostgresInsertTask extends Task<String> {

	@Override
	public String getName() {
		return "jkaiser-postgres";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public String getDescription() {
		return "This task is used to insert data in a postgres database";
	}

	@Override
	public Map<String, ParameterType> getRequiredParameters() {
		final Map<String, ParameterType> params = new HashMap<>();
		return params;
	}

	@Override
	public Map<String, ParameterType> getOptionalParameters() {
		final Map<String, ParameterType> params = new HashMap<>();
		return params;
	}

	@Override
	public Result<String> onCall(JobContext context) throws KaiserException {
		// We register the PostgreSQL driver
        // Registramos el driver de PostgresSQL
        try { 
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ex) {
            System.out.println("Error al registrar el driver de PostgreSQL: " + ex);
        }
		Connection connection = null;
		// Database connect
		// Conectamos con la base de datos
		try {
			connection = DriverManager.getConnection(
			        "jdbc:postgresql://localhost:5432/kaiserdb",
			        "postgres", "postgres");
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		
		return null;
	}

}
