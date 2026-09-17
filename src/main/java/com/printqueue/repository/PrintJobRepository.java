package com.printqueue.repository;

import com.printqueue.entity.PrintJob;
import com.printqueue.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrintJobRepository extends JpaRepository<PrintJob, Long> {
    
    List<PrintJob> findByUserOrderBySubmittedAtDesc(User user);
    
    @Query("SELECT p.jobNumber FROM PrintJob p WHERE p.jobNumber LIKE :prefix% ORDER BY p.id DESC LIMIT 1")
    Optional<String> findLastJobNumberByPrefix(String prefix);

    List<PrintJob> findByStatusIn(List<com.printqueue.entity.PrintJobStatus> statuses);

    Optional<PrintJob> findByPrinterAndStatus(com.printqueue.entity.VirtualPrinter printer, com.printqueue.entity.PrintJobStatus status);
}
