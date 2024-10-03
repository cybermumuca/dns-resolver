package com.mumuca.dnsresolver.dns.records;

import com.mumuca.dnsresolver.dns.enums.QueryType;
import com.mumuca.dnsresolver.dns.exceptions.QueryTypeUnsupportedException;
import com.mumuca.dnsresolver.dns.utils.PacketBuffer;
import com.mumuca.dnsresolver.dns.utils.exceptions.EndOfBufferException;

public sealed interface ResourceRecord permits A, AAAA, CNAME, MX, NS {
    String name();
    QueryType type();
    short qClass();
    int ttl();
    int length();

    default void writeInBuffer(PacketBuffer buffer) throws QueryTypeUnsupportedException {
        try {
            buffer.writeQName(name());
            buffer.write16b(type().getValue());
            buffer.write16b(qClass());
            buffer.write32b(ttl());
            buffer.write16b(length());

            throw new QueryTypeUnsupportedException();
        } catch (EndOfBufferException e) {
            throw new RuntimeException(e);
        }
    }
}
