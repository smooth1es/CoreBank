package model;

import java.math.BigDecimal;

public abstract class Account {
    private final String id;
    private final String userId;
    protected BigDecimal balance;

    public Account(String id, String userId, BigDecimal balance) {
        this.id = id;
        this.userId = userId;
        this.balance = balance;
    }

    public abstract void withdraw(BigDecimal amount);

    public void deposit (BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

}
