package AlbumDAL;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import MySQLConnector.MySQLConnector;
import SharedPhotosUtils.SysOLog;

public class AlbumUpdate {
	
	static PreparedStatement sharedPhotosPreparedStatement = null;
	
	public static int updateAlbumName(String accountName, String oldAlbumName, String newAlbumName, MySQLConnector databaseConnector) {
		 
		try {
			String insertQueryStatement = "UPDATE albums SET album_name = ? "
					+ "WHERE account_id = (SELECT account_id FROM accounts WHERE account_name = BINARY ?) "
					+ "AND album_name = BINARY ?);";
 
			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(insertQueryStatement);
			sharedPhotosPreparedStatement.setString(1, newAlbumName);
			sharedPhotosPreparedStatement.setString(2, accountName);
			sharedPhotosPreparedStatement.setString(3, oldAlbumName);
 
			// execute insert SQL statement
			sharedPhotosPreparedStatement.executeUpdate();
			SysOLog.log(oldAlbumName + " updated successfully to " + newAlbumName);
			return 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return 1;
		}
	}
	
	public static int updateAlbumPublicStatus(String accountName, String albumName, int publicV, MySQLConnector databaseConnector) {
		 
		try {
			String insertQueryStatement = "UPDATE albums SET public = ? "
					+ "WHERE account_id = (SELECT account_id FROM accounts WHERE account_name = BINARY ?) "
					+ "AND album_name = BINARY ?);";
 
			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(insertQueryStatement);
			sharedPhotosPreparedStatement.setInt(1, publicV);
			sharedPhotosPreparedStatement.setString(2, accountName);
			sharedPhotosPreparedStatement.setString(3, albumName);
 
			// execute insert SQL statement
			sharedPhotosPreparedStatement.executeUpdate();
			SysOLog.log(albumName + " was made public");
			return 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return 1;
		}
	}
}
