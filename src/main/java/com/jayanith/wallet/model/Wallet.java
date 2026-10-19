package com.jayanith.wallet.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    /** Optimistic locking so two concurrent updates cannot overwrite each other. */
    @Version
    private Long version;

    protected Wallet() { }

    public Wallet(String username) {
        this.username = username;
        this.balance = BigDecimal.ZERO;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}
