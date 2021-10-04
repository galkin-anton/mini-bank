package ru.sberbank.reboot.bank.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sberbank.reboot.bank.jpa.Client;
import ru.sberbank.reboot.bank.service.ClientService;

import java.util.List;

/**
 * REST контроллер для клиента
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/clients",
  consumes = MediaType.APPLICATION_JSON_VALUE,
  produces = MediaType.APPLICATION_JSON_VALUE)
public class ClientController {
    private final ClientService clientService;

    /**
     * Возвращает клиента по идентификатору
     *
     * @param id идентификатор клиента
     * @return экземпляр сущности {@link Client}
     */
    @GetMapping(value = "/{id}")
    public Client findByIds(@PathVariable(name = "id") Long id) {
        return clientService.findClientById(id);
    }

    /**
     * Вывод всех клиентов
     *
     * @return список клиентов сущности {@link Client}
     */
    @GetMapping(value = "")
    public List<Client> findAll() {
        return clientService.findAll();
    }

    /**
     * Сохранение клиента
     *
     * @param client десериализованный клиент из тела запроса
     * @return сохраненный экземпляр сущности {@link Client}
     */
    @PostMapping(value = "")
    public Client saveClient(@RequestBody Client client) {
        return clientService.saveClient(client);
    }
}
