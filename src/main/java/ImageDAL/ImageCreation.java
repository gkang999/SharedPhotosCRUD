package ImageDAL;

import java.sql.PreparedStatement;
import SharedPhotosUtils.*;
import java.sql.SQLException;
import MySQLConnector.*;
 
public class ImageCreation {
 
	static PreparedStatement sharedPhotosPreparedStatement = null;
 
	public static int addPicture(String accountName, String pictureName, String albumName, String pictureExtension, MySQLConnector databaseConnector) {
 
		try {
			String insertQueryStatement = "INSERT  INTO  pictures "
					+ "SELECT albums.account_id, ?, albums.album_id, ? "
					+ "FROM albums INNER JOIN accounts ON albums.account_id = accounts.account_id "
					+ "WHERE accounts.account_name = BINARY ? " 
					+ "AND albums.album_name = BINARY ?;";
 
			sharedPhotosPreparedStatement = databaseConnector.sharedPhotosConn.prepareStatement(insertQueryStatement);
			sharedPhotosPreparedStatement.setString(1, pictureName);
			sharedPhotosPreparedStatement.setString(2, pictureExtension);
			sharedPhotosPreparedStatement.setString(3, accountName);
			sharedPhotosPreparedStatement.setString(4, albumName);
 
			// execute insert SQL statement
			sharedPhotosPreparedStatement.executeUpdate();
			SysOLog.log(pictureName + " added successfully");
			return 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return 1;
		}
	}

}