package bakery.Repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import bakery.Models.entity.StoreFrontRecord;

@Repository
public interface StoreFrontRepository extends JpaRepository<StoreFrontRecord, String> {

    /**
     * Adds to a storefront running total in a single statement, inserting the row
     * if it is missing, so concurrent callers can neither lose an amount nor
     * collide on the primary key.
     *
     * @param id     storefront row to credit, e.g. {@code TotalRevenue}
     * @param amount value added to the stored total; negative values subtract
     */
    @Modifying
    @Query(value = "insert into store_front (id, total_money_made) values (:id, :amount) "
            + "on conflict (id) do update set total_money_made = store_front.total_money_made + :amount",
            nativeQuery = true)
    void addRevenue(@Param("id") String id, @Param("amount") BigDecimal amount);
}
