package alex.customerservice.client;

import alex.customerservice.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class BookingClient {

    private final RestTemplate restTemplate;
    private final String bookingServiceUrl;
    private final JwtService jwtService;

    public BookingClient(
            @Value("${booking.service.url}") String bookingServiceUrl,
            JwtService jwtService) {

        SimpleClientHttpRequestFactory factory =
                new SimpleClientHttpRequestFactory();

        factory.setConnectTimeout(3000);
        factory.setReadTimeout(5000);

        this.restTemplate = new RestTemplate(factory);
        this.bookingServiceUrl = bookingServiceUrl.replaceAll("/+$", "");
        this.jwtService = jwtService;
    }

    public boolean hasActiveBookings(Long customerId) {

        String token = jwtService.generateToken("customer-service");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Boolean> response = restTemplate.exchange(
                bookingServiceUrl
                        + "/api/bookings/customer/"
                        + customerId
                        + "/active",
                HttpMethod.GET,
                entity,
                Boolean.class
        );

        return Boolean.TRUE.equals(response.getBody());
    }
}