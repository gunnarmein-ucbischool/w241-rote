/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.berkeley.ischool.rote;

import java.util.concurrent.ThreadLocalRandom;
import spark.Session;

/**
 *
 * @author gunnar
 */
public class RoteSession {
    private String id = "";
    private Session session;
    private long start;
    private String startDateTime;
    private boolean treatment;

    public RoteSession(Session s) {
        this.session = s;
        this.start = System.currentTimeMillis();
        this.startDateTime = java.time.LocalDate.now().toString()+" "+java.time.LocalTime.now().toString();
        this.id = session.id(); 
        // random assignment to treatment or control
        this.treatment = ThreadLocalRandom.current().nextDouble() < 0.5;
        
    }
    
    public String getID() {
        return id;
    }
    
    public boolean getTreatment() {
        return treatment;
    }
    
    @Override
    public String toString() {
        return id+","+start+","+treatment;
    }
}
