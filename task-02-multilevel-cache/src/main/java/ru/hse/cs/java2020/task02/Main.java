package ru.hse.cs.java2020.task02;

import java.io.IOException;

public class Main {
    static Long TEST_VAL = 435L;
    private static EvictionPolicy epTVOUmatiBLAD = new LFUCache();
    private static EvictionPolicy  epALyaVrotTAKIEdz = new LRUCache();

    public static void main(String[] args) throws IOException {
        System.out.println("----------------");
        CacheImpl heheOTCHISLENIE = new CacheImpl(TEST_VAL, TEST_VAL, "E:\\trash\\", epTVOUmatiBLAD);
    }
}