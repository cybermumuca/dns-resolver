package com.mumuca.dnsresolver.utils.exceptions;

public class EndOfBufferException extends Exception {
    public EndOfBufferException(String message) {
        super(message);
    }

    public EndOfBufferException() {
        super("End of Buffer.");
    }
}
