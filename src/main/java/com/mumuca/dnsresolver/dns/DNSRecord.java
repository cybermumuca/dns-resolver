package com.mumuca.dnsresolver.dns;

import com.mumuca.dnsresolver.dns.enums.QueryType;
import com.mumuca.dnsresolver.dns.exceptions.QueryTypeUnsupportedException;
import com.mumuca.dnsresolver.dns.exceptions.ResourceRecordMalformedException;
import com.mumuca.dnsresolver.dns.records.*;
import com.mumuca.dnsresolver.dns.utils.PacketBuffer;
import com.mumuca.dnsresolver.dns.utils.exceptions.BufferPositionOutOfBoundsException;
import com.mumuca.dnsresolver.dns.utils.exceptions.EndOfBufferException;
import com.mumuca.dnsresolver.dns.utils.exceptions.JumpLimitExceededException;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class DNSRecord {
    /**
     * Representa o RR (Resource Record) de uma mensagem DNS.
     *
     * <p>Esta classe encapsula os detalhes de um registro de recurso DNS, conforme
     * definido na <a href="https://datatracker.ietf.org/doc/html/rfc1035#section-4.1.3">RFC 1035</a>.
     *
     * <p><strong>Atributos:</strong>
     * <ul>
     *   <li><strong>name</strong> - O nome do domínio associado a este registro
     *   de recurso.</li>
     *   <li><strong>type</strong> - O tipo do registro DNS, que especifica o
     *   tipo de informação contida (por exemplo, A, AAAA, CNAME, etc.).</li>
     *   <li><strong>qClass</strong> - A classe do registro, definida
     *   como 1 para registros da Internet.</li>
     *   <li><strong>ttl</strong> - O tempo de vida (Time to Live) do registro,
     *   que indica quanto tempo ele deve ser mantido em cache.</li>
     *   <li><strong>length</strong> - O comprimento dos dados do registro em bytes.</li>
     *   <li><strong>address</strong> - O endereço associado ao registro, que pode ser
     *   um endereço IP ou outro tipo de dado, dependendo do tipo do registro.
     *   </li>
     *   <li><strong>priority</strong> - A prioridade do registro, aplicável
     *   especialmente para registros MX (Mail Exchange), onde uma prioridade mais baixa
     *   indica uma preferência maior para o servidor de e-mail.</li>
     * </ul>
     *
     * @see QueryType
     */

    public static ResourceRecord fromBuffer(PacketBuffer buffer) throws QueryTypeUnsupportedException, ResourceRecordMalformedException {
        try {
            String name = buffer.readQName();

            QueryType type = QueryType.fromValue(buffer.read16b());
            short qClass = (short) buffer.read16b();
            int ttl = buffer.read32b();
            int length = buffer.read16b();

            switch (type) {
                case A -> {
                    int rawAddress = buffer.read32b();
                    InetAddress inetAddress = Inet4Address.getByAddress(new byte[]{
                            (byte) (rawAddress >> 24 & 0xFF),
                            (byte) (rawAddress >> 16 & 0xFF),
                            (byte) (rawAddress >> 8 & 0xFF),
                            (byte) (rawAddress & 0xFF)
                    });
                    String address = inetAddress.getHostAddress();
                    return new A(name, qClass, ttl, length, address);
                }
                case NS -> {
                    String ns = buffer.readQName();
                    return new NS(name, qClass, ttl, length, ns);
                }
                case CNAME -> {
                    String cname = buffer.readQName();
                    return new CNAME(name, qClass, ttl, length, cname);
                }
                case MX -> {
                    int priority = buffer.read16b();
                    String mx = buffer.readQName();

                    return new MX(name, qClass, ttl, length, priority, mx);
                }
                case AAAA -> {
                    int rawAddress1 = buffer.read32b();
                    int rawAddress2 = buffer.read32b();
                    int rawAddress3 = buffer.read32b();
                    int rawAddress4 = buffer.read32b();

                    InetAddress inetAddress = Inet6Address.getByAddress(new byte[] {
                            (byte) (rawAddress1 >> 24), (byte) (rawAddress1 >> 16), (byte) (rawAddress1 >> 8), (byte) rawAddress1,
                            (byte) (rawAddress2 >> 24), (byte) (rawAddress2 >> 16), (byte) (rawAddress2 >> 8), (byte) rawAddress2,
                            (byte) (rawAddress3 >> 24), (byte) (rawAddress3 >> 16), (byte) (rawAddress3 >> 8), (byte) rawAddress3,
                            (byte) (rawAddress4 >> 24), (byte) (rawAddress4 >> 16), (byte) (rawAddress4 >> 8), (byte) rawAddress4
                    });

                    String address = inetAddress.getHostAddress();

                    return new AAAA(name, qClass, ttl, length, address);
                }
                default -> throw new QueryTypeUnsupportedException();
            }
        } catch (JumpLimitExceededException | BufferPositionOutOfBoundsException | UnknownHostException |
                 EndOfBufferException e) {
            throw new ResourceRecordMalformedException();
        }

    }
}
