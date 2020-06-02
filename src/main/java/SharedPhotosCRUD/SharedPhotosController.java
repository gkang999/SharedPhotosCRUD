package SharedPhotosCRUD;

import java.util.List;
import java.util.UUID;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;

import IdentityDAL.*;
import ImageDAL.*;
import MySQLConnector.*;
import SharedPhotosUtils.PassHasher;
import CRUDUtils.*;
import GroupAlbumDAL.GroupAlbumCreation;
import GroupAlbumDAL.GroupAlbumDelete;
import GroupAlbumDAL.GroupAlbumRead;
import GroupDAL.GroupCreation;
import GroupDAL.GroupDelete;
import GroupDAL.GroupRead;
import GroupMemberDAL.GroupMemberCreation;
import GroupMemberDAL.GroupMemberDelete;
import GroupMemberDAL.GroupMemberRead;
import GroupMemberDAL.GroupMemberUpdate;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.javatuples.Triplet;

import java.time.LocalDateTime;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import AlbumDAL.AlbumCreation;
import AlbumDAL.AlbumDelete;
import AlbumDAL.AlbumRead;
import AlbumDAL.AlbumUpdate;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RestController
public class SharedPhotosController {

	List<Triplet<UUID, LocalDateTime, String>> SessionKeys = new ArrayList<Triplet<UUID, LocalDateTime, String>>();

	private boolean isValid(String uuidAsString, String accountName) {
		this.cleanSessionKeys(); // remove all old session keys
		for (int i = 0; i < SessionKeys.size(); i++) {
			if (SessionKeys.get(i).getValue0().equals(UUID.fromString(uuidAsString))
					&& SessionKeys.get(i).getValue2().contentEquals(accountName)) {
				return true;
			}
		}
		return false;
	}

	private void cleanSessionKeys() {
		for (int i = 0; i < SessionKeys.size(); i++) {
			if (SessionKeys.get(i).getValue1().plusHours(24).isBefore(LocalDateTime.now())) {
				SessionKeys.remove(i);
			}
		}
	}
	
	@PostMapping("/session")
	public ResponseEntity<Integer> validateSession(
			@RequestHeader("SPDKSessionKey") String sessionKey, 
			@RequestHeader("SPDKKeyAccount") String sessionAccount)
			throws SQLException, IOException {
		if (!this.isValid(sessionKey, sessionAccount)) {
			System.out.println("invalid key");
			return new ResponseEntity<Integer>(1, HttpStatus.UNAUTHORIZED);
		}

		return new ResponseEntity<Integer>(0, HttpStatus.OK);
	}

	/**************************************************
	 ************ Account CRUD operations *************
	 **************************************************/

	/**
	 * sends account information to DAL to read account
	 * 
	 * @param type Identity containing account information
	 */
	@PostMapping("/accounts/read")
	public ResponseEntity<List<Identity>> getAccountByAccountName(@RequestBody Identity idenReqBody) throws Exception {

		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();

		List<Identity> tr = ResultSetConvertor
				.convertToIdentityList(IdentityRead.readIdentity(idenReqBody.getAccountName(), myConnector));

		myConnector.closeJDBCConnection();
		return new ResponseEntity<List<Identity>>(tr, HttpStatus.OK);
	}

	/**
	 * sends account information to DAL to create account
	 * 
	 * @param type Identity containing account information
	 */
	@PostMapping("/accounts/create")
	public ResponseEntity<Integer> postAccount(@RequestBody Identity idenReqBody) throws Exception {

		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();

		try {
			IdentityCreation.addIdentity(idenReqBody.getAccountName(), idenReqBody.getEmail(),
					idenReqBody.getAccountOwner(), idenReqBody.getRoleType(),
					PassHasher.generateStrongPasswordHash(idenReqBody.getAccountPass()), myConnector);

			myConnector.closeJDBCConnection();
		} catch (Exception e) {
			return new ResponseEntity<Integer>(1, HttpStatus.OK);
		}
		
		return new ResponseEntity<Integer>(0, HttpStatus.OK);
	}

	/**
	 * sends account information to DAL to validate account
	 * 
	 * @param type Identity containing account information
	 */
	@PostMapping("/accounts/login")
	public ResponseEntity<List<String>> validateAccount(@RequestBody Identity idenReqBody) throws Exception {

		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();

		int matchingAccounts = ResultSetConvertor
				.countFromResultSet(IdentityRead.readIdentityCountByAccountAndHPass(idenReqBody.getAccountName(),
						PassHasher.generateStrongPasswordHash(idenReqBody.getAccountPass()), myConnector), "NumberOfIdentity");

		myConnector.closeJDBCConnection();

		if (matchingAccounts == 1) {
			UUID temp = UUID.randomUUID();
			this.SessionKeys.add(
					new Triplet<UUID, LocalDateTime, String>(temp, LocalDateTime.now(), idenReqBody.getAccountName()));
			List<String> tmp = new LinkedList<String>();
			tmp.add(temp.toString());
			return new ResponseEntity<List<String>>(tmp, HttpStatus.OK);
		}
		return new ResponseEntity<List<String>>(HttpStatus.UNAUTHORIZED);
	}

	/**
	 * sends account information to DAL to delete account
	 * 
	 * @param type Identity containing account information
	 */
	@DeleteMapping("/accounts/delete")
	public ResponseEntity<Integer> deleteAccount(@RequestBody Identity idenReqBody) {
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();

		int sqlResponse = IdentityDelete.deleteIdentity(idenReqBody.getAccountName(), myConnector);

		myConnector.closeJDBCConnection();
		
		return sqlResponse == 0 ? new ResponseEntity<Integer>(0, HttpStatus.OK) : new ResponseEntity<Integer>(1, HttpStatus.OK);
	}

	/**
	 * sends account information to DAL to update account
	 * 
	 * @param type Identity containing account information
	 */
	@PutMapping("/accounts/update")
	public ResponseEntity<Integer> updateAccount(@RequestBody Identity idenReqBody) throws Exception {
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();

		int sqlResponse = IdentityUpdate.updateIdentityName(idenReqBody.getOldAccountName(), idenReqBody.getAccountName(),
				idenReqBody.getEmail(), idenReqBody.getAccountOwner(), idenReqBody.getRoleType(), myConnector);

		myConnector.closeJDBCConnection();
		
		return sqlResponse == 0 ? new ResponseEntity<Integer>(0, HttpStatus.OK) : new ResponseEntity<Integer>(1, HttpStatus.OK);
	}

	/**************************************************
	 ************ Image CRUD operations ***************
	 **************************************************/


	/**
	 * sends image information to DAL to read image
	 * 
	 * @param type Image containing account information
	 * @throws IOException 
	 */
	@PostMapping("/images/read")
	public ResponseEntity<List<Image>> getPicturesByAlbumAndAccountName(@RequestBody Image idenReqBody, 
			@RequestHeader("SPDKSessionKey") String sessionKey, 
			@RequestHeader("SPDKKeyAccount") String sessionAccount)
			throws SQLException, IOException {
		if (!this.isValid(sessionKey, sessionAccount)) {
			System.out.println("invalid key");
			return new ResponseEntity<List<Image>>(HttpStatus.UNAUTHORIZED);
		}
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();

		List<Image> tr = ResultSetConvertor.convertToImageList(ImageRead
				.readPictures(idenReqBody.getAccountName(), idenReqBody.getAlbumName(), myConnector));

		// get the base64 encodings and then return the list
		for (int i = 0; i < tr.size(); i++) {
			HttpPost post = new HttpPost("http://" + System.getenv("mediaServiceIP") + ":8080/photos/get");
			// add request parameter, form parameters
			List<NameValuePair> urlParameters = new ArrayList<>();
			urlParameters.add(new BasicNameValuePair("accountName", idenReqBody.getAccountName()));
			urlParameters.add(new BasicNameValuePair("albumName", idenReqBody.getAlbumName()));
			urlParameters.add(new BasicNameValuePair("photoName", tr.get(i).getPictureName()));
			urlParameters.add(new BasicNameValuePair("photoExtension", tr.get(i).getPictureExtension()));

			post.setEntity(new UrlEncodedFormEntity(urlParameters));
			try (CloseableHttpClient httpClient = HttpClients.createDefault();
					CloseableHttpResponse response = httpClient.execute(post)) {
				tr.get(i).setBase64Encoding(
						new JSONObject(EntityUtils.toString(response.getEntity())).getString("base64Encoding"));
			}
		}

		myConnector.closeJDBCConnection();
		return new ResponseEntity<List<Image>>(tr, HttpStatus.OK);
	}

	/**
	 * sends image information to DAL to create image
	 * 
	 * @param type Image containing account information
	 * @throws IOException 
	 */
	@PostMapping("/images/create")
	public ResponseEntity<Integer> postImage(@RequestBody Image idenReqBody, 
			@RequestHeader("SPDKSessionKey") String sessionKey, 
			@RequestHeader("SPDKKeyAccount") String sessionAccount)
			throws SQLException, IOException {
		if (!this.isValid(sessionKey, sessionAccount)) {
			System.out.println("invalid key");
			return new ResponseEntity<Integer>(2, HttpStatus.UNAUTHORIZED);
		}
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();
		String idenReqOriginalName = idenReqBody.getPictureName();

		int count = ResultSetConvertor.countFromResultSet(ImageRead.readPictureCountByAccountAlbumPicture(
				idenReqBody.getAccountName(), idenReqBody.getAlbumName(), idenReqBody.getPictureName(), myConnector), "NumberOfPictures");
		int duplicate = 0;
		while (count > 0) {
			duplicate += 1;
			count = ResultSetConvertor.countFromResultSet(
					ImageRead.readPictureCountByAccountAlbumPicture(idenReqBody.getAccountName(), idenReqBody.getAlbumName(),
							idenReqOriginalName + "(" + String.valueOf(duplicate) + ")", myConnector), "NumberOfPictures");
		}
		if (duplicate > 0) {
			idenReqBody.setPictureName(idenReqOriginalName + "(" + String.valueOf(duplicate) + ")");
		}

		HttpPost post = new HttpPost("http://" + System.getenv("mediaServiceIP") + ":8080/photos/upload");

		// add request parameter, form parameters
		List<NameValuePair> urlParameters = new ArrayList<>();
		urlParameters.add(new BasicNameValuePair("accountName", idenReqBody.getAccountName()));
		urlParameters.add(new BasicNameValuePair("albumName", idenReqBody.getAlbumName()));
		urlParameters.add(new BasicNameValuePair("photoName", idenReqBody.getPictureName()));
		urlParameters.add(new BasicNameValuePair("base64Encoding", idenReqBody.getBase64Encoding()));

		post.setEntity(new UrlEncodedFormEntity(urlParameters));
		JSONObject image;
		try (CloseableHttpClient httpClient = HttpClients.createDefault();
				CloseableHttpResponse response = httpClient.execute(post)) {
			if (response == null) {
				return new ResponseEntity<Integer>(1, HttpStatus.OK);
			}
			image = new JSONObject(EntityUtils.toString(response.getEntity()));
		} catch (Exception e) {
			return new ResponseEntity<Integer>(1, HttpStatus.OK);
		}

		int sqlResponse = 0;
		try {
			sqlResponse = ImageCreation.addPicture(idenReqBody.getAccountName(), idenReqBody.getPictureName(),
					idenReqBody.getAlbumName(), image.getString("photoExtension"), myConnector);

			myConnector.closeJDBCConnection();
		} catch (Exception e) {
			return new ResponseEntity<Integer>(1, HttpStatus.OK);
		}

		return new ResponseEntity<Integer>(sqlResponse, HttpStatus.OK);
	}

	/*
	 * Takes accountName, albumName, pictureName and deletes entry from DB
	 * idenReqBody requires: String: accountName String: pictureName String:
	 * albumName
	 */
	/**
	 * sends image information to DAL to delete image
	 * 
	 * @param type Image containing account information
	 */
	@PostMapping("/images/delete")
	public ResponseEntity<Integer> deleteImage(@RequestBody Image idenReqBody, 
			@RequestHeader("SPDKSessionKey") String sessionKey, 
			@RequestHeader("SPDKKeyAccount") String sessionAccount)
			throws SQLException, IOException {
		if (!this.isValid(sessionKey, sessionAccount)) {
			System.out.println("invalid key");
			return new ResponseEntity<Integer>(2, HttpStatus.UNAUTHORIZED);
		}
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();

		
		int sqlResponse = ImageDelete.deletePicture(idenReqBody.getAccountName(), idenReqBody.getPictureName(),
				idenReqBody.getAlbumName(), myConnector);

		myConnector.closeJDBCConnection();

		HttpPost post = new HttpPost("http://" + System.getenv("mediaServiceIP") + ":8080/photos/delete");

		// add request parameter, form parameters
		List<NameValuePair> urlParameters = new ArrayList<>();
		urlParameters.add(new BasicNameValuePair("accountName", idenReqBody.getAccountName()));
		urlParameters.add(new BasicNameValuePair("albumName", idenReqBody.getAlbumName()));
		urlParameters.add(new BasicNameValuePair("photoName", idenReqBody.getPictureName()));

		post.setEntity(new UrlEncodedFormEntity(urlParameters));
		try (CloseableHttpClient httpClient = HttpClients.createDefault();
				CloseableHttpResponse response = httpClient.execute(post)) {
		} catch (Exception e) {
			return new ResponseEntity<Integer>(1, HttpStatus.OK);
		}

		return new ResponseEntity<Integer>(sqlResponse, HttpStatus.OK);
	}

	/**
	 * sends image information to DAL to update account
	 * 
	 * @param type Image containing account information
	 */
	@PutMapping("/images/update/name")
	public ResponseEntity<Integer> updateImage(@RequestBody Image idenReqBody, 
			@RequestHeader("SPDKSessionKey") String sessionKey, 
			@RequestHeader("SPDKKeyAccount") String sessionAccount)
			throws SQLException, IOException {
		if (!this.isValid(sessionKey, sessionAccount)) {
			System.out.println("invalid key");
			return new ResponseEntity<Integer>(2, HttpStatus.UNAUTHORIZED);
		}
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();

		Client client = ClientBuilder.newClient();

		// grab base64 encoding
		WebTarget target = client.target(System.getenv("mediaServiceIP") + ":8080/photos/get")
				.queryParam("accountName", idenReqBody.getAccountName())
				.queryParam("albumName", idenReqBody.getAlbumName())
				.queryParam("photoName", idenReqBody.getPictureName());
		idenReqBody.setBase64Encoding(target.request(MediaType.APPLICATION_JSON).get(Image.class).getBase64Encoding());

		// delete old image
		target = client.target(System.getenv("mediaServiceIP") + ":8080/photos/delete")
				.queryParam("accountName", idenReqBody.getAccountName())
				.queryParam("albumName", idenReqBody.getAlbumName())
				.queryParam("photoName", idenReqBody.getPictureName());

		// upload new image with new name
		target = client.target(System.getenv("mediaServiceIP") + ":8080/photos/upload")
				.queryParam("accountName", idenReqBody.getAccountName())
				.queryParam("albumName", idenReqBody.getAlbumName())
				.queryParam("photoName", idenReqBody.getNewPictureName())
				.queryParam("base64Encoding", idenReqBody.getBase64Encoding());
		
		int sqlResponse = ImageUpdate.updatePictureName(idenReqBody.getAccountName(), idenReqBody.getAlbumName(),
				idenReqBody.getPictureName(), idenReqBody.getNewPictureName(), myConnector);

		myConnector.closeJDBCConnection();

		return new ResponseEntity<Integer>(sqlResponse, HttpStatus.OK);
	}

	/**
	 * sends image information to DAL to update album of image
	 * 
	 * @param type Image containing image information
	 */
	@PutMapping("/images/update/album")
	public ResponseEntity<Integer> updatePicturesAlbum(@RequestBody Image idenReqBody, 
			@RequestHeader("SPDKSessionKey") String sessionKey, 
			@RequestHeader("SPDKKeyAccount") String sessionAccount)
			throws SQLException, IOException {
		if (!this.isValid(sessionKey, sessionAccount)) {
			System.out.println("invalid key");
			return new ResponseEntity<Integer>(2, HttpStatus.UNAUTHORIZED);
		}
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();

		Client client = ClientBuilder.newClient();

		// grab base64 encoding
		WebTarget target = client.target(System.getenv("mediaServiceIP") + ":8080/photos/get")
				.queryParam("accountName", idenReqBody.getAccountName())
				.queryParam("albumName", idenReqBody.getAlbumName())
				.queryParam("photoName", idenReqBody.getPictureName());
		idenReqBody.setBase64Encoding(target.request(MediaType.APPLICATION_JSON).get(Image.class).getBase64Encoding());

		// delete old image
		target = client.target(System.getenv("mediaServiceIP") + ":8080/photos/delete")
				.queryParam("accountName", idenReqBody.getAccountName())
				.queryParam("albumName", idenReqBody.getAlbumName())
				.queryParam("photoName", idenReqBody.getPictureName());

		// upload new image with new name
		target = client.target(System.getenv("mediaServiceIP") + ":8080/photos/upload")
				.queryParam("accountName", idenReqBody.getAccountName())
				.queryParam("albumName", idenReqBody.getNewAlbumName())
				.queryParam("photoName", idenReqBody.getPictureName())
				.queryParam("base64Encoding", idenReqBody.getBase64Encoding());

		int sqlResponse = ImageUpdate.updatePicturesAlbum(idenReqBody.getAccountName(), idenReqBody.getPictureName(),
				idenReqBody.getAlbumName(), idenReqBody.getNewAlbumName(), myConnector);

		myConnector.closeJDBCConnection();

		return new ResponseEntity<Integer>(sqlResponse, HttpStatus.OK);
	}

	/**************************************************
	 ************ Album CRUD operations ***************
	 **************************************************/

	/**
	 * sends album information to DAL to create album
	 * 
	 * @param type Album containing album information
	 */
	@PostMapping("/albums/create")
	public ResponseEntity<Integer> createAlbum(@RequestBody Album idenReqBody, 
			@RequestHeader("SPDKSessionKey") String sessionKey, 
			@RequestHeader("SPDKKeyAccount") String sessionAccount)
			throws SQLException {
		if (!this.isValid(sessionKey, sessionAccount)) {
			System.out.println("invalid key");
			return new ResponseEntity<Integer>(2, HttpStatus.UNAUTHORIZED);
		}
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();
		int sqlResponse = 0;
		try {
			sqlResponse = AlbumCreation.addAlbum(idenReqBody.getAccountName(), idenReqBody.getAlbumName(), myConnector);
		} catch (Exception e) {
			return new ResponseEntity<Integer>(1, HttpStatus.OK);
		}

		return new ResponseEntity<Integer>(sqlResponse, HttpStatus.OK);
	}

	/**
	 * sends album information to DAL to read album
	 * 
	 * @param type Album containing album information
	 */
	@PostMapping("/albums/read")
	public ResponseEntity<List<Album>> readAlbums(@RequestBody Album idenReqBody, 
			@RequestHeader("SPDKSessionKey") String sessionKey, 
			@RequestHeader("SPDKKeyAccount") String sessionAccount)
			throws SQLException, IOException {
		if (!this.isValid(sessionKey, sessionAccount)) {
			System.out.println("invalid key");
			return new ResponseEntity<List<Album>>(HttpStatus.UNAUTHORIZED);
		}
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();
		List<Album> tr = ResultSetConvertor
				.convertToAlbumList(AlbumRead.readAlbums(idenReqBody.getAccountName(), myConnector));

		return new ResponseEntity<List<Album>>(tr, HttpStatus.OK);
	}

	/**
	 * sends image information to DAL to delete album
	 * 
	 * @param type Album containing album information
	 */
	@PostMapping("/albums/delete")
	public ResponseEntity<Integer> deleteAlbum(@RequestBody Album idenReqBody, @RequestHeader("SPDKSessionKey") String sessionKey)
			throws SQLException {
		if (!this.isValid(sessionKey, idenReqBody.getAccountName())) {
			System.out.println("invalid key");
			return new ResponseEntity<Integer>(2, HttpStatus.UNAUTHORIZED);
		}
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();
		
		int sqlResponse = 0;
		//delete pictures of album first
		try {
			sqlResponse += ImageDelete.deletePicturesByAlbum(idenReqBody.getAccountName(), idenReqBody.getAlbumName(), myConnector);
		} catch (Exception e) {
			return new ResponseEntity<Integer>(1, HttpStatus.OK);
		}
		
		try {
			sqlResponse += AlbumDelete.deleteAlbum(idenReqBody.getAccountName(), idenReqBody.getAlbumName(), myConnector);
		} catch (Exception e) {
			return new ResponseEntity<Integer>(1, HttpStatus.OK);
		}

		return new ResponseEntity<Integer>(sqlResponse, HttpStatus.OK);
	}
	
	@PostMapping("/albums/public")
	public ResponseEntity<Integer> publicizeAlbum(@RequestBody Album idenReqBody, @RequestHeader("SPDKSessionKey") String sessionKey)
			throws SQLException {
		if (!this.isValid(sessionKey, idenReqBody.getAccountName())) {
			System.out.println("invalid key");
			return new ResponseEntity<Integer>(2, HttpStatus.UNAUTHORIZED);
		}
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();
		
		int sqlResponse = 0;
		
		try {
			sqlResponse = AlbumUpdate.updateAlbumPublicStatus(idenReqBody.getAccountName(), idenReqBody.getAlbumName(), 1, myConnector);
		} catch (Exception e) {
			return new ResponseEntity<Integer>(1, HttpStatus.OK);
		}

		return new ResponseEntity<Integer>(sqlResponse, HttpStatus.OK);
	}
	
	@PostMapping("/albums/unpublic")
	public ResponseEntity<Integer> unpublicizeAlbum(@RequestBody Album idenReqBody, @RequestHeader("SPDKSessionKey") String sessionKey)
			throws SQLException {
		if (!this.isValid(sessionKey, idenReqBody.getAccountName())) {
			System.out.println("invalid key");
			return new ResponseEntity<Integer>(2, HttpStatus.UNAUTHORIZED);
		}
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();
		
		int sqlResponse = 0;
		
		try {
			sqlResponse = AlbumUpdate.updateAlbumPublicStatus(idenReqBody.getAccountName(), idenReqBody.getAlbumName(), 0, myConnector);
		} catch (Exception e) {
			return new ResponseEntity<Integer>(1, HttpStatus.OK);
		}

		return new ResponseEntity<Integer>(sqlResponse, HttpStatus.OK);
	}

	
	/**************************************************
	 ************ Group CRUD operations ***************
	 **************************************************/
	
	@PostMapping("/groups/create")
	public ResponseEntity<Integer> createGroup(@RequestBody Group idenReqBody, 
			@RequestHeader("SPDKSessionKey") String sessionKey, 
			@RequestHeader("SPDKKeyAccount") String sessionAccount)
			throws SQLException, IOException {
		if (!this.isValid(sessionKey, sessionAccount)) {
			System.out.println("invalid key");
			return new ResponseEntity<Integer>(2, HttpStatus.UNAUTHORIZED);
		}
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();
		int sqlResponse = 0;
		try {
			sqlResponse += GroupCreation.addGroup(idenReqBody.getGroupOwner(), idenReqBody.getGroupName(), myConnector);
		} catch (Exception e) {
			return new ResponseEntity<Integer>(1, HttpStatus.OK);
		}
		
		//add self to newly created group
		try {
			sqlResponse += GroupMemberCreation.addGroupMember(idenReqBody.getGroupOwner(), idenReqBody.getGroupName(), myConnector);
			sqlResponse += GroupMemberUpdate.updateGroupMemberStatus(idenReqBody.getGroupOwner(), idenReqBody.getGroupName(), 1, myConnector);
		} catch (Exception e) {
			return new ResponseEntity<Integer>(1, HttpStatus.OK);
		}
		
		return new ResponseEntity<Integer>(sqlResponse, HttpStatus.OK);
	}
	
	@PostMapping("/groups/read")
	public ResponseEntity<List<Group>> readGroups(@RequestBody Group idenReqBody, 
			@RequestHeader("SPDKSessionKey") String sessionKey, 
			@RequestHeader("SPDKKeyAccount") String sessionAccount)
			throws SQLException, IOException {
		if (!this.isValid(sessionKey, sessionAccount)) {
			System.out.println("invalid key");
			return new ResponseEntity<List<Group>>(HttpStatus.UNAUTHORIZED);
		}
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();
		List<Group> tr = ResultSetConvertor
				.convertToGroupList(GroupRead.readGroupsByOwner(idenReqBody.getGroupOwner(), myConnector));

		return new ResponseEntity<List<Group>>(tr, HttpStatus.OK);
	}
	
	@PostMapping("/groups/delete")
	public ResponseEntity<Integer> deleteGroup(@RequestBody Group idenReqBody, 
			@RequestHeader("SPDKSessionKey") String sessionKey, 
			@RequestHeader("SPDKKeyAccount") String sessionAccount)
			throws SQLException, IOException {
		if (!this.isValid(sessionKey, sessionAccount)) {
			System.out.println("invalid key");
			return new ResponseEntity<Integer>(2, HttpStatus.UNAUTHORIZED);
		}
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();
		int sqlResponse = 0;
		try {
			//delete child group_member(s)
			sqlResponse += GroupMemberDelete.deleteGroupMemberByGroup(idenReqBody.getGroupName(), myConnector);
			//delete child group_album(s)
			sqlResponse += GroupAlbumDelete.deleteGroupAlbumByGroup(idenReqBody.getGroupName(), myConnector);
			//delete parent group
			sqlResponse += GroupDelete.deleteGroup(idenReqBody.getGroupName(), idenReqBody.getGroupOwner(), myConnector);
		} catch (Exception e) {
			return new ResponseEntity<Integer>(1, HttpStatus.OK);
		}

		return new ResponseEntity<Integer>(sqlResponse, HttpStatus.OK);
	}
	
	/**************************************************
	 ********** Group Member CRUD operations **********
	 **************************************************/
	
	@PostMapping("/groupmember/create")
	public ResponseEntity<Integer> createGroupMember(@RequestBody GroupMember idenReqBody, 
			@RequestHeader("SPDKSessionKey") String sessionKey, 
			@RequestHeader("SPDKKeyAccount") String sessionAccount)
			throws SQLException, IOException {
		if (!this.isValid(sessionKey, sessionAccount)) {
			System.out.println("invalid key");
			return new ResponseEntity<Integer>(2, HttpStatus.UNAUTHORIZED);
		}
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();
		try {
			int matchingRecordCount = ResultSetConvertor.countFromResultSet(GroupMemberRead.readGroupMemberCountByGroupAndOwner(idenReqBody.getGroupName(), idenReqBody.getAccountName(), myConnector), "NumberOfGroupMember");
			if(matchingRecordCount>0) {
				return new ResponseEntity<Integer>(1, HttpStatus.OK);
			}
			GroupMemberCreation.addGroupMember(idenReqBody.getAccountName(), idenReqBody.getGroupName(), myConnector);
		} catch (Exception e) {
			return new ResponseEntity<Integer>(1, HttpStatus.OK);
		}

		return new ResponseEntity<Integer>(0, HttpStatus.OK);
	}
	
	@PostMapping("/groupmember/readbygroup")
	public ResponseEntity<List<GroupMember>> readGroupMemberGroup(@RequestBody GroupMember idenReqBody, 
			@RequestHeader("SPDKSessionKey") String sessionKey, 
			@RequestHeader("SPDKKeyAccount") String sessionAccount)
			throws SQLException, IOException {
		if (!this.isValid(sessionKey, sessionAccount)) {
			System.out.println("invalid key");
			return new ResponseEntity<List<GroupMember>>(HttpStatus.UNAUTHORIZED);
		}
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();
		List<GroupMember> tr = ResultSetConvertor
				.convertToGroupMemberList(GroupMemberRead.readGroupMemberByGroup(idenReqBody.getGroupName(), myConnector));

		return new ResponseEntity<List<GroupMember>>(tr, HttpStatus.OK);
	}
	
	@PostMapping("/groupmember/readbymember")
	public ResponseEntity<List<GroupMember>> readGroupMemberAccount(@RequestBody GroupMember idenReqBody, 
			@RequestHeader("SPDKSessionKey") String sessionKey, 
			@RequestHeader("SPDKKeyAccount") String sessionAccount)
			throws SQLException, IOException {
		if (!this.isValid(sessionKey, sessionAccount)) {
			System.out.println("invalid key");
			return new ResponseEntity<List<GroupMember>>(HttpStatus.UNAUTHORIZED);
		}
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();
		List<GroupMember> tr = ResultSetConvertor
				.convertToGroupMemberList(GroupMemberRead.readGroupMemberByMember(idenReqBody.getAccountName(), myConnector));

		return new ResponseEntity<List<GroupMember>>(tr, HttpStatus.OK);
	}
	
	@PostMapping("/groupmember/delete")
	public ResponseEntity<Integer> deleteGroupMember(@RequestBody GroupMember idenReqBody, 
			@RequestHeader("SPDKSessionKey") String sessionKey, 
			@RequestHeader("SPDKKeyAccount") String sessionAccount)
			throws SQLException, IOException {
		if (!this.isValid(sessionKey, sessionAccount)) {
			System.out.println("invalid key");
			return new ResponseEntity<Integer>(2, HttpStatus.UNAUTHORIZED);
		}
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();
		try {
			GroupMemberDelete.deleteGroupMember(idenReqBody.getGroupName(), idenReqBody.getAccountName(), myConnector);
		} catch (Exception e) {
			return new ResponseEntity<Integer>(1, HttpStatus.OK);
		}

		return new ResponseEntity<Integer>(0, HttpStatus.OK);
	}
	
	//1 for active, 0 for pending
	@PostMapping("/groupmember/update")
	public ResponseEntity<Integer> updateGroupMember(@RequestBody GroupMember idenReqBody, 
			@RequestHeader("SPDKSessionKey") String sessionKey, 
			@RequestHeader("SPDKKeyAccount") String sessionAccount)
			throws SQLException, IOException {
		if (!this.isValid(sessionKey, sessionAccount)) {
			System.out.println("invalid key");
			return new ResponseEntity<Integer>(2, HttpStatus.UNAUTHORIZED);
		}
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();
		try {
			System.out.println(idenReqBody.getAccountName() + idenReqBody.getGroupName() + idenReqBody.getMembershipStatus());
			GroupMemberUpdate.updateGroupMemberStatus(idenReqBody.getAccountName(), idenReqBody.getGroupName(), idenReqBody.getMembershipStatus(), myConnector);
		} catch (Exception e) {
			return new ResponseEntity<Integer>(1, HttpStatus.OK);
		}

		return new ResponseEntity<Integer>(0, HttpStatus.OK);
	}
	
	/**************************************************
	 ********** Group Album CRUD operations ***********
	 **************************************************/
	
	@PostMapping("/groupalbum/create")
	public ResponseEntity<Integer> createGroupAlbum(@RequestBody GroupAlbum idenReqBody, 
			@RequestHeader("SPDKSessionKey") String sessionKey, 
			@RequestHeader("SPDKKeyAccount") String sessionAccount)
			throws SQLException, IOException {
		if (!this.isValid(sessionKey, sessionAccount)) {
			System.out.println("invalid key");
			return new ResponseEntity<Integer>(2, HttpStatus.UNAUTHORIZED);
		}
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();
		try {
			int matchingRecordsCount = ResultSetConvertor.countFromResultSet(GroupAlbumRead.readGroupAlbumCountByGroupAndAlbum(idenReqBody.getGroupName(), idenReqBody.getAlbumName(), myConnector), "NumberOfGroupAlbum");
			if(matchingRecordsCount>0) {
				return new ResponseEntity<Integer>(1, HttpStatus.OK);
			}
			GroupAlbumCreation.addGroupAlbum(idenReqBody.getAlbumName(), idenReqBody.getGroupName(), myConnector);
		} catch (Exception e) {
			return new ResponseEntity<Integer>(1, HttpStatus.OK);
		}

		return new ResponseEntity<Integer>(0, HttpStatus.OK);
	}
	
	@PostMapping("/groupalbum/readbygroup")
	public ResponseEntity<List<GroupAlbum>> readGroupAlbumByGroup(@RequestBody GroupMember idenReqBody, 
			@RequestHeader("SPDKSessionKey") String sessionKey, 
			@RequestHeader("SPDKKeyAccount") String sessionAccount)
			throws SQLException, IOException {
		if (!this.isValid(sessionKey, sessionAccount)) {
			System.out.println("invalid key");
			return new ResponseEntity<List<GroupAlbum>>(HttpStatus.UNAUTHORIZED);
		}
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();
		List<GroupAlbum> tr = ResultSetConvertor
				.convertToGroupAlbumList(GroupAlbumRead.readGroupAlbumByGroup(idenReqBody.getGroupName(), myConnector));

		return new ResponseEntity<List<GroupAlbum>>(tr, HttpStatus.OK);
	}
	
	@PostMapping("/groupalbum/readbyalbum")
	public ResponseEntity<List<GroupAlbum>> readGroupAlbumByAlbum(@RequestBody GroupAlbum idenReqBody, 
			@RequestHeader("SPDKSessionKey") String sessionKey, 
			@RequestHeader("SPDKKeyAccount") String sessionAccount)
			throws SQLException, IOException {
		if (!this.isValid(sessionKey, sessionAccount)) {
			System.out.println("invalid key");
			return new ResponseEntity<List<GroupAlbum>>(HttpStatus.UNAUTHORIZED);
		}
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();
		List<GroupAlbum> tr = ResultSetConvertor
				.convertToGroupAlbumList(GroupAlbumRead.readGroupAlbumByAlbum(idenReqBody.getAlbumName(), myConnector));

		return new ResponseEntity<List<GroupAlbum>>(tr, HttpStatus.OK);
	}
	
	@PostMapping("/groupalbum/delete")
	public ResponseEntity<Integer> deleteGroupAlbum(@RequestBody GroupAlbum idenReqBody, 
			@RequestHeader("SPDKSessionKey") String sessionKey, 
			@RequestHeader("SPDKKeyAccount") String sessionAccount)
			throws SQLException, IOException {
		if (!this.isValid(sessionKey, sessionAccount)) {
			System.out.println("invalid key");
			return new ResponseEntity<Integer>(2, HttpStatus.UNAUTHORIZED);
		}
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();
		try {
			GroupAlbumDelete.deleteGroupAlbum(idenReqBody.getGroupName(), idenReqBody.getAlbumName(), myConnector);
		} catch (Exception e) {
			return new ResponseEntity<Integer>(1, HttpStatus.OK);
		}

		return new ResponseEntity<Integer>(0, HttpStatus.OK);
	}
	
	/**************************************************
	 ********** Public Album CRUD operations **********
	 **************************************************/
	
	@PostMapping("/public/albums/create")
	public ResponseEntity<Integer> createPublicAlbum(@RequestBody Album idenReqBody)
			throws SQLException {
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();
		try {
			AlbumCreation.addAlbum("GuestAccount", idenReqBody.getAlbumName(), myConnector);
		} catch (Exception e) {
			return new ResponseEntity<Integer>(1, HttpStatus.OK);
		}

		return new ResponseEntity<Integer>(0, HttpStatus.OK);
	}

	@PostMapping("/public/albums/read")
	public ResponseEntity<List<Album>> readPublicAlbums(@RequestBody Album idenReqBody)
			throws SQLException, IOException {
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();

		List<Album> tr = ResultSetConvertor
				.convertToAlbumList(AlbumRead.readPublicAlbumsFromDB(myConnector));

		return new ResponseEntity<List<Album>>(tr, HttpStatus.OK);
	}

	@PostMapping("/public/albums/delete")
	public ResponseEntity<Integer> deletePublicAlbum(@RequestBody Album idenReqBody)
			throws SQLException {
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();
		try {
			AlbumDelete.deleteAlbum("GuestAccount", idenReqBody.getAlbumName(), myConnector);
		} catch (Exception e) {
			return new ResponseEntity<Integer>(1, HttpStatus.OK);
		}

		return new ResponseEntity<Integer>(0, HttpStatus.OK);
	}
	
	/**************************************************
	 ********** Public Image CRUD operations **********
	 **************************************************/
	
	/**
	 * sends image information to DAL to read image
	 * 
	 * @param type Image containing account information
	 * @throws IOException 
	 */
	@PostMapping("/public/images/read")
	public ResponseEntity<List<Image>> getPublicPicturesByAlbumAndAccountName(@RequestBody Image idenReqBody)
			throws SQLException, IOException {
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();

		List<Image> tr = ResultSetConvertor.convertToImageList(ImageRead
				.readPictures("GuestAccount", idenReqBody.getAlbumName(), myConnector));

		// get the base64 encodings and then return the list
		for (int i = 0; i < tr.size(); i++) {
			HttpPost post = new HttpPost("http://" + System.getenv("mediaServiceIP") + ":8080/photos/get");
			// add request parameter, form parameters
			List<NameValuePair> urlParameters = new ArrayList<>();
			urlParameters.add(new BasicNameValuePair("accountName", idenReqBody.getAccountName()));
			urlParameters.add(new BasicNameValuePair("albumName", idenReqBody.getAlbumName()));
			urlParameters.add(new BasicNameValuePair("photoName", tr.get(i).getPictureName()));
			urlParameters.add(new BasicNameValuePair("photoExtension", tr.get(i).getPictureExtension()));

			post.setEntity(new UrlEncodedFormEntity(urlParameters));
			try (CloseableHttpClient httpClient = HttpClients.createDefault();
					CloseableHttpResponse response = httpClient.execute(post)) {
				tr.get(i).setBase64Encoding(
						new JSONObject(EntityUtils.toString(response.getEntity())).getString("base64Encoding"));
			} catch (Exception e) {
				return new ResponseEntity<List<Image>>(HttpStatus.OK);
			}
		}

		myConnector.closeJDBCConnection();
		return new ResponseEntity<List<Image>>(tr, HttpStatus.OK);
	}

	/**
	 * sends image information to DAL to create image
	 * 
	 * @param type Image containing account information
	 * @throws IOException 
	 */
	@PostMapping("/public/images/create")
	public ResponseEntity<Integer> postPublicImage(@RequestBody Image idenReqBody)
			throws SQLException, IOException {
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();
		String idenReqOriginalName = idenReqBody.getPictureName();

		int matchingRecordsCount = ResultSetConvertor.countFromResultSet(
				ImageRead.readPictureCountByAccountAlbumPicture("GuestAccount", idenReqBody.getAlbumName(), idenReqBody.getNewPictureName(), myConnector), "NumberOfPictures");
		int duplicate = 0;
		while (matchingRecordsCount > 0) {
			duplicate += 1;
			matchingRecordsCount = ResultSetConvertor.countFromResultSet(
					ImageRead.readPictureCountByAccountAlbumPicture("GuestAccount", idenReqBody.getAlbumName(),
							idenReqOriginalName + "(" + String.valueOf(duplicate) + ")", myConnector), "NumberOfPictures");
		}
		if (duplicate > 0) {
			idenReqBody.setPictureName(idenReqOriginalName + "(" + String.valueOf(duplicate) + ")");
		}

		HttpPost post = new HttpPost("http://" + System.getenv("mediaServiceIP") + ":8080/photos/upload");

		// add request parameter, form parameters
		List<NameValuePair> urlParameters = new ArrayList<>();
		urlParameters.add(new BasicNameValuePair("accountName", idenReqBody.getAccountName()));
		urlParameters.add(new BasicNameValuePair("albumName", idenReqBody.getAlbumName()));
		urlParameters.add(new BasicNameValuePair("photoName", idenReqBody.getPictureName()));
		urlParameters.add(new BasicNameValuePair("base64Encoding", idenReqBody.getBase64Encoding()));

		post.setEntity(new UrlEncodedFormEntity(urlParameters));
		JSONObject image;
		try (CloseableHttpClient httpClient = HttpClients.createDefault();
				CloseableHttpResponse response = httpClient.execute(post)) {
			if (response == null) {
				return new ResponseEntity<Integer>(1, HttpStatus.OK);
			}
			image = new JSONObject(EntityUtils.toString(response.getEntity()));
		} catch (Exception e) {
			return new ResponseEntity<Integer>(1, HttpStatus.OK);
		}

		try {
			ImageCreation.addPicture(idenReqBody.getAccountName(), idenReqBody.getPictureName(),
					idenReqBody.getAlbumName(), image.getString("photoExtension"), myConnector);

			myConnector.closeJDBCConnection();
		} catch (Exception e) {
			return new ResponseEntity<Integer>(1, HttpStatus.OK);
		}

		return new ResponseEntity<Integer>(0, HttpStatus.OK);
	}

	/*
	 * Takes accountName, albumName, pictureName and deletes entry from DB
	 * idenReqBody requires: String: accountName String: pictureName String:
	 * albumName
	 */
	/**
	 * sends image information to DAL to delete image
	 * 
	 * @param type Image containing account information
	 */
	@PostMapping("/public/images/delete")
	public ResponseEntity<Integer> deletePublicImage(@RequestBody Image idenReqBody)
			throws SQLException, IOException {
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();

		ImageDelete.deletePicture("GuestAccount", idenReqBody.getPictureName(),
				idenReqBody.getAlbumName(), myConnector);

		myConnector.closeJDBCConnection();

		HttpPost post = new HttpPost("http://" + System.getenv("mediaServiceIP") + ":8080/photos/delete");

		// add request parameter, form parameters
		List<NameValuePair> urlParameters = new ArrayList<>();
		urlParameters.add(new BasicNameValuePair("accountName", idenReqBody.getAccountName()));
		urlParameters.add(new BasicNameValuePair("albumName", idenReqBody.getAlbumName()));
		urlParameters.add(new BasicNameValuePair("photoName", idenReqBody.getPictureName()));

		post.setEntity(new UrlEncodedFormEntity(urlParameters));
		try (CloseableHttpClient httpClient = HttpClients.createDefault();
				CloseableHttpResponse response = httpClient.execute(post)) {
		} catch (Exception e) {
			return new ResponseEntity<Integer>(1, HttpStatus.OK);
		}

		return new ResponseEntity<Integer>(0, HttpStatus.OK);
	}
	
}
