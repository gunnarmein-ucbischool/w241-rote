/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.berkeley.ischool.rote;



import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import static spark.Spark.*;
import spark.servlet.SparkApplication;

//public class Main2 implements SparkApplication {
//
//    private static Logger logger = LoggerFactory.getLogger(Main2.class);
//
//    @Override
//    public void init() {
//        logger.info("Rote: Initializing");
//        System.out.println("Rote: Initializing");
//
//        port(80);
//        get("name", (req, res) -> getLoginName(req));
//        get("headers", (req, res) -> getHeaders(req));
//        get("login", (req, res) -> login(req, res));
//        staticFiles.location("/");
//    }
//
//    private static String login(Request req, Response res) {
//        String login = req.headers("X-MS-CLIENT-PRINCIPAL-NAME");
//        if (login != null) {
//            String red = req.queryParams("url")
//                    + "?" + req.queryParams("passthroughparam") + "=" + req.queryParams("passthrough")
//                    + "&" + req.queryParams("loginparam") + "=" + login;
//            logger.info("EPSAuth: redirecting: " + red);
//            res.redirect(red, 302);
//            return "ok";
//        }
//        throw halt(401);
//    }
//
//    private static String getLoginName(Request req) {
//        String result = req.headers("X-MS-CLIENT-PRINCIPAL-NAME");
//        if (result != null) {
//            return result;
//        }
//        return "";
//    }
//
//    private static String getHeaders(Request req) {
//        System.out.println("Rote headers");
//        String result = "";
//
//        for (String s : req.headers()) {
//            result += s + ":" + req.headers(s) + "<br>";
//        }
//
//        return result;
//    }
//}


public class Main implements SparkApplication {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    
//    Main2() {
//        System.out.println("");
//        System.out.println("");
//        System.out.println("");
//        System.out.println("Rote: Spark filter 'Rote' constructed");
//        System.out.println("");
//        System.out.println("");
//        System.out.println("");
//    }

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
        String result = "Rote headers:<br><br>";
        
        System.out.println("Rote: headers route");

        result = req.headers().stream().map((s) -> s + ":" + req.headers(s) + "<br>").reduce(result, String::concat);

        return result;
    }
}

