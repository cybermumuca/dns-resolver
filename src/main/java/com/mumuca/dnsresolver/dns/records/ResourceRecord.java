package com.mumuca.dnsresolver.dns.records;

import com.mumuca.dnsresolver.dns.enums.QueryType;
import com.mumuca.dnsresolver.dns.exceptions.QueryTypeUnsupportedException;
import com.mumuca.dnsresolver.dns.utils.PacketBuffer;

public sealed interface ResourceRecord permits A, AAAA, CNAME, MX, NS {
    String name();
    QueryType type();
    short qClass();
    int ttl();
    int length();

    default void writeToBuffer(PacketBuffer buffer) throws QueryTypeUnsupportedException {
        throw new QueryTypeUnsupportedException();
    }
}
