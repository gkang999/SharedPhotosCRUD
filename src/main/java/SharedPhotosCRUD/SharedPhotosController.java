package SharedPhotosCRUD;

import java.util.List;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import IdentityDAL.*;
import ImageDAL.*;
import MySQLConnector.*;
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

import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestBody;

@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RestController
public class SharedPhotosController {

	@PostMapping("/accounts/readAll")
	public List<Identity> getAllAccounts() throws Exception {
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();

		List<Identity> tr = ResultSetConvertor.convertToIdentityList(IdentityRead.readAllDataFromDB(myConnector));

		myConnector.closeJDBCConnection();
		return tr;
	}
	
	/*idenReqBody requires:
	 * String: accountName
	 */
	@PostMapping("/accounts/read")
	public List<Identity> getAccountByAccountName(@RequestBody Identity idenReqBody) throws Exception {
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();

		List<Identity> tr = ResultSetConvertor.convertToIdentityList(IdentityRead.readDataFromDB(idenReqBody.getAccountName(), myConnector));

		myConnector.closeJDBCConnection();
		return tr;
	}
	
	/*idenReqBody requires:
	 * String: accountName
	 * String: email
	 * String: accountOwner
	 * String: roleType
	 */
	@PostMapping("/accounts/create")
	public void postAccount(@RequestBody Identity idenReqBody) throws Exception {
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();

		
		IdentityCreation.addDataToDB(
				idenReqBody.getAccountName(), 
				idenReqBody.getEmail(),
				idenReqBody.getAccountOwner(),
				idenReqBody.getRoleType(),
				myConnector);

		myConnector.closeJDBCConnection();
	}
	
	/*idenReqBody requires:
	 * String: accountName
	 */
	@DeleteMapping("/accounts/delete")
	public void deleteAccount(@RequestBody Identity idenReqBody) throws Exception {
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();
		
		IdentityDelete.deleteDataFromDB(
				idenReqBody.getAccountName(),
				myConnector);

		myConnector.closeJDBCConnection();
	}
	
	/*idenReqBody requires:
	 * String: oldAccountName
	 * String: newAccountName
	 * String: email
	 * String: accountOwner
	 * String: roleType
	 */
	@PutMapping("/accounts/update")
	public void updateAccount(@RequestBody Identity idenReqBody) throws Exception {
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();
		
		IdentityUpdate.updateDataToDB(
				idenReqBody.getOldAccountName(), 
				idenReqBody.getAccountName(),
				idenReqBody.getEmail(),
				idenReqBody.getAccountOwner(),
				idenReqBody.getRoleType(),
				myConnector);

		myConnector.closeJDBCConnection();
	}
	
	/***
	 * Image CRUD operations
	 */
	
	/* Takes album name and retrieves list of pictures
	 * idenReqBody requires:
	 * String: albumName
	 * String: accountName
	 */
	@PostMapping("/images/read")
	public List<Image> getPicturesByAlbumAndAccountName(@RequestBody Image idenReqBody) throws Exception {
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();

		List<Image> tr = ResultSetConvertor.convertToImageList(ImageAndAlbumRead.readPicturesFromDB(idenReqBody.getAccountName(), idenReqBody.getAlbumName(), myConnector));
		
		//get the base64 encodings and then return the list
		for(int i = 0; i<tr.size(); i++) {
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
	    		tr.get(i).setBase64Encoding(new JSONObject(EntityUtils.toString(response.getEntity())).getString("base64Encoding"));
	        }
		}
		
		myConnector.closeJDBCConnection();
		return tr;
	}
	
	
	
	/* Takes picture information and creates entry for that picture in DB
	 * idenReqBody requires:
	 * String: pictureName
	 * String: accountName
	 * String: albumName
	 * String: base64Encoding
	 */
	@PostMapping("/images/create")
	public int postImage(@RequestBody Image idenReqBody) throws Exception {
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();
		String idenReqOriginalName = idenReqBody.getPictureName();

		List<Image> tr = ResultSetConvertor.convertToImageList(ImageAndAlbumRead.readPictureFromDB(idenReqBody.getAccountName(), idenReqBody.getAlbumName(), idenReqBody.getPictureName(), myConnector));
		int duplicate = 0;
		System.out.println(tr.size());
		while(tr.size()>0) {
			System.out.println("in while");
			duplicate += 1;
			tr = ResultSetConvertor.convertToImageList(ImageAndAlbumRead.readPictureFromDB(idenReqBody.getAccountName(), idenReqBody.getAlbumName(), idenReqOriginalName+"("+String.valueOf(duplicate)+")", myConnector));
		}
		if(duplicate>0) {
			idenReqBody.setPictureName(idenReqOriginalName+"("+String.valueOf(duplicate)+")");
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
        	if(response == null) {
        		return 1;
        	}
    		image = new JSONObject(EntityUtils.toString(response.getEntity()));
        }

		ImageAndAlbumCreation.addPictureToDB(
				idenReqBody.getAccountName(),
				idenReqBody.getPictureName(),
				idenReqBody.getAlbumName(),
				image.getString("photoExtension"),
				myConnector);
		
		myConnector.closeJDBCConnection();

		System.out.println(tr.size());
		return 0;
	}
	
	/* Takes accountName, albumName, pictureName and deletes entry from DB
	 * idenReqBody requires:
	 * String: accountName
	 * String: pictureName
	 * String: albumName
	 */
	@PostMapping("/images/delete")
	public int deleteImage(@RequestBody Image idenReqBody) throws Exception {
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();
		
		ImageAndAlbumDelete.deletePictureFromDB(
				idenReqBody.getAccountName(),
				idenReqBody.getPictureName(),
				idenReqBody.getAlbumName(),
				myConnector);

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
	
	/*idenReqBody requires:
	 * String: accountName
	 * String: albumName
	 * String: pictureName
	 * String: newPictureName
	 */
	@PutMapping("/images/update/name")
	public void updateImage(@RequestBody Image idenReqBody) throws Exception {
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();
		
		ImageUpdate.updatePictureNameToDB(
				idenReqBody.getAccountName(), 
				idenReqBody.getAlbumName(),
				idenReqBody.getPictureName(),
				idenReqBody.getNewPictureName(),
				myConnector);

		Client client = ClientBuilder.newClient();
		
		//grab base64 encoding
		WebTarget target = client.target(System.getenv("mediaServiceIP") + ":8080/photos/get")
				.queryParam("accountName", idenReqBody.getAccountName())
				.queryParam("albumName", idenReqBody.getAlbumName())
				.queryParam("photoName", idenReqBody.getPictureName());
		idenReqBody.setBase64Encoding(target.request(MediaType.APPLICATION_JSON).get(Image.class).getBase64Encoding());
		
		//delete old image
		target = client.target(System.getenv("mediaServiceIP") + ":8080/photos/delete")
				.queryParam("accountName", idenReqBody.getAccountName())
				.queryParam("albumName", idenReqBody.getAlbumName())
				.queryParam("photoName", idenReqBody.getPictureName());
		
		//upload new image with new name
		target = client.target(System.getenv("mediaServiceIP") + ":8080/photos/upload")
				.queryParam("accountName", idenReqBody.getAccountName())
				.queryParam("albumName", idenReqBody.getAlbumName())
				.queryParam("photoName", idenReqBody.getNewPictureName())
				.queryParam("base64Encoding", idenReqBody.getBase64Encoding());

		myConnector.closeJDBCConnection();
	}
	
	/*idenReqBody requires:
	 * String: accountName
	 * String: pictureName
	 * String: newPictureLocation
	 * String: oldAlbumName
	 * String: newAlbumName
	 */
	@PutMapping("/images/update/album")
	public void updatePicturesAlbumToDB(@RequestBody Image idenReqBody) throws Exception {
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();

		Client client = ClientBuilder.newClient();
		
		//grab base64 encoding
		WebTarget target = client.target(System.getenv("mediaServiceIP") + ":8080/photos/get")
				.queryParam("accountName", idenReqBody.getAccountName())
				.queryParam("albumName", idenReqBody.getAlbumName())
				.queryParam("photoName", idenReqBody.getPictureName());
		idenReqBody.setBase64Encoding(target.request(MediaType.APPLICATION_JSON).get(Image.class).getBase64Encoding());
		
		//delete old image
		target = client.target(System.getenv("mediaServiceIP") + ":8080/photos/delete")
				.queryParam("accountName", idenReqBody.getAccountName())
				.queryParam("albumName", idenReqBody.getAlbumName())
				.queryParam("photoName", idenReqBody.getPictureName());
		
		//upload new image with new name
		target = client.target(System.getenv("mediaServiceIP") + ":8080/photos/upload")
				.queryParam("accountName", idenReqBody.getAccountName())
				.queryParam("albumName", idenReqBody.getNewAlbumName())
				.queryParam("photoName", idenReqBody.getPictureName())
				.queryParam("base64Encoding", idenReqBody.getBase64Encoding());
		
		ImageUpdate.updatePicturesAlbumToDB(
				idenReqBody.getAccountName(), 
				idenReqBody.getPictureName(),
				idenReqBody.getAlbumName(),
				idenReqBody.getNewAlbumName(),
				myConnector);

		myConnector.closeJDBCConnection();
	}
	
	/* idenReqBody requires:
	 * String: accountName
	 * String: albumName
	 */
	@PostMapping("/albums/create")
	public int createAlbum(@RequestBody Image idenReqBody) {
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();
		ImageAndAlbumCreation.addAlbumToDB(idenReqBody.getAccountName(), idenReqBody.getAlbumName(), myConnector);
		
		return 0;
	}
	
	/* idenReqBody requires:
	 * String: accountName
	 */
	@PostMapping("/albums/read")
	public List<Album> readAlbums(@RequestBody Image idenReqBody) throws SQLException {
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();
		List<Album> tr = ResultSetConvertor.convertToAlbumList(ImageAndAlbumRead.readAlbumsFromDB(idenReqBody.getAccountName(), myConnector));
		
		return tr;
	}
	
	/* idenReqBody requires:
	 * String: accountName
	 * String: albumName
	 */
	@PostMapping("/albums/delete")
	public int deleteAlbum(@RequestBody Image idenReqBody) {
		MySQLConnector myConnector = new MySQLConnector();
		myConnector.makeJDBCConnection();
		ImageAndAlbumDelete.deleteAlbumFromDB(idenReqBody.getAccountName(), idenReqBody.getAlbumName(), myConnector);
		
		return 0;
	}
	
}
