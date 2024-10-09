package com.mumuca.dnsresolver.servers.udp.resolvers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.mumuca.dnsresolver.dns.DNSQuery;
import com.mumuca.dnsresolver.dns.DNSQuestion;
import com.mumuca.dnsresolver.dns.DNSResponse;
import com.mumuca.dnsresolver.servers.exceptions.ServerFailureException;
import com.mumuca.dnsresolver.servers.resolvers.Resolver;
import com.mumuca.dnsresolver.servers.udp.deserializers.DoHResponseDeserializer;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class CloudflareDoHResolver extends Resolver {
    private static final String DOH_URL = "https://1.1.1.1/dns-query";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public CloudflareDoHResolver() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public DNSResponse resolve(DNSQuery dnsQuery) {
        DNSQuestion question = dnsQuery.getQuestion();

        String qName = URLEncoder.encode(question.qName, StandardCharsets.UTF_8);
        String queryType = URLEncoder.encode(String.valueOf(question.qType.getValue()), StandardCharsets.UTF_8);

        String urlString = String.format("%s?name=%s&type=%s", DOH_URL, qName, queryType);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .header("Accept", "application/dns-json")
                .version(HttpClient.Version.HTTP_2)
                .timeout(Duration.ofMillis(2000))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                SimpleModule module = new SimpleModule();

                module.addDeserializer(DNSResponse.class, new DoHResponseDeserializer(dnsQuery));

                objectMapper.registerModule(module);

                return objectMapper.readValue(response.body(), DNSResponse.class);
            } else {
                throw new ServerFailureException("Failed to resolve DoH: HTTP error code " + response.statusCode());
            }

        } catch (JsonProcessingException e) {
            throw new ServerFailureException("Error deserializing response from upstream server");
        } catch (IOException e) {
            throw new ServerFailureException("Error sending upstream request");
        } catch (InterruptedException e) {
            throw new ServerFailureException("Thread interrupted");
        }
    }
}
