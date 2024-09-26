package com.mumuca.dnsresolver.dns.exceptions;

public class QuestionMalformedException extends Exception {
    public QuestionMalformedException() {
        super("Question Section malformed.");
    }
}
