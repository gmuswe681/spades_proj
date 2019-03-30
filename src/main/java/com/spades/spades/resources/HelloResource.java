package com.spades.spades.resources;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/")
@RestController
public class HelloResource {

    @GetMapping("/")
    public String hello() {
        String result = "<html>\n";
        result += "<head></head>\n";
        result += "<body>\n";
        result += "<p>Hello World!</p>\n";
        result += "<a href=\"/secured/all\">Login</a>\n";
        result += "</body>\n";
        result += "</html>";
        return result;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/secured/all")
    public String securedHello()
    {
        String result = "<html>\n";
        result += "<head></head>\n";
        result += "<body>\n";
        result += "<p>Secured Hello!</p>\n";
        result += "<a href=\"/logout\">Logout</a>\n";
        result += "</body>\n";
        result += "</html>";
        return result;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/secured/alternate")
    public String alternate() {
        String result = "<html>\n";
        result += "<head></head>\n";
        result += "<body>\n";
        result += "<p>Alternate</p>\n";
        result += "<a href=\"/logout\">Logout</a>\n";
        result += "</body>\n";
        result += "</html>";
        return result;
    }
}