package GroupDAL;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import MySQLConnector.MySQLConnector;
import SharedPhotosUtils.SysOLog;

public class GroupDelete {
	
	static PreparedStatement sharedPhotosPreparedStatement = null;
	 
	public static void deleteGroupFromDB(String groupName, String accountName, MySQLConnector databaseConnector) {
 
		try {
			String insertQueryStatement = "DELETE FROM groups WHERE group_name = BINARY ? "
					+ "AND account_id = (SELECT account_id FROM accounts WHERE account_name = ?)";
 
			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(insertQueryStatement);
			sharedPhotosPreparedStatement.setString(1, groupName);
			sharedPhotosPreparedStatement.setString(2, accountName);
 
			// execute insert SQL statement
			sharedPhotosPreparedStatement.executeUpdate();
			SysOLog.log(groupName + " deleted successfully");
		} catch (
 
		SQLException e) {
			e.printStackTrace();
		}
	}
}
