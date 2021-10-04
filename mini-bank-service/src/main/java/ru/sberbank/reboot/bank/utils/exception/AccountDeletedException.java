package ru.sberbank.reboot.bank.utils.exception;

public class AccountDeletedException extends RuntimeException {
    private static final long serialVersionUID = 8022122603364310095L;

    public AccountDeletedException(Long id) {
        super("Счет " + id + ", закрыт");
    }
}
