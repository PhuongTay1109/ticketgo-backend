package com.ticketgo.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ObjectUtils {

    public static void copyProperties(Object source, Object target) {
        if (source == null || target == null) {
            throw new IllegalArgumentException("Source or target object is null");
        }

        Class<?> sourceClass = source.getClass();
        Class<?> targetClass = target.getClass();

        while (sourceClass != null) {
            Field[] sourceFields = sourceClass.getDeclaredFields();
            for (Field sourceField : sourceFields) {
                try {
                    sourceField.setAccessible(true);
                    Object sourceValue = sourceField.get(source);

                    if (sourceValue != null && hasField(targetClass, sourceField.getName())) {
                        Field targetField = targetClass.getDeclaredField(sourceField.getName());
                        targetField.setAccessible(true);

                        if (isAssignable(targetField.getType(), sourceField.getType())) {
                            targetField.set(target, sourceValue);
                        } else {
                            log.warn("Field '{}' types are not compatible: source={}, target={}",
                                    sourceField.getName(), sourceField.getType(), targetField.getType());
                        }
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    log.error("Failed to copy field '{}'", sourceField.getName(), e);
                }
            }
            sourceClass = sourceClass.getSuperclass();
        }
    }

    private static boolean hasField(Class<?> clazz, String fieldName) {
        try {
            clazz.getDeclaredField(fieldName);
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    private static boolean isAssignable(Class<?> targetType, Class<?> sourceType) {
        return targetType.isAssignableFrom(sourceType) ||
                (targetType.isPrimitive() && sourceType.equals(getPrimitiveWrapper(targetType))) ||
                (sourceType.isPrimitive() && targetType.equals(getPrimitiveWrapper(sourceType)));
    }

    private static Class<?> getPrimitiveWrapper(Class<?> primitiveType) {
        if (!primitiveType.isPrimitive()) return primitiveType;
        if (primitiveType == int.class) return Integer.class;
        if (primitiveType == long.class) return Long.class;
        if (primitiveType == double.class) return Double.class;
        if (primitiveType == float.class) return Float.class;
        if (primitiveType == boolean.class) return Boolean.class;
        if (primitiveType == char.class) return Character.class;
        if (primitiveType == byte.class) return Byte.class;
        if (primitiveType == short.class) return Short.class;
        return primitiveType;
    }
}
