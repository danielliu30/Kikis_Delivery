package bakery.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bakery.Models.entity.CustomerOrderRecord;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrderRecord, Long> {
}
