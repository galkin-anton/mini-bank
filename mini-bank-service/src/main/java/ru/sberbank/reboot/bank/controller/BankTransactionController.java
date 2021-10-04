package ru.sberbank.reboot.bank.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.reboot.bank.jpa.BankTransaction;
import ru.sberbank.reboot.bank.service.BankTransactionService;

import java.util.List;

/**
 * Контроллер транзакций
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/transactions")
public class BankTransactionController {
    private final BankTransactionService bankTransactionService;

    /**
     * Возвращает список всех транзакций
     *
     * @return список транзакций
     */
    @GetMapping("")
    public List<BankTransaction> getAllTransactions() {
        return bankTransactionService.getAll();
    }
}
