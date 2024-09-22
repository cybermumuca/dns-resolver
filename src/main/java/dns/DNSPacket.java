package dns;

import utils.PacketBuffer;

import java.util.ArrayList;
import java.util.List;

public class DNSPacket {
    public DNSHeader header;
    public List<DNSQuestion> questions;
    public List<DNSRecord> answers;
    public List<DNSRecord> authorities;
    public List<DNSRecord> resources;

    public DNSPacket() {
        this.header = new DNSHeader();
        this.questions = new ArrayList<>();
        this.answers = new ArrayList<>();
        this.authorities = new ArrayList<>();
        this.resources = new ArrayList<>();
    }

    public DNSPacket(DNSHeader header, List<DNSQuestion> questions, List<DNSRecord> answers, List<DNSRecord> authorities, List<DNSRecord> resources) {
        this.header = header;
        this.questions = questions;
        this.answers = answers;
        this.authorities = authorities;
        this.resources = resources;
    }

    public static DNSPacket fromBuffer(PacketBuffer buffer) throws Exception {
        var dnsPacket = new DNSPacket();
        dnsPacket.header.readBuffer(buffer);

        for (short i = 0; i < dnsPacket.header.questionCount; i++) {
            DNSQuestion question = new DNSQuestion();
            question.readBuffer(buffer);
            dnsPacket.questions.add(question);
        }

        for (short i = 0; i < dnsPacket.header.answerRecordCount; i++) {
            DNSRecord answer = DNSRecord.fromBuffer(buffer);
            dnsPacket.answers.add(answer);
        }

        for (short i = 0; i < dnsPacket.header.authoritativeRecordCount; i++) {
            DNSRecord aAnswer = DNSRecord.fromBuffer(buffer);
            dnsPacket.authorities.add(aAnswer);
        }

        for (short i = 0; i < dnsPacket.header.additionalRecordCount; i++) {
            DNSRecord additionalRecord = DNSRecord.fromBuffer(buffer);
            dnsPacket.resources.add(additionalRecord);
        }

        return dnsPacket;
    }

    @Override
    public String toString() {
        return "DNSPacket{" +
                "header=" + header +
                ", questions=" + questions +
                ", answers=" + answers +
                ", authorities=" + authorities +
                ", resources=" + resources +
                '}';
    }
}
