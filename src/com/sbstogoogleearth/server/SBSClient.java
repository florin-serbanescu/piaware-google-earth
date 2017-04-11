/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sbstogoogleearth.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UnknownFormatConversionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Connect to SBS source and retrieve raw data to be parsed
 *
 * @author florin
 */
public class SBSClient extends Thread {

    private boolean shouldStop = false;
    private final List<SBSMessageEvent> observers = Collections.synchronizedList(new ArrayList<SBSMessageEvent>());

    /**
     * Stop this worker thread
     */
    public void stopThread() {
        this.shouldStop = true;
    }

    /**
     * Add observer to be notified on items
     *
     * @param o Notify observer
     */
    public void addObserver(SBSMessageEvent o) {
        synchronized (observers) {
            observers.add(o);

        }
    }

    /**
     * Remove observer to be notified on items
     *
     * @param o Notify observer
     */
    public void removeObserver(SBSMessageEvent o) {
        synchronized (observers) {
            observers.remove(o);

        }
    }

    /**
     * Remove observer to be notified on items
     *
     * @param o Notify observer
     */
    public void notifyObservers(SBSMessage o) {
        synchronized (observers) {

            Iterator<SBSMessageEvent> i = observers.iterator(); // Must be in synchronized block
            while (i.hasNext()) //i.next().;
            {
                i.next().onNewMessage(o);
            }
        }
    }

    @Override
    public void run() {
        //
        // Try to stay connected and retry if not
        //
        while (!shouldStop) {
            System.out.println("Connect to SBS...");
            connect();
            System.out.println("SBS connection terminated");
            //
            // Break imediatly if the case
            //
            if (shouldStop) {
                break;
            } else {
                //
                // Sleep and retry
                //
                try {
                    Thread.sleep(Configuration.getInstance().getSBSRetry());
                } catch (InterruptedException ex) {
                    Logger.getLogger(SBSClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }

    }

    /**
     * Connect function that does the loop for everything
     */
    private void connect() {

        Socket sock;
        PrintWriter out;
        BufferedReader in;

        try {

            sock = new Socket(Configuration.getInstance().getSBSIP(), Configuration.getInstance().getSBSPort());

            out = new PrintWriter(sock.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(
                    sock.getInputStream()));
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        BufferedReader stdIn = new BufferedReader(
                new InputStreamReader(System.in));
        String line;
        SBSMessage m;
        try {
            while ((line = in.readLine()) != null) {
                //
                // Parse and notify observers
                //
                try {
                    m = SBSMessage.parse(line);
                    this.notifyObservers(m);
                } catch (UnknownFormatConversionException ex) {
                    // Ignore this

                }
                //
                // SHould stop
                //
                if (shouldStop) {
                    break;
                }

            }
        } catch (IOException ex) {
            Logger.getLogger(SBSClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        out.close();
        try {
            in.close();
        } catch (IOException ex) {
            Logger.getLogger(SBSClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            stdIn.close();
        } catch (IOException ex) {
            Logger.getLogger(SBSClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            sock.close();
        } catch (IOException ex) {
            Logger.getLogger(SBSClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
