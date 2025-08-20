package com.fkhrayef.capstone3.DTOin;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FreelancerProjectDTO {

    @NotEmpty(message = "project can't be empty")
    @Size(min = 4, max = 30, message = "project name length between 4-30")
    private String projectName;

    @NotEmpty(message = "description can't be empty")
    @Size(min = 4, max = 100, message = "project name length between 4-100")
    private String description;

    @NotNull(message = "start date can't be empty")
    private LocalDate startDate;

    @NotNull(message = "end date can't be empty")
    private LocalDate endDate;

    // could figure the status at the service
}
