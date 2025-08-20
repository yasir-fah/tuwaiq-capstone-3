package com.fkhrayef.capstone3.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
// todo DTOin
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Check(constraints = "status IN ('active', 'completed', 'pending','rejected','accepted','cancelled') and " +
                    "total_amount >= 0 and " +
                    "estimated_hours >= 0")
public class FreelancerProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(columnDefinition = "varchar(30) not null")
    private String projectName;


    @Column(columnDefinition = "varchar(100) not null")
    private String description;


    @Pattern(regexp = "^(active|completed|pending|rejected|accepted|cancelled)$",
            message = "status should be active|completed|pending|rejected|accepted|cancelled")
    @Column(columnDefinition = "varchar(9)")
    private String status;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    // Pricing fields - only hourly pricing supported (validation moved to DTO)
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double totalAmount;

    @Column(columnDefinition = "DECIMAL(8,2)")
    private Double estimatedHours;

    // relations
    @ManyToOne
    @JsonIgnore
    private Freelancer freelancer;

    @ManyToOne
    @JsonIgnore
    private Startup startup;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
