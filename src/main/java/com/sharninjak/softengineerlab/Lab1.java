package com.sharninjak.softengineerlab;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


/**
 * main class Lab1.
 */
public class Lab1 {
    private static final int INT_MAX = Integer.MAX_VALUE;
    private static final Map<String, Integer> graphElements =
            new HashMap<>();
    private static final Map<Integer, String> graphElements2 =
            new HashMap<>(); //数组下标->单词映射
    private static final Map<Integer, List<Integer>> outDegreeGraph =
            new HashMap<>(); //出度图
    private static int[][] outDegreeMatrix; //出度矩阵

    /**
     * main function.
     */
    public static void main(final String[] args) {
        try {
            String[] wordList = getTextFromFile("src/main/java/com/sharninjak/softengineerlab/test.txt"); //从文件读入数据,得到单词数组
            // System.out.println(1);
            // System.out.println(Arrays.toString(wordList));
            initializes(wordList); //从文件路径读入数据并进行处理，生成出度矩阵、出度图和哈希表
            // System.out.println(graphElements);
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("\n--------------------------------------------");
                System.out.println("Please select function:");
                //StringBuilder sb = new StringBuilder();
                //sb.append("1.Search bridge words\n")
                //        .append("2.Generate new text\n")
                //        .append("3.Calculate the shortest path\n")
                //        .append("4.Random walk\n")
                //        .append("5.Show graph\n")
                //        .append("0.Exit");
                //String result = sb.toString();
                String result = """
                    1.Search bridge words
                    2.Generate new text
                    3.Calculate the shortest path
                    4.Random walk
                    5.Show graph
                    0.Exit""";
                System.out.println(result);
                int chioce = scanner.nextInt();
                scanner.nextLine(); //读入缓冲区的换行符
                switch (chioce) {
                    case 1: //查询桥接词
                        System.out.println("Please input the first word:");
                        String word1 = scanner.nextLine();
                        System.out.println("Please input the second word:");
                        String word2 = scanner.nextLine();
                        System.out.println(queryBridgeWords(word1, word2));
                        break;
                    case 2: //根据输入文本生成新文本
                        System.out.println("Please enter text:");
                        String outputText = generateNewText(scanner.nextLine());
                        //System.out.println("The new text is: " + "\n" + outputText);
                        System.out.printf("The new text is:\n%s", outputText);
                        break;
                    case 3: //计算最短路径
                        System.out.println("Please input the first word:");
                        String nextLineString = scanner.nextLine();
                        //System.out.println("Please input the second word:  if null then calculate shortest path "
                        //        + "from " + "\"" + nextLineString + "\"" + " to all other words");
                        System.out.printf("""
                            Please input the second word: if null then calculate shortest path
                            from "%s" to all other words""", nextLineString);
                        String nextLineString2 = scanner.nextLine();
                        if (nextLineString2.isEmpty()) {
                            if (!graphElements.containsKey(nextLineString)) {
                                System.out.println("The word isn't in the Graph!");
                            } else {
                                for (int i = 0; i < graphElements.size(); i++) {
                                    if (!graphElements2.get(i).equals(nextLineString)) {
                                        System.out.println(calcShortestPath(nextLineString, graphElements2.get(i)));
                                    }
                                }
                            }
                        } else {
                            System.out.println(calcShortestPath(nextLineString, nextLineString2));
                        }
                        // System.out.println(calcShortestPath(word1_3, word2_3));
                        break;
                    case 4: //随机游走，并将生成的文本生成txt文件
                        System.out.println("Text generation...");
                        String randText = randomWalk();
                        String outputFilePath = "src/main/java/com/sharninjak/softengineerlab/func6outputText.txt";
                        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
                            writer.write(randText);
                            System.out.println("Write file successfully!");
                        } catch (IOException e) {
                            System.out.println("Write file failure!");
                        }
                        break;
                    case 5:
                        displayGraph(wordList);
                        break;
                    case 0:
                        System.out.println("END");
                        break;
                    default:
                        System.out.println("Input error!");
                }
                if (chioce == 0) {
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        } finally {
            System.out.println("Parsing is over, thank you for your use!");
        }
    }

    /**
     * 生成出度矩阵、出度图及两个映射.
     *
     * @param strlist all words in an array
     */
    public static void initializes(final String[] strlist) {
        //for (int i = 0; i < strlist.length; i++) { //生成单词和数组下标的互相映射和出度图
        //    if (!graphElements.containsKey(strlist[i])) {
        //        // System.out.print(strlist[i] + "\t");
        //        graphElements.put(strlist[i], count);
        //        graphElements2.put(count, strlist[i]);
        //        outDegreeGraph.put(count++, new ArrayList<Integer>());
        //    }
        //}
        int count = 0;
        Iterator<String> iterator = Arrays.asList(strlist).iterator();
        while (iterator.hasNext()) {
            String word = iterator.next();
            if (!graphElements.containsKey(word)) {
                // 将单词和计数器count放入 graphElements 中
                graphElements.put(word, count);
                // 将计数器count和单词放入 graphElements2 中
                graphElements2.put(count, word);
                // 创建出度图的列表，并与计数器count相关联
                outDegreeGraph.put(count, new ArrayList<Integer>());
                // 递增计数器
                count++;
            }
        }
        System.out.println();
        outDegreeMatrix = new int[count][count];
        for (int i = 0; i < strlist.length - 1; i++) { //遍历文本生成出度矩阵
            outDegreeMatrix[graphElements.get(strlist[i])]
                    [graphElements.get(strlist[i + 1])]++;
            outDegreeGraph.get(graphElements.get(strlist[i]))
                    .add(graphElements.get(strlist[i + 1]));
            // System.out.print("<" + graphElements.get(strlist[i]) + "," + graphElements.get(strlist[i + 1]) + ">" + "\t");
        }
        //System.out.println(Arrays.toString(strlist));
        // Iterator<Map.Entry<Integer, List<Integer>>> iterator = outDegreeGraph.entrySet().iterator();
        // while (iterator.hasNext()) {
        //     Map.Entry<Integer, List<Integer>> entry = iterator.next();
        //     System.out.println("key:" + entry.getKey() + ",vaule:" + entry.getValue());
        // }
        // System.out.println();
        // for (int i = 0; i < count; i++) {
        //     System.out.println(Arrays.toString(outDegreeMatrix[i]));
        // }
    }

    /**
     * 从文件读入并处理文本.
     *
     * @param fileName name of input file
     * @return String[] 无大写，符号的单词数组
     */
    public static String[] getTextFromFile(final String fileName) throws IOException {
        List<String> list = Files.readAllLines(Paths.get(fileName));
        String templist = "";
        templist = String.join(" ", list);
        templist = templist.trim();
        templist = templist.toLowerCase();
        // System.out.println(templist);
        if (templist.charAt(0) < 'a' || templist.charAt(0) > 'z') {
            templist = templist.substring(1);
        }
        return templist.split("[^a-z]+");
    }

    /**
     * 生成有向图png部分-计算单词对的出现次数.
     *
     * @param sentence 处理后的句子，只有小写单词和空格
     * @return wordPairs
     */
    private static Map<String, Map<String, Integer>> countWordPairs(final String sentence) {
        Map<String, Map<String, Integer>> wordPairs = new HashMap<>();
        String[] words = sentence.split("\\s+"); // 使用正则表达式分割字符串
        for (int i = 0; i < words.length - 1; i++) {
            String wordA = words[i];
            String wordB = words[i + 1];
            wordPairs.computeIfAbsent(wordA, k -> new HashMap<>()).merge(wordB, 1, Integer::sum);
        }
        return wordPairs;
    }

    /**
     * 生成有向图png部分-生成DOT文件内容.
     *
     * @param wordPairs 单词对及出现次数
     * @return String
     */
    private static String generateDotFile(final Map<String, Map<String, Integer>> wordPairs) {
        StringBuilder dotFileContent = new StringBuilder();
        dotFileContent.append("digraph G {\n");
        // Add nodes
        for (String word : wordPairs.keySet()) {
            dotFileContent.append("    ").append(word).append(";\n");
        }
        // Add edges with weights
        for (String wordA : wordPairs.keySet()) {
            for (String wordB : wordPairs.get(wordA).keySet()) {
                int weight = wordPairs.get(wordA).get(wordB);
                dotFileContent.append("    ").append(wordA).append(" -> ").append(wordB)
                        .append(" [label=\"").append(weight).append("\"];\n");
            }
        }
        dotFileContent.append("}\n");
        return dotFileContent.toString();
    }

    /**
     * 生成有向图png部分-生成DOT文件.
     *
     * @param content DOT文件内容
     * @param fileName 生成DOT文件的名字
     * @return
     */
    private static void writeToFile(final String content, final String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 根据DOT文件生成PNG图片
    /**
     * 生成有向图png部分-根据DOT文件生成PNG图片.
     *
     * @param dotFileName DOT文件名
     * @return None
     */
    private static void generateGraph(final String dotFileName) {
        try {
            ProcessBuilder pb = new ProcessBuilder("dot", "-Tpng", dotFileName, "-o", dotFileName + ".png");
            Process process = pb.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成有向图png部分-展示有向图.
     *
     * @param strlist 单词数组
     * @return None
     */
    public static void displayGraph(final String[] strlist) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strlist.length; i++) {
            sb.append(strlist[i]);
            if (i < strlist.length - 1) {
                sb.append(" "); // 在每个元素后面添加一个空格，除了最后一个元素
            }
        }
        String joinedString = sb.toString();
        // System.out.println(joinedString);
        Map<String, Map<String, Integer>> wordPairs = countWordPairs(joinedString); // 计算单词对的出现次数
        String dotFileContent = generateDotFile(wordPairs); // 生成DOT文件内容
        String dotFileName = "src/main/java/com/sharninjak/softengineerlab/graph.dot"; // DOT文件名
        writeToFile(dotFileContent, dotFileName); // 生成DOT文件
        generateGraph(dotFileName); // 生成PNG图片
        System.out.println("The graph is saved in " + dotFileName + ".png");
    }

    /**
     * 查询桥接词.
     *
     * @param word1 桥接词1
     * @param word2 桥接词2
     * @return List<String> word1和word2之间的所有可作为桥接词的单词
     */
    public static List<String> query(final String word1, final String word2) {
        List<String> list = new ArrayList<>();
        if (!(graphElements.containsKey(word1) && graphElements.containsKey(word2))) {
            return list;
        }
        int pre = graphElements.get(word1);
        int sub = graphElements.get(word2);
        for (int i = 0; i < outDegreeMatrix.length; i++) {
            if (outDegreeMatrix[pre][i] > 0) {
                if (outDegreeMatrix[i][sub] > 0) {
                    list.add(graphElements2.get(i));
                }
            }
        }
        return list;
    }

    /**
     * 判定word1和word2是否存在，再查询桥接词.
     *
     * @param word1 桥接词1
     * @param word2 桥接词2
     * @return String 最终结果
     */
    public static String queryBridgeWords(final String word1, final String word2) {
        String result;
        if (!(graphElements.containsKey(word1))
                && !(graphElements.containsKey(word2))) {
            //System.out.println("No \"" + word1 + "\" and \""
            //        + word2 + "\" in the graph!");
            result = "No \"" + word1 + "\" and \"" + word2 + "\" in the graph!";
            //System.out.printf("No \"%s\" and \"%s\" in the graph!\n", word1, word2);
        } else if (!(graphElements.containsKey(word1))) {
            //System.out.println("No \"" + word1 + "\" in the graph!\n");
            result = "No \"" + word1 + "\" in the graph!";
            //System.out.printf("No \"%s\" in the graph!\n", word1);
        } else if (!(graphElements.containsKey(word2))) {
            //System.out.println("No \"" + word2 + "\" in the graph!");
            result = "No \"" + word2 + "\" in the graph!";
            //System.out.printf("No \"%s\" in the graph!\n", word2);
        } else {
            List<String> list = query(word1, word2);
            if (list.isEmpty()) {
                //System.out.println("No bridge words from " + "\"" + word1 + "\"" + " to " + "\"" + word2 + "\"" + "!");
                result = "No bridge words from \"" + word1 + "\" to \"" + word2 + "\"!";
                //System.out.printf("No bridge words from \"%s\" to \"%s\"!", word1, word2);
            } else {
                result = "The bridge word list from \"" + word1 + "\" to \"" + word2 + "\" is: " + list;
                //System.out.printf("The bridge word list from \"%s\" to \"%s\" is: %s%n", word1, word2, list);
            }
        }
        return result;
    }

    /**
     * 根据bridge word生成新文本,扩充句子.
     *
     * @param inputText a sentence to expand using BridgeWords
     * @return String 用桥接词扩充后的句子
     */
    public static String generateNewText(final String inputText) throws IOException {
        String[] textList = inputText.split("[^a-zA-Z]+");
        String retStr = "";
        for (int i = 0; i < textList.length - 1; i++) {
            retStr += textList[i] + " ";
            //求出单词的出度词序列，随机选择一个插入原文本
            List<String> bridgeStr = query(textList[i].toLowerCase(), textList[i + 1].toLowerCase());
            if (!bridgeStr.isEmpty()) {
                int randomInt = new Random().nextInt(bridgeStr.size());
                retStr += bridgeStr.get(randomInt) + " ";
            }
        }
        retStr += textList[textList.length - 1];
        retStr = retStr.trim();
        return retStr;
    }

    // Dijkstra算法
    /**
     * Dijkstra算法.
     *
     * @param index word id
     * @return int[][] 一个词到其他所有词的最短路径
     */
    public static int[][] dijkstra(final int index) {
        //经典的算法，本质是贪心
        Map<Integer, String> gather = new HashMap<>();
        int n = graphElements.size();
        int[] path = new int[n];
        int[] pathLen = new int[n];
        for (int i = 0; i < n; i++) { //初始化两个数组
            path[i] = index;
            if (outDegreeMatrix[index][i] != 0) {
                pathLen[i] = outDegreeMatrix[index][i];
            } else {
                pathLen[i] = INT_MAX;
            }
        }
        // System.out.println("path: " + Arrays.toString(path));
        // System.out.println("pathlen: " + Arrays.toString(pathLen));
        gather.put(index, null);
        for (int i = 1; i < n; i++) {
            int minIndex = index;
            for (int j = 0; j < n; j++) { //选出未被选择过的路径最短的点
                if ((!gather.containsKey(j)) && pathLen[j] < pathLen[minIndex]) {
                    minIndex = j;
                }
            }
            gather.put(minIndex, null);
            for (int j = 0; j < n; j++) { //更新其余未被选择点的最短路径
                if (!gather.containsKey(j)) {
                    if (outDegreeMatrix[minIndex][j] != 0) {
                        if (pathLen[minIndex] + outDegreeMatrix[minIndex][j] < pathLen[j]) {
                            pathLen[j] = pathLen[minIndex] + outDegreeMatrix[minIndex][j];
                            path[j] = minIndex;
                        }
                    }
                }
            }
        }
        // System.out.println(Arrays.deepToString(new int[][]{path, pathLen}));
        return new int[][]{path, pathLen};
    }

    /**
     * 计算两个单词之间的最短路径.
     *
     * @param word1 第一个词
     * @param word2 第二个词
     * @return String 最短路径 "a->b->c"
     */
    public static String calcShortestPath(final String word1, final String word2) {
        if (!graphElements.containsKey(word1)) {
            return new String("The \"" + word1 + "\" isn't in the Graph!");
        }
        if (!graphElements.containsKey(word2) && !word2.isEmpty()) {
            return new String("The \"" + word2 + "\" isn't in the Graph!");
        }
        int m = graphElements.get(word1);
        int n;
        int[][] path = dijkstra(m);
        // if (!graphElements.containsKey(word2)) {
        //     n = new Random().nextInt(graphElements.size());
        // } else {
        n = graphElements.get(word2);
        // }
        if (path[1][n] == INT_MAX) {
            return new String("The \"" + word1 + "\" to \"" + graphElements2.get(n) + "\" is unreachable!");
        }
        String ret = "->" + graphElements2.get(n);
        int tmp = path[0][n];
        while (tmp != m) {
            ret = "->" + graphElements2.get(tmp) + ret;
            tmp = path[0][tmp];
        }
        ret = graphElements2.get(m) + ret;
        ret += " " + "The shortest path is " + path[1][n] + "!";
        return ret;
    }

    /**
     * 随机游走.
     *
     * @return String 随机游走序列 "a b c d"
     */
    public static String randomWalk() throws InterruptedException {
        Map<String, String> passedPath = new HashMap<>(); //维护哈希表记录游走过的边
        String ret = "";
        Random rand = new Random();
        int index = rand.nextInt(graphElements.size());
        ret += graphElements2.get(index) + " ";
        System.out.print(graphElements2.get(index) + " ");
        while (!outDegreeGraph.get(index).isEmpty()) { //单词出度为0时结束游走
            Thread.sleep(200);
            //从出度表中随机选择下一个单词
            int nextIndex = outDegreeGraph.get(index).get(rand.nextInt(outDegreeGraph.get(index).size()));
            String tmp = "" + index + nextIndex;
            ret += graphElements2.get(nextIndex) + " ";
            System.out.print(graphElements2.get(nextIndex) + " ");
            if (passedPath.containsKey(tmp)) { // 路径重复时结束游走
                break;
            }
            passedPath.put(tmp, null); //将该边加入哈希表
            index = nextIndex;
        }
        System.out.println();
        return ret.trim();
    }
}
