package bakery.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bakery.Models.entity.StoreFrontRecord;

@Repository
public interface StoreFrontRepository extends JpaRepository<StoreFrontRecord, String> {
}
