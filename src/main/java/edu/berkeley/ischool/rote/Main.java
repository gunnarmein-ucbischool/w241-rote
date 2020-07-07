/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.berkeley.ischool.rote;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import static spark.Spark.*;
import spark.servlet.SparkApplication;

public class Main implements SparkApplication {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final String logFileName = "/home/rote_log.txt";
    private static final String covFileName = "/home/rote_cov.csv";
    private static final String testFileName = "/home/rote_test.csv";
    private static final String mainPW = "powerlifter";

    @Override
    public void init() {
        init(true);
    }

    static void main(String[] args) {
        Main m = new Main();
        m.init(false);
        while (true) {
            try {
                Thread.currentThread().sleep(1000);
            } catch (Exception e) {
                break;
            }
        }
    }

    public void init(boolean onAzure) {
        logger.info("Rote: Initializing");
        System.out.println("Rote: Initializing");
        System.out.println("user.dir:  " + System.getProperty("user.dir"));
        System.out.println("user.home: " + System.getProperty("user.home"));

        logToFile(logFileName, "\n\n---------------start of log for new instance\n");
        logToFile(covFileName, "\n");
        logToFile(testFileName, "\n");

        port(80);
        if (onAzure) {
            staticFiles.externalLocation("/home/site/wwwroot/webapps/ROOT/public/");
        } else {
            staticFiles.location("/public");
        }
        //get("new", (req, res) -> getSessionID(req));
        get("headers", (req, res) -> getHeaders(req));

        // admin
        before("/admin/*", (req, res) -> {
            if (!req.session().attribute("admin").equals("admin")) {
                halt(401, "Go Away!");
            }
        });
        post("/adminlogin", (req, res) -> adminLogin(req, res));
        get("getlogfile", (req, res) -> getLogFile(req, res));

        // main functionality
        get("/start", (req, res) -> start(req, res));
        post("/submitinfo", (req, res) -> postInfo(req, res));
        post("/checkanswers", (req, res) -> checkAnswers(req, res));
        get("/distraction", (req, res) -> distraction(res));
        get("/test", (req, res) -> test(res));
        post("/submittest", (req, res) -> postTest(req, res));

        // done
        logger.info("Rote: Fully initialized");
        System.out.println("Rote: Fully initialized");
    }

    private static String getSessionIDRoute(Request req) {
        String s = getSessionID(req);
        logToFile(logFileName, "Rote: Session id requested, result: " + s);
        return s;
    }

    private static boolean startSession(Request req) {
        // if (req.session().isNew()) {
        req.session().attribute("rote_session", new RoteSession(req.session()));
        logToFile(logFileName, "Rote: New session: " + ((RoteSession) req.session().attribute("rote_session")).getID());
        return true;
        // }
        // return false;
    }

    private String distraction(Response res) {
        res.redirect("distraction.html");
        return "";
    }

    private String test(Response res) {
        res.redirect("test.html");
        return "";
    }

    private static String getSessionID(Request req) {
        return getSession(req).getID();
    }

    private static String getHeaders(Request req) {
        String result = "Rote headers:<br><br>";
        log(req, "Rote: headers route");

        result = req.headers().stream().map((s) -> s + ":" + req.headers(s) + "<br>").reduce(result, String::concat);
        return result;
    }

    private static String start(Request req, Response res) {
        // this will assign treat/control and establish the context
        if (startSession(req)) {
            res.redirect("info.html");
        } else {
            res.redirect("info.html");
//            res.redirect("error.html?reason=sessionexists");
        }
        return "started";
    }

    private static String adminLogin(Request req, Response res) {
        String pw = req.queryParamOrDefault("pw", "nope");
        if (pw.equalsIgnoreCase(mainPW)) {
            req.session().attribute("admin", "admin");
            res.redirect("/admin/adminpage.html");
            return "";
        }
        halt(405, "unauthorized");
        return "";
    }

    private static String postInfo(Request req, Response res) {
        RoteSession rs = getSession(req);
        String age = req.queryParamOrDefault("Age", "NA");
        String gender = req.queryParamOrDefault("Gender", "NA");
        String knowledge = req.queryParamOrDefault("Knowledge", "NA");
        String reading = req.queryParamOrDefault("Reading", "NA");

        System.out.println(age);
        System.out.println(gender);
        System.out.println(knowledge);
        System.out.println(reading);

        logToFile(covFileName, rs + "," + age + "," + gender + "," + knowledge + "," + reading);

        res.redirect(rs.getTreatment() ? "memorization.html" : "material.html");
        return "ok";
    }

    private static String checkAnswers(Request req, Response res) {
        String n1 = req.queryParamOrDefault("N1", "NA");
        String n2 = req.queryParamOrDefault("N2", "NA");
        String n3 = req.queryParamOrDefault("N3", "NA");

        log(req, "[" + n1 + "] [" + n2 + "] [" + n3 + "] ");

        res.redirect("/distraction");
        return "";
    }

    private static String postTest(Request req, Response res) {
        RoteSession rs = getSession(req);
        String q1 = req.queryParamOrDefault("Q1", "NA");
        String q2 = req.queryParamOrDefault("Q2", "NA");
        String q3 = req.queryParamOrDefault("Q3", "NA");

        logToFile(testFileName, rs + "," + q1 + "," + q2 + "," + q3);

        res.redirect("thankyou.html");
        return "ok";
    }

    //
    // util
    //
    private static RoteSession getSession(Request req) {
        return (RoteSession) req.session().attribute("rote_session");
    }

    private static void log(Request req, String s) {
        logToFile(logFileName, "Session: " + getSessionID(req) + ": " + s);
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

    private static Object getLogFile(Request req, Response res) {
        String logFileName = req.queryParams("logfilename");
        String pw = req.queryParams("pw");
        if (pw == null || !pw.equalsIgnoreCase(mainPW)) {
            System.out.println("Attempted pw: '" + pw + "'");
            halt(401, "unauthorized");
            return null;
        }

        File file = new File(logFileName);
        res.raw().setContentType("application/octet-stream");
        res.raw().setHeader("Content-Disposition", "attachment; filename=" + file.getName() + ".zip");

        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(res.raw().getOutputStream()));
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
            ZipEntry zipEntry = new ZipEntry(file.getName());

            zipOutputStream.putNextEntry(zipEntry);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = bufferedInputStream.read(buffer)) > 0) {
                zipOutputStream.write(buffer, 0, len);
            }
            zipOutputStream.flush();
            zipOutputStream.close();

        } catch (Exception e) {
            log(req, e.getMessage());
            halt(405, "server error");
            return null;
        }

        return null;
    }
}
