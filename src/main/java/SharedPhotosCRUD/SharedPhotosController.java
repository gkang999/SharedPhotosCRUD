package SharedPhotosCRUD;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import IdentityDAL.*;
import ImageDAL.*;
import MySQLConnector.*;
import SharedPhotosUtils.PassHasher;
import CRUDUtils.*;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.javatuples.Triplet;

import java.time.LocalDateTime;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

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

	/**************************************************
	 ************ Account CRUD operations *************
	 **************************************************/

	/**
	 * sends account information to DAL to read account
	 * 
	 * @param type Identity containing account information
	 * @return List<Identity> should be containing the single requested entry
	 */
	@PostMapping("/accounts/read")
	public List<Identity> getAccountByAccountName(@RequestBody Identity idenReqBody) throws Exception {

		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();

		List<Identity> tr = ResultSetConvertor
				.convertToIdentityList(IdentityRead.readDataFromDB(idenReqBody.getAccountName(), myConnector));

		myConnector.closeJDBCConnection();
		return tr;
	}

	/**
	 * sends account information to DAL to create account
	 * 
	 * @param type Identity containing account information
	 * @return void
	 */
	@PostMapping("/accounts/create")
	public void postAccount(@RequestBody Identity idenReqBody) throws Exception {

		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();

		IdentityCreation.addDataToDB(idenReqBody.getAccountName(), idenReqBody.getEmail(),
				idenReqBody.getAccountOwner(), idenReqBody.getRoleType(),
				PassHasher.generateStrongPasswordHash(idenReqBody.getAccountPass()), myConnector);

		myConnector.closeJDBCConnection();
	}

	/**
	 * sends account information to DAL to validate account
	 * 
	 * @param type Identity containing account information
	 * @return void
	 */
	@PostMapping("/accounts/login")
	public List<String> validateAccount(@RequestBody Identity idenReqBody) throws Exception {

		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();

		List<Identity> tr = ResultSetConvertor
				.convertToIdentityList(IdentityRead.validateLogin(idenReqBody.getAccountName(),
						PassHasher.generateStrongPasswordHash(idenReqBody.getAccountPass()), myConnector));

		myConnector.closeJDBCConnection();

		if (tr.size() == 1) {
			UUID temp = UUID.randomUUID();
			this.SessionKeys.add(
					new Triplet<UUID, LocalDateTime, String>(temp, LocalDateTime.now(), idenReqBody.getAccountName()));
			List<String> ret = new ArrayList<String>();
			ret.add(temp.toString());
			return ret;
		}

		return null;
	}

	/**
	 * sends account information to DAL to delete account
	 * 
	 * @param type Identity containing account information
	 * @return void
	 */
	@DeleteMapping("/accounts/delete")
	public void deleteAccount(@RequestBody Identity idenReqBody) throws Exception {
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();

		IdentityDelete.deleteDataFromDB(idenReqBody.getAccountName(), myConnector);

		myConnector.closeJDBCConnection();
	}

	/**
	 * sends account information to DAL to update account
	 * 
	 * @param type Identity containing account information
	 * @return void
	 */
	@PutMapping("/accounts/update")
	public void updateAccount(@RequestBody Identity idenReqBody) throws Exception {
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();

		IdentityUpdate.updateDataToDB(idenReqBody.getOldAccountName(), idenReqBody.getAccountName(),
				idenReqBody.getEmail(), idenReqBody.getAccountOwner(), idenReqBody.getRoleType(), myConnector);

		myConnector.closeJDBCConnection();
	}

	/**************************************************
	 ************ Image CRUD operations ***************
	 **************************************************/

	/**
	 * sends image information to DAL to read image
	 * 
	 * @param type Image containing account information
	 * @return List<Image> should be containing the single requested entry
	 */
	@PostMapping("/images/read")
	public List<Image> getPicturesByAlbumAndAccountName(@RequestBody Image idenReqBody) throws Exception {
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();

		List<Image> tr = ResultSetConvertor.convertToImageList(ImageAndAlbumRead
				.readPicturesFromDB(idenReqBody.getAccountName(), idenReqBody.getAlbumName(), myConnector));

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
		return tr;
	}

	/**
	 * sends image information to DAL to create image
	 * 
	 * @param type Image containing account information
	 * @return 0 on success, 1 otherwise
	 */
	@PostMapping("/images/create")
	public int postImage(@RequestBody Image idenReqBody) throws Exception {
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();
		String idenReqOriginalName = idenReqBody.getPictureName();

		List<Image> tr = ResultSetConvertor.convertToImageList(ImageAndAlbumRead.readPictureFromDB(
				idenReqBody.getAccountName(), idenReqBody.getAlbumName(), idenReqBody.getPictureName(), myConnector));
		int duplicate = 0;
		System.out.println(tr.size());
		while (tr.size() > 0) {
			System.out.println("in while");
			duplicate += 1;
			tr = ResultSetConvertor.convertToImageList(
					ImageAndAlbumRead.readPictureFromDB(idenReqBody.getAccountName(), idenReqBody.getAlbumName(),
							idenReqOriginalName + "(" + String.valueOf(duplicate) + ")", myConnector));
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
				return 1;
			}
			image = new JSONObject(EntityUtils.toString(response.getEntity()));
		} catch (Exception e) {
			return 1;
		}

		try {
			ImageAndAlbumCreation.addPictureToDB(idenReqBody.getAccountName(), idenReqBody.getPictureName(),
					idenReqBody.getAlbumName(), image.getString("photoExtension"), myConnector);

			myConnector.closeJDBCConnection();
		} catch (Exception e) {
			return 1;
		}

		System.out.println(tr.size());
		return 0;
	}

	/*
	 * Takes accountName, albumName, pictureName and deletes entry from DB
	 * idenReqBody requires: String: accountName String: pictureName String:
	 * albumName
	 */
	@PostMapping("/images/delete")
	/**
	 * sends image information to DAL to delete image
	 * 
	 * @param type Image containing account information
	 * @return 0 on success, 1 otherwise
	 */
	@DeleteMapping("/images/delete")
	public int deleteImage(@RequestBody Image idenReqBody) throws Exception {
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();

		ImageAndAlbumDelete.deletePictureFromDB(idenReqBody.getAccountName(), idenReqBody.getPictureName(),
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
			return 1;
		}

		return 0;
	}

	/**
	 * sends image information to DAL to update account
	 * 
	 * @param type Image containing account information
	 * @return void
	 */
	@PutMapping("/images/update/name")
	public void updateImage(@RequestBody Image idenReqBody) throws Exception {
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();

		ImageUpdate.updatePictureNameToDB(idenReqBody.getAccountName(), idenReqBody.getAlbumName(),
				idenReqBody.getPictureName(), idenReqBody.getNewPictureName(), myConnector);

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

		myConnector.closeJDBCConnection();
	}

	/**
	 * sends image information to DAL to update album of image
	 * 
	 * @param type Image containing image information
	 * @return void
	 */
	@PutMapping("/images/update/album")
	public void updatePicturesAlbumToDB(@RequestBody Image idenReqBody) throws Exception {
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

		ImageUpdate.updatePicturesAlbumToDB(idenReqBody.getAccountName(), idenReqBody.getPictureName(),
				idenReqBody.getAlbumName(), idenReqBody.getNewAlbumName(), myConnector);

		myConnector.closeJDBCConnection();
	}

	/**************************************************
	 ************ Album CRUD operations ***************
	 **************************************************/

	/**
	 * sends album information to DAL to create album
	 * 
	 * @param type Album containing album information
	 * @return 0 on success
	 */
	@PostMapping("/albums/create")
	public int createAlbum(@RequestBody Album idenReqBody) {
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();
		try {
			ImageAndAlbumCreation.addAlbumToDB(idenReqBody.getAccountName(), idenReqBody.getAlbumName(), myConnector);
		} catch (Exception e) {
			return 1;
		}

		return 0;
	}

	/**
	 * sends album information to DAL to read album
	 * 
	 * @param type Album containing album information
	 * @return List<Album> should contain single album requested
	 */
	@PostMapping("/albums/read")
	public List<Album> readAlbums(@RequestBody Album idenReqBody, @RequestHeader Map<String, String> headers)
			throws SQLException {
		System.out.println(headers.get("Content-Type"));
		System.out.println(headers.get("spdksessionKey"));
		if (!this.isValid(headers.get("SPDKSessionKey"), idenReqBody.getAccountName())) {
			return null;
		}
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();
		List<Album> tr = ResultSetConvertor
				.convertToAlbumList(ImageAndAlbumRead.readAlbumsFromDB(idenReqBody.getAccountName(), myConnector));

		return tr;
	}

	/**
	 * sends image information to DAL to delete album
	 * 
	 * @param type Album containing album information
	 * @return 0 on success
	 */
	@PostMapping("/albums/delete")
	public int deleteAlbum(@RequestBody Album idenReqBody) {
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();
		try {
			ImageAndAlbumDelete.deleteAlbumFromDB(idenReqBody.getAccountName(), idenReqBody.getAlbumName(),
					myConnector);
		} catch (Exception e) {
			return 1;
		}

		return 0;
	}

}
