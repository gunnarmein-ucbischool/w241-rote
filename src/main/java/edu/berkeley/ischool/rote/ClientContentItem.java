/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.berkeley.ischool.rote;

import static edu.berkeley.ischool.rote.Content.NUM_ANSWERS;
import edu.berkeley.ischool.rote.Content.Question;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author gunnar
 */
public class ClientContentItem {
    
    String title;
    String passage;
    List<String> questions;
    
    

    public ClientContentItem(Content.Item ci) {
        List<Question> qs = ci.questions;
        this.title = ci.title;
        this.passage = ci.passage;
        this.questions = qs.stream().map(q->q.question).collect(Collectors.toList());
    }
}
