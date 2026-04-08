package com.example.department.exception;

public class UnauthorizedRoleException 
extends RuntimeException {

    public UnauthorizedRoleException(String message) {
        super(message);
    }


}
