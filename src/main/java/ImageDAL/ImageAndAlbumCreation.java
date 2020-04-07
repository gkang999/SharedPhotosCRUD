package ImageDAL;

import java.sql.PreparedStatement;
import SharedPhotosUtils.*;
import java.sql.SQLException;
import MySQLConnector.*;
 
public class ImageAndAlbumCreation {
 
	static PreparedStatement sharedPhotosPreparedStatement = null;
 
	public static void addPictureToDB(String accountName, String pictureName, String albumName, String pictureExtension, MySQLConnector databaseConnector) {
 
		try {
			String insertQueryStatement = "INSERT  INTO  pictures "
					+ "SELECT albums.account_id, ?, albums.album_id, ? "
					+ "FROM albums WHERE albums.account_id = (SELECT account_id FROM accounts WHERE account_name = BINARY ?) " 
					+ "AND albums.album_name = BINARY ?;";
 
			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(insertQueryStatement);
			sharedPhotosPreparedStatement.setString(1, pictureName);
			sharedPhotosPreparedStatement.setString(2, pictureExtension);
			sharedPhotosPreparedStatement.setString(3, accountName);
			sharedPhotosPreparedStatement.setString(4, albumName);
 
			// execute insert SQL statement
			sharedPhotosPreparedStatement.executeUpdate();
			SysOLog.log(pictureName + " added successfully");
		} catch (
 
		SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void addAlbumToDB(String accountName, String albumName, MySQLConnector databaseConnector) {
		 
		try {
			String insertQueryStatement = "INSERT  INTO  albums (account_id, album_name) SELECT "
					+ "accounts.account_id, ? FROM accounts WHERE accounts.account_name = BINARY ?";
 
			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(insertQueryStatement);
			sharedPhotosPreparedStatement.setString(1, albumName);
			sharedPhotosPreparedStatement.setString(2, accountName);
 
			// execute insert SQL statement
			sharedPhotosPreparedStatement.executeUpdate();
			SysOLog.log(albumName + " added successfully");
		} catch (
 
		SQLException e) {
			e.printStackTrace();
		}
	}
}