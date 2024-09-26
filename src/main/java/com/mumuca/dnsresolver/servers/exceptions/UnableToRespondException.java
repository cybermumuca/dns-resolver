package com.mumuca.dnsresolver.servers.exceptions;

public class UnableToRespondException extends DNSServerException {
    public UnableToRespondException(String message) {
        super(message);
    }
}
