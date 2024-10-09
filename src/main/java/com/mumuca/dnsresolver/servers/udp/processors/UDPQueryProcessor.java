package com.mumuca.dnsresolver.servers.udp.processors;

import com.mumuca.dnsresolver.servers.resolvers.Resolver;
import com.mumuca.dnsresolver.servers.udp.contexts.DNSQueryContext;
import com.mumuca.dnsresolver.servers.udp.handlers.AbstractHandler;
import com.mumuca.dnsresolver.servers.udp.handlers.HeaderDeserializationHandler;
import com.mumuca.dnsresolver.servers.udp.handlers.QueryHandler;
import com.mumuca.dnsresolver.servers.udp.handlers.QuestionDeserializationHandler;
import com.mumuca.dnsresolver.servers.udp.resolvers.CloudflareDNSResolver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPQueryProcessor implements Runnable {
    private final DatagramSocket serverSocket;
    private final DatagramPacket queryPacket;

    public UDPQueryProcessor(DatagramSocket serverSocket, DatagramPacket queryPacket) {
        this.serverSocket = serverSocket;
        this.queryPacket = queryPacket;
    }

    @Override
    public void run() {
        process();
    }

    private void process() {
        DNSQueryContext context = new DNSQueryContext(queryPacket, serverSocket);

        AbstractHandler headerDeserializationHandler = new HeaderDeserializationHandler();
        AbstractHandler questionDeserializationHandler = new QuestionDeserializationHandler();

        Resolver cloudflareDNSResolver = new CloudflareDNSResolver();
        AbstractHandler queryHandler = new QueryHandler(cloudflareDNSResolver);

        headerDeserializationHandler.setNext(questionDeserializationHandler);
        questionDeserializationHandler.setNext(queryHandler);

        try {
            headerDeserializationHandler.handle(context);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}

