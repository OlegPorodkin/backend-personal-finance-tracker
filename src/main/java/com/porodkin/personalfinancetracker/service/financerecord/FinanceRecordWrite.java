package com.porodkin.personalfinancetracker.service.financerecord;

import com.porodkin.personalfinancetracker.dto.request.financetransaction.NewFinanceRecordRequest;
import com.porodkin.personalfinancetracker.dto.response.financetransaction.FinanceRecordResponse;

public interface FinanceRecordWrite {
    FinanceRecordResponse create(NewFinanceRecordRequest transaction);
    void delete(Long id);
}
