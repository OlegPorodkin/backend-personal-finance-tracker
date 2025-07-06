package com.porodkin.personalfinancetracker.configuration;

import com.porodkin.personalfinancetracker.persistence.repository.FinanceRecordRepository;
import com.porodkin.personalfinancetracker.service.financerecord.FinanceRecordRead;
import com.porodkin.personalfinancetracker.service.financerecord.impl.FinanceRecordService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FinanceRecordConfiguration {

    @Bean
    public FinanceRecordRead crudFinanceTransaction(FinanceRecordRepository financeRecordRepository) {
        return new FinanceRecordService(financeRecordRepository);
    }
}
