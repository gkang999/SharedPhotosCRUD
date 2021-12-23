package IdentityDAL;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import MySQLConnector.*;
import SharedPhotosUtils.*;
 
public class IdentityCreation {
 
	static PreparedStatement sharedPhotosPreparedStatement = null;
 
	public static int addIdentity(String accountName, String email, String accountOwner, String roleType, String hashedPass, MySQLConnector databaseConnector) {
 
		try {
			String insertQueryStatement = "INSERT  INTO  Accounts (AccountName, AccountPassword, AccountEmail, AccountOwner, AccountCreationDate, Account_Role_Id) SELECT "
					+ "?, ?, ?, ?, NOW(), Roles.Role_Id FROM Roles WHERE Role_Type = ? LIMIT 1";

			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(insertQueryStatement);
			sharedPhotosPreparedStatement.setString(1, accountName);
			sharedPhotosPreparedStatement.setString(2, hashedPass);
			sharedPhotosPreparedStatement.setString(3, email);
			sharedPhotosPreparedStatement.setString(4, accountOwner);
			sharedPhotosPreparedStatement.setString(5, roleType);
 
			// execute insert SQL statement
			sharedPhotosPreparedStatement.executeUpdate();
			SysOLog.log(accountName + " added successfully");
			SysOLog.log(email + " added successfully");
			SysOLog.log(accountOwner + " added successfully");
			SysOLog.log(roleType + " added successfully");
			return 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return 1;
		}
	}
}