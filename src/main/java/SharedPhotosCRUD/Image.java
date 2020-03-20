package SharedPhotosCRUD;

public class Image {
	private String pictureName;//*
	private String accountName;
	private String albumName;
	private String newPictureName;
	private String newAlbumName;
	private String pictureExtension;//*
	private String base64Encoding;//*

	public String getPictureName() {
		return pictureName;
	}

	public void setPictureName(String pictureName) {
		this.pictureName = pictureName;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getAlbumName() {
		return albumName;
	}

	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}

	public String getNewPictureName() {
		return newPictureName;
	}

	public void setNewPictureName(String newPictureName) {
		this.newPictureName = newPictureName;
	}

	public String getNewAlbumName() {
		return newAlbumName;
	}

	public void setNewAlbumName(String newAlbumName) {
		this.newAlbumName = newAlbumName;
	}

	public String getBase64Encoding() {
		return base64Encoding;
	}

	public void setBase64Encoding(String base64Encoding) {
		this.base64Encoding = base64Encoding;
	}

	public String getPictureExtension() {
		return pictureExtension;
	}

	public void setPictureExtension(String pictureExtension) {
		this.pictureExtension = pictureExtension;
	}
}
