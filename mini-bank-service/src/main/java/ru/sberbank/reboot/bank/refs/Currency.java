package ru.sberbank.reboot.bank.refs;

import lombok.Getter;

/**
 * Валюта
 */
public enum Currency {
    RUB("810"),
    USD("840");

    @Getter
    private final String currencyCode;

    Currency(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    /**
     * Определение валюты независимо от регистра
     *
     * @param currencyCode код
     * @return код валюты
     */
    public static Currency findIgnoreCase(String currencyCode) {
        return Currency.valueOf(currencyCode.toUpperCase());
    }
}
