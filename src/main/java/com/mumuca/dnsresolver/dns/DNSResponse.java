package com.mumuca.dnsresolver.dns;

import com.mumuca.dnsresolver.dns.exceptions.*;
import com.mumuca.dnsresolver.dns.records.ResourceRecord;
import com.mumuca.dnsresolver.dns.utils.PacketBuffer;

import java.util.ArrayList;
import java.util.List;

public class DNSResponse {

    private final DNSHeader dnsHeader;
    private final DNSQuestion dnsQuestion;
    private final List<ResourceRecord> answerRecords;
    private final List<ResourceRecord> authorityRecords;
    private final List<ResourceRecord> additionalRecords;

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

    public DNSQuestion getQuery() {
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

        List<DNSQuestion> dnsQuestions = new ArrayList<>(dnsHeader.questionCount);

        for (short i = 0; i < dnsHeader.questionCount; i++) {
            DNSQuestion question = new DNSQuestion();
            question.readBuffer(buffer);
            dnsQuestions.add(question);
        }

        List<ResourceRecord> answerRecords = new ArrayList<>(dnsHeader.answerRecordCount);

        for (short i = 0; i < dnsHeader.answerRecordCount; i++) {
            ResourceRecord answerRecord = DNSRecord.fromBuffer(buffer);
            answerRecords.add(answerRecord);
        }

        List<ResourceRecord> authorityRecords = new ArrayList<>(dnsHeader.authoritativeRecordCount);

        for (short i = 0; i < dnsHeader.authoritativeRecordCount; i++) {
            ResourceRecord authorityRecord = DNSRecord.fromBuffer(buffer);
            authorityRecords.add(authorityRecord);
        }

        List<ResourceRecord> additionalRecords = new ArrayList<>(dnsHeader.additionalRecordCount);

        for (short i = 0; i < dnsHeader.additionalRecordCount; i++) {
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
}
