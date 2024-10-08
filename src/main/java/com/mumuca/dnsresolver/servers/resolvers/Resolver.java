package com.mumuca.dnsresolver.servers.resolvers;

import com.mumuca.dnsresolver.dns.DNSQuery;
import com.mumuca.dnsresolver.dns.DNSResponse;

public abstract class Resolver {
    public abstract DNSResponse resolve(DNSQuery dnsQuery);
}
