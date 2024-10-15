package com.mumuca.dnsresolver.servers.udp.lookups;

import com.mumuca.dnsresolver.dns.DNSQuery;
import com.mumuca.dnsresolver.dns.DNSResponse;
import com.mumuca.dnsresolver.dns.utils.PacketBuffer;
import com.mumuca.dnsresolver.servers.udp.deserializers.UDPResponseDeserializer;
import com.mumuca.dnsresolver.servers.udp.serializers.UDPQuerySerializer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPDNSLookup implements DNSLookup {
    private static final int DNS_RESOLVER_PORT = 53;
    private static final int DNS_REPLY_BUFFER_SIZE = 512;

    private static final int MAX_RETRIES = 3;
    private static final int TIMEOUT_MS = 1200;

    @Override
    public DNSResponse lookup(DNSQuery dnsQuery, String server) throws IOException {
        int attempts = 0;
        while (attempts < MAX_RETRIES) {
            try (var socket = new DatagramSocket()) {
                socket.setSoTimeout(TIMEOUT_MS);

                DatagramPacket dnsQueryPacket = UDPQuerySerializer.serialize(dnsQuery);
                dnsQueryPacket.setAddress(InetAddress.getByName(server));
                dnsQueryPacket.setPort(DNS_RESOLVER_PORT);

                socket.send(dnsQueryPacket);

                byte[] dnsReplyBuffer = new byte[DNS_REPLY_BUFFER_SIZE];
                DatagramPacket dnsReplyPacket = new DatagramPacket(dnsReplyBuffer, dnsReplyBuffer.length);
                socket.receive(dnsReplyPacket);

                PacketBuffer dnsReplyPacketBuffer = new PacketBuffer(dnsReplyPacket.getData());
                return UDPResponseDeserializer.deserialize(dnsReplyPacketBuffer);
            } catch (IOException e) {
                attempts++;
                if (attempts >= MAX_RETRIES) {
                    throw new IOException("Failed to receive DNS response after " + MAX_RETRIES + " attempts", e);
                }
            }
        }
        throw new IOException("Unexpected error in DNS lookup");
    }
}