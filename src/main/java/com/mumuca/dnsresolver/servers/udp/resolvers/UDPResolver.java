package com.mumuca.dnsresolver.servers.udp.resolvers;

import com.mumuca.dnsresolver.dns.DNSQuery;
import com.mumuca.dnsresolver.dns.DNSResponse;
import com.mumuca.dnsresolver.dns.enums.QueryType;
import com.mumuca.dnsresolver.servers.exceptions.ServerFailureException;
import com.mumuca.dnsresolver.servers.udp.deserializers.UDPResponseDeserializer;
import com.mumuca.dnsresolver.dns.utils.PacketBuffer;
import com.mumuca.dnsresolver.servers.udp.serializers.UDPQuerySerializer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPResolver {
    public static DNSResponse query(int id, boolean withRecursion, String name, QueryType queryType, short qClass) {
        try (var socket = new DatagramSocket()) {
            DNSQuery dnsQuery = DNSQuery.of(id, withRecursion, name, queryType, qClass);

            DatagramPacket dnsQueryPacket = UDPQuerySerializer.serialize(dnsQuery);

            dnsQueryPacket.setAddress(InetAddress.getByName("8.8.8.8"));
            dnsQueryPacket.setPort(53);

            socket.send(dnsQueryPacket);

            byte[] dnsReplyPacket = new byte[512];

            DatagramPacket responsePacket = new DatagramPacket(dnsReplyPacket, dnsReplyPacket.length);

            socket.receive(responsePacket);

            PacketBuffer replyBuffer = new PacketBuffer(responsePacket.getData());

            return UDPResponseDeserializer.deserialize(replyBuffer);
        } catch (IOException e) {
            throw new ServerFailureException("Something went wrong.");
        }
    }
}
