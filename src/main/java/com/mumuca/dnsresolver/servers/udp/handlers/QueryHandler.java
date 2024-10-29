package com.mumuca.dnsresolver.servers.udp.handlers;

import com.mumuca.dnsresolver.dns.DNSHeader;
import com.mumuca.dnsresolver.dns.DNSQuery;
import com.mumuca.dnsresolver.dns.DNSQuestion;
import com.mumuca.dnsresolver.dns.DNSResponse;
import com.mumuca.dnsresolver.servers.exceptions.DNSServerException;
import com.mumuca.dnsresolver.servers.resolvers.Resolver;
import com.mumuca.dnsresolver.servers.udp.contexts.DNSQueryContext;
import com.mumuca.dnsresolver.servers.udp.serializers.UDPResponseSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;

public class QueryHandler extends AbstractHandler {

    private static final Logger logger = LoggerFactory.getLogger(QueryHandler.class);

    private final Resolver resolver;

    public QueryHandler(Resolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void handle(DNSQueryContext context) throws IOException {
        DNSHeader dnsHeader = context.getDnsHeader();
        DNSQuestion dnsQuestion = context.getDnsQuestion();

        logger.info("Handling query: ID = {}, Question = {}", dnsHeader.getId(), dnsQuestion);

        try {
            DNSResponse dnsResponse = resolver.resolve(new DNSQuery(dnsHeader, dnsQuestion));
            sendResponse(context, dnsResponse);
            logger.info("Successfully resolved query ID = {} with response: {}", dnsHeader.getId(), dnsResponse);
        } catch (DNSServerException e) {
            logger.error("Error resolving query: {}", e.getMessage());
            sendError(context, context.getDnsHeader().getId(), getResultCode(e));
        }
    }

    private void sendResponse(DNSQueryContext context, DNSResponse dnsResponse) throws IOException {
        DatagramPacket response = UDPResponseSerializer.serialize(dnsResponse);
        response.setAddress(context.getQueryPacket().getAddress());
        response.setPort(context.getQueryPacket().getPort());
        context.getServerSocket().send(response);
        logger.debug("Sent response for query ID = {} to {}:{}", context.getDnsHeader().getId(),
                context.getQueryPacket().getAddress(), context.getQueryPacket().getPort());
    }
}