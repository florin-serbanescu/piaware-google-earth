/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sbstogoogleearth.server;

/**
 * This will be used as notifier for receivers that subsribe to messages feed
 * @author florin
 */
public interface SBSMessageEvent {
    /**
     * Triggered when a new message arrives
     * @param m Message arrived
     */
    public void onNewMessage(SBSMessage m);
}
