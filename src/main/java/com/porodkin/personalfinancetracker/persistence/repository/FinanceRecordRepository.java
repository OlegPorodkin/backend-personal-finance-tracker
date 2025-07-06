package com.porodkin.personalfinancetracker.persistence.repository;

import com.porodkin.personalfinancetracker.persistence.entity.FinanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinanceRecordRepository extends JpaRepository<FinanceRecord, Long> {
}
