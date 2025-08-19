package com.fkhrayef.capstone3.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
@Check(constraints = "status IN ('active', 'completed', 'cancelled')")
public class FreelancerProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // todo (manyToOne relation with freelancer.java)
    // private Freelancer freelancer

    // todo (manyToOne) relation with startup.java
    // private Startup startup

    @NotEmpty(message = "project can't be empty")
    @Size(min = 4, max = 30, message = "project name length between 4-30")
    @Column(columnDefinition = "varchar(30) not null")
    private String projectName;

    @NotEmpty(message = "description can't be empty")
    @Size(min = 4, max = 100, message = "project name length between 4-100")
    @Column(columnDefinition = "varchar(100) not null")
    private String description;


    @Pattern(regexp = "^(active|completed|cancelled)$", message = "status should be active|completed|cancelled")
    @Column(columnDefinition = "varchar(10)")
    private String status;

    @NotNull(message = "start date can't be empty")
    @Column(nullable = false)
    private LocalDate startDate;

    @NotNull(message = "end date can't be empty")
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
