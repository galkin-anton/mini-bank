package ru.sberbank.reboot.bank.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sberbank.reboot.bank.jpa.Account;
import ru.sberbank.reboot.bank.jpa.BankTransaction;
import ru.sberbank.reboot.bank.service.AccountService;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST контроллер для счетов
 */
@Slf4j
@RestController
@RequestMapping(
  consumes = MediaType.APPLICATION_JSON_VALUE,
  produces = MediaType.APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    /**
     * Возвращает список счетов клиента
     *
     * @param idClient идентификатор клиента
     * @return JSON представление списка счетов клиента по его идентификатору
     */
    @GetMapping(value = "/clients/{idClient}/accounts")
    public List<Account> getClientAccounts(@PathVariable("idClient") Long idClient) {
        return accountService.getClientAccounts(idClient);
    }

    /**
     * Возвращает счет клиента по идентификатору клиента и идентификатору счета
     *
     * @param idClient  идентификатор клиента
     * @param idAccount идентификатор счета
     * @return JSON представление счетов клиента
     */
    @GetMapping(value = "/clients/{idClient}/accounts/{idAccount}")
    public Account getClientAccounts(@PathVariable("idClient") Long idClient, @PathVariable("idAccount") Long idAccount) {
        return accountService.getClientAccountById(idClient, idAccount);
    }

    /**
     * Сохраняет счет для клиента
     *
     * @param idClient идентификатор клиента
     * @param account  сам счет, который нужно сохранить
     * @return экземляр сущности счет {@link Account}
     */
    @PostMapping(value = "/clients/{idClient}/accounts")
    public Account saveAccountToClient(@PathVariable("idClient") Long idClient, @RequestBody Account account) {
        return accountService.saveAccountToClient(idClient, account);
    }

    /**
     * Отправка средств с одного аккаунта клиента на другой любой счет
     *
     * @param idClient        идентификатор клиента
     * @param idAccount       идентификатор счета источника
     * @param idTargetAccount идентификатор счета получателя
     * @param summ            сумма операции
     * @return экземляр сущности счет {@link BankTransaction}
     */
    @PostMapping(value = "/clients/{idClient}/accounts/{idAccount}/send/{idTargetAccount}")
    public BankTransaction sendMoneyToAccountId(@PathVariable("idClient") Long idClient,
      @PathVariable("idAccount") Long idAccount,
      @PathVariable("idTargetAccount") Long idTargetAccount,
      @RequestParam("summ") BigDecimal summ) {
        return accountService.sendMoneyByClientIdAndAccountIds(idClient, idAccount, idTargetAccount, summ);
    }

    /**
     * Возвращает список всех счетов
     *
     * @return список экземпляров сушности {@link Account} счет
     */
    @GetMapping(value = "/accounts")
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    /**
     * Получить конкретный счет
     * @param idAccount идентификатор счета
     * @return экземпляр сущности {@link Account} счет
     */
    @GetMapping(value = "/accounts/{idAccount}")
    public Account getAllAccounts(@PathVariable("idAccount") Long idAccount) {
        return accountService.getAccountById(idAccount);
    }

    /**
     * Отправка средств с одного счета на другой счет по идентификатору
     *
     * @param idAccount       идентификатор счета источника
     * @param idTargetAccount идентификатор счета получателя
     * @param summ            сумма операции
     * @return экземляр сущности счет {@link BankTransaction}
     */
    @PostMapping(value = "/accounts/{idAccount}/send/{idTargetAccount}")
    public BankTransaction sendMoneyToAccountId(@PathVariable("idAccount") Long idAccount,
      @PathVariable("idTargetAccount") Long idTargetAccount,
      @RequestParam("summ") BigDecimal summ) {
        return accountService.sendMoneyByAccountsIds(idAccount, idTargetAccount, summ);
    }

    /**
     * Получение списка транзакций по счету
     *
     * @param idAccount идентификатор счет
     * @return список экземпляров сущности транзакция {@link BankTransaction}
     */
    @GetMapping(value = "/accounts/{idAccount}/transactions")
    public List<BankTransaction> sendMoneyToAccountId(@PathVariable("idAccount") Long idAccount) {
        return accountService.getTransactionsByAccountId(idAccount);
    }

    /**
     * Удаление аккаунта
     *
     * @param idAccount идентификатор счет
     * @return экземляр сущности счет {@link Account}
     */
    @DeleteMapping(value = "/accounts/{idAccount}")
    public Account closeAccount(@PathVariable("idAccount") Long idAccount) {
        return accountService.deleteAccount(idAccount);
    }
}
