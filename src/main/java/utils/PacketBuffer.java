package utils;

import java.nio.charset.StandardCharsets;

public class PacketBuffer {
    /**
     * Representa o Buffer de um pacote
     */

    private final byte[] buffer;
    private int position;

    public PacketBuffer() {
        this.buffer = new byte[512];
        this.position = 0;
    }

    public PacketBuffer(byte[] buffer) {
        this.buffer = buffer;
        this.position = 0;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public int getPosition() {
        return position;
    }

    public int read() throws Exception {
        if (position >= buffer.length) {
            throw new Exception("End of Buffer");
        }

        return buffer[position++] & 0xFF;
    }

    public int read16b() throws Exception {
        if (position + 1 >= buffer.length) {
            throw new Exception("End of Buffer");
        }
        int highByte = read();
        int lowByte = read();
        return (highByte << 8) | lowByte;
    }

    public int read32b() throws Exception {
        if (position + 3 >= buffer.length) {
            throw new Exception("End of Buffer");
        }
        return (read() << 24) | (read() << 16) | (read() << 8) | read();
    }

    public void seek(int pos) throws Exception {
        if (pos >= buffer.length) {
            throw new Exception("Seek position out of bounds");
        }
        position = pos;
    }

    public int get(int pos) throws Exception {
        if (pos >= buffer.length) {
            throw new Exception("Position out of bounds");
        }
        return buffer[pos] & 0xFF;
    }

    public String readQName() throws Exception {
        int pos = this.position;
        boolean jumped = false;
        int maxJumps = 5;
        int jumpsPerformed = 0;
        StringBuilder domainParts = new StringBuilder();

        while (true) {
            if (jumpsPerformed > maxJumps) {
                throw new Exception("Limit of " + maxJumps + " jumps exceeded");
            }

            int length = get(pos);

            if ((length & 0xC0) == 0xC0) {
                if (!jumped) {
                    seek(pos + 2);
                }

                int b2 = get(pos + 1);
                pos = ((length ^ 0xC0) << 8) | b2;

                jumped = true;
                jumpsPerformed++;
                continue;
            } else {
                pos++;

                if (length == 0) {
                    break;
                }

                byte[] bytes = readBytesAt(pos, length);
                domainParts.append(new String(bytes, StandardCharsets.UTF_8).toLowerCase()).append(".");
                pos += length;
            }
        }

        if (!jumped) {
            seek(pos);
        }

        if (!domainParts.isEmpty()) {
            domainParts.setLength(domainParts.length() - 1);
        }

        return domainParts.toString();
    }

    public byte[] readBytes(int length) throws Exception {
        if (position + length > buffer.length) {
            throw new Exception("End of Buffer");
        }

        byte[] result = new byte[length];
        System.arraycopy(buffer, position, result, 0, length);
        position += length;

        return result;
    }

    public byte[] readBytesAt(int pos, int length) throws Exception {
        if (pos + length > buffer.length) {
            throw new Exception("Read position out of bounds");
        }

        byte[] result = new byte[length];
        System.arraycopy(buffer, pos, result, 0, length);
        return result;
    }

    /**
     * Escreve 8 bits e avança 1 posição.
     *
     * @param value Valor a ser escrito.
     * @throws Exception Se o final do buffer for atingido.
     */
    public void write(int value) throws Exception {
        if (position >= buffer.length) {
            throw new Exception("End of Buffer");
        }
        buffer[position] = (byte) value;
        position++;
    }

    /**
     * Escreve 16 bits e avança 2 posições.
     *
     * @param value Valor a ser escrito.
     * @throws Exception Se o final do buffer for atingido.
     */
    public void write16b(int value) throws Exception {
        if (position + 1 >= buffer.length) {
            throw new Exception("End of Buffer");
        }
        write(value >> 8);
        write(value & 0xFF);
    }

    /**
     * Escreve 32 bits e avança 4 posições.
     *
     * @param value Valor a ser escrito.
     * @throws Exception Se o final do buffer for atingido.
     */
    public void write32b(int value) throws Exception {
        if (position + 3 >= buffer.length) {
            throw new Exception("End of Buffer");
        }
        write((value >> 24) & 0xFF);
        write((value >> 16) & 0xFF);
        write((value >> 8) & 0xFF);
        write(value & 0xFF);
    }

    /**
     * Escreve o 'QName' (nome do domínio) do pacote DNS.
     *
     * @param qname Nome do domínio.
     * @throws Exception Se um rótulo tiver mais de 63 caracteres ou o final do buffer for atingido.
     */
    public void writeQName(String qname) throws Exception {
        String[] labels = qname.split("\\.");

        for (String label : labels) {
            int length = label.length();

            if (length > 0x3F) {
                throw new IllegalArgumentException("Single label exceeds 63 characters of length");
            }

            write(length);

            byte[] labelBytes = label.getBytes(StandardCharsets.UTF_8);
            for (byte b : labelBytes) {
                write(b);
            }
        }
        write(0);
    }

    public void set(int position, int value) {
        buffer[position] = (byte) value;
    }

    public void set16b(int position, int value) {
        set(position, value >> 8);
        set(position + 1, value & 0xFF);
    }
}
