package com.mumuca.dnsresolver;

import com.mumuca.dnsresolver.dns.DNSPacket;
import com.mumuca.dnsresolver.utils.PacketBuffer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class DNSResolver {
    public static void main(String[] args) {
        try (DatagramSocket serverSocket = new DatagramSocket(3053)) {
            byte[] buffer = new byte[512];
            System.out.println("Binded in 127.0.0.1:3053\n");

            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);

                serverSocket.receive(request);

                byte[] requestData = request.getData();

                PacketBuffer requestBuffer = new PacketBuffer(requestData);

                DNSPacket dnsPacket = DNSPacket.fromBuffer(requestBuffer);

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
