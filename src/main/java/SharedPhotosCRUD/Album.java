package SharedPhotosCRUD;

public class Album {
	private String albumName;	
	private String accountName;	
	private int publicStatus;
	
	public String getAlbumName() {
		return albumName;
	}
	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public int getPublicStatus() {
		return publicStatus;
	}
	public void setPublicStatus(int publicStatus) {
		this.publicStatus = publicStatus;
	}
}
