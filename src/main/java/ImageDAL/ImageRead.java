package ImageDAL;

import java.sql.PreparedStatement;
import MySQLConnector.*;
//import SharedPhotosUtils.SysOLog;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ImageRead {
	static PreparedStatement sharedPhotosPreparedStatement = null;
	
	public static ResultSet readPictures(String accountName, String albumName, MySQLConnector databaseConnector) {
		 
		try {
			// MySQL Select Query Tutorial
			String getQueryStatement = "SELECT picture_name, picture_extension FROM pictures " + 
					"INNER JOIN albums ON pictures.album_id = albums.album_id " + 
					"INNER JOIN accounts ON pictures.account_id = accounts.account_id " + 
					"WHERE albums.album_name = BINARY ? " + 
					"AND accounts.account_name = BINARY ? ";
 
			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(getQueryStatement);

			sharedPhotosPreparedStatement.setString(1, albumName);
			sharedPhotosPreparedStatement.setString(2, accountName);
			
 
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
 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
 
	}
	
	public static ResultSet readPictureCountByAccountAlbumPicture(String accountName, String albumName, String pictureName, MySQLConnector databaseConnector) {
		 
		try {
			// MySQL Select Query Tutorial
			String getQueryStatement = "SELECT COUNT(picture_name) AS NumberOfPictures FROM pictures " + 
					"INNER JOIN albums ON pictures.album_id = albums.album_id " + 
					"INNER JOIN accounts ON pictures.account_id = accounts.account_id " + 
					"WHERE pictures.picture_name = BINARY ? " + 
					"AND albums.album_name = BINARY ? " +
					"AND account_name = BINARY ?" + 
					"AND albums.public = ?";
 
			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(getQueryStatement);

			sharedPhotosPreparedStatement.setString(1, pictureName);
			sharedPhotosPreparedStatement.setString(2, albumName);
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
 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
 
	}
	
	
}
