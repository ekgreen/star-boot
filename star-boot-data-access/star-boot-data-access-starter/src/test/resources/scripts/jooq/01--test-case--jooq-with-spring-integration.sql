-- Скрипт инициализации тестовой базы данных для JOOQ тестов
-- Создает схему ratings и таблицу credit_object

-- Создание схемы ratings
CREATE SCHEMA IF NOT EXISTS ratings;

-- Создание таблицы credit_object
CREATE TABLE IF NOT EXISTS ratings.credit_object
(
    id                     bigint primary key          not null,
    code                   varchar(128)                not null,
    name                   varchar(256)                not null,
    isin                   varchar(32)                 null,
    sector_id              integer                     not null,
    parent_id              bigint                      null,
    is_deleted             integer                     not null,
    creation_timestamp     timestamp without time zone not null,
    modification_timestamp timestamp without time zone not null
);

-- Комментарии на столбцы
COMMENT ON COLUMN ratings.credit_object.id                      IS 'Уникальный числовой идентификатор объекта под наблюдением кредитного агентства в регистре';
COMMENT ON COLUMN ratings.credit_object.code                    IS 'Универсальный буквенный код (кодированное название, очищенное от лишних символов, в kebab-case)';
COMMENT ON COLUMN ratings.credit_object.name                    IS 'Буквенное название, приведенное к общему формату';
COMMENT ON COLUMN ratings.credit_object.isin                    IS 'ISIN предназначен для стандартизации и упрощения идентификации ценных бумаг на мировом рынке';
COMMENT ON COLUMN ratings.credit_object.sector_id               IS 'Сектор бизнеса или экономики к которому относится объект наблюдения';
COMMENT ON COLUMN ratings.credit_object.parent_id               IS 'Родственная связь между дочерними и родительскими объектами, например между ценной бумагой и организацией выпускающей ее';
COMMENT ON COLUMN ratings.credit_object.is_deleted              IS 'Объект присутствует или отсутствует в последней выгрузке';
COMMENT ON COLUMN ratings.credit_object.creation_timestamp      IS 'Время вставки записи';
COMMENT ON COLUMN ratings.credit_object.modification_timestamp  IS 'Время изменения записи';

-- Индексы
CREATE UNIQUE INDEX IF NOT EXISTS idx__ratings_credit_object__id ON ratings.credit_object (id);
CREATE UNIQUE INDEX IF NOT EXISTS idx__ratings_credit_object__code ON ratings.credit_object (code);

-- Вставляем несколько тестовых записей для проверки
INSERT INTO ratings.credit_object (id, code, name, sector_id, is_deleted, creation_timestamp, modification_timestamp)
VALUES
    (1, 'TEST_COMPANY_1', 'Test Company 1', 1, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'TEST_COMPANY_2', 'Test Company 2', 2, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 'TEST_BOND_1', 'Test Bond 1', 1, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;