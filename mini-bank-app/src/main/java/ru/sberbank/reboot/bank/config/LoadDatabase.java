package ru.sberbank.reboot.bank.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.sberbank.reboot.bank.jpa.Account;
import ru.sberbank.reboot.bank.jpa.Client;
import ru.sberbank.reboot.bank.refs.Currency;
import ru.sberbank.reboot.bank.repository.AccountRepository;
import ru.sberbank.reboot.bank.repository.ClientRepository;
import ru.sberbank.reboot.bank.service.AccountService;

import java.math.BigDecimal;

@Slf4j
@Configuration
@Profile("local")
@RequiredArgsConstructor
public class LoadDatabase {
    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;

    @Bean
    CommandLineRunner initDatabase() {
        Client client = new Client();
        client.setFirstName("Иван");
        client.setLastName("Иванов");
        clientRepository.save(client);
        client = new Client();
        client.setFirstName("Иван");
        client.setLastName("Ivanov");
        clientRepository.save(client);
        //Accounts
        Account account = new Account();
        account.setNumber(435665L);
        account.setCurrency(Currency.RUB);
        account.setCurrencyCode(Currency.RUB.getCurrencyCode());
        account.setClient(client);
        accountService.saveAccount(account);
        //Балансовый счет
        Account accountBalance = new Account();
        accountBalance.setBalance(BigDecimal.valueOf(1.0E10));
        accountBalance.setFirstOrderNumber(202);
        accountBalance.setCurrency(Currency.RUB);
        accountService.saveAccount(accountBalance);
        return args -> {
            log.info("format string {}", String.format("%05d", 11));
        };
    }
}

