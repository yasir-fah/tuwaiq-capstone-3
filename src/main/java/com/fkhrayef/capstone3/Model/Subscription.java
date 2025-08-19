package com.fkhrayef.capstone3.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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

    @Column(columnDefinition = "VARCHAR(15) NOT NULL")
    private String planType;

    @Column(columnDefinition = "INTEGER DEFAULT 0")
    private Integer aiLimit;

    @Column(columnDefinition = "VARCHAR(15) NOT NULL")
    private String billingCycle;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Column(columnDefinition = "VARCHAR(15)")
    private String status;

    // relations
    @OneToOne
    @MapsId
    @JsonIgnore
    private Founder founder;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
