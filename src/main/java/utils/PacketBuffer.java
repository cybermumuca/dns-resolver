package utils;

import java.nio.charset.StandardCharsets;

public class PacketBuffer {
    private final byte[] buffer;
    private int position;

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
}
