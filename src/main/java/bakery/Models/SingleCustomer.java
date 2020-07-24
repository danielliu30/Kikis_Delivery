package bakery.Models;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SingleCustomer {
	@JsonProperty("email")
	private String email;
	@JsonProperty("name")
	private String name;
	@JsonProperty("member")
	private String memberStatus;
	@JsonProperty("Updated")
	private LocalDateTime time = LocalDateTime.now(); 
	public String getEmail() {
		return email;
	}

	public String getName() {
		return name;
	}
	
	public String getMemberStatus() {
		return memberStatus;
	}
	
	public String getLastUpdated() {
		return time.toString();
	}
}
