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

    @Modifying
    @Query("update StoreFrontRecord s set s.totalMoneyMade = s.totalMoneyMade + :amount where s.id = :id")
    int increment(@Param("id") String id, @Param("amount") BigDecimal amount);
}
