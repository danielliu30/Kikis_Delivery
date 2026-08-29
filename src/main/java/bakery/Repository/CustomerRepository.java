package bakery.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bakery.Models.entity.CustomerRecord;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerRecord, String> {
}
