package com.mumuca.dnsresolver.servers.udp;

import com.mumuca.dnsresolver.servers.udp.processors.UDPQueryProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UDPServer implements Runnable {
    private static final Marker fatal = MarkerFactory.getMarker("FATAL");
    private static final Logger logger = LoggerFactory.getLogger(UDPServer.class);

    private final ExecutorService executorService;

    public UDPServer() {
        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(3053)) {
            logger.info("Listening on port 3053...");

            while (true) {
                byte[] buffer = new byte[512];

                DatagramPacket queryPacket = new DatagramPacket(buffer, buffer.length);

                try {
                    socket.receive(queryPacket);

                    logger.debug("Received a packet from {}:{}", queryPacket.getAddress(), queryPacket.getPort());

                    executorService.submit(new UDPQueryProcessor(socket, queryPacket));
                } catch (Exception e) {
                    logger.error("Error while receiving packet", e);
                }
            }
        } catch (Exception ex) {
            logger.error(fatal, "FATAL Error", ex);
        } finally {
            logger.info("Shutting down the executor service.");
            this.executorService.shutdown();
        }
    }
}
