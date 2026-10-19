package com.jayanith.wallet.service;

import com.jayanith.wallet.dto.BalanceResponse;
import com.jayanith.wallet.dto.TransactionResponse;
import com.jayanith.wallet.model.TransactionType;
import com.jayanith.wallet.model.Wallet;
import com.jayanith.wallet.model.WalletTransaction;
import com.jayanith.wallet.repository.TransactionRepository;
import com.jayanith.wallet.repository.WalletRepository;
import com.jayanith.wallet.strategy.TransactionStrategyFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionStrategyFactory strategyFactory;

    public WalletService(WalletRepository walletRepository,
                         TransactionRepository transactionRepository,
                         TransactionStrategyFactory strategyFactory) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.strategyFactory = strategyFactory;
    }

    /** Returns the user's wallet, creating an empty one on first login. */
    @Transactional
    public Wallet getOrCreate(String username) {
        return walletRepository.findByUsername(username)
                .orElseGet(() -> walletRepository.save(new Wallet(username)));
    }

    @Transactional
    public BalanceResponse getBalance(String username) {
        Wallet w = getOrCreate(username);
        return new BalanceResponse(w.getUsername(), w.getBalance());
    }

    @Transactional
    public BalanceResponse addMoney(String username, BigDecimal amount) {
        return execute(username, TransactionType.CREDIT, amount);
    }

    @Transactional
    public BalanceResponse withdrawMoney(String username, BigDecimal amount) {
        return execute(username, TransactionType.DEBIT, amount);
    }

    @Transactional
    public List<TransactionResponse> history(String username) {
        Wallet w = getOrCreate(username);
        return transactionRepository.findByWalletOrderByIdDesc(w).stream()
                .map(TransactionResponse::from)
                .toList();
    }

    private BalanceResponse execute(String username, TransactionType type, BigDecimal amount) {
        Wallet w = getOrCreate(username);
        BigDecimal newBalance = strategyFactory.get(type).apply(w.getBalance(), amount);
        w.setBalance(newBalance);
        walletRepository.save(w);
        transactionRepository.save(WalletTransaction.builder()
                .wallet(w).type(type).amount(amount).balanceAfter(newBalance).build());
        return new BalanceResponse(w.getUsername(), newBalance);
    }
}
