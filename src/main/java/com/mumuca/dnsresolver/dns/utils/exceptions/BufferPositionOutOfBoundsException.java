package com.mumuca.dnsresolver.dns.utils.exceptions;

public class BufferPositionOutOfBoundsException extends Exception {
    public BufferPositionOutOfBoundsException() {
        super("Buffer position out of bounds.");
    }

    public BufferPositionOutOfBoundsException(String message) {
        super(message);
    }
}
