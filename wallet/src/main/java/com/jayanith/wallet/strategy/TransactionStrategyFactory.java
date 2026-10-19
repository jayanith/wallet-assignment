package com.jayanith.wallet.strategy;

import com.jayanith.wallet.model.TransactionType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Factory pattern: returns the right strategy for a transaction type. */
@Component
public class TransactionStrategyFactory {

    private final Map<TransactionType, TransactionStrategy> strategies = new EnumMap<>(TransactionType.class);

    public TransactionStrategyFactory(List<TransactionStrategy> all) {
        all.forEach(s -> strategies.put(s.type(), s));
    }

    public TransactionStrategy get(TransactionType type) {
        TransactionStrategy s = strategies.get(type);
        if (s == null) {
            throw new IllegalArgumentException("Unsupported transaction type: " + type);
        }
        return s;
    }
}
