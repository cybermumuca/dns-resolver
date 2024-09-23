package com.mumuca.dnsresolver.servers;

import com.mumuca.dnsresolver.dns.DNSPacket;
import com.mumuca.dnsresolver.utils.PacketBuffer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPServer implements Runnable {

    public UDPServer() {}

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(3053)) {
            System.out.println("UDP Server listening on port 3053...");

            while (true) {
                byte[] buffer = new byte[512];

                DatagramPacket queryPacket = new DatagramPacket(buffer, buffer.length);

                socket.receive(queryPacket);

                byte[] queryPacketData = queryPacket.getData();

                PacketBuffer queryPacketDataBuffer = new PacketBuffer(queryPacketData);

                DNSPacket queryDNSPacket = DNSPacket.fromBuffer(queryPacketDataBuffer);

                System.out.println(queryDNSPacket);
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
