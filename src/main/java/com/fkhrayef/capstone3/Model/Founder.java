package com.fkhrayef.capstone3.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

// TODO DTOin cuz we don't take isFounder, aiUsageCount from user

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Check(constraints = "equityPercentage >= 0 AND equityPercentage <= 100")
public class Founder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "Name cannot be null")
    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;

    @NotEmpty(message = "Email cannot be null")
    @Email(message = "Email is not valid")
    @Column(columnDefinition = "VARCHAR(255) NOT NULL UNIQUE")
    private String email;

    @NotEmpty(message = "Phone cannot be null")
    @Pattern(regexp = "^(\\+9665[0-9]\\d{8})$", message = "Phone number is not valid")
    @Column(columnDefinition = "VARCHAR(13) NOT NULL")
    private String phone;

    @NotNull(message = "Equity percentage cannot be null")
    @Column(columnDefinition = "DOUBLE NOT NULL")
    private Double equityPercentage;

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean hasStartup; // TODO start of as false, when he creates a startup or joins one it becomes true

    @Column(columnDefinition = "INTEGER DEFAULT 0")
    private Integer aiUsageCount; // TODO should be reset everyday (cron job)

    // relations
    @ManyToOne
    @JsonIgnore
    private Startup startup;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "founder")
    @PrimaryKeyJoinColumn
    private Subscription subscription;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
