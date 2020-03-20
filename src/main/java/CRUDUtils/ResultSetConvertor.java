package CRUDUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import SharedPhotosCRUD.Album;
import SharedPhotosCRUD.Identity;
import SharedPhotosCRUD.Image;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ResultSetConvertor {

	public static JSONArray convertToJSON(ResultSet resultSet)
            throws Exception {
        JSONArray jsonArray = new JSONArray();
        while (resultSet.next()) {
            int total_rows = resultSet.getMetaData().getColumnCount();
            for (int i = 0; i < total_rows; i++) {
                JSONObject obj = new JSONObject();
                obj.put(resultSet.getMetaData().getColumnLabel(i + 1)
                        .toLowerCase(), resultSet.getObject(i + 1));
                jsonArray.put(obj);
            }
        }
        return jsonArray;
    }
	
	public static List<Identity> convertToIdentityList(ResultSet resultSet) throws SQLException {
		List<Identity> ll = new LinkedList<Identity>();
	
		// Fetch each row from the result set
		while (resultSet.next()) {
			String accountName = resultSet.getString("account_name");
			String email = resultSet.getString("email");
			String accountOwner = resultSet.getString("account_owner");
			String creationDate = resultSet.getString("creation_date");
			String roleType = resultSet.getString("role_type");
	
		  Identity acc = new Identity(accountName, email, accountOwner, creationDate, roleType);
	
		  ll.add(acc);
		}
		
		return ll;
	}
	
	public static List<Image> convertToImageList(ResultSet resultSet) throws SQLException {
		List<Image> ll = new LinkedList<Image>();
	
		// Fetch each row from the result set
		while (resultSet.next() && resultSet != null) {
			String pictureName = resultSet.getString("picture_name");
			String pictureExt = resultSet.getString("picture_extension");
	
		  //Assuming you have a user object
			Image acc = new Image();
			acc.setPictureName(pictureName);
			acc.setPictureExtension(pictureExt);
	
		  ll.add(acc);
		}
		
		return ll;
	}
	
	public static List<Album> convertToAlbumList(ResultSet resultSet) throws SQLException {
		List<Album> ll = new LinkedList<Album>();
	
		// Fetch each row from the result set
		while (resultSet.next() && resultSet != null) {
			String albumName = resultSet.getString("album_name");
	
		  //Assuming you have a user object
			Album acc = new Album();
			acc.setAlbumName(albumName);
	
		  ll.add(acc);
		}
		
		return ll;
	}
	
}
