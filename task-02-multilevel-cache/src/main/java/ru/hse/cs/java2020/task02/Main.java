package ru.hse.cs.java2020.task02;

import java.io.IOException;

public class Main {
    static final Long TEST_VAL = 250L;
    static final Long TEST_BIG_VAL = 320L;
    private static EvictionPolicy lFUintExample = new LFUCache();
    private static EvictionPolicy  lRUintExample = new LRUCache();

    public static void main(String[] args) throws IOException {
        System.out.println("----------------");

        CacheImpl cacheObj = new CacheImpl(TEST_VAL,
                TEST_BIG_VAL, "E:\\trash\\", lFUintExample);
// forpull
//        System.out.println(cacheObj.put(7L, "aaaa"));
//        System.out.println(cacheObj.put(8L, "bbbb"));
//        System.out.println(cacheObj.put(9L, "cccc"));
//        System.out.println(cacheObj.put(81L, "dddd"));
//        System.out.println(cacheObj.put(72L, "aaaa\nbbbb\ncccc\ndddd\neeee\nffff\n"));
//        System.out.println(cacheObj.put(78L, "uktdrfukjtyfu"));
//        System.out.println(cacheObj.put(2L, "ijhjhqqq4q"));
//        System.out.println(cacheObj.put(31L, "\n\nl\n\n"));
//        System.out.println(cacheObj.put(4L, "anlknlaa\n"));
//        System.out.println(cacheObj.put(5L, "\nmmmmllllll"));
//        System.out.println(cacheObj.put(81L, "33333"));
//        System.out.println(cacheObj.put(1L, ""));
//        System.out.println(cacheObj.get(7L));
//        System.out.println(cacheObj.get(8L));
//        System.out.println(cacheObj.get(9L));
//        System.out.println(cacheObj.get(81L));
//        System.out.println(cacheObj.get(72L));
//        System.out.println(cacheObj.get(78L));
//        System.out.println(cacheObj.get(2L));
//        System.out.println(cacheObj.get(31L));
//        System.out.println(cacheObj.get(4L));
//        System.out.println(cacheObj.get(5L));
//        System.out.println(cacheObj.get(81L));
//        System.out.println(cacheObj.get(1L));
//        System.out.println(cacheObj.put(7L, "aaaa"));
//        System.out.println(cacheObj.put(8L, "bbbb"));
//        System.out.println(cacheObj.put(9L, "cccc"));
//        System.out.println(cacheObj.put(81L, "dddd"));
//        System.out.println(cacheObj.put(72L, "aaaa\nbbbb\ncccc\ndddd\neeee\nffff\n"));
//        System.out.println(cacheObj.put(78L, "uktdrfukjtyfu"));
//        System.out.println(cacheObj.put(2L, "ijhjhqqq4q"));
//        System.out.println(cacheObj.put(31L, "\n\nl\n\n"));
//        System.out.println(cacheObj.put(4L, "anlknlaa\n"));
//        System.out.println(cacheObj.put(5L, "\nmmmmllllll"));
//        System.out.println(cacheObj.put(81L, "33333"));
//        System.out.println(cacheObj.put(1L, ""));

    }
}
