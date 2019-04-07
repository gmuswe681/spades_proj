package com.spades.spades.resources;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.spades.spades.model.Users;
import com.spades.spades.model.Role;
import com.spades.spades.repository.UsersRepository;

@RequestMapping("/createuser")
@RestController
public class CreateUserController {

    private final UsersRepository repository;
    private static final Logger LOGGER = LogManager.getLogger("CreateUserController.class");

    CreateUserController(UsersRepository repository) {
        this.repository = repository;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public String submitUser(HttpServletRequest hReq) {
        String userName = hReq.getParameter("username");
        String lastName = hReq.getParameter("lastname");
        String email = hReq.getParameter("email");
        String password = hReq.getParameter("password");

        String message = "";

        if ((userName == null) || (lastName == null) || (email == null) || (password == null))
        {
            message = "A field wasn't specified";
        }
        else if (userName.length() <= 4 || password.length() <= 7)
        {
            message = "Username or password is too short.";
        }
        else if(checkExistingUsers(userName))
        {
            message = "User already exists. Pick another username please.";
            LOGGER.error(message);
        }
        else
        {
            PasswordEncoder pEncoder = new BCryptPasswordEncoder();

            Users newUser = new Users();



            newUser.setName(userName);
            newUser.setLastName(lastName);
            newUser.setEmail(email);
            newUser.setPassword("{bcrypt}" + pEncoder.encode(password));
            newUser.setActive(1);

            Set<Role> roles = new HashSet<Role>();
            Role r = new Role();
            r.setRole("USER");
            r.setRoleId(2);
            roles.add(r);
            newUser.setRoles(roles);

            repository.save(newUser);
            message = "User was successfully created.";
        }
        String result = "<html>\n";
        result += "<head></head>\n";
        result += "<body>\n";
        result += "<p>" + message + "</p>\n";
        result += "<a href=\"/\">Go to Homepage</a>\n";
        result += "</body>\n";
        result += "</html>";
        return result;
    }

    /****
     * Uses the existing password repository to get 
     ****/
    private boolean checkExistingUsers(String name)
    {
        Optional<Users> listUser = repository.findByName(name);

        // User wasn't found
        if(!listUser.isPresent())
        {
            return false;
        }

        return true;
    }
}