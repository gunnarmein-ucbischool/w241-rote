/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.berkeley.ischool.rote;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
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
    private static final String logFileName = "/home/site/wwwroot/webapps/rote_log.txt";
    private static final String csvFileName = "/home/site/wwwroot/webapps/rote_log.csv";
    private static final String mainPW = "powerlifter";

    @Override
    public void init() {
        logger.info("Rote: Initializing");
        System.out.println("Rote: Initializing");
        System.out.println("user.dir:  " + System.getProperty("user.dir"));
        System.out.println("user.home: " + System.getProperty("user.home"));

        logToFile("---------------start of log for new instance");
        logToCSV("\n");

        port(80);
        staticFiles.externalLocation("/home/site/wwwroot/webapps/ROOT/public/");
//        staticFiles.location("/");
        get("new", (req, res) -> getSessionID(req));
        get("headers", (req, res) -> getHeaders(req));
        get("getlogfile", (req, res) -> getLogFile(req, res));
        get("csvfile", (req, res) -> getCSVFile(req, res));
        logger.info("Rote: Fully initialized");
        System.out.println("Rote: Fully initialized");
    }

    private static String getSessionIDRoute(Request req) {

        String s = getSessionID(req);
        logToFile("Rote: Session id requested, result: " + s);
        return s;
    }

    private static String getSessionID(Request req) {
        if (req.session().isNew()) {
            req.session().attribute("rote_session", new RoteSession(req.session()));
            logToFile("Rote: New session: " + ((RoteSession) req.session().attribute("rote_session")).getID());
        }

        String s = ((RoteSession) req.session().attribute("rote_session")).getID();
        return s;
    }

    private static String getHeaders(Request req) {
        String result = "Rote headers:<br><br>";
        log(req, "Rote: headers route");

        result = req.headers().stream().map((s) -> s + ":" + req.headers(s) + "<br>").reduce(result, String::concat);

        return result;
    }

    private static void log(Request req, String s) {
        logToFile("Session: " + getSessionID(req) + ": " + s);
    }

    private static void logToFile(String data) {
        try {
            File f = new File(logFileName);
            if (!f.exists()) {
                f.createNewFile();
            }
            System.out.println("Logging to " + f.getAbsolutePath());

            FileWriter fileWritter = new FileWriter(f.getName(), true);
            BufferedWriter bw = new BufferedWriter(fileWritter);
            bw.write(data);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Object getLogFile(Request req, Response res) {
        String pw = req.params("pw");
        if (!pw.equalsIgnoreCase(mainPW)) {
            throw halt(401, "unauthorized");
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
            throw halt(405, "server error");
        }

        return null;
    }

    private static void logToCSV(String data) {
        try {
            File f = new File(csvFileName);
            if (!f.exists()) {
                f.createNewFile();
            }
            System.out.println("Logging to " + f.getAbsolutePath());

            FileWriter fileWritter = new FileWriter(f.getName(), true);
            BufferedWriter bw = new BufferedWriter(fileWritter);
            bw.write(data);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Object getCSVFile(Request req, Response res) {
        String pw = req.params("pw");
        if (!pw.equalsIgnoreCase(mainPW)) {
            throw halt(401, "unauthorized");
        }

        File file = new File(csvFileName);
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
            throw halt(405, "server error");
        }

        return null;
    }
}
