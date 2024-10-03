package com.mumuca.dnsresolver.dns.records;

import com.mumuca.dnsresolver.dns.enums.QueryType;
import com.mumuca.dnsresolver.dns.utils.PacketBuffer;
import com.mumuca.dnsresolver.dns.utils.exceptions.EndOfBufferException;

public record CNAME(String name, short qClass, int ttl, int length, String cname) implements ResourceRecord {
    @Override
    public QueryType type() {
        return QueryType.CNAME;
    }

    @Override
    public void writeToBuffer(PacketBuffer buffer) {
        try {
            buffer.writeQName(name());
            buffer.write16b(type().getValue());
            buffer.write16b(qClass());
            buffer.write32b(ttl());

            int position = buffer.getPosition();
            buffer.write16b(0);

            buffer.writeQName(cname());

            int size = buffer.getPosition() - (position + 2);
            buffer.set16b(position, size);
        } catch (EndOfBufferException e) {
            throw new RuntimeException(e);
        }
    }
}
