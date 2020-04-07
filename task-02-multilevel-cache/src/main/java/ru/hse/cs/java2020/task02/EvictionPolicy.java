package ru.hse.cs.java2020.task02;

import java.io.IOException;

public interface EvictionPolicy {
    String get(Long key);
    String put(Long key, String value) throws IOException;
    void openFolder(String path);
    void setSizes(Long cacheSz, Long folderSz);
}
