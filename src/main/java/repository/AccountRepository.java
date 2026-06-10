package repository;

import model.Account;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class AccountRepository {
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    public AccountRepository() {
    }

    public void save(Account account) {
        accounts.put(account.getId(), account);
    }

    public Optional<Account> findById(String id) {
        return Optional.ofNullable(accounts.get(id));
    }

    public List<Account> findAll() {
        return new ArrayList<>(accounts.values());
    }
}
