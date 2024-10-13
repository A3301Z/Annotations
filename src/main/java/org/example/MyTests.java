package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.annotations.AfterSuite;
import org.example.annotations.BeforeSuite;
import org.example.annotations.CsvSource;
import org.example.annotations.Test;

@Slf4j
public class MyTests {

    @BeforeSuite
    public static void beforeAll() {
        log.info("#Before all tests");
    }

    @AfterSuite
    public static void afterAll() {
        log.info("#After all tests");
    }

    @Test(priority = 1)
    public void highPriorityTest() {
        log.info("#High priority test executed");
    }

    @Test(priority = 5)
    public void mediumPriorityTest() {
        log.info("#Medium priority test executed");
    }

    @Test(priority = 10)
    public void lowPriorityTest() {
        log.info("#Low priority test executed");
    }

    @CsvSource("10, Java, 20, true")
    @Test(priority = 7)
    public void csvTest(int a, String b, int c, boolean d) {
        log.info(String.format("#CSV Test executed with values: %d, %s, %d, %b", a, b, c, d));
    }
}