package bakery.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO for all baked goods. Used during deserialization 
 * in the StoreController
 * @author barney
 *
 */

@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class BakedItem {

	@JsonProperty
	public String BakedItem;
	@JsonProperty
	public String size;
	@JsonProperty
	public String shape;
	@JsonProperty
	public String count;
	@JsonProperty
	public String flavor;
	@JsonProperty
	public String layers;
	@JsonProperty
	public String calories;
	@JsonProperty
	public String toppings;
	@JsonProperty
	public String fillings;
	@JsonProperty
	public String vegan;
	@JsonProperty
	public String glutenFree;
	@JsonProperty
	public String expirationTime;
	@JsonProperty
	public String cost;
	@JsonProperty
	public String ItemVariation;
	
	//jenkins test comment
	@JsonIgnore
	public int getExpirationTime() {
		return Integer.parseInt(expirationTime);
	}
}
