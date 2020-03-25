package ru.hse.cs.java2020.task02;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LRUCache implements EvictionPolicy {
    private Node<Long, String> lru;
    private Node<Long, String> mru;
    private Map<Long, Node<Long, String>> container;
    private HashMap<Long, Long> fileList; // pair key - file
    private int currentSize;
    private File folder;
    private String pathToDisc;
    private Long lastFileN;
    private Long maxCacheSize;
    private Long maxDiscSize;
    private Long cacheSize;
    private Long discSize;

    public Long getFreeFileN() {
        return 23L;
    }

    public void test () {

    }
    private static int countLines(String str){
        String[] lines = str.split("\r\n|\r|\n");
        return  lines.length;
    }

    public LRUCache() {
        this.maxCacheSize = 0L;
        this.currentSize = 0;
        lru = new Node<Long, String>(null, null, null, null);
        mru = lru;
        container = new HashMap<Long, Node<Long, String>>();
        System.out.println(countLines(""));
    }

    public void OpenFolder(String path) {
        pathToDisc = path;
        folder = new File(pathToDisc);
        for (File i : folder.listFiles()) {
            System.out.println(i);
        }
    }

    public void SetSizes(Long cacheSz, Long folderSz) {
        maxCacheSize = cacheSz;
        maxDiscSize = folderSz;
        cacheSize = 0L;
        discSize = 0L;
    }

    public String get(Long key) {
        Node<Long, String> tempNode = container.get(key);
        if (tempNode == null) {
            return null;
        }
        // If MRU leave the list as it is
        else if (tempNode.key == mru.key) {
            return mru.value;
        }

        // Get the next and prev nodes
        Node<Long, String> nextNode = tempNode.next;
        Node<Long, String> prevNode = tempNode.prev;

        // If at the left-most, we update LRU
        if (tempNode.key == lru.key) {
            nextNode.prev = null;
            lru = nextNode;
        } else if (tempNode.key != mru.key) {
            // If we are in the middle, we need to update the items before and after our
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
        }

        // Finally move our item to the MRU
        tempNode.prev = mru;
        mru.next = tempNode;
        mru = tempNode;
        mru.next = null;

        return tempNode.value;
    }

    public String put(Long key, String value) {
        String ret = null;
        if (container.containsKey(key)) {
            ret = container.get(key).value;
        }

        // Put the new node at the right-most end of the linked-list
        Node<Long, String> myNode = new Node<Long, String>(mru, null, key, value);

        // // Add size controller

        // Put in cache
        mru.next = myNode;
        container.put(key, myNode);
        mru = myNode;

        // Delete the left-most entry and update the LRU pointer
        if (currentSize == maxCacheSize) {
            container.remove(lru.key);
            lru = lru.next;
            lru.prev = null;
        }

        // Update container size, for the first added entry update the LRU pointer
        else if (currentSize < maxCacheSize) {
            if (currentSize == 0) {
                lru = myNode;
            }
            currentSize++;
        }

        // // Put on disk part

        return ret;
    }

    // Node for doubly linked list
    class Node<T, U> {
        T key;
        U value;
        Node<T, U> prev;
        Node<T, U> next;

        public Node(Node<T, U> prev, Node<T, U> next, T key, U value) {
            this.prev = prev;
            this.next = next;
            this.key = key;
            this.value = value;
        }
    }
}