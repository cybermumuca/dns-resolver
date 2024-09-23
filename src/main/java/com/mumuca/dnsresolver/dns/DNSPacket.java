package com.mumuca.dnsresolver.dns;

import com.mumuca.dnsresolver.dns.enums.QueryType;
import com.mumuca.dnsresolver.utils.PacketBuffer;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class DNSPacket {
    /**
     * Representa a encapsulação de um pacote da mensagem DNS.
     *
     * <p>Esta classe encapsula a estrutura de um pacote DNS, conforme definido na
     * <a href="https://datatracker.ietf.org/doc/html/rfc1035#section-4.1">RFC 1035</a>.
     *
     * <p><strong>Atributos:</strong>
     * <ul>
     *   <li><strong>header</strong> - O cabeçalho do pacote DNS, que contém informações
     *   sobre a consulta, como ID, contagem de perguntas e flags.</li>
     *   <li><strong>questions</strong> - Uma lista de perguntas DNS que são incluídas
     *   no pacote, cada uma representando uma consulta a um nome de domínio.</li>
     *   <li><strong>answers</strong> - Uma lista de registros de resposta recebidos
     *   do servidor DNS, que contém as informações solicitadas.</li>
     *   <li><strong>authorities</strong> - Uma lista de registros autoritativos,
     *   que fornece informações sobre o servidor que fornece a resposta.</li>
     *   <li><strong>resources</strong> - Uma lista de registros adicionais que podem
     *   ajudar na resolução da consulta.</li>
     * </ul>
     *
     * @see DNSHeader
     * @see DNSQuestion
     * @see DNSRecord
     */

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

    public void writeInBuffer(PacketBuffer buffer) throws Exception {
        this.header.questionCount = this.questions.size();
        this.header.answerRecordCount = this.answers.size();
        this.header.authoritativeRecordCount = this.authorities.size();
        this.header.additionalRecordCount = this.resources.size();

        this.header.writeInBuffer(buffer);

        for (DNSQuestion question : questions) {
            question.writeInBuffer(buffer);
        }

        for (DNSRecord answer : answers) {
            answer.writeInBuffer(buffer);
        }

        for (DNSRecord authority : authorities) {
            authority.writeInBuffer(buffer);
        }

        for (DNSRecord additionalRecord : resources) {
            additionalRecord.writeInBuffer(buffer);
        }
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

    public static void main(String[] args) throws Exception {
        String qname = "github.com";
        QueryType qtype = QueryType.A;

        var socket = new DatagramSocket();

        DNSPacket packet = new DNSPacket();

        packet.header.id = 8920;
        packet.header.questionCount = 1;
        packet.header.recursionDesired = true;
        packet.header.query = true;
        packet.questions.add(new DNSQuestion(qname, qtype, (short) 1));

        PacketBuffer buffer = new PacketBuffer();
        packet.writeInBuffer(buffer);

        DatagramPacket dnsQueryPacket = new DatagramPacket(buffer.getBuffer(), buffer.getBuffer().length, InetAddress.getByName("8.8.8.8"), 53);

        socket.send(dnsQueryPacket);

        byte[] dnsReplyPacket = new byte[1024];
        DatagramPacket responsePacket = new DatagramPacket(dnsReplyPacket, dnsReplyPacket.length);

        socket.receive(responsePacket);

        PacketBuffer replyBuffer = new PacketBuffer(responsePacket.getData());
        DNSPacket replyPacket = DNSPacket.fromBuffer(replyBuffer);
        System.out.println(replyPacket.header);
        System.out.println(replyPacket.questions);
        System.out.println(replyPacket.answers);
        System.out.println(replyPacket.authorities);
        System.out.println(replyPacket.resources);
        socket.close();
    }
}
