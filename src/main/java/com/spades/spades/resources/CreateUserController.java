package com.spades.spades.resources;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/createuser")
@RestController
public class CreateUserController {

    @RequestMapping(value = "", method = RequestMethod.POST)
    public String submitUser(HttpServletRequest hReq) {
        String firstName = hReq.getParameter("firstname");
        String lastName = hReq.getParameter("lastname");
        String userName = hReq.getParameter("user");
        String password = hReq.getParameter("password");
        return "user was entered: " + userName + ", " + password + ", Human name is " + firstName + " " + lastName;
    }
}