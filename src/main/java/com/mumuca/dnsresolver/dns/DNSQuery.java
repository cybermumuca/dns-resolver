package com.mumuca.dnsresolver.dns;

import com.mumuca.dnsresolver.dns.enums.QueryType;
import com.mumuca.dnsresolver.dns.exceptions.InvalidHeaderSizeException;
import com.mumuca.dnsresolver.dns.exceptions.QuestionMalformedException;
import com.mumuca.dnsresolver.dns.exceptions.SuspiciousDomainNamePayloadException;
import com.mumuca.dnsresolver.dns.utils.PacketBuffer;
import com.mumuca.dnsresolver.dns.utils.exceptions.EndOfBufferException;

public class DNSQuery {
    private final DNSHeader dnsHeader;
    private final DNSQuestion dnsQuestion;

    public DNSQuery(DNSHeader dnsHeader, DNSQuestion dnsQuestion) {
        this.dnsHeader = dnsHeader;
        this.dnsQuestion = dnsQuestion;
    }

    public DNSHeader getHeader() {
        return dnsHeader;
    }

    public DNSQuestion getQuestion() {
        return dnsQuestion;
    }

    public static DNSQuery from(int id, boolean withRecursion, String name, QueryType queryType, short qclass) {
        DNSHeader dnsHeader = new DNSHeader();

        dnsHeader.setId(id);
        dnsHeader.setQuery(true);
        dnsHeader.setOpcode((short) 0);
        dnsHeader.setRecursionDesired(withRecursion);
        dnsHeader.setQuestionCount(1);

        return new DNSQuery(dnsHeader, new DNSQuestion(name, queryType, qclass));
    }

    public void writeToBuffer(PacketBuffer buffer) throws InvalidHeaderSizeException {
        this.dnsHeader.writeToBuffer(buffer);
        this.dnsQuestion.writeToBuffer(buffer);
    }
}
