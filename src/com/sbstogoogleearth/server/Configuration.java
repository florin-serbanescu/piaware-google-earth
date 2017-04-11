/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sbstogoogleearth.server;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * Loads and serve configuration for this service
 *
 * @author florin
 */
public class Configuration {

    //
    // Singletone instance
    //
    private static Configuration instance = null;
    //
    // Config file
    //
    private Properties conf = null;

    private Configuration() {
        conf = new Properties();
        try {
            conf.load(new FileInputStream("config.properties"));
        } catch (Exception ex) {
            ex.printStackTrace();
            conf = null;
        }
    }

    /**
     * Singletone implementation for configuration
     *
     * @return Configuration object
     */
    public static Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }

        return instance;
    }

    /**
     * Get a generic int value
     *
     * @param name Name of value to get
     * @param defaultValue Default value
     * @return Configuration
     */
    private int getIntValue(String name, int defaultValue) {
        if (conf == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(conf.getProperty(name));
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    /**
     * Get a generic string value
     *
     * @param name Name of value to get
     * @param defaultValue Default value
     * @return Configuration
     */
    private String getStringValue(String name, String defaultValue) {
        if (conf == null) {
            return defaultValue;
        }

        return conf.getProperty(name);

    }

    public String getHTTPIP() {
        return this.getStringValue("http_ip", "127.0.0.1");
    }

    public int getHTTPPort() {
        return this.getIntValue("http_port", 8088);
    }

    public String getSBSIP() {
        return this.getStringValue("sbs_ip", "127.0.0.1");
    }

    public int getSBSPort() {
        return this.getIntValue("sbs_port", 30003);
    }
    public int getSBSRetry() {
        return this.getIntValue("sbs_connection_retry", 5000);
    }
    
    
    

}
