package com.fkhrayef.capstone3.DTOin;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdvisorSessionDTO {


    @NotNull(message = "Session date and time is required")
    private LocalDateTime startDate;

    @NotNull(message = "duration in minutes is required")
    @Positive(message = "Duration must be greater than zero")
    private Integer duration_minutes;

    @NotEmpty(message = "notes can't be empty")
    private String notes;


}
