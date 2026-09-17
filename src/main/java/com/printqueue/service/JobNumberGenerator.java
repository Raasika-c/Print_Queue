package com.printqueue.service;

import com.printqueue.repository.PrintJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobNumberGenerator {

    private final PrintJobRepository printJobRepository;

    @Transactional(readOnly = true)
    public synchronized String generateNextJobNumber() {
        int currentYear = LocalDate.now().getYear();
        String prefix = "DPQ-" + currentYear + "-";
        
        Optional<String> lastJobNumberOpt = printJobRepository.findLastJobNumberByPrefix(prefix);
        
        if (lastJobNumberOpt.isEmpty()) {
            return prefix + "0001";
        }
        
        String lastJobNumber = lastJobNumberOpt.get();
        try {
            int sequence = Integer.parseInt(lastJobNumber.substring(prefix.length()));
            sequence++;
            return prefix + String.format("%04d", sequence);
        } catch (NumberFormatException e) {
            return prefix + "0001";
        }
    }
}
