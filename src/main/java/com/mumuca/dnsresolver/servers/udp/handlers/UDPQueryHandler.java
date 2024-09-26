package com.mumuca.dnsresolver.servers.udp.handlers;

import com.mumuca.dnsresolver.dns.DNSHeader;
import com.mumuca.dnsresolver.dns.DNSQuery;
import com.mumuca.dnsresolver.dns.DNSQuestion;
import com.mumuca.dnsresolver.dns.DNSResponse;
import com.mumuca.dnsresolver.servers.udp.resolvers.UDPResolver;

public class UDPQueryHandler {
    public UDPQueryHandler() {
    }

    public DNSResponse handle(DNSQuery query) {
        DNSHeader dnsHeader = query.getHeader();
        DNSQuestion dnsQuestion = query.getQuestion();

        return UDPResolver.query(dnsHeader.id, dnsHeader.recursionDesired, dnsQuestion.qName, dnsQuestion.qType, dnsQuestion.qClass);
    }
}
