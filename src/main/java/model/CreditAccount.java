package model;

import exception.InsufficientFundsException;

import java.math.BigDecimal;

public class CreditAccount extends Account{
    private final BigDecimal creditLimit;

    public CreditAccount(String id, String userId, BigDecimal balance, BigDecimal creditLimit) {
        super(id, userId, balance);
        this.creditLimit = creditLimit;
    }

    @Override
    public void withdraw(BigDecimal amount) {
        BigDecimal newBalance = this.balance.subtract(amount);
        if (newBalance.compareTo(creditLimit.negate()) < 0) {
            throw new InsufficientFundsException("Credit limit exceeded");
        }
        this.balance = newBalance;
    }
}
