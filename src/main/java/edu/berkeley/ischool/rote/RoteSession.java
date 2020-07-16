/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.berkeley.ischool.rote;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import spark.Session;

/**
 *
 * @author gunnar
 */
public class RoteSession {

    private static final long systemStart = System.currentTimeMillis();
    private static final long INITIAL_PERIOD = (1000 * 60) * 60; // first hour is cluster size =1
    private static final long CLUSTER_PERIOD = (1000 * 60) * 5;  // after that, assignment is in 5-minute start time clusters
    private static boolean currentClusterAssignment;
    private static long currentClusterStart = 0;

    private String id = "";
    private final Session session;
    private final long start;
    private final String startDateTime;
    private boolean treatment;
    List<Content.Item> content;

    public RoteSession(Session s) {
        this.session = s;
        this.start = System.currentTimeMillis();
        this.startDateTime = java.time.LocalDate.now().toString() + " " + java.time.LocalTime.now().toString();
        this.id = session.id();
        // random assignment to treatment or control
        this.treatment = assignTreatControl();
        this.content = Content.getRandomItems();

    }

    public String getID() {
        return id;
    }

    public boolean getTreatment() {
        return treatment;
    }

    @Override
    public String toString() {
        return id + "," + start + "," + treatment;
    }

    boolean assignTreatControl() {
        // if in initial period, assign every session individually
        if (System.currentTimeMillis() < (RoteSession.systemStart + INITIAL_PERIOD)) {
            return ThreadLocalRandom.current().nextDouble() < 0.5;
        }

        // afterwards, keep track of CLUSTER_PERIOD intervals and assign in clusters
        // if the last cluster start was longer than CLUSTER_PERIOD ago, assign a new cluster value
        synchronized (RoteSession.class) {
            if (System.currentTimeMillis() > RoteSession.currentClusterStart + CLUSTER_PERIOD) {
                RoteSession.currentClusterStart = System.currentTimeMillis();
                RoteSession.currentClusterAssignment = ThreadLocalRandom.current().nextDouble() < 0.5;
            }
            return RoteSession.currentClusterAssignment;
        }
    }
}
