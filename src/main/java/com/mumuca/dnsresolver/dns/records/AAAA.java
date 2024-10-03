package com.mumuca.dnsresolver.dns.records;

import com.mumuca.dnsresolver.dns.enums.QueryType;
import com.mumuca.dnsresolver.dns.utils.PacketBuffer;
import com.mumuca.dnsresolver.dns.utils.exceptions.EndOfBufferException;

import java.net.Inet6Address;
import java.net.UnknownHostException;

public record AAAA(String name, short qClass, int ttl, int length, String address) implements ResourceRecord {

    @Override
    public QueryType type() {
        return QueryType.AAAA;
    }

    @Override
    public void writeInBuffer(PacketBuffer buffer) {
        try {
            buffer.writeQName(name());
            buffer.write16b(type().getValue());
            buffer.write16b(qClass());
            buffer.write32b(ttl());
            buffer.write16b(16);

            byte[] rawAddress = Inet6Address.getByName(address).getAddress();

            for (byte b : rawAddress) {
                buffer.write(b);
            }
        } catch (UnknownHostException | EndOfBufferException e) {
            throw new RuntimeException(e);
        }
    }

}
