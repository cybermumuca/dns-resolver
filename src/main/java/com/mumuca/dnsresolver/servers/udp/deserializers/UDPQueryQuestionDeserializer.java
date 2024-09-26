package com.mumuca.dnsresolver.servers.udp.deserializers;

import com.mumuca.dnsresolver.dns.DNSQuestion;
import com.mumuca.dnsresolver.dns.exceptions.QuestionMalformedException;
import com.mumuca.dnsresolver.dns.exceptions.SuspiciousDomainNamePayloadException;
import com.mumuca.dnsresolver.dns.utils.PacketBuffer;
import com.mumuca.dnsresolver.servers.exceptions.FormatErrorException;
import com.mumuca.dnsresolver.servers.exceptions.RefusedException;

public class UDPQueryQuestionDeserializer {
    public static DNSQuestion deserialize(PacketBuffer buffer) {
        try {
            DNSQuestion dnsQuestion = new DNSQuestion();
            dnsQuestion.readBuffer(buffer);
            return dnsQuestion;
        } catch (SuspiciousDomainNamePayloadException e) {
            throw new RefusedException("Suspicious Payload.");
        } catch (QuestionMalformedException e) {
            throw new FormatErrorException("Question malformed.");
        }
    }
}
