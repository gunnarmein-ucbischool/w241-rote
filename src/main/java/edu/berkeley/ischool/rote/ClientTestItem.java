/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.berkeley.ischool.rote;

import edu.berkeley.ischool.rote.Content.Question;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author gunnar
 */
public class ClientTestItem {

    String title;
    List<TestQuestion> questions;

    public static class TestQuestion {
        //Passage,Question,A,B,C,D,E,Answer

        public String question;
        public String[] answers;

        public TestQuestion(Content.Question q) {
            this.question = q.question;
            this.answers = q.answers.clone();
        }
    }

    public ClientTestItem(Content.Item ci) {
        List<Question> qs = ci.questions;
        this.title = ci.title;
        this.questions = qs.stream().map(q->new TestQuestion(q)).collect(Collectors.toList());
    }
}
