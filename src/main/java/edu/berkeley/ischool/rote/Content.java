/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.berkeley.ischool.rote;

//import java.util.Arrays;
import com.opencsv.CSVReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author gunnar
 */
public class Content {

    public static final int NUM_ANSWERS = 4;
    private static ArrayList<Item> list = new ArrayList<>();

    public static class Item {
        //Passage,Question,A,B,C,D,E,Answer

        int id;
        String passage;
        String question;
        String[] answers;
        int correctAnswer;

        public Item(String p, String q, String A, String B, String C, String D, String answer) {
            this.passage = p;
            this.question = q;

            this.answers = new String[NUM_ANSWERS];

            this.answers[0] = A;
            this.answers[1] = B;
            this.answers[2] = C;
            this.answers[3] = D;

            //this.correctAnswer = answer;
            this.correctAnswer = -1;
            for (int i = 0; i < NUM_ANSWERS; i++) {
                if (this.answers[i].equals(answer)) {
                    this.correctAnswer = i;
                    break;
                }
            }

            if (this.correctAnswer == -1) {
                System.out.println("  ERROR: Correct answer not found");
            }
        }
    }

    public static void readContent() {
        System.out.println("Java is looking for Content1 at: "+Content.class.getResource("content1.csv"));
        InputStream is = Content.class.getResourceAsStream("content1.csv");
        try {
            InputStreamReader isr = new InputStreamReader(Content.class.getResourceAsStream("content1.csv"));
            CSVReader reader = new CSVReader(isr);
            String[] line;
            while ((line = reader.readNext()) != null) {
                if (line.length != 6) {
                    System.err.println("ERROR: Incorrect number of columns in CSV content1.csv");
                }
                
                Item i = new Item(line[0], line[1], line[2], line[3], line[4], line[5], line[6]);
                i.id = Content.list.size();
                Content.list.add(i);
            }
        } catch (Exception e) {
            System.err.println("ERROR: Reading content files: " + e);
        }
    }
    
    public static List<Item> getRandomItems() {
        ArrayList<Item> results = null;
        synchronized(Content.class) {
            results = (ArrayList<Item>) list.clone();
        }
        Collections.shuffle(list);
        return list.subList(0, NUM_ANSWERS);
    }
}
