package com.mumuca.dnsresolver.servers.exceptions;

public class RefusedException extends DNSServerException {
    public RefusedException(String message) {
        super(message);
    }
}
