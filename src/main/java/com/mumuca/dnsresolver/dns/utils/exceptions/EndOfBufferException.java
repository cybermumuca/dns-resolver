package com.mumuca.dnsresolver.dns.utils.exceptions;

public class EndOfBufferException extends Exception {
    public EndOfBufferException(String message) {
        super(message);
    }

    public EndOfBufferException() {
        super("End of Buffer.");
    }
}
