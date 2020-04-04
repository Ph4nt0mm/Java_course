package ru.hse.cs.java2020.task02;

import java.io.IOException;

public class Main {
    static Long TEST_VAL = 25L;
    static Long TEST_BIG_VAL = 25L;
    private static EvictionPolicy epTVOUmatiBLAD = new LFUCache();
    private static EvictionPolicy  epALyaVrotTAKIEdz = new LRUCache();

    public static void main(String[] args) throws IOException {
        System.out.println("----------------");
        CacheImpl heheOTCHISLENIE = new CacheImpl(TEST_VAL,
                TEST_BIG_VAL, "E:\\trash\\", epTVOUmatiBLAD);
//        System.out.println(heheOTCHISLENIE.get(1L));
//        System.out.println(heheOTCHISLENIE.put(1L, "abc\n"));
        System.out.println(heheOTCHISLENIE.put(5L, "aaaaaaaa\n"));
        System.out.println(heheOTCHISLENIE.put(6L, "\nmmmm"));
        System.out.println(heheOTCHISLENIE.put(7L, "qqq4q"));
        System.out.println(heheOTCHISLENIE.put(8L, "\n\nl\n\n"));
        System.out.println(heheOTCHISLENIE.get(5L));
        System.out.println(heheOTCHISLENIE.get(6L));
        System.out.println(heheOTCHISLENIE.get(7L));
        System.out.println(heheOTCHISLENIE.get(8L));

    }
}