package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.annotations.AfterSuite;
import org.example.annotations.BeforeSuite;
import org.example.annotations.CsvSource;
import org.example.annotations.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
public class TestRunner {

    public static void runTests(Class<?> testClass) {
        Method beforeSuiteMethod = null;
        Method afterSuiteMethod = null;
        List<Method> testMethods = new ArrayList<>();


        // Сканирование методов класса
        for (Method method : testClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(BeforeSuite.class)) {
                if (beforeSuiteMethod != null) {
                    throw new RuntimeException("Допустим только один @BeforeSuite метод");
                }
                beforeSuiteMethod = method;
            } else if (method.isAnnotationPresent(AfterSuite.class)) {
                if (afterSuiteMethod != null) {
                    throw new RuntimeException("Допустим только один @AfterSuite метод");
                }
                afterSuiteMethod = method;
            } else if (method.isAnnotationPresent(Test.class)) {
                testMethods.add(method);
            }
        }

        try {
            // Выполнение метода @BeforeSuite
            if (beforeSuiteMethod != null) {
                beforeSuiteMethod.invoke(null);
            }

            // Сортировка методов с аннотацией @Test по приоритету
            testMethods.sort(Comparator.comparingInt(m -> m.getAnnotation(Test.class).priority()));

            // Выполнение тестов
            for (Method testMethod : testMethods) {
                // Выполнение теста
                if (testMethod.isAnnotationPresent(CsvSource.class)) {
                    executeCsvTest(testClass, testMethod);
                } else {
                    testMethod.invoke(testClass.getDeclaredConstructor().newInstance());
                }
            }

            // Выполнение метода @AfterSuite
            if (afterSuiteMethod != null) {
                afterSuiteMethod.invoke(null);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    // Метод для выполнения тестов с аннотацией @CsvSource
    private static void executeCsvTest(Class<?> testClass, Method testMethod) throws Exception {
        CsvSource csvSource = testMethod.getAnnotation(CsvSource.class);
        String[] params = csvSource.value().split(",\\s*");

        // Преобразование строковых данных к типам параметров метода
        Object[] parsedParams = new Object[params.length];
        Class<?>[] parameterTypes = testMethod.getParameterTypes();

        for (int i = 0; i < params.length; i++) {
            String param = params[i];
            Class<?> paramType = parameterTypes[i];
            parsedParams[i] = parseParam(param, paramType);
        }

        // Выполнение теста
        testMethod.invoke(testClass.getDeclaredConstructor().newInstance(), parsedParams);
    }

    // Преобразование строк к нужным типам данных
    private static Object parseParam(String param, Class<?> paramType) {
        if (paramType == int.class) {
            return Integer.parseInt(param);
        } else if (paramType == boolean.class) {
            return Boolean.parseBoolean(param);
        } else if (paramType == String.class) {
            return param;
        }
        throw new IllegalArgumentException("Неизвестный тип параметра: " + paramType);
    }
}