package twist.uk.co.robotelectronics;

import java.io.Closeable;

public interface Server extends Closeable {
    void listen(RequestListener listener);
}
