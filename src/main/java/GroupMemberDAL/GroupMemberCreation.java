package GroupMemberDAL;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import MySQLConnector.MySQLConnector;
import SharedPhotosUtils.SysOLog;

public class GroupMemberCreation {
	
	static PreparedStatement sharedPhotosPreparedStatement = null;
	
	public static int addGroupMember(String accountName, String groupName, MySQLConnector databaseConnector) {
		 
		//membership_status: 0=pending, 1=active, 2=inactive
		
		try {
			String insertQueryStatement = "INSERT INTO group_member (group_id, account_id, membership_status) "
					+ "SELECT groups.group_id, accounts.account_id, 0 "
					+ "FROM groups, accounts WHERE groups.group_id = (SELECT group_id FROM groups WHERE group_name = BINARY ?) "
					+ "AND accounts.account_id = (SELECT accounts.account_id FROM accounts WHERE account_name = BINARY ?) "
					+ "ON DUPLICATE KEY UPDATE groups.group_id = groups.group_id";

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
