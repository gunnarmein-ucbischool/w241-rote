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
    public static final int NUM_ITEMS = 4;
    public static final int NUM_QUESTIONS = 4;
    public static final int NUM_ANSWERS = 5;
    private static final ArrayList<Item> list = new ArrayList<>();

    public static class Item {

        int id;
        String title;
        int seconds;
        String passage;
        public List<Content.Question> questions;

        public Item(int id, String title, String p, String seconds, List<Content.Question> questions) {
            this.id = id;
            this.title = title;
            this.passage = p;
            this.questions = questions;
            this.seconds = Integer.parseInt(seconds);
        }
    }

    public static class Question {
        //Passage,Question,A,B,C,D,E,Answer

        public String question;
        public String[] answers;
        public int correctAnswer;

        public Question(String q, String A, String B, String C, String D, String E, String answer) {
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
                Main.log("  ERROR: Correct answer not found");
            }
        }
    }

    public static void readContent(String file) {
        String lastPassage = "";
        String lastTitle = "";
        System.out.println("Reading content csv " + file);
        try {
            InputStream is = new FileInputStream(file);

            InputStreamReader isr = new InputStreamReader(is);
            CSVReader reader = new CSVReader(isr);
            String[] line;
            reader.readNext(); // skip header
            int id = 1;
            while ((line = reader.readNext()) != null) {
                if (line.length != NUM_COLUMNS) {
                    Main.log("ERROR: Incorrect number of columns in CSV content1.csv: " + line.length);
                }
                // Passage,Question number,Question,A,B,C,D,E,IDK,Answer
                String title = line[0];
                String seconds = line[1];
                String passage = line[2];

                ArrayList<Question> qs = new ArrayList<>();
                for (int i = 0; i < NUM_QUESTIONS; i++) {
                    String question = line[3];
                    String A = line[4];
                    String B = line[5];
                    String C = line[6];
                    String D = line[7];
                    String E = line[8];
                    String IDK = line[9];
                    String answer = line[10];

                    Question q = new Question(question, A, B, C, D, E, answer);
                    qs.add(q);

                    if (i == NUM_QUESTIONS - 1) {
                        break;
                    }
                    line = reader.readNext();
                }

                Item i = new Item(id, title, passage, seconds, qs);
                Content.list.add(i);
                Main.log("Content added: id: "+id+", title: " + title);
                lastPassage = passage;
                lastTitle = title;
                id++;
            }
        } catch (CsvValidationException | IOException e) {
            Main.log("ERROR: Reading content files: " + e);
            Main.log("Content not found at " + file);
            return;
        }
        Main.log("Successfully read content csv " + file);
    }

    public static List<Item> getRandomItems() {
        ArrayList<Item> results = null;
        synchronized (Content.class) {
            results = (ArrayList<Item>) list.clone();
        }
        Collections.shuffle(results);
        return results.subList(0, NUM_ITEMS);
    }

}
