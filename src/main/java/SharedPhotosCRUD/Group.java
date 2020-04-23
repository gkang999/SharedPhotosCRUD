package SharedPhotosCRUD;

public class Group {
	private String groupName;
	private String groupOwner;
	
	public Group(String groupName, String groupOwner){
		this.groupName = groupName;
		this.groupOwner = groupOwner;
	}
	
	public Group() {
		
	}
	
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getGroupOwner() {
		return groupOwner;
	}
	public void setGroupOwner(String groupOwner) {
		this.groupOwner = groupOwner;
	}
}
