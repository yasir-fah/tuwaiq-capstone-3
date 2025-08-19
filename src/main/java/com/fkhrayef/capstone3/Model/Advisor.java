package com.fkhrayef.capstone3.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Check( constraints = "hourly_rate >= 0 and " +
                      "LENGTH(name) >= 3 and " +
                      "LENGTH(email) >= 3")
//todo DTO
public class Advisor {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "Name is required")
    @Size(min = 3, max = 200, message = "Name must be between 3 and 200 characters")
    @Column(columnDefinition = "VARCHAR(200)")
    private String name;

    @NotEmpty(message = "Email is required")
    @Size(min = 3, max = 255, message = "Email must be between 3 and 255 characters")
    @Email(message = "Email must be valid")
    @Column(columnDefinition = "VARCHAR(255) unique")
    private String email;

    @NotEmpty(message = "Phone cannot be null")
    @Pattern(regexp = "^(\\+9665[0-9]\\d{8})$")
    @Column(columnDefinition = "VARCHAR(13)")
    private String phone;

    @NotEmpty(message = "Expertise Area is required")
    // todo Adding expertiseAreas pattern
    private String expertiseArea;

    @NotNull(message = "Years Experience is required")
    @PositiveOrZero(message = "Years Experience must be greater than or equal to zero")
    @Max(value = 100, message = "Years Experience must be less than or equal to 100")
    @Column(columnDefinition = "INTEGER")
    private Integer yearsExperience;

    @NotNull(message = "Hourly Rate is required")
    @PositiveOrZero(message = "Hourly Rate must be greater than or equal to zero")
    @Column(columnDefinition = "DECIMAL(8,2)")
    private Double hourlyRate;

    @NotNull(message = "Availability Status is required")
    @Column(columnDefinition = "BOOLEAN")
    private Boolean isAvailable;


    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
