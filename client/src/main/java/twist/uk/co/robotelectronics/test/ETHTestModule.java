package twist.uk.co.robotelectronics.test;

import twist.uk.co.robotelectronics.data.ModuleInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ETHTestModule extends TestModuleBase {

    private static final Logger log = Logger.getLogger(ETHTestModule.class.getName());

    private final AtomicBoolean working = new AtomicBoolean();

    protected ETHTestModule(ModuleInfo moduleInfo,
                            Set<Integer> digitalOutputs,
                            Set<Integer> digitalInputs,
                            Set<Integer> analogInputs) {
        super(moduleInfo, digitalOutputs, digitalInputs, analogInputs);
    }

    protected void startOnPort(int port) {
        if(!working.compareAndSet(false, true)) {
            throw new IllegalStateException();
        }
        new Thread() {
            public void run() {
                try (ServerSocket serverSocket = new ServerSocket(port)) {
                    while (working.get() && !serverSocket.isClosed() && !Thread.interrupted()) {
                        try (Socket socket = serverSocket.accept()) {
                            doProcessing(socket);
                        } catch(IOException e) {
                            log.log(Level.WARNING, "IOException on reading", e);
                        } catch (RuntimeException e) {
                            log.log(Level.SEVERE, "RuntimeException on processing", e);
                        }
                    }
                } catch(IOException e) {
                    log.log(Level.SEVERE, "IOException on port opening", e);
                }
            }
        }.start();
    }

    private void doProcessing(Socket socket) throws IOException {
        try (InputStream inputStream = socket.getInputStream()) {
            try (OutputStream outputStream = socket.getOutputStream()) {
                byte[] message = new byte[0];

                byte[] buffer = new byte[512];
                int bytesRead = inputStream.read(buffer);
                if (bytesRead > 0) {
                    message = new byte[bytesRead];
                    System.arraycopy(buffer, 0, message, 0, bytesRead);
                }

                byte[] result = onMessage(message);
                outputStream.write(result);
            }
        }
    }

    protected void stopListening() {
        if(!working.compareAndSet(true, false)) {
            throw new IllegalStateException();
        }
    }
}
