package com.spades.spades.service;

import com.spades.spades.GameTimeOut;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public final class GameTimerService {

    private static Map<Integer, GameTimeOut> timerMap = new HashMap<>();


    public GameTimeOut getTimer(Integer id) {
        return timerMap.get(id);
    }

    public void setTimer(Integer id, GameTimeOut gameTimeOut) {
        timerMap.put(id, gameTimeOut);
    }

}
