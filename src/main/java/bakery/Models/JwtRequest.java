package bakery.Models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JwtRequest implements Serializable {
	@JsonProperty
	private String userName;
	@JsonProperty
	private String password;
	
	public String getUserName() {
		return userName;
	}
	
	public String getPassword() {
		return password;
	}
}
