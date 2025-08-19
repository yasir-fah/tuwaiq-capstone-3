package com.fkhrayef.capstone3.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Check(constraints = "round_type IN ('pre_seed', 'seed', 'series_a', 'series_b', 'series_c') and " +
                     "payment_method IN('check', 'transfer') and " +
                     "investment_amount > 0 and " +
                     "recurring_amount > 0 and " +
                     "recurring_years > 0 and " +
                     "minimum_investment_period > 0")
//todo DTO
public class Investment {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Integer id;



    @NotEmpty(message = "Round Type is required")
    @Pattern(regexp = "^(?i)(pre_seed|seed|series_a|series_b|series_c)$")
    @Column(columnDefinition = "VARCHAR(8)")
    private String roundType;

    @NotNull(message = "Effective Date is required")
    @FutureOrPresent(message = "Effective Date must be in the future or present")
    @Column(columnDefinition = "Date")
    private LocalDate effectiveDate;


    @NotNull(message = "Investment Amount is required")
    @Positive(message = "Investment Amount must be greater than zero")
    @Column(columnDefinition = "DECIMAL(15,2)")
    private Double investment_amount;

    @NotEmpty(message = "Payment Method is required")
    @Pattern(regexp = "^(?i)(check|transfer)$")
    @Column(columnDefinition = "VARCHAR(8)")
    private String paymentMethod;


    // todo check the recurring labels
    @NotNull(message = "isRecurring is required")
    @Column(columnDefinition = "BOOLEAN")
    private Boolean isRecurring;

    @Positive(message = "Recurring Amount must be greater than zero")
    @Column(columnDefinition = "DECIMAL(15,2)")
    private Double recurringAmount;

    @Positive(message = "Recurring Years must be greater than zero")
    @Column(columnDefinition = "INTEGER")
    private Integer recurringYears;

    @Positive(message = "Minimum Investment Period in years must be greater than zero")
    @Column(columnDefinition = "INTEGER")
    private Integer minimumInvestmentPeriod;

    // relations
    @ManyToOne
    @JsonIgnore
    private Investor investor;

    @ManyToOne
    @JsonIgnore
    private Startup startup;


    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;


   /* Table InvestmentContract {
        id integer [primary key]
        investor_id integer [ref: > Investor.id]
        startup_id integer [ref: > Startup.id]
        effective_date date
        investment_amount decimal(15,2)
        payment_method varchar(50) // check, transfer
        payment_type varchar(50)   // one_time, recurring
        recurring_amount decimal(15,2)
        recurring_years integer
        minimum_investment_period integer
        created_at timestamp
    }*/

}
