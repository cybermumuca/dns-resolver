package com.mumuca.dnsresolver.servers.udp.serializers;

import com.mumuca.dnsresolver.dns.DNSHeader;
import com.mumuca.dnsresolver.dns.exceptions.InvalidHeaderSizeException;
import com.mumuca.dnsresolver.dns.utils.PacketBuffer;

import java.net.DatagramPacket;

public class UDPResponseHeaderSerializer {
    public static DatagramPacket serialize(DNSHeader dnsHeader) {
        try {
            PacketBuffer responseBuffer = new PacketBuffer();
            dnsHeader.writeToBuffer(responseBuffer);
            return new DatagramPacket(responseBuffer.getBuffer(), responseBuffer.getPosition());
        } catch (InvalidHeaderSizeException e) {
            throw new RuntimeException(e);
        }
    }
}
