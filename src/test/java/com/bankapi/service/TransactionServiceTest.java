package com.bankapi.service;

import com.bankapi.dto.request.TransferRequest;
import com.bankapi.dto.response.TransactionResponse;
import com.bankapi.exception.AccountNotActiveException;
import com.bankapi.exception.InsufficientFundsException;
import com.bankapi.exception.ResourceNotFoundException;
import com.bankapi.model.Account;
import com.bankapi.model.Transaction;
import com.bankapi.model.User;
import com.bankapi.repository.AccountRepository;
import com.bankapi.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionService Tests")
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Account sourceAccount;
    private Account targetAccount;
    private TransferRequest transferRequest;

    @BeforeEach
    void setUp() {
        User owner = User.builder()
                .id(1L)
                .email("user@test.com")
                .fullName("Test User")
                .build();

        sourceAccount = Account.builder()
                .id(1L)
                .accountNumber("1234567890")
                .balance(new BigDecimal("1000.00"))
                .status(Account.AccountStatus.ACTIVE)
                .type(Account.AccountType.SAVINGS)
                .owner(owner)
                .build();

        targetAccount = Account.builder()
                .id(2L)
                .accountNumber("0987654321")
                .balance(new BigDecimal("500.00"))
                .status(Account.AccountStatus.ACTIVE)
                .type(Account.AccountType.CHECKING)
                .owner(owner)
                .build();

        transferRequest = new TransferRequest();
        transferRequest.setSourceAccountNumber("1234567890");
        transferRequest.setTargetAccountNumber("0987654321");
        transferRequest.setAmount(new BigDecimal("200.00"));
        transferRequest.setDescription("Test transfer");
    }

    @Test
    @DisplayName("Should transfer money successfully")
    void transfer_ShouldSucceed_WhenValidRequest() {
        when(accountRepository.findByAccountNumber("1234567890")).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByAccountNumber("0987654321")).thenReturn(Optional.of(targetAccount));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> {
            Transaction t = inv.getArgument(0);
            t.setId(1L);
            return t;
        });

        TransactionResponse response = transactionService.transfer(transferRequest);

        assertThat(response).isNotNull();
        assertThat(sourceAccount.getBalance()).isEqualByComparingTo("800.00");
        assertThat(targetAccount.getBalance()).isEqualByComparingTo("700.00");
        verify(accountRepository, times(2)).save(any(Account.class));
    }

    @Test
    @DisplayName("Should throw InsufficientFundsException when balance is too low")
    void transfer_ShouldThrow_WhenInsufficientFunds() {
        transferRequest.setAmount(new BigDecimal("5000.00"));
        when(accountRepository.findByAccountNumber("1234567890")).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByAccountNumber("0987654321")).thenReturn(Optional.of(targetAccount));

        assertThatThrownBy(() -> transactionService.transfer(transferRequest))
                .isInstanceOf(InsufficientFundsException.class)
                .hasMessageContaining("Insufficient funds");
    }

    @Test
    @DisplayName("Should throw AccountNotActiveException when source account is blocked")
    void transfer_ShouldThrow_WhenSourceAccountBlocked() {
        sourceAccount.setStatus(Account.AccountStatus.BLOCKED);
        when(accountRepository.findByAccountNumber("1234567890")).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByAccountNumber("0987654321")).thenReturn(Optional.of(targetAccount));

        assertThatThrownBy(() -> transactionService.transfer(transferRequest))
                .isInstanceOf(AccountNotActiveException.class);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when source account does not exist")
    void transfer_ShouldThrow_WhenSourceAccountNotFound() {
        when(accountRepository.findByAccountNumber("1234567890")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.transfer(transferRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Source account not found");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when transferring to same account")
    void transfer_ShouldThrow_WhenSameAccount() {
        transferRequest.setTargetAccountNumber("1234567890");
        when(accountRepository.findByAccountNumber("1234567890"))
                .thenReturn(Optional.of(sourceAccount))
                .thenReturn(Optional.of(sourceAccount));

        assertThatThrownBy(() -> transactionService.transfer(transferRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
