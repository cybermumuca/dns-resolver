package com.mumuca.dnsresolver.servers.udp.resolvers;

import com.mumuca.dnsresolver.dns.DNSHeader;
import com.mumuca.dnsresolver.dns.DNSQuery;
import com.mumuca.dnsresolver.dns.DNSQuestion;
import com.mumuca.dnsresolver.dns.DNSResponse;
import com.mumuca.dnsresolver.dns.enums.QueryType;
import com.mumuca.dnsresolver.dns.enums.ResultCode;
import com.mumuca.dnsresolver.dns.records.A;
import com.mumuca.dnsresolver.servers.exceptions.ServerFailureException;
import com.mumuca.dnsresolver.servers.resolvers.Resolver;
import com.mumuca.dnsresolver.servers.udp.lookups.DNSLookup;
import com.mumuca.dnsresolver.servers.udp.lookups.UDPDNSLookup;

import java.io.IOException;
import java.util.Optional;

public class UDPDNSResolver extends Resolver {
    private static final String ROOT_SERVER_F = "192.5.5.241";
    private static final DNSLookup udpLookup = new UDPDNSLookup();

    @Override
    public DNSResponse resolve(DNSQuery dnsQuery) {
        if (!dnsQuery.getHeader().isRecursionDesired()) {
            return lookup(dnsQuery, ROOT_SERVER_F);
        }

        String nameServer = ROOT_SERVER_F;

        while (true) {
            DNSResponse response = lookup(dnsQuery, nameServer);

            ResultCode resultCode = response.getHeader().getResultCode();

            boolean noError = resultCode == ResultCode.NO_ERROR;
            boolean answersIsEmpty = response.getAnswerRecords().isEmpty();

            if (noError && !answersIsEmpty && !response.getHeader().isTruncatedMessage()) {
                return response;
            }

            boolean domainDoesNotExist = resultCode == ResultCode.NAME_ERROR;

            if (domainDoesNotExist) {
                return response;
            }

            String qname = response.getQuestion().qName;
            Optional<String> newNameServer = response.getResolvedNS(qname);

            if (newNameServer.isPresent()) {
                nameServer = newNameServer.get();
                continue;
            }

            Optional<String> newNameServerName = response.getUnresolvedNS(qname);

            if (newNameServerName.isPresent()) {
                DNSHeader nsHeader = new DNSHeader((int) (Math.random() * 65535), (short) 0, true, 1);
                DNSQuestion question = new DNSQuestion(newNameServerName.get(), QueryType.A, (short) 1);
                DNSResponse nsResponse = resolve(new DNSQuery(nsHeader, question));

                if (!nsResponse.getAnswerRecords().isEmpty() && !nsResponse.getHeader().isTruncatedMessage()) {
                    nameServer = ((A) nsResponse.getAnswerRecords().getFirst()).address();
                    continue;
                }
            }

            return response;
        }
    }

    private DNSResponse lookup(DNSQuery dnsQuery, String server) {
        try {
            return udpLookup.lookup(dnsQuery, server);
        } catch (IOException e) {
            throw new ServerFailureException("Error resolving query.");
        }
    }
}
