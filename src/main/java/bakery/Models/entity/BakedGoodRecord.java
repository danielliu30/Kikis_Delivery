package bakery.Models.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * A single baked good on the shelf. {@code itemVariation} is the creation
 * timestamp, which together with the category identifies one item.
 */
@Entity
@Table(name = "baked_goods")
@Getter
@Setter
public class BakedGoodRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "baked_item", nullable = false)
    private String bakedItem;

    @Column(name = "item_variation", nullable = false)
    private String itemVariation;

    @Column(name = "expiration_time")
    private LocalDateTime expirationTime;

    @Column(name = "size")
    private String size;

    @Column(name = "shape")
    private String shape;

    @Column(name = "count")
    private String count;

    @Column(name = "flavor")
    private String flavor;

    @Column(name = "layers")
    private String layers;

    @Column(name = "calories")
    private String calories;

    @Column(name = "toppings")
    private String toppings;

    @Column(name = "fillings")
    private String fillings;

    @Column(name = "vegan")
    private String vegan;

    @Column(name = "gluten_free")
    private String glutenFree;

    @Column(name = "cost")
    private String cost;
}
