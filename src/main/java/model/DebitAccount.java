package model;

import exception.InsufficientFundsException;

import java.math.BigDecimal;

public class DebitAccount extends Account{
    public DebitAccount(String id, String userId, BigDecimal balance) {
        super(id, userId, balance);
    }

    @Override
    public void withdraw(BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Not enough funds on the debit account.");
        }
        this.balance = this.balance.subtract(amount);
    }
}
