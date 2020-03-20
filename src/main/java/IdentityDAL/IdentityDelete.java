package IdentityDAL;

import java.sql.PreparedStatement;
import SharedPhotosUtils.*;
import MySQLConnector.*;
import java.sql.SQLException;

public class IdentityDelete {
	 
	static PreparedStatement sharedPhotosPreparedStatement = null;
 
	public static void deleteDataFromDB(String accountName, MySQLConnector databaseConnector) {
 
		try {
			String insertQueryStatement = "DELETE FROM accounts WHERE account_name = ?";
 
			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(insertQueryStatement);
			sharedPhotosPreparedStatement.setString(1, accountName);
 
			// execute insert SQL statement
			sharedPhotosPreparedStatement.executeUpdate();
			SysOLog.log(accountName + " deleted successfully");
		} catch (
 
		SQLException e) {
			e.printStackTrace();
		}
	}
}
