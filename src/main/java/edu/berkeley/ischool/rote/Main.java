/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.berkeley.ischool.rote;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import static spark.Spark.*;
import spark.servlet.SparkApplication;
import com.google.gson.Gson;

public class Main implements SparkApplication {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private final static Gson gson = new Gson();

    public static String logFileName;
    public static String covFileName;
    public static String ansFileName;
    public static String testFileName;
    public static String logs;
    public static final String MAIN_PW = "powerlifter";

    public static int forceAssignment = -1;

    @Override
    public void init() {
        String files;

        logger.info("Rote: Initializing");
        System.out.println("Rote: Initializing");
        System.out.println("user.dir:  " + System.getProperty("user.dir"));
        System.out.println("user.home: " + System.getProperty("user.home"));

        if (System.getProperty("user.home").startsWith("/Users/")) {
            // on Mac
            port(8080);
            logs = System.getProperty("user.home");
            //staticFiles.location("public");
            files = System.getProperty("user.dir").substring(0, System.getProperty("user.dir").length() - 3) + "webapps/Rote-1.0";
        } else {
            // on Azure
            port(80);
            logs = "/home";
            files = "/home/site/wwwroot/webapps/ROOT";
        }

        logFileName = logs + "/rote_log.txt";
        covFileName = logs + "/rote_cov.csv";
        ansFileName = logs + "/rote_ans.csv";
        testFileName = logs + "/rote_test.csv";
        staticFiles.externalLocation(files + "/public/");
        Content.readContent(files + "/content1.csv");

        logToFile(logFileName, "\n\n--------------- start of log for new instance");
        logToFile(logFileName, "\nRote: Initializing");
        logToFile(logFileName, "\nuser.dir:  " + System.getProperty("user.dir"));
        logToFile(logFileName, "\nuser.home: " + System.getProperty("user.home"));
        logToFile(logFileName, "\nStatic files: " + files);
        System.out.println("Static files: " + files);
        logToFile(logFileName, "\nSystem start time: " + RoteSession.systemStart);

        // admin
        before("admin/*", (req, res) -> {
            if (!req.session().attribute("admin").equals("admin")) {
                halt(401, "Go Away!");
            }
        });
        post("adminlogin", (req, res) -> Admin.adminLogin(req, res));
        get("admin/getlogfile", (req, res) -> Admin.getLogFiles(req, res));
        get("admin/headers", (req, res) -> Admin.getHeaders(req));
        put("admin/forceassignment", (req, res) -> Admin.forceAssignment(req));
        get("admin/getlog", (req, res) -> Admin.getLog(req, res));
        get("admin/start", (req, res) -> {
            res.redirect("../index.html");
            return "ok";
        });

        // main functionality
        get("get_forcedsettings", (req, res) -> "" + Main.forceAssignment);
        get("current_stage", (req, res) -> Stages.currentStage(req, res));
        get("stage_info", (req, res) -> Stages.start(req, res));
        post("post_info", (req, res) -> Stages.postInfo(req, res));

        get("stage_content", (req, res) -> Stages.content(req, res));
        get("continue_content", (req, res) -> Stages.continueContent(req, res));
        get("get_content", (req, res) -> RoteSession.getContent(req), Main::render);
        get("stage_distraction", (req, res) -> Stages.distraction(req, res));
        get("stage_test", (req, res) -> Stages.test(req, res));
        post("post_test", (req, res) -> Stages.postTest(req, res));

        get("stage_readagain", (req, res) -> Stages.readAgain(req, res));

        get("stage_speak", (req, res) -> Stages.speak(req, res));
        get("stage_write", (req, res) -> Stages.write(req, res));
        post("post_write", (req, res) -> Stages.postWrite(req, res));

        get("stage_finished", (req, res) -> Stages.finished(req, res));

        // done
        logger.info("Rote: Fully initialized");
        System.out.println("Rote: Fully initialized");
        logToFile(logFileName, "\nRote: Fully initialized");
        logToFile(logFileName, "\n---------------\n");
    }

    public boolean checkDoneWith(Request req, Stages.Stage correct) {
        return (RoteSession.getSession(req).stage != correct);
    }

    //
    // util
    //
    static private String render(Object o) {
        return gson.toJson(o);
    }

    public static void log(Request req, String s) {
        logToFile(logFileName, RoteSession.getSessionID(req) + ": " + s + "\n");
    }

    public static void log(RoteSession rs, String s) {
        logToFile(logFileName, rs.getID() + ": " + s + "\n");
    }

    public static void logCov(String s) {
        logToFile(covFileName, s);
    }

    public static void logAnswers(String s) {
        logToFile(ansFileName, s);
    }

    public static void logTest(String s) {
        logToFile(testFileName, s);
    }

    private static void logToFile(String logFileName, String data) {
        synchronized (Main.class) {
            try {
                File f = new File(logFileName);
                if (!f.exists()) {
                    f.createNewFile();
                }
                //System.out.println("Logging to " + f.getAbsolutePath() + ": " + data);

                FileWriter fw = new FileWriter(f, true);
                fw.write(data);
                fw.flush();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
