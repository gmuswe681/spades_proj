package com.spades.spades;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Timer;
import java.util.TimerTask;


public class TimeOut {
    private static final Logger LOGGER = LogManager.getLogger("TimeOut.class");
    private Timer timer = new Timer("Timer");
    private String message;
    public TimeOut(String handOrGame, Long delay) {

    message = "";
    TimerTask task = new TimerTask() {
        public void run() {
            LOGGER.error(handOrGame + " has timed out.");
            if(handOrGame == "game") {
                gameTimeout();
            }else {
                handTimeOut();
            }

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
           return gameTimeout();
        }
    }

    private String gameTimeout() {
        message = "<p>Your game has timed out waiting on a second player, please go back to the main menu</p>\n";
        message += "<a href='./secured/all'>Home</a>";
        return message;
    }

    private String handTimeOut(){
        String result = "Player has timedout on this hand, your win this hand.";
        return result;
    }
}
