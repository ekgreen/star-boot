package com.github.old.dog.star.boot.model;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Enumeration representing all basic Java data types including their array representations.
 * <p>
 * This enum provides comprehensive information about Java primitive types, their wrapper classes,
 * array types, and common reference types. It includes methods for type checking, conversion,
 * and validation operations.
 * <p>
 * Each enum constant contains information about:
 * <ul>
 *   <li>Type name as string representation</li>
 *   <li>Array type name as string representation</li>
 *   <li>Corresponding Java Class objects</li>
 *   <li>Default values for primitive types</li>
 *   <li>Size information where applicable</li>
 * </ul>
 */
public enum DataType {

    // @checkstyle:off: Читаемый вариант большой таблицы
    // @formatter:off

    // ┌────────────────┬─────────────────────────────────┬─────────────────────────────────────┬─────────────────────┬─────────────────────┬─────────────────┬──────────┐
    // │    CONSTANT    │           TYPE_NAME             │            ARRAY_TYPE_NAME          │     TYPE_CLASS      │     ARRAY_CLASS     │  DEFAULT_VALUE  │   SIZE   │
    // └────────────────┴─────────────────────────────────┴─────────────────────────────────────┴─────────────────────┴─────────────────────┴─────────────────┴──────────┘
    // Primitive types
    BYTE                (DataType.BYTE_TYPE,                DataType.BYTE_ARRAY_TYPE,               byte.class,         byte[].class            , (byte) 0      , 1),
    SHORT               (DataType.SHORT_TYPE,               DataType.SHORT_ARRAY_TYPE,              short.class,        short[].class           , (short) 0     , 2),
    INT                 (DataType.INT_TYPE,                 DataType.INT_ARRAY_TYPE,                int.class,          int[].class             , 0             , 4),
    LONG                (DataType.LONG_TYPE,                DataType.LONG_ARRAY_TYPE,               long.class,         long[].class            , 0L            , 8),
    FLOAT               (DataType.FLOAT_TYPE,               DataType.FLOAT_ARRAY_TYPE,              float.class,        float[].class           , 0.0f          , 4),
    DOUBLE              (DataType.DOUBLE_TYPE,              DataType.DOUBLE_ARRAY_TYPE,             double.class,       double[].class          , 0.0d          , 8),
    BOOLEAN             (DataType.BOOLEAN_TYPE,             DataType.BOOLEAN_ARRAY_TYPE,            boolean.class,      boolean[].class         , false         , 1),
    CHAR                (DataType.CHAR_TYPE,                DataType.CHAR_ARRAY_TYPE,               char.class,         char[].class            , '\u0000'      , 2),

    // Wrapper types
    BYTE_WRAPPER        (DataType.BYTE_WRAPPER_TYPE,        DataType.BYTE_WRAPPER_ARRAY_TYPE,       Byte.class,         Byte[].class            , null          , 1),
    SHORT_WRAPPER       (DataType.SHORT_WRAPPER_TYPE,       DataType.SHORT_WRAPPER_ARRAY_TYPE,      Short.class,        Short[].class           , null          , 2),
    INTEGER_WRAPPER     (DataType.INTEGER_WRAPPER_TYPE,     DataType.INTEGER_WRAPPER_ARRAY_TYPE,    Integer.class,      Integer[].class         , null          , 4),
    LONG_WRAPPER        (DataType.LONG_WRAPPER_TYPE,        DataType.LONG_WRAPPER_ARRAY_TYPE,       Long.class,         Long[].class            , null          , 8),
    FLOAT_WRAPPER       (DataType.FLOAT_WRAPPER_TYPE,       DataType.FLOAT_WRAPPER_ARRAY_TYPE,      Float.class,        Float[].class           , null          , 4),
    DOUBLE_WRAPPER      (DataType.DOUBLE_WRAPPER_TYPE,      DataType.DOUBLE_WRAPPER_ARRAY_TYPE,     Double.class,       Double[].class          , null          , 8),
    BOOLEAN_WRAPPER     (DataType.BOOLEAN_WRAPPER_TYPE,     DataType.BOOLEAN_WRAPPER_ARRAY_TYPE,    Boolean.class,      Boolean[].class         , null          , 1),
    CHARACTER_WRAPPER   (DataType.CHARACTER_WRAPPER_TYPE,   DataType.CHARACTER_WRAPPER_ARRAY_TYPE,  Character.class,    Character[].class       , null          , 2),

    // Common reference types
    STRING              (DataType.STRING_TYPE,              DataType.STRING_ARRAY_TYPE,             String.class,       String[].class          , null          , -1),
    OBJECT              (DataType.OBJECT_TYPE,              DataType.OBJECT_ARRAY_TYPE,             Object.class,       Object[].class          , null          , -1),

    // Special types
    VOID                (DataType.VOID_TYPE,                DataType.VOID_ARRAY_TYPE,               void.class,         Void[].class            , null          , 0),
    VOID_WRAPPER        (DataType.VOID_WRAPPER_TYPE,        DataType.VOID_WRAPPER_ARRAY_TYPE,       Void.class,         Void[].class            , null          , 0);
    // @formatter:on
    // @checkstyle:on

    // String constants for primitive types
    public static final String BYTE_TYPE = "byte";
    public static final String BYTE_ARRAY_TYPE = "byte[]";
    public static final String SHORT_TYPE = "short";
    public static final String SHORT_ARRAY_TYPE = "short[]";
    public static final String INT_TYPE = "int";
    public static final String INT_ARRAY_TYPE = "int[]";
    public static final String LONG_TYPE = "long";
    public static final String LONG_ARRAY_TYPE = "long[]";
    public static final String FLOAT_TYPE = "float";
    public static final String FLOAT_ARRAY_TYPE = "float[]";
    public static final String DOUBLE_TYPE = "double";
    public static final String DOUBLE_ARRAY_TYPE = "double[]";
    public static final String BOOLEAN_TYPE = "boolean";
    public static final String BOOLEAN_ARRAY_TYPE = "boolean[]";
    public static final String CHAR_TYPE = "char";
    public static final String CHAR_ARRAY_TYPE = "char[]";

    // String constants for wrapper types
    public static final String BYTE_WRAPPER_TYPE = "java.lang.Byte";
    public static final String BYTE_WRAPPER_ARRAY_TYPE = "java.lang.Byte[]";
    public static final String SHORT_WRAPPER_TYPE = "java.lang.Short";
    public static final String SHORT_WRAPPER_ARRAY_TYPE = "java.lang.Short[]";
    public static final String INTEGER_WRAPPER_TYPE = "java.lang.Integer";
    public static final String INTEGER_WRAPPER_ARRAY_TYPE = "java.lang.Integer[]";
    public static final String LONG_WRAPPER_TYPE = "java.lang.Long";
    public static final String LONG_WRAPPER_ARRAY_TYPE = "java.lang.Long[]";
    public static final String FLOAT_WRAPPER_TYPE = "java.lang.Float";
    public static final String FLOAT_WRAPPER_ARRAY_TYPE = "java.lang.Float[]";
    public static final String DOUBLE_WRAPPER_TYPE = "java.lang.Double";
    public static final String DOUBLE_WRAPPER_ARRAY_TYPE = "java.lang.Double[]";
    public static final String BOOLEAN_WRAPPER_TYPE = "java.lang.Boolean";
    public static final String BOOLEAN_WRAPPER_ARRAY_TYPE = "java.lang.Boolean[]";
    public static final String CHARACTER_WRAPPER_TYPE = "java.lang.Character";
    public static final String CHARACTER_WRAPPER_ARRAY_TYPE = "java.lang.Character[]";

    // String constants for common reference types
    public static final String STRING_TYPE = "java.lang.String";
    public static final String STRING_ARRAY_TYPE = "java.lang.String[]";
    public static final String OBJECT_TYPE = "java.lang.Object";
    public static final String OBJECT_ARRAY_TYPE = "java.lang.Object[]";

    // String constants for special types
    public static final String VOID_TYPE = "void";
    public static final String VOID_ARRAY_TYPE = "void[]";
    public static final String VOID_WRAPPER_TYPE = "java.lang.Void";
    public static final String VOID_WRAPPER_ARRAY_TYPE = "java.lang.Void[]";

    // Static lookup maps for efficient type resolution
    private static final Map<String, DataType> TYPE_NAME_MAP = Arrays.stream(values())
        .collect(Collectors.toMap(DataType::getTypeName, Function.identity()));

    private static final Map<String, DataType> ARRAY_TYPE_NAME_MAP = Arrays.stream(values())
        .collect(Collectors.toMap(DataType::getArrayTypeName, Function.identity()));

    private static final Map<Class<?>, DataType> TYPE_CLASS_MAP = Arrays.stream(values())
        .collect(Collectors.toMap(DataType::getTypeClass, Function.identity()));

    private static final Map<Class<?>, DataType> ARRAY_CLASS_MAP = Arrays.stream(values())
        .filter(dt -> dt.getArrayClass() != null)
        .collect(Collectors.toMap(DataType::getArrayClass, Function.identity()));

    private final String typeName;
    private final String arrayTypeName;
    private final Class<?> typeClass;
    private final Class<?> arrayClass;
    private final Object defaultValue;
    private final int sizeInBytes;

    /**
     * Constructor for DataType enum constants.
     *
     * @param typeName      the string representation of the type
     * @param arrayTypeName the string representation of the array type
     * @param typeClass     the Class object representing the type
     * @param arrayClass    the Class object representing the array type
     * @param defaultValue  the default value for primitive types (null for reference types)
     * @param sizeInBytes   the size of the type in bytes (-1 for reference types with variable size)
     */
    DataType(String typeName, String arrayTypeName, Class<?> typeClass, Class<?> arrayClass,
             Object defaultValue, int sizeInBytes) {
        this.typeName = typeName;
        this.arrayTypeName = arrayTypeName;
        this.typeClass = typeClass;
        this.arrayClass = arrayClass;
        this.defaultValue = defaultValue;
        this.sizeInBytes = sizeInBytes;
    }

    /**
     * Returns the string representation of the type.
     *
     * @return the type name as string
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * Returns the string representation of the array type.
     *
     * @return the array type name as string
     */
    public String getArrayTypeName() {
        return arrayTypeName;
    }

    /**
     * Returns the Class object representing the type.
     *
     * @return the Class object for this type
     */
    public Class<?> getTypeClass() {
        return typeClass;
    }

    /**
     * Returns the Class object representing the array type.
     *
     * @return the Class object for the array type
     */
    public Class<?> getArrayClass() {
        return arrayClass;
    }

    /**
     * Returns the default value for primitive types.
     * For reference types, returns null.
     *
     * @return the default value for this type
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * Returns the size of the type in bytes.
     * Returns -1 for reference types with variable size.
     *
     * @return the size in bytes, or -1 for variable-size types
     */
    public int getSizeInBytes() {
        return sizeInBytes;
    }

    /**
     * Checks if this type is a primitive type.
     *
     * @return true if this is a primitive type, false otherwise
     */
    public boolean isPrimitive() {
        return typeClass.isPrimitive() && !typeClass.equals(void.class);
    }

    /**
     * Checks if this type is a wrapper type for a primitive.
     *
     * @return true if this is a wrapper type, false otherwise
     */
    public boolean isWrapper() {
        return !typeClass.isPrimitive() && getPrimitiveType().isPresent();
    }

    /**
     * Checks if this type is a reference type (not primitive).
     *
     * @return true if this is a reference type, false otherwise
     */
    public boolean isReference() {
        return !typeClass.isPrimitive();
    }

    /**
     * Checks if this type represents an array.
     *
     * @return true if this represents an array type, false otherwise
     */
    public boolean isArray() {
        return arrayClass.isArray();
    }

    /**
     * Checks if this type is a numeric type (byte, short, int, long, float, double and their wrappers).
     *
     * @return true if this is a numeric type, false otherwise
     */
    public boolean isNumeric() {
        // noinspection PointlessBooleanExpression
        return false
               || this == BYTE || this == SHORT || this == INT || this == LONG
               || this == FLOAT || this == DOUBLE
               || this == BYTE_WRAPPER || this == SHORT_WRAPPER || this == INTEGER_WRAPPER
               || this == LONG_WRAPPER || this == FLOAT_WRAPPER || this == DOUBLE_WRAPPER;
    }

    /**
     * Checks if this type is an integral type (byte, short, int, long and their wrappers).
     *
     * @return true if this is an integral type, false otherwise
     */
    public boolean isIntegral() {
        return this == BYTE || this == SHORT || this == INT || this == LONG
               || this == BYTE_WRAPPER || this == SHORT_WRAPPER || this == INTEGER_WRAPPER || this == LONG_WRAPPER;
    }

    /**
     * Checks if this type is a floating-point type (float, double and their wrappers).
     *
     * @return true if this is a floating-point type, false otherwise
     */
    public boolean isFloatingPoint() {
        return this == FLOAT || this == DOUBLE || this == FLOAT_WRAPPER || this == DOUBLE_WRAPPER;
    }

    /**
     * Returns the corresponding primitive type for wrapper types.
     *
     * @return Optional containing the primitive DataType, or empty if not applicable
     */
    public Optional<DataType> getPrimitiveType() {
        return switch (this) {
            case BYTE_WRAPPER -> Optional.of(BYTE);
            case SHORT_WRAPPER -> Optional.of(SHORT);
            case INTEGER_WRAPPER -> Optional.of(INT);
            case LONG_WRAPPER -> Optional.of(LONG);
            case FLOAT_WRAPPER -> Optional.of(FLOAT);
            case DOUBLE_WRAPPER -> Optional.of(DOUBLE);
            case BOOLEAN_WRAPPER -> Optional.of(BOOLEAN);
            case CHARACTER_WRAPPER -> Optional.of(CHAR);
            case VOID_WRAPPER -> Optional.of(VOID);
            default -> Optional.empty();
        };
    }

    /**
     * Returns the corresponding wrapper type for primitive types.
     *
     * @return Optional containing the wrapper DataType, or empty if not applicable
     */
    public Optional<DataType> getWrapperType() {
        return switch (this) {
            case BYTE -> Optional.of(BYTE_WRAPPER);
            case SHORT -> Optional.of(SHORT_WRAPPER);
            case INT -> Optional.of(INTEGER_WRAPPER);
            case LONG -> Optional.of(LONG_WRAPPER);
            case FLOAT -> Optional.of(FLOAT_WRAPPER);
            case DOUBLE -> Optional.of(DOUBLE_WRAPPER);
            case BOOLEAN -> Optional.of(BOOLEAN_WRAPPER);
            case CHAR -> Optional.of(CHARACTER_WRAPPER);
            case VOID -> Optional.of(VOID_WRAPPER);
            default -> Optional.empty();
        };
    }

    /**
     * Creates an array instance of the specified length for this type.
     *
     * @param length the length of the array to create
     * @return a new array instance of this type
     * @throws IllegalArgumentException if length is negative
     */
    public Object createArray(int length) {
        if (length < 0) {
            throw new IllegalArgumentException("Array length cannot be negative: " + length);
        }
        return Array.newInstance(typeClass, length);
    }

    /**
     * Checks if the provided value can be assigned to this type.
     *
     * @param value the value to check
     * @return true if the value is assignable to this type, false otherwise
     */
    public boolean isAssignableFrom(Object value) {
        if (value == null) {
            return !isPrimitive();
        }
        return typeClass.isAssignableFrom(value.getClass());
    }

    /**
     * Finds a DataType by its type name.
     *
     * @param typeName the name of the type to find
     * @return Optional containing the DataType, or empty if not found
     */
    public static Optional<DataType> findByTypeName(String typeName) {
        return Optional.ofNullable(TYPE_NAME_MAP.get(typeName));
    }

    /**
     * Finds a DataType by its array type name.
     *
     * @param arrayTypeName the name of the array type to find
     * @return Optional containing the DataType, or empty if not found
     */
    public static Optional<DataType> findByArrayTypeName(String arrayTypeName) {
        return Optional.ofNullable(ARRAY_TYPE_NAME_MAP.get(arrayTypeName));
    }

    /**
     * Finds a DataType by its Class object.
     *
     * @param typeClass the Class object to find
     * @return Optional containing the DataType, or empty if not found
     */
    public static Optional<DataType> findByClass(Class<?> typeClass) {
        return Optional.ofNullable(TYPE_CLASS_MAP.get(typeClass));
    }

    /**
     * Finds a DataType by its array Class object.
     *
     * @param arrayClass the array Class object to find
     * @return Optional containing the DataType, or empty if not found
     */
    public static Optional<DataType> findByArrayClass(Class<?> arrayClass) {
        return Optional.ofNullable(ARRAY_CLASS_MAP.get(arrayClass));
    }

    /**
     * Returns all primitive DataTypes.
     *
     * @return array of primitive DataTypes
     */
    public static DataType[] getPrimitiveTypes() {
        return Arrays.stream(values())
            .filter(DataType::isPrimitive)
            .toArray(DataType[]::new);
    }

    /**
     * Returns all wrapper DataTypes.
     *
     * @return array of wrapper DataTypes
     */
    public static DataType[] getWrapperTypes() {
        return Arrays.stream(values())
            .filter(DataType::isWrapper)
            .toArray(DataType[]::new);
    }

    /**
     * Returns all numeric DataTypes (both primitive and wrapper).
     *
     * @return array of numeric DataTypes
     */
    public static DataType[] getNumericTypes() {
        return Arrays.stream(values())
            .filter(DataType::isNumeric)
            .toArray(DataType[]::new);
    }
}
