package com.mumuca.dnsresolver.dns;

import com.mumuca.dnsresolver.dns.exceptions.*;
import com.mumuca.dnsresolver.dns.records.A;
import com.mumuca.dnsresolver.dns.records.NS;
import com.mumuca.dnsresolver.dns.records.ResourceRecord;
import com.mumuca.dnsresolver.dns.utils.PacketBuffer;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class DNSResponse {

    private final DNSHeader dnsHeader;
    private final DNSQuestion dnsQuestion;
    private final List<ResourceRecord> answerRecords;
    private final List<ResourceRecord> authorityRecords;
    private final List<ResourceRecord> additionalRecords;

    public DNSResponse(DNSQuery dnsQuery, List<ResourceRecord> answerRecords, List<ResourceRecord> authorityRecords) {
        this.dnsHeader = dnsQuery.getHeader();
        this.dnsQuestion = dnsQuery.getQuestion();
        this.answerRecords = answerRecords;
        this.authorityRecords = authorityRecords;
        this.additionalRecords = new ArrayList<>(0);
    }

    private DNSResponse(DNSHeader dnsHeader, DNSQuestion dnsQuestion, List<ResourceRecord> answerRecords, List<ResourceRecord> authorityRecords, List<ResourceRecord> additionalRecords) {
        this.dnsHeader = dnsHeader;
        this.dnsQuestion = dnsQuestion;
        this.answerRecords = answerRecords;
        this.authorityRecords = authorityRecords;
        this.additionalRecords = additionalRecords;
    }

    public DNSHeader getHeader() {
        return dnsHeader;
    }

    public DNSQuestion getQuestion() {
        return dnsQuestion;
    }

    public List<ResourceRecord> getAnswerRecords() {
        return answerRecords;
    }

    public List<ResourceRecord> getAuthorityRecords() {
        return authorityRecords;
    }

    public List<ResourceRecord> getAdditionalRecords() {
        return additionalRecords;
    }


    public static DNSResponse fromBuffer(PacketBuffer buffer) throws InvalidHeaderSizeException, SuspiciousDomainNamePayloadException, QuestionMalformedException, QueryTypeUnsupportedException, ResourceRecordMalformedException {
        DNSHeader dnsHeader = DNSHeader.fromBuffer(buffer);

        List<DNSQuestion> dnsQuestions = new ArrayList<>(dnsHeader.getQuestionCount());

        for (short i = 0; i < dnsHeader.getQuestionCount(); i++) {
            DNSQuestion question = new DNSQuestion();
            question.readBuffer(buffer);
            dnsQuestions.add(question);
        }

        List<ResourceRecord> answerRecords = new ArrayList<>(dnsHeader.getAnswerRecordCount());

        for (short i = 0; i < dnsHeader.getAnswerRecordCount(); i++) {
            ResourceRecord answerRecord = DNSRecord.fromBuffer(buffer);
            answerRecords.add(answerRecord);
        }

        List<ResourceRecord> authorityRecords = new ArrayList<>(dnsHeader.getAuthoritativeRecordCount());

        for (short i = 0; i < dnsHeader.getAuthoritativeRecordCount(); i++) {
            ResourceRecord authorityRecord = DNSRecord.fromBuffer(buffer);
            authorityRecords.add(authorityRecord);
        }

        List<ResourceRecord> additionalRecords = new ArrayList<>(dnsHeader.getAdditionalRecordCount());

        for (short i = 0; i < dnsHeader.getAdditionalRecordCount(); i++) {
            ResourceRecord additionalRecord = DNSRecord.fromBuffer(buffer);
            additionalRecords.add(additionalRecord);
        }
        
        return new DNSResponse(dnsHeader, dnsQuestions.getFirst(), answerRecords, authorityRecords, additionalRecords);
    }

    public void writeToBuffer(PacketBuffer buffer) throws InvalidHeaderSizeException, QueryTypeUnsupportedException {
        this.dnsHeader.writeToBuffer(buffer);

        this.dnsQuestion.writeToBuffer(buffer);

        for (ResourceRecord answerRecord : answerRecords) {
            answerRecord.writeToBuffer(buffer);
        }

        for (ResourceRecord authorityRecord : authorityRecords) {
            authorityRecord.writeToBuffer(buffer);
        }

        for (ResourceRecord additionalRecord : additionalRecords) {
            additionalRecord.writeToBuffer(buffer);
        }
    }

    private Stream<String[]> getNS(String qname) {
        return this.authorityRecords.stream()
                .filter(record -> record instanceof NS)
                .map(record -> {
                    NS nsRecord = (NS) record;
                    return new String[]{nsRecord.name(), nsRecord.ns()};
                })
                .filter(tuple -> qname.endsWith(tuple[0]));
    }

    public Optional<String> getResolvedNS(String qname) {
        return getNS(qname)
                .flatMap(pair -> additionalRecords.stream()
                        .filter(record -> record instanceof A)
                        .filter(record -> record.name().equals(pair[1]))
                        .map(record -> ((A) record).address())
                )
                .findFirst();
    }

    public Optional<String> getUnresolvedNS(String qname) {
        return getNS(qname)
                .map(ns -> ns[1])
                .findFirst();
    }
}
