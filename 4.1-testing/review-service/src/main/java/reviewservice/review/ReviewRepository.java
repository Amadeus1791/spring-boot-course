package reviewservice.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByEventId(Long eventId, Pageable pageable);

    long countByEventId(Long eventId);

    // A derived query knows count, but no average: JPQL. Over no rows, avg is null.
    @Query("select avg(r.stars) from Review r where r.eventId = :eventId")
    Double averageStars(Long eventId);
}
