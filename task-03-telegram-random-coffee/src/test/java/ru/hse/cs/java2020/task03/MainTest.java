package ru.hse.cs.java2020.task03;

import org.junit.Test;
import org.junit.jupiter.api.Tag;

import static org.junit.Assert.assertTrue;

public class MainTest {
    @Test
    @Tag("test")
    public void testTrueIsTrue() {
        System.out.println("this part is working");
        assertTrue(true);
    }

    @Test
    @Tag("tracker")
    public void getFullTask() {}
//
//    @Test
//    @Tag("tracker")
//    public void getTask() {}
//
//    @Test
//    @Tag("tracker")
//    public void getFullTask() {}
//
//    @Test
//    @Tag("tracker")
//    public void getFullTask() {}
//    @Test
//    @Tag("tracker")
//    public void getFullTask() {}
}
