package com.spades.spades;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

public final class HandTimeOut {
    private static final Logger LOGGER = LogManager.getLogger("HandTimeOut.class");
    private Timer timer = new Timer("HandTimer");
    private String message;
    private boolean hasTimedOut;

    public HandTimeOut(Long delay){
        message = "";
        hasTimedOut = false;
        TimerTask task = new TimerTask() {
            public void run() {
                LOGGER.error("Your hand has timed out waiting on a second player.");

                handTimeout();
            }
        };

        timer.schedule(task,delay);
    }


    public void cancelTimeout(){
        timer.cancel();
    }

    public boolean getTimedOut(){
        return hasTimedOut;
    }

    public String getMessage(){
        if (message.equals("")){
            return "running";
        } else {
            return handTimeout();
        }
    }

    private String handTimeout() {
        hasTimedOut = true;

        message = "<p>Your hand has timed out. Game over.</p>\n";
        message += "<a href='/secured/all'>Home</a>";
        return message;
    }

}
