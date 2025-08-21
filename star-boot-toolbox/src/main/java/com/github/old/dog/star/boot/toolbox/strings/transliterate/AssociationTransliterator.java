package com.github.old.dog.star.boot.toolbox.strings.transliterate;

import java.util.HashMap;
import java.util.Map;
import static java.lang.Character.UnicodeBlock;
import com.github.old.dog.star.boot.toolbox.strings.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Реализация {@link Transliterator}, которая использует ассоциативные карты для транслитерации
 * символов и строк между различными языками.
 * <p>
 * Этот класс поддерживает как однобуквенные, так и многобуквенные ассоциации, что позволяет
 * создавать более сложные правила транслитерации. Например, русская буква "щ" может
 * транслитерироваться как комбинация "sch" в английском языке.
 * <p>
 * Класс предоставляет предварительно настроенный транслитератор EN_RU для транслитерации
 * с русского на английский язык.
 */
public class AssociationTransliterator implements Transliterator {

    /**
     * Исходный язык для транслитерации.
     */
    private final Strings.Languages originalLanguage;

    /**
     * Целевой язык для транслитерации.
     */
    private final Strings.Languages expectedLanguage;

    /**
     * Карта для многосимвольных ассоциаций (например, "щ" -> "sch").
     */
    private final Map<String, String> multiCharacterAssociations;

    /**
     * Карта для односимвольных ассоциаций (например, "а" -> "a").
     */
    private final Map<Character, String> onlyCharacterAssociations;

    /**
     * Создает новый экземпляр транслитератора с указанными исходным и целевым языками.
     *
     * @param originalLanguage исходный язык, с которого будет осуществляться транслитерация
     * @param expectedLanguage целевой язык, на который будет осуществляться транслитерация
     */
    public AssociationTransliterator(@NotNull Strings.Languages originalLanguage, @NotNull Strings.Languages expectedLanguage) {
        if (originalLanguage == null || expectedLanguage == null) {
            throw new IllegalArgumentException("originalLanguage & expectedLanguage cannot be null");
        }

        this.originalLanguage = originalLanguage;
        this.expectedLanguage = expectedLanguage;

        this.multiCharacterAssociations = new HashMap<>();
        this.onlyCharacterAssociations = new HashMap<>();
    }

    /**
     * Получает транслитерацию для указанной строки.
     * <p>
     * Если строка состоит из одного символа, метод делегирует выполнение методу {@link #get(char)}.
     * В противном случае ищет соответствие в карте многосимвольных ассоциаций.
     *
     * @param ch строка для транслитерации
     * @return транслитерированная строка или null, если соответствие не найдено
     */
    @Override
    public @Nullable String get(String ch) {
        return ch.length() == 1 ? this.get(ch.charAt(0)) : this.multiCharacterAssociations.get(ch);
    }

    /**
     * Получает транслитерацию для указанного символа.
     * <p>
     * Ищет соответствие в карте односимвольных ассоциаций.
     *
     * @param ch символ для транслитерации
     * @return транслитерированная строка или null, если соответствие не найдено
     */
    @Override
    public @Nullable String get(char ch) {
        return onlyCharacterAssociations.get(ch);
    }

    /**
     * Проверяет, принадлежит ли символ к блоку юникода исходного языка.
     *
     * @param letter проверяемый символ
     * @return true, если символ принадлежит блоку юникода исходного языка
     */
    @Override
    public boolean inOriginalBlock(char letter) {
        return UnicodeBlock.of(letter) == originalLanguage.getBlock();
    }

    /**
     * Проверяет, принадлежит ли символ к блоку юникода целевого языка.
     *
     * @param letter проверяемый символ
     * @return true, если символ принадлежит блоку юникода целевого языка
     */
    @Override
    public boolean inExpectedBlock(char letter) {
        return UnicodeBlock.of(letter) == expectedLanguage.getBlock();
    }

    /**
     * Возвращает код исходного языка.
     *
     * @return строковое представление кода исходного языка
     */
    @Override
    public @NotNull String getOriginalLanguage() {
        return originalLanguage.getLanguage();
    }

    /**
     * Возвращает код целевого языка.
     *
     * @return строковое представление кода целевого языка
     */
    @Override
    public @NotNull String getExpectedLanguage() {
        return expectedLanguage.getLanguage();
    }

    /**
     * Добавляет новую ассоциацию для транслитерации.
     * <p>
     * Если первый аргумент состоит из одного символа, ассоциация добавляется в карту
     * односимвольных ассоциаций. В противном случае - в карту многосимвольных ассоциаций.
     * <p>
     * Метод поддерживает цепочку вызовов (fluent interface).
     *
     * @param ch1 исходная строка или символ
     * @param ch2 соответствующая транслитерация
     * @return this для цепочки вызовов
     */
    public AssociationTransliterator associate(String ch1, String ch2) {
        if (ch1.length() == 1) {
            this.onlyCharacterAssociations.put(ch1.charAt(0), ch2);
        } else {
            this.multiCharacterAssociations.put(ch1, ch2);
        }
        return this;
    }

    /**
     * Предварительно настроенный транслитератор для конвертации с русского на английский язык.
     * <p>
     * Содержит полный набор правил транслитерации для всех букв русского алфавита,
     * включая заглавные буквы. Например:
     * <ul>
     *   <li>"а" -> "a"</li>
     *   <li>"ж" -> "zh"</li>
     *   <li>"щ" -> "sch"</li>
     * </ul>
     */
    public static final AssociationTransliterator EN_RU = new AssociationTransliterator(Strings.Languages.RUSSIAN, Strings.Languages.ENGLISH)
            .associate("а", "a")
            .associate("б", "b")
            .associate("в", "v")
            .associate("г", "g")
            .associate("д", "d")
            .associate("е", "e")
            .associate("ё", "yo")
            .associate("ж", "zh")
            .associate("з", "z")
            .associate("и", "i")
            .associate("й", "y")
            .associate("к", "k")
            .associate("л", "l")
            .associate("м", "m")
            .associate("н", "n")
            .associate("о", "o")
            .associate("п", "p")
            .associate("р", "r")
            .associate("с", "s")
            .associate("т", "t")
            .associate("у", "u")
            .associate("ф", "f")
            .associate("х", "kh")
            .associate("ц", "ts")
            .associate("ч", "ch")
            .associate("ш", "sh")
            .associate("щ", "sch")
            .associate("ъ", "")
            .associate("ы", "y")
            .associate("ь", "")
            .associate("э", "e")
            .associate("ю", "yu")
            .associate("я", "ya")
            // Заглавные буквы
            .associate("А", "A")
            .associate("Б", "B")
            .associate("В", "V")
            .associate("Г", "G")
            .associate("Д", "D")
            .associate("Е", "E")
            .associate("Ё", "Yo")
            .associate("Ж", "Zh")
            .associate("З", "Z")
            .associate("И", "I")
            .associate("Й", "Y")
            .associate("К", "K")
            .associate("Л", "L")
            .associate("М", "M")
            .associate("Н", "N")
            .associate("О", "O")
            .associate("П", "P")
            .associate("Р", "R")
            .associate("С", "S")
            .associate("Т", "T")
            .associate("У", "U")
            .associate("Ф", "F")
            .associate("Х", "Kh")
            .associate("Ц", "Ts")
            .associate("Ч", "Ch")
            .associate("Ш", "Sh")
            .associate("Щ", "Sch")
            .associate("Ъ", "")
            .associate("Ы", "Y")
            .associate("Ь", "")
            .associate("Э", "E")
            .associate("Ю", "Yu")
            .associate("Я", "Ya");
}
