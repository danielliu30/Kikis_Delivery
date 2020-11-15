package bakery.Models;

import java.time.LocalDateTime;

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
    public String category;
    @JsonProperty
    public String timeStampCreated;
    @JsonIgnore
    public String timePurchased = LocalDateTime.now().toString();
}
