package com.mumuca.dnsresolver.servers.udp.handlers;

import com.mumuca.dnsresolver.dns.DNSHeader;
import com.mumuca.dnsresolver.servers.exceptions.DNSServerException;
import com.mumuca.dnsresolver.servers.exceptions.UnableToRespondException;
import com.mumuca.dnsresolver.servers.udp.contexts.DNSQueryContext;
import com.mumuca.dnsresolver.servers.udp.deserializers.UDPQueryHeaderDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HeaderDeserializationHandler extends AbstractHandler {

    private static final Logger logger = LoggerFactory.getLogger(HeaderDeserializationHandler.class);

    @Override
    public void handle(DNSQueryContext context) throws IOException {
        logger.debug("Starting header deserialization for query from {}", context.getQueryPacket().getAddress());

        try {
            DNSHeader dnsHeader = UDPQueryHeaderDeserializer.deserialize(context.getQueryPacketDataBuffer());
            logger.debug("DNS header deserialized successfully: {}", dnsHeader);

            UDPQueryHeaderDeserializer.checkDNSHeader(dnsHeader);
            context.setDnsHeader(dnsHeader);

            if (next != null) {
                logger.debug("Passing context to next handler: {}", next.getClass().getSimpleName());
                next.handle(context);
            }
        } catch (UnableToRespondException e) {
            logger.warn("Error deserializing header (Unable To Respond): {}", e.getMessage());
        } catch (DNSServerException e) {
            logger.error("Error deserializing header ({}): {}", e.getClass().getSimpleName(), e.getMessage());
            sendError(context, null, getResultCode(e));
        } catch (Exception e) {
            logger.error("Unexpected error during header deserialization", e);
        }
    }
}

