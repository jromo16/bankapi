package com.bankapi.service;

import com.bankapi.dto.request.TransferRequest;
import com.bankapi.dto.response.TransactionResponse;
import com.bankapi.exception.AccountNotActiveException;
import com.bankapi.exception.InsufficientFundsException;
import com.bankapi.exception.ResourceNotFoundException;
import com.bankapi.model.Account;
import com.bankapi.model.Transaction;
import com.bankapi.repository.AccountRepository;
import com.bankapi.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public TransactionResponse transfer(TransferRequest request) {
        Account source = accountRepository.findByAccountNumber(request.getSourceAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Source account not found"));

        Account target = accountRepository.findByAccountNumber(request.getTargetAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Target account not found"));

        if (source.getAccountNumber().equals(target.getAccountNumber())) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        if (source.getStatus() != Account.AccountStatus.ACTIVE) {
            throw new AccountNotActiveException("Source account is not active");
        }

        if (target.getStatus() != Account.AccountStatus.ACTIVE) {
            throw new AccountNotActiveException("Target account is not active");
        }

        if (source.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds. Available: " + source.getBalance());
        }

        source.setBalance(source.getBalance().subtract(request.getAmount()));
        target.setBalance(target.getBalance().add(request.getAmount()));

        accountRepository.save(source);
        accountRepository.save(target);

        Transaction transaction = Transaction.builder()
                .sourceAccount(source)
                .targetAccount(target)
                .amount(request.getAmount())
                .description(request.getDescription())
                .status(Transaction.TransactionStatus.COMPLETED)
                .build();

        Transaction saved = transactionRepository.save(transaction);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> getTransactionsByAccount(
            String accountNumber, LocalDateTime start, LocalDateTime end, Pageable pageable) {

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNumber));

        Page<Transaction> transactions;
        if (start != null && end != null) {
            transactions = transactionRepository.findByAccountAndDateRange(account, start, end, pageable);
        } else {
            transactions = transactionRepository.findByAccount(account, pageable);
        }

        return transactions.map(this::toResponse);
    }

    private TransactionResponse toResponse(Transaction t) {
        return TransactionResponse.builder()
                .id(t.getId())
                .sourceAccountNumber(t.getSourceAccount().getAccountNumber())
                .targetAccountNumber(t.getTargetAccount().getAccountNumber())
                .amount(t.getAmount())
                .description(t.getDescription())
                .status(t.getStatus())
                .createdAt(t.getCreatedAt())
                .build();
    }
}
