package com.mumuca.dnsresolver.servers.udp.processors;

import com.mumuca.dnsresolver.servers.resolvers.Resolver;
import com.mumuca.dnsresolver.servers.udp.UDPServer;
import com.mumuca.dnsresolver.servers.udp.contexts.DNSQueryContext;
import com.mumuca.dnsresolver.servers.udp.handlers.AbstractHandler;
import com.mumuca.dnsresolver.servers.udp.handlers.HeaderDeserializationHandler;
import com.mumuca.dnsresolver.servers.udp.handlers.QueryHandler;
import com.mumuca.dnsresolver.servers.udp.handlers.QuestionDeserializationHandler;
import com.mumuca.dnsresolver.servers.udp.resolvers.GoogleDNSResolver;
import com.mumuca.dnsresolver.servers.udp.resolvers.UDPDNSResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPQueryProcessor implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(UDPQueryProcessor.class);

    private final DatagramSocket serverSocket;
    private final DatagramPacket queryPacket;

    public UDPQueryProcessor(DatagramSocket serverSocket, DatagramPacket queryPacket) {
        this.serverSocket = serverSocket;
        this.queryPacket = queryPacket;
    }

    @Override
    public void run() {
        Thread.currentThread()
                .setName("UDPQueryProcessor-" + queryPacket.getAddress().getHostAddress() + ":" + queryPacket.getPort());
        logger.debug("Processing packet from {}:{}", queryPacket.getAddress(), queryPacket.getPort());
        process();
    }

    private void process() {
        DNSQueryContext context = new DNSQueryContext(queryPacket, serverSocket);

        AbstractHandler headerDeserializationHandler = new HeaderDeserializationHandler();
        AbstractHandler questionDeserializationHandler = new QuestionDeserializationHandler();

        Resolver googleDNSResolver = new GoogleDNSResolver();
        AbstractHandler queryHandler = new QueryHandler(googleDNSResolver);

        headerDeserializationHandler.setNext(questionDeserializationHandler);
        questionDeserializationHandler.setNext(queryHandler);

        try {
            headerDeserializationHandler.handle(context);
        } catch (IOException e) {
            logger.error("Error processing query from {}: {}", queryPacket.getAddress(), e.getMessage(), e);
        }
    }
}

