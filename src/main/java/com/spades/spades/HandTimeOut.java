package com.spades.spades;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

public class HandTimeOut {
    private static final Logger LOGGER = LogManager.getLogger("HandTimeOut.class");
    private Timer timer = new Timer("HandTimer");
    private String message;

    public HandTimeOut(Long delay){
        message = "";
        TimerTask task = new TimerTask() {




            public void run() {
                LOGGER.error("Your game has timed out waiting on a second player.");

                handTimeout();

            }
        };



        timer.schedule(task,delay);
    }


    public void cancelTimeout(){
        timer.cancel();
    }


    public String getMessage(){
        if (message == ""){
            return "running";
        } else {
            return handTimeout();
        }
    }

    private String handTimeout() {
        message = "<p>Your hand has timed out. Game over.</p>\n";
        message += "<a href='/secured/all'>Home</a>";
        return message;
    }

}
