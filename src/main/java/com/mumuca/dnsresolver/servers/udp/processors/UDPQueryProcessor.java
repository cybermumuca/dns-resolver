package com.mumuca.dnsresolver.servers.udp.processors;

import com.mumuca.dnsresolver.dns.DNSHeader;
import com.mumuca.dnsresolver.dns.DNSQuery;
import com.mumuca.dnsresolver.dns.DNSQuestion;
import com.mumuca.dnsresolver.dns.DNSResponse;
import com.mumuca.dnsresolver.dns.enums.ResultCode;
import com.mumuca.dnsresolver.dns.utils.PacketBuffer;
import com.mumuca.dnsresolver.servers.exceptions.*;
import com.mumuca.dnsresolver.servers.udp.deserializers.UDPQueryHeaderDeserializer;
import com.mumuca.dnsresolver.servers.udp.deserializers.UDPQueryQuestionDeserializer;
import com.mumuca.dnsresolver.servers.udp.handlers.UDPQueryHandler;
import com.mumuca.dnsresolver.servers.udp.serializers.UDPResponseHeaderSerializer;
import com.mumuca.dnsresolver.servers.udp.serializers.UDPResponseSerializer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPQueryProcessor implements Runnable {
    private final DatagramSocket serverSocket;
    private final DatagramPacket queryPacket;

    public UDPQueryProcessor(DatagramSocket serverSocket, DatagramPacket queryPacket) {
        this.serverSocket = serverSocket;
        this.queryPacket = queryPacket;
    }

    @Override
    public void run() {
        try {
            process();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void process() throws IOException {
        PacketBuffer queryPacketDataBuffer = new PacketBuffer(this.queryPacket.getData());

        DNSHeader dnsHeader = tryDeserializeHeader(queryPacketDataBuffer);

        if (dnsHeader == null) {
            return;
        }

        DNSQuestion dnsQuestion = tryDeserializeQuestion(queryPacketDataBuffer, dnsHeader);

        if (dnsQuestion == null) {
            return;
        }

        handleDNSQuery(dnsHeader, dnsQuestion);
    }

    private DNSHeader tryDeserializeHeader(PacketBuffer buffer) throws IOException {
        try {
            DNSHeader dnsHeader = UDPQueryHeaderDeserializer.deserialize(buffer);
            UDPQueryHeaderDeserializer.checkDNSHeader(dnsHeader);
            return dnsHeader;
        } catch (UnableToRespondException e) {
            // Não podemos responder, silenciosamente retornamos
            return null;
        } catch (DNSServerException e) {
            // Em caso de erro, enviamos a resposta com o código adequado
            sendError(null, getResultCode(e));
            return null;
        }
    }

    private DNSQuestion tryDeserializeQuestion(PacketBuffer buffer, DNSHeader dnsHeader) throws IOException {
        try {
            return UDPQueryQuestionDeserializer.deserialize(buffer);
        } catch (DNSServerException e) {
            sendError(dnsHeader.id, getResultCode(e));
        } catch (Exception e) {
            sendError(dnsHeader.id, ResultCode.SERVER_FAILURE);
        }

        return null;
    }

    private void handleDNSQuery(DNSHeader dnsHeader, DNSQuestion dnsQuestion) throws IOException {
        UDPQueryHandler queryHandler = new UDPQueryHandler();

        try {
            DNSResponse dnsResponse = queryHandler.handle(new DNSQuery(dnsHeader, dnsQuestion));
            sendResponse(dnsResponse);
        } catch (DNSServerException e) {
            sendError(dnsHeader.id, getResultCode(e));
        }
    }

    private void sendResponse(DNSResponse dnsResponse) throws IOException {
        DatagramPacket response = UDPResponseSerializer.serialize(dnsResponse);
        response.setAddress(queryPacket.getAddress());
        response.setPort(queryPacket.getPort());
        serverSocket.send(response);
    }

    private ResultCode getResultCode(RuntimeException e) {
        if (e instanceof FormatErrorException) {
            return ResultCode.FORM_ERROR;
        } else if (e instanceof RefusedException) {
            return ResultCode.REFUSED;
        } else if (e instanceof NotImplementedException) {
            return ResultCode.NOT_IMPLEMENTED;
        }
        return ResultCode.SERVER_FAILURE;
    }

    private void sendError(Integer headerId, ResultCode resultCode) throws IOException {
        DNSHeader dnsHeaderResponse = new DNSHeader(headerId != null ? headerId : 0, false, (short) 0, false, true, resultCode);
        DatagramPacket responsePacket = UDPResponseHeaderSerializer.serialize(dnsHeaderResponse);
        responsePacket.setAddress(queryPacket.getAddress());
        responsePacket.setPort(queryPacket.getPort());
        serverSocket.send(responsePacket);
    }
}

