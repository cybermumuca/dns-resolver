package com.mumuca.dnsresolver.servers.udp.handlers;

import com.mumuca.dnsresolver.dns.DNSHeader;
import com.mumuca.dnsresolver.dns.DNSQuestion;
import com.mumuca.dnsresolver.dns.DNSResponse;
import com.mumuca.dnsresolver.servers.exceptions.DNSServerException;
import com.mumuca.dnsresolver.servers.udp.contexts.DNSQueryContext;
import com.mumuca.dnsresolver.servers.udp.resolvers.UDPResolver;
import com.mumuca.dnsresolver.servers.udp.serializers.UDPResponseSerializer;

import java.io.IOException;
import java.net.DatagramPacket;

public class QueryHandler extends AbstractHandler {

    @Override
    public void handle(DNSQueryContext context) throws IOException {
        DNSHeader dnsHeader = context.getDnsHeader();
        DNSQuestion dnsQuestion = context.getDnsQuestion();

        try {
            DNSResponse dnsResponse = UDPResolver.query(dnsHeader.id, dnsHeader.recursionDesired, dnsQuestion.qName, dnsQuestion.qType, dnsQuestion.qClass);
            sendResponse(context, dnsResponse);
        } catch (DNSServerException e) {
            sendError(context, context.getDnsHeader().id, getResultCode(e));
        }
    }

    private void sendResponse(DNSQueryContext context, DNSResponse dnsResponse) throws IOException {
        DatagramPacket response = UDPResponseSerializer.serialize(dnsResponse);
        response.setAddress(context.getQueryPacket().getAddress());
        response.setPort(context.getQueryPacket().getPort());
        context.getServerSocket().send(response);
    }
}