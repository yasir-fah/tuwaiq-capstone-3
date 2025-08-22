package com.fkhrayef.capstone3.Controller;

import com.fkhrayef.capstone3.Api.ApiResponse;
import com.fkhrayef.capstone3.DTOin.InvestmentDTO;
import com.fkhrayef.capstone3.Service.InvestmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/investment")
public class InvestmentController {

    final InvestmentService investmentService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllInvestments(){
        return ResponseEntity.status(HttpStatus.OK.value()).body(investmentService.getAllInvestments());
    }

    @PostMapping("/add/")
    public ResponseEntity<?> addInvestment(@RequestBody @Valid InvestmentDTO investmentDTO, @RequestParam String exchange){
        investmentService.createInvestment(investmentDTO, exchange);
        return ResponseEntity.status(HttpStatus.CREATED.value()).body(new ApiResponse("Investment added successfully"));
    }

    @PutMapping("/update/{investment_id}")
    public ResponseEntity<?> updateInvestment(@RequestBody @Valid InvestmentDTO investmentDTO, @PathVariable Integer investment_id){
        investmentService.updateInvestment(investment_id, investmentDTO);
        return ResponseEntity.status(HttpStatus.OK.value()).body(new ApiResponse("Investment updated successfully"));
    }

    @DeleteMapping("/delete/{investment_id}")
    public ResponseEntity<?> deleteInvestment(@PathVariable Integer investment_id){
        investmentService.deleteInvestment(investment_id);
        return ResponseEntity.status(HttpStatus.OK.value()).body(new ApiResponse("Investment deleted successfully"));
    }
}
