package com.mumuca.dnsresolver.dns.exceptions;

public class ResourceRecordMalformedException extends Exception {
    public ResourceRecordMalformedException() {
        super("Resource record malformed.");
    }
}
