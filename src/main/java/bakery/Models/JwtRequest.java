package bakery.Models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class JwtRequest{
	
	@JsonProperty
	private String userName;
	@JsonProperty
	private String password;
	
	@JsonIgnore
	public String getUserName() {
		return userName;
	}
	
	@JsonIgnore
	public String getPassword() {
		return password;
	}
}
