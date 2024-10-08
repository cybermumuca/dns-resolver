package com.mumuca.dnsresolver.servers.udp.handlers;

import com.mumuca.dnsresolver.dns.DNSHeader;
import com.mumuca.dnsresolver.servers.exceptions.DNSServerException;
import com.mumuca.dnsresolver.servers.exceptions.UnableToRespondException;
import com.mumuca.dnsresolver.servers.udp.contexts.DNSQueryContext;
import com.mumuca.dnsresolver.servers.udp.deserializers.UDPQueryHeaderDeserializer;

import java.io.IOException;

public class HeaderDeserializationHandler extends AbstractHandler {

    @Override
    public void handle(DNSQueryContext context) throws IOException {
        try {
            DNSHeader dnsHeader = UDPQueryHeaderDeserializer.deserialize(context.getQueryPacketDataBuffer());
            UDPQueryHeaderDeserializer.checkDNSHeader(dnsHeader);
            dnsHeader.setRecursionAvailable(true);
            context.setDnsHeader(dnsHeader);

            if (next != null) {
                next.handle(context);
            }
        } catch (UnableToRespondException e) {
            e.printStackTrace();
            return;
        } catch (DNSServerException e) {
            e.printStackTrace();
            sendError(context, null, getResultCode(e));
        }
    }
}

