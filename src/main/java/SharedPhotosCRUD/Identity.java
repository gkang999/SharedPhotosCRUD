package SharedPhotosCRUD;

public class Identity {
	private String accountName;
	private String email;
	private String accountOwner;
	private String creationDate;
	private String roleType;
	private String oldAccountName;
	
	//response body read
	public Identity(String accName, String em, String accOwn, String creDate, String rType){
		this.setAccountName(accName);
		this.setEmail(em);
		this.setAccountOwner(accOwn);
		this.setCreationDate(creDate);
		this.setRoleType(rType);
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAccountOwner() {
		return accountOwner;
	}

	public void setAccountOwner(String accountOwner) {
		this.accountOwner = accountOwner;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}

	public String getOldAccountName() {
		return oldAccountName;
	}

	public void setOldAccountName(String oldAccountName) {
		this.oldAccountName = oldAccountName;
	}
}
