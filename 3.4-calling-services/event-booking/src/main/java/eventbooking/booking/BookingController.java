package eventbooking.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class BookingController {

    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping("/api/events/{eventId}/bookings")
    public ResponseEntity<BookingResponse> book(@PathVariable Long eventId,
                                                @Valid @RequestBody BookRequest request) {
        BookingResponse created = service.book(eventId, request);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/bookings/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/api/bookings/{id}")
    public BookingResponse byId(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/api/bookings")
    public Page<BookingResponse> byCustomer(
            @RequestParam @Email String email,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.findByCustomer(email, pageable);
    }

    @GetMapping("/api/events/{eventId}/bookings")
    public Page<BookingResponse> byEvent(
            @PathVariable Long eventId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.findByEvent(eventId, pageable);
    }
}
