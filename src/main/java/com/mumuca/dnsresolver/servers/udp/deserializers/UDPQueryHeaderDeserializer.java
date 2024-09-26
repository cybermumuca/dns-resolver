package com.mumuca.dnsresolver.servers.udp.deserializers;

import com.mumuca.dnsresolver.dns.DNSHeader;
import com.mumuca.dnsresolver.dns.exceptions.InvalidHeaderSizeException;
import com.mumuca.dnsresolver.dns.utils.PacketBuffer;
import com.mumuca.dnsresolver.servers.exceptions.FormatErrorException;
import com.mumuca.dnsresolver.servers.exceptions.NotImplementedException;
import com.mumuca.dnsresolver.servers.exceptions.UnableToRespondException;

public class UDPQueryHeaderDeserializer {
    public static DNSHeader deserialize(PacketBuffer buffer) {
        try {
            return DNSHeader.fromBuffer(buffer);
        } catch (InvalidHeaderSizeException e) {
            throw new UnableToRespondException("Invalid header.");
        }
    }

    public static void checkDNSHeader(DNSHeader dnsHeader) {
        if (!dnsHeader.query) {
            throw new FormatErrorException("Not a dns query.");
        }

        if (dnsHeader.opcode != 0) {
            throw new NotImplementedException("Operation not implemented.");
        }

        if (dnsHeader.authoritativeAnswer) {
            throw new FormatErrorException("Not an authoritative server.");
        }

        if (dnsHeader.truncatedMessage) {
            throw new NotImplementedException("Messages larger than 512 bytes via UDP not implemented.");
        }

        if (dnsHeader.questionCount != 1) {
            throw new FormatErrorException("Restricted to 1 question per query.");
        }

        if (dnsHeader.answerRecordCount != 0) {
            throw new FormatErrorException("ANCOUNT must be 0 in queries.");
        }

        if (dnsHeader.authoritativeRecordCount != 0) {
            throw new FormatErrorException("NSCOUNT must be 0 in queries.");
        }

        if (dnsHeader.additionalRecordCount != 0) {
            throw new FormatErrorException("ARCOUNT must be 0 in queries.");
        }
    }
}
