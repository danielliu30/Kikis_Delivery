package bakery.Models;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class SingleCustomer {
	@JsonProperty
	private String email;
	@JsonProperty
	private String name;
	@JsonProperty
	private String member;
	@JsonProperty
	private String updated = LocalDateTime.now().toString(); 
}
