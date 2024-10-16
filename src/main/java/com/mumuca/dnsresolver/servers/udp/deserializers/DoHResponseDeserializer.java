package com.mumuca.dnsresolver.servers.udp.deserializers;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.mumuca.dnsresolver.dns.DNSHeader;
import com.mumuca.dnsresolver.dns.DNSQuery;
import com.mumuca.dnsresolver.dns.DNSResponse;
import com.mumuca.dnsresolver.dns.enums.QueryType;
import com.mumuca.dnsresolver.dns.enums.ResultCode;
import com.mumuca.dnsresolver.dns.records.*;
import com.mumuca.dnsresolver.servers.exceptions.NotImplementedException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DoHResponseDeserializer extends JsonDeserializer<DNSResponse> {

    private static final short INTERNET_CLASS = 1;
    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    private final DNSQuery dnsQuery;

    public DoHResponseDeserializer(DNSQuery dnsQuery) {
        this.dnsQuery = dnsQuery;
    }

    @Override
    public DNSResponse deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode rootNode = jsonParser.getCodec().readTree(jsonParser);

        DNSHeader header = dnsQuery.getHeader();

        header.setResultCode(ResultCode.fromCode(rootNode.get("Status").asInt()));
        header.setTruncatedMessage(rootNode.get("TC").asBoolean());

        List<ResourceRecord> answerRecords = new ArrayList<>();
        List<ResourceRecord> authorityRecords = new ArrayList<>();

        processRecords(rootNode.get("Answer"), answerRecords);
        processRecords(rootNode.get("Authority"), authorityRecords);

        return new DNSResponse(dnsQuery, answerRecords, authorityRecords);
    }

    private void processRecords(JsonNode recordsNode, List<ResourceRecord> recordList) {
        if (recordsNode != null && recordsNode.isArray()) {
            recordsNode.forEach(node -> processResourceRecord(node, recordList));
        }
    }

    private void processResourceRecord(JsonNode node, List<ResourceRecord> recordList) {
        String name = Optional.ofNullable(node.get("name"))
                .map(JsonNode::asText)
                .filter(n -> !n.isEmpty())
                .orElse(dnsQuery.getQuestion().qName);
        int ttl = Optional.ofNullable(node.get("TTL"))
                .map(JsonNode::asInt)
                .orElse(0);
        QueryType queryType = QueryType.fromValue(node.get("type").asInt());
        String data = node.get("data").asText();
        int dataLength = data.getBytes(UTF_8).length;

        switch (queryType) {
            case A -> {
                A aRecord = new A(name, (short) 1, ttl, dataLength, data);
                recordList.add(aRecord);
            }
            case NS -> {
                NS nsRecord = new NS(name, (short) 1, ttl, dataLength, data);
                recordList.add(nsRecord);
            }
            case CNAME -> {
                CNAME cnameRecord = new CNAME(name, (short) 1, ttl, dataLength, data);
                recordList.add(cnameRecord);
            }
            case SOA -> {
                String[] soaFields = data.split(" ");
                String mname = soaFields[0];
                String rname = soaFields[1];
                long serial = Long.parseLong(soaFields[2]);
                int refresh = Integer.parseInt(soaFields[3]);
                int retry = Integer.parseInt(soaFields[4]);
                long expire = Long.parseLong(soaFields[5]);
                int minimum = Integer.parseInt(soaFields[6]);

                SOA soaRecord = new SOA(name, (short) 1, ttl, dataLength, mname, rname, serial, refresh, retry, expire, minimum);
                recordList.add(soaRecord);
            }
            case PTR -> {
                PTR ptrRecord = new PTR(name, (short) 1, ttl, dataLength, data);
                recordList.add(ptrRecord);
            }
            case MX -> {
                String[] mxFields = data.split(" ");
                int priority = Integer.parseInt(mxFields[0]);
                String mailServer = mxFields[1];
                MX mxRecord = new MX(name, (short) 1, ttl, dataLength, priority, mailServer);
                recordList.add(mxRecord);
            }
            case AAAA -> {
                AAAA aaaaRecord = new AAAA(name, (short) 1, ttl, dataLength, data);
                recordList.add(aaaaRecord);
            }
            default -> throw new NotImplementedException("Unsupported QueryType: " + queryType);
        }
    }
}
