package ru.sberbank.reboot.bank.utils.exception;

public class AccountNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -504171222021226466L;

    public AccountNotFoundException(Long id) {
        super("Счет с идентификтором " + id + " не найден");
    }
}
