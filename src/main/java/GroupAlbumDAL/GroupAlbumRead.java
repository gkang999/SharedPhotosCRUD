package GroupAlbumDAL;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import MySQLConnector.MySQLConnector;

public class GroupAlbumRead {
	
	static PreparedStatement sharedPhotosPreparedStatement = null;
	
	public static ResultSet readGroupAlbumByGroup(String groupName, MySQLConnector databaseConnector) {
		 
		try {
			// MySQL Select Query Tutorial
			String getQueryStatement = "SELECT album_name, account_name FROM group_album " + 
			"INNER JOIN groups ON groups.group_id = group_album.group_id " + 
			"INNER JOIN albums ON albums.album_id = group_album.album_id " + 
			"INNER JOIN accounts ON accounts.account_id = albums.account_id " + 
			"WHERE group_name = BINARY ?";
 
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
	
	public static ResultSet readGroupAlbumByAlbum(String albumName, MySQLConnector databaseConnector) {
		 
		try {
			// MySQL Select Query Tutorial
			String getQueryStatement = "SELECT album_name, account_name FROM group_album " + 
			"INNER JOIN groups ON groups.group_id = group_album.group_id " + 
			"INNER JOIN albums ON albums.album_id = group_album.album_id " + 
			"INNER JOIN accounts ON accounts.account_id = albums.account_id " + 
			"WHERE album_name = BINARY ?";
 
			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(getQueryStatement);

			sharedPhotosPreparedStatement.setString(1, albumName);
 
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
	
	public static ResultSet readGroupAlbumCountByGroupAndAlbum(String groupName, String albumName, MySQLConnector databaseConnector) {
		 
		try {
			// MySQL Select Query Tutorial
			String getQueryStatement = "SELECT album_name, account_name FROM group_album " + 
			"INNER JOIN groups ON groups.group_id = group_album.group_id " + 
			"INNER JOIN albums ON albums.album_id = group_album.album_id " + 
			"INNER JOIN accounts ON accounts.account_id = albums.account_id " + 
			"WHERE group_name = BINARY ? " + 
			"AND album_name = BINARY ?";
 
			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(getQueryStatement);

			sharedPhotosPreparedStatement.setString(1, groupName);
			sharedPhotosPreparedStatement.setString(2, albumName);
 
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
