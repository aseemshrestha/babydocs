package com.babydocs.service;

import exceptions.BadRequestException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class UserValidationService {
    public void isLoggedUserValid(String username, HttpServletRequest request) throws Exception {

        if (request.getUserPrincipal() == null) {
            throw new BadRequestException("Bad Request with username:");
        }
        String loggedInUser = request.getUserPrincipal().getName();

        if (!loggedInUser.equals(username)) {
            throw new BadRequestException("Not permitted to perform the requested operation");
        }
    }
}
