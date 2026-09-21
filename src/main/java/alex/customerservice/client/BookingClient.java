package alex.customerservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class BookingClient {

    private final RestTemplate restTemplate;
    private final String bookingServiceUrl;

    public BookingClient(
            @Value("${booking.service.url}") String bookingServiceUrl) {

        SimpleClientHttpRequestFactory factory =
                new SimpleClientHttpRequestFactory();

        factory.setConnectTimeout(3000);
        factory.setReadTimeout(5000);

        this.restTemplate = new RestTemplate(factory);
        this.bookingServiceUrl = bookingServiceUrl.replaceAll("/+$", "");
    }

    public boolean hasActiveBookings(Long customerId) {
        Boolean result = restTemplate.getForObject(
                bookingServiceUrl
                        + "/api/bookings/customer/"
                        + customerId
                        + "/active",
                Boolean.class
        );

        return Boolean.TRUE.equals(result);
    }
}