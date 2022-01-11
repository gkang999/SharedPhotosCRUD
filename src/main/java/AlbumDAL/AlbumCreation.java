package AlbumDAL;

import java.sql.PreparedStatement;
import SharedPhotosUtils.*;
import java.sql.SQLException;
import MySQLConnector.*;
 
public class AlbumCreation {
 
	static PreparedStatement sharedPhotosPreparedStatement = null;
	
	public static int addAlbum(String accountName, String albumName, MySQLConnector databaseConnector) {
		 
		try {
			String insertQueryStatement = "INSERT  INTO  albums (albumname, album_owner_id, public) SELECT "
					+ "?, accounts.account_id, 0 FROM accounts WHERE accounts.accountname = BINARY ? "
					+ "ON DUPLICATE KEY UPDATE album_id = album_id";
 
			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(insertQueryStatement);
			sharedPhotosPreparedStatement.setString(1, albumName);
			sharedPhotosPreparedStatement.setString(2, accountName);
 
			// execute insert SQL statement
			sharedPhotosPreparedStatement.executeUpdate();
			SysOLog.log(albumName + " added successfully");
			return 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return 1;
		}
	}
	
	public static int addPublicAlbum(String accountName, String albumName, MySQLConnector databaseConnector) {
		 
		try {
			String insertQueryStatement = "INSERT  INTO  albums (albumname, album_owner_id, public) SELECT "
					+ "?, accounts.account_id, 1 FROM accounts WHERE accounts.accountname = BINARY ? "
					+ "ON DUPLICATE KEY UPDATE album_id = album_id";
 
			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(insertQueryStatement);
			sharedPhotosPreparedStatement.setString(1, albumName);
			sharedPhotosPreparedStatement.setString(2, accountName);
 
			// execute insert SQL statement
			sharedPhotosPreparedStatement.executeUpdate();
			SysOLog.log(albumName + " added successfully");
			return 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return 1;
		}
	}
}