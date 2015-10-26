package chronicle.test;


import java.io.File;
import java.io.IOException;

import net.openhft.chronicle.Chronicle;
import net.openhft.chronicle.ChronicleQueueBuilder;
import net.openhft.chronicle.ExcerptTailer;

/**
 * <p>
 * <b> TODO : Insert description of the class's responsibility/role. </b>
 * </p>
 */
public class Sink {
    private String sinkPath = "sink";

    private PortSupplier portSupplier;

    private Source source;


    /**
     * <p>
     * <b> TODO : Insert description of the method's responsibility/role. </b>
     * </p>
     *
     * @param source
     */
    public Sink(final Source source) {
        this.source = source;
        this.portSupplier = source.getPortSupplier();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.context.ApplicationListener#onApplicationEvent(org
     * .springframework.context.ApplicationEvent)
     */

    public void replicate() throws IOException {
        final int port = this.portSupplier.getAndAssertOnError();
        final Chronicle sink = ChronicleQueueBuilder.vanilla(this.sinkPath).sink().connectAddress("localhost", port).build();
        System.out.println(new File(this.sinkPath).getCanonicalPath());
        System.out.println("sink created");

        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                ExcerptTailer tailer;
                try {
                    tailer = sink.createTailer().toStart();
                    while (tailer.nextIndex()) {
                        System.out.println("next");
                        MyMessage dslMessage = new MyMessage();
                        dslMessage.readMarshallable(tailer);
                        System.out.println(tailer.index() + "  " + dslMessage.getControlEventId());
                        tailer.finish();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    try {
                        sink.close();
                        Sink.this.source.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        t.start();

    }

}
