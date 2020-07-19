/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.berkeley.ischool.rote;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import spark.Request;
import spark.Session;

/**
 *
 * @author gunnar
 */
public class RoteSession {

    public static final long systemStart = System.currentTimeMillis();
    private static final long INITIAL_PERIOD = (1000 * 60) * 60; // first hour is cluster size =1
    private static final long CLUSTER_PERIOD = (1000 * 60) * 5;  // after that, assignment is in 5-minute start time clusters
    private static boolean currentClusterAssignment;
    private static long currentClusterStart = 0;
    private static int currentCluster = 0;

    private String id = "";
    private final Session session;
    private final long start;
    private final String startDateTime;
    private boolean treatment;
    private int cluster;
    public Stages.Stage stage;
    List<Content.Item> content1;
    List<Content.Item> content2;

    public RoteSession(Session s) {
        this.session = s;
        this.start = System.currentTimeMillis();
        this.startDateTime = java.time.LocalDate.now().toString() + " " + java.time.LocalTime.now().toString();
        this.id = session.id();
        // random content can be assgined now
        List<Content.Item> content = Content.getRandomItems();
        this.content1 = content.subList(0, Content.NUM_ITEMS / 2);
        this.content2 = content.subList(Content.NUM_ITEMS / 2, Content.NUM_ITEMS);
        this.stage = Stages.Stage.START;

        // treat/control is assigned later
    }

    public static RoteSession getSession(Request req) {
        return (RoteSession) req.session().attribute("rote_session");
    }

    public static String getSessionIDRoute(Request req) {
        String s = getSessionID(req);
        Main.log(req, "Rote: Session id requested, result: " + s);
        return s;
    }

    public static boolean startSession(Request req) {
        // if (req.session().isNew()) {
        RoteSession rs = new RoteSession(req.session());
        req.session().attribute("rote_session", rs);
        Main.log(req, "Rote: New session, time: "+rs.start);
        return true;
        // }
        // return false;
    }

    public static String getSessionID(Request req) {
        return getSession(req).getID();
    }

    public String getID() {
        return id;
    }

    public boolean getTreatment() {
        return treatment;
    }

    @Override
    public String toString() {
        return id + "," + start + "," + cluster + "," + treatment;
    }

    private boolean newTreatControl() {
        // if in regular mode, assign randomly
        if (Main.forceAssignment == -1) {
            return ThreadLocalRandom.current().nextDouble() < 0.5;
        }

        // if in forced admin override, assign by that (treat = 1, control = 0)
        return Main.forceAssignment == 1;
    }

    public void assignTreatControl() {
        // if in initial period, assign every session individually
        if (System.currentTimeMillis() < (RoteSession.systemStart + INITIAL_PERIOD)) {
            this.treatment = newTreatControl();
            synchronized (RoteSession.class) {
                RoteSession.currentCluster++;
                this.cluster = RoteSession.currentCluster;
            }
            Main.log(this, "Assigned to treatment: " + this.treatment + ", cluster: " + this.cluster);
            return;
        }

        // afterwards, keep track of CLUSTER_PERIOD intervals and assign in clusters
        // if the last cluster start was longer than CLUSTER_PERIOD ago, assign a new cluster value
        synchronized (RoteSession.class) {
            if (System.currentTimeMillis() > RoteSession.currentClusterStart + CLUSTER_PERIOD) {
                RoteSession.currentClusterStart = System.currentTimeMillis();
                RoteSession.currentClusterAssignment = newTreatControl();
                RoteSession.currentCluster++;
            }
            this.cluster = RoteSession.currentCluster;
            this.treatment = RoteSession.currentClusterAssignment;
            Main.log(this, "Assigned to treatment: " + this.treatment + ", cluster: " + this.cluster);
        }
    }

    public void setStage(Stages.Stage s) {
        Main.log(this, "Setting stage: " + s);
        this.stage = s;
    }

    public static List getContent(Request req) {
        RoteSession rs = getSession(req);
        switch (rs.stage) {
            case CONTENT1:
                Main.log(req, "delivering client content item " + rs.stage);
                return rs.content1.stream().map(ci -> new ClientContentItem(ci)).collect(Collectors.toList());
            case TEST1:
                Main.log(req, "delivering client test item " + rs.stage);
                return rs.content1.stream().map(ci -> new ClientTestItem(ci)).collect(Collectors.toList());

            case CONTENT2:
            case CONTENT2_READAGAIN:
            case CONTENT2_SPEAK:
            case CONTENT2_WRITE:
                Main.log(req, "delivering client content item " + rs.stage);
                return rs.content2.stream().map(ci -> new ClientContentItem(ci)).collect(Collectors.toList());
            case TEST2:
                Main.log(req, "delivering client test item " + rs.stage);
                return rs.content2.stream().map(ci -> new ClientTestItem(ci)).collect(Collectors.toList());
        }
        Main.log(req, "getcontent: Unexpected stage, was: " + rs.stage);
        return null;
    }
}
