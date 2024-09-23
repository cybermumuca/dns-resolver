package com.mumuca.dnsresolver.utils.exceptions;

public class BufferPositionOutOfBoundsException extends Exception {
    public BufferPositionOutOfBoundsException() {
        super("Buffer position out of bounds.");
    }

    public BufferPositionOutOfBoundsException(String message) {
        super(message);
    }
}
