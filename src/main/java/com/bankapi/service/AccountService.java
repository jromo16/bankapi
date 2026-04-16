package com.bankapi.service;

import com.bankapi.dto.request.CreateAccountRequest;
import com.bankapi.dto.response.AccountResponse;
import com.bankapi.exception.ResourceNotFoundException;
import com.bankapi.model.Account;
import com.bankapi.model.User;
import com.bankapi.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request, User owner) {
        String accountNumber = generateAccountNumber();

        Account account = Account.builder()
                .accountNumber(accountNumber)
                .type(request.getType())
                .balance(BigDecimal.ZERO)
                .status(Account.AccountStatus.ACTIVE)
                .owner(owner)
                .build();

        Account saved = accountRepository.save(account);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getAccountsByUser(User user) {
        return accountRepository.findByOwner(user)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccountByNumber(String accountNumber, User user) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNumber));

        if (!account.getOwner().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Account not found: " + accountNumber);
        }

        return toResponse(account);
    }

    private String generateAccountNumber() {
        String number;
        do {
            number = String.format("%010d", new Random().nextLong(9_000_000_000L) + 1_000_000_000L);
        } while (accountRepository.existsByAccountNumber(number));
        return number;
    }

    public AccountResponse toResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .type(account.getType())
                .balance(account.getBalance())
                .status(account.getStatus())
                .ownerName(account.getOwner().getFullName())
                .createdAt(account.getCreatedAt())
                .build();
    }
}
