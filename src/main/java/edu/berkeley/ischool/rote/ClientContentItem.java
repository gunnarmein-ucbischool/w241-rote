/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.berkeley.ischool.rote;

/**
 *
 * @author gunnar
 */
public class ClientContentItem {
    String title;
    String passage;
    String question;
    
    public ClientContentItem(Content.Item ci) {
        this.title = ci.title;
        this.passage = ci.passage;
        this.question = ci.question;
    }
}
