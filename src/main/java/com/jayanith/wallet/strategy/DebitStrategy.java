package com.jayanith.wallet.strategy;

import com.jayanith.wallet.exception.InsufficientBalanceException;
import com.jayanith.wallet.model.TransactionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DebitStrategy implements TransactionStrategy {
    @Override
    public TransactionType type() { return TransactionType.DEBIT; }

    @Override
    public BigDecimal apply(BigDecimal currentBalance, BigDecimal amount) {
        if (currentBalance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance: available " + currentBalance + ", requested " + amount);
        }
        return currentBalance.subtract(amount);
    }
}
