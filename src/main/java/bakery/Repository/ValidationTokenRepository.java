package bakery.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bakery.Models.entity.ValidationTokenRecord;

@Repository
public interface ValidationTokenRepository extends JpaRepository<ValidationTokenRecord, String> {
}
