package ru.sberbank.reboot.bank.utils.exception;

public class AccountNotApplicableForTransferingException extends RuntimeException {
    private static final long serialVersionUID = -504171222021226466L;

    public AccountNotApplicableForTransferingException(Long idTarget, Long idSource) {
        super("Невозможно перевести средства на счет " + idTarget + " со счета " + idSource);
    }
}
