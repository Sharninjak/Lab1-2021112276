import java.io.BufferedWriter;
// import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.lang.String;



public class Lab1 {
    static final int INT_MAX = Integer.MAX_VALUE; //int最大整数
    static Map<String, Integer> graphElements = new HashMap<String, Integer>(); //单词->数组下标映射
    static Map<Integer, String> graphElements2 = new HashMap<Integer, String>(); //数组下标->单词映射
    static Map<Integer, List<Integer>> outDegreeGraph = new HashMap<Integer, List<Integer>>(); //出度图
    static int[][] outDegreeMatrix; //出度矩阵

    public static void main(String[] args) {
        try {
            String[] wordList = getTextFromFile("test.txt"); //从文件读入数据,得到单词数组
            // System.out.println(1);
            // System.out.println(Arrays.toString(wordList));
            Initializes(wordList); //从文件路径读入数据并进行处理，生成出度矩阵、出度图和哈希表
            // System.out.println(graphElements);

            Scanner scanner = new Scanner(System.in);
            while (true){
                System.out.println("\nPlease select function:");
                System.out.println("1.Search bridge words\n2.Generate new text\n3.Calculate the shortest path\n4.Random walk\n" +
                        "5.Show graph\n0.Exit");
                int chioce = scanner.nextInt();
                scanner.nextLine(); //读入缓冲区的换行符
                switch (chioce) {
                    case 1: //查询桥接词
                        System.out.println("Please input the first word:");
                        String word1 = scanner.nextLine();
                        System.out.println("Please input the second word:");
                        String word2 = scanner.nextLine();
                        if (!(graphElements.containsKey(word1)) &&!(graphElements.containsKey(word2))){
                            System.out.println("No \"" + word1 + "\" and \"" + word2 + "\" in the graph!");
                        }else if (!(graphElements.containsKey(word1))) {
                            System.out.println("No \"" + word1 + "\" in the graph!");
                        }else if (!(graphElements.containsKey(word2))) {
                            System.out.println("No \"" + word2 + "\" in the graph!");
                        }else {
                            List<String> list = queryBridgeWords(word1, word2);
                            if (list.isEmpty()) {
                                System.out.println("No bridge words from " + "\"" + word1 + "\"" + " to " + "\"" + word2 + "\"" + "!");
                            } else {
                                System.out.println("The bridge word list from " + "\"" + word1 + "\"" + " to " + "\"" + word2 + "\"" + " is: " + list);
                            }
                        }
                        break;
                    case 2: //根据输入文本生成新文本
                        System.out.println("Please enter text:");
                        String outputText = generateNewText(scanner.nextLine());
                        System.out.println("The new text is: " + "\n" + outputText);
                        break;
                    case 3: //计算最短路径
                        System.out.println("Please input the first word:");
                        String word1_3 = scanner.nextLine();
                        System.out.println("Please input the second word:  if null then calculate shortest path " +
                                "from " + "\"" + word1_3 + "\"" + " to all other words");
                        String word2_3 = scanner.nextLine();
                        if(word2_3.isEmpty()){
                            if (!graphElements.containsKey(word1_3)) {
                                System.out.println("The word isn't in the Graph!");
                            }else{
                                for(int i = 0; i < graphElements.size(); i++){
                                    if(!graphElements2.get(i).equals(word1_3) ){
                                        System.out.println(calcShortestPath(word1_3, graphElements2.get(i)));
                                    }
                                }
                            }
                        }else{
                            System.out.println(calcShortestPath(word1_3, word2_3));
                        }
                        // System.out.println(calcShortestPath(word1_3, word2_3));
                        break;
                    case 4: //随机游走，并将生成的文本生成txt文件
                        System.out.println("Text generation...");
                        String randText = randomWalk();
                        String outputFilePath = "func6outputText.txt";
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
                if (chioce == 0){
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        } finally {
            System.out.println("Parsing is over, thank you for your use!");
        }
    }

    //    生成出度矩阵、出度图及两个映射
    public static void Initializes(String[] strlist) {
        int count = 0;
        for (int i = 0; i < strlist.length; i++ ) { //生成单词和数组下标的互相映射和出度图
            if (!graphElements.containsKey(strlist[i])) {
                // System.out.print(strlist[i] + "\t");
                graphElements.put(strlist[i], count);
                graphElements2.put(count, strlist[i]);
                outDegreeGraph.put(count++, new ArrayList<Integer>());
            }
        }
        System.out.println();
        outDegreeMatrix = new int[count][count];
        for (int i = 0; i < strlist.length - 1; i++) { //遍历文本生成出度矩阵
            outDegreeMatrix[graphElements.get(strlist[i])][graphElements.get(strlist[i + 1])]++;
            outDegreeGraph.get(graphElements.get(strlist[i])).add(graphElements.get(strlist[i + 1]));
           // System.out.print("<" + graphElements.get(strlist[i]) + "," + graphElements.get(strlist[i + 1]) + ">" + "\t");
        }
        System.out.println(Arrays.toString(strlist));
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

    //    从文件读入并处理文本
    public static String[] getTextFromFile(String fileName) throws IOException {
        List<String> list = Files.readAllLines(Paths.get(fileName));
        String templist = "";
        for (int i = 0; i < list.size(); i++) {
            templist += " " + list.get(i);
        }
        templist = templist.trim();
        templist = templist.toLowerCase();
        // System.out.println(templist);
        if(templist.charAt(0) < 'a' || templist.charAt(0) > 'z'){
            templist = templist.substring(1);
        }
        return templist.split("[^a-z]+");
    }

    // public static String generateDotFile(Set<String> words) {
    //     StringBuilder dotFileContent = new StringBuilder();
    //     dotFileContent.append("digraph G {\n"); // 定义一个有向图
    //     // Add nodes
    //     for (String word : words) {
    //         dotFileContent.append("    ").append(word).append(";\n");
    //     }
    //     // Add edges
    //     for (String word : words) {
    //         for (String otherWord : words) {
    //             if (!word.equals(otherWord)) {
    //                 dotFileContent.append("    ").append(word).append(" -> ").append(otherWord).append(";\n");
    //             }
    //         }
    //     }
    //     dotFileContent.append("}\n");
    //     return dotFileContent.toString();
    // }
    // 生成有向图png部分
    // 计算单词对的出现次数
    private static Map<String, Map<String, Integer>> countWordPairs(String sentence) {
        Map<String, Map<String, Integer>> wordPairs = new HashMap<>();
        String[] words = sentence.split("\\s+"); // 使用正则表达式分割字符串
        for (int i = 0; i < words.length - 1; i++) {
            String wordA = words[i];
            String wordB = words[i + 1];
            wordPairs.computeIfAbsent(wordA, k -> new HashMap<>()).merge(wordB, 1, Integer::sum);
        }
        return wordPairs;
    }
    // 生成DOT文件内容
    private static String generateDotFile(Map<String, Map<String, Integer>> wordPairs) {
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
    // 生成DOT文件
    private static void writeToFile(String content, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 根据DOT文件生成PNG图片
    private static void generateGraph(String dotFileName) {
        try {
            ProcessBuilder pb = new ProcessBuilder("dot", "-Tpng", dotFileName, "-o", dotFileName + ".png");
            Process process = pb.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    //    展示有向图
    public static void displayGraph(String[] strlist) {
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
        String dotFileName = "graph.dot"; // DOT文件名
        writeToFile(dotFileContent, dotFileName); // 生成DOT文件
        generateGraph(dotFileName); // 生成PNG图片
        System.out.println("The graph is saved in " + dotFileName + ".png");
    }

    //    查询桥接词
    public static List<String> queryBridgeWords(String word1, String word2) {
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

    // 根据bridge word生成新文本
    public static String generateNewText(String inputText) throws IOException {
        String[] textList = inputText.split("[^a-zA-Z]+");
        String retStr = "";
        for (int i = 0; i < textList.length - 1; i++) {
            retStr += textList[i] + " ";
            //求出单词的出度词序列，随机选择一个插入原文本
            List<String> bridgeStr = queryBridgeWords(textList[i].toLowerCase(), textList[i + 1].toLowerCase());
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
    public static int[][] dijkstra(int index) {
        //经典的算法，本质是贪心
        Map<Integer, String> S_Gather = new HashMap<>();
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
        S_Gather.put(index, null);
        for (int i = 1; i < n; i++) {
            int minIndex = index;
            for (int j = 0; j < n; j++) { //选出未被选择过的路径最短的点
                if ((!S_Gather.containsKey(j)) && pathLen[j] < pathLen[minIndex]) {
                    minIndex = j;
                }
            }
            S_Gather.put(minIndex, null);
            for (int j = 0; j < n; j++) { //更新其余未被选择点的最短路径
                if (!S_Gather.containsKey(j)) {
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

    // 计算两个单词之间的最短路径
    public static String calcShortestPath(String word1, String word2) {
        if (!graphElements.containsKey(word1)) {
            return new String("The \"" + word1 + "\" isn't in the Graph!");
        }
        if (!graphElements.containsKey(word2)&&!word2.isEmpty()) {
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

    // 随机游走
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
// some test for git
