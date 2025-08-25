package com.github.old.dog.star.boot.toolbox.strings.transliterate;

import com.github.old.dog.star.boot.toolbox.strings.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Реализация {@link Transliterator} для перевода языка в самого себя (identity mapping).
 * <p>
 * Этот класс используется, когда требуется транслитератор, который не изменяет входные данные.
 * Он возвращает символы и строки без изменений, но при этом соблюдает интерфейс транслитератора,
 * что позволяет использовать его в тех же контекстах, что и другие транслитераторы.
 * <p>
 * Класс предоставляет статические константы для часто используемых языков: {@link #ENGLISH} и {@link #RUSSIAN}.
 */
public class IdentityTransliterator implements Transliterator {

    /**
     * Язык, для которого создан этот identity транслитератор.
     */
    private final Strings.Languages language;

    /**
     * Создает новый экземпляр identity транслитератора для указанного языка.
     *
     * @param language язык, для которого создается транслитератор
     */
    public IdentityTransliterator(@NotNull Strings.Languages language) {
        this.language = language;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Для identity транслитератора возвращает входную строку без изменений.
     * Обрабатывает случаи null и пустых строк корректно.
     *
     * @param ch строка для транслитерации
     * @return исходная строка без изменений
     */
    @Override
    public @Nullable String get(String ch) {
        return ch;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Для identity транслитератора преобразует символ в строку и возвращает его без изменений.
     *
     * @param ch символ для транслитерации
     * @return символ, преобразованный в строку
     */
    @Override
    public @Nullable String get(char ch) {
        // Для identity транслитератора возвращаем символ как строку
        return String.valueOf(ch);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Проверяет, принадлежит ли символ к блоку юникода языка этого транслитератора.
     *
     * @param letter проверяемый символ
     * @return true, если символ принадлежит блоку юникода языка транслитератора
     */
    @Override
    public boolean inOriginalBlock(char letter) {
        return language.match(letter);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Для identity транслитератора исходный и целевой блоки одинаковы, поэтому
     * этот метод делегирует выполнение методу {@link #inOriginalBlock(char)}.
     *
     * @param letter проверяемый символ
     * @return true, если символ принадлежит блоку юникода языка транслитератора
     */
    @Override
    public boolean inExpectedBlock(char letter) {
        // Для identity транслитератора исходный и целевой блоки одинаковы
        return language.match(letter);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Возвращает код языка этого транслитератора.
     *
     * @return строковое представление кода языка
     */
    @Override
    public @NotNull String getOriginalLanguage() {
        return language.getLanguage();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Для identity транслитератора исходный и целевой языки одинаковы, поэтому
     * этот метод делегирует выполнение методу {@link #getOriginalLanguage()}.
     *
     * @return строковое представление кода языка
     */
    @Override
    public @NotNull String getExpectedLanguage() {
        // Для identity транслитератора исходный и целевой языки одинаковы
        return language.getLanguage();
    }

    /**
     * Identity транслитератор для английского языка.
     */
    public static final IdentityTransliterator ENGLISH = new IdentityTransliterator(Strings.Languages.ENGLISH);

    /**
     * Identity транслитератор для русского языка.
     */
    public static final IdentityTransliterator RUSSIAN = new IdentityTransliterator(Strings.Languages.RUSSIAN);
}
