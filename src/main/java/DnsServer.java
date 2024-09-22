import dns.DNSPacket;
import utils.PacketBuffer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class DnsServer {
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

                System.out.println(dnsPacket);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
