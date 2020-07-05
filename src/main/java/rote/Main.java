/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import static spark.Spark.*;
import spark.servlet.SparkApplication;

/**
 *
 * @author gmein
 */
public class Main implements SparkApplication {

//    public static void main(String[] args) {
//        Main app = new Main();
//        app.init();
////        try {
////            // loop until interrupted
////            while (true) {
////                Thread.sleep(1000);
////            }
////        } catch (Exception e) {
////            // catch and be quiet, then exit
////        }
//    }
    private static Logger logger = LoggerFactory.getLogger(Main.class);

    @Override
    public void init() {
        logger.info("Rote: Initializing");
        System.out.println("Rote: Initializing");

        port(80);
        get("new", (req, res) -> getSessionID(req));
        get("headers", (req, res) -> getHeaders(req));
        staticFiles.location("/");
        
        
        logger.info("Rote: Fully initialized");
        System.out.println("Rote: Fully initialized");
    }

    private static String getSessionID(Request req) {
        if (req.session().isNew()) {
            req.session().attribute("rote_session", new RoteSession(req.session()));
        }

        return ((RoteSession) req.session().attribute("rote_session")).getID();
    }

    private static String getHeaders(Request req) {
        String result = "";

        result = req.headers().stream().map((s) -> s + ":" + req.headers(s) + "<br>").reduce(result, String::concat);

        return result;
    }
}
