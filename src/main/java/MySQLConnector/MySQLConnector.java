package MySQLConnector;

import java.sql.Connection;
import SharedPhotosUtils.*;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnector {
	public Connection sharedPhotosConn = null;
	
	public void makeJDBCConnection() {
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			SysOLog.log("Congrats - Seems your MySQL JDBC Driver Registered!");
		} catch (ClassNotFoundException e) {
			SysOLog.log("Sorry, couldn't found JDBC driver. Make sure you have added JDBC Maven Dependency Correctly");
			e.printStackTrace();
			return;
		}
 
		try {
			// DriverManager: The basic service for managing a set of JDBC drivers.
			String jdbcConnString = System.getenv("jdbcConnectionString");
			String jdbcUser = System.getenv("jdbcUser");
			String jdbcPassword = System.getenv("jdbcPassword");
			
			sharedPhotosConn = DriverManager.getConnection(jdbcConnString, jdbcUser, jdbcPassword);
			if (sharedPhotosConn != null) {
				SysOLog.log("Connection Successful! Enjoy. Now it's time to push data");
			} else {
				SysOLog.log("Failed to make connection!");
			}
		} catch (SQLException e) {
			SysOLog.log("MySQL Connection Failed!");
			e.printStackTrace();
			return;
		}
 
	}
	
public void makeTestJDBCConnection() {
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			SysOLog.log("Congrats - Seems your MySQL JDBC Driver Registered!");
		} catch (ClassNotFoundException e) {
			SysOLog.log("Sorry, couldn't found JDBC driver. Make sure you have added JDBC Maven Dependency Correctly");
			e.printStackTrace();
			return;
		}
 
		try {
			// DriverManager: The basic service for managing a set of JDBC drivers.
			String jdbcConnString = System.getenv("testjdbcConnectionString");
			String jdbcUser = System.getenv("testjdbcUser");
			String jdbcPassword = System.getenv("testjdbcPassword");
			
			sharedPhotosConn = DriverManager.getConnection(jdbcConnString, jdbcUser, jdbcPassword);
			if (sharedPhotosConn != null) {
				SysOLog.log("Connection Successful! Enjoy. Now it's time to push data");
			} else {
				SysOLog.log("Failed to make connection!");
			}
		} catch (SQLException e) {
			SysOLog.log("MySQL Connection Failed!");
			e.printStackTrace();
			return;
		}
 
	}
	
	public void closeJDBCConnection() {
		try {
			sharedPhotosConn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
