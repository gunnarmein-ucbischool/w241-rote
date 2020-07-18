/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.berkeley.ischool.rote;

//import java.util.Arrays;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author gunnar
 */
public class Content {

    public static final int NUM_COLUMNS = 11;
    public static final int NUM_ITEMS = 8;
    public static final int NUM_ANSWERS = 5;
    private static final ArrayList<Item> list = new ArrayList<>();

    public static class Item {
        //Passage,Question,A,B,C,D,E,Answer

        int id;
        String title;
        String passage;
        String question;
        String[] answers;
        int correctAnswer;

        public Item(String title, String p, String q, String A, String B, String C, String D, String E, String answer) {
            this.title = title;
            this.passage = p;
            this.question = q;

            this.answers = new String[NUM_ANSWERS];

            this.answers[0] = A;
            this.answers[1] = B;
            this.answers[2] = C;
            this.answers[3] = D;
            this.answers[4] = E;

            //this.correctAnswer = answer;
            this.correctAnswer = "ABCDE".indexOf(answer);
          

            if (this.correctAnswer == -1) {
                System.out.println("  ERROR: Correct answer not found");
            }
        }
    }

    public static void readContent(String file) {
        String lastPassage = "";
        String lastTitle = "";
        System.out.println("Reading content csv "+file);
        try {
            InputStream is = new FileInputStream(file);

            InputStreamReader isr = new InputStreamReader(is);
            CSVReader reader = new CSVReader(isr);
            String[] line;
            reader.readNext(); // skip header
            while ((line = reader.readNext()) != null) {
                if (line.length != NUM_COLUMNS) {
                    System.err.println("ERROR: Incorrect number of columns in CSV content1.csv: " + line.length);
                }
                // Passage,Question number,Question,A,B,C,D,E,IDK,Answer
                String title = line[0].equals("") ? lastTitle : line[0];
                String passage = line[0].equals("") ? lastPassage : line[1];
                System.out.println("Content title: "+title);
                String questionNumber = line[2];
                String question = line[3];
                String A = line[4];
                String B = line[5];
                String C = line[6];
                String D = line[7];
                String E = line[8];
                String IDK = line[9];
                String answer = line[10];

                Item i = new Item(title, passage, question, A, B, C, D, E, answer);
                i.id = Content.list.size();
                Content.list.add(i);
                lastPassage = passage;
                lastTitle = title;
            }
        } catch (CsvValidationException | IOException e) {
            System.err.println("ERROR: Reading content files: " + e);
            System.err.println("Content not found at " + file);
            return;
        }
        System.out.println("Successfully read content csv "+file);
    }

    public static List<Item> getRandomItems() {
        ArrayList<Item> results = null;
        synchronized (Content.class) {
            results = (ArrayList<Item>) list.clone();
        }
        Collections.shuffle(list);
        return list.subList(0, NUM_ITEMS);
    }

}
