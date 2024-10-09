package com.mumuca.dnsresolver.servers.udp.resolvers;

import com.mumuca.dnsresolver.dns.DNSQuery;
import com.mumuca.dnsresolver.dns.DNSResponse;
import com.mumuca.dnsresolver.dns.utils.PacketBuffer;
import com.mumuca.dnsresolver.servers.exceptions.ServerFailureException;
import com.mumuca.dnsresolver.servers.resolvers.Resolver;
import com.mumuca.dnsresolver.servers.udp.deserializers.UDPResponseDeserializer;
import com.mumuca.dnsresolver.servers.udp.serializers.UDPQuerySerializer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class CloudflareDNSResolver extends Resolver {

    private static final String CLOUDFLARE_DNS_RESOLVER_URL = "1.1.1.1";
    private static final int CLOUDFLARE_DNS_RESOLVER_PORT = 53;

    @Override
    public DNSResponse resolve(DNSQuery dnsQuery) {
        try (var socket = new DatagramSocket()) {
            DatagramPacket dnsQueryPacket = UDPQuerySerializer.serialize(dnsQuery);

            dnsQueryPacket.setAddress(InetAddress.getByName(CLOUDFLARE_DNS_RESOLVER_URL));
            dnsQueryPacket.setPort(CLOUDFLARE_DNS_RESOLVER_PORT);

            socket.send(dnsQueryPacket);

            byte[] dnsReplyPacket = new byte[512];

            DatagramPacket responsePacket = new DatagramPacket(dnsReplyPacket, dnsReplyPacket.length);

            socket.receive(responsePacket);

            PacketBuffer replyBuffer = new PacketBuffer(responsePacket.getData());

            return UDPResponseDeserializer.deserialize(replyBuffer);
        } catch (IOException e) {
            throw new ServerFailureException("Error sending upstream request");
        }
    }
}
