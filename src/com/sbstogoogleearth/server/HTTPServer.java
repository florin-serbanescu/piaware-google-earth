/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sbstogoogleearth.server;

import fi.iki.elonen.NanoHTTPD;
import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;
import java.io.IOException;
import java.util.Map;
import java.util.UnknownFormatConversionException;

/**
 * HTTP server to handle requests
 *
 * @author florin
 */
public class HTTPServer extends NanoHTTPD {

    //
    // Selected aircraft id
    //
    private String aircraftSelection = "";
    //
    // kml content to be served
    //
    private String kml = "";

    //
    // Keep last message
    //
    private SBSMessage last = null;

    public HTTPServer() {
        super(Configuration.getInstance().getHTTPIP(), Configuration.getInstance().getHTTPPort());

    }

    /**
     * Do the actual start operations for this server
     */
    public void start() throws IOException {
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("HTTP Server started on ip:port:"
                + Configuration.getInstance().getHTTPIP() + ":"
                + Integer.toString(Configuration.getInstance().getHTTPPort()));

        //
        // Add listener for data
        //
        Main.getSBSClient().addObserver(new SBSMessageEvent() {
            @Override
            public void onNewMessage(SBSMessage m) {

                //
                // See if this is KML suitable and build content
                //
                if (m.isKMLSuitable()) {

                    //
                    // Check if this is our plane
                    //
                    if (m.aircraftID.equals(aircraftSelection)) {
                        if (last != null) {
                            //
                            // Calculate heading
                            //
                            int heading = SBSMessage.calculateHeading(last, m);

                            //
                            // Tolerance 5 degrees or if zero keep last
                            //
                            if ((Integer.parseInt(last.heading) != 0) && (Math.abs(heading - Integer.parseInt(last.heading)) > 10)) {
                                heading = Integer.parseInt(last.heading);
                            }
                            if (heading == 0) {
                                heading = Integer.parseInt(last.heading);
                            }
                            m.heading = Integer.toString(heading);
                        }

                        //
                        // Create KML
                        //
                        try {
                            kml = m.toKML();
                            last = m;
                        } catch (UnknownFormatConversionException ex) {

                        }
                    }
                }
            }

        });
    }

    @Override
    public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {

        Map<String, String> parms = session.getParms();

        //
        // Handle select aircraft by id
        //
        if (parms.get("select") != null) {
            this.aircraftSelection = parms.get("select");
            
            //
            // Invalidate last aircraft
            //
            last = null;

            Response res = newFixedLengthResponse("Selected " + this.aircraftSelection);
            res.addHeader("Access-Control-Allow-Origin", "*");

            return res;
        }
        //
        // Normal serve of last KLM
        //

        return newFixedLengthResponse(this.kml);
    }

}
