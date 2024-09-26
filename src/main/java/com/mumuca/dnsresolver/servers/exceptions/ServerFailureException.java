package com.mumuca.dnsresolver.servers.exceptions;

public class ServerFailureException extends DNSServerException {
    public ServerFailureException(String message) {
        super(message);
    }
}
