package com.porodkin.personalfinancetracker.service.financerecord;

import com.porodkin.personalfinancetracker.dto.response.financetransaction.FinanceRecordResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FinanceRecordRead {
    Page<FinanceRecordResponse> giveAll(Pageable pageable);
}
