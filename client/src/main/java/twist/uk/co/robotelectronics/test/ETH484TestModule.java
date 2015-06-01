package twist.uk.co.robotelectronics.test;

import twist.uk.co.robotelectronics.data.ModuleInfo;
import twist.uk.co.robotelectronics.impl.ETH484ModuleClient;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

public class ETH484TestModule extends ETHTestModule {

    private static final AtomicInteger listenPortSequence = new AtomicInteger(17494);

    public ETH484TestModule() {
        super(new ModuleInfo(1, 2, 3),
                new HashSet<>(Arrays.<Integer>asList(1, 2, 3, 4, 9, 10, 11, 12, 13, 14, 15, 16)),
                new HashSet<>(Arrays.<Integer>asList(9, 10, 11, 12, 13, 14, 15, 16)),
                new HashSet<>(Arrays.<Integer>asList(1, 2, 3, 4)));
    }

    @Override
    public ETH484ModuleClient getClient() {
        int port = listenPortSequence.getAndIncrement();
        startOnPort(port);
        return new ETH484ModuleClient("localhost", port) {
            @Override
            public void close() {
                super.close();
                stopListening();
            }
        };
    }
}
