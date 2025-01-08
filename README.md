# Short Link App

## Описание проекта

Приложение представляет собой полноценное веб-приложение.  
Взаимодействие происходит с помощью REST, хранение данных - в PostgreSQL.  

* [LinkController](src/main/java/sf/shortlinks/api/LinkController.java)

### Работа с приложением

Взаимодействие с приложением происходит с помощью трех эндпоинтов:

1. POST api/v1/link

Этот эндпоинт используется для создания короткой ссылки.  
Если запрос отправляется без ownerUid(пользователь не зарегистрирован) или нет активной записи с таким url - будет создана ссылка.  
Если запрос отправляется с ownerUid и есть активная запись - будет записана новая короткая ссылка.  

Пример запроса(пользователь зарегистрирован в системе)
```json
{
  "url": "http://www.google.com",
  "ownerUid": "cc01c8ed-a6e8-466d-b578-9f2be9a1c9b2",
  "limit": 10,
  "duration": 60
}
```
Пример ответа: 
```json
{
  "ownerUid": "e6690715-d3eb-458f-96df-68448fe9f4ca",
  "shortUrl": "www.leenk.com/myO4z",
  "expirationDateTime": "2025-01-08T06:30:35.444711"
}
```
Алгоритм генерации короткой ссылки указан в классе **LinkGenerator**. Параметры алгоритма вынесены в конфигурационный файл [application.yml](src/main/resources/application.yml)

2. PATCH api/v1/link

Этот эндпоинт используется для изменения параметров ссылки - срок жизни(expiration_time), максимальное количество переходов(lim) и флаг активности(is_active).  
Если флаг активности помечается как false, то такая ссылка становится недоступна пользователю, а затем удаляется с помощью [LinkScheduler](src/main/java/sf/shortlinks/scheduler/LinkScheduler.java).
Поиск ссылки в БД происходит по owner_uid и short_url

Пример запроса:
```json
{
  "ownerUid": "cc01c8ed-a6e8-466d-b578-9f2be9a1c9b2",
  "shortUrl": "www.leenk.com/MXF8P",
  "limit": 11,
  "duration": 60,
  "isActive": false
}
```
Пример ответа:
```json
{
  "ownerUid": "cc01c8ed-a6e8-466d-b578-9f2be9a1c9b2",
  "shortUrl": "www.leenk.com/MXF8P",
  "expirationDateTime": "2025-01-08T06:14:47.979744",
  "limit": 11,
  "isActive": false
}
```

3. POST api/v1/link/redirect

Этот эндпоинт используется для перехода по оригинальной ссылке.   
Поиск ссылки в БД происходит по owner_uid и short_url, при этом увеличивается счетчик.
Если counter(текущее значение счетчика) становится равным lim(предельное значение счетчика), то is_active помечается false.  
Пример запроса:
```json
{
  "ownerUid": "cc01c8ed-a6e8-466d-b578-9f2be9a1c9b2",
  "shortUrl": "www.leenk.com/MXF8P"
}
```
Запрос отправлял в Postman, там генерируются нужные заголовки, чтобы получить страницу google. Редирект производится с помощью статуса 303(SEE_OTHER).  
* [AdviceController](src/main/java/sf/shortlinks/api/AdviceController.java) 

Класс для того, чтобы возвращать клиенту информацию о возможных ошибках.

* [LinkGenerator](src/main/java/sf/shortlinks/generator/LinkGenerator.java)

Класс с настройками и методом генерации короткой ссылки.

* [LinkScheduler](src/main/java/sf/shortlinks/scheduler/LinkScheduler.java)

Автоматическое удаление неактивных ссылок(по is_active или времени) происходит с помощью шедулера, который срабатывает раз в промежуток(задаваемый cron-выражением).  

Сравнение пользовательского времени и системного происходит в методе [LinkServiceImpl.prepareExpirationTime](src/main/java/sf/shortlinks/service/impl/LinkServiceImpl.java)
Сравнение пользовательского лимита и системного происходит в методе [LinkServiceImpl.prepareLimit](src/main/java/sf/shortlinks/service/impl/LinkServiceImpl.java)

### Конфигурационный файл 
```yaml
# настройки приложения
spring:
  application:
    name: short-links
  datasource:
    url: 
    username: 
    password: 
    driver-class-name: org.postgresql.Driver
  liquibase:
    change-log: classpath:db/changelog/db.changelog-master.xml
    default-schema: public
  scheduling:
    enabled: true
    cron: 0 0 0 * * *

# настройки формирования короткой ссылки
link:
  duration: 120 # в минутах
  limit: 5 # количество переходов
  algorithm:
    prefix: www.leenk.com/ 
    length: 5
    chars:
      letters: true # при false ссылка содержит только числа
      numbers: true
```
### Модель данных 
Исходное состояние БД задается с помощью скрипта liquibase [db.changelog-master.xml](src/main/resources/db/changelog/db.changelog-master.xml)

```sql
create extension if not exists "uuid-ossp";

create table link(
    id uuid not null,
    owner_uid uuid not null, -- идентификатор пользователя
    url varchar(255) not null, -- ссылка на ресурс
    short_url varchar(255) unique not null, -- короткая ссылка
    created_at timestamp default now(), -- дата и время создания
    expiration_time time not null, -- длительность ссылки
    counter int not null default 0, -- текущий счетчик переходов
    lim int not null, -- максимальный счетчик переходов
    is_active boolean default true, -- флаг активности

    primary key (id),
    unique (owner_uid, url),  -- на каждый ресурс может быть одна запись на пользователя
    unique (owner_uid, short_url), -- короткой ссылкой владеет только 1 пользователь
    check (counter <= lim)
)
```
## Как тестировать приложение

В приложение добавлены интеграционные тесты, которые работают на TestContainers.  
Необходимо либо иметь включенный докер на устройстве, либо их отключить и в тестовом конфигурационном файле указать настройки БД.
Если в основном конфигурационном файле указать настройки БД, можно запустить приложение и вручную отправить запросы на сервер и проверить работу приложения.
