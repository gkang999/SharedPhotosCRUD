package GroupAlbumDAL;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import MySQLConnector.MySQLConnector;
import SharedPhotosUtils.SysOLog;

public class GroupAlbumCreation {
	
	static PreparedStatement sharedPhotosPreparedStatement = null;
	
	public static int addGroupAlbum(String albumName, String groupName, MySQLConnector databaseConnector) {
		 
		//membership_status: 0=pending, 1=active, 2=inactive
		
		try {
			String insertQueryStatement = "INSERT INTO group_album (group_id, album_id) "
					+ "SELECT groups.group_id, albums.album_id "
					+ "FROM groups, albums WHERE groups.group_id = (SELECT group_id FROM groups WHERE group_name = BINARY ?) "
					+ "AND albums.album_id = (SELECT albums.album_id FROM albums WHERE album_name = BINARY ?) "
					+ "ON DUPLICATE KEY UPDATE group_id = group_id";

			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(insertQueryStatement);
			sharedPhotosPreparedStatement.setString(1, groupName);
			sharedPhotosPreparedStatement.setString(2, albumName);
 
			// execute insert SQL statement
			sharedPhotosPreparedStatement.executeUpdate();
			SysOLog.log(albumName + " added successfully");
			return 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return 1;
		}
	}
}
