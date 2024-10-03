package com.mumuca.dnsresolver.dns;

import com.mumuca.dnsresolver.dns.enums.QueryType;
import com.mumuca.dnsresolver.dns.exceptions.QueryTypeUnsupportedException;
import com.mumuca.dnsresolver.dns.exceptions.ResourceRecordMalformedException;
import com.mumuca.dnsresolver.dns.records.*;
import com.mumuca.dnsresolver.dns.utils.PacketBuffer;
import com.mumuca.dnsresolver.dns.utils.exceptions.BufferPositionOutOfBoundsException;
import com.mumuca.dnsresolver.dns.utils.exceptions.EndOfBufferException;
import com.mumuca.dnsresolver.dns.utils.exceptions.JumpLimitExceededException;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * A classe {@code DNSRecord} fornece métodos estáticos para manipulação e criação de registros
 * de recursos DNS a partir de um buffer.
 */
public class DNSRecord {
    public static ResourceRecord fromBuffer(PacketBuffer buffer) throws QueryTypeUnsupportedException, ResourceRecordMalformedException {
        try {
            String name = buffer.readQName();

            QueryType type = QueryType.fromValue(buffer.read16b());
            short qClass = (short) buffer.read16b();
            int ttl = buffer.read32b();
            int length = buffer.read16b();

            switch (type) {
                case A -> {
                    int rawAddress = buffer.read32b();
                    InetAddress inetAddress = Inet4Address.getByAddress(new byte[]{
                            (byte) (rawAddress >> 24 & 0xFF),
                            (byte) (rawAddress >> 16 & 0xFF),
                            (byte) (rawAddress >> 8 & 0xFF),
                            (byte) (rawAddress & 0xFF)
                    });
                    String address = inetAddress.getHostAddress();
                    return new A(name, qClass, ttl, length, address);
                }
                case NS -> {
                    String ns = buffer.readQName();
                    return new NS(name, qClass, ttl, length, ns);
                }
                case CNAME -> {
                    String cname = buffer.readQName();
                    return new CNAME(name, qClass, ttl, length, cname);
                }
                case SOA -> {
                    String mname = buffer.readQName();
                    String rname = buffer.readQName();

                    int serial = buffer.read32b();
                    int refresh = buffer.read32b();
                    int retry = buffer.read32b();
                    int expire = buffer.read32b();
                    int minimum = buffer.read32b();

                    return new SOA(name, qClass, ttl, length, mname, rname, serial, refresh, retry, expire, minimum);
                }
                case MX -> {
                    int priority = buffer.read16b();
                    String mx = buffer.readQName();

                    return new MX(name, qClass, ttl, length, priority, mx);
                }
                case AAAA -> {
                    int rawAddress1 = buffer.read32b();
                    int rawAddress2 = buffer.read32b();
                    int rawAddress3 = buffer.read32b();
                    int rawAddress4 = buffer.read32b();

                    InetAddress inetAddress = Inet6Address.getByAddress(new byte[] {
                            (byte) (rawAddress1 >> 24), (byte) (rawAddress1 >> 16), (byte) (rawAddress1 >> 8), (byte) rawAddress1,
                            (byte) (rawAddress2 >> 24), (byte) (rawAddress2 >> 16), (byte) (rawAddress2 >> 8), (byte) rawAddress2,
                            (byte) (rawAddress3 >> 24), (byte) (rawAddress3 >> 16), (byte) (rawAddress3 >> 8), (byte) rawAddress3,
                            (byte) (rawAddress4 >> 24), (byte) (rawAddress4 >> 16), (byte) (rawAddress4 >> 8), (byte) rawAddress4
                    });

                    String address = inetAddress.getHostAddress();

                    return new AAAA(name, qClass, ttl, length, address);
                }
                default -> throw new QueryTypeUnsupportedException();
            }
        } catch (JumpLimitExceededException | BufferPositionOutOfBoundsException | UnknownHostException |
                 EndOfBufferException e) {
            throw new ResourceRecordMalformedException();
        }
    }
}
