package com.spades.spades;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Timer;
import java.util.TimerTask;


public class GameTimeOut {
    private static final Logger LOGGER = LogManager.getLogger("GameTimeOut.class");
    private Timer timer = new Timer("GameTimer");
    private String message;
    public GameTimeOut(Long delay) {

    message = "";
    TimerTask task = new TimerTask() {




        public void run() {
            LOGGER.error("Your game has timed out waiting on a second player.");

                gameTimeout();

        }
    };



    timer.schedule(task,delay);


}


    public void cancelTimeout(){
        timer.cancel();
    }


    public String getMessage(){
        if (message.equals("")){
            return "running";
        } else {
           return gameTimeout();
        }
    }

    private String gameTimeout() {
        message = "<p>Your game has timed out waiting on a second player, please go back to the main menu</p>\n";
        message += "<a href='/secured/all'>Home</a>";
        return message;
    }


}
