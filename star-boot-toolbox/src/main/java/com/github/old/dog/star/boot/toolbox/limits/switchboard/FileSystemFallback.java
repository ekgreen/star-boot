package com.github.old.dog.star.boot.toolbox.limits.switchboard;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import com.github.old.dog.star.boot.toolbox.strings.Strings;

/**
 * Thread-safe implementation of a file system-based fallback data source for Switchboard.
 *
 * <p>This class provides a fallback mechanism that stores data in the file system
 * and retrieves it when needed. Data is saved in two files:</p>
 * <ul>
 *   <li><strong>{fileName}.ini</strong> - contains metadata and system information in INI format</li>
 *   <li><strong>{fileName}.db</strong> - contains the binary representation of the data</li>
 * </ul>
 *
 * <p><strong>Key features:</strong></p>
 * <ul>
 *   <li>Data is loaded from files only once at the first call to {@link #get()}</li>
 *   <li>After loading, the snapshot is cached in memory</li>
 *   <li>With each call to {@link #accept(byte[])}, files are completely rewritten</li>
 *   <li>Support for dynamic metadata through {@link Supplier}</li>
 *   <li>Thread safety is ensured through ReadWriteLock</li>
 * </ul>
 *
 * @author AI Assistant
 * @see Switchboard.Fallback
 * @since 1.0.0
 */
@Slf4j
public class FileSystemFallback implements Switchboard.FallbackWithInit<byte[]> {

    /**
     * Базовое имя файла (без расширения).
     */
    private final String fileName;

    /**
     * Метаданные с поддержкой динамических значений.
     */
    private final Map<String, Supplier<String>> metadata;

    /**
     * Длительность кэширования (время до следующего обновления).
     */
    private final Duration cacheValidityDuration;

    /**
     * ReadWriteLock для потокобезопасного доступа к данным.
     */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Кэшированный snapshot.
     */
    private volatile FallbackSnapshot<byte[]> snapshot;

    /**
     * Флаг инициализации.
     */
    private volatile boolean initialized = false;


    /**
     * Creates a new file system fallback with the specified parameters.
     *
     * @param fileName              the base name for the files (without extension)
     * @param metadata              the metadata with support for dynamic values
     * @param cacheValidityDuration the duration for which cached data is considered valid
     */
    public FileSystemFallback(String fileName, Map<String, Supplier<String>> metadata, Duration cacheValidityDuration) {
        this.fileName = fileName;
        this.metadata = metadata;
        this.cacheValidityDuration = cacheValidityDuration;

        initializeFromFiles();
    }

    /**
     * Initializes the fallback and returns a condition based on the loaded snapshot.
     * <p>
     * This method checks if the snapshot exists and is still valid based on its
     * creation time and the cache validity duration. If valid, it returns a
     * TimeBasedYield with the appropriate expiration time; otherwise, it returns
     * a condition that yields false.
     *
     * @return a condition based on the snapshot's validity
     */
    public Switchboard.Condition init() {
        lock.readLock().lock();

        try {
            Switchboard.Condition condition;

            if (snapshot == null || snapshot.createdAt == null || LocalDateTime.now().isAfter(snapshot.createdAt.plus(cacheValidityDuration))) {
                condition = () -> false;
            } else {
                condition = new TimeBasedYield(snapshot.createdAt.plus(cacheValidityDuration));
            }

            this.initialized = true;

            return condition;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Получает данные из кэша или загружает их из файлов при первом вызове.
     *
     * <p>Если данные еще не были загружены, метод пытается прочитать оба файла
     * {fileName}.ini и {fileName}.db. Если хотя бы один из файлов отсутствует,
     * возвращается null и требуется новая загрузка данных.</p>
     *
     * @return кэшированные данные или null, если файлы не найдены
     * @throws RuntimeException если произошла ошибка при чтении файлов
     */
    @Override
    public byte[] get() {
        lock.readLock().lock();
        try {
            return snapshot != null ? snapshot.getData() : null;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Принимает новые данные, обновляет snapshot и файлы, возвращает условие переключения.
     *
     * <p>Этот метод выполняет следующие действия:</p>
     * <ol>
     *   <li>Создает новый {@link FallbackSnapshot} с актуальными метаданными</li>
     *   <li>Записывает данные в файлы .ini и .db используя snapshot</li>
     *   <li>Обновляет кэшированный snapshot</li>
     *   <li>Возвращает TimeBasedYield с временем следующего обновления</li>
     * </ol>
     *
     * @param data новые данные для сохранения
     * @return TimeBasedYield с временем следующего обновления
     * @throws RuntimeException если произошла ошибка при записи файлов
     */
    @Override
    public Switchboard.Condition accept(byte[] data) {
        lock.writeLock().lock();
        try {
            FallbackSnapshot<byte[]> newSnapshot = createFallbackSnapshot(data);
            writeFiles(newSnapshot);
            this.snapshot = newSnapshot;

            // Создаем TimeBasedYield с временем следующего обновления
            LocalDateTime nextUpdateTime = newSnapshot.getCreatedAt().plus(cacheValidityDuration);
            return new TimeBasedYield(nextUpdateTime);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Инициализирует snapshot данными из файловой системы.
     *
     * <p>Метод пытается загрузить данные из обоих файлов {fileName}.ini и {fileName}.db.
     * Если хотя бы один из файлов отсутствует, snapshot остается равным null.</p>
     */
    private void initializeFromFiles() {
        try {
            Path dbPath = Paths.get(fileName + ".db");
            Path iniPath = Paths.get(fileName + ".ini");

            // Проверяем существование обоих файлов
            if (!Files.exists(dbPath) || !Files.exists(iniPath)) {
                log.debug("Один или оба файла не найдены: {}.db={}, {}.ini={}",
                    fileName, Files.exists(dbPath), fileName, Files.exists(iniPath));
                snapshot = null;
                return;
            }

            // Загружаем данные из .db файла
            byte[] dbData = Files.readAllBytes(dbPath);
            if (dbData.length == 0) {
                log.debug("Файл {}.db пуст", fileName);
                snapshot = null;
                return;
            }

            // Загружаем метаданные из .ini файла

            this.snapshot = loadSnapshotFromIniFile(iniPath, dbData);

            log.debug("Загружен snapshot из файлов: {}.ini и {}.db", fileName, fileName);

        } catch (Exception e) {
            log.error("Ошибка при инициализации из файлов: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось инициализировать данные из файлов", e);
        }
    }

    /**
     * Загружает snapshot из .ini файла, используя данные из .db файла.
     *
     * @param iniPath путь к .ini файлу
     * @param dbData  данные из .db файла
     * @return восстановленный snapshot
     * @throws IOException если произошла ошибка чтения файла
     */
    private FallbackSnapshot<byte[]> loadSnapshotFromIniFile(Path iniPath, byte[] dbData) throws IOException {
        FallbackSnapshot.FallbackSnapshotBuilder<byte[]> builder = FallbackSnapshot.<byte[]>builder()
            .fileName(fileName)
            .data(dbData);

        List<String> lines = Files.readAllLines(iniPath);

        Predicate<String> isNewBlock = line -> line.startsWith("[") && line.endsWith("]");
        BiFunction<Integer, Map<String, String>, Integer> blockReader = blockReader(iniPath, lines, isNewBlock);

        for (int i = 0; i < lines.size(); ) {
            String line = lines.get(i);

            if (isNewBlock.test(line)) {

                switch (line) {
                    case "[metadata]" -> {
                        Map<String, String> metadataBlock = new HashMap<>();
                        i = blockReader.apply(i, metadataBlock);

                        builder.metadata(metadataBlock);

                        continue;
                    }
                    case "[system]" -> {
                        Map<String, String> system = new HashMap<>();
                        i = blockReader.apply(i, system);

                        builder.createdAt(LocalDateTime.parse(system.get("created_at"), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                        builder.javaVersion(system.get("java_version"));
                        builder.osName(system.get("os_name"));
                        builder.dataClassName(system.get("data_class"));

                        continue;
                    }
                    default -> log.warn("unknow .ini file tag = " + line);
                }
            }

            i = i + 1;
        }

        return builder.build();
    }

    private  BiFunction<Integer, Map<String, String>, Integer> blockReader(Path iniPath, List<String> lines, Predicate<String> isNewBlock) {
        return (index, store) -> {
            int i = index + 1;
            for (; i < lines.size(); i++) {
                String line = lines.get(i);

                if (isNewBlock.test(line)) {
                    break;
                }

                if (Strings.isEmpty(line)) {
                    continue;
                }

                String[] split = line.split(" = ", 2);

                if (split.length != 2) {
                    log.warn(
                        "не корректный формат .ini файла [{}] на строке ({}) = {}",
                        iniPath, i, line
                    );
                }

                store.put(split[0], split[1]);
            }

            return i;
        };
    }

    /**
     * Записывает данные в оба файла (.ini и .db) используя snapshot.
     *
     * @param snapshot снимок данных для записи
     * @throws RuntimeException если произошла ошибка при записи
     */
    private void writeFiles(FallbackSnapshot<byte[]> snapshot) {
        try {
            writeDbFile(snapshot.getData());
            writeIniFile(snapshot);

            log.debug("Файлы {}.ini и {}.db успешно обновлены", fileName, fileName);
        } catch (Exception e) {
            log.error("Ошибка при записи файлов: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось записать файлы", e);
        }
    }

    /**
     * Записывает бинарные данные в .db файл.
     *
     * @param binaryData данные для записи
     * @throws IOException если произошла ошибка ввода-вывода
     */
    private void writeDbFile(byte[] binaryData) throws IOException {
        Path dbPath = Paths.get(fileName + ".db");
        this.writeFile(dbPath, binaryData);
    }

    /**
     * Записывает метаданные и системную информацию в .ini файл используя snapshot.
     *
     * <p>Файл содержит пользовательские метаданные и системную информацию с префиксом "system."</p>
     *
     * @param snapshot снимок данных с метаданными и системной информацией
     * @throws IOException если произошла ошибка ввода-вывода
     */
    private void writeIniFile(FallbackSnapshot<byte[]> snapshot) throws IOException {
        Path iniPath = Paths.get(fileName + ".ini");

        // Добавляем системную информацию
        StringBuilder ini = new StringBuilder();

        ini
            .append("[metadata]")
            .append(System.lineSeparator());

        for (Map.Entry<String, String> entry : snapshot.getMetadata().entrySet()) {
            ini
                .append(entry.getKey()).append(" = ").append(entry.getValue())
                .append(System.lineSeparator());
        }

        ini
            .append(System.lineSeparator());

        ini
            .append("[system]")
            .append(System.lineSeparator());

        Map<String, String> system = Map.of(
            "created_at", snapshot.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            "file_name", snapshot.getFileName(),
            "data_class", snapshot.getDataClassName(),
            "java_version", snapshot.getJavaVersion(),
            "os_name", snapshot.getOsName()
        );

        for (Map.Entry<String, String> entry : system.entrySet()) {
            ini
                .append(entry.getKey()).append(" = ").append(entry.getValue())
                .append(System.lineSeparator());
        }

        this.writeFile(iniPath, ini.toString().getBytes(StandardCharsets.UTF_8));
    }

    private void writeFile(Path filePath, byte[] data) throws IOException {
        Path parentDir = filePath.getParent();

        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }

        Files.write(filePath, data,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING);

    }

    /**
     * Создает снимок состояния fallback с актуальными метаданными.
     *
     * @param data данные для включения в snapshot
     * @return новый объект FallbackSnapshot с resolved метаданными
     */
    private FallbackSnapshot<byte[]> createFallbackSnapshot(byte[] data) {
        Map<String, String> resolvedMetadata = new HashMap<>();
        for (Map.Entry<String, Supplier<String>> entry : metadata.entrySet()) {
            String value = entry.getValue().get();
            resolvedMetadata.put(entry.getKey(), value != null ? value : "");
        }

        return new FallbackSnapshot<>(
            fileName,
            resolvedMetadata,
            data,
            LocalDateTime.now(),
            System.getProperty("java.version"),
            System.getProperty("os.name"),
            data != null ? data.getClass().getSimpleName() : "null"
        );
    }

    /**
     * Immutable snapshot of the fallback state with metadata and system information.
     * <p>
     * This class encapsulates all the information needed to restore the fallback
     * state, including the actual data, metadata, and various system properties
     * recorded at creation time.
     *
     * @param <T> the type of data stored in the snapshot
     */
    @Getter
    @Builder
    public static class FallbackSnapshot<T> {
        /**
         * The base file name associated with this snapshot.
         */
        private final String fileName;

        /**
         * The metadata associated with this snapshot, as an unmodifiable map.
         */
        private final Map<String, String> metadata;

        /**
         * The actual data stored in this snapshot.
         */
        private final T data;

        /**
         * The time when this snapshot was created.
         */
        private final LocalDateTime createdAt;

        /**
         * The Java version used when this snapshot was created.
         */
        private final String javaVersion;

        /**
         * The operating system name used when this snapshot was created.
         */
        private final String osName;

        /**
         * The class name of the data stored in this snapshot.
         */
        private final String dataClassName;

    }

}
