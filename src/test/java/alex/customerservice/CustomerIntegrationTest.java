package alex.customerservice;

import alex.customerservice.models.Customer;
import alex.customerservice.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class CustomerIntegrationTest {

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
    }

    @Test
    void shouldCreateCustomer() {
        Customer customer = new Customer();
        customer.setFirstName("Alex");
        customer.setLastName("Zeljic");
        customer.setEmail("alex@test.se");
        customer.setPhone("0701234567");

        Customer savedCustomer = customerRepository.save(customer);

        assertNotNull(savedCustomer.getId());
        assertEquals("Alex", savedCustomer.getFirstName());
    }

    @Test
    void shouldFindCustomerById() {
        Customer customer = new Customer();
        customer.setFirstName("Anna");
        customer.setLastName("Andersson");
        customer.setEmail("anna@test.se");
        customer.setPhone("0701112233");

        Customer savedCustomer = customerRepository.save(customer);

        Customer foundCustomer = customerRepository
                .findById(savedCustomer.getId())
                .orElse(null);

        assertNotNull(foundCustomer);
        assertEquals("Anna", foundCustomer.getFirstName());
        assertEquals("Andersson", foundCustomer.getLastName());
    }

    @Test
    void shouldUpdateCustomer() {
        Customer customer = new Customer();
        customer.setFirstName("Erik");
        customer.setLastName("Svensson");
        customer.setEmail("erik@test.se");
        customer.setPhone("0709998877");

        Customer savedCustomer = customerRepository.save(customer);

        savedCustomer.setFirstName("Erik Updated");
        customerRepository.save(savedCustomer);

        Customer updatedCustomer = customerRepository
                .findById(savedCustomer.getId())
                .orElseThrow();

        assertEquals("Erik Updated", updatedCustomer.getFirstName());
    }
}