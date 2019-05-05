package com.spades.spades.service;

import com.spades.spades.HandTimeOut;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public final class HandTimerService {
    private static Map<Integer, HandTimeOut> timerMap = new HashMap<>();

    public HandTimeOut getTimer(Integer id){
        return timerMap.get(id);
    }

    public void setTimer(Integer id, HandTimeOut handTimeOut){
        timerMap.put(id, handTimeOut);
    }
}
