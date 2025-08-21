package com.github.old.dog.star.boot.toolbox.strings;

import com.github.old.dog.star.boot.toolbox.strings.transliterate.AssociationTransliterator;
import com.github.old.dog.star.boot.toolbox.strings.transliterate.Transliterator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Strings - Тесты транслитерации")
class StringsTransliterationTest {

    @Nested
    @DisplayName("StringChain - Цепочка операций")
    class StringChainTests {

        @Test
        @DisplayName("Создание цепочки с валидными параметрами")
        void shouldCreateChainWithValidParameters() {
            // given
            String input = "Тестовая строка";

            // when
            Strings.StringChain chain = Strings.chain(
                    Strings.Languages.RUSSIAN,
                    Strings.TextCase.PLAIN_TEXT,
                    input
            );

            // then
            assertNotNull(chain);
        }

        @Test
        @DisplayName("Транслитерация русского текста в английский")
        void shouldTransliterateRussianToEnglish() {
            // given
            String russianText = "Привет мир";

            // when
            String result = Strings.chain(Strings.Languages.RUSSIAN, Strings.TextCase.PLAIN_TEXT, russianText)
                    .transliterate(Strings.Languages.ENGLISH, Strings.Options.Include.WHITESPACE)
                    .end();

            // then
            assertEquals("Privet mir", result);
        }

        @Test
        @DisplayName("Транслитерация с изменением регистра на KEBAB_CASE")
        void shouldTransliterateAndChangeToKebabCase() {
            // given
            String russianText = "Тестовая Строка";

            // when
            String result = Strings.chain(Strings.Languages.RUSSIAN, Strings.TextCase.PLAIN_TEXT, russianText)
                    .transliterate(Strings.Languages.ENGLISH, Strings.Options.Include.WHITESPACE)
                    .changeCase(Strings.TextCase.KEBAB_CASE)
                    .end();

            // then
            assertEquals("testovaya-stroka", result);
        }

        @Test
        @DisplayName("Транслитерация с опциями включения цифр и пробелов")
        void shouldTransliterateWithDigitsAndWhitespace() {
            // given
            String russianText = "Компания123 тест";
            int options = Strings.Options.Include.DIGITS | Strings.Options.Include.WHITESPACE;

            // when
            String result = Strings.chain(Strings.Languages.RUSSIAN, Strings.TextCase.PLAIN_TEXT, russianText)
                    .transliterate(Strings.Languages.ENGLISH, options)
                    .end();

            // then
            assertEquals("Kompaniya123 test", result);
        }

        @Test
        @DisplayName("BUG FIX: NPE при null originalLanguage в Transliterator - тест покрывает основную ошибку из стектрейса")
        void shouldHandleNullOriginalLanguageInTransliterator() {
            // given
            String input = "Тест";

            // when & then
            assertDoesNotThrow(() -> {
                String result = Strings.chain(Strings.Languages.RUSSIAN, Strings.TextCase.PLAIN_TEXT, input)
                        .transliterate(Strings.Languages.ENGLISH)
                        .end();
                assertNotNull(result);
            });
        }

        @Test
        @DisplayName("Цепочка с null значением")
        void shouldHandleNullValue() {
            // when & then
            assertDoesNotThrow(() -> {
                String result = Strings.chain(Strings.Languages.RUSSIAN, Strings.TextCase.PLAIN_TEXT, null)
                        .transliterate(Strings.Languages.ENGLISH)
                        .end();
                assertNull(result);
            });
        }

        @Test
        @DisplayName("Цепочка с пустой строкой")
        void shouldHandleEmptyString() {
            // given
            String emptyString = "";

            // when
            String result = Strings.chain(Strings.Languages.RUSSIAN, Strings.TextCase.PLAIN_TEXT, emptyString)
                    .transliterate(Strings.Languages.ENGLISH)
                    .end();

            // then
            assertEquals("", result);
        }
    }

    @Nested
    @DisplayName("Languages - Транслитерация языков")
    class LanguagesTests {

        @Test
        @DisplayName("Транслитерация русского в английский")
        void shouldTransliterateRussianToEnglish() {
            // given
            String input = "привет";

            // when
            String result = Strings.Languages.RUSSIAN.transliterate(Strings.Languages.ENGLISH, input, 0);

            // then
            assertEquals("privet", result);
        }

        @Test
        @DisplayName("Транслитерация с сохранением пробелов")
        void shouldTransliterateWithWhitespace() {
            // given
            String input = "привет мир";

            // when
            String result = Strings.Languages.RUSSIAN.transliterate(
                    Strings.Languages.ENGLISH,
                    input,
                    Strings.Options.Include.WHITESPACE
            );

            // then
            assertEquals("privet mir", result);
        }

        @Test
        @DisplayName("Транслитерация с цифрами")
        void shouldTransliterateWithDigits() {
            // given
            String input = "тест123";

            // when
            String result = Strings.Languages.RUSSIAN.transliterate(
                    Strings.Languages.ENGLISH,
                    input,
                    Strings.Options.Include.DIGITS
            );

            // then
            assertEquals("test123", result);
        }

        @ParameterizedTest
        @MethodSource("russianToEnglishTransliterationData")
        @DisplayName("Параметризованный тест транслитерации русских символов")
        void shouldTransliterateRussianCharacters(String russian, String expected) {
            // when
            String result = Strings.Languages.RUSSIAN.transliterate(Strings.Languages.ENGLISH, russian, 0);

            // then
            assertEquals(expected, result);
        }

        private static Stream<Arguments> russianToEnglishTransliterationData() {
            return Stream.of(
                    Arguments.of("а", "a"),
                    Arguments.of("б", "b"),
                    Arguments.of("в", "v"),
                    Arguments.of("г", "g"),
                    Arguments.of("д", "d"),
                    Arguments.of("е", "e"),
                    Arguments.of("ё", "yo"),
                    Arguments.of("ж", "zh"),
                    Arguments.of("з", "z"),
                    Arguments.of("и", "i"),
                    Arguments.of("й", "y"),
                    Arguments.of("к", "k"),
                    Arguments.of("л", "l"),
                    Arguments.of("м", "m"),
                    Arguments.of("н", "n"),
                    Arguments.of("о", "o"),
                    Arguments.of("п", "p"),
                    Arguments.of("р", "r"),
                    Arguments.of("с", "s"),
                    Arguments.of("т", "t"),
                    Arguments.of("у", "u"),
                    Arguments.of("ф", "f"),
                    Arguments.of("х", "kh"),
                    Arguments.of("ц", "ts"),
                    Arguments.of("ч", "ch"),
                    Arguments.of("ш", "sh"),
                    Arguments.of("щ", "sch"),
                    Arguments.of("ъ", ""),
                    Arguments.of("ы", "y"),
                    Arguments.of("ь", ""),
                    Arguments.of("э", "e"),
                    Arguments.of("ю", "yu"),
                    Arguments.of("я", "ya"),
                    Arguments.of("компания", "kompaniya"),
                    Arguments.of("банк", "bank")
            );
        }

        @Test
        @DisplayName("Поиск языка по коду")
        void shouldFindLanguageByCode() {
            // when
            Strings.Languages russian = Strings.Languages.byLanguage("ru");
            Strings.Languages english = Strings.Languages.byLanguage("en");

            // then
            assertEquals(Strings.Languages.RUSSIAN, russian);
            assertEquals(Strings.Languages.ENGLISH, english);
        }

        @Test
        @DisplayName("Поиск языка по несуществующему коду")
        void shouldReturnNullForUnknownLanguage() {
            // when
            Strings.Languages unknown = Strings.Languages.byLanguage("unknown");

            // then
            assertNull(unknown);
        }

        @Test
        @DisplayName("Проверка поддерживаемого языка")
        void shouldCheckSupportedLanguage() {
            // when & then
            assertTrue(Strings.Languages.RUSSIAN.isSupportedLanguage("ru"));
            assertTrue(Strings.Languages.ENGLISH.isSupportedLanguage("en"));
            assertFalse(Strings.Languages.RUSSIAN.isSupportedLanguage("en"));
        }

        @Test
        @DisplayName("Проверка соответствия символа языку")
        void shouldMatchCharacterToLanguage() {
            // when & then
            assertTrue(Strings.Languages.RUSSIAN.match('а'));
            assertTrue(Strings.Languages.ENGLISH.match('a'));
            assertFalse(Strings.Languages.RUSSIAN.match('a'));
            assertFalse(Strings.Languages.ENGLISH.match('а'));
        }
    }

    @Nested
    @DisplayName("Transliterator - Транслитератор")
    class TransliteratorTests {

        @Test
        @DisplayName("Создание транслитератора с валидными языками")
        void shouldCreateTransliteratorWithValidLanguages() {
            // when
            Transliterator transliterator = new AssociationTransliterator(
                    Strings.Languages.RUSSIAN,
                    Strings.Languages.ENGLISH
            );

            // then
            assertNotNull(transliterator);
            assertEquals("ru", transliterator.getOriginalLanguage());
            assertEquals("en", transliterator.getExpectedLanguage());
        }

        @Test
        @DisplayName("BUG FIX: NPE при создании транслитератора с null originalLanguage")
        void shouldThrowExceptionWhenOriginalLanguageIsNull() {
            // when & then - этот тест покрывает ошибку из стектрейса
            assertThrows(IllegalArgumentException.class, () -> {
                new AssociationTransliterator(null, Strings.Languages.ENGLISH);
            });
        }

        @Test
        @DisplayName("BUG FIX: NPE при создании транслитератора с null expectedLanguage")
        void shouldThrowExceptionWhenExpectedLanguageIsNull() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> {
                new AssociationTransliterator(Strings.Languages.RUSSIAN, null);
            });
        }

        @Test
        @DisplayName("Получение транслитерации символа")
        void shouldGetTransliterationForCharacter() {
            // given
            Transliterator transliterator = AssociationTransliterator.EN_RU;

            // when
            String result = transliterator.get('а');

            // then
            assertEquals("a", result);
        }

        @Test
        @DisplayName("Получение некорректной ассоциации из справочника транслитерации")
        void shouldGetTransliterationForString() {
            // given
            Transliterator transliterator = AssociationTransliterator.EN_RU;

            // when
            String result = transliterator.get("привет");

            // then
            assertNull(result);
        }

        @Test
        @DisplayName("Проверка принадлежности символа исходному блоку")
        void shouldCheckIfCharacterInOriginalBlock() {
            // given
            Transliterator transliterator = AssociationTransliterator.EN_RU;

            // when & then
            assertTrue(transliterator.inOriginalBlock('а'));
            assertFalse(transliterator.inOriginalBlock('a'));
        }

        @Test
        @DisplayName("Проверка принадлежности символа ожидаемому блоку")
        void shouldCheckIfCharacterInExpectedBlock() {
            // given
            Transliterator transliterator = AssociationTransliterator.EN_RU;

            // when & then
            assertTrue(transliterator.inExpectedBlock('a'));
            assertFalse(transliterator.inExpectedBlock('а'));
        }

        @Test
        @DisplayName("Добавление ассоциации символов")
        void shouldAddCharacterAssociation() {
            // given
            AssociationTransliterator transliterator = new AssociationTransliterator(
                    Strings.Languages.RUSSIAN,
                    Strings.Languages.ENGLISH
            );

            // when
            AssociationTransliterator result = transliterator.associate("ы", "y");

            // then
            assertSame(transliterator, result); // should return same instance for chaining
        }
    }

    @Nested
    @DisplayName("Edge Cases - Граничные случаи")
    class EdgeCasesTests {

        @Test
        @DisplayName("Транслитерация null строки")
        void shouldHandleNullString() {
            // when
            String result = Strings.Languages.RUSSIAN.transliterate(Strings.Languages.ENGLISH, null, 0);

            // then
            Assertions.assertNull(result);
        }

        @Test
        @DisplayName("Транслитерация пустой строки")
        void shouldHandleEmptyString() {
            // given
            String empty = "";

            // when
            String result = Strings.Languages.RUSSIAN.transliterate(Strings.Languages.ENGLISH, empty, 0);

            // then
            assertEquals("", result);
        }

        @Test
        @DisplayName("Транслитерация строки только с пробелами")
        void shouldHandleWhitespaceOnlyString() {
            // given
            String whitespace = "   ";

            // when
            String result = Strings.Languages.RUSSIAN.transliterate(
                    Strings.Languages.ENGLISH,
                    whitespace,
                    Strings.Options.Include.WHITESPACE
            );

            // then
            assertEquals("   ", result);
        }

        @Test
        @DisplayName("Транслитерация смешанного текста (русский + английский)")
        void shouldHandleMixedLanguageText() {
            // given
            String mixed = "Hello привет";

            // when
            String result = Strings.Languages.RUSSIAN.transliterate(
                    Strings.Languages.ENGLISH,
                    mixed,
                    Strings.Options.Include.WHITESPACE | Strings.Options.Include.FOREIGN
            );

            // then
            assertTrue(result.contains("privet"));
        }

        @Test
        @DisplayName("BUG FIX: Симуляция ошибки из CreditRating.createCreditObjectKey - главный тест покрывающий баг")
        void shouldHandleCreditRatingTransliterationScenario() {
            // given - симуляция данных из реального использования
            String creditObjectName = "ПАО Сбербанк России";

            // when & then - этот тест покрывает ошибку из стектрейса в методе createCreditObjectKey
            assertDoesNotThrow(() -> {
                String result = Strings
                        .chain(Strings.Languages.RUSSIAN, Strings.TextCase.PLAIN_TEXT, creditObjectName)
                        .transliterate(Strings.Languages.ENGLISH, Strings.Options.Include.DIGITS | Strings.Options.Include.WHITESPACE)
                        .changeCase(Strings.TextCase.KEBAB_CASE)
                        .end();

                assertNotNull(result);
                assertFalse(result.isEmpty());
                assertEquals("pao-sberbank-rossii", result);
            });
        }
    }

    @Nested
    @DisplayName("TransliteratorConsumer - Потребитель транслитерации")
    class TransliteratorConsumerTests {

        @Test
        @DisplayName("StringBuilderTransliterator - добавление символа")
        void shouldAcceptCharacterInStringBuilderTransliterator() {
            // given
            Strings.TransliteratorConsumer.StringBuilderTransliterator consumer =
                    new Strings.TransliteratorConsumer.StringBuilderTransliterator();

            // when
            consumer.accept('a');

            // then
            assertEquals("a", consumer.toString());
        }

        @Test
        @DisplayName("StringBuilderTransliterator - добавление строки")
        void shouldAcceptStringInStringBuilderTransliterator() {
            // given
            Strings.TransliteratorConsumer.StringBuilderTransliterator consumer =
                    new Strings.TransliteratorConsumer.StringBuilderTransliterator();

            // when
            consumer.accept("test");

            // then
            assertEquals("test", consumer.toString());
        }
    }

    @Nested
    @DisplayName("Integration Tests - Интеграционные тесты")
    class IntegrationTests {

        @ParameterizedTest
        @ValueSource(strings = {
                "ООО Рога и копыта",
                "АО Газпром",
                "ПАО Лукойл",
                "Банк ВТБ",
                "Сбербанк России"
        })
        @DisplayName("Полная цепочка транслитерации для типичных названий компаний")
        void shouldTransliterateTypicalCompanyNames(String companyName) {
            // when & then
            assertDoesNotThrow(() -> {
                String result = Strings
                        .chain(Strings.Languages.RUSSIAN, Strings.TextCase.PLAIN_TEXT, companyName)
                        .transliterate(Strings.Languages.ENGLISH, Strings.Options.Include.DIGITS | Strings.Options.Include.WHITESPACE)
                        .changeCase(Strings.TextCase.KEBAB_CASE)
                        .end();

                assertNotNull(result);
                assertFalse(result.isEmpty());
                assertFalse(result.contains(" ")); // не должно содержать пробелов после KEBAB_CASE
                assertTrue(result.matches("^[a-z0-9-]+$")); // должно содержать только lowercase + цифры + дефисы
            });
        }
    }
}
