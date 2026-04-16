package com.bankapi.controller;

import com.bankapi.dto.request.CreateAccountRequest;
import com.bankapi.dto.response.AccountResponse;
import com.bankapi.model.User;
import com.bankapi.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Accounts", description = "Account management endpoints")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @Operation(summary = "Create a new bank account")
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody CreateAccountRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountService.createAccount(request, user));
    }

    @GetMapping
    @Operation(summary = "Get all accounts for the authenticated user")
    public ResponseEntity<List<AccountResponse>> getMyAccounts(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(accountService.getAccountsByUser(user));
    }

    @GetMapping("/{accountNumber}")
    @Operation(summary = "Get account by account number")
    public ResponseEntity<AccountResponse> getAccount(
            @PathVariable String accountNumber,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(accountService.getAccountByNumber(accountNumber, user));
    }
}
