package server.helpers;

public class LoginCredentials {
	private String user, password;
	
	public LoginCredentials(String user, String password) {
		this.user = user;
		this.password = password;
	}
	
	public String getUser() {
		return new String(user);
	}
	
	public String getPassword() {
		return new String(password);
	}
}
