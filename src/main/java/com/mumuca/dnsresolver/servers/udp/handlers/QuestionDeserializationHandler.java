package com.mumuca.dnsresolver.servers.udp.handlers;

import com.mumuca.dnsresolver.dns.DNSQuestion;
import com.mumuca.dnsresolver.dns.enums.ResultCode;
import com.mumuca.dnsresolver.servers.exceptions.DNSServerException;
import com.mumuca.dnsresolver.servers.udp.contexts.DNSQueryContext;
import com.mumuca.dnsresolver.servers.udp.deserializers.UDPQueryQuestionDeserializer;

import java.io.IOException;

public class QuestionDeserializationHandler extends AbstractHandler {

    @Override
    public void handle(DNSQueryContext context) throws IOException {
        try {
            DNSQuestion dnsQuestion = UDPQueryQuestionDeserializer.deserialize(context.getQueryPacketDataBuffer());
            context.setDnsQuestion(dnsQuestion);

            if (next != null) {
                next.handle(context);
            }

        } catch (DNSServerException e) {
            e.printStackTrace();
            sendError(context, context.getDnsHeader().getId(), getResultCode(e));
        } catch (Exception e) {
            e.printStackTrace();
            sendError(context, context.getDnsHeader().getId(), ResultCode.SERVER_FAILURE);
        }
    }
}
