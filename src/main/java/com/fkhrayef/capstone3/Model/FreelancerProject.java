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
@Check(constraints = "status IN ('active', 'completed', 'pending','rejected','accepted','cancelled')")
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
