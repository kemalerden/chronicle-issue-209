package chronicle.test;

import java.nio.channels.SelectableChannel;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import net.openhft.chronicle.tcp.TcpConnectionHandler;

class PortSupplier extends TcpConnectionHandler {
    private final AtomicInteger port;
    private final CountDownLatch latch;

    public PortSupplier() {
        this.port = new AtomicInteger(-1);
        this.latch = new CountDownLatch(1);
    }

    @Override
    public void onListen(final ServerSocketChannel channel) {
        this.port.set(channel.socket().getLocalPort());
        this.latch.countDown();
    }

    public void onError(final SelectableChannel channel, final Exception exception) {
        exception.printStackTrace();
        this.port.set(-1);
        this.latch.countDown();
    }

    public void reset() {
        this.port.set(-1);
    }

    public int port() {
        if (this.port.get() == -1) {
            try {
                this.latch.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
            }
        }
        System.out.println("port=" + this.port.get());
        return this.port.get();
    }

    public int getAndAssertOnError() {
        final int port = port();
        assert -1 != port;
        return port;
    }

}
