package IdentityDAL;

import java.sql.PreparedStatement;
import SharedPhotosUtils.*;
import MySQLConnector.*;
import java.sql.SQLException;

public class IdentityUpdate {
	static PreparedStatement sharedPhotosPreparedStatement = null;
	 
	public static void updateDataToDB(String oldAccountName, String newAccountName, String email, String accountOwner, String roleType, MySQLConnector databaseConnector) {
 
		try {
			String insertQueryStatement = "UPDATE accounts SET account_name = ?, email = ?, account_owner = ?, role_id = (SELECT roles.role_id FROM roles WHERE role_type = ?) WHERE account_name = ?";
 
			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(insertQueryStatement);
			sharedPhotosPreparedStatement.setString(1, newAccountName);
			sharedPhotosPreparedStatement.setString(2, email);
			sharedPhotosPreparedStatement.setString(3, accountOwner);
			sharedPhotosPreparedStatement.setString(4, roleType);
			sharedPhotosPreparedStatement.setString(5, oldAccountName);
 
			// execute insert SQL statement
			sharedPhotosPreparedStatement.executeUpdate();
			SysOLog.log(oldAccountName + " updated successfully to " + newAccountName);
		} catch (
 
		SQLException e) {
			e.printStackTrace();
		}
	}
}
