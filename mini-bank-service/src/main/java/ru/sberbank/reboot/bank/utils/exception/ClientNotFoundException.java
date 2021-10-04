package ru.sberbank.reboot.bank.utils.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 3026183954485667246L;

    public ClientNotFoundException(Long id) {
        super("Клиент с идентификтором " + id + " не найден");
    }
}
