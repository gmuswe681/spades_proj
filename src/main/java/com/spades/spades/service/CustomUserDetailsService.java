package com.spades.spades.service;

import com.spades.spades.model.Users;
import com.spades.spades.model.CustomUserDetails;
import com.spades.spades.repository.UsersRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsersRepository usersRepository;

    private static final Logger LOGGER = LogManager.getLogger("CustomUserDetailsService.class");


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {


        Optional<Users> optionalUsers = usersRepository.findByName(username);
        optionalUsers
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        LOGGER.error("Username " + username + "not found.");
        return optionalUsers
                .map(CustomUserDetails::new).get();
    }
}