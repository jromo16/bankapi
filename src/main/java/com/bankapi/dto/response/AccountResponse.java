package com.bankapi.dto.response;

import com.bankapi.model.Account;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class AccountResponse {
    private Long id;
    private String accountNumber;
    private Account.AccountType type;
    private BigDecimal balance;
    private Account.AccountStatus status;
    private String ownerName;
    private LocalDateTime createdAt;
}
