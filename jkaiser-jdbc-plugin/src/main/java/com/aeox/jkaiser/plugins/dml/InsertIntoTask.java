package com.aeox.jkaiser.plugins.dml;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aeox.jkaiser.core.JobContext;
import com.aeox.jkaiser.core.ParameterType;
import com.aeox.jkaiser.core.Result;
import com.aeox.jkaiser.core.Task;
import com.aeox.jkaiser.core.exception.KaiserException;

public class InsertIntoTask extends Task<String> {

	@Override
	public String getName() {
		return "jkaiser-jdbc-insert";
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
		params.put("dburl", ParameterType.STRING);
		params.put("sqlquery", ParameterType.STRING);
		return params;
	}

	@Override
	public Map<String, ParameterType> getOptionalParameters() {
		final Map<String, ParameterType> params = new HashMap<>();
		params.put("sqlparams", ParameterType.LIST);
		return params;
	}

	@Override
	public Result<String> onCall(JobContext context) throws KaiserException {
		try {
			try (final Connection conn = this.getConnection((String) context.getParameter("dburl"), 
					(String) context.getParameter("dbusr"),
					(String) context.getParameter("dbpasswd"))) {		
				final String query = (String) context.getParameter("sqlquery");
				try(final PreparedStatement pstmt = conn.prepareStatement(query)) {					
					final Object paramsObject = context.getParameter("sqlparams");	
					if (paramsObject != null) {
						if (!(paramsObject instanceof List<?>)) {
							throw new KaiserException("Invalid list of parameters 'sqlparams'", 400);
						}					
						this.addParameters(pstmt, (List<?>) paramsObject);
					}
					pstmt.execute();					
					final boolean wasError = pstmt.getUpdateCount() < 1;
					
					return new Result<String>() {

						@Override
						public String getResult() {
							return "Performed query: "+ query;
						}

						@Override
						public boolean wasError() {
							return wasError;
						}
						
					};
				}				
			}			
		} catch (SQLException e) {
			throw new KaiserException("Exception message: " + e.getMessage(), 400);
		}
	}
	
	private Connection getConnection(final String url, final String username, final String password) throws SQLException {
		return DriverManager.getConnection(url, username, password);
	}
	
	private void addParameters(final PreparedStatement pstmt, final List<?> params) throws SQLException {
		for (int i=0;i<params.size();i++) {
			pstmt.setObject(i+1, params.get(i));
		}
	}

}
