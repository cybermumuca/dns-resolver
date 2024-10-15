package com.mumuca.dnsresolver.servers.udp.lookups;

import com.mumuca.dnsresolver.dns.DNSQuery;
import com.mumuca.dnsresolver.dns.DNSResponse;

import java.io.IOException;

public class TCPDNSLookup implements DNSLookup {
    @Override
    public DNSResponse lookup(DNSQuery dnsQuery, String server) throws IOException {
        return null;
    }
}