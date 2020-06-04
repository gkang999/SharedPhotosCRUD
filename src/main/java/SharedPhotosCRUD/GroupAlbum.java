package SharedPhotosCRUD;

public class GroupAlbum {
	private String groupName;
	private String albumName;
	private String accountName;
	
	public GroupAlbum(String groupName, String albumName) {
		this.setGroupName(groupName);
		this.setAlbumName(albumName);
	}
	
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getAlbumName() {
		return albumName;
	}
	public void setAlbumName(String accountName) {
		this.albumName = accountName;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
}
