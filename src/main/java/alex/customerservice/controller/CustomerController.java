package alex.customerservice.controller;

import alex.customerservice.models.Customer;
import alex.customerservice.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public List<Customer> findAll() {
        return customerService.findAll();
    }

    @PostMapping
    public ResponseEntity<Customer> create(
            @Valid @RequestBody Customer customer) {

        Customer savedCustomer = customerService.create(customer);

        return ResponseEntity
                .created(URI.create("/api/customers/" + savedCustomer.getId()))
                .body(savedCustomer);
    }

    @GetMapping("/{id}")
    public Customer findById(@PathVariable Long id) {
        return customerService.findById(id);
    }

    @PutMapping("/{id}")
    public Customer update(
            @PathVariable Long id,
            @Valid @RequestBody Customer customer) {

        return customerService.update(id, customer);
    }
}