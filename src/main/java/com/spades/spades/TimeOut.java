package com.spades.spades;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class TimeOut {
    private static final Logger LOGGER = LogManager.getLogger("TimeOut.class");





    public TimeOut(String handOrGame, String pageRedirect) {


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
    Timer timer = new Timer("Timer");

    long delay =  1000L;
    timer.schedule(task,delay);


}

    private String gameTimeout() {
        String result = "<html>\n";
        result += "<head></head>\n";
        result += "<body>\n";
        result += "<p>Your game has timed out waiting on a second player, please go back to the main menu</p>\n";
        result += "<a href='./secured/all'>Home</a>";
        result += "</body>\n";
        result += "</html>";
        return result;
    }

    private String handTimeOut(){
        String result = "Player has timedout on this hand, your win this hand.";
        return result;
    }
}
