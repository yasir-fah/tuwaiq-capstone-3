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
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Check( constraints = "duration_minutes > 0 and " +
                      "status IN ('scheduled', 'in_progress', 'completed', 'cancelled')")
//todo DTO
public class AdvisorSession {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Integer id;

    // todo this is session_date + start_time, and duration_minutes is the session duration in mins
    @NotNull(message = "Session date and time is required")
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime startDate;

    @NotNull(message = "duration in minutes is required")
    @Positive(message = "Duration must be greater than zero")
    @Column(columnDefinition = "INTEGER")
    private Integer duration_minutes;

    @NotEmpty(message = "Status is required")
    @Pattern(regexp = "^(?i)(scheduled|in_progress|completed|cancelled)$")
    @Column(columnDefinition = "VARCHAR(11)")
    private String status;

    @Column(columnDefinition = "VARCHAR(2000)")
    private String notes;

    // relations
    @ManyToOne
    @JsonIgnore
    private Advisor advisor;

    @ManyToOne
    @JsonIgnore
    private Startup startup;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;







  /*  Table AdvisorSession {
        id integer [primary key]
        advisor_id integer [ref: > Advisor.id]
        startup_id integer [ref: > Startup.id]
        session_date date
        start_time time
        end_time time
        status varchar(50) // scheduled, in_progress, completed, cancelled
        notes text // session notes/summary
        created_at timestamp
        updated_at timestamp
    }*/
}
