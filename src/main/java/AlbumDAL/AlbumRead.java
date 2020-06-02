package AlbumDAL;

import java.sql.PreparedStatement;
import MySQLConnector.*;
//import SharedPhotosUtils.SysOLog;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AlbumRead {
	static PreparedStatement sharedPhotosPreparedStatement = null;
	
	public static ResultSet readAlbums(String accountName, MySQLConnector databaseConnector) {
		 
		try {
			// MySQL Select Query Tutorial
			String getQueryStatement = "SELECT album_name, account_name FROM albums "
					+ "INNER JOIN accounts ON albums.account_id = accounts.account_id "
					+ "WHERE accounts.account_name = BINARY ?";
 
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
 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
 
	}
	
	public static ResultSet readPublicAlbumsFromDB(MySQLConnector databaseConnector) {
		 
		try {
			// MySQL Select Query Tutorial
			String getQueryStatement = "SELECT album_name, account_name FROM albums INNER JOIN accounts ON albums.account_id = accounts.account_id WHERE albums.public = 1";
 
			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(getQueryStatement);
 
			// Execute the Query, and get a java ResultSet
			ResultSet rs = sharedPhotosPreparedStatement.executeQuery();
 
			// Let's iterate through the java ResultSet
			/*while (rs.next()) {
				String albumName = rs.getString("album_name");
 
				// Simply Print the results
				SysOLog.log( String.format("%s\n", albumName));
			}*/
			return rs;
 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
 
	}
	
	public static ResultSet readAlbumCountByAccountAndAlbum(String accountName, String albumName, MySQLConnector databaseConnector) {
		 
		try {
			// MySQL Select Query Tutorial
			String getQueryStatement = "SELECT COUNT(album_id) AS NumberOfAlbums FROM albums "
					+ "INNER JOIN accounts ON albums.account_id = accounts.account_id"
					+ "WHERE albums.album_name = BINARY ? "
					+ "AND accounts.account_name = BINARY ?";
 
			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(getQueryStatement);
			
			sharedPhotosPreparedStatement.setString(1, albumName);
			sharedPhotosPreparedStatement.setString(2, accountName);
 
			// Execute the Query, and get a java ResultSet
			ResultSet rs = sharedPhotosPreparedStatement.executeQuery();
 
			// Let's iterate through the java ResultSet
			/*while (rs.next()) {
				String albumName = rs.getString("album_name");
 
				// Simply Print the results
				SysOLog.log( String.format("%s\n", albumName));
			}*/
			return rs;
 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
 
	}
	
}
