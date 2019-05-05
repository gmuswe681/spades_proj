package com.spades.spades.resources;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class GeneralErrorController implements ErrorController {

    @RequestMapping("/error")
    public String errorResponse(HttpServletRequest request) {
         final Logger LOGGER = LogManager.getLogger("GeneralErrorController.class");
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");


        String result = "<!DOCTYPE html>\n<html>\n";
        result += "<head><meta charset=\"UTF-8\"/> </head>\n";
        result += "<body>\n";
        result += "<p>Sorry, an error occurred on our end.</p>\n";
        result += "<a href=\"/\">Go to homepage</a>\n";
        result += "</body>\n";
        result += "</html>";

        String error =  String.format("Status code: %s "
                        + "Exception Message: %s",
                statusCode, exception==null? "N/A": exception.getMessage());

        LOGGER.error(error);


        return result;


    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}