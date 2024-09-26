package com.mumuca.dnsresolver.servers.udp.serializers;

import com.mumuca.dnsresolver.dns.DNSResponse;
import com.mumuca.dnsresolver.dns.exceptions.InvalidHeaderSizeException;
import com.mumuca.dnsresolver.dns.exceptions.QueryTypeUnsupportedException;
import com.mumuca.dnsresolver.servers.exceptions.ServerFailureException;
import com.mumuca.dnsresolver.dns.utils.PacketBuffer;

import java.net.DatagramPacket;

public class UDPResponseSerializer {
    public static DatagramPacket serialize(DNSResponse response) {
        try {
            PacketBuffer responseBuffer = new PacketBuffer();
            response.writeToBuffer(responseBuffer);
            return new DatagramPacket(responseBuffer.getBuffer(), responseBuffer.getPosition());
        } catch (InvalidHeaderSizeException | QueryTypeUnsupportedException e) {
            throw new ServerFailureException("Something went wrong while serializing the response.");
        }
    }
}
