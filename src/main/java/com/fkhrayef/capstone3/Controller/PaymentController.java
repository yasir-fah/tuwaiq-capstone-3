package com.fkhrayef.capstone3.Controller;

import com.fkhrayef.capstone3.DTOin.PaymentRequest;
import com.fkhrayef.capstone3.Service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/card")
    public ResponseEntity<ResponseEntity<String>> processPayment(@RequestBody PaymentRequest paymentRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(paymentService.processPayment(paymentRequest));
    }

    @GetMapping("/get-status/{id}")
    public ResponseEntity<?> getPaymentStatus(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(paymentService.getPaymentStatus(id));
    }
}
