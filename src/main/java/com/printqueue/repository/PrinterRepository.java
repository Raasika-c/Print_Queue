package com.printqueue.repository;

import com.printqueue.entity.VirtualPrinter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrinterRepository extends JpaRepository<VirtualPrinter, Long> {
    Optional<VirtualPrinter> findByName(String name);
}
