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
public class ClientTestItem {

    String title;
    String question;
    String[] answers;

    public ClientTestItem(Content.Item ci) {
        this.title = ci.title;
        this.question = ci.question;
        this.answers = ci.answers.clone();
    }
}
