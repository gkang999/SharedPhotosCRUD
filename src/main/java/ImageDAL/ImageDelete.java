package ImageDAL;

import java.sql.PreparedStatement;
import SharedPhotosUtils.*;
import MySQLConnector.*;
import java.sql.SQLException;

public class ImageDelete {
	 
	static PreparedStatement sharedPhotosPreparedStatement = null;
 
	public static int deletePicture(String accountName, String pictureName, String albumName, MySQLConnector databaseConnector) {
 
		try {
			String insertQueryStatement = "DELETE pictures.* FROM pictures "
					+ "INNER JOIN albums ON pictures.picture_album_id = albums.album_id "
					+ "INNER JOIN accounts ON albums.album_owner_id = accounts.account_id "
					+ "WHERE picturename = BINARY ? "
					+ "AND albums.albumname = BINARY ? "
					+ "AND accounts.accountname = BINARY ?";
 
			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(insertQueryStatement);
			sharedPhotosPreparedStatement.setString(1, pictureName);
			sharedPhotosPreparedStatement.setString(2, albumName);
			sharedPhotosPreparedStatement.setString(3, accountName);
 
			// execute insert SQL statement
			sharedPhotosPreparedStatement.executeUpdate();
			SysOLog.log(pictureName + " deleted successfully");
			return 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return 1;
		}
	}
	
	public static int deletePicturesByAlbum(String accountName, String albumName, MySQLConnector databaseConnector) {
		 
		try {
			String insertQueryStatement = "DELETE pictures.* FROM pictures "
					+ "INNER JOIN albums ON pictures.picture_album_id = albums.album_id "
					+ "INNER JOIN accounts ON albums.album_owner_id = accounts.account_id "
					+ "AND albums.albumname = BINARY ? "
					+ "AND accounts.accountname = BINARY ?";
 
			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(insertQueryStatement);
			sharedPhotosPreparedStatement.setString(1, albumName);
			sharedPhotosPreparedStatement.setString(2, accountName);
 
			// execute insert SQL statement
			sharedPhotosPreparedStatement.executeUpdate();
			SysOLog.log("pictures in " + albumName + " deleted successfully");
			return 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return 1;
		}
	}
}
