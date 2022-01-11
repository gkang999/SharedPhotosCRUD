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
			String getQueryStatement = "SELECT picturename, pictureextension FROM pictures " + 
					"INNER JOIN albums ON pictures.picture_album_id = albums.album_id " + 
					"INNER JOIN accounts ON pictures.picture_owner_id = accounts.account_id " + 
					"WHERE albums.albumname = BINARY ? " + 
					"AND accounts.accountname = BINARY ? ";
 
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
			String getQueryStatement = "SELECT COUNT(picturename) AS NumberOfPictures FROM pictures " + 
					"INNER JOIN albums ON pictures.picture_album_id = albums.album_id " + 
					"INNER JOIN accounts ON pictures.picture_owner_id = accounts.account_id " + 
					"WHERE pictures.picturename = BINARY ? " + 
					"AND albums.albumname = BINARY ? " +
					"AND accountname = BINARY ?";
 
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
