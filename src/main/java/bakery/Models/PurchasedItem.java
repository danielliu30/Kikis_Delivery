package bakery.Models;

import java.time.LocalDateTime;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchasedItem {
    @JsonProperty
    public String userName;
    @JsonProperty
    public String timeStampCreated;
    @JsonProperty
    public ArrayList<BakedGoods> orderList;

    @JsonIgnore
    public String timePurchased = LocalDateTime.now().toString();
}
