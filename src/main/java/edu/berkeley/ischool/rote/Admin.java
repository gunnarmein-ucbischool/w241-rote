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
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import spark.Request;
import spark.Response;
import static spark.Spark.halt;

/**
 *
 * @author gunnar
 */
public class Admin {

    public static String adminLogin(Request req, Response res) {
        RoteSession.startSession(req);
        String pw = req.queryParamOrDefault("pw", "nope");
        if (pw.equalsIgnoreCase(Main.MAIN_PW)) {
            req.session().attribute("admin", "admin");
            res.redirect("admin/adminpage.html");
            return "";
        }
        halt(405, "unauthorized");
        return "";
    }

    public static String getHeaders(Request req) {
        String result = "Rote headers:<br><br>";
        Main.log(req, "Rote: headers route");

        result = req.headers().stream().map((s) -> s + ":" + req.headers(s) + "<br>").reduce(result, String::concat);
        return result;
    }

    public static String forceAssignment(Request req) {
        String s = req.queryParamOrDefault("assignment", "-1");
        int a = Integer.parseInt(s);
        Main.log(req, "Admin: Forcing assignment: " + a);
        Main.forceAssignment = a;
        return "forceAssignment set to:" + a;
    }

    public static Object getLog(Request req, Response res) {

        try {
            File f = new File(Main.logFileName);
            InputStream is = new FileInputStream(f);
            Scanner sc = new Scanner(is);
            String s = "";
            while (sc.hasNextLine()) {
                s += sc.nextLine() + "\n";
            }
            if (s.length() > 5000) {
                return s.substring(s.length() - 5000);
            } else {
                return s;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "fail";
    }

    public static Object getLogFiles(Request req, Response res) {

        res.raw().setContentType("application/octet-stream");
        res.raw().setHeader("Content-Disposition", "attachment; filename=logs.zip");

        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(res.raw().getOutputStream()));
            for (String logFileName : new String[]{Main.logFileName, Main.covFileName, Main.ansFileName, Main.testFileName}) {
                logFileName = Main.logs + logFileName;

                File file = new File(logFileName);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
                ZipEntry zipEntry = new ZipEntry(file.getName());

                zipOutputStream.putNextEntry(zipEntry);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = bufferedInputStream.read(buffer)) > 0) {
                    zipOutputStream.write(buffer, 0, len);
                }
            }
            zipOutputStream.flush();
            zipOutputStream.close();

        } catch (Exception e) {
            Main.log(req, e.getMessage());
            halt(405, "server error");
            return null;
        }

        return null;
    }
}
