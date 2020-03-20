package ImageDAL;

import java.sql.PreparedStatement;
import SharedPhotosUtils.*;
import MySQLConnector.*;
import java.sql.SQLException;

public class ImageUpdate {
	static PreparedStatement sharedPhotosPreparedStatement = null;
	 
	//Moves a picture to another album
	public static void updatePicturesAlbumToDB(String accountName, String pictureName, String oldAlbumName, String newAlbumName, MySQLConnector databaseConnector) {
 
		try {
			String insertQueryStatement = "UPDATE pictures SET album_id = "
					+ "(SELECT album_id FROM albums WHERE album_name = ?"
					+ "AND account_id = (SELECT account_id FROM accounts WHERE account_name = ?) LIMIT 1) "
					+ "WHERE account_id = (SELECT account_id FROM accounts WHERE account_name = ?) "
					+ "AND album_id = (SELECT album_id FROM albums WHERE album_name = ?)";
 
			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(insertQueryStatement);
			sharedPhotosPreparedStatement.setString(1, newAlbumName);
			sharedPhotosPreparedStatement.setString(2, accountName);
			sharedPhotosPreparedStatement.setString(3, accountName);
			sharedPhotosPreparedStatement.setString(4, oldAlbumName);
 
			// execute insert SQL statement
			sharedPhotosPreparedStatement.executeUpdate();
			SysOLog.log(pictureName + " location updated successfully from " + oldAlbumName + " to " + newAlbumName);
		} catch (
 
		SQLException e) {
			e.printStackTrace();
		}
	}
	
	//renames a picture
	public static void updatePictureNameToDB(String accountName, String albumName, String oldPictureName, String newPictureName, MySQLConnector databaseConnector) {
		 
		try {
			String insertQueryStatement = "UPDATE pictures SET picture_name = ? "
					+ "WHERE account_id = (SELECT account_id FROM accounts WHERE account_name = ?) "
					+ "AND album_id = (SELECT album_id FROM albums WHERE album_name = ?) "
					+ "AND picture_name = ?;";
 
			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(insertQueryStatement);
			sharedPhotosPreparedStatement.setString(1, newPictureName);
			sharedPhotosPreparedStatement.setString(2, accountName);
			sharedPhotosPreparedStatement.setString(3, albumName);
			sharedPhotosPreparedStatement.setString(4, oldPictureName);
 
			// execute insert SQL statement
			sharedPhotosPreparedStatement.executeUpdate();
			SysOLog.log(oldPictureName + " updated successfully to " + newPictureName);
		} catch (
 
		SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void updateAlbumNameToDB(String accountName, String oldAlbumName, String newAlbumName, MySQLConnector databaseConnector) {
		 
		try {
			String insertQueryStatement = "UPDATE albums SET album_name = ? "
					+ "WHERE account_id = (SELECT account_id FROM accounts WHERE account_name = ?) "
					+ "AND album_name = ?);";
 
			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(insertQueryStatement);
			sharedPhotosPreparedStatement.setString(1, newAlbumName);
			sharedPhotosPreparedStatement.setString(2, accountName);
			sharedPhotosPreparedStatement.setString(3, oldAlbumName);
 
			// execute insert SQL statement
			sharedPhotosPreparedStatement.executeUpdate();
			SysOLog.log(oldAlbumName + " updated successfully to " + newAlbumName);
		} catch (
 
		SQLException e) {
			e.printStackTrace();
		}
	}
}
