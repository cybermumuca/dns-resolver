package com.mumuca.dnsresolver;

import com.mumuca.dnsresolver.servers.UDPServer;

public class DNSResolver {
    public static void main(String[] args) {
        UDPServer udpServer = new UDPServer();

        Thread udpServerThread = new Thread(udpServer);

        udpServerThread.start();
    }
}
