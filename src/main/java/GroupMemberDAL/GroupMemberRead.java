package GroupMemberDAL;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import MySQLConnector.MySQLConnector;

public class GroupMemberRead {
	
	static PreparedStatement sharedPhotosPreparedStatement = null;
	
	public static ResultSet readGroupMemberByMemberFromDB(String accountNameSearch, MySQLConnector databaseConnector) {
		 
		try {
			// MySQL Select Query Tutorial
			String getQueryStatement = "SELECT group_name, membership_status, account_name FROM group_member "
					+ "INNER JOIN groups ON group_member.group_id = groups.group_id "
					+ "INNER JOIN accounts ON accounts.account_id = group_member.account_id "
					+ "WHERE group_member.account_id = (SELECT account_id FROM accounts WHERE account_name = BINARY ?) "
					+ "AND group_member.membership_status = 1;";
 
			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(getQueryStatement);

			sharedPhotosPreparedStatement.setString(1, accountNameSearch);
 
			// Execute the Query, and get a java ResultSet
			ResultSet rs = sharedPhotosPreparedStatement.executeQuery();
 
			// Let's iterate through the java ResultSet
			/*while (rs.next()) {
				String accountName = rs.getString("account_name");
				String email = rs.getString("email");
				String accountOwner = rs.getString("account_owner");
				String creationDate = rs.getString("creation_date");
				String roleType = rs.getString("role_type");
 
				// Simply Print the results
				SysOLog.log( String.format("%s, %s, %s, %s, %s\n", accountName, email, accountOwner, creationDate, roleType));
			}*/
			
			return rs;
 
		} catch (SQLException e) {
			e.printStackTrace();
		}
 
		return null;
	}
	
	public static ResultSet readGroupMemberByGroupFromDB(String groupName, MySQLConnector databaseConnector) {
		 
		try {
			// MySQL Select Query Tutorial
			String getQueryStatement = "SELECT account_name, account_owner, membership_status, group_name FROM accounts "
					+ "INNER JOIN group_member ON accounts.account_id = group_member.account_id "
					+ "INNER JOIN groups ON groups.group_id = group_member.group_id "
					+ "WHERE groups.group_name = BINARY ?;";
 
			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(getQueryStatement);

			sharedPhotosPreparedStatement.setString(1, groupName);
 
			// Execute the Query, and get a java ResultSet
			ResultSet rs = sharedPhotosPreparedStatement.executeQuery();
 
			// Let's iterate through the java ResultSet
			/*while (rs.next()) {
				String accountName = rs.getString("account_name");
				String email = rs.getString("email");
				String accountOwner = rs.getString("account_owner");
				String creationDate = rs.getString("creation_date");
				String roleType = rs.getString("role_type");
 
				// Simply Print the results
				SysOLog.log( String.format("%s, %s, %s, %s, %s\n", accountName, email, accountOwner, creationDate, roleType));
			}*/
			
			return rs;
 
		} catch (SQLException e) {
			e.printStackTrace();
		}
 
		return null;
	}
}
