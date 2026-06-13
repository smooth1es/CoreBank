import model.Account;
import model.DebitAccount;
import repository.AccountRepository;
import service.BankService;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        AccountRepository accountRepository = new AccountRepository();
        BankService bankService = new BankService(accountRepository);
        DebitAccount ACC1 = new DebitAccount("ACC1", "USER1", new BigDecimal("10000"));
        DebitAccount ACC2 = new DebitAccount("ACC2", "USER2", new BigDecimal("10000"));
        accountRepository.save(ACC1);
        accountRepository.save(ACC2);

        CountDownLatch latch;
        try (ExecutorService executor = Executors.newFixedThreadPool(100)) {
            latch = new CountDownLatch(100);
            for (int i = 0; i < 100; i++) {
                boolean from1to2 = (i % 2 == 0);
                executor.submit(() -> {
                    try {
                        if (from1to2) {
                            bankService.transfer("ACC1", "ACC2", new BigDecimal("100"));
                        } else {
                            bankService.transfer("ACC2", "ACC1", new BigDecimal("100"));
                        }
                    } catch (Exception e){
                        System.out.println(e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                });
            }
            latch.await();
        }
        Account finalAcc1 = accountRepository.findById("ACC1").get();
        Account finalAcc2 = accountRepository.findById("ACC2").get();

        System.out.println("Balance ACC1: " + finalAcc1.getBalance());
        System.out.println("Balance ACC2: " + finalAcc2.getBalance());
        System.out.println("Total: " + finalAcc1.getBalance().add(finalAcc2.getBalance()));
    }
}
