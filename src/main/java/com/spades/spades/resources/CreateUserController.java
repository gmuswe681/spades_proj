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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.spades.spades.model.Users;
import com.spades.spades.model.Role;
import com.spades.spades.repository.UsersRepository;

@RequestMapping("/createuser")
@RestController
public class CreateUserController {

    private final UsersRepository repository;
    private static final Logger LOGGER = LogManager.getLogger("CreateUserController.class");
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern VALID_USER_NAME = Pattern.compile("^[a-zA-Z0-9]+$");
    private static final Pattern VALID_PASSWORD = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");

    CreateUserController(UsersRepository repository) {
        this.repository = repository;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public String submitUser(HttpServletRequest hReq) {
        String userName = hReq.getParameter("username");
        String lastName = hReq.getParameter("lastname");
        String email = hReq.getParameter("email");
        String password = hReq.getParameter("password");
        StringBuffer message = new StringBuffer();

        if (validateUser(userName, lastName, email, password).length() > 0){
            message.append("<ul>");
            message = validateUser(userName, lastName, email, password);
            message.append("</ul>");
        } else {

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
            message.append("User was successfully created.");
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

    private StringBuffer validateUser(String userName, String lastName, String email, String password){

        StringBuffer errorMessage = new StringBuffer();
        String message = "";
        if(isNull(userName) == true){
            message = "User Name is Null.";
            appendMessage(errorMessage, message);
        }
        if(isNull(password) == true){
            message = "Password is Null.";
            appendMessage(errorMessage, message);
        }
        if(isNull(lastName)== true){
            message = "Lastname is Null.";
            appendMessage(errorMessage, message);
        }
        if (isNull(password)){
            message = "Password is Null.";
            appendMessage(errorMessage, message);
        }
        if (userName.length() <= 4)
        {
            message = "Username is too short.";
            appendMessage(errorMessage, message);
        }
        if (userName.length() >= 55){
            message = "Username is too long";
            appendMessage(errorMessage, message);
        }
        if (checkUsername(userName) == false){
            message = "Username is not alphanumeric.";
            appendMessage(errorMessage, message);
        }
        if (password.length() <= 4)
        {
            message = "Password is too short.";
            appendMessage(errorMessage, message);
        }
        if (password.length() >= 55){
            message = "Password is too long";
            appendMessage(errorMessage, message);
        }
        if (checkPassword(password) == false){
            message = "Password does not meet requirments.";
            appendMessage(errorMessage, message);
        }
        if (checkEmail(email) == false){
            message = "Please enter a valid email address.";
            appendMessage(errorMessage, message);
        }
        if(email.length() > 255){
            message = "Email Address is too large.";
            appendMessage(errorMessage, message);
        }
        if(lastName.length() > 255){
            message = "Last Name is too large.";
            appendMessage(errorMessage, message);
        }
        if(checkExistingUsers(userName))
        {
            message = "User already exists. Pick another username please.";
            appendMessage(errorMessage, message);
        }
         return errorMessage;
    }

    private void appendMessage(StringBuffer errorMessage, String message) {
        LOGGER.error(message);
        errorMessage.append("<li>" + message);
    }

    private boolean isNull(String field){
        if(field == null | field.length() == 0){
            return true;
        } else {
            return false;
        }
     }


    private boolean checkEmail(String email){
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(email);
        return matcher.find();
    }

    private boolean checkUsername(String username){
        Matcher matcher = VALID_USER_NAME.matcher(username);
        return  matcher.find();
    }

    private boolean checkPassword(String password){
        Matcher matcher = VALID_PASSWORD.matcher(password);
        return matcher.find();
    }
}