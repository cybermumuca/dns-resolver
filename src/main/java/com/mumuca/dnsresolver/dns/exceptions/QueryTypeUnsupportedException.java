package com.mumuca.dnsresolver.dns.exceptions;

public class QueryTypeUnsupportedException extends Exception {
    public QueryTypeUnsupportedException() {
        super("Query type unsupported.");
    }
}
