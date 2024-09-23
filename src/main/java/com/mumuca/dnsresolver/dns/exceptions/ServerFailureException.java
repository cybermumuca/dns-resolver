package com.mumuca.dnsresolver.dns.exceptions;

public class ServerFailureException extends RuntimeException{
    public ServerFailureException(String message) {
        super(message);
    }
}
