package com.fkhrayef.capstone3.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

// TODO Should have DTOin because we don't take the ai limit, status

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Check(constraints = "plan_type IN ('free', 'pro', 'enterprise')")
@Check(constraints = "billing_cycle IN ('monthly', 'yearly')")
@Check(constraints = "status IN ('active', 'expired', 'cancelled')")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "Plan Type cannot be null")
    @Pattern(regexp = "^(free|pro|enterprise)$",  message = "Plan Type must be either free, pro or enterprise") // TODO enterprise means everyone working for the startup of the founder gets unlimited AI requests?
    @Column(columnDefinition = "VARCHAR(15) NOT NULL")
    private String planType;

    @Column(columnDefinition = "INTEGER DEFAULT 0")
    private Integer aiLimit;

    @NotEmpty(message = "Billing Cycle cannot be null")
    @Pattern(regexp = "^(monthly|yearly)$", message = "Billing Cycle must be either monthly or yearly")
    @Column(columnDefinition = "VARCHAR(15) NOT NULL")
    private String billingCycle;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Pattern(regexp = "^(active|expired|cancelled)$", message = "Status must be either active, expired or cancelled")
    @Column(columnDefinition = "VARCHAR(15)")
    private String status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
