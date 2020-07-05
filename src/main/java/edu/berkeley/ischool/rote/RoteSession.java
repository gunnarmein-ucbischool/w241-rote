/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.berkeley.ischool.rote;

import spark.Session;

/**
 *
 * @author gunnar
 */
public class RoteSession {
    String id = "";
    Session session;
    long start;

    public RoteSession(Session s) {
        this.session = s;
        this.start = System.currentTimeMillis();
        this.id = session.id(); // todo; add current data to id
    }
    
    public String getID() {
        return id;
    }
}
