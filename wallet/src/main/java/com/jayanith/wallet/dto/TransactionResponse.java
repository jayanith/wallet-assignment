package com.jayanith.wallet.dto;

import com.jayanith.wallet.model.WalletTransaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(Long id, String type, BigDecimal amount,
                                  BigDecimal balanceAfter, LocalDateTime createdAt) {
    public static TransactionResponse from(WalletTransaction t) {
        return new TransactionResponse(t.getId(), t.getType().name(), t.getAmount(),
                t.getBalanceAfter(), t.getCreatedAt());
    }
}
