package com.porodkin.personalfinancetracker.controllers;

import com.porodkin.personalfinancetracker.dto.request.financetransaction.NewFinanceRecordRequest;
import com.porodkin.personalfinancetracker.dto.response.financetransaction.FinanceRecordResponse;
import com.porodkin.personalfinancetracker.service.financerecord.FinanceRecordRead;
import com.porodkin.personalfinancetracker.service.financerecord.FinanceRecordWrite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/finance-record")
public class FinanceRecordController {

    private final FinanceRecordRead serviceRead;
    private final FinanceRecordWrite serviceWrite;

    public FinanceRecordController(FinanceRecordRead serviceRead, FinanceRecordWrite serviceWrite) {
        this.serviceRead = serviceRead;
        this.serviceWrite = serviceWrite;
    }

    @GetMapping
    public ResponseEntity<Page<FinanceRecordResponse>> receivePageableTransactions(Pageable pageable) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(serviceRead.giveAll(pageable));
    }

    @PostMapping
    public ResponseEntity<FinanceRecordResponse> createTransactions(@RequestBody NewFinanceRecordRequest transaction) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceWrite.create(transaction));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<FinanceRecordResponse> deleteTransaction(@PathVariable("id") Long id) {
        serviceWrite.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
