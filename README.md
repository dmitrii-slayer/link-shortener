# Сервис коротких ссылок link-shortener

## Оглавление
- [Шаги для запоска](#шаги-для-запуска)
- [Описание API и примеры запросов](#описание-api-и-примеры-запросов)
- [Архитектура проекта](#архитектура-проекта)
- [Модели данных](#модели-данных)
- [Тестирование](#тестирование)
---

## Установка

Для запуска сервиса потребуется:

- JDK версии 17+
- Maven

## Шаги для запуска:
### Склонировать проект:

```shell
git clone git@github.com:dmitrii-slayer/link-shortener.git
cd link-shortener
```

### Сборка проекта и запуск приложения:
```shell
mvn clean package
java -jar ./target/link-shortener-0.0.1.jar
```

## Описание API и примеры запросов

### Swagger UI:
http://localhost:8080/swagger-ui/index.html

### Создание новой сокращённой ссылки:
```shell
curl -X POST localhost:8080/api/links/ \
-H 'Content-Type: application/json' \
-d '{"originalUrl": "https://www.github.com/", "username": "test_user", "clickLimit": 10}'
```

### Просмотр информации о ссылке
```shell
curl -X GET localhost:8080/api/links/my-short-link/user/test_user/info
```

### Просмотр ссылок пользователя:
```shell
curl -X GET localhost:8080/api/links/user/test_user
```

### Редактирование лимита переходов для существующей ссылки:
```shell
curl -X PUT localhost:8080/api/links/my-short-link/user/test_user/limit \
-H 'Content-Type: application/json' \
-d '{"clickLimit": 20}'
```

### Удаление ссылки
```shell
curl -X DELETE localhost:8080/api/links/my-short-link/user/test_user
```

## Чтобы проверить работоспобность ссылки нужно в браузере ввести полученную короткую ссылку:
http://localhost:8080/my-short-link
где my-short-link нужно подставить сгенерированную короткую ссылку

```shell
curl -I localhost:8080/my-short-link
```

## Архитектура проекта
Проект построен по паттерну MVC и реализован на платформе Spring Boot. Основные компоненты системы:

- Controller: принимает запросы и делегирует обработку сервисному слою.
- DTO: объекты передачи данных между слоями и внешними сервисами.
- Service: компоненты, реализующие бизнес-логику.
- DAO Layer: обеспечивает доступ к данным через ORM Hibernate.
- Entities: сущностные классы, представляющие таблицы базы данных.


## Модели данных:

| Entity        | Описание |
|---------------|----------|
| ShortLink     | Короткая ссылка    |
| User          | Пользователь сервиса     |

## Тестирование

Проект включает unit и интеграционные тесты.

### Команда для выполнения тестов:
```shell
mvn test
```
