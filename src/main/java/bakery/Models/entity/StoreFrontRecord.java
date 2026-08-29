package bakery.Models.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * Storefront totals, one row per named total such as {@code TotalRevenue}.
 */
@Entity
@Table(name = "store_front")
@Getter
@Setter
public class StoreFrontRecord {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "total_money_made", nullable = false)
    private BigDecimal totalMoneyMade = BigDecimal.ZERO;
}
