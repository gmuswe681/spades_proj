package com.spades.spades.resources;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import com.spades.spades.model.Users;
import com.spades.spades.repository.UsersRepository;

@RequestMapping("/createuser")
@RestController
public class CreateUserController {

    private final UsersRepository repository;

    CreateUserController(UsersRepository repository) {
        this.repository = repository;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public String submitUser(HttpServletRequest hReq) {
        String firstName = hReq.getParameter("firstname");
        String lastName = hReq.getParameter("lastname");
        String email = hReq.getParameter("user");
        String password = hReq.getParameter("password");

        if ((firstName == null) || (lastName == null) || (email == null) || (password == null))
        {
            return "A field wasn't specified";
        }

        Users newUser = new Users();
        newUser.setName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setPassword(password);
        repository.save(newUser);
        return "user was entered: " + firstName + ", " + password + ", Human name is " + firstName + " " + lastName;
    }
}