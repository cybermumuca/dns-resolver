package dns;

import dns.enums.QueryType;
import utils.PacketBuffer;

import java.net.InetAddress;


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
     *   <li><strong>qClass</strong> - A classe do registro, geralmente definida
     *   como 1 para registros da Internet.</li>
     *   <li><strong>ttl</strong> - O tempo de vida (Time to Live) do registro,
     *   que indica quanto tempo ele deve ser mantido em cache.</li>
     *   <li><strong>length</strong> - O comprimento dos dados do registro em bytes.</li>
     *   <li><strong>address</strong> - O endereço associado ao registro, que pode ser
     *   um endereço IP ou outro tipo de dado, dependendo do tipo do registro.</li>
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

    public DNSRecord() {}

    public DNSRecord(String name, QueryType type, short qClass, int ttl, int length, String address) {
        this.name = name;
        this.type = type;
        this.qClass = qClass;
        this.ttl = ttl;
        this.length = length;
        this.address = address;
    }

    public static DNSRecord fromBuffer(PacketBuffer buffer) throws Exception {
        String name = buffer.readQName();

        QueryType type = QueryType.fromValue(buffer.read16b());
        short qClass = (short) buffer.read16b();
        int ttl = buffer.read32b();
        int length = buffer.read16b();

        switch (type) {
            case A -> {
                int rawAddress = buffer.read32b();
                InetAddress inetAddress = InetAddress.getByAddress(new byte[]{
                        (byte) (rawAddress >> 24 & 0xFF),
                        (byte) (rawAddress >> 16 & 0xFF),
                        (byte) (rawAddress >> 8 & 0xFF),
                        (byte) (rawAddress & 0xFF)
                });
                String address = inetAddress.getHostAddress();
                return new DNSRecord(name, QueryType.A, qClass, ttl, length, address);
            }
            default -> {
                throw new UnsupportedOperationException("Unsupported query type: " + type);
            }
        }
    }

    public void writeInBuffer(PacketBuffer buffer) throws Exception {
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
            case UNKNOWN -> {
                throw new UnsupportedOperationException("Unsupported query type: " + type);
            }
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
                ", address='" + address + '\'' +
                '}';
    }
}
