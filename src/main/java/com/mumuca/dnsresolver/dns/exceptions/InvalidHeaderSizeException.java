package com.mumuca.dnsresolver.dns.exceptions;

public class InvalidHeaderSizeException extends Exception {
    public InvalidHeaderSizeException() {
        super("Packet smaller than 12 bytes");
    }
}
