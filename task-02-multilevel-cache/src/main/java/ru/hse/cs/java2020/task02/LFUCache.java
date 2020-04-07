package ru.hse.cs.java2020.task02;

import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class LFUCache implements EvictionPolicy {
    private HashMap<Long, DataBlock> cacheMap; //cache key - value
    private HashMap<Long, Long> countCalls; // key - counter
    private HashMap<Long, LinkedHashSet<Long>> lists; // Count calls - item list
    private LinkedList<Long> filesWithOne; // N of files
    private ArrayList<Long> arryaOfCountCalls; // pair key - file
    private Long lastFileN;
    private File folder;
    private Long cacheSizeMax;
    private Long discSizeMax;
    private Long cacheSize;
    private Long discSize;
    private final Long appandingSize = 4L;

    static class DataBlock {
        private Long fileN;
        private Long key;
        private Long countLines;
        private String text;
        DataBlock() {
        }
    }

    public LFUCache() {
        cacheMap = new HashMap<>();
        countCalls = new HashMap<>();
        lists = new HashMap<>();
        lists.put(1L, new LinkedHashSet<>());
        filesWithOne = new LinkedList<>();
        arryaOfCountCalls = new ArrayList<>();
        lastFileN = 0L;
        cacheSize = 0L;
        discSize = 0L;
    }

    //Done work ochev
    public void setSizes(Long cacheSz, Long folderSz) {
        cacheSizeMax = cacheSz;
        discSizeMax = folderSz;
        cacheSize = 0L;
        discSize = 0L;
    }

    //Done work well (вроде)
    public void openFolder(String path) {
        // Открываем собсно папку
        folder = new File(path);

        // дем по всем файлам в нем
        for (File i : folder.listFiles()) {
            // Сюды читаем
            DataBlock timeBlock = new DataBlock();
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
                        lastFileN = timeBlock.fileN;
                        counter++;


                        for (int j = 0; j < timeBlock.countLines; j++) {
                            if (j != 0) {
                                timeBlock.text += "\n";
                            }
                            timeBlock.text += scanner.nextLine();
                        }

                        putFromFolder(timeBlock);

                        timeBlock = new DataBlock();

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

    //Done work well
    private void addStringToFile(DataBlock b) throws IOException, IOException {
        File file = new File(folder.getPath() + "\\" + b.fileN);
        FileWriter fr = new FileWriter(file, true);

        fr.write(b.key + " " + b.countLines + "\n");
        fr.write(b.text + "\n");
        fr.close();
    }

    //Done work
    private void removeStringFromFile(DataBlock b) throws IOException {
        int listIndex = filesWithOne.indexOf(b.fileN);
        File inputFile = new File(folder.getPath() + "\\" + b.fileN);

        if (listIndex != -1) {
            filesWithOne.remove(listIndex);
            inputFile.delete();
            return;
        }
        File tempFile = new File(folder + "\\TempFile.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        filesWithOne.add(b.fileN);

        try (Scanner scanner = new Scanner(inputFile)) {
            String nums = scanner.nextLine();
            String[] infoData = nums.split(" ");

            if (Long.parseLong(infoData[0]) == b.key) {
                for (int i = 0; i < Long.parseLong(infoData[1]); i++) {
                    scanner.nextLine();
                }
                nums = scanner.nextLine();
                infoData = nums.split(" ");
                writer.write(nums + "\n");
            } else {
                writer.write(nums + "\n");
            }

            String s;
            for (Long j = 0L; j < Long.parseLong(infoData[1]); j++) {
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
    private int clearData(DataBlock b) throws IOException {
        while (discSize + appandingSize + b.text.length() > discSizeMax) {
            if (arryaOfCountCalls.size() <= 0) {
                return 2;
            }
            Long evit = lists.get(arryaOfCountCalls.get(0)).iterator().next();
            discSize -= readStringFromFile(cacheMap.get(evit)).length();
            cacheSize -= appandingSize;
            if (cacheMap.get(evit).text != null) {
                cacheSize -= cacheMap.get(evit).text.length();
            }
            discSize -= appandingSize;
            removeStringFromFile(cacheMap.get(evit));
            // Clear cach
            lists.get(arryaOfCountCalls.get(0)).remove(evit);
            cacheMap.remove(evit);
            countCalls.remove(evit);
        }

        if (cacheSize + appandingSize + b.text.length() > cacheSizeMax) {
            Set set = new HashSet(arryaOfCountCalls);

            // Delete strings
            for (Iterator<Long> countCallsI = set.iterator(); countCallsI.hasNext()
                    && cacheSize + appandingSize > cacheSizeMax;) {
                Long countCheck = countCallsI.next();
                for (Iterator<Long> keyI = lists.get(countCheck).iterator(); keyI.hasNext()
                        && cacheSize + appandingSize > cacheSizeMax;) {
                    Long clearingKey = keyI.next();
                    if (cacheMap.get(clearingKey).text != null) {
                        cacheSize -= cacheMap.get(clearingKey).text.length();
                        cacheMap.get(clearingKey).text = null;
                    }
                }
            }

            // Delete files
            for (Iterator<Long> countCallsI = set.iterator(); countCallsI.hasNext()
                    && cacheSize + appandingSize > cacheSizeMax;) {
                Long countCheck = countCallsI.next();
                for (Iterator<Long> keyI = lists.get(countCheck).iterator(); keyI.hasNext()
                        && cacheSize + appandingSize > cacheSizeMax;) {
                    Long keyClearing = keyI.next();
                    cacheSize -= appandingSize;
                    discSize -= appandingSize;
                    discSize -= cacheMap.get(keyClearing).text.length();
                    removeStringFromFile(cacheMap.get(keyClearing));
                    cacheMap.remove(keyClearing);
                    lists.get(arryaOfCountCalls.get(arryaOfCountCalls.indexOf(countCheck))).remove(keyClearing);
                    arryaOfCountCalls.remove(countCalls.get(keyClearing));
                    countCalls.remove(keyClearing);
                }
            }
            if (cacheSize + appandingSize > cacheSizeMax) {
                return 2;
            }
            if (cacheSize + appandingSize + b.text.length() > cacheSizeMax) {
                return 1;
            }
        }
        return 0;
    }

    //Done, work
    private String readStringFromFile(DataBlock b) {
        String text = "";

        try (Scanner scanner = new Scanner(new File(folder.getPath() + "\\" + b.fileN))) {
            String nums = scanner.nextLine();
            String[] infoData = nums.split(" ");

            if (Long.parseLong(infoData[0]) != b.key) {
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

    //Done, work
    private Long countLines(String str) {
        Matcher m = Pattern.compile("\r\n|\r|\n").matcher(str);
        Long lines = 1L;
        while (m.find()) {
            lines++;
        }

        return lines;
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

    // Pre done
    public String get(Long key) {
        if (!cacheMap.containsKey(key)) {
            return null;
        }

        // Get the count from countCalls map
        Long count = countCalls.get(key);
        // increase the counter
        arryaOfCountCalls.remove(count);
        arryaOfCountCalls.add(count + 1);
        Collections.sort(arryaOfCountCalls);
        countCalls.put(key, count + 1);
        // remove the element from the counter to linkedhashset
        lists.get(count).remove(key);

        // when current min does not have any data, next one would be the min
        if (!lists.containsKey(count + 1)) {
            lists.put(count + 1, new LinkedHashSet<>());
        }

        lists.get(count + 1).add(key);

        if (cacheMap.get(key).text != null) {
            return cacheMap.get(key).text;
        }
        return readStringFromFile(cacheMap.get(key));
    }

    //Done
    public String put(Long key, String value) throws IOException {
        String tmp = null;
        DataBlock timeBlock = new DataBlock();
        timeBlock.key = key;
        timeBlock.text = value;
        timeBlock.countLines = countLines(value);

        // return and disk part
        if (cacheMap.containsKey(key)) {
            if (cacheMap.get(key).text != null) {
                tmp = cacheMap.get(key).text;
                cacheSize -= tmp.length();
            } else {
                tmp = readStringFromFile(cacheMap.get(key));
            }

            discSize -= appandingSize;
            discSize -= tmp.length();
            cacheSize -= appandingSize;
            removeStringFromFile(cacheMap.get(key));
            cacheMap.remove(key);
            lists.get(countCalls.get(key)).remove(key);
            arryaOfCountCalls.remove(countCalls.get(key));
            countCalls.remove(key);
        }

        int todo = clearData(timeBlock);

        if (todo == 2) {
            return null;
        }

        timeBlock.fileN = getFreeFileN();
        addStringToFile(timeBlock);

        if (todo == 1) {
            discSize += timeBlock.text.length();
            timeBlock.text = null;
        } else {
            discSize += timeBlock.text.length();
            cacheSize += timeBlock.text.length();
        }
        discSize += appandingSize;
        cacheSize += appandingSize;

        // put in cache and disk
        cacheMap.put(key, timeBlock);
        countCalls.put(key, 1L);
        lists.get(1L).add(key);
        arryaOfCountCalls.add(1L);
        return tmp;
    }

    public void putFromFolder(DataBlock value) throws IOException {
        int todo = clearData(value);
        if (todo == 2) {
            return;
        } else if (todo == 1) {
            discSize += value.text.length();
            value.text = null;
        } else {
            discSize += value.text.length();
            cacheSize += value.text.length();
        }
        discSize += appandingSize;
        cacheSize += appandingSize;

        // put in cache
        cacheMap.put(value.key, value);
        countCalls.put(value.key, 1L);
        arryaOfCountCalls.add(1L);
        lists.get(1L).add(value.key);
    }
}
