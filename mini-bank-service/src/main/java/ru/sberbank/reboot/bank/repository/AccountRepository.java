package ru.sberbank.reboot.bank.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.sberbank.reboot.bank.jpa.Account;
import ru.sberbank.reboot.bank.jpa.Client;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий счетов
 */

public interface AccountRepository extends CrudRepository<Account, Long> {
    List<Account> findAll();

    @Query(value = "SELECT max(a.number) FROM Account a where a.firstOrderNumber = ?1 " +
      "and a.secondOrderAccNumber = ?2 and a.branch = ?3 and a.controlDigit = ?4 and a.currencyCode = ?5")
    Optional<Long> maxAccountNumberInGroup(int firstOrderNumber,
      String secondOrderAccNumber,
      String branch,
      int controlDigit,
      String currencyCode);

    @Query(value = "SELECT a FROM Account a where a.number = ?1")
    List<Account> findByNumber(Long number);

    @Query(value = "SELECT a FROM Account a where a.id = ?2 and a.client = ?1")
    Optional<Account> findByIdAndClientId(Client client, Long idAccount);
}
