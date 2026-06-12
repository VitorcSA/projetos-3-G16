package com.sintropia.calculator.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Email ou senha inválidos");
    }
}
