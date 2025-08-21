package com.github.old.dog.star.boot.dictionaries.spring.entities;

import java.time.LocalDateTime;
import com.github.old.dog.star.boot.dictionaries.api.annotations.DictionaryColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.github.old.dog.star.boot.dictionaries.api.annotations.Code;
import com.github.old.dog.star.boot.dictionaries.api.annotations.CreationTimestamp;
import com.github.old.dog.star.boot.dictionaries.api.annotations.Definition;
import com.github.old.dog.star.boot.dictionaries.api.annotations.DictionaryTable;
import com.github.old.dog.star.boot.dictionaries.api.annotations.Id;
import com.github.old.dog.star.boot.dictionaries.api.annotations.Name;

/**
 * Тестовая сущность для проверки работы системы справочников.
 *
 * <p>Представляет справочник тестовых категорий с базовым набором атрибутов
 * для проверки функциональности автоматической регистрации и маппинга.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DictionaryTable(schema = "test", table = "categories")
public class TestCategoryEntity {

    /**
     * Уникальный идентификатор категории
     */
    @Id
    private Integer id;

    /**
     * Код категории
     */
    @Code
    private String code;

    /**
     * Наименование категории
     */
    @Name
    private String name;

    /**
     * Описание категории
     */
    @Definition
    private String definition;

    /**
     * Время создания записи
     */
    @CreationTimestamp
    private LocalDateTime creationTimestamp;

    /**
     * Активность записи
     */
    @DictionaryColumn(attribute = "is_active")
    private Boolean isActive;

    /**
     * Порядок сортировки
     */
    @DictionaryColumn(attribute = "sort_order")
    private Integer sortOrder;
}
