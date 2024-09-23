package com.mumuca.dnsresolver.dns.exceptions;

public class RefusedException extends RuntimeException {
    public RefusedException(String message) {
        super(message);
    }
}
