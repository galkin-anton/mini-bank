package ru.sberbank.reboot.bank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.reboot.bank.jpa.Account;
import ru.sberbank.reboot.bank.jpa.BankTransaction;
import ru.sberbank.reboot.bank.repository.BankTransactionRepository;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;

/**
 * Сервис банковских транзакций
 */
@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class BankTransactionService {
    private final BankTransactionRepository bankTransactionRepository;

    private void addTransactionToAccount(Account account, BankTransaction bankTransaction) {
        account.getTransactionList().add(bankTransaction);
    }

    /**
     * СОздает экземпляр сущности банковская транзакция по счету
     *
     * @param sourceAccount экземпляр сушности счета источника
     * @param targetAccount экземпляр сушности счета получателя
     * @param summ          сумма операции
     * @return экзкмпляр сущности банковская транзакция по счету
     */
    public BankTransaction createTransaction(Account sourceAccount, Account targetAccount, BigDecimal summ) {
        log.debug("Создание транзакции перевода средств со счета {}, на счет {}, сумма {}",
          sourceAccount,
          targetAccount,
          summ);
        //TODO добавить поддержку разных валют
        BankTransaction bankTransaction = new BankTransaction();
        bankTransaction.setSourceAccount(sourceAccount.getId());
        bankTransaction.setTargetAccount(targetAccount.getId());
        bankTransaction.setBalanceSourceBefore(sourceAccount.getBalance());
        bankTransaction.setBalanceTargetBefore(targetAccount.getBalance());
        bankTransaction.setOperationSumm(summ);

        sourceAccount.setBalance(sourceAccount.getBalance().add(summ.negate()));
        targetAccount.setBalance(targetAccount.getBalance().add(summ));
        bankTransaction.setBalanceSourceAfter(sourceAccount.getBalance());
        bankTransaction.setBalanceTargetAfter(targetAccount.getBalance());

        addTransactionToAccount(targetAccount, bankTransaction);
        addTransactionToAccount(sourceAccount, bankTransaction);

        HashSet<Account> accounts = new HashSet<>();
        accounts.add(targetAccount);
        accounts.add(sourceAccount);
        bankTransaction.setAccountList(accounts);

        bankTransactionRepository.save(bankTransaction);
        log.debug("Создана транзакция {}", bankTransaction);
        return bankTransaction;
    }

    public List<BankTransaction> getAll() {
        return bankTransactionRepository.findAll();
    }
}
