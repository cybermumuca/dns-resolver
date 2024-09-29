package com.mumuca.dnsresolver.servers.udp.contexts;

import com.mumuca.dnsresolver.dns.DNSHeader;
import com.mumuca.dnsresolver.dns.DNSQuestion;
import com.mumuca.dnsresolver.dns.utils.PacketBuffer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class DNSQueryContext {
    private final DatagramPacket queryPacket;
    private final DatagramSocket serverSocket;
    private final PacketBuffer queryPacketDataBuffer;
    private DNSHeader dnsHeader;
    private DNSQuestion dnsQuestion;

    public DNSQueryContext(DatagramPacket queryPacket, DatagramSocket serverSocket) {
        this.queryPacket = queryPacket;
        this.serverSocket = serverSocket;
        this.queryPacketDataBuffer = new PacketBuffer(this.queryPacket.getData());
    }

    public PacketBuffer getQueryPacketDataBuffer() {
        return queryPacketDataBuffer;
    }

    public void setDnsHeader(DNSHeader dnsHeader) {
        this.dnsHeader = dnsHeader;
    }

    public DNSHeader getDnsHeader() {
        return dnsHeader;
    }

    public void setDnsQuestion(DNSQuestion dnsQuestion) {
        this.dnsQuestion = dnsQuestion;
    }

    public DNSQuestion getDnsQuestion() {
        return dnsQuestion;
    }

    public DatagramPacket getQueryPacket() {
        return queryPacket;
    }

    public DatagramSocket getServerSocket() {
        return serverSocket;
    }
}
