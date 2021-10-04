package ru.sberbank.reboot.bank.utils.exception;

public class AccountDeleteAlreadyDeletedException extends RuntimeException {
    private static final long serialVersionUID = 3424420203140297536L;

    public AccountDeleteAlreadyDeletedException(Long id) {
        super("Невозможно удалить счет " + id + ", т.к. он уже закрыт");
    }
}
