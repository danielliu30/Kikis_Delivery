package bakery.Models;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonProperty;

//May merge with JWTRequest. 
@JsonInclude(Include.NON_NULL)
public class SingleCustomer {
	@JsonProperty
	private String email;
	@JsonProperty
	private String password;
	@JsonProperty
	private String name;
	@JsonProperty
	private String member;
	@JsonProperty
	private String admin;
	@JsonProperty
	private String updated = LocalDateTime.now().toString(); 

	@JsonIgnore
	public String getEmail(){
		return email;
	}

	@JsonIgnore
	public String getPassWord(){
		return password;
	}
}
