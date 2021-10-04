package ru.sberbank.reboot.bank.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.reboot.bank.jpa.Account;
import ru.sberbank.reboot.bank.jpa.BankTransaction;
import ru.sberbank.reboot.bank.jpa.Client;
import ru.sberbank.reboot.bank.refs.Status;
import ru.sberbank.reboot.bank.repository.AccountRepository;
import ru.sberbank.reboot.bank.utils.exception.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Сервис по счетам
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final ClientService clientService;
    private final BankTransactionService bankTransactionService;

    /**
     * Возвращает список всех счетов
     *
     * @return список экземпляров сушности {@link Account} счет
     */
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    /**
     * Отправка средств с одного счета на другой
     *
     * @param sourceAccountId идентификатор счета источника
     * @param targetAccountId идентификатор счета получателя
     * @param summ            сумма операции
     * @return экземпляр сущности {@link BankTransaction}
     * @throws AccountDeletedException если счет источник удален
     */
    public BankTransaction sendMoneyByAccountsIds(Long sourceAccountId, Long targetAccountId, BigDecimal summ) {
        Account sourceAccount = getAccountById(sourceAccountId);
        if (sourceAccount.getStatus().equals(Status.ARCHIVED)) {
            throw new AccountDeletedException(sourceAccountId);
        }
        return sendMoneyByAccountId(sourceAccount, targetAccountId, summ);
    }

    /**
     * Перевод денежных средств с одного счета на другой, указав идентификатор счета получателя
     *
     * @param sourceAccount   экземпляр сущности счета отправителя
     * @param targetAccountId идентификатор счета получателя
     * @param summ            сумма операции
     * @return экземпляр сущности транзакция счета
     * @throws AccountNotFoundException не найден счет получателя
     * @throws AccountDeletedException если счет удален
     */
    public BankTransaction sendMoneyByAccountId(Account sourceAccount, Long targetAccountId, BigDecimal summ) {
        log.debug("Отправка денежных средств со счета {}, на счет {}, сумма {}, по идентификатору счета получателя",
          sourceAccount.getId(),
          targetAccountId,
          summ);
        Account targetAccount = getAccountById(targetAccountId);

        if (targetAccount.getStatus().equals(Status.ARCHIVED)) {
            throw new AccountDeletedException(targetAccountId);
        }
        return sendMoneyByAccount(sourceAccount, targetAccount, summ);
    }

    /**
     * Поиск счета по идентификатору
     *
     * @param id идентификатор счета
     * @return экземпляр сущности {@link Account} счет
     * @throws AccountNotFoundException не найден счет получателя
     */
    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
          .orElseThrow(() -> new AccountNotFoundException(id));
    }

    /**
     * Перевод денежных средств с одного счета на другой, указав номер счета получателя
     *
     * @param sourceAccount       экземпляр сущности счета отправителя
     * @param targetAccountNumber номер счета получателя
     * @param summ                сумма операции
     * @return экземпляр сущности транзакция счета
     */
    public BankTransaction sendMoneyByAccountNumber(Account sourceAccount, Long targetAccountNumber, BigDecimal summ) {
        log.debug("Отправка денежных средств со счета {}, на счет {}, сумма {}, по номеру счета получателя",
          sourceAccount,
          targetAccountNumber,
          summ);
        //TODO обработать исключение
        Account targetAccount = accountRepository.findByNumber(targetAccountNumber).get(0);
        return sendMoneyByAccount(sourceAccount, targetAccount, summ);
    }

    /**
     * Перевод денежных средств с одного счета на другой, указав экземпляр счета
     *
     * @param sourceAccount экземпляр сущности счета отправителя
     * @param targetAccount экземпляр сущности счета получателя
     * @param summ сумма операции
     * @return экземпляр сущности транзакция счета
     */
    public BankTransaction sendMoneyByAccount(Account sourceAccount, Account targetAccount, BigDecimal summ) {
        log.debug("Отправка денежных средств со счета {}, на счет {}, сумма {}", sourceAccount, targetAccount, summ);
        if (sourceAccount.getBalance().compareTo(summ) < 0) {
            throw new AccountNotEnoughFunds(sourceAccount.getId());
        }
        return bankTransactionService.createTransaction(sourceAccount, targetAccount, summ);
    }

    /**
     * Находим все счета по клиенту
     * <p>
     * Вначале необходимо найти клиента а затем уже все его счета
     *
     * @param idClient идентификатор клиента
     * @return счета клиента
     * @throws ClientNotFoundException в случае, если клиент не найден
     */
    public List<Account> getClientAccounts(Long idClient) {
        List<Account> accounts = clientService.findClientById(idClient)
          .getAccounts();
        log.debug("У клиента {} счетов {}", idClient, accounts.size());
        return accounts;
    }

    /**
     * Сохраняем готовый счет клиенту
     *
     * @param idClient идентификатор клиента
     * @param account  сам счет
     * @return обновленный/сохраненный счет
     * @throws ClientNotFoundException в случае, если клиент не найден
     */
    public Account saveAccountToClient(Long idClient, Account account) {
        log.debug("Поиск всех счетов по клиенту {}", idClient);
        Client client = clientService.findClientById(idClient);
        setAccountFullNumber(account);
        account.setClient(client);
        account.setCurrencyCode(account.getCurrency().getCurrencyCode());
        accountRepository.save(account);

        return account;
    }

    /**
     * Сохраняем готовый счет клиенту
     *
     * @param account сам счет
     * @throws ClientNotFoundException в случае, если клиент не найден
     */
    public void saveAccount(Account account) {
        log.debug("Сохранение счета {}", account);
        setAccountFullNumber(account);
        account.setCurrencyCode(account.getCurrency().getCurrencyCode());
        accountRepository.save(account);
        log.debug("Сохранен счет {}", account);
    }

    /**
     * Поиск счета по идентификатору клиента и идентификатору счета
     *
     * @param idClient  идентификатор  клиента
     * @param idAccount идентификатор счета
     * @return экземпляр сущности {@link Account} счета
     */
    public Account getClientAccountById(Long idClient, Long idAccount) {
        log.debug("Поиск счета по клиенту {}, и идентификатору счета {}", idClient, idAccount);
        Client client = clientService.findClientById(idClient);
        return accountRepository.findByIdAndClientId(client, idAccount)
          .orElseThrow(() -> new AccountNotFoundForClientException(idAccount, idClient));
    }

    /**
     * Отправка средств по идентификаторам клиента, счета отправителя, и счета получателя
     *
     * @param clientId        идентификатор клиента
     * @param sourceAccountId идентификатор счета источника
     * @param targetAccountId идентификатор счета получателя
     * @param summ            сумма операции
     * @return экземпляр сущности {@link BankTransaction}
     */
    @SneakyThrows
    public BankTransaction sendMoneyByClientIdAndAccountIds(Long clientId, Long sourceAccountId, Long targetAccountId, BigDecimal summ) {
        log.debug("Отправка средств по идентификаторам, ид клиента {}, ид счета отправителя {}, ид счета получателя {}, сумма {}",
          clientId,
          sourceAccountId,
          targetAccountId,
          summ);
        if (sourceAccountId.equals(targetAccountId)) {
            throw new AccountNotApplicableForTransferingException(targetAccountId, sourceAccountId);
        }

        Account sourceAccount = getClientAccountById(clientId, sourceAccountId);
        return sendMoneyByAccountId(sourceAccount, targetAccountId, summ);
    }

    /**
     * Присваивание номера счета
     * <p>
     * Номер счета должен быть уникальным, он состоит из
     * <ul>
     *     <li>группа - номер счета первого порядка + номер счета второго порядка + код валюты
     *      * + контрольная цифра + код филиала банка (у нас будет 0000)</li>
     *      <li> + порядковый номер в этой группе</li>
     * </ul>
     *
     * @param account экземпляр счета
     */
    private void setAccountFullNumber(Account account) {
        Long maxNumber = accountRepository.maxAccountNumberInGroup(account.getFirstOrderNumber(),
          account.getSecondOrderAccNumber(),
          account.getBranch(),
          account.getControlDigit(),
          account.getCurrency().getCurrencyCode()
        ).orElse(0L);
        account.setNumber(maxNumber + 1);
        String fullAccountNumber = "" + account.getFirstOrderNumber()
          + account.getSecondOrderAccNumber()
          + account.getCurrency().getCurrencyCode()
          + account.getControlDigit()
          + account.getBranch()
          + String.format("%09d", account.getNumber());

        log.debug("Сгенерирован номер счета {} для счета {}", fullAccountNumber, account);
        account.setFullNumber(fullAccountNumber);
    }

    /**
     * Возвращает список транзакций по счету
     *
     * @param id идентификатор счета
     * @return список транзакций {@link BankTransaction}
     */
    public List<BankTransaction> getTransactionsByAccountId(Long id) {
        Account account = getAccountById(id);
        return account.getTransactionList();
    }

    /**
     * Удаляем счет (перевод в архив) если на нем не нулевой баланс
     *
     * @param id идентификатор счета
     * @return экземпляр сущности {@link Account} счет
     * @throws AccountDeleteHasBalanceException если на балансе не нулевая сумма
     * @throws AccountDeleteAlreadyDeletedException если аккаунт уже удален
     */
    public Account deleteAccount(Long id) {
        Account account = getAccountById(id);
        log.debug("Удаляем счет {}", account);
        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new AccountDeleteHasBalanceException(id);
        }
        if (account.getStatus().equals(Status.ARCHIVED)) {
            throw new AccountDeleteAlreadyDeletedException(id);
        }
        account.setStatus(Status.ARCHIVED);
        return account;
    }
}
