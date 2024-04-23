# Описание проекта

Это движок микросервиса с собственной базой данных для контроля баланса на счету юридического лица.

Счётом могут пользоваться много независимых внешних клиентов. По этой причине изменение баланса счета может происходить одновременно по нескольким независимым запросам.

## Использованные технологии

**Язык** - Java 21

**Фреймворки**:

* Spring Boot 3.2.4
* Spring Data
* Spring Security

**Автоматизация сборки** – Maven

**База данных** - H2

In-memory база данных H2 хранит данные в оперативной памяти, что ускоряет их обработку.

Операции с данными соответствуют ACID принципам благодаря транзакционному управлению Spring Data.

Spring Security вводит ограничения безопасности HTTP Basic Auth.

# Инструкция по запуску и эксплуатации проекта

## Сборка проекта


**Клонируйте к себе репозиторий с исходным кодом проекта**

```shell
git clone https://github.com/stepagin/hackathon-be-coder-2024
```
Перейдите в директорию проекта

```shell
cd hackathon-be-coder-2024
```

**Соберите jar файл**:

На Linux:

```shell
mvn clean package
```

На Windows:
```shell
.\mvnw clean package --% -Dmaven.test.skip=true
```

**Файл появится в папке target и его можно будет запустить**:

```shell
cd target
java -jar be-coder-1.0.jar 
```

**Приложение запустится на порту 18002, его можно изменить в ``application.properties``**


## Запросы для работы с приложением

Приложение защищено HTTP Basic Auth, для работы необходимо передавать логин и пароль пользователя в заголовке каждого запроса, кроме:

* создания нового пользователя
* просмотра состояния базы данных H2

**OpenAPI** документацию в формате JSON можно получить по ссылке:
```http request
http://localhost:18002/api-docs
```

Посмотреть документацию в **Swagger UI** можно по ссылке:
```http request
http://localhost:18002/swagger-ui.html
```

Разумеется, во время прехода по ссылкам необходимо, чтобы приложение было запущено.

### 1. Создание нового пользователя
Чтобы создать нового пользователя, надо отправить ``POST`` запрос на данный URL:
```
http://localhost:18002/auth/register
```
С телом запроса формата json:
```json
{
    "login": "login",
    "password": "password"
}
```

Пример curl:
```shell
curl --location 'http://localhost:18002/auth/register' \
--header 'Content-Type: application/json' \
--data '{
    "login": "user123",
    "password": "user123"
}'
```
В случае успешной регистрации вы получите json-объект вида:
```json
{
    "id" : 3,
    "login" : "user123"
}
```

Этот объект будет содержать id, выделенный пользователю, и его логин.

### 2. Просмотр логинов и id всех пользователей, которые зарегистрированы в системе

Чтобы получить всех пользователей, надо отправить ``GET`` запрос на URL:
```
http://localhost:18002/auth/all
```
Пример curl
```shell
curl --location 'http://localhost:18002/auth/all' \
-u "user123:user123"
```

В случае успешного выполнения запроса вы получите json-объект вида:
```json
[
    {"id":1,"login":"admin"},
    {"id":2,"login":"user123"}
]
```

### 3. Создание банковского аккаунта для юридической организации
Создание банковского аккаунта происходит через ``POST`` запрос по данному URL:
```
http://localhost:18002/account
```

Пример curl:
```shell
curl --location --request ``POST`` 'http://localhost:18002/account' \
-u "user123:user123"
```
В случае успешного создания аккаунта вы получите json объект вида:

```json
{
    "id":"26aed3f8-d182-4885-a07d-83ad2ab1e898",
    "balance":0
}
```

Который будет содержать ``UUID`` аккаунта и его текущий баланс (при создании он всегда устанавливается в 0).
Когда пользователь создает аккаунт, он автоматически получает к нему доступ и может просматривать состояние этого аккаунта и изменять баланс.

### 4. Просмотр всех аккаунтов, к которым пользователь имеет доступ
Чтобы посмотреть все аккаунты, к которым пользователь имеет доступ, надо отправить ``GET`` запрос по URL, где надо указать user_id пользователя:
```
http://localhost:18002/account/all/{user_id}
```

Пример curl:
```shell
curl --location 'http://localhost:18002/account/all/2' \
-u "user123:user123"
```

В случае успешного выполнения запроса будет получен json-объект вида:
```json
[
    {"accountId":"26aed3f8-d182-4885-a07d-83ad2ab1e898"},
    {"accountId":"86fc5196-57d6-4e4a-9991-7ba2105b1fdf"}
]
```
Каждый элемент содержит ``UUID`` аккаунта (в строковом формате), к которому имеет доступ данный пользователь.
### 5. Получение текущего состояния аккаунта по его id
Чтобы узнать актуальное состояние аккаунта,
необходимо отправить ``GET`` запрос с указанием ``UUID`` аккаунта:
```
http://localhost:18002/account/{UUID}
```

Пример curl:
```shell
curl --location 'http://localhost:18002/account/26aed3f8-d182-4885-a07d-83ad2ab1e898' \
-u "user123:user123"
```

Ответ на этот запрос будет выглядеть таким образом:
```json
{
  "id":"9d557618-4688-42f1-94f2-faf0d86d174b",
  "balance":0
}
```
Он содержит ``UUID`` аккаунта и его баланс.

### 6. Пополнение баланса аккаунта
Для пополнения баланса аккаунта необходимо отправить ``POST`` запрос на URL:
```
http://localhost:18002/account/increase
```
С телом запроса, где надо указать сумму пополнения и объект аккаунта с его ``UUID``:
```json
{
    "amount": 1500,
    "account": {
        "id": "26aed3f8-d182-4885-a07d-83ad2ab1e898"
    }
}
```

Пример curl:
```shell
curl --location 'http://localhost:18002/account/increase' \
--header 'Content-Type: application/json' \
-u "user123:user123" \
--data '{
    "amount": 1500,
    "account": {
        "id": "26aed3f8-d182-4885-a07d-83ad2ab1e898"
    }
}'
```

В случае успеха вы получите сообщение ``Счёт успешно пополнен``

Иначе вы получите статус ответа ``400`` и сообщение об ошибке.

Баланс аккаунта не изменится, так как произойдёт откат транзакции в ответ на исключение.

### 7. Снятие с баланса аккаунта
URL похож при запросе на пополнения счёта:
```
http://localhost:18002/account/increase
```
В теле запрос так же необходимо указать сумму и объект аккаунта с его ``UUID``:
```json
{
    "amount": 1500,
    "account": {
        "id": "26aed3f8-d182-4885-a07d-83ad2ab1e898"
  }
}
```

Пример curl:

```shell
curl --location 'http://localhost:18002/account/decrease' \
--header 'Content-Type: application/json' \
-u "user123:user123" \
--data '{
    "amount": 100,
    "account": {
        "id": "26aed3f8-d182-4885-a07d-83ad2ab1e898"
    }
}'
```
В случае успеха вы получите сообщение: ``Оплата прошла успешно``

В противном случае вы увидите статус ответа ``400`` и сообщение об ошибке.
Деньги не снимутся благодаря транзакциям.

### 8. Присвоение прав на изменение счёта аккаунта
Чтобы выдать пользователю права на изменение баланса, надо отправить ``POST`` запрос на данный URL, где надо указать ``UUID`` аккаунта.:
```
http://localhost:18002/account/grant/{UUID}
```


В теле запроса же надо указать пользователя, которому предоставляется право на аккаунт:
```json
{
    "id": 3,
    "login": "login"
}
```
Присвоить права может только владелец счета.

Пример curl:

```shell
curl --location 'http://localhost:18002/account/grant/26aed3f8-d182-4885-a07d-83ad2ab1e898' \
--header 'Content-Type: application/json' \
-u "user123:user123" \
--data '{
    "id": 100,
    "login": "qwerty"
}'
```
В случае успешного запроса вы получите сообщение ``Пользователю {login} выдан доступ к аккаунту {UUID}``

Иначе же вы увидите статус ответа ``400`` и сообщение ``некорректный запрос на выдачу прав``.

### 9. Отзыв прав на изменение счёта аккаунта
Чтобы отозвать права на изменение баланса, надо отправить ``POST`` запрос на данный URL, где надо указать ``UUID`` аккаунта.:
```
http://localhost:18002/account/revoke/{UUID}
```

В теле запроса же надо указать пользователя, которому предоставляется право на аккаунт:
```json
{
    "id": 3,
    "login": "login"
}
```
Отозвать права может только владелец счета.


Пример curl:

```shell
curl --location 'http://localhost:18002/account/grant/26aed3f8-d182-4885-a07d-83ad2ab1e898' \
--header 'Content-Type: application/json' \
-u "user123:user123" \
--data '{
    "id": 100,
    "login": "qwerty"
}'
```
В случае успешного запроса вы получите сообщение ``У пользователя {login} отозван доступ к аккаунту {UUID}``

Иначе же вы увидите статус ответа ``400`` и сообщение ``некорректный запрос на отзыв прав``.

## Просмотр состояния базы данных

Перейдите по url:
```
http://localhost:18002/h2-console/
```
В открывшуюся форму необходимо ввести следующие данные
JDBC URL: ``jdbc:h2:mem:becoder``

Пользователь: ``root``

Пароль: ``root``

В открывшемся окне можно выполнять sql запросы.

Данные для подключения к БД можно изменить в файле application.properties

## Postman

Вы можете протестировать приложение самостоятельно через Postman.

Примеры запросов представлены [в нашей документации ](https://documenter.getpostman.com/view/33960847/2sA3BkcYLc).
