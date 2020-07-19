/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.berkeley.ischool.rote;

import java.util.List;
import spark.Request;
import spark.Response;

/**
 *
 * @author gunnar
 */
public class Stages {

    public static enum Stage {
        INVALID,
        START,
        INFO,
        CONTENT1,
        CONTENT2_READAGAIN,
        DISTRACTION1,
        TEST1,
        CONTENT2,
        CONTENT2_READ,
        CONTENT2_SPEAK,
        CONTENT2_WRITE,
        DISTRACTION2,
        TEST2,
        RESULTS,
        FINISHED;
    }

    public static String currentStage(Request req, Response res) {
        RoteSession rs = RoteSession.getSession(req);

        // are we in a plausible stage for this page?
        String possible = req.queryParamOrDefault("stage", "");
        Main.log(req, "Checking, current stage: " + rs.stage);
        String[] stages = possible.split(",");
        for (String stage : stages) {
            stage = stage.trim();
            Main.log(req, "  Plausible stage: " + stage);
            if (stage.equals(rs.stage.toString())) {
                return "ok";
            }
        }

        // no, recommend redirects
        String url;
        switch (rs.stage) {
            case START:
                url = "index.html";
                break;
            case INFO:
                url = "stage_info.html";
                break;
            case CONTENT1:
                url = "stage_content.html";
                break;
            case CONTENT2_READAGAIN:
                url = "stage_content.html";
                break;
            case DISTRACTION1:
                url = "stage_distraction.html";
                break;
            case TEST1:
                url = "stage_test.html";
                break;
            case CONTENT2:
                url = "stage_content.html";
                break;
            case CONTENT2_SPEAK:
                url = "stage_speak.html";
                break;
            case CONTENT2_WRITE:
                url = "stage_write.html";
                break;
            case DISTRACTION2:
                url = "stage_distraction.hml";
                break;
            case TEST2:
                url = "stage_test.html";
                break;
            case FINISHED:
                url = "stage_finished.html";
                break;
            case INVALID:
            default:
                url = "index.html";
        }
        Main.log(req, "Wrong stage, redirecting to: " + url);
        return url;
    }

    public static String start(Request req, Response res) {
        // startSession will assign treat/control and establish the context
        Boolean success = RoteSession.startSession(req);
        RoteSession rs = RoteSession.getSession(req);
        if (success) {
            rs.setStage(Stage.INFO);
            res.redirect("stage_info.html");
        } else {
            rs.setStage(Stage.INFO);
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
        String practice = req.queryParamOrDefault("Practice", "NA");
        String reading = req.queryParamOrDefault("Reading", "NA");

        rs.assignTreatControl();

//        System.out.println(age);
//        System.out.println(gender);
//        System.out.println(knowledge);
//        System.out.println(reading);
        Main.logCov(rs + "," + age + "," + gender + "," + practice + "," + reading);

        res.redirect("stage_content");
        return "ok";
    }

    public static String content(Request req, Response res) {
        RoteSession rs = RoteSession.getSession(req);
        if (rs.stage == Stage.INFO) {
            rs.setStage(Stage.CONTENT1);
        } else if (rs.stage == Stage.TEST1) {
            rs.setStage(Stage.CONTENT2);
        } else {
            Main.log(req, "Invalid stage in 'content': " + rs.stage);
            return "";
        }

        Main.log(req, "content: next stage is " + rs.stage);
        res.redirect("before_content.html");
        return "";
    }

    public static String continueContent(Request req, Response res) {
        RoteSession rs = RoteSession.getSession(req);
        if (rs.stage == Stage.CONTENT1 || rs.stage == Stage.DISTRACTION1) {
            Main.log(req, "After content1, redirecting to distraction");
            res.redirect("stage_distraction");
            return "";
        }

        if (rs.stage == Stage.CONTENT2 || rs.stage == Stage.DISTRACTION2) {
            // stage 2, decide between treatent and control
            if (rs.getTreatment()) {
                // treat
                Main.log(req, "After content2 in TREATMENT, redirecting to speak");
                res.redirect("stage_speak");
                return "";
            } else {
                // control
                if (rs.stage == Stage.CONTENT2_READAGAIN) {
                    Main.log(req, "After content2 in CONTROL, redirecting to disctraction");
                    res.redirect("stage_distraction");
                } else {
                    Main.log(req, "After content2 in CONTROL, redirecting to readagain");
                    res.redirect("stage_readagain");
                }
                return "";
            }
        }

        res.redirect("index.html");
        return "wrong stage";
    }

    public static String readAgain(Request req, Response res) {
        RoteSession rs = RoteSession.getSession(req);
        rs.setStage(Stage.CONTENT2_READAGAIN);
        res.redirect("before_readagain.html");
        return "";
    }

    public static String speak(Request req, Response res) {
        RoteSession rs = RoteSession.getSession(req);
        rs.setStage(Stage.CONTENT2_SPEAK);
        res.redirect("before_speak.html");
        return "";
    }

    public static String write(Request req, Response res) {
        RoteSession rs = RoteSession.getSession(req);
        rs.setStage(Stage.CONTENT2_WRITE);
        res.redirect("before_write.html");
        return "";
    }

    public static String postWrite(Request req, Response res) {
        String n11 = req.queryParamOrDefault("N11", "NA");
        String n12 = req.queryParamOrDefault("N12", "NA");
        String n13 = req.queryParamOrDefault("N13", "NA");
        String n14 = req.queryParamOrDefault("N14", "NA");

        String n21 = req.queryParamOrDefault("N21", "NA");
        String n22 = req.queryParamOrDefault("N22", "NA");
        String n23 = req.queryParamOrDefault("N23", "NA");
        String n24 = req.queryParamOrDefault("N24", "NA");
        Main.log(req, RoteSession.getSession(req) + ": Written answers: [" + n21 + "] [" + n22 + "] [" + n23 + "] [" + n24 + "]"
                + "[" + n21 + "] [" + n22 + "] [" + n23 + "] [" + n24 + "]");

        res.redirect("stage_distraction");
        return "";
    }

    public static String distraction(Request req, Response res) {
        RoteSession rs = RoteSession.getSession(req);
        if (rs.stage == Stage.CONTENT1 || rs.stage == Stage.DISTRACTION1) {
            rs.setStage(Stage.DISTRACTION1);
            res.redirect("before_distraction1.html");
        } else {
            rs.setStage(Stage.DISTRACTION2);
            res.redirect("before_distraction2.html");
        }
        return "";
    }

    public static String test(Request req, Response res) {
        RoteSession rs = RoteSession.getSession(req);
        Main.log(req, "In test, stage is " + rs.stage);
        if (rs.stage == Stage.DISTRACTION1) {
            rs.setStage(Stage.TEST1);
        } else {
            rs.setStage(Stage.TEST2);
        }
        res.redirect("before_test.html");
        return "";
    }

    public static String postTest(Request req, Response res) {
        RoteSession rs = RoteSession.getSession(req);

        List<Content.Item> content;
        String test;
        if (rs.stage == Stage.TEST1) {
            content = rs.content1;
            test = "baseline";
        } else if (rs.stage == Stage.TEST2) {
            content = rs.content2;
            test = "experiment";
        } else {
            Main.log(req, "Wrong stage for recording test results: " + rs.stage);
            return "fail";
        }

        String t = "";
        for (int i = 0; i < Content.NUM_ITEMS / 2; i++) {
            t += content.get(i).id + ",";
            Main.log(req, " item id: "+content.get(i).id );
            for (int q = 0; q < Content.NUM_QUESTIONS; q++) {
                List<Content.Question> qs = content.get(i).questions;
                t += req.queryParamOrDefault("Q" + (i + 1) + (q + 1), "NA") + "," + (qs.get(q).correctAnswer+1) + ",";
                Main.log(req, "  "+req.queryParamOrDefault("Q" + (i + 1) + (q + 1), "NA") + "," + (qs.get(q).correctAnswer+1));
            }
        }

        String result = rs + "," +System.currentTimeMillis() + "," + t;

        Main.logTest(result);
        Main.log(req, "Test results recorded for stage " + rs.stage + ":");
        Main.log(req, " "+result);

        if (rs.stage == Stage.TEST1) {
            res.redirect("stage_content");
        } else {
            res.redirect("stage_finished");
        }
        return "ok";
    }

    public static String finished(Request req, Response res) {
        RoteSession rs = RoteSession.getSession(req);
        rs.setStage(Stage.FINISHED);
        res.redirect("stage_finished.html");
        return "";
    }
}
