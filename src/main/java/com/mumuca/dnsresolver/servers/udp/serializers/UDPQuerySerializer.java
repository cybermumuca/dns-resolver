package com.mumuca.dnsresolver.servers.udp.serializers;

import com.mumuca.dnsresolver.dns.DNSQuery;
import com.mumuca.dnsresolver.dns.exceptions.InvalidHeaderSizeException;
import com.mumuca.dnsresolver.dns.utils.PacketBuffer;
import com.mumuca.dnsresolver.servers.exceptions.ServerFailureException;

import java.net.DatagramPacket;

public class UDPQuerySerializer {
    public static DatagramPacket serialize(DNSQuery query) {
        PacketBuffer packetBuffer = new PacketBuffer();

        try {
            query.writeToBuffer(packetBuffer);
        } catch (InvalidHeaderSizeException e) {
            throw new ServerFailureException("Something went wrong while serializing a query.");
        }

        return new DatagramPacket(packetBuffer.getBuffer(), packetBuffer.getPosition());
    }
}
