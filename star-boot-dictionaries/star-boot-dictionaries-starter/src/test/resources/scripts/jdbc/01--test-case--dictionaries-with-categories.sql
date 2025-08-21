-- Схема для тестирования системы справочников

DO
$BLOCK$
    BEGIN
        -- Создаем схему для тестов (опционально)
        CREATE SCHEMA IF NOT EXISTS test;

        -- Устанавливаем search_path для удобства
        SET search_path TO test, public;

        -- Таблица для тестовых категорий
        CREATE TABLE IF NOT EXISTS test.categories
        (
            id                 INTEGER PRIMARY KEY,
            code               VARCHAR(50)  NOT NULL UNIQUE,
            name               VARCHAR(100) NOT NULL,
            definition         VARCHAR(255),
            is_active          BOOLEAN                     DEFAULT true,
            sort_order         INTEGER                     DEFAULT 0,
            creation_timestamp TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
        );

        -- Индексы для производительности
        CREATE INDEX IF NOT EXISTS idx_test_categories_code ON test.categories (code);
        CREATE INDEX IF NOT EXISTS idx_test_categories_active ON test.categories (is_active);
        CREATE INDEX IF NOT EXISTS idx_test_categories_sort ON test.categories (sort_order);

        -- Комментарии для документации (PostgreSQL style)
        COMMENT ON TABLE test.categories IS 'Тестовая таблица справочника категорий для проверки системы справочников';
        COMMENT ON COLUMN test.categories.id IS 'Уникальный идентификатор категории';
        COMMENT ON COLUMN test.categories.code IS 'Уникальный код категории';
        COMMENT ON COLUMN test.categories.name IS 'Наименование категории';
        COMMENT ON COLUMN test.categories.definition IS 'Описание категории';
        COMMENT ON COLUMN test.categories.is_active IS 'Флаг активности записи';
        COMMENT ON COLUMN test.categories.sort_order IS 'Порядок сортировки';
        COMMENT ON COLUMN test.categories.creation_timestamp IS 'Время создания записи';

        -- Начальные тестовые данные
        INSERT INTO test.categories (id, code, name, definition, is_active, sort_order)
        VALUES (1, 'TECH', 'Technology', 'Technology sector companies', true, 1),
               (2, 'FINANCE', 'Finance', 'Financial services sector', true, 2),
               (3, 'HEALTHCARE', 'Healthcare', 'Healthcare and pharmaceuticals', false, 3),
               (4, 'ENERGY', 'Energy', 'Energy and utilities sector', true, 4),
               (5, 'RETAIL', 'Retail', 'Retail and consumer goods', true, 5)
        ON CONFLICT (id) DO NOTHING;
        -- Избегаем ошибок при повторном запуске

        -- Создаем последовательность для автоинкремента (если нужно)
        CREATE SEQUENCE IF NOT EXISTS test.categories_id_seq
            START WITH 100
            INCREMENT BY 1
            OWNED BY test.categories.id;

    END
$BLOCK$
