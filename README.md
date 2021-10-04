# Mini Bank
**Сложность**: L - XL

Реализовать свой "Мини банк"

## Минимальные требования (сложность: L)
- Регистрация и хранение клиентов;
- Открытие / закрытие счетов, баланс счетов;
- Транзакции (траты и пополнения);

### Доп. требования (сложность: XL)
- Выделить функциональность в сервисы:
  - Сервис "Клиенты";
  - Сервис "Счета";
  - Сервис "Транзакции".

## Рекомендации 
- Используйте Spring Boot;
- Для автоматического тестирования используйте Spring Boot Test;
- Для развертывания инфраструктуры в тестах используйте [testcontainers](https://www.testcontainers.org/);
- Для развертывания необходимой инфраструктуры используйте Docker Desktop;
- Пишите JavaDoc.

## DoD
- Написан код, согласно требованиям;
- Форк от текущего репозитория, оформление PR;
- Состояние хранится в БД (можно H2);
- Вся функциональность покрыта Feature-тестами (при необходимости Unit);
- Документация (описание вашего решения в текущем файле, см. секцию ниже)

# Документация решения (инструкция и описание)
(заполняется по факту реализации)

(раскройте темы: дизайн решения, сборка проекта, запуск проекта)
## Дизайн решения
#### Сущности
Выделены следующие сущности
- Клиент
- Счет
- Банковские транзакции

#### Клиент
- Имя
- Фамилия

Имеет связь один ко многим с сущностью счет

#### Счет
- порядковый номер в группе
- полный номер
- номер первого порядка (для физ лиц 408)
- номер второго порядка (для текущих счетов 17)
- номер филиала банка (у нас только один 0000)
- контрольная цифра (по умолчанию 5)
- валюта счета (наложено ограничение enum'ом)
- код валюты (вычисляется при сохранении)
- баланс (по умолчанию нулевой)
- статус счета (ARCHIVED|ACTIVE) ограничен enum'ом

Имеет связь многие к одному с сущностью клиент, и многие ко многим с сущностью банковская транзакция

#### Банковская транзакция
- идентификатор счета списания
- идентифактор счета получения
- баланс счета списания до операции
- баланс счета получения до операции
- баланс счета списания после операции
- баланс счета получения после операции
- сумма операции

Имеет связь с сущностью счет многие ко многим

В каждой сущности есть 2 поля аудита:
- Дата создания
- Дата обновления (для транзакций не очень актуально т.к. неизменны)

### Сервис
#### Сохранение нового клиента
```http request
[POST] <Hub REST API URL>/clients
```    
```json 
{
    "firstName": "Иван",
    "lastName": "Иванов1"
}
```     
#### Получение всего списка клиентов
```http request
[GET] <Hub REST API URL>/clients
```            
Примерный ответ
```json 
[
    {
        "id": 1,
        "firstName": "Иван",
        "lastName": "Иванов",
        "createdDateTime": "2020-11-24T21:54:23.404+00:00",
        "updatedDateTime": "2020-11-24T21:54:23.404+00:00"
    },
    {
        "id": 2,
        "firstName": "Иван",
        "lastName": "Ivanov",
        "createdDateTime": "2020-11-24T21:54:23.416+00:00",
        "updatedDateTime": "2020-11-24T21:54:23.416+00:00"
    }
]
```   
### Пройти в контекст клиента
```http request
[GET] <Hub REST API URL>/clients/{{idClient}}
```            
Примерный ответ
```json 
{
    "id": 1,
    "firstName": "Иван",
    "lastName": "Иванов",
    "createdDateTime": "2020-11-24T21:54:23.404+00:00",
    "updatedDateTime": "2020-11-24T21:54:23.404+00:00"
}
```       
Можем получить ошибку если клиента не существует
```json 
{
    "timestamp": "2020-11-25T12:56:46.568+03:00",
    "status": "NOT_FOUND",
    "debugMessage": "Клиент с идентификтором 12 не найден"
}
```       
### В контексте клиента получить весь список его договоров
```http request
[GET] <Hub REST API URL>/clients/{{idClient}}/accounts
```            
Примерный ответ
```json 
[
    {
        "id": 3,
        "number": 1,
        "fullNumber": "4081781050000000000001",
        "firstOrderNumber": 408,
        "secondOrderAccNumber": "17",
        "branch": "0000",
        "controlDigit": 5,
        "currency": "RUB",
        "currencyCode": "810",
        "balance": 0.00,
        "status": "ACTIVE",
        "createdDateTime": "2020-11-24T21:54:23.596+00:00",
        "updatedDateTime": "2020-11-24T21:54:23.596+00:00"
    },
    {
        "id": 5,
        "number": 2,
        "fullNumber": "4081781050000000000002",
        "firstOrderNumber": 408,
        "secondOrderAccNumber": "17",
        "branch": "0000",
        "controlDigit": 5,
        "currency": "RUB",
        "currencyCode": "810",
        "balance": 0.00,
        "status": "ACTIVE",
        "createdDateTime": "2020-11-24T21:58:25.041+00:00",
        "updatedDateTime": "2020-11-24T21:58:25.041+00:00"
    }
]
```       
### В контексте клиента создать для него счет
```http request
[POST] <Hub REST API URL>/clients/{{idClient}}/accounts
```      
Примерный запрос, тогда откроется рублевой счет и номера первого и второго порядка присвоятся по умолчанияю,
также как и контрольная цифра и филиал, опционально и можно указать
```json
{
   "currency": "RUB"
}
```      
Примерный ответ
```json 
{
    "id": 5,
    "number": 2,
    "fullNumber": "4081781050000000000002",
    "firstOrderNumber": 408,
    "secondOrderAccNumber": "17",
    "branch": "0000",
    "controlDigit": 5,
    "currency": "RUB",
    "currencyCode": "810",
    "balance": 0,
    "status": "ACTIVE",
    "createdDateTime": "2020-11-24T21:58:25.041+00:00",
    "updatedDateTime": "2020-11-24T21:58:25.041+00:00"
}
``` 

Пример с опциональными параметрами
```json
{
    "firstOrderNumber": 409,
    "secondOrderAccNumber": "02",
    "branch": "0001",
    "controlDigit": 2,
    "currency": "RUB",
    "balance": 10
}
``` 
В ответ получим
```json 
{
    "id": 6,
    "number": 1,
    "fullNumber": "4090281020001000000001",
    "firstOrderNumber": 409,
    "secondOrderAccNumber": "02",
    "branch": "0001",
    "controlDigit": 2,
    "currency": "RUB",
    "currencyCode": "810",
    "balance": 10,
    "status": "ACTIVE",
    "createdDateTime": "2020-11-24T22:05:03.551+00:00",
    "updatedDateTime": "2020-11-24T22:05:03.551+00:00"
}
``` 
Номер счета формируется автоматически - это порядковый номер в группе `4090281020001`
Может вернуться ошибка валюты
```json 
{
    "timestamp": "2020-11-25T01:07:09.026+03:00",
    "status": "NOT_ACCEPTABLE",
    "debugMessage": "Cannot deserialize value of type `ru.sberbank.reboot.bank.refs.Currency` from String \"CZ\": not one of the values accepted for Enum class: [USD, RUB]\n at [Source: (PushbackInputStream); line: 6, column: 17] (through reference chain: ru.sberbank.reboot.bank.jpa.Account[\"currency\"])"
}
``` 
### В контексте клиента пройти в контекст счета
```http request
[GET] <Hub REST API URL>/clients/{{idClient}}/accounts/{{idAccount}}
```            
Ответ
```json 
{
    "id": 6,
    "number": 1,
    "fullNumber": "4090281020001000000001",
    "firstOrderNumber": 409,
    "secondOrderAccNumber": "02",
    "branch": "0001",
    "controlDigit": 2,
    "currency": "RUB",
    "currencyCode": "810",
    "balance": 10.00,
    "status": "ACTIVE",
    "createdDateTime": "2020-11-24T22:05:03.551+00:00",
    "updatedDateTime": "2020-11-24T22:05:03.551+00:00"
}
``` 
Можем получить ошибку если пошли не в тот счет
```json 
{
    "timestamp": "2020-11-25T01:11:19.175+03:00",
    "status": "NOT_FOUND",
    "debugMessage": "Для клиента 2 счет с идентификтором 61 не найден"
}
``` 
### В контексте счета клиента осуществить перевод
```http request
[POST] <Hub REST API URL>/clients/{{idClient}}/accounts/{{idAccount}}/send/{{idTargetAccount}}?summ={{summ}}
```  
Появляется параметр запроса summ - Decimal сумма перевода

В ответ получаем сохраненную транзакцию 
```json
{
    "id": 7,
    "sourceAccount": 4,
    "targetAccount": 3,
    "balanceSourceBefore": 10000000000.00,
    "balanceSourceAfter": 9999999996.66,
    "balanceTargetBefore": 0.00,
    "balanceTargetAfter": 3.34,
    "operationSumm": 3.34,
    "createdDateTime": "2020-11-24T22:13:48.886+00:00"
}
``` 
Возможые ошибки
```json
{
    "timestamp": "2020-11-25T01:19:20.017+03:00",
    "status": "NOT_FOUND",
    "debugMessage": "Счет с идентификтором 31 не найден"
}
``` 
```json
{
    "timestamp": "2020-11-25T01:26:40.121+03:00",
    "status": "NOT_ACCEPTABLE",
    "debugMessage": "На счете 3 недостаточно средств"
}
```  
```json
{
    "timestamp": "2020-11-25T01:45:31.632+03:00",
    "status": "NOT_ACCEPTABLE",
    "debugMessage": "Счет 3, удален"
}
```   
### Получить все счета в банке
```http request
[GET] <Hub REST API URL>/accounts
```
Ответ
```json 
[
    {
        "id": 3,
        "number": 1,
        "fullNumber": "4081781050000000000001",
        "firstOrderNumber": 408,
        "secondOrderAccNumber": "17",
        "branch": "0000",
        "controlDigit": 5,
        "currency": "RUB",
        "currencyCode": "810",
        "balance": 0.00,
        "status": "ACTIVE",
        "createdDateTime": "2020-11-24T22:27:48.624+00:00",
        "updatedDateTime": "2020-11-24T22:27:48.624+00:00"
    },
    {
        "id": 4,
        "number": 1,
        "fullNumber": "2021781050000000000001",
        "firstOrderNumber": 202,
        "secondOrderAccNumber": "17",
        "branch": "0000",
        "controlDigit": 5,
        "currency": "RUB",
        "currencyCode": "810",
        "balance": 10000000000.00,
        "status": "ACTIVE",
        "createdDateTime": "2020-11-24T22:27:48.631+00:00",
        "updatedDateTime": "2020-11-24T22:27:48.631+00:00"
    }
]
``` 
### Пройти в контекст определенного счета
```http request
[GET] <Hub REST API URL>/accounts/{{idAccount}}}}
```                                              
Ответ
```json
{
    "id": 4,
    "number": 1,
    "fullNumber": "2021781050000000000001",
    "firstOrderNumber": 202,
    "secondOrderAccNumber": "17",
    "branch": "0000",
    "controlDigit": 5,
    "currency": "RUB",
    "currencyCode": "810",
    "balance": 10000000000.00,
    "status": "ACTIVE",
    "createdDateTime": "2020-11-24T22:27:48.631+00:00",
    "updatedDateTime": "2020-11-24T22:27:48.631+00:00"
}
```
Возможная ошибка
```json
{
    "timestamp": "2020-11-25T01:34:45.553+03:00",
    "status": "NOT_FOUND",
    "debugMessage": "Счет с идентификтором 5 не найден"
}
```
### Просмотреть все транзакции в контексте определенного счета
```http request
[GET] <Hub REST API URL>/accounts/{{idAccount}}/transactions
```    
Ответ
```json
[
    {
        "id": 5,
        "sourceAccount": 4,
        "targetAccount": 3,
        "balanceSourceBefore": 10000000000.00,
        "balanceSourceAfter": 9998500000.00,
        "balanceTargetBefore": 0.00,
        "balanceTargetAfter": 1500000.00,
        "operationSumm": 1500000.00,
        "createdDateTime": "2020-11-24T22:36:44.163+00:00"
    }
]
``` 
### Перевести средства в контексте счета на другой счет 
```http request
[POST] <Hub REST API URL>/accounts/{{idAccount}}/send/{{idTargetAccount}}?summ={{summ}}
``` 
summ - Decimal сумма перевода   
Ответ
```json
{
    "id": 5,
    "sourceAccount": 4,
    "targetAccount": 3,
    "balanceSourceBefore": 10000000000.00,
    "balanceSourceAfter": 9998500000.00,
    "balanceTargetBefore": 0.00,
    "balanceTargetAfter": 1500000.00,
    "operationSumm": 1500000.00,
    "createdDateTime": "2020-11-24T22:36:44.163+00:00"
}
```    
Возможные ошибки
```json
{
    "timestamp": "2020-11-25T01:40:07.674+03:00",
    "status": "NOT_FOUND",
    "debugMessage": "Счет с идентификтором 31 не найден"
}
```   
```json
{
    "timestamp": "2020-11-25T01:40:27.280+03:00",
    "status": "NOT_ACCEPTABLE",
    "debugMessage": "На счете 4 недостаточно средств"
}
```
```json
{
    "timestamp": "2020-11-25T01:45:31.632+03:00",
    "status": "NOT_ACCEPTABLE",
    "debugMessage": "Счет 3, удален"
}
```
### Закрытие счета
```http request
[DELETE] <Hub REST API URL>/accounts/{{idAccount}}
```   
Если на счете есть средства то
```json
{
    "timestamp": "2020-11-25T01:41:30.276+03:00",
    "status": "NOT_ACCEPTABLE",
    "debugMessage": "Невозможно закрыть счет 3, т.к. баланс не нулевой"
}
``` 
Необходимо перевести средства на другой счет

Если успешно то
```json 
{
    "id": 3,
    "number": 1,
    "fullNumber": "4081781050000000000001",
    "firstOrderNumber": 408,
    "secondOrderAccNumber": "17",
    "branch": "0000",
    "controlDigit": 5,
    "currency": "RUB",
    "currencyCode": "810",
    "balance": 0.00,
    "status": "ARCHIVED",
    "createdDateTime": "2020-11-24T22:27:48.624+00:00",
    "updatedDateTime": "2020-11-24T22:43:47.005+00:00"
}
```   
Статус счета поменялся на `ARCHIVED`

Также может оказаться что счет уже закрыт
```json
{
    "timestamp": "2020-11-25T01:44:14.456+03:00",
    "status": "NOT_ACCEPTABLE",
    "debugMessage": "Невозможно закрыть счет 3, т.к. он уже закрыт"
}
``` 

### Получить список всех транзакций
```http request
[GET] <Hub REST API URL>/transactions
```                           
Ответ
``` json
[
    {
        "id": 5,
        "sourceAccount": 4,
        "targetAccount": 3,
        "balanceSourceBefore": 10000000000.00,
        "balanceSourceAfter": 9998500000.00,
        "balanceTargetBefore": 0.00,
        "balanceTargetAfter": 1500000.00,
        "operationSumm": 1500000.00,
        "createdDateTime": "2020-11-24T22:36:44.163+00:00"
    },
    {
        "id": 6,
        "sourceAccount": 3,
        "targetAccount": 4,
        "balanceSourceBefore": 1500000.00,
        "balanceSourceAfter": 0.00,
        "balanceTargetBefore": 9998500000.00,
        "balanceTargetAfter": 10000000000.00,
        "operationSumm": 1500000.00,
        "createdDateTime": "2020-11-24T22:43:18.699+00:00"
    }
]
```    
Для тестирования использовать профиль -local, предзагружаются 2 клиента, счет, и балансовый счет

Юнит/Фича тесты, к сожалению, не написаны

Еще была идея подтягивать валюты отсюда [Курсы валют](https://www.cbr-xml-daily.ru/daily_json.js)