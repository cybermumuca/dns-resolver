package com.mumuca.dnsresolver.dns.records;

import com.mumuca.dnsresolver.dns.enums.QueryType;
import com.mumuca.dnsresolver.dns.exceptions.QueryTypeUnsupportedException;
import com.mumuca.dnsresolver.dns.utils.PacketBuffer;
import com.mumuca.dnsresolver.dns.utils.exceptions.EndOfBufferException;

public record SOA(String name, short qClass, int ttl, int length, String mname, String rname, int serial, int refresh, int retry, int expire, int minimum) implements ResourceRecord {
    @Override
    public QueryType type() {
        return QueryType.SOA;
    }

    @Override
    public void writeToBuffer(PacketBuffer buffer) throws QueryTypeUnsupportedException {
        try {
            buffer.writeQName(name());
            buffer.write16b(type().getValue());
            buffer.write16b(qClass());
            buffer.write32b(ttl());

            int position = buffer.getPosition();
            buffer.write16b(0);

            buffer.writeQName(mname());

            buffer.writeQName(rname());

            buffer.write32b(serial());
            buffer.write32b(refresh());
            buffer.write32b(retry());
            buffer.write32b(expire());
            buffer.write32b(minimum());

            int size = buffer.getPosition() - (position + 2);
            buffer.set16b(position, size);
        } catch (EndOfBufferException e) {
            throw new RuntimeException(e);
        }
    }
}
