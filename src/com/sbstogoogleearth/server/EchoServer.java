/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sbstogoogleearth.server;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to test an offline source
 *
 * @author florin
 */
public class EchoServer {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(30003);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 10007.");
            System.exit(1);
        }

        boolean always = true;
        while (always) {
            Socket clientSocket = null;
            System.out.println("Waiting for connection.....");

            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            System.out.println("Connection successful");
            System.out.println("Waiting for input.....");

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),
                    true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;

            //
            // Read the file and output
            //
            // Open the file
            

            while (clientSocket.isConnected()) {
                FileInputStream fstream = new FileInputStream("sbs_sample.txt");
                BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

                String strLine;

                //Read File Line By Line
                while ((strLine = br.readLine()) != null) {
                    // Print the content on the console
                    out.println(strLine);
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(EchoServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                //Close the input stream
                br.close();
            }

            /*while ((inputLine = in.readLine()) != null) {
            System.out.println("Server: " + inputLine);
            out.println(inputLine);

            if (inputLine.equals("Bye.")) {
                break;
            }
        }*/
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }
}
