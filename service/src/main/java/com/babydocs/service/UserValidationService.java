package com.babydocs.service;

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class UserValidationService {
    public void isLoggedUserValid(String username, HttpServletRequest request) throws Exception {

        if (request.getUserPrincipal() == null) {
            throw new Exception("Bad Request with username:");
        }
        String loggedInUser = request.getUserPrincipal().getName();

        if (!loggedInUser.equals(username)) {
            throw new Exception("Bad Request with username:" + username);
        }
    }
}
