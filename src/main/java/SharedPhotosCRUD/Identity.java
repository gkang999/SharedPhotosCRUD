package SharedPhotosCRUD;

public class Identity {
	private String accountName;
	private String email;
	private String accountOwner;
	private String creationDate;
	private String roleType;
	private String oldAccountName;
	private String accountPass;
	
	public Identity() {
		
	}
	
	//response body read
	public Identity(String accName, String em, String accOwn, String rType){
		this.setAccountName(accName);
		this.setEmail(em);
		this.setAccountOwner(accOwn);
		this.setRoleType(rType);
	}
	
	public void print() {
		if(this.accountName!=null) {
			System.out.println("accountName:" + accountName);
		}
		if(this.email!=null) {
			System.out.println("email:" + email);
		}
		if(this.accountOwner!=null) {
			System.out.println("accountOwner:" + accountOwner);
		}
		if(this.creationDate!=null) {
			System.out.println("creationDate:" + creationDate);
		}
		if(this.roleType!=null) {
			System.out.println("roleType:" + roleType);
		}
		if(this.oldAccountName!=null) {
			System.out.println("oldAccountName:" + oldAccountName);
		}
		if(this.accountPass!=null) {
			System.out.println("accountPass:" + accountPass);
		}
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

	public String getAccountPass() {
		return accountPass;
	}

	public void setAccountPass(String password) {
		this.accountPass = password;
	}
}
