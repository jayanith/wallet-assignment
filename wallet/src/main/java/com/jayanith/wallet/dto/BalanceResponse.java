package com.jayanith.wallet.dto;

import java.math.BigDecimal;

public record BalanceResponse(String username, BigDecimal balance) { }
