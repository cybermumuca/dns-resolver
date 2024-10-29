package com.mumuca.dnsresolver.servers.udp.handlers;

import com.mumuca.dnsresolver.dns.DNSQuestion;
import com.mumuca.dnsresolver.dns.enums.ResultCode;
import com.mumuca.dnsresolver.servers.exceptions.DNSServerException;
import com.mumuca.dnsresolver.servers.udp.contexts.DNSQueryContext;
import com.mumuca.dnsresolver.servers.udp.deserializers.UDPQueryQuestionDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class QuestionDeserializationHandler extends AbstractHandler {

    private static final Logger logger = LoggerFactory.getLogger(QuestionDeserializationHandler.class);

    @Override
    public void handle(DNSQueryContext context) throws IOException {
        logger.debug("Starting question deserialization for query ID: {}, from: {}", context.getDnsHeader().getId(), context.getQueryPacket().getAddress());

        try {
            DNSQuestion dnsQuestion = UDPQueryQuestionDeserializer.deserialize(context.getQueryPacketDataBuffer());
            logger.debug("DNS question deserialized successfully: {}", dnsQuestion);

            context.setDnsQuestion(dnsQuestion);

            if (next != null) {
                logger.debug("Passing context to next handler: {}", next.getClass().getSimpleName());
                next.handle(context);
            }

        } catch (DNSServerException e) {
            logger.error("Error deserializing question (Server Error): {}", e.getMessage());
            sendError(context, context.getDnsHeader().getId(), getResultCode(e));
        } catch (Exception e) {
            logger.error("Unexpected error deserializing question: {}", e.getMessage(), e);
            sendError(context, context.getDnsHeader().getId(), ResultCode.SERVER_FAILURE);
        }
    }
}
