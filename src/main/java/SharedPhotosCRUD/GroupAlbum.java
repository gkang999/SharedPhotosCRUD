package SharedPhotosCRUD;

public class GroupAlbum {
	private String albumName;
	private String groupName;
	private String accountName;
	
	GroupAlbum(){
	
	}
	
	public GroupAlbum(String groupName, String albumName, String accountName) {
		this.setGroupName(groupName);
		this.setAlbumName(albumName);
		this.setAccountName(accountName);
	}
	
	public String getAlbumName() {
		return albumName;
	}
	public void setAlbumName(String accountName) {
		this.albumName = accountName;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}	
	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
}
