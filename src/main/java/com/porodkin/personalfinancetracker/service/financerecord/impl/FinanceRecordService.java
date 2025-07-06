package com.porodkin.personalfinancetracker.service.financerecord.impl;

import com.porodkin.personalfinancetracker.dto.request.financetransaction.NewFinanceRecordRequest;
import com.porodkin.personalfinancetracker.dto.response.financetransaction.FinanceRecordResponse;
import com.porodkin.personalfinancetracker.persistence.entity.FinanceRecord;
import com.porodkin.personalfinancetracker.persistence.repository.FinanceRecordRepository;
import com.porodkin.personalfinancetracker.service.financerecord.FinanceRecordRead;
import com.porodkin.personalfinancetracker.service.financerecord.FinanceRecordWrite;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class FinanceRecordService implements FinanceRecordRead, FinanceRecordWrite {

    private final FinanceRecordRepository financeRecordRepository;

    public FinanceRecordService(FinanceRecordRepository financeRecordRepository) {
        this.financeRecordRepository = financeRecordRepository;
    }

    @Override
    @Transactional
    public Page<FinanceRecordResponse> giveAll(Pageable pageable) {
        return financeRecordRepository
                .findAll(pageable)
                .map(t -> new FinanceRecordResponse(
                        t.getType(),
                        t.getAmount(),
                        t.getDate(),
                        t.getDescription(),
                        t.getUserId(),
                        t.getCategoryId(),
                        t.getCurrencyCode()
                ));
    }

    @Override
    @Transactional
    public FinanceRecordResponse create(NewFinanceRecordRequest transaction) {

        financeRecordRepository.save(mapToFinanceRecord(transaction));

        return null;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        financeRecordRepository.deleteById(id);
    }

    private FinanceRecord mapToFinanceRecord(NewFinanceRecordRequest request) {
        return new FinanceRecord(
                request.type(),
                request.amount(),
                request.date(),
                request.description(),
                request.userId(),
                request.categoryId(),
                request.currencyCode()
        );
    }
}
