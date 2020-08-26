package bakery.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO for all baked goods. Used during deserialization 
 * in the StoreController
 * @author barney
 *
 */
@JsonInclude(Include.NON_NULL)
public class BakedItem {

	@JsonProperty
	private String category;
	@JsonProperty
	private String size;
	@JsonProperty
	private String shape;
	@JsonProperty
	private String count;
	@JsonProperty
	private String flavor;
	@JsonProperty
	private String layers;
	@JsonProperty
	private String calories;
	@JsonProperty
	private String toppings;
	@JsonProperty
	private String fillings;
	@JsonProperty
	private String vegan;
	@JsonProperty
	private String glutenFree;
	@JsonProperty
	private String expirationTime;

	//jenkins test comment
	@JsonIgnore
	public int getExpirationTime() {
		return Integer.parseInt(expirationTime);
	}
}
