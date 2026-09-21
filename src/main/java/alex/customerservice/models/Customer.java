package alex.customerservice.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Förnamn måste anges.")
    @Size(max = 50, message = "Förnamn får innehålla högst 50 tecken.")
    private String firstName;

    @NotBlank(message = "Efternamn måste anges.")
    @Size(max = 50, message = "Efternamn får innehålla högst 50 tecken.")
    private String lastName;

    @NotBlank(message = "E-post måste anges.")
    @Email(message = "Ange en giltig e-postadress.")
    private String email;

    @Pattern(
            regexp = "^\\+?[0-9]{1,4}?[ .-]?[0-9]{6,12}$",
            message = "Telefonnummer är ogiltigt."
    )
    private String phone;
}