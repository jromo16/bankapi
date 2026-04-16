package com.bankapi.repository;

import com.bankapi.model.Account;
import com.bankapi.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE t.sourceAccount = :account OR t.targetAccount = :account ORDER BY t.createdAt DESC")
    Page<Transaction> findByAccount(@Param("account") Account account, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE (t.sourceAccount = :account OR t.targetAccount = :account) AND t.createdAt BETWEEN :start AND :end ORDER BY t.createdAt DESC")
    Page<Transaction> findByAccountAndDateRange(
            @Param("account") Account account,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable);
}
