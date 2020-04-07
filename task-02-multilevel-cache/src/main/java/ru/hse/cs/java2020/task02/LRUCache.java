package ru.hse.cs.java2020.task02;

import java.io.FileNotFoundException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LRUCache implements EvictionPolicy {
    private Node lru;
    private Node mru;
    private Map<Long, Node> container;
    private File folder;
    private Long lastFileN;
    private LinkedList<Long> filesWithOne; // pair key - file
    private Long cacheSizeMax;
    private Long discSizeMax;
    private Long cacheSize;
    private Long discSize;
    private final Long appandingSize = 4L;

    // Node for doubly linked list
    class Node {
        private Long fileN;
        private Long countLines;
        private Long key;
        private String text;
        private Node prev;
        private Node next;

        Node() {
            this.prev = null;
            this.next = null;
            this.countLines = null;
            this.fileN = null;
            this.key = null;
            this.text = null;
        }
    }

    public LRUCache() {
        lru = new Node();
        mru = lru;
        lastFileN = 0L;
        filesWithOne = new LinkedList<>();
        container = new HashMap<Long, Node>();
    }

    public void setSizes(Long cacheSz, Long folderSz) {
        cacheSizeMax = cacheSz;
        discSizeMax = folderSz;
        cacheSize = 0L;
        discSize = 0L;
    }

    // start here
    // Done work well (вроде)
    public void openFolder(String path) {
        // Открываем собсно папку
        folder = new File(path);

        // go throught all fiels
        for (File i : folder.listFiles()) {
            // Сюды читаем
            Node timeBlock = new Node();
            try (Scanner scanner = new Scanner(i)) {
                long counter = 0;
                String nums = scanner.nextLine();

                // Считываем до конца файла, null - не смогли считать
                while (nums != null) {
                    try {
                        String[] infoData = nums.split(" ");
                        timeBlock.text = "";
                        timeBlock.key = Long.parseLong(infoData[0]);
                        timeBlock.countLines = Long.parseLong(infoData[1]);
                        timeBlock.fileN = Long.parseLong(i.getName());
                        timeBlock.prev = mru;
                        lastFileN = timeBlock.fileN;
                        counter++;


                        for (int j = 0; j < timeBlock.countLines; j++) {
                            if (j != 0) {
                                timeBlock.text += "\n";
                            }
                            timeBlock.text += scanner.nextLine();
                        }

                        putFromFolder(timeBlock);

                        timeBlock = new Node();

                        nums = scanner.nextLine();
                    } catch (Exception e) {
                        nums = null;
                    }
                }
                if (counter == 1) {
                    filesWithOne.add(Long.valueOf(i.getName()));
                }
                counter = 0;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Done
    //block needs: fileN, key, text, countLines
    private void addStringToFile(Node block) throws IOException {
        File file = new File(folder.getPath() + "\\" + block.fileN);
        FileWriter fr = new FileWriter(file, true);

        fr.write(block.key + " " + block.countLines + "\n");
        fr.write(block.text + "\n");
        fr.close();
    }

    // block needs: fileN, key
    // Done
    private void removeStringFromFile(Node block) throws IOException {
        int listIndex = filesWithOne.indexOf(block.fileN);
        File inputFile = new File(folder.getPath() + "\\" + block.fileN);

        if (listIndex != -1) {
            filesWithOne.remove(listIndex);
            inputFile.delete();
            return;
        }
        File tempFile = new File(folder + "\\TempFile.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        filesWithOne.add(block.fileN);

        try (Scanner scanner = new Scanner(inputFile)) {
            String nums = scanner.nextLine();
            String[] infoData = nums.split(" ");

            if (Long.parseLong(infoData[0]) == block.key) {
                for (int i = 0; i < Long.parseLong(infoData[1]); i++) {
                    scanner.nextLine();
                }
                nums = scanner.nextLine();
                infoData = nums.split(" ");
                writer.write(nums + "\n");
            } else {
                writer.write(nums + "\n");
            }

            for (int j = 0; j < Long.parseLong(infoData[1]); j++) {
                if (j != 0) {
                    writer.write("\n");
                }
                writer.write(scanner.nextLine());
            }
        } catch (NoSuchElementException e) {
            writer.write("\n");
            writer.close();
            inputFile.delete();
            tempFile.renameTo(inputFile);
        }

        writer.write("\n");
        writer.close();
        inputFile.delete();
        tempFile.renameTo(inputFile);
    }

    //0 - we just insert
    //1 - we insert without a string
    //2 - we cant insert
    // In block we need: text, fileN, key
    // Done
    private int clearData(Node block) throws IOException {
        while (discSize + appandingSize + block.text.length() > discSizeMax) {
            if (mru.key == null) {
                return 2;
            }

            if (lru.text != null) {
                cacheSize -= lru.text.length();
                discSize -= lru.text.length();
            } else {
                discSize -= readStringFromFile(lru).length();
            }
            discSize -= appandingSize;
            cacheSize -= appandingSize;
            removeStringFromFile(lru);
            // Clear cach
            container.remove(lru.key);
            lru = lru.next;
            if (lru != null) {
                lru.prev = null;
            } else {
                mru = new Node();
                lru = mru;
            }
        }

        if (cacheSize + appandingSize + block.text.length() > cacheSizeMax) {
            // Delete strings
            for (Node currentNode = lru; (currentNode != mru || mru.text != null)
                    && cacheSize + appandingSize > cacheSizeMax;) {
                if (currentNode.text != null) {
                    cacheSize -= currentNode.text.length();
                    currentNode.text = null;
                }
                currentNode = currentNode.next;
            }

            // Delete files
            while (mru != null && cacheSize + appandingSize > cacheSizeMax) {
                if (lru.text != null) {
                    cacheSize -= lru.text.length();
                    discSize -= lru.text.length();
                } else {
                    discSize -= readStringFromFile(lru).length();
                }
                discSize -= appandingSize;
                cacheSize -= appandingSize;
                removeStringFromFile(lru);
                // Clear cach
                container.remove(lru.key);
                lru = lru.next;
                lru.prev = null;
            }


            if (cacheSize + appandingSize > cacheSizeMax) {
                return 2;
            }
            if (cacheSize + appandingSize + block.text.length() > cacheSizeMax) {
                return 1;
            }
        }
        return 0;
    }

    // Block needs: fileN, key
    private String readStringFromFile(Node block) {
        String text = "";

        try (Scanner scanner = new Scanner(new File(folder.getPath() + "\\" + block.fileN))) {
            String nums = scanner.nextLine();
            String[] infoData = nums.split(" ");

            if (Long.parseLong(infoData[0]) != block.key) {
                for (int i = 0; i < Long.parseLong(infoData[1]); i++) {
                    scanner.nextLine();
                }
                nums = scanner.nextLine();
                infoData = nums.split(" ");
            }

            for (int j = 0; j < Long.parseLong(infoData[1]); j++) {
                if (j != 0) {
                    text += "\n";
                }
                text += scanner.nextLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return text;
    }

    private static long countLines(String str) {
        Matcher m = Pattern.compile("\r\n|\r|\n").matcher(str);
        Long lines = 1L;
        while (m.find()) {
            lines++;
        }

        return (long) lines;
    }

    //Done, work
    private Long getFreeFileN() {
        if (filesWithOne.size() > 0) {
            return filesWithOne.poll();
        }
        lastFileN++;
        File f = new File(folder.getPath() + "\\" + lastFileN);
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        filesWithOne.add(lastFileN);
        return lastFileN;
    }

    // Done
    public String get(Long key) {
        Node tempNode = container.get(key);
        if (tempNode == null) {
            return null;
        } else if (tempNode.key == mru.key) {
            if (mru.text != null) {
                return mru.text;
            } else {
                return readStringFromFile(tempNode);
            }
        }

        // Get the next and prev nodes
        Node nextNode = tempNode.next;
        Node prevNode = tempNode.prev;

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

        if (tempNode.text != null) {
            return tempNode.text;
        } else {
            return readStringFromFile(tempNode);
        }
    }

    public String put(Long key, String value) throws IOException {
        String ret = null;
        Node myNode = new Node();
        myNode.prev = mru;
        myNode.next = null;
        myNode.key = key;
        myNode.text = value;
        myNode.countLines = countLines(myNode.text);

        // Prephere return
        if (container.containsKey(key)) {
            if (container.get(key).text != null) {
                ret = container.get(key).text;
                cacheSize -= ret.length();
            } else {
                ret = readStringFromFile(myNode);
            }

            discSize -= appandingSize;
            discSize -= ret.length();
            cacheSize -= appandingSize;
            removeStringFromFile(container.get(key));
            get(key);
            mru = mru.prev;
            mru.next = null;
            container.remove(key);
        }

        int todo = clearData(myNode);

        // // Add size controller

        if (todo == 2) {
            return null;
        }

        myNode.fileN = getFreeFileN();
        addStringToFile(myNode);

        if (todo == 1) {
            discSize += myNode.text.length();
            myNode.text = null;
        } else {
            discSize += myNode.text.length();
            cacheSize += myNode.text.length();
        }
        discSize += appandingSize;
        cacheSize += appandingSize;

        // Put the new node at the right-most end of the linked-list
        // Put in cache and disk
        mru.next = myNode;
        container.put(key, myNode);
        mru = myNode;

        // if it is first
        if (lru.key == null) {
            lru = mru;
        }
        return ret;
    }

    // In block: text, key, countLines, fileN
    public void putFromFolder(Node block) throws IOException {
        int todo = clearData(block);
        if (todo == 2) {
            return;
        } else if (todo == 1) {
            discSize += block.text.length();
            block.text = null;
        } else {
            discSize += block.text.length();
            cacheSize += block.text.length();
        }

        discSize += appandingSize;
        cacheSize += appandingSize;

        mru.next = block;
        container.put(block.key, block);
        mru = block;

        // if it is first
        if (lru.key == null) {
            lru = block;
        }
    }
}
