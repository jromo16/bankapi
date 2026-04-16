package com.bankapi.dto.response;

import com.bankapi.model.Transaction;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponse {
    private Long id;
    private String sourceAccountNumber;
    private String targetAccountNumber;
    private BigDecimal amount;
    private String description;
    private Transaction.TransactionStatus status;
    private LocalDateTime createdAt;
}
