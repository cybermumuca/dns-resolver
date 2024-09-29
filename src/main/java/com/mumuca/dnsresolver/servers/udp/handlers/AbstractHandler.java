package com.mumuca.dnsresolver.servers.udp.handlers;

import com.mumuca.dnsresolver.dns.DNSHeader;
import com.mumuca.dnsresolver.dns.enums.ResultCode;
import com.mumuca.dnsresolver.servers.exceptions.FormatErrorException;
import com.mumuca.dnsresolver.servers.exceptions.NotImplementedException;
import com.mumuca.dnsresolver.servers.exceptions.RefusedException;
import com.mumuca.dnsresolver.servers.udp.contexts.DNSQueryContext;
import com.mumuca.dnsresolver.servers.udp.serializers.UDPResponseHeaderSerializer;

import java.io.IOException;
import java.net.DatagramPacket;

public abstract class AbstractHandler {
    protected AbstractHandler next;

    public void setNext(AbstractHandler next) {
        this.next = next;
    }

    public abstract void handle(DNSQueryContext context) throws IOException;

    protected ResultCode getResultCode(RuntimeException e) {
        if (e instanceof FormatErrorException) {
            return ResultCode.FORM_ERROR;
        } else if (e instanceof RefusedException) {
            return ResultCode.REFUSED;
        } else if (e instanceof NotImplementedException) {
            return ResultCode.NOT_IMPLEMENTED;
        }
        return ResultCode.SERVER_FAILURE;
    }

    protected void sendError(DNSQueryContext context, Integer headerId, ResultCode resultCode) throws IOException {
        DNSHeader dnsHeaderResponse = new DNSHeader(
                headerId != null ? headerId : 0,
                false,
                (short) 0,
                false,
                true,
                resultCode
        );
        DatagramPacket responsePacket = UDPResponseHeaderSerializer.serialize(dnsHeaderResponse);
        responsePacket.setAddress(context.getQueryPacket().getAddress());
        responsePacket.setPort(context.getQueryPacket().getPort());
        context.getServerSocket().send(responsePacket);
    }
}
