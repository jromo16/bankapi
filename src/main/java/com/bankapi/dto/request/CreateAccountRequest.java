package com.bankapi.dto.request;

import com.bankapi.model.Account;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAccountRequest {

    @NotNull(message = "Account type is required")
    private Account.AccountType type;
}
