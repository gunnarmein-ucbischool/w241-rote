/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.berkeley.ischool.rote;

import spark.Request;
import spark.Response;

/**
 *
 * @author gunnar
 */
public class Stages {

    public static String start(Request req, Response res) {
        // startSession will assign treat/control and establish the context
        Boolean success = RoteSession.startSession(req);
        RoteSession rs = RoteSession.getSession(req);
        if (success) {
            rs.stage = Main.Stage.INFO;
            res.redirect("stage_info.html");
        } else {
            rs.stage = Main.Stage.INFO;
            res.redirect("stage_info.html");
//            res.redirect("error.html?reason=sessionexists");
//            rs.stage = Main.NextStage.INVALID;
        }

        return "started";
    }

    public static String postInfo(Request req, Response res) {
        RoteSession rs = RoteSession.getSession(req);
        String age = req.queryParamOrDefault("Age", "NA");
        String gender = req.queryParamOrDefault("Gender", "NA");
        String knowledge = req.queryParamOrDefault("Knowledge", "NA");
        String reading = req.queryParamOrDefault("Reading", "NA");
        
        rs.assignTreatControl();

//        System.out.println(age);
//        System.out.println(gender);
//        System.out.println(knowledge);
//        System.out.println(reading);
        Main.logCov(rs + "," + age + "," + gender + "," + knowledge + "," + reading);

        res.redirect("stage_content");
        return "ok";
    }

    public static String content(Request req, Response res) {
        RoteSession rs = RoteSession.getSession(req);
        if (rs.stage == Main.Stage.INFO) {
            rs.stage = Main.Stage.CONTENT1;
        } else {
            rs.stage = Main.Stage.CONTENT2;
        }
        System.out.println("content: next stage is "+rs.stage);
        res.redirect("stage_content.html");
        return "";
    }

    public static String continueContent(Request req, Response res) {
        RoteSession rs = RoteSession.getSession(req);
        if (rs.stage == Main.Stage.CONTENT1) {
            res.redirect("stage_test");
            return "";
        }

        // stage 2, decide between treatent and control
        if (rs.getTreatment()) {
            // treat
            res.redirect("stage_speak");
            return "";
        } else {
            // control
            res.redirect("stage_distraction");
            return "";
        }
    }

    public static String speak(Request req, Response res) {
        RoteSession rs = RoteSession.getSession(req);
        rs.stage = Main.Stage.CONTENT2_SPEAK;
        res.redirect("stage_speak.html");
        return "";
    }

    public static String write(Request req, Response res) {
        RoteSession rs = RoteSession.getSession(req);
        rs.stage = Main.Stage.CONTENT2_WRITE;
        res.redirect("stage_write.html");
        return "";
    }

    public static String postWrite(Request req, Response res) {
        String n1 = req.queryParamOrDefault("N1", "NA");
        String n2 = req.queryParamOrDefault("N2", "NA");
        String n3 = req.queryParamOrDefault("N3", "NA");
        String n4 = req.queryParamOrDefault("N4", "NA");

        Main.log(req, RoteSession.getSession(req) + ": Written answers: [" + n1 + "] [" + n2 + "] [" + n3 + "] ["+ n4 + "]");

        res.redirect("stage_distraction");
        return "";
    }

    public static String distraction(Request req, Response res) {
        RoteSession rs = RoteSession.getSession(req);
        rs.stage = Main.Stage.DISTRACTION;
        res.redirect("stage_distraction.html");
        return "";
    }

    public static String test(Request req, Response res) {
        RoteSession rs = RoteSession.getSession(req);
        if (rs.stage == Main.Stage.CONTENT1) {
            rs.stage = Main.Stage.TEST1;
        } else {
            rs.stage = Main.Stage.TEST2;
        }
        res.redirect("stage_test.html");
        return "";
    }

    public static String postTest(Request req, Response res) {
        RoteSession rs = RoteSession.getSession(req);

        String q1 = req.queryParamOrDefault("Q1", "NA");
        String q2 = req.queryParamOrDefault("Q2", "NA");
        String q3 = req.queryParamOrDefault("Q3", "NA");
        String q4 = req.queryParamOrDefault("Q4", "NA");

        Main.logTest(rs + "," + q1 + "," + q2 + "," + q3 + "," + q4);

        if (rs.stage == Main.Stage.TEST1) {
            res.redirect("stage_content");
        } else {
            res.redirect("stage_finished");
        }
        return "ok";
    }

    public static String finished(Request req, Response res) {
        RoteSession rs = RoteSession.getSession(req);
        rs.stage = Main.Stage.FINISHED;
        res.redirect("stage_finished.html");
        return "";
    }
}
