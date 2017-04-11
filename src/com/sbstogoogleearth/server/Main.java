/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sbstogoogleearth.server;

import java.io.IOException;

/**
 * Main entry point for the server
 *
 * @author florin
 */
public class Main {

    //
    // Public object accesible from different places
    //
    private static HTTPServer http;
    private static SBSClient sbsClient;

    public Main() {
        //
        // Load configuration
        //
        Configuration.getInstance();

        //
        // Init
        //
        sbsClient = new SBSClient();
        http = new HTTPServer();
        

        //
        // Start objects
        //
        sbsClient.start();
        try {
            http.start();
        } catch (IOException ex) {
            System.exit(1);
        }
        

        //
        // End of start
        //
        System.out.println("Service started.");
    }

    /**
     * Static var for sbs client
     *
     * @return SBS client handling
     */
    public static SBSClient getSBSClient() {
        return sbsClient;
    }

    /**
     * Get HTTP server object
     *
     * @return HTTP server object
     */
    public static HTTPServer getHTTPServer() {
        return http;
    }

    public static void main(String[] args) {
        Main app;
        app = new Main();

    }

}
