package chronicle.test;

import java.io.IOException;

public class Main {
    public static void main(final String[] args) throws IOException {

        Source source = new Source();
        Sink sink = new Sink(source);
        source.addData();
        sink.replicate();
    }
}
