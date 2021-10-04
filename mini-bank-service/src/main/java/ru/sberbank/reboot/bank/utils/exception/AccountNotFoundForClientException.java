package ru.sberbank.reboot.bank.utils.exception;

public class AccountNotFoundForClientException extends RuntimeException {
    private static final long serialVersionUID = -504171222021226466L;

    public AccountNotFoundForClientException(Long idAccount, Long idClient) {
        super("Для клиента " + idClient + " счет с идентификтором " + idAccount + " не найден");
    }
}
