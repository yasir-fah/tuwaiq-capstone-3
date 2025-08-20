package com.fkhrayef.capstone3.DTOin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    private String name;
    private String number;
    private String cvc;
    private String month;
    private String year;
    private Double amount;
    private String currency;
    private String description;
    private String callbackUrl;
}
