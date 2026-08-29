package bakery.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import bakery.Models.entity.BakedGoodRecord;

@Repository
public interface BakedGoodRepository extends JpaRepository<BakedGoodRecord, Long> {

    List<BakedGoodRecord> findByBakedItem(String bakedItem);

    Optional<BakedGoodRecord> findByBakedItemAndItemVariation(String bakedItem, String itemVariation);

    @Query("select distinct b.bakedItem from BakedGoodRecord b order by b.bakedItem")
    List<String> findDistinctCategories();
}
