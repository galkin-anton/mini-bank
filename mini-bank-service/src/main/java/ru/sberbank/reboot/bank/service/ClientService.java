package ru.sberbank.reboot.bank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.reboot.bank.jpa.Client;
import ru.sberbank.reboot.bank.repository.ClientRepository;
import ru.sberbank.reboot.bank.utils.exception.ClientNotFoundException;

import java.util.List;

/**
 * Сервис по клиенту
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;

    /**
     * Находим и возвращаем клиента
     *
     * @param id идентификатор клиента
     * @return экземпляр клиента
     * @throws ClientNotFoundException если клиент не найден
     */
    public Client findClientById(Long id) {
        log.debug("Поиск клиента по идентификатору {}", id);
        return clientRepository.findById(id).orElseThrow(() -> new ClientNotFoundException(id));
    }

    /**
     * Возвращает всех клиентов
     *
     * @return список клиентов
     */
    public List<Client> findAll() {
        List<Client> allClients = clientRepository.findAll();
        log.debug("Выводим всех клиентов, количество {}", allClients.size());
        return allClients;
    }

    /**
     * Сохранение клиента
     *
     * @param client экземпляр клиента
     * @return сохраненный клиент
     */
    public Client saveClient(Client client) {
        return clientRepository.save(client);
    }
}
