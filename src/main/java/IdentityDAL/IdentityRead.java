package IdentityDAL;

import java.sql.PreparedStatement;
import MySQLConnector.*;
import java.sql.ResultSet;
import java.sql.SQLException;
//import SharedPhotosUtils.*;

public class IdentityRead {
	static PreparedStatement sharedPhotosPreparedStatement = null;
	
	public static ResultSet readAllDataFromDB(MySQLConnector databaseConnector) {
		 
		try {
			// MySQL Select Query Tutorial
			String getQueryStatement = "SELECT account_name, email, account_owner, creation_date, role_type FROM accounts, roles WHERE role_type = roles.role_type AND roles.role_id = accounts.role_id ORDER BY account_name ASC";
 
			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(getQueryStatement);
 
			// Execute the Query, and get a java ResultSet
			ResultSet rs = sharedPhotosPreparedStatement.executeQuery();
 
			/*
			// Let's iterate through the java ResultSet
			while (rs.next()) {
				String accountName = rs.getString("account_name");
				String email = rs.getString("email");
				String accountOwner = rs.getString("account_owner");
				String creationDate = rs.getString("creation_date");
				String roleType = rs.getString("role_type");
 
				// Simply Print the results
				SysOLog.log( String.format("%s, %s, %s, %s, %s\n", accountName, email, accountOwner, creationDate, roleType));
			}*/
			
			return rs;
 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
 
	}
	
	public static ResultSet readDataFromDB(String accountNameSearch, MySQLConnector databaseConnector) {
		 
		try {
			// MySQL Select Query Tutorial
			String getQueryStatement = "SELECT account_name, email, account_owner, creation_date, role_type FROM accounts, "
					+ "roles WHERE role_type = roles.role_type AND roles.role_id = accounts.role_id AND accounts.account_name = ? ORDER BY account_name ASC";
 
			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(getQueryStatement);

			sharedPhotosPreparedStatement.setString(1, accountNameSearch);
 
			// Execute the Query, and get a java ResultSet
			ResultSet rs = sharedPhotosPreparedStatement.executeQuery();
 
			// Let's iterate through the java ResultSet
			/*while (rs.next()) {
				String accountName = rs.getString("account_name");
				String email = rs.getString("email");
				String accountOwner = rs.getString("account_owner");
				String creationDate = rs.getString("creation_date");
				String roleType = rs.getString("role_type");
 
				// Simply Print the results
				SysOLog.log( String.format("%s, %s, %s, %s, %s\n", accountName, email, accountOwner, creationDate, roleType));
			}*/
			
			return rs;
 
		} catch (SQLException e) {
			e.printStackTrace();
		}
 
		return null;
	}
	
	public static ResultSet validateLogin(String accountNameSearch, String hashedPass, MySQLConnector databaseConnector) {
		 
		try {
			// MySQL Select Query Tutorial
			String getQueryStatement = "SELECT account_name, email, account_owner, creation_date, role_type FROM accounts, roles "
					+ "WHERE role_type = roles.role_type "
					+ "AND roles.role_id = accounts.role_id "
					+ "AND accounts.account_name = ? "
					+ "AND accounts.pass_hash = ? "
					+ "ORDER BY account_name ASC";
 
			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(getQueryStatement);

			sharedPhotosPreparedStatement.setString(1, accountNameSearch);
			sharedPhotosPreparedStatement.setString(2, hashedPass);
 
			// Execute the Query, and get a java ResultSet
			ResultSet rs = sharedPhotosPreparedStatement.executeQuery();
 
			// Let's iterate through the java ResultSet
			/*while (rs.next()) {
				String accountName = rs.getString("account_name");
				String email = rs.getString("email");
				String accountOwner = rs.getString("account_owner");
				String creationDate = rs.getString("creation_date");
				String roleType = rs.getString("role_type");
 
				// Simply Print the results
				SysOLog.log( String.format("%s, %s, %s, %s, %s\n", accountName, email, accountOwner, creationDate, roleType));
			}*/
			
			return rs;
 
		} catch (SQLException e) {
			e.printStackTrace();
		}
 
		return null;
	}
}
