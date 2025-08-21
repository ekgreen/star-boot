# Star Boot Framework

[![CI/CD](https://github.com/ekgreen/star-boot/workflows/Build/badge.svg)](https://github.com/ekgreen/star-boot/actions)
[![Coverage](https://codecov.io/gh/ekgreen/star-boot/branch/main/graph/badge.svg)](https://codecov.io/gh/ekgreen/star-boot)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)

Набор библиотек для работы с Java проектами, построенный на основе Spring Boot.

## 🏗️ Архитектура проекта

### Диаграмма зависимостей модулей

```mermaid
graph TD
    %% Корневой модуль
    A[star-boot] --> B[star-boot-dependencies]
    A --> C[star-boot-parent]

    %% Базовые модули
    A --> D[star-boot-model-and-interfaces]
    A --> E[star-boot-reflection]
    A --> F[star-boot-code-smell]

    %% Сериализация
    A --> G[star-boot-serialization]
    G --> G1[star-boot-serialization-api]
    G --> G2[star-boot-json-serialization-implementation]

    %% Прикладные модули
    A --> H[star-boot-toolbox]
    A --> I[star-boot-spring]
    A --> J[star-boot-identifier]
    A --> K[star-boot-http-client]
    A --> L[star-boot-cache]
    A --> M[star-boot-data-access]
    A --> N[star-boot-dictionaries]

    %% Зависимости между модулями
    G1 --> D
    G1 --> E
    G2 --> G1
    H --> D
    H --> E
    H --> G2
    E --> D

    %% Стилизация
    classDef rootModule fill:#e1f5fe
    classDef coreModule fill:#f3e5f5
    classDef appModule fill:#e8f5e8
    classDef parentModule fill:#fff3e0

    class A rootModule
    class B,C parentModule
    class D,E,F coreModule
    class G,G1,G2,H,I,J,K,L,M,N appModule
```

### Иерархия наследования Maven

```mermaid
graph TD
    A[Spring Boot Starter Parent<br/>3.5.4] --> B[star-boot<br/>Root POM]
    B --> C[star-boot-parent<br/>Configuration Parent]
    C --> D[star-boot-dependencies<br/>BOM]

    B --> E[star-boot-serialization<br/>Multi-module]
    C --> F[star-boot-serialization-api]
    C --> G[star-boot-json-serialization-implementation]
    E --> F
    E --> G

    C --> H[star-boot-model-and-interfaces]
    C --> I[star-boot-reflection]
    C --> J[star-boot-toolbox]
    C --> K[star-boot-spring]
    C --> L[star-boot-identifier]
    C --> M[star-boot-http-client]
    C --> N[star-boot-cache]
    C --> O[star-boot-data-access]
    C --> P[star-boot-dictionaries]
    C --> Q[star-boot-code-smell]

    classDef springBoot fill:#6ab7ff
    classDef rootPom fill:#ffeb3b
    classDef parentPom fill:#ff9800
    classDef modulePom fill:#4caf50
    classDef libraryPom fill:#9c27b0

    class A springBoot
    class B rootPom
    class C,D parentPom
    class E modulePom
    class F,G,H,I,J,K,L,M,N,O,P,Q libraryPom
```

## 📦 Описание модулей

### Управляющие модули

| Модуль | Тип | Описание |
|--------|-----|----------|
| **star-boot** | `pom` | Корневой модуль проекта, определяет общую структуру и включает все подмодули |
| **star-boot-parent** | `pom` | Родительский POM с конфигурацией плагинов, свойств и управления зависимостями |
| **star-boot-dependencies** | `pom` | BOM (Bill of Materials) для управления версиями зависимостей |

### Базовые модули

| Модуль | Тип | Описание |
|--------|-----|----------|
| **star-boot-model-and-interfaces** | `jar` | Базовые модели данных, интерфейсы и контракты системы |
| **star-boot-reflection** | `jar` | Утилиты для работы с рефлексией Java, анализ классов и методов |
| **star-boot-code-smell** | `jar` | Инструменты статического анализа кода и обнаружения "code smells" |

### Модули сериализации

| Модуль | Тип | Описание |
|--------|-----|----------|
| **star-boot-serialization** | `pom` | Группирующий модуль для компонентов сериализации |
| **star-boot-serialization-api** | `jar` | API для сериализации объектов, абстракции и интерфейсы |
| **star-boot-json-serialization-implementation** | `jar` | Реализация JSON сериализации на основе Jackson |

### Прикладные модули

| Модуль | Тип | Описание |
|--------|-----|----------|
| **star-boot-toolbox** | `jar` | Набор утилит для работы с файлами (Excel, CSV), HTML парсинг (JSoup) |
| **star-boot-spring** | `jar` | Интеграция со Spring Framework, автоконфигурация, Spring-специфичные компоненты |
| **star-boot-identifier** | `jar` | Генерация и работа с уникальными идентификаторами |
| **star-boot-http-client** | `jar` | HTTP клиент с поддержкой различных протоколов и форматов |
| **star-boot-cache** | `jar` | Система кеширования с поддержкой различных провайдеров |
| **star-boot-data-access** | `jar` | Абстракции для работы с базами данных, поддержка JOOQ и Liquibase |
| **star-boot-dictionaries** | `jar` | Система справочников и классификаторов |

## 🔧 Технологический стек

- **Java**: 24
- **Spring Boot**: 3.5.4
- **Jakarta EE**: с jakarta imports
- **Lombok**: Для генерации boilerplate кода
- **Maven**: Система сборки
- **JOOQ**: Type-safe SQL builder
- **Liquibase**: Database migration
- **Apache POI**: Работа с Excel файлами
- **JSoup**: HTML парсинг
- **OpenCSV**: CSV обработка
- **JUnit 5**: Тестирование
- **Testcontainers**: Integration тестирование

## 📊 Зависимости между модулями

### Прямые зависимости

- `star-boot-reflection` зависит от:
  - `star-boot-model-and-interfaces`

- `star-boot-serialization-api` зависит от:
  - `star-boot-model-and-interfaces`
  - `star-boot-reflection`

- `star-boot-json-serialization-implementation` зависит от:
  - `star-boot-serialization-api` (транзитивно получает остальные)
  - `star-boot-reflection`

- `star-boot-toolbox` зависит от:
  - `star-boot-model-and-interfaces`
  - `star-boot-reflection`
  - `star-boot-json-serialization-implementation`

### Внешние библиотеки

Проект использует контролируемые версии внешних зависимостей через `star-boot-dependencies` BOM:
- Apache POI (Excel)
- JSoup (HTML parsing)
- OpenCSV (CSV processing)
- PostgreSQL driver
- Testcontainers

## 🚀 Начало работы

1. Убедитесь, что у вас установлена Java 24+
2. Клонируйте репозиторий
3. Выполните сборку: `./mvnw clean install`

## 📈 Качество кода

Проект включает:
- **Checkstyle** - статический анализ стиля кода
- **JaCoCo** - анализ покрытия тестами (минимум 80% для строк, 75% для веток)
- **Allure** - детальная отчетность по тестам
- **Maven Enforcer** - контроль качества зависимостей и конфигурации

## 📝 Лицензия

Apache License 2.0 - подробности в файле LICENSE

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the
parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.

