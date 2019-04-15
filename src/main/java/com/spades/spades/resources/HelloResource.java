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
        result += "<p>Welcome to Spades. Please Create a User or Login.</p>\n";
        result += "<a href=\"/secured/all\">Login</a>\n";
        result += "<a href=\"/createuserpage.html\">Create User</a>\n";
        result += "</body>\n";
        result += "</html>";

        return result;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/secured/all")
    public String securedHello()
    {
        String result = "<html>\n";
        result += "<head></head>\n";
        result += "<body>\n";
        result += "<p>Secured Hello!</p>\n";

        result += "<form><button type=\"submit\" formmethod=\"post\" formaction=\"/secured/all/creategame\">Create Game</button></form>";
        result += "<form><button type=\"submit\" formmethod=\"get\" formaction=\"/secured/all/retrieveopengames/\">Find Games</button></form>";

        result += "<a href=\"/logout\">Logout</a>\n";
        result += "</body>\n";
        result += "</html>";
        return result;
    }
}