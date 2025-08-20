package com.fkhrayef.capstone3.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Check(constraints = "years_experience >= 0")
@Check(constraints = "rating >= 0")
@Check(constraints = "available_balance >= 0")
@Check(constraints = "pending_balance >= 0")
@Check(constraints = "total_earnings >= 0")
public class Freelancer {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(columnDefinition = "varchar(25) not null")
    private String name;


    @Column(nullable = false, unique = true)
    private String email;


    @Column(columnDefinition = "VARCHAR(13) UNIQUE")
    private String phone;


    @Column(columnDefinition = "varchar(50) not null")
    private String specialization;


    @Column(columnDefinition = "double not null")
    private Double hourlyRate;

    @NotNull(message = "availability status of freelancer can't be empty")
    @Column(columnDefinition = "boolean not null")
    private Boolean isAvailable = true;


    @Column(columnDefinition = "int not null")
    private Integer yearsExperience;


//    @NotNull(message = "rating can't be empty")
//    @PositiveOrZero(message = "rating should be positive or Zero")
//    @Column(columnDefinition = "double not null")
//    private Double rating;

    // Balance management fields
    @PositiveOrZero(message = "available balance should not be negative")
    @Column(columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private Double availableBalance = 0.0;

    @PositiveOrZero(message = "pending balance should not be negative")
    @Column(columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private Double pendingBalance = 0.0;

    @PositiveOrZero(message = "total earnings should not be negative")
    @Column(columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private Double totalEarnings = 0.0;

    // relations
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "freelancer")
    private Set<FreelancerProject> freelancerProjects;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;


    // todo (one to many relation with freelancerProject.java)
    // private List<FreelancerProject> freelancerProjects

}
