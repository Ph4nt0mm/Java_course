package ru.hse.cs.java2020.task02;

import java.io.IOException;

public interface EvictionPolicy {
    public String get(Long key);
    public String put(Long key, String value) throws IOException;
    public void OpenFolder(String path);
    public void SetSizes(Long cacheSz, Long folderSz);
    public void test () throws IOException;
}
