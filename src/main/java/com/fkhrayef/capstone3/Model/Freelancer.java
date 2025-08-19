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
public class Freelancer {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "name can't be empty")
    @Size(min = 4, max = 25, message = "name length should be between 4-25 ")
    @Column(columnDefinition = "varchar(25) not null")
    private String name;

    @NotEmpty(message = "Email can't be empty")
    @Email(message = "Email should be valid")
    @Column(nullable = false, unique = true)
    private String email;

    @NotEmpty(message = "phone can't be empty")
    @Pattern(regexp = "^(\\+9665[0-9]\\d{8})$", message = "Phone number must be a valid Saudi mobile in the format +9665XXXXXXXX")
    @Column(columnDefinition = "VARCHAR(13)")
    private String phone;

    //todo add pattern underneath:
    @NotEmpty(message ="specialization can't be empty")
    @Column(columnDefinition = "varchar(50) not null")
    private String specialization;

    @NotNull(message = "hourly rate can't be empty")
    @PositiveOrZero(message = "hourly rate should not be negative")
    @Column(columnDefinition = "double not null")
    private Double hourlyRate;

    @NotNull(message = "availability status of freelancer can't be empty")
    @Column(columnDefinition = "boolean not null")
    private Boolean isAvailable = true;

    @NotNull(message = "years pf experience can't be empty")
    @PositiveOrZero(message = "years pf experience  should be Zero or Above")
    @Column(columnDefinition = "int not null")
    private Integer yearsExperience;


    @NotNull(message = "rating can't be empty")
    @PositiveOrZero(message = "rating should be positive or Zero")
    @Column(columnDefinition = "double not null")
    private Double rating;

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
