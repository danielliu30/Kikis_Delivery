package bakery.Models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO for all baked goods. Used during deserialization in the StoreController
 * 
 * @author barney
 *
 */

@DynamoDBTable(tableName = "BakedGoods")
@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class BakedGoods {

	
	@JsonProperty
	@DynamoDBHashKey(attributeName = "BakedItem")
	public String BakedItem;

	@DynamoDBIndexHashKey(attributeName = "ItemVariation")
	@JsonProperty
	public String ItemVariation;

	@DynamoDBAttribute(attributeName = "size")
	@JsonProperty
	public String size;

	@DynamoDBAttribute(attributeName = "shape")
	@JsonProperty
	public String shape;

	@DynamoDBAttribute(attributeName = "count")
	@JsonProperty
	public String count;

	@DynamoDBAttribute(attributeName = "flavor")
	@JsonProperty
	public String flavor;

	@DynamoDBAttribute(attributeName = "layers")
	@JsonProperty
	public String layers;

	@DynamoDBAttribute(attributeName = "calories")
	@JsonProperty
	public String calories;

	@DynamoDBAttribute(attributeName = "toppings")
	@JsonProperty
	public String toppings;

	@DynamoDBAttribute(attributeName = "fillings")
	@JsonProperty
	public String fillings;

	@DynamoDBAttribute(attributeName = "vegan")
	@JsonProperty
	public String vegan;

	@DynamoDBAttribute(attributeName = "glutenFree")
	@JsonProperty
	public String glutenFree;

	@DynamoDBAttribute(attributeName = "expirationTime")
	@JsonProperty
	public String expirationTime;

	@DynamoDBAttribute(attributeName = "cost")
	@JsonProperty
	public String cost;

	// jenkins test comment
	@JsonIgnore
	public int getExpirationTime() {
		return Integer.parseInt(expirationTime);
	}
}
