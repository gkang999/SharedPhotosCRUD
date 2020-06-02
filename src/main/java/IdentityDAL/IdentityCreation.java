package IdentityDAL;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import MySQLConnector.*;
import SharedPhotosUtils.*;
 
public class IdentityCreation {
 
	static PreparedStatement sharedPhotosPreparedStatement = null;
 
	public static int addIdentity(String accountName, String email, String accountOwner, String roleType, String hashedPass, MySQLConnector databaseConnector) {
 
		try {
			String insertQueryStatement = "INSERT  INTO  accounts (role_id, account_name, email, account_owner, creation_date, pass_hash) SELECT "
					+ "roles.role_id, ?, ?, ?, NOW(), ? FROM roles WHERE role_type = ? LIMIT 1";

			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(insertQueryStatement);
			sharedPhotosPreparedStatement.setString(1, accountName);
			sharedPhotosPreparedStatement.setString(2, email);
			sharedPhotosPreparedStatement.setString(3, accountOwner);
			sharedPhotosPreparedStatement.setString(4, hashedPass);
			sharedPhotosPreparedStatement.setString(5, roleType);
 
			// execute insert SQL statement
			sharedPhotosPreparedStatement.executeUpdate();
			SysOLog.log(accountName + " added successfully");
			return 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return 1;
		}
	}
}