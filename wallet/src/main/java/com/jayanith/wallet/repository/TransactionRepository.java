package com.jayanith.wallet.repository;

import com.jayanith.wallet.model.Wallet;
import com.jayanith.wallet.model.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<WalletTransaction, Long> {
    List<WalletTransaction> findByWalletOrderByIdDesc(Wallet wallet);
}
