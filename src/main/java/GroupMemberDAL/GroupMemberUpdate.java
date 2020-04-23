package GroupMemberDAL;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import MySQLConnector.MySQLConnector;

public class GroupMemberUpdate {
	static PreparedStatement sharedPhotosPreparedStatement = null;
	 
	public static void updateDataToDB(String accountName, String groupName, int membershipStatus, MySQLConnector databaseConnector) {
 
		try {
			String insertQueryStatement = "UPDATE group_member SET membership_status = ? "
					+ "WHERE account_id = (SELECT account_id FROM accounts WHERE account_name = ?) "
					+ "AND group_id = (SELECT group_id FROM groups WHERE group_name = ?)";
			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(insertQueryStatement);
			sharedPhotosPreparedStatement.setInt(1, membershipStatus);
			sharedPhotosPreparedStatement.setString(2, accountName);
			sharedPhotosPreparedStatement.setString(3, groupName);
 
			// execute insert SQL statement
			sharedPhotosPreparedStatement.executeUpdate();
		} catch (
 
		SQLException e) {
			e.printStackTrace();
		}
	}
}
