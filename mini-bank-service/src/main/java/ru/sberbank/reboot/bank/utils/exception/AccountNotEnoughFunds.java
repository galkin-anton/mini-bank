package ru.sberbank.reboot.bank.utils.exception;

/**
Недостаточно средств на счете
 */

public class AccountNotEnoughFunds extends RuntimeException {
    private static final long serialVersionUID = -6281703682674005827L;

    public AccountNotEnoughFunds(Long id) {
        super("На счете " + id + " недостаточно средств");
    }
}
