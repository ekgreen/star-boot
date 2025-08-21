package com.github.old.dog.star.boot.toolbox.limits.switchboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Supplier;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit тесты для класса FileSystemFallback с фокусом на корректность записи
 * и чтения списков CreditRating в файловую систему.
 */
class FileSystemFallbackTest {

    @TempDir
    Path tempDir;

    private String testFileName;
    private Map<String, Supplier<String>> testMetadata;
    private Duration testCacheDuration;

    @BeforeEach
    void setUp() {
        testFileName = tempDir.resolve("test-ratings").toString();
        testMetadata = Map.of(
            "source", () -> "test-source",
            "version", () -> "1.0.0",
            "environment", () -> "test"
        );
        testCacheDuration = Duration.ofMinutes(10);
    }

    /**
     * Утилитный метод для сериализации списка CreditRating в byte[].
     */
    private byte[] serializeCreditRatings(List<CreditRating> ratings) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(ratings);
            return baos.toByteArray();
        }
    }

    /**
     * Утилитный метод для десериализации byte[] обратно в список CreditRating.
     */
    @SuppressWarnings("unchecked")
    private List<CreditRating> deserializeCreditRatings(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return (List<CreditRating>) ois.readObject();
        }
    }

    /**
     * Создает тестовый список CreditRating.
     */
    private List<CreditRating> createTestRatingsList() {
        return List.of(
            new CreditRatingPojo("RU000A0JX0J2", "Сбербанк Облигация БО-001P",
                "ПАО Сбербанк", "Банки", "ruAAA", "Final",
                "Stable", "ACRA"),
            new CreditRatingPojo("RU000A0ZYYM9", "ВЭБ.РФ Облигация 001P-02",
                "ВЭБ.РФ", "Развитие", "ruAAA", "Final",
                "Stable", "ACRA"),
            new CreditRatingPojo("RU000A103X66", "Лукойл Облигация БО-20",
                "ПАО ЛУКОЙЛ", "Нефтегаз", "ruAA+", "Final",
                "Stable", "ACRA")
        );
    }

    @Test
    @DisplayName("Должен корректно сохранять и загружать список CreditRating")
    void shouldCorrectlySaveAndLoadCreditRatingsList() throws IOException, ClassNotFoundException {
        // Arrange
        List<CreditRating> originalRatings = createTestRatingsList();
        byte[] serializedData = serializeCreditRatings(originalRatings);

        FileSystemFallback fallback = new FileSystemFallback(testFileName, testMetadata, testCacheDuration);

        // Act - сохраняем данные
        Switchboard.Condition condition = fallback.accept(serializedData);

        // Act - загружаем данные
        byte[] loadedData = fallback.get();
        List<CreditRating> deserializedRatings = deserializeCreditRatings(loadedData);

        // Assert
        assertThat(condition)
            .isNotNull();
        assertThat(loadedData)
            .isNotNull()
            .isEqualTo(serializedData);

        assertThat(deserializedRatings).isNotNull()
            .hasSize(3)
            .containsExactlyElementsOf(originalRatings);

        // Проверяем что файлы созданы
        assertThat(Files.exists(Path.of(testFileName + ".db"))).isTrue();
        assertThat(Files.exists(Path.of(testFileName + ".ini"))).isTrue();
    }

    @Test
    @DisplayName("Должен корректно читать .ini файл с метаданными")
    void shouldCorrectlyReadIniFileWithMetadata() throws IOException {
        // Arrange
        List<CreditRating> ratings = createTestRatingsList();
        byte[] serializedData = serializeCreditRatings(ratings);

        FileSystemFallback fallback = new FileSystemFallback(testFileName, testMetadata, testCacheDuration);
        fallback.accept(serializedData);

        // Act - читаем .ini файл
        Path iniPath = Path.of(testFileName + ".ini");
        String iniContent = Files.readString(iniPath);

        // Assert
        assertThat(iniContent)
            .contains("[metadata]")
            .contains("source = test-source")
            .contains("version = 1.0.0")
            .contains("environment = test")
            .contains("[system]") // Проверяем что в коде есть ошибка - второй раз [metadata] вместо [system]
            .contains("created_at = ")
            .contains("file_name = " + testFileName)
            .contains("data_class = byte[]")
            .contains("java_version = ")
            .contains("os_name = ");
    }

    @Test
    @DisplayName("Должен корректно загружать данные из существующих файлов при создании нового экземпляра")
    void shouldCorrectlyLoadDataFromExistingFilesOnNewInstance() throws IOException, ClassNotFoundException {
        // Arrange - создаем первый экземпляр и сохраняем данные
        List<CreditRating> originalRatings = createTestRatingsList();
        byte[] serializedData = serializeCreditRatings(originalRatings);

        FileSystemFallback firstFallback = new FileSystemFallback(testFileName, testMetadata, testCacheDuration);
        firstFallback.accept(serializedData);

        // Act - создаем новый экземпляр, который должен загрузить существующие файлы
        FileSystemFallback secondFallback = new FileSystemFallback(testFileName, testMetadata, testCacheDuration);
        byte[] loadedData = secondFallback.get();

        // Assert
        assertThat(loadedData).isNotNull()
            .isEqualTo(serializedData);

        List<CreditRating> deserializedRatings = deserializeCreditRatings(loadedData);
        assertThat(deserializedRatings).containsExactlyElementsOf(originalRatings);
    }

    @Test
    @DisplayName("Должен возвращать null при отсутствии файлов")
    void shouldReturnNullWhenFilesDoNotExist() {
        // Arrange & Act
        FileSystemFallback fallback = new FileSystemFallback(testFileName, testMetadata, testCacheDuration);
        byte[] result = fallback.get();

        // Assert
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Должен возвращать null при пустом .db файле")
    void shouldReturnNullWhenDbFileIsEmpty() throws IOException {
        // Arrange - создаем пустой .db файл и корректный .ini файл
        Path dbPath = Path.of(testFileName + ".db");
        Path iniPath = Path.of(testFileName + ".ini");

        Files.createFile(dbPath); // пустой файл
        Files.writeString(iniPath, "[metadata]\nsource = test\n[system]\ncreated_at = 2024-01-01T12:00:00\n");

        // Act
        FileSystemFallback fallback = new FileSystemFallback(testFileName, testMetadata, testCacheDuration);
        byte[] result = fallback.get();

        // Assert
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Должен обновлять данные при повторном вызове accept")
    void shouldUpdateDataOnSubsequentAcceptCalls() throws IOException, ClassNotFoundException {
        // Arrange
        List<CreditRating> firstRatings = createTestRatingsList();
        List<CreditRating> secondRatings = List.of(
            new CreditRatingPojo("RU000A104EC0", "Газпром Облигация БО-15",
                "ПАО Газпром", "Нефтегаз", "ruAA", "Final",
                "Positive", "ACRA")
        );

        byte[] firstData = serializeCreditRatings(firstRatings);
        byte[] secondData = serializeCreditRatings(secondRatings);

        FileSystemFallback fallback = new FileSystemFallback(testFileName, testMetadata, testCacheDuration);

        // Act - сохраняем первые данные
        fallback.accept(firstData);
        byte[] loadedFirstData = fallback.get();

        // Act - сохраняем вторые данные
        fallback.accept(secondData);
        byte[] loadedSecondData = fallback.get();

        // Assert
        List<CreditRating> firstDeserialized = deserializeCreditRatings(loadedFirstData);
        List<CreditRating> secondDeserialized = deserializeCreditRatings(loadedSecondData);

        assertThat(firstDeserialized).containsExactlyElementsOf(firstRatings);
        assertThat(secondDeserialized).containsExactlyElementsOf(secondRatings)
            .doesNotContainAnyElementsOf(firstRatings);
    }

    @Test
    @DisplayName("Должен быть потокобезопасным при concurrent операциях")
    void shouldBeThreadSafeForConcurrentOperations() throws InterruptedException, IOException, ClassNotFoundException {
        // Arrange
        FileSystemFallback fallback = new FileSystemFallback(testFileName, testMetadata, testCacheDuration);
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(10);
        ConcurrentLinkedQueue<Exception> exceptions = new ConcurrentLinkedQueue<>();

        List<CreditRating> testRatings = createTestRatingsList();
        byte[] testData = serializeCreditRatings(testRatings);

        try {
            // Act - параллельные операции записи и чтения
            for (int i = 0; i < 10; i++) {
                final int threadId = i;
                executor.submit(() -> {
                    try {
                        if (threadId % 2 == 0) {
                            // четные потоки записывают
                            fallback.accept(testData);
                        } else {
                            // нечетные потоки читают
                            fallback.get();
                        }
                    } catch (Exception e) {
                        exceptions.add(e);
                    } finally {
                        latch.countDown();
                    }
                });
            }

            // Assert
            assertThat(latch.await(30, TimeUnit.SECONDS)).isTrue();
            assertThat(exceptions).isEmpty();

            // Проверяем финальное состояние
            byte[] finalData = fallback.get();
            assertThat(finalData).isNotNull();

            List<CreditRating> finalRatings = deserializeCreditRatings(finalData);
            assertThat(finalRatings).containsExactlyElementsOf(testRatings);

        } finally {
            executor.shutdown();
        }
    }

    @Test
    @DisplayName("Должен корректно создавать TimeBasedYield условие")
    void shouldCreateCorrectTimeBasedYieldCondition() throws IOException {
        // Arrange
        List<CreditRating> ratings = createTestRatingsList();
        byte[] testData = serializeCreditRatings(ratings);

        Duration shortDuration = Duration.ofMillis(100);
        FileSystemFallback fallback = new FileSystemFallback(testFileName, testMetadata, shortDuration);

        // Act
        Switchboard.Condition condition = fallback.accept(testData);

        // Assert
        assertThat(condition).isInstanceOf(TimeBasedYield.class);

        // Сразу после создания условие должно возвращать false
        assertThat(condition.yield()).isTrue();

        // После истечения времени условие должно возвращать true
        try {
            Thread.sleep(150); // больше чем shortDuration
            assertThat(condition.yield()).isFalse();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Тест прерван");
        }
    }

    @Test
    @DisplayName("Должен корректно обрабатывать пустой список CreditRating")
    void shouldHandleEmptyRatingsList() throws IOException, ClassNotFoundException {
        // Arrange
        List<CreditRating> emptyRatings = List.of();
        byte[] serializedData = serializeCreditRatings(emptyRatings);

        FileSystemFallback fallback = new FileSystemFallback(testFileName, testMetadata, testCacheDuration);

        // Act
        fallback.accept(serializedData);
        byte[] loadedData = fallback.get();
        List<CreditRating> deserializedRatings = deserializeCreditRatings(loadedData);

        // Assert
        assertThat(deserializedRatings).isNotNull()
            .isEmpty();
    }

    @Test
    @DisplayName("Должен корректно обрабатывать динамические метаданные")
    void shouldHandleDynamicMetadata() throws IOException {
        // Arrange
        final LocalDateTime testTime = LocalDateTime.now();
        Map<String, Supplier<String>> dynamicMetadata = Map.of(
            "timestamp", () -> testTime.toString(),
            "counter", new Supplier<String>() {
                private int count = 0;

                @Override
                public String get() {
                    return String.valueOf(++count);
                }
            }
        );

        List<CreditRating> ratings = createTestRatingsList();
        byte[] testData = serializeCreditRatings(ratings);

        FileSystemFallback fallback = new FileSystemFallback(testFileName, dynamicMetadata, testCacheDuration);

        // Act
        fallback.accept(testData);

        // Assert
        Path iniPath = Path.of(testFileName + ".ini");
        String iniContent = Files.readString(iniPath);

        assertThat(iniContent)
            .contains("timestamp = " + testTime.toString())
            .contains("counter = 1");
    }
}
