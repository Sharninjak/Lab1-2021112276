package com.sharninjak.softengineerlab;

import org.junit.jupiter.api.Test;

import java.io.IOException;


import static com.sharninjak.softengineerlab.Lab1.getTextFromFile;
import static com.sharninjak.softengineerlab.Lab1.initializes;
import static com.sharninjak.softengineerlab.Lab1.queryBridgeWords;
import static org.junit.jupiter.api.Assertions.*;

class Lab1Test {
    @Test
    void queryBridgeWordsTest() {
        try {
            String[] word1 = {"dance", "of", "the", "of", "asdfasd", "    "};
            String[] word2 = {"change", "and", "of", "", "for", "-adfafc."};
            String[] expected =
                    {
                            "The bridge word list from \"dance\" to \"change\" is: [of]",
                            "The bridge word list from \"of\" to \"and\" is: [change, hello]",
                            "No bridge words from \"the\" to \"of\"!",
                            "No \"\" in the graph!",
                            "No \"asdfasd\" in the graph!",
                            "No \"    \" and \"-adfafc.\" in the graph!",
                    };
            int test_index = 2;
            String[] wordList = getTextFromFile("src/test/java/com/sharninjak/softengineerlab/test.txt");
            initializes(wordList);
            String result = queryBridgeWords(word1[test_index], word2[test_index]);
            System.out.println(result);
            assertEquals(expected[test_index], result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void randomWalkTesk() {

    }
}