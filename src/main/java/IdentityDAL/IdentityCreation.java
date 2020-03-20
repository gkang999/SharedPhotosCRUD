package IdentityDAL;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import MySQLConnector.*;
import SharedPhotosUtils.*;
 
public class IdentityCreation {
 
	static PreparedStatement sharedPhotosPreparedStatement = null;
 
	public static void addDataToDB(String accountName, String email, String accountOwner, String roleType, MySQLConnector databaseConnector) {
 
		try {
			String insertQueryStatement = "INSERT  INTO  accounts (role_id, account_name, email, account_owner, creation_date) SELECT "
					+ "roles.role_id, ?, ?, ?, NOW() FROM roles WHERE role_type = ? LIMIT 1";
 
			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(insertQueryStatement);
			sharedPhotosPreparedStatement.setString(1, accountName);
			sharedPhotosPreparedStatement.setString(2, email);
			sharedPhotosPreparedStatement.setString(3, accountOwner);
			sharedPhotosPreparedStatement.setString(4, roleType);
 
			// execute insert SQL statement
			sharedPhotosPreparedStatement.executeUpdate();
			SysOLog.log(accountName + " added successfully");
		} catch (
 
		SQLException e) {
			e.printStackTrace();
		}
	}
}