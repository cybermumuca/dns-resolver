package com.mumuca.dnsresolver.servers.handlers;

import com.mumuca.dnsresolver.dns.DNSPacket;
import com.mumuca.dnsresolver.utils.PacketBuffer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPQueryHandler implements Runnable {

    private final DatagramSocket serverSocket;
    private final DatagramPacket queryPacket;


    public UDPQueryHandler(DatagramSocket serverSocket, DatagramPacket queryPacket) {
        this.serverSocket = serverSocket;
        this.queryPacket = queryPacket;
    }

    @Override
    public void run() {
        PacketBuffer queryPacketDataBuffer = new PacketBuffer(queryPacket.getData());
        DNSPacket queryDNSPacket = DNSPacket.fromBuffer(queryPacketDataBuffer);
        System.out.println(queryDNSPacket);
    }
}
