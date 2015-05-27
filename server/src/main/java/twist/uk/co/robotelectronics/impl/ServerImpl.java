package twist.uk.co.robotelectronics.impl;

import twist.uk.co.robotelectronics.RequestListener;
import twist.uk.co.robotelectronics.Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerImpl implements Server {

    private static final String MAX_CONNECTION_QUEUE_LENGTH_PROPERTY = "twist.uk.co.robotelectronics.maxConnectionQueueLength";
    private static final int MAX_CONNECTION_QUEUE_DEFAULT_LENGTH = 20;

    private final InetAddress inetAddress;
    private final int port;
    private final String password;
    private final int maxQueueLength;

    private final AtomicBoolean working = new AtomicBoolean();

    public ServerImpl(int port) {
        this((InetAddress)null, port, null);
    }

    public ServerImpl(String host, int port) throws UnknownHostException {
        this(host, port, null);
    }

    public ServerImpl(String host, int port, String password) throws UnknownHostException {
        this(InetAddress.getByName(host), port, password);
    }

    private ServerImpl(InetAddress inetAddress, int port, String password) {
        this.inetAddress = inetAddress;
        this.port = port;
        this.maxQueueLength = Integer.parseInt(
                System.getProperty(MAX_CONNECTION_QUEUE_LENGTH_PROPERTY, String.valueOf(MAX_CONNECTION_QUEUE_DEFAULT_LENGTH)));
        this.password = password;
    }

    @Override
    public void listen(final RequestListener listener) {
        if(!working.compareAndSet(false, true)) {
            throw new IllegalStateException();
        }
        new Thread() {
            public void run() {
                try (ServerSocket serverSocket = new ServerSocket(port, maxQueueLength, inetAddress)) {
                    while (working.get() && !serverSocket.isClosed() && !Thread.interrupted()) {
                        try (Socket socket = serverSocket.accept()) {
                            doProcessing(socket, listener);
                        }
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void doProcessing(Socket socket, RequestListener listener) throws IOException {
        try (InputStream inputStream = socket.getInputStream()) {
            try (OutputStream outputStream = socket.getOutputStream()) {
                while (true) {
                    byte[] buffer = new byte[512];
                    int bytesRead = inputStream.read(buffer);
                    if (bytesRead <= 0) {
                        break;
                    }
                    if (buffer[0] == (byte) 0x79) {
                        String password = new String(buffer, 1, bytesRead - 1);
                        if ((this.password == null && password.isEmpty()) ||
                                (this.password != null && this.password.equals(password))) {
                            outputStream.write(1);
                        } else {
                            outputStream.write(2);
                        }
                    } else {
                        if(buffer[0] != (byte) 0x20 && buffer[0] != (byte) 0x21) {
                            outputStream.write(1);
                            continue;
                        }
                        outputStream.write(notify(buffer[1], buffer[0] == (byte) 0x20, listener) ? 0 : 1);
                    }
                }
            }
        }
    }

    private static boolean notify(int number, boolean activate, RequestListener listener) {
        try {
            if (activate) {
                listener.activate(number);
            } else {
                listener.inactivate(number);
            }
        } catch (RuntimeException e) {
            return false;
        }
        return true;
    }

    @Override
    public void close() {
        if(!working.compareAndSet(true, false)) {
            throw new IllegalStateException();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = new ServerImpl("0.0.0.0", 17494);
        server.listen(new RequestListener() {
            @Override
            public void activate(int number) {
                System.out.println("activate : " + number);
            }

            @Override
            public void inactivate(int number) {
                System.out.println("inactivate : " + number);
            }
        });
    }
}
