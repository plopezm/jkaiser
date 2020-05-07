package com.aeox.jkaiser.plugins.dml;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.aeox.jkaiser.core.JobContext;
import com.aeox.jkaiser.core.ParameterMappings;
import com.aeox.jkaiser.core.ParameterType;
import com.aeox.jkaiser.core.Result;
import com.aeox.jkaiser.core.Task;
import com.aeox.jkaiser.core.exception.KaiserException;

public class JdbcDmlTask extends Task<List<Map<String, Object>>> {
	
	public JdbcDmlTask() {
		super();
	}
	
	public JdbcDmlTask(final ParameterMappings mappings) {
		super(mappings);
	}

	@Override
	public String getName() {
		return "jkaiser-jdbc-dml";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public String getDescription() {
		return "This task is used to insert/update/select/delete data in a database. A JDBC driver is required in plugins folder to make it work. The driver will depend on the database used.";
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
	public Result<List<Map<String, Object>>> onCall(JobContext context) throws KaiserException {
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
					
					if (query.toLowerCase().startsWith("select")) {
						return this.performSelect(pstmt);						
					} else {
						return this.performModification(pstmt);
					}
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
	
	private Map<String, Object> map(final ResultSet rs) throws SQLException {
	    Map<String, Object> result = new HashMap<String, Object>();
	    final ResultSetMetaData rsmd = rs.getMetaData();	    
	    for (int i = 0;i<rsmd.getColumnCount();i++) {
	    	result.put(rsmd.getColumnName(i+1), rs.getObject(i+1));
	    }	    
	    return result;
	}
	
	private Result<List<Map<String, Object>>> performSelect(final PreparedStatement pstmt) throws SQLException {
		pstmt.execute();
		final List<Map<String, Object>> results = new LinkedList<>();		
		ResultSet rs = pstmt.getResultSet();
		if (rs == null) {							
			rs = pstmt.getGeneratedKeys();							
		} 
		while(rs.next()) {
			results.add(map(rs));
		}
		rs.close();		
		return new Result<List<Map<String, Object>>>() {
			@Override
			public List<Map<String, Object>> getResult() {
				return results;
			}

			@Override
			public boolean wasError() {
				return results.size() == 0; 
			}							
		};
	}
	
	private Result<List<Map<String, Object>>> performModification(final PreparedStatement pstmt) throws SQLException {
		final int result = pstmt.executeUpdate();
			
		return new Result<List<Map<String, Object>>>() {
			@Override
			public List<Map<String, Object>> getResult() {
				return new ArrayList<>();
			}

			@Override
			public boolean wasError() {
				return result < 1; 
			}							
		};
	}
	
}
