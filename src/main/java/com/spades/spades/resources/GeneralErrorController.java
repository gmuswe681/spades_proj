package com.spades.spades.resources;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GeneralErrorController implements ErrorController {

    @RequestMapping("/error")
    public String errorResponse() {
        String result = "<html>\n";
        result += "<head></head>\n";
        result += "<body>\n";
        result += "<p>Sorry, an error occurred on our end.</p>\n";
        result += "<a href=\"/\">Go to homepage</a>\n";
        result += "</body>\n";
        result += "</html>";
        return result;
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}