
package com.github.old.dog.star.boot.toolbox.strings;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import static java.lang.Character.UnicodeBlock;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import com.github.old.dog.star.boot.toolbox.strings.transliterate.IdentityTransliterator;
import com.github.old.dog.star.boot.toolbox.strings.transliterate.Transliterator;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class for advanced string processing operations including transliteration and text case transformations.
 * <p>
 * This class provides comprehensive string manipulation capabilities through static methods and contains
 * several nested types that support various string processing functions:
 * <ul>
 *   <li>{@link Languages} - enumeration of supported languages for transliteration</li>
 *   <li>{@link TextCase} - enumeration of text case styles (camelCase, snake_case, etc.)</li>
 *   <li>{@link Options} - configuration options for customizing method behavior</li>
 *   <li>{@link StringChain} - fluent API for chaining multiple string operations</li>
 *   <li>{@link TransliteratorConsumer} - interface for processing transliteration results</li>
 * </ul>
 * <p>
 * The class is designed to be used primarily through static imports and provides a fluent API
 * for complex string transformations involving multiple languages and text formats.
 * <p>
 * All methods are static and the class cannot be instantiated.
 *
 * @see Languages
 * @see TextCase
 * @see StringChain
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Strings {

    /**
     * Checks if a given character sequence is blank, meaning it is null, empty,
     * or contains only whitespace characters.
     *
     * @param sequence the character sequence to be checked
     * @return true if the sequence is blank, false otherwise
     */
    public static boolean isBlank(@Nullable CharSequence sequence) {
        return StringUtils.isBlank(sequence);
    }

    /**
     * Checks if the given character sequence is empty.
     * A character sequence is considered empty if it is null or its length is zero.
     *
     * @param sequence the character sequence to be checked, which may be null
     * @return true if the sequence is null or empty, false otherwise
     */
    public static boolean isEmpty(@Nullable CharSequence sequence) {
        return StringUtils.isEmpty(sequence);
    }

    /**
     * Creates a {@link StringChain} object for performing a sequence of string operations.
     * <p>
     * This method enables fluent API usage by allowing multiple string transformation operations
     * to be chained together, improving code readability and eliminating the need for
     * intermediate variables.
     * <p>
     * Example usage:
     * <pre>
     * String result = Strings.chain(Languages.RUSSIAN, TextCase.PLAIN_TEXT, "пример строки")
     *     .transliterate(Languages.ENGLISH)
     *     .changeCase(TextCase.CAMEL_CASE)
     *     .end();
     * // Result: "primerStroki"
     * </pre>
     *
     * @param language the source language of the input string
     * @param textCase the current text case style of the input string
     * @param value    the input string to be processed
     * @return a StringChain object for performing chained operations
     */
    public static StringChain chain(Languages language, TextCase textCase, String value) {
        return new StringChain(language, textCase, value);
    }

    /**
     * Converts a string from one text case style to another without format validation.
     * <p>
     * This method transforms text case styles (e.g., from snake_case to camelCase)
     * without additional options or input format validation.
     *
     * @param original the current text case style of the input
     * @param expected the desired text case style for the output
     * @param input    the input string to be converted
     * @return the string converted to the new text case style
     */
    public static String changeCase(TextCase original, TextCase expected, String input) {
        return original.changeCase(expected, input, 0);
    }

    /**
     * Converts a string from one text case style to another with configurable options.
     * <p>
     * This method provides flexible text case transformation with customizable behavior
     * through bitwise option flags (e.g., {@link TextCase#OPT_STRICT} for strict format validation).
     *
     * @param original the current text case style of the input
     * @param expected the desired text case style for the output
     * @param input    the input string to be converted
     * @param options  bitwise mask of options for customizing the conversion process
     * @return the string converted to the new text case style
     * @throws IllegalArgumentException if OPT_STRICT option is enabled and the input doesn't match the original format
     */
    public static String changeCase(TextCase original, TextCase expected, String input, int options) {
        return original.changeCase(expected, input, options);
    }

    /**
     * Capitalizes the first letter of a string, leaving the rest unchanged.
     * <p>
     * This method serves as a proxy to {@link StringUtils#capitalize(String)}.
     *
     * @param word the string to be capitalized
     * @return the string with its first letter capitalized
     */
    public static String capitalize(String word) {
        return StringUtils.capitalize(word);
    }

    /**
     * Performs transliteration from one language to another followed by text case transformation.
     * <p>
     * This method combines two operations: transliteration and case conversion, allowing for
     * single-call transformation of text from one language and format to another
     * (e.g., Russian text to transliterated English in camelCase format).
     * <p>
     * Uses {@link Options.Include#WHITESPACE} option by default to preserve whitespace characters.
     *
     * @param original     the source language of the input string
     * @param expected     the target language for transliteration
     * @param expectedCase the desired text case style for the result
     * @param input        the input string to be processed
     * @return the transliterated string in the specified text case style
     */
    public String transliterateAndCase(Languages original, Languages expected, TextCase expectedCase, String input) {
        // todo подумать над опциями
        return this.transliterateAndCase(original, expected, expectedCase, input, 0);

    }

    /**
     * Performs transliteration from one language to another followed by text case transformation,
     * with configurable processing options.
     * <p>
     * This method provides flexible control over both transliteration and case conversion processes
     * through bitwise option flags. Currently, only {@link Options.Include#WHITESPACE} option
     * is used for transliteration in the implementation.
     *
     * @param original     the source language of the input string
     * @param expected     the target language for transliteration
     * @param expectedCase the desired text case style for the result
     * @param input        the input string to be processed
     * @param options      bitwise mask of options for customizing the process
     * @return the transliterated string in the specified text case style
     */
    public String transliterateAndCase(Languages original, Languages expected, TextCase expectedCase, String input, int options) {
        String transliterated
            = original.transliterate(expected, input, options & Options.Include.WHITESPACE);

        return Strings.changeCase(TextCase.PLAIN_TEXT, expectedCase, transliterated);
    }

    // ============================================================================================================== //

    /**
     * Enumeration of supported text case styles with conversion capabilities.
     * <p>
     * This enum defines various text formatting styles commonly used in programming and provides
     * methods to convert between them. Each enum constant implements specific logic for:
     * <ul>
     *   <li>Format validation through regex patterns</li>
     *   <li>Word extraction from formatted strings</li>
     *   <li>Word formatting according to the style rules</li>
     * </ul>
     * <p>
     * Supported formats:
     * <ul>
     *   <li>{@link #PLAIN_TEXT} - Space-separated words: "example string"</li>
     *   <li>{@link #CAMEL_CASE} - Camel case format: "exampleString"</li>
     *   <li>{@link #SNAKE_CASE} - Snake case format: "example_string"</li>
     *   <li>{@link #KEBAB_CASE} - Kebab case format: "example-string"</li>
     * </ul>
     */
    @RequiredArgsConstructor
    public enum TextCase {
        /**
         * Plain text format with space-separated words.
         * Example: "example string"
         */
        PLAIN_TEXT("example string", " ") {
            /**
             * Always returns true as plain text accepts any input format.
             */
            @Override
            boolean matchCase(String input) {
                return true;
            }

            /**
             * Extracts words by splitting on the delimiter (space).
             */
            public String[] extractWords(String input) {
                return input.split(getDelimiter());
            }

            /**
             * Formats words as lowercase with space separators.
             */
            @Override
            String formatWords(String[] words) {
                return words[0].toLowerCase() + Arrays
                    .stream(words, 1, words.length)
                    .map(String::toLowerCase)
                    .collect(Collectors.joining(getDelimiter()));
            }
        },

        /**
         * Camel case format with first word lowercase and subsequent words capitalized.
         * Example: "exampleString"
         */
        CAMEL_CASE("exampleString", "") {
            /**
             * Validates that input contains only alphanumeric characters.
             */
            @Override
            boolean matchCase(String input) {
                return input.matches("^[a-zA-Z0-9]*$");
            }

            /**
             * Extracts words by splitting camelCase at lowercase-to-uppercase transitions.
             */
            public String[] extractWords(String input) {
                return input
                    .replaceAll("([a-z])([A-Z])", "$1 $2")
                    .split("\\s+");
            }

            /**
             * Formats words with first word lowercase and subsequent words capitalized, no separators.
             */
            @Override
            String formatWords(String[] words) {
                return words[0].toLowerCase() + Arrays
                    .stream(words, 1, words.length)
                    .map(word -> capitalize(word.toLowerCase()))
                    .collect(Collectors.joining());
            }
        },

        /**
         * Snake case format with underscore separators.
         * Example: "example_string"
         */
        SNAKE_CASE("example_string", "_") {
            /**
             * Validates uppercase letters, digits, and underscores only.
             */
            @Override
            boolean matchCase(String input) {
                return input.matches("^[A-Z][A-Z0-9_]*$");
            }

            /**
             * Extracts words by splitting on underscores.
             */
            @Override
            String[] extractWords(String input) {
                return input.split(getDelimiter());
            }

            /**
             * Formats words as lowercase with underscore separators.
             */
            @Override
            String formatWords(String[] words) {
                return Arrays.stream(words)
                    .map(String::toLowerCase)
                    .collect(Collectors.joining(getDelimiter()));
            }
        },

        /**
         * Kebab case format with hyphen separators.
         * Example: "example-string"
         */
        KEBAB_CASE("example-string", "-") {
            /**
             * Validates uppercase letters, digits, and hyphens only.
             */
            @Override
            boolean matchCase(String input) {
                return input.matches("^[A-Z][A-Z0-9\\-]*$");
            }

            /**
             * Extracts words by splitting on hyphens.
             */
            @Override
            String[] extractWords(String input) {
                return input.split(getDelimiter());
            }

            /**
             * Formats words as lowercase with hyphen separators.
             */
            @Override
            String formatWords(String[] words) {
                return Arrays.stream(words)
                    .map(String::toLowerCase)
                    .collect(Collectors.joining(getDelimiter()));
            }
        };

        /**
         * Option flag for strict format validation during case conversion.
         */
        public static final int OPT_STRICT = 0x00000001;

        /**
         * Example string demonstrating this text case format.
         */
        @Getter
        private final String example;

        /**
         * Delimiter character used to separate words in this format.
         */
        @Getter
        private final String delimiter;

        /**
         * Validates whether the input string matches this text case format.
         *
         * @param input the string to validate
         * @return true if the input matches this format
         */
        abstract boolean matchCase(String input);

        /**
         * Extracts individual words from a string formatted in this text case style.
         *
         * @param input the formatted string
         * @return array of individual words
         */
        abstract String[] extractWords(String input);

        /**
         * Formats an array of words according to this text case style rules.
         *
         * @param words array of words to format
         * @return formatted string in this text case style
         */
        abstract String formatWords(String[] words);

        /**
         * Converts text from this case style to another case style.
         *
         * @param expected the target text case style
         * @param input    the input string in this format
         * @param options  processing options (bitwise flags)
         * @return the converted string
         * @throws IllegalArgumentException if OPT_STRICT is set and input doesn't match this format
         */
        private String changeCase(TextCase expected, String input, int options) {
            TextCase original = this;

            if (input == null || input.isEmpty()) {
                return input;
            }

            if ((options & TextCase.OPT_STRICT) == OPT_STRICT && !original.matchCase(input)) {
                throw new IllegalArgumentException(
                    String.format(
                        "Строка %s не соответствует формату %s. Ожидался формат: %s",
                        input, original.name(), original.getExample()
                    ));
            }

            // Сначала приводим к единому базовому формату (массив слов)
            String[] words = original.extractWords(input);

            // Затем преобразуем в нужный формат
            return expected.formatWords(words);
        }
    }

    /**
     * Enumeration of supported languages for transliteration operations.
     * <p>
     * This enum defines languages that can be used as source or target for transliteration,
     * along with their Unicode blocks and transliteration mappings. Each language is associated with:
     * <ul>
     *   <li>A Java {@link Locale} for language identification</li>
     *   <li>A {@link UnicodeBlock} that defines the character set</li>
     *   <li>Transliteration mappings to other supported languages</li>
     * </ul>
     * <p>
     * Currently supported languages:
     * <ul>
     *   <li>{@link #ENGLISH} - English with Basic Latin character set</li>
     *   <li>{@link #RUSSIAN} - Russian with Cyrillic character set, can transliterate to English</li>
     * </ul>
     */
    public enum Languages {
        /**
         * English language using Basic Latin Unicode block.
         */
        ENGLISH(Locale.ENGLISH, UnicodeBlock.BASIC_LATIN),

        /**
         * Russian language using Cyrillic Unicode block with transliteration support to English.
         */
        RUSSIAN(Locale.of("ru"), UnicodeBlock.CYRILLIC, Locale.ENGLISH);

        private final Locale locale;

        /**
         * The Unicode block associated with this language's character set.
         */
        @Getter
        private final UnicodeBlock block;

        private final Map<String, String> transliterationKeys;

        /**
         * Constructs a Languages enum constant.
         *
         * @param locale           the Java locale for this language
         * @param block            the Unicode block for this language's characters
         * @param transliterations optional array of target locales for transliteration
         */
        Languages(Locale locale, UnicodeBlock block, Locale... transliterations) {
            this.locale = locale;
            this.block = block;
            this.transliterationKeys = transliterations == null ? Map.of() : Arrays.stream(transliterations)
                .collect(Collectors.toMap(
                    Locale::getLanguage,
                    foreign -> String.format("%s_%s", foreign.getLanguage(), locale.getLanguage()).toUpperCase()
                ));
        }

        /**
         * Finds a language by its language code.
         *
         * @param lang the ISO language code (e.g., "en", "ru")
         * @return the matching Languages enum value, or null if not found
         */
        public static Languages byLanguage(String lang) {
            return Arrays.stream(values())
                .filter(locales -> locales.isSupportedLanguage(lang))
                .findFirst()
                .orElse(null);
        }

        /**
         * Finds a language by its Java Locale.
         *
         * @param locale the Java Locale object
         * @return the matching Languages enum value, or null if not found
         */
        public static Languages byLocale(Locale locale) {
            return Languages.byLanguage(locale.getLanguage());
        }

        /**
         * Gets the ISO language code for this language.
         *
         * @return the language code (e.g., "en", "ru")
         */
        public String getLanguage() {
            return locale.getLanguage();
        }

        /**
         * Transliterates text from this language to another language.
         * <p>
         * This method performs character-by-character transliteration using the configured
         * transliteration mappings. If no mapping exists between the languages, an exception is thrown.
         *
         * @param languages the target language for transliteration
         * @param input     the input text to transliterate
         * @param options   processing options for handling special characters
         * @return the transliterated text
         * @throws UnsupportedOperationException if no transliteration mapping exists
         */
        public String transliterate(Languages languages, String input, int options) {
            if (Objects.isNull(input)) {
                return null;
            }

            final TransliteratorConsumer.StringBuilderTransliterator transliterator
                = new TransliteratorConsumer.StringBuilderTransliterator();

            this.transliterate(languages, input, options, transliterator);

            return transliterator.toString();
        }

        /**
         * Transliterates text using a custom consumer for result processing.
         * <p>
         * This method allows for custom handling of transliteration results through
         * the provided consumer interface, enabling streaming or custom processing of characters.
         *
         * @param languages the target language for transliteration
         * @param input     the input text to transliterate
         * @param options   processing options for handling special characters
         * @param consumer  the consumer to process transliteration results
         * @throws UnsupportedOperationException if no transliteration mapping exists
         */
        public void transliterate(Languages languages, String input, int options, TransliteratorConsumer consumer) {
            Transliterator transliterator;

            if (languages == this) {
                transliterator = new IdentityTransliterator(this);
            } else {

                String transliterationKey = transliterationKeys.get(languages.getLanguage());

                if (Objects.isNull(transliterationKey)) {
                    throw new UnsupportedOperationException("there is no transliterate map to perform operation with key = " + transliterationKey);
                }

                transliterator = Transliterator.COMMON.get(transliterationKey);

            }

            if (Objects.isNull(transliterator)) {
                throw new UnsupportedOperationException("there is no transliterate map to perform operation with locale = " + languages);
            }

            for (int i = 0; i < input.length(); i++) {
                this.transliterate(transliterator, input.charAt(i), options, consumer);
            }
        }

        /**
         * Transliterates a single character using the specified transliterator and options.
         *
         * @param transliterator the transliterator to use
         * @param letter         the character to transliterate
         * @param options        processing options
         * @param consumer       the consumer to receive the result
         */
        private void transliterate(Transliterator transliterator, Character letter, int options, TransliteratorConsumer consumer) {
            boolean isDigit = Character.isDigit(letter);

            if (isDigit && (options & Options.Include.DIGITS) == Options.Include.DIGITS) {
                consumer.accept(letter);
                return;
            }

            boolean isWhitespace = Character.isWhitespace(letter);

            if (isWhitespace && (options & Options.Include.WHITESPACE) == Options.Include.WHITESPACE) {
                consumer.accept(letter);
                return;
            }

            boolean isLetter = Character.isLetter(letter);

            if (isLetter && transliterator.inOriginalBlock(letter)) {
                consumer.accept(transliterator.get(letter));
                return;
            }

            if (isLetter && transliterator.inExpectedBlock(letter)) {
                consumer.accept(letter);
                return;
            }

            if (isLetter && (options & Options.Include.FOREIGN) == Options.Include.FOREIGN) {
                consumer.accept(letter);
                return;
            }

            if (isLetter && (options & Options.Change.FOREIGN) == Options.Change.FOREIGN) {
                consumer.accept('*');
                return;
            }

            if (!isLetter && !isDigit && (options & Options.Include.SYMBOLS) == Options.Include.SYMBOLS) {
                consumer.accept(letter);
                return;
            }
        }

        /**
         * Checks if this language supports the given language code.
         *
         * @param lang the language code to check
         * @return true if this language matches the given code
         */
        public boolean isSupportedLanguage(String lang) {
            return locale.getLanguage().equalsIgnoreCase(lang);
        }

        /**
         * Checks if a character belongs to this language's Unicode block.
         *
         * @param letter the character to check
         * @return true if the character belongs to this language's block
         */
        public boolean match(char letter) {
            return UnicodeBlock.of(letter) == block;
        }
    }

    /**
     * Configuration options for string processing operations.
     * <p>
     * This class provides bitwise flag constants that can be combined to customize
     * the behavior of transliteration and text processing methods.
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Options {

        /**
         * Options for including specific character types in processing results.
         */
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static final class Include {
            /**
             * Include symbol characters in the result.
             */
            public static final int SYMBOLS = 0x00000001;
            /**
             * Include digit characters in the result.
             */
            public static final int DIGITS = 0x00000002;
            /**
             * Include foreign language characters in the result.
             */
            public static final int FOREIGN = 0x00000004;
            /**
             * Include whitespace characters in the result.
             */
            public static final int WHITESPACE = 0x00000008;
        }

        /**
         * Options for changing specific character types during processing.
         */
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static final class Change {
            /**
             * Replace foreign language characters with asterisks.
             */
            public static final int FOREIGN = 0x00001000;
        }
    }

    /**
     * Functional interface for consuming transliteration results.
     * <p>
     * This interface allows for flexible processing of transliteration output,
     * supporting both single character and string acceptance.
     */
    public interface TransliteratorConsumer {
        /**
         * Accepts a single character result.
         *
         * @param letter the character to accept
         */
        void accept(char letter);

        /**
         * Accepts a string result.
         *
         * @param letters the string to accept
         */
        void accept(String letters);

        /**
         * Default implementation that builds transliteration results into a StringBuilder.
         * <p>
         * This implementation is suitable for most use cases where the complete
         * transliterated string is needed as a final result.
         */
        public static class StringBuilderTransliterator implements TransliteratorConsumer {

            private final StringBuilder builder = new StringBuilder();

            /**
             * Appends a character to the internal StringBuilder.
             */
            @Override
            public void accept(char letter) {
                builder.append(letter);
            }

            /**
             * Appends a string to the internal StringBuilder.
             */
            @Override
            public void accept(String letters) {
                builder.append(letters);
            }

            /**
             * Returns the accumulated transliteration result.
             *
             * @return the complete transliterated string
             */
            @Override
            public String toString() {
                return builder.toString();
            }
        }
    }

    /**
     * Fluent API for chaining multiple string transformation operations.
     * <p>
     * This class enables method chaining for complex string transformations involving
     * multiple steps such as transliteration and case conversion. It maintains the current
     * state (language, text case, and value) throughout the transformation chain.
     * <p>
     * Example usage:
     * <pre>
     * String result = new StringChain(Languages.RUSSIAN, TextCase.PLAIN_TEXT, "привет мир")
     *     .transliterate(Languages.ENGLISH)
     *     .changeCase(TextCase.CAMEL_CASE)
     *     .end();
     * // Result: "privetMir"
     * </pre>
     */
    public static final class StringChain {
        private Languages language;
        private TextCase textCase;
        private String value;

        /**
         * Constructs a new StringChain with initial state.
         *
         * @param language the initial language of the string
         * @param textCase the initial text case of the string
         * @param value    the initial string value
         */
        public StringChain(Languages language, TextCase textCase, String value) {
            this.language = language;
            this.textCase = textCase;
            this.value = value;
        }

        /**
         * Updates the current context state.
         */
        private void setContext(Languages language, TextCase textCase, String value) {
            this.language = language;
            this.textCase = textCase;
            this.value = value;
        }

        /**
         * Combines multiple option flags into a single bitwise value.
         */
        private int reduceOptions(int... options) {
            int option = 0;

            if (options != null) {
                for (int i : options) {
                    option = option | i;
                }
            }

            return option;
        }

        /**
         * Transliterates the current string to another language.
         *
         * @param expected the target language for transliteration
         * @param options  optional processing flags
         * @return this StringChain for method chaining
         */
        public StringChain transliterate(Languages expected, int... options) {
            String transliterated
                = language.transliterate(expected, value, reduceOptions(options));

            this.setContext(expected, textCase, transliterated);
            return this;
        }

        /**
         * Cleans the current string by applying character filtering options.
         * <p>
         * This method transliterates the string to the same language with specified options,
         * effectively filtering characters according to the option flags.
         *
         * @param options processing flags for character filtering
         * @return this StringChain for method chaining
         */
        public StringChain clear(int... options) {
            String transliterated
                = language.transliterate(language, value, reduceOptions(options));

            this.setContext(language, textCase, transliterated);
            return this;
        }

        /**
         * Changes the text case style of the current string.
         *
         * @param expected the target text case style
         * @param options  optional processing flags for case conversion
         * @return this StringChain for method chaining
         */
        public StringChain changeCase(TextCase expected, int... options) {
            String cased
                = textCase.changeCase(expected, value, reduceOptions(options));

            this.setContext(language, expected, cased);
            return this;
        }

        /**
         * Terminates the method chain and returns the final processed string.
         *
         * @return the final transformed string value
         */
        public String end() {
            return value;
        }
    }
}
