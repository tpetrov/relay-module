package twist.uk.co.robotelectronics.impl;

import twist.uk.co.robotelectronics.ClientException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public abstract class ETHModuleClient extends ClientBase {

    private final String host;
    private final int port;

    private Socket socket;

    public ETHModuleClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    protected void sendAndReceive(byte[] command, byte[] result) throws ClientException {
        try {
            Socket socket = getSocket();
            sendAndReceive(socket, command, result);
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    private Socket getSocket() throws IOException {
        if(socket == null || socket.isClosed()) {
            socket = new Socket(host, port);
            socket.setKeepAlive(true);
            socket.setSoTimeout(0);
        }
        return socket;
    }

    @Override
    public void close() throws ClientException {
        try {
            if(socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    private static void sendAndReceive(Socket socket, byte[] bytesToSend, byte[] result) throws IOException {
        try(OutputStream outputStream = socket.getOutputStream()) {
            outputStream.write(bytesToSend);
            try(InputStream inputStream = socket.getInputStream()) {
                inputStream.read(result);
            }
        }
    }
}
