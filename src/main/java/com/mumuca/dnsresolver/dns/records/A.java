package com.mumuca.dnsresolver.dns.records;

import com.mumuca.dnsresolver.dns.enums.QueryType;
import com.mumuca.dnsresolver.dns.utils.PacketBuffer;
import com.mumuca.dnsresolver.dns.utils.exceptions.EndOfBufferException;

import java.net.InetAddress;
import java.net.UnknownHostException;

public record A(String name, short qClass, int ttl, int length, String address) implements ResourceRecord {

    @Override
    public QueryType type() {
        return QueryType.A;
    }

    @Override
    public void writeInBuffer(PacketBuffer buffer) {
        try {
            buffer.writeQName(name());
            buffer.write16b(type().getValue());
            buffer.write16b(1);
            buffer.write32b(ttl());
            buffer.write16b(4);

            var rawAddress = InetAddress.getByName(address).getAddress();

            buffer.write(rawAddress[0]);
            buffer.write(rawAddress[1]);
            buffer.write(rawAddress[2]);
            buffer.write(rawAddress[3]);
        } catch (EndOfBufferException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
