package alex.customerservice.service;

import alex.customerservice.models.Customer;
import alex.customerservice.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import alex.customerservice.client.BookingClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final BookingClient bookingClient;

    @Transactional(readOnly = true)
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Transactional
    public Customer create(Customer input) {
        Customer customer = new Customer();
        customer.setFirstName(input.getFirstName());
        customer.setLastName(input.getLastName());
        customer.setEmail(input.getEmail());
        customer.setPhone(input.getPhone());

        return customerRepository.save(customer);
    }

    @Transactional(readOnly = true)
    public Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Kunden med id " + id + " finns inte."
                ));
    }

    @Transactional
    public Customer update(Long id, Customer input) {
        Customer customer = findById(id);

        customer.setFirstName(input.getFirstName());
        customer.setLastName(input.getLastName());
        customer.setEmail(input.getEmail());
        customer.setPhone(input.getPhone());

        return customerRepository.save(customer);
    }
    @Transactional
    public void delete(Long id) {
        Customer customer = findById(id);

        try {
            if (bookingClient.hasActiveBookings(id)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Kunden kan inte tas bort eftersom kunden har en aktiv bokning."
                );
            }
        } catch (RestClientException exception) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Kunden kan inte tas bort eftersom Booking Service inte är tillgänglig."
            );
        }

        customerRepository.delete(customer);
    }
}