package com.fkhrayef.capstone3.DTOin;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AiAdvisorDTO {

    @NotEmpty(message = "prompt can't be empty")
    private String prompt;
}
