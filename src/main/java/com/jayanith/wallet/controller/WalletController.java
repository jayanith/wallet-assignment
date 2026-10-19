package com.jayanith.wallet.controller;

import com.jayanith.wallet.dto.AmountRequest;
import com.jayanith.wallet.dto.BalanceResponse;
import com.jayanith.wallet.dto.TransactionResponse;
import com.jayanith.wallet.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/balance")
    public BalanceResponse balance(Principal principal) {
        return walletService.getBalance(principal.getName());
    }

    @PostMapping("/add")
    public BalanceResponse add(Principal principal, @Valid @RequestBody AmountRequest req) {
        return walletService.addMoney(principal.getName(), req.amount());
    }

    @PostMapping("/withdraw")
    public BalanceResponse withdraw(Principal principal, @Valid @RequestBody AmountRequest req) {
        return walletService.withdrawMoney(principal.getName(), req.amount());
    }

    @GetMapping("/transactions")
    public List<TransactionResponse> transactions(Principal principal) {
        return walletService.history(principal.getName());
    }
}
