package service;

import exception.AccountNotFoundException;
import exception.InsufficientFundsException;
import model.*;
import repository.AccountRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankService {
    private final AccountRepository accountRepository;
    private final List<Transaction> transactionHistory = new CopyOnWriteArrayList<>();
    private final ConcurrentMap<String, Lock> locks = new ConcurrentHashMap<>();

    public BankService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    private Lock getLock(String accountId) {
        return locks.computeIfAbsent(accountId, k -> new ReentrantLock());
    }

    public void transfer(String fromAccountId, String toAccountId, BigDecimal amount) {
        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Sender account Id not found, "
                        + fromAccountId));
        Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Receiver account Id not found, "
                        + toAccountId));
        String firstLockId = fromAccountId.compareTo(toAccountId) < 0 ? fromAccountId : toAccountId;
        String secondLockId = fromAccountId.compareTo(toAccountId) < 0 ? toAccountId : fromAccountId;

        Lock firstLock = getLock(firstLockId);
        Lock secondLock = getLock(secondLockId);

        firstLock.lock();
        secondLock.lock();

        try {
            fromAccount.withdraw(amount);
            toAccount.deposit(amount);
            accountRepository.save(fromAccount);
            accountRepository.save(toAccount);
            Transaction transaction = new Transaction(UUID.randomUUID().toString(), fromAccountId, toAccountId,
                    amount, LocalDateTime.now(), TransactionStatus.SUCCESS);
            transactionHistory.add(transaction);
        } catch (InsufficientFundsException e) {
            Transaction transaction = new Transaction(UUID.randomUUID().toString(), fromAccountId, toAccountId,
                    amount, LocalDateTime.now(), TransactionStatus.FAILED);
            transactionHistory.add(transaction);
            throw e;
        } finally {
            secondLock.unlock();
            firstLock.unlock();
        }
    }

    public BigDecimal calculateTotalBalance(String userId) {
        return accountRepository.findAll().stream()
                .filter(account -> account.getUserId().equals(userId))
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<Account> getTopAccounts(int limit) {
        return accountRepository.findAll().stream()
                .sorted((a1, a2) -> a2.getBalance().compareTo(a1.getBalance()))
                .limit(limit)
                .toList();
    }

    public List<Transaction> getTransactionHistory(String accountId) {
        return transactionHistory.stream()
                .filter(tx -> tx.fromAccountId().equals(accountId) || tx.toAccountId().equals(accountId))
                .toList();
    }
}
