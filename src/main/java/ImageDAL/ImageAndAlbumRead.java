package ImageDAL;

import java.sql.PreparedStatement;
import MySQLConnector.*;
//import SharedPhotosUtils.SysOLog;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ImageAndAlbumRead {
	static PreparedStatement sharedPhotosPreparedStatement = null;
	
	public static ResultSet readAllPicturesFromDB(MySQLConnector databaseConnector) {
		
		try {
			// MySQL Select Query Tutorial
			String getQueryStatement = "SELECT picture_name, picture_extension FROM pictures";
 
			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(getQueryStatement);
 
			// Execute the Query, and get a java ResultSet
			ResultSet rs = sharedPhotosPreparedStatement.executeQuery();
			// Let's iterate through the java ResultSet
			/*while (rs.next()) {
				String pictureLocation = rs.getString("picture_location");
				String pictureName= rs.getString("picture_name");
 
				// Simply Print the results
				SysOLog.log( String.format("%s, %s\n", pictureLocation, pictureName));
			}*/
			
			return rs;
 
		} catch (
 
		SQLException e) {
			e.printStackTrace();
		}

		return null;
 
	}
	
	public static ResultSet readPicturesFromDB(String accountName, String albumName, MySQLConnector databaseConnector) {
		 
		try {
			// MySQL Select Query Tutorial
			String getQueryStatement = "SELECT picture_name, picture_extension FROM pictures "
					+ "WHERE pictures.album_id = (SELECT album_id FROM albums WHERE album_name = BINARY ? "
					+ "AND account_id = (SELECT account_id FROM accounts WHERE accounts.account_name = BINARY ?)) "
					+ "AND pictures.account_id = (SELECT account_id FROM accounts WHERE accounts.account_name = BINARY ?);";
 
			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(getQueryStatement);

			sharedPhotosPreparedStatement.setString(1, albumName);
			sharedPhotosPreparedStatement.setString(2, accountName);
			sharedPhotosPreparedStatement.setString(3, accountName);
 
			// Execute the Query, and get a java ResultSet
			ResultSet rs = sharedPhotosPreparedStatement.executeQuery();
 
			// Let's iterate through the java ResultSet
			/*while (rs.next()) {
				String pictureLocation = rs.getString("picture_location");
				String pictureName= rs.getString("picture_name");
 
				// Simply Print the results
				SysOLog.log( String.format("%s, %s\n", pictureLocation, pictureName));
			}*/
			return rs;
 
		} catch (
 
		SQLException e) {
			e.printStackTrace();
		}
		
		return null;
 
	}
	
	public static ResultSet readPictureFromDB(String accountName, String albumName, String pictureName, MySQLConnector databaseConnector) {
		 
		try {
			// MySQL Select Query Tutorial
			String getQueryStatement = "SELECT picture_name, picture_extension FROM pictures WHERE "
					+ "pictures.picture_name = BINARY ? AND "
					+ "pictures.album_id = (SELECT album_id FROM albums WHERE albums.album_name = BINARY ? "
					+ "AND albums.account_id = (SELECT account_id FROM accounts WHERE accounts.account_name = BINARY ?)) AND "
					+ "pictures.account_id = (SELECT account_id FROM accounts WHERE accounts.account_name = BINARY ?)";
 
			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(getQueryStatement);

			sharedPhotosPreparedStatement.setString(1, pictureName);
			sharedPhotosPreparedStatement.setString(2, albumName);
			sharedPhotosPreparedStatement.setString(3, accountName);
			sharedPhotosPreparedStatement.setString(4, accountName);
 
			// Execute the Query, and get a java ResultSet
			ResultSet rs = sharedPhotosPreparedStatement.executeQuery();
 
			// Let's iterate through the java ResultSet
			/*while (rs.next()) {
				String pictureLocation = rs.getString("picture_location");
				String pictureName= rs.getString("picture_name");
 
				// Simply Print the results
				SysOLog.log( String.format("%s, %s\n", pictureLocation, pictureName));
			}*/
			return rs;
 
		} catch (
 
		SQLException e) {
			e.printStackTrace();
		}
		
		return null;
 
	}
	
	public static ResultSet readAlbumsFromDB(String accountName, MySQLConnector databaseConnector) {
		 
		try {
			// MySQL Select Query Tutorial
			String getQueryStatement = "SELECT album_name FROM albums WHERE "
					+ "account_id = (SELECT account_id FROM accounts WHERE accounts.account_name = BINARY ?)";
 
			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(getQueryStatement);

			sharedPhotosPreparedStatement.setString(1, accountName);
 
			// Execute the Query, and get a java ResultSet
			ResultSet rs = sharedPhotosPreparedStatement.executeQuery();
 
			// Let's iterate through the java ResultSet
			/*while (rs.next()) {
				String albumName = rs.getString("album_name");
 
				// Simply Print the results
				SysOLog.log( String.format("%s\n", albumName));
			}*/
			return rs;
 
		} catch (
 
		SQLException e) {
			e.printStackTrace();
		}
		return null;
 
	}
	
	
}
