package vms.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class ConnectionManager {
	
	public static ConnectionManager instance = null;
	
	public static ConnectionManager getInstance() {
		if(instance == null) instance = new ConnectionManager();
		return instance;
	}
	
	private final String url = DATABASE_URL_HERE; //TODO: Hide URL FROM CODE
	
	public Connection getConnection(){	
		Connection connection = null;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection(url, "root", DB_PASSWORD); //TODO: hide credentials from code
		} catch (Exception e){
			e.printStackTrace();
		}
		return connection;
	}
	
	public void close(Connection connection){
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getUpdateSQL(String tableName, List<String> columns) {
		String sql = "update " + tableName +"  set   ";
		for (String column : columns) {
			sql += column + " = ? , ";
		}
		sql = sql.substring(0, sql.length() -2);
		sql += " where id = ?";
		return sql;
	}
}
