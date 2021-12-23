package GroupAlbumDAL;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import MySQLConnector.MySQLConnector;
import SharedPhotosUtils.SysOLog;

public class GroupAlbumCreation {
	
	static PreparedStatement sharedPhotosPreparedStatement = null;
	
	public static int addGroupAlbum(String albumName, String groupName, String groupOwnerName, MySQLConnector databaseConnector) {
		
		try {
			String insertQueryStatement = "INSERT INTO groupalbum (group_id, album_id) SELECT groupaccount.group_id, albums.album_id FROM groupaccounts, albums "
					+ "WHERE groupaccount.group_id = (SELECT groupaccount.group_id FROM groupaccount WHERE groupname = BINARY ? "
					+ "AND groupaccount.group_owner_id = (SELECT account_id FROM accounts WHERE accountname = BINARY ?)) "
					+ "AND albums.album_id = (SELECT albums.album_id FROM albums WHERE albumname = BINARY ? "
					+ "AND albums.album_owner_id = (SELECT account_id FROM accounts WHERE accountname = BINARY ?)) "
					+ "ON DUPLICATE KEY UPDATE group_id = groupalbum.group_id";

			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(insertQueryStatement);
			sharedPhotosPreparedStatement.setString(1, groupName);
			sharedPhotosPreparedStatement.setString(2, groupOwnerName);
			sharedPhotosPreparedStatement.setString(3, albumName);
			sharedPhotosPreparedStatement.setString(4, groupOwnerName);
 
			// execute insert SQL statement
			sharedPhotosPreparedStatement.execute();
			SysOLog.log(albumName + " added successfully");
			return 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return 1;
		}
	}
}
