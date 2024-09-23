package com.mumuca.dnsresolver.dns;

import com.mumuca.dnsresolver.dns.enums.QueryType;
import com.mumuca.dnsresolver.dns.exceptions.NotImplementedException;
import com.mumuca.dnsresolver.utils.PacketBuffer;
import com.mumuca.dnsresolver.utils.exceptions.BufferPositionOutOfBoundsException;
import com.mumuca.dnsresolver.utils.exceptions.EndOfBufferException;
import com.mumuca.dnsresolver.utils.exceptions.JumpLimitExceededException;

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

    public String name;
    public QueryType type;
    public short qClass;
    public int ttl;
    public int length;
    public String address;
    public int priority;

    public DNSRecord() {}

    public DNSRecord(String name, QueryType type, short qClass, int ttl, int length, String address) {
        this.name = name;
        this.type = type;
        this.qClass = qClass;
        this.ttl = ttl;
        this.length = length;
        this.address = address;
    }

    public DNSRecord(String name, QueryType type, short qClass, int ttl, int length, int priority, String mx) {
        this.name = name;
        this.type = type;
        this.qClass = qClass;
        this.ttl = ttl;
        this.length = length;
        this.priority = priority;
        this.address = mx;
    }

    public static DNSRecord fromBuffer(PacketBuffer buffer) throws JumpLimitExceededException, BufferPositionOutOfBoundsException, EndOfBufferException, UnknownHostException {
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
                return new DNSRecord(name, QueryType.A, qClass, ttl, length, address);
            }
            case NS -> {
                String ns = buffer.readQName();
                return new DNSRecord(name, QueryType.NS, qClass, ttl, length, ns);
            }
            case CNAME -> {
                String cname = buffer.readQName();
                return new DNSRecord(name, QueryType.CNAME, qClass, ttl, length, cname);
            }
            case MX -> {
                int priority = buffer.read16b();
                String mx = buffer.readQName();

                return new DNSRecord(name, QueryType.MX, qClass, ttl, length, priority, mx);
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

                return new DNSRecord(name, QueryType.AAAA, qClass, ttl, length, address);
            }
            default -> throw new UnsupportedOperationException("Unsupported query type: " + type);
        }
    }

    public void writeInBuffer(PacketBuffer buffer) throws EndOfBufferException, UnknownHostException, UnsupportedOperationException {
        switch (this.type) {
            case A -> {
                buffer.writeQName(name);
                buffer.write16b(type.getValue());
                buffer.write16b(1);
                buffer.write32b(ttl);
                buffer.write16b(4);

                var rawAddress = InetAddress.getByName(address).getAddress();
                buffer.write(rawAddress[0]);
                buffer.write(rawAddress[1]);
                buffer.write(rawAddress[2]);
                buffer.write(rawAddress[3]);
            }
            case NS, CNAME -> {
                buffer.writeQName(name);
                buffer.write16b(type.getValue());
                buffer.write16b(1);
                buffer.write32b(ttl);

                int position = buffer.getPosition();
                buffer.write(0);

                buffer.writeQName(address);

                int size = buffer.getPosition() - (position + 2);
                buffer.set16b(position, size);
            }
            case MX -> {
                buffer.writeQName(name);
                buffer.write16b(type.getValue());
                buffer.write16b(1);
                buffer.write32b(ttl);

                int position = buffer.getPosition();
                buffer.write(0);

                buffer.write16b(priority);
                buffer.writeQName(address);

                int size = buffer.getPosition() - (position + 2);
                buffer.set16b(position, size);
            }
            case AAAA -> {
                buffer.writeQName(name);
                buffer.write16b(type.getValue());
                buffer.write16b(1);
                buffer.write32b(ttl);
                buffer.write16b(16);

                byte[] rawAddress = Inet6Address.getByAddress(address.getBytes()).getAddress();

                for (byte b : rawAddress) {
                    buffer.write(b);
                }
            }
            case UNKNOWN -> throw new UnsupportedOperationException("Unsupported query type: " + type);
        }
    }

    @Override
    public String toString() {
        return "DNSRecord{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", qClass=" + qClass +
                ", ttl=" + ttl +
                ", length=" + length +
                ", priority=" + priority +
                ", address='" + address + '\'' +
                '}';
    }
}
