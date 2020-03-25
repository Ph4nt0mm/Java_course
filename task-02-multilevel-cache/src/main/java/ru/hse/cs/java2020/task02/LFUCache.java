package ru.hse.cs.java2020.task02;

import java.io.*;
import java.util.*;

public class LFUCache implements EvictionPolicy {
    private HashMap<Long, dataBlock> cacheMap; //cache K and V
    private HashMap<Long, Long> countCalls; //K and counters
    private HashMap<Long, LinkedHashSet<Long>> lists; //Counter and item list
    private LinkedList<Long> filesWithOne; // pair key - file
    private ArrayList<Long> arryaOfCountCalls; // pair key - file
    private Long lastFileN;
    private File folder;
    public Long cacheSizeMax;
    public Long discSizeMax;
    public Long cacheSize;
    public Long discSize;

    class dataBlock {
        public Long fileN;
        public Long key;
        public Long countLines;
        public String text;
        dataBlock(Long n, String t) {
            fileN = n;
            countLines = countLines(t);
            text = t;
        }
        dataBlock() {}
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

    // will delete
    public void test() throws IOException {
        removeStringFromFile(cacheMap.get(13L));
    }

    //Done work well (вроде)
    public void OpenFolder(String path) {
        // Открываем собсно папку
        folder = new File(path);

        // Iдем по всем файлам в нем

        for (File i : folder.listFiles()) {
            // Сюды читаем
            dataBlock timeBlock = new dataBlock();
            try (Scanner scanner = new Scanner(i)) {
                long counter = 0;
                System.out.println(i.getPath());
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
                            if (j != 0)
                                timeBlock.text += "\n";
                            timeBlock.text += scanner.nextLine();
                        }

                        putFromFolder(timeBlock);

                        timeBlock = new dataBlock();

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

    //Done work ochev
    public void SetSizes(Long cacheSz, Long folderSz) {
        cacheSizeMax = cacheSz;
        discSizeMax = folderSz;
    }

    //Done work well
    private void addStringToFile(dataBlock b) throws IOException {
        File file = new File(folder.getPath() + "\\" + b.fileN);
        FileWriter fr = new FileWriter(file, true);

        fr.write(b.key + " " + b.countLines + "\n");
        fr.write(b.text + "\n");
        fr.close();
    }

    //Done work
    private void removeStringFromFile(dataBlock b) throws IOException {
        int listIndex = filesWithOne.indexOf(b.fileN);
        File inputFile = new File(folder.getPath() + "\\" + b.fileN);
        File tempFile = new File(folder + "\\TempFile.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        if (listIndex != -1) {
            filesWithOne.remove(listIndex);
            inputFile.delete();
            return;
        }

        filesWithOne.add(b.fileN);

        try (Scanner scanner = new Scanner(inputFile)) {
            String nums = scanner.nextLine();
            String[] infoData = nums.split(" ");

            if (Long.parseLong(infoData[0]) == b.key) {
                for (int i = 0; i < Long.parseLong(infoData[1]); i++)
                    scanner.nextLine();
            }

            nums = scanner.nextLine();
            infoData = nums.split(" ");
            writer.write(nums + "\n");
            for (int j = 0; j < Long.parseLong(infoData[1]); j++) {
                if (j != 0)
                    writer.write("\n");
                writer.write(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        writer.close();

        inputFile.delete();
        System.out.println(tempFile.renameTo(inputFile));
    }

    //0 - we just insert
    //1 - we insert without a string
    //2 - we cant insert
    private int clearData(dataBlock b) throws IOException {
        while (discSize + b.text.length() > discSizeMax) {
            if (arryaOfCountCalls.size() <= 0)
                return 2;
            Long evit = lists.get(arryaOfCountCalls.get(0)).iterator().next();
            discSize -= readStringFromFile(cacheMap.get(evit)).length();
            discSize -= 4;
            removeStringFromFile(cacheMap.get(evit));
            // Clear cach
            lists.get(arryaOfCountCalls.get(0)).remove(evit);
            cacheMap.remove(evit);
            countCalls.remove(evit);
        }

        Long i = 0L;

        if (cacheSize + b.text.length() > cacheSizeMax) {
            Set set = new HashSet(arryaOfCountCalls);
            // Delete strings
            for (Iterator<Long> countCallsI = set.iterator(); countCallsI.hasNext() &&
                    cacheSize + b.text.length() > cacheSizeMax;)
            {
                Long countCheck = countCallsI.next();
                for (Iterator<Long> keyI = lists.get(countCheck).iterator(); keyI.hasNext() &&
                        cacheSize + b.text.length() > cacheSizeMax;)
                {
                    if (cacheMap.get(keyI).text != null) {
                        cacheSize -= cacheMap.get(keyI).text.length();
                        cacheMap.get(keyI).text = null;
                    }
                }
            }

            // Delete files
            for (Iterator<Long> countCallsI = set.iterator(); countCallsI.hasNext() &&
                    cacheSize + b.text.length() > cacheSizeMax;) {
                Long countCheck = countCallsI.next();
                for (Iterator<Long> keyI = lists.get(countCheck).iterator(); keyI.hasNext() &&
                        cacheSize + b.text.length() > cacheSizeMax;)
                {
                    if (cacheMap.get(keyI).text != null) {
                        cacheSize -= 4;
                        cacheMap.remove(keyI);
                        lists.get(arryaOfCountCalls.get(Math.toIntExact(countCheck))).remove(keyI);
                        arryaOfCountCalls.remove(countCalls.get(keyI));
                        countCalls.remove(keyI);
                    }
                }
            }
            if (cacheSize + b.text.length() > cacheSizeMax)
                return 2;
            if (cacheSize + 4 > cacheSizeMax)
                return 1;
        }
        return 0;
    }

    //Done, work
    private String readStringFromFile(dataBlock b) {
        String text = "";

        try (Scanner scanner = new Scanner(new File(folder.getPath() + "\\" + b.fileN))) {
            String nums = scanner.nextLine();
            String[] infoData = nums.split(" ");

            if (Long.parseLong(infoData[0]) != b.key) {
                for (int i = 0; i < Long.parseLong(infoData[1]); i++)
                    scanner.nextLine();
                nums = scanner.nextLine();
                infoData = nums.split(" ");
            }

            for (int j = 0; j < Long.parseLong(infoData[1]); j++) {
                if (j != 0)
                    text += "\n";
                text += scanner.nextLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return text;
    }

    //Done, work
    private Long countLines(String str){
        String[] lines = str.split("\r\n|\r|\n");
        return (long) lines.length;
    }

    //Done, work
    public Long getFreeFileN() {
        if (filesWithOne.size() > 0)
            return filesWithOne.poll();
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
        if (!cacheMap.containsKey(key))
            return null;

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
        if (!lists.containsKey(count + 1))
            lists.put(count + 1, new LinkedHashSet<>());

        lists.get(count + 1).add(key);

        if (cacheMap.get(key).text != null)
            return cacheMap.get(key).text;
        return readStringFromFile(cacheMap.get(key));
    }

    //Done
    public String put(Long key, String value) throws IOException {
        dataBlock timeBlock = new dataBlock();
        timeBlock.key = key;
        timeBlock.text = value;
        timeBlock.countLines = countLines(value);

        int todo = clearData(timeBlock);
        if (todo == 2) {
            return null;
        } else if (todo == 1) {
            discSize += timeBlock.text.length();
            timeBlock.text = null;
        } else {
            discSize += timeBlock.text.length();
            cacheSize += timeBlock.text.length();
        }
        discSize += 4;
        cacheSize += 4;

        timeBlock.fileN = getFreeFileN();

        String tmp = null;

        // return and disk part
        if (cacheMap.containsKey(key)) {
            if (cacheMap.get(key).text != null)
                tmp = cacheMap.get(key).text;
            else
                tmp = readStringFromFile(cacheMap.get(key));

            removeStringFromFile(cacheMap.get(key));
            lists.get(countCalls.get(key)).remove(key);
            arryaOfCountCalls.remove(countCalls.get(key));
            countCalls.remove(key);
        }
        addStringToFile(timeBlock);

        // put in cache
        cacheMap.put(key, timeBlock);
        countCalls.put(key, 1L);
        lists.get(1L).add(key);
        arryaOfCountCalls.add(1L);
        return tmp;
    }

    public void putFromFolder(dataBlock value) throws IOException {
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
        discSize += 4;
        cacheSize += 4;

        // put in cache
        cacheMap.put(value.key, value);
        countCalls.put(value.key, 1L);
        arryaOfCountCalls.add(1L);
        lists.get(1).add(value.key);
    }
}