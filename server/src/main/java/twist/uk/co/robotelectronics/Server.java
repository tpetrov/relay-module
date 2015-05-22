package twist.uk.co.robotelectronics;

import java.io.Closeable;
import java.io.IOException;

public interface Server extends Closeable {
    void listen(RequestListener listener) throws IOException;
}
