package com.mumuca.dnsresolver.servers.exceptions;

public abstract class DNSServerException extends RuntimeException {
    public DNSServerException(String message) {
        super(message);
    }
}
