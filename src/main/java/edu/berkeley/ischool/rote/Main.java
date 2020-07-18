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

    public static enum Stage {
        INVALID,
        START,
        INFO,
        CONTENT1,
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

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private final static Gson gson = new Gson();

    private static String logFileName;
    private static String covFileName;
    private static String testFileName;
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
        testFileName = logs + "/rote_test.csv";
        staticFiles.externalLocation(files + "/public/");
        Content.readContent(files + "/content1.csv");
        
        logToFile(logFileName, "\nRote: Initializing");
        logToFile(logFileName, "user.dir:  " + System.getProperty("user.dir"));
        logToFile(logFileName, "user.home: " + System.getProperty("user.home"));
        logToFile(logFileName, "Static files: " + files);
        System.out.println("Static files: " + files);

        logToFile(logFileName, "\n\n---------------start of log for new instance\n");

        // admin
        before("admin/*", (req, res) -> {
            if (!req.session().attribute("admin").equals("admin")) {
                halt(401, "Go Away!");
            }
        });
        post("adminlogin", (req, res) -> Admin.adminLogin(req, res));
        get("admin/getlogfile", (req, res) -> Admin.getLogFile(req, res));
        get("admin/headers", (req, res) -> Admin.getHeaders(req));
        put("admin/forceassignment", (req, res) -> Admin.forceAssignment(req));
        get("admin/getcatalina", (req, res) -> Admin.getCatalina(req,res));
        get("admin/start", (req, res) -> {
            res.redirect("../index.html");
            return "ok";
        });

        // main functionality
        get("stage_info", (req, res) -> Stages.start(req, res));
        post("post_info", (req, res) -> Stages.postInfo(req, res));

        get("stage_content", (req, res) -> Stages.content(req, res));
        get("continue_content", (req, res) -> Stages.continueContent(req, res));
        get("get_content", (req, res) -> RoteSession.getContent(req), Main::render);
        get("stage_distraction", (req, res) -> Stages.distraction(req, res));
        get("stage_test", (req, res) -> Stages.test(req, res));
        post("post_test", (req, res) -> Stages.postTest(req, res));

        get("stage_speak", (req, res) -> Stages.speak(req, res));
        get("stage_write", (req, res) -> Stages.write(req, res));
        post("post_write", (req, res) -> Stages.postWrite(req, res));

        get("stage_finished", (req, res) -> Stages.finished(req, res));

        // done
        logger.info("Rote: Fully initialized");
        System.out.println("Rote: Fully initialized");
    }

    public boolean checkDoneWith(Request req, Stage correct) {
        return (RoteSession.getSession(req).stage != correct);
    }

    //
    // util
    //
    static private String render(Object o) {
        return gson.toJson(o);
    }

    public static void log(Request req, String s) {
        logToFile(logFileName, "Session: " + RoteSession.getSessionID(req) + ": " + s);
    }

    public static void logCov(String s) {
        logToFile(covFileName, s);
    }

    public static void logTest(String s) {
        logToFile(testFileName, s);
    }

    private static void logToFile(String logFileName, String data) {
        try {
            File f = new File(logFileName);
            if (!f.exists()) {
                f.createNewFile();
            }
            System.out.println("Logging to " + f.getAbsolutePath() + ": " + data);

            FileWriter fw = new FileWriter(f, true);
            fw.write(data);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
