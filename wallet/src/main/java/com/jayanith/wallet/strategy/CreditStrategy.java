package com.jayanith.wallet.strategy;

import com.jayanith.wallet.model.TransactionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CreditStrategy implements TransactionStrategy {
    @Override
    public TransactionType type() { return TransactionType.CREDIT; }

    @Override
    public BigDecimal apply(BigDecimal currentBalance, BigDecimal amount) {
        return currentBalance.add(amount);
    }
}
