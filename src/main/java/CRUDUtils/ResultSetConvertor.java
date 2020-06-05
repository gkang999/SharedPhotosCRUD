package CRUDUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import SharedPhotosCRUD.Album;
import SharedPhotosCRUD.Identity;
import SharedPhotosCRUD.Image;
import SharedPhotosCRUD.Group;
import SharedPhotosCRUD.GroupAlbum;
import SharedPhotosCRUD.GroupMember;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ResultSetConvertor {

	/**
	   * Converts a mysql query result set to a JSONArray
	   * @param type is ResultSet
	   * @return JSONArray conversion of the result set
	   */
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
	
	/**
	   * Converts a mysql query result set to a list of Identity
	   * @param ResultSet of Identity
	   * @return List<Identity> conversion of ResultSet
	   */
	public static List<Identity> convertToIdentityList(ResultSet resultSet) throws SQLException {
		List<Identity> ll = new LinkedList<Identity>();
	
		// Fetch each row from the result set
		while (resultSet.next()) {
			String accountName = resultSet.getString("account_name");
			String email = resultSet.getString("email");
			String accountOwner = resultSet.getString("account_owner");
			String roleType = resultSet.getString("role_type");
	
		  Identity acc = new Identity(accountName, email, accountOwner, roleType);
	
		  ll.add(acc);
		}
		
		return ll;
	}
	
	/**
	   * Converts a mysql query result set to a list of Image
	   * @param ResultSet of Image
	   * @return List<Image> conversion of ResultSet
	   */
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
	
	/**
	   * Converts a mysql query result set to a list of Album
	   * @param ResultSet of Album
	   * @return List<Album> conversion of ResultSet
	   */
	public static List<Album> convertToAlbumList(ResultSet resultSet) throws SQLException {
		List<Album> ll = new LinkedList<Album>();
	
		// Fetch each row from the result set
		while (resultSet.next() && resultSet != null) {
			String albumName = resultSet.getString("album_name");
			String accountName = resultSet.getString("account_name");
			
	
		  //Assuming you have a user object
			Album acc = new Album();
			acc.setAlbumName(albumName);
			acc.setAccountName(accountName);
	
		  ll.add(acc);
		}
		
		return ll;
	}
	
	public static List<Group> convertToGroupList(ResultSet resultSet) throws SQLException {
		List<Group> ll = new LinkedList<Group>();
	
		// Fetch each row from the result set
		while (resultSet.next() && resultSet != null) {
			String groupName = resultSet.getString("group_name");
			String accountName = resultSet.getString("account_name");
	
		  //Assuming you have a user object
			Group acc = new Group(groupName, accountName);
	
		  ll.add(acc);
		}
		
		return ll;
	}
	
	public static List<GroupMember> convertToGroupMemberList(ResultSet resultSet) throws SQLException {
		List<GroupMember> ll = new LinkedList<GroupMember>();
	
		// Fetch each row from the result set
		while (resultSet.next() && resultSet != null) {
			int membershipStatus = resultSet.getInt("membership_status");
			String accountName = resultSet.getString("account_name");
			String accountOwner = resultSet.getString("account_owner");
			String groupName = resultSet.getString("group_name");
	
		  //Assuming you have a user object
			GroupMember acc = new GroupMember(groupName, accountName, accountOwner, membershipStatus);
	
		  ll.add(acc);
		}
		
		return ll;
	}
	
	public static List<GroupAlbum> convertToGroupAlbumList(ResultSet resultSet) throws SQLException {
		List<GroupAlbum> ll = new LinkedList<GroupAlbum>();
	
		// Fetch each row from the result set
		while (resultSet.next() && resultSet != null) {
			String albumName = resultSet.getString("album_name");
			String accountName = resultSet.getString("account_name");
	
		  //Assuming you have a user object
			GroupAlbum acc = new GroupAlbum(albumName, accountName);
	
		  ll.add(acc);
		}
		
		return ll;
	}
	
	public static int countFromResultSet(ResultSet resultSet, String countType) throws SQLException {
		if (resultSet.next() && resultSet != null) {
			return resultSet.getInt(countType);
		}
		return -1;
	}
	
}
