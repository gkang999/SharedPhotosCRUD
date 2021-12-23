package GroupDAL;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import MySQLConnector.MySQLConnector;
import SharedPhotosUtils.SysOLog;

public class GroupCreation {

	static PreparedStatement sharedPhotosPreparedStatement = null;
	 
	public static int addGroup(String accountName, String groupName, MySQLConnector databaseConnector) {
 
		try {
			String insertQueryStatement = "INSERT INTO groupaccounts (group_owner_id, groupname) "
					+ "SELECT accounts.account_id, ? FROM accounts "
					+ "WHERE accountname = BINARY ? "
					+ "ON DUPLICATE KEY UPDATE group_id = group_id";

			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(insertQueryStatement);
			sharedPhotosPreparedStatement.setString(1, groupName);
			sharedPhotosPreparedStatement.setString(2, accountName);
 
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
