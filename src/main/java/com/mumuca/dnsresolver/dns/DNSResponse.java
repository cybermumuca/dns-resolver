package com.mumuca.dnsresolver.dns;

import com.mumuca.dnsresolver.dns.exceptions.*;
import com.mumuca.dnsresolver.dns.utils.PacketBuffer;

import java.util.ArrayList;
import java.util.List;

public class DNSResponse {

    private final DNSHeader dnsHeader;
    private final DNSQuestion dnsQuestion;
    private final List<DNSRecord> answerRecords;
    private final List<DNSRecord> authorityRecords;
    private final List<DNSRecord> additionalRecords;

    private DNSResponse(DNSHeader dnsHeader, DNSQuestion dnsQuestion, List<DNSRecord> answerRecords, List<DNSRecord> authorityRecords, List<DNSRecord> additionalRecords) {
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

    public List<DNSRecord> getAnswerRecords() {
        return answerRecords;
    }

    public List<DNSRecord> getAuthorityRecords() {
        return authorityRecords;
    }

    public List<DNSRecord> getAdditionalRecords() {
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

        List<DNSRecord> answerRecords = new ArrayList<>(dnsHeader.answerRecordCount);

        for (short i = 0; i < dnsHeader.answerRecordCount; i++) {
            DNSRecord answerRecord = DNSRecord.fromBuffer(buffer);
            answerRecords.add(answerRecord);
        }

        List<DNSRecord> authorityRecords = new ArrayList<>(dnsHeader.authoritativeRecordCount);

        for (short i = 0; i < dnsHeader.authoritativeRecordCount; i++) {
            DNSRecord authorityRecord = DNSRecord.fromBuffer(buffer);
            authorityRecords.add(authorityRecord);
        }

        List<DNSRecord> additionalRecords = new ArrayList<>(dnsHeader.additionalRecordCount);

        for (short i = 0; i < dnsHeader.additionalRecordCount; i++) {
            DNSRecord additionalRecord = DNSRecord.fromBuffer(buffer);
            additionalRecords.add(additionalRecord);
        }
        
        return new DNSResponse(dnsHeader, dnsQuestions.getFirst(), answerRecords, authorityRecords, additionalRecords);
    }

    public void writeToBuffer(PacketBuffer buffer) throws InvalidHeaderSizeException, QueryTypeUnsupportedException {
        this.dnsHeader.writeToBuffer(buffer);

        this.dnsQuestion.writeToBuffer(buffer);

        for (DNSRecord answerRecord : answerRecords) {
            answerRecord.writeInBuffer(buffer);
        }

        for (DNSRecord authorityRecord : authorityRecords) {
            authorityRecord.writeInBuffer(buffer);
        }

        for (DNSRecord additionalRecord : additionalRecords) {
            additionalRecord.writeInBuffer(buffer);
        }
    }
}
