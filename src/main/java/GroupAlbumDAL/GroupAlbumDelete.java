package GroupAlbumDAL;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import MySQLConnector.MySQLConnector;
import SharedPhotosUtils.SysOLog;

public class GroupAlbumDelete {
	
	static PreparedStatement sharedPhotosPreparedStatement = null;
	 
	public static int deleteGroupAlbum(String groupName, String albumName, MySQLConnector databaseConnector) {
 
		try {
			String insertQueryStatement = "DELETE group_album.* FROM group_album " + 
					"INNER JOIN albums ON albums.album_id = group_album.album_id " + 
					"INNER JOIN groups ON groups.group_id = group_album.group_id " + 
					"WHERE album_name = BINARY ? " + 
					"AND group_name = BINARY ?";
 
			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(insertQueryStatement);
			sharedPhotosPreparedStatement.setString(1, albumName);
			sharedPhotosPreparedStatement.setString(2, groupName);
 
			// execute insert SQL statement
			sharedPhotosPreparedStatement.executeUpdate();
			SysOLog.log(groupName + " deleted successfully");
			return 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return 1;
		}
	}
	
	public static int deleteGroupAlbumByGroup(String groupName, MySQLConnector databaseConnector) {
		 
		try {
			String insertQueryStatement = "DELETE group_album.* FROM group_album " + 
					"INNER JOIN groups ON groups.group_id = group_album.group_id " + 
					"WHERE groups.group_name = BINARY ?)";
 
			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(insertQueryStatement);
			sharedPhotosPreparedStatement.setString(1, groupName);
 
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
