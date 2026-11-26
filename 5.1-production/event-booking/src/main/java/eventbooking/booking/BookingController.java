package eventbooking.booking;

import jakarta.validation.Valid;
import eventbooking.payment.PaymentDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;

@RestController
public class BookingController {

    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping("/api/events/{eventId}/bookings")
    public ResponseEntity<BookingResponse> book(@PathVariable Long eventId,
                                                @Valid @RequestBody BookRequest request,
                                                Principal customer) {
        BookingResponse created = service.book(eventId, request, customer.getName());
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
            Principal customer,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.findByCustomer(customer.getName(), pageable);
    }

    @GetMapping("/api/events/{eventId}/bookings")
    public Page<BookingResponse> byEvent(
            @PathVariable Long eventId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.findByEvent(eventId, pageable);
    }

    @GetMapping("/api/bookings/{id}/payment")
    public PaymentDetails payment(@PathVariable Long id) {
        return service.paymentOf(id);
    }
}
