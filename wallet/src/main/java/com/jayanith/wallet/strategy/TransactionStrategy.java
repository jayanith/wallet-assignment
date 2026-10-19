package com.jayanith.wallet.strategy;

import com.jayanith.wallet.model.TransactionType;

import java.math.BigDecimal;

/** Strategy pattern: each transaction type knows how to compute the new balance. */
public interface TransactionStrategy {
    TransactionType type();
    BigDecimal apply(BigDecimal currentBalance, BigDecimal amount);
}
