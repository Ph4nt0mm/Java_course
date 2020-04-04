package ru.hse.cs.java2020.task02;

import java.io.IOException;

public class CacheImpl {
        private  EvictionPolicy policy;
        CacheImpl(long memorySize, long diskSize,
                  String path, EvictionPolicy pol) throws IOException {
                policy = pol;
                policy.SetSizes(memorySize, diskSize);
                policy.OpenFolder(path);
        }
        public String get(Long k) {
                return policy.get(k);
        }
        public String put(Long k, String s) throws IOException {
                return policy.put(k, s);
        }
}
