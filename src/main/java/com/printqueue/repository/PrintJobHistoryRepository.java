package com.printqueue.repository;

import com.printqueue.entity.PrintJobHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrintJobHistoryRepository extends JpaRepository<PrintJobHistory, Long> {
    List<PrintJobHistory> findByPrintJobIdOrderByChangedAtDesc(Long printJobId);
}
