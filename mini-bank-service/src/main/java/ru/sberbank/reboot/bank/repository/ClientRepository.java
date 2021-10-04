package ru.sberbank.reboot.bank.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import ru.sberbank.reboot.bank.jpa.Client;

import java.util.List;

/**
 * Репозиторий клиента
 */

public interface ClientRepository extends CrudRepository<Client, Long> {
    List<Client> findAll();
    Page<Client> findAll(Pageable pageable);
}
