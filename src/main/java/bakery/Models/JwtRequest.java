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
		//test
		String s = null;
		return userName;
	}
	
	@JsonIgnore
	public String getPassword() {
		return password;
	}
}
