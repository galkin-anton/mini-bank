package ru.sberbank.reboot.bank.repository;

import org.springframework.data.repository.CrudRepository;
import ru.sberbank.reboot.bank.jpa.BankTransaction;

import java.util.List;

/**
 * Репозиторий транзакций
 */

public interface BankTransactionRepository extends CrudRepository<BankTransaction, Long> {
    List<BankTransaction> findAll();
}
