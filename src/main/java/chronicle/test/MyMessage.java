package chronicle.test;


import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;

import net.openhft.lang.io.Bytes;
import net.openhft.lang.io.serialization.BytesMarshallable;

/**
 *
 */
public class MyMessage implements BytesMarshallable {
    private long timestamp;
    private String payload;
    private String version;
    private String controlEventId;
    private byte[] payloadAsByteArr;
    private Long chronicleIndex;

    private static final int SHORT_BYTE_LENGTH = 2;
    private static final int INT_LENGTH = 4;
    private static final int LONG_BYTE_LENGTH = 8;


    /*
     * (non-Javadoc)
     *
     * @see
     * net.openhft.lang.io.serialization.BytesMarshallable#readMarshallable
     * (net.openhft.lang.io.Bytes)
     */

    @Override
    public void readMarshallable(final Bytes in) throws IllegalStateException {
        this.version = in.readUTF();
        this.timestamp = in.readLong();
        this.controlEventId = in.readUTF();
        this.payload = readPayload(in);
    }


    private String readPayload(final Bytes in) {
        int payloadLength = in.readInt();
        // byte[] bytes = in.readObject(byte[].class);
        byte[] bytes = new byte[payloadLength];
        in.read(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }


    /*
     * (non-Javadoc)
     *
     * @see
     * net.openhft.lang.io.serialization.BytesMarshallable#writeMarshallable
     * (net.openhft.lang.io.Bytes)
     */

    @Override
    public void writeMarshallable(final Bytes out) {
        out.writeUTF(this.version);
        out.writeLong(this.timestamp);
        out.writeUTF(this.controlEventId);
        // out.writeObject(this.payload);
        out.writeInt(this.payloadAsByteArr.length);
        out.write(this.payloadAsByteArr);
    }

    /**
     * @return the timestamp
     */

    public long getTimestamp() {
        return this.timestamp;
    }

    /**
     * @param timestamp
     *            the timestamp to set
     */

    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the payload
     */

    public String getPayload() {
        return this.payload;
    }

    /**
     * @param payload
     *            the payload to set
     */

    public void setPayload(final String payload) {
        this.payload = payload;
    }

    /**
     * @return the version
     */

    public String getVersion() {
        return this.version;
    }

    /**
     * @param version
     *            the version to set
     */

    public void setVersion(final String version) {
        this.version = version;
    }

    /**
     * @return the controlEventId
     */

    public String getControlEventId() {
        return this.controlEventId;
    }

    /**
     * @param controlEventId
     *            the controlEventId to set
     */

    public void setControlEventId(final String controlEventId) {
        this.controlEventId = controlEventId;
    }


    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */

    @Override
    public String toString() {
        return MessageFormat.format("controlEventId=[{0}],version=[{1}],timestamp=[{2}],\npayload=\n{3}", this.controlEventId,
            this.version, this.timestamp, this.payload);
    }

    public String getOutput() {
        return MessageFormat.format("controlEventId=[{0}],chronicleIndex=[{1}]\n{2}\n", this.controlEventId,
            String.valueOf(this.chronicleIndex), this.payload);
    }

    public int length() {
        this.payloadAsByteArr = this.payload.getBytes(StandardCharsets.UTF_8);

        return
        // version=llv{4} short+String
            SHORT_BYTE_LENGTH + this.version.length() +
            // timestamp=t{8} long
            LONG_BYTE_LENGTH +
            // controlEventId=llc{66} short+String
            SHORT_BYTE_LENGTH + this.controlEventId.length() +
            // payload=llllp{*} int+byte[]
            INT_LENGTH + this.payloadAsByteArr.length;
    }


    /**
     * @return the chronicleIndex
     */

    public Long getChronicleIndex() {
        return this.chronicleIndex;
    }


    /**
     * @param chronicleIndex
     *            the chronicleIndex to set
     */

    public void setChronicleIndex(final Long chronicleIndex) {
        this.chronicleIndex = chronicleIndex;
    }

}
