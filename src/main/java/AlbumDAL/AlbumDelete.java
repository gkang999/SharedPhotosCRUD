package AlbumDAL;

import java.sql.PreparedStatement;
import SharedPhotosUtils.*;
import MySQLConnector.*;
import java.sql.SQLException;

public class AlbumDelete {
	 
	static PreparedStatement sharedPhotosPreparedStatement = null;
	
	public static int deleteAlbum(String accountName, String albumName, MySQLConnector databaseConnector) {
		 
		try {
			String insertQueryStatement = "DELETE albums.* FROM albums "
					+ "INNER JOIN accounts ON accounts.account_id = albums.album_owner_id "
					+ "WHERE albumname = BINARY ? "
					+ "AND accounts.accountname = BINARY ?";
 
			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(insertQueryStatement);
			sharedPhotosPreparedStatement.setString(1, albumName);
			sharedPhotosPreparedStatement.setString(2, accountName);
 
			// execute insert SQL statement
			sharedPhotosPreparedStatement.executeUpdate();
			SysOLog.log(albumName + " deleted successfully");
			
			return 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return 1;
		}
	}
}
