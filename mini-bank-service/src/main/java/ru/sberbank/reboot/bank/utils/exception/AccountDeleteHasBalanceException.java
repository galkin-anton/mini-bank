package ru.sberbank.reboot.bank.utils.exception;

public class AccountDeleteHasBalanceException extends RuntimeException {
    private static final long serialVersionUID = 6729009585974237220L;

    public AccountDeleteHasBalanceException(Long id) {
        super("Невозможно закрыть счет " + id + ", т.к. баланс не нулевой");
    }
}
