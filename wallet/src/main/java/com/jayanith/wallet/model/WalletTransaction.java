package com.jayanith.wallet.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_transactions")
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Wallet wallet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceAfter;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected WalletTransaction() { }

    private WalletTransaction(Builder b) {
        this.wallet = b.wallet;
        this.type = b.type;
        this.amount = b.amount;
        this.balanceAfter = b.balanceAfter;
        this.createdAt = LocalDateTime.now();
    }

    public static Builder builder() { return new Builder(); }

    /** Builder pattern for creating immutable transaction records. */
    public static class Builder {
        private Wallet wallet;
        private TransactionType type;
        private BigDecimal amount;
        private BigDecimal balanceAfter;

        public Builder wallet(Wallet wallet) { this.wallet = wallet; return this; }
        public Builder type(TransactionType type) { this.type = type; return this; }
        public Builder amount(BigDecimal amount) { this.amount = amount; return this; }
        public Builder balanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; return this; }
        public WalletTransaction build() { return new WalletTransaction(this); }
    }

    public Long getId() { return id; }
    public TransactionType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
