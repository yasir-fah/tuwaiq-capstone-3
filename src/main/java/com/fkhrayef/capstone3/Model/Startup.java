package com.fkhrayef.capstone3.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Check(constraints = "employee_count >= 0")
@Check(constraints = "valuation >= 0")
public class Startup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "Name cannot be null")
    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;

    @NotEmpty(message = "Description cannot be null")
    @Column(columnDefinition = "VARCHAR(2096) NOT NULL")
    private String description;

    @NotEmpty(message = "Industry cannot be null")
    @Column(columnDefinition = "VARCHAR(100) NOT NULL")
    // TODO Add Pattern
    private String industry;

    @NotEmpty(message = "Stage cannot be null")
    @Column(columnDefinition = "VARCHAR(50) NOT NULL")
    // TODO Add Pattern
    private String stage;

    @NotNull(message = "Founded Date cannot be null")
    @Column(columnDefinition = "DATE NOT NULL")
    private LocalDate foundedDate;

    @NotNull(message = "Employee Count cannot be null")
    @PositiveOrZero(message = "Employee Count cannot be negative")
    @Column(columnDefinition = "INTEGER DEFAULT 1 NOT NULL")
    private Integer employeeCount;

    @NotNull(message = "Valuation cannot be null")
    @PositiveOrZero(message = "Valuation cannot be negative")
    @Column(columnDefinition = "DOUBLE DEFAULT 0 NOT NULL")
    private Double valuation;

    @NotEmpty(message = "Status cannot be null")
    @Column(columnDefinition = "VARCHAR(50) NOT NULL")
    // TODO Add Pattern
    private String status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
