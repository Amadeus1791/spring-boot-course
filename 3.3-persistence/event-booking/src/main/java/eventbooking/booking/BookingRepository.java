package eventbooking.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByCustomerEmail(String customerEmail, Pageable pageable);

    @Query("select coalesce(sum(b.seats), 0) from Booking b where b.event.id = :eventId")
    long countBookedSeats(Long eventId);
}
