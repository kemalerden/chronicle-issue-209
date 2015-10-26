package chronicle.test;

import java.io.File;
import java.io.IOException;

import net.openhft.chronicle.Chronicle;
import net.openhft.chronicle.ChronicleQueueBuilder;
import net.openhft.chronicle.ExcerptAppender;

public class Source {

    private final String sourcePath = "source";


    private final PortSupplier portSupplier = new PortSupplier();

    final Chronicle source;

    public Source() throws IOException {
        this.source = ChronicleQueueBuilder.vanilla(this.sourcePath).source().bindAddress(0).connectionListener(this.portSupplier)
            .build();
        System.out.println(new File(this.sourcePath).getCanonicalPath());
    }

    /**
     * @return the portSupplier
     */
    public PortSupplier getPortSupplier() {
        return this.portSupplier;
    }

    /**
     * @return the sourcePath
     */
    public String getSourcePath() {
        return this.sourcePath;
    }

    public void close() throws IOException {
        this.source.close();
    }

    public void addData() {
        for (int i = 0; i < 10; i++) {
            MyMessage message = new MyMessage();
            message.setControlEventId("0x000000000000000000000000000000000000001");
            message.setTimestamp(System.currentTimeMillis());
            message.setVersion("v1");
            String str = str(10000);
            message.setPayload(str);
            try (ExcerptAppender appender = this.source.createAppender()) {
                appender.startExcerpt(message.length());
                message.writeMarshallable(appender);
                appender.finish();
            } catch (Exception ex) {
                System.err.println("Unable to journal the message " + message);
            }
        }
    }

    /**
     * <p>
     * <b> TODO : Insert description of the method's responsibility/role. </b>
     * </p>
     *
     * @return
     */
    private String str(final int length) {
        return new RandomString(length).nextString();
    }
}
