package ImageDAL;

import java.sql.PreparedStatement;
import SharedPhotosUtils.*;
import MySQLConnector.*;
import java.sql.SQLException;

public class ImageAndAlbumDelete {
	 
	static PreparedStatement sharedPhotosPreparedStatement = null;
 
	public static void deletePictureFromDB(String accountName, String pictureName, String albumName, MySQLConnector databaseConnector) {
 
		try {
			String insertQueryStatement = "DELETE FROM pictures WHERE picture_name = ? AND "
					+ "pictures.account_id = (SELECT account_id FROM accounts WHERE accounts.account_name = ?)"
					+ "AND pictures.album_id = (SELECT album_id FROM albums WHERE album_name = ?);";
 
			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(insertQueryStatement);
			sharedPhotosPreparedStatement.setString(1, pictureName);
			sharedPhotosPreparedStatement.setString(2, accountName);
			sharedPhotosPreparedStatement.setString(3, albumName);
 
			// execute insert SQL statement
			sharedPhotosPreparedStatement.executeUpdate();
			SysOLog.log(pictureName + " deleted successfully");
		} catch (
 
		SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void deleteAlbumFromDB(String accountName, String albumName, MySQLConnector databaseConnector) {
		 
		try {
			String insertQueryStatement = "DELETE FROM pictures WHERE album_id = "
					+ "(SELECT album_id FROM albums WHERE album_name = ? AND account_id = "
					+ "(SELECT account_id FROM accounts WHERE account_name = ?))";
 
				sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(insertQueryStatement);
				sharedPhotosPreparedStatement.setString(1, albumName);
				sharedPhotosPreparedStatement.setString(2, accountName);
	 
				// execute insert SQL statement
			sharedPhotosPreparedStatement.executeUpdate();
			SysOLog.log("pictures deleted successfully");
				
			insertQueryStatement = "DELETE FROM albums WHERE album_name = ? AND "
					+ "albums.account_id = (SELECT account_id FROM accounts WHERE accounts.account_name = ?)";
 
			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(insertQueryStatement);
			sharedPhotosPreparedStatement.setString(1, albumName);
			sharedPhotosPreparedStatement.setString(2, accountName);
 
			// execute insert SQL statement
			sharedPhotosPreparedStatement.executeUpdate();
			SysOLog.log(albumName + " deleted successfully");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
