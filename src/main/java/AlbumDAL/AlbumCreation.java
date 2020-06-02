package AlbumDAL;

import java.sql.PreparedStatement;
import SharedPhotosUtils.*;
import java.sql.SQLException;
import MySQLConnector.*;
 
public class AlbumCreation {
 
	static PreparedStatement sharedPhotosPreparedStatement = null;
	
	public static int addAlbum(String accountName, String albumName, MySQLConnector databaseConnector) {
		 
		try {
			String insertQueryStatement = "INSERT  INTO  albums (account_id, album_name) SELECT "
					+ "accounts.account_id, ? FROM accounts WHERE accounts.account_name = BINARY ? "
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