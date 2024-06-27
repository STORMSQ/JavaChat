package tw.com.stormsq.model;

public class User {


	private String name;
	private int userId;
	private boolean isLogin=false;

	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public User(String name) {
		super();

		this.name = name;
	}
	public User(int userid,String name) {
		super();
		this.userId = userid;
		this.name = name;
	}
	@Override
	public String toString() {
		return "User [name=" + name + ", userId=" + userId + "]";
	}
	public boolean isLogin() {
		return isLogin;
	}
	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + userId;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (userId != other.userId)
			return false;
		return true;
	}
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}


	
	
}
