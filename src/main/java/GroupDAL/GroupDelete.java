package GroupDAL;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import MySQLConnector.MySQLConnector;
import SharedPhotosUtils.SysOLog;

public class GroupDelete {
	
	static PreparedStatement sharedPhotosPreparedStatement = null;
	 
	public static int deleteGroup(String groupName, String accountName, MySQLConnector databaseConnector) {
 
		try {
			String insertQueryStatement = "DELETE groups.* FROM groups " + 
					"INNER JOIN accounts ON accounts.account_id = groups.account_id" + 
					"WHERE group_name = BINARY ? " +
					"AND account_name = BINARY ?";
 
			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(insertQueryStatement);
			sharedPhotosPreparedStatement.setString(1, groupName);
			sharedPhotosPreparedStatement.setString(2, accountName);
 
			// execute insert SQL statement
			sharedPhotosPreparedStatement.executeUpdate();
			SysOLog.log(groupName + " deleted successfully");
			return 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return 1;
		}
	}
}
