package com.printqueue.service;

import com.printqueue.entity.ColorMode;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CostCalculationService {

    private static final BigDecimal COLOR_PRICE_PER_PAGE = new BigDecimal("5.00");
    private static final BigDecimal BW_PRICE_PER_PAGE = new BigDecimal("1.00");

    public BigDecimal calculateCost(int numberOfPages, int numberOfCopies, ColorMode colorMode) {
        BigDecimal pricePerPage = (colorMode == ColorMode.COLOR) ? COLOR_PRICE_PER_PAGE : BW_PRICE_PER_PAGE;
        int totalPages = numberOfPages * numberOfCopies;
        return pricePerPage.multiply(new BigDecimal(totalPages));
    }
}
