package eventbooking.event;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Chapter 3.2 looks inside: Spring Data implements this interface at startup
// and writes each query from its method name, for example findAllByOrderByStartsAt().
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByOrderByStartsAt();
}
