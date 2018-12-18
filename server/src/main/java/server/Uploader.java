package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Uploader extends Thread {

    private final Socket client;
    private final long pausedAt;
    private final byte[] data;
    private final long dataOffset;

    Uploader(long dataOffset, Long pausedAt, byte[] data, Socket client) {
        this.dataOffset = dataOffset;
        this.pausedAt = pausedAt;
        this.client = client;
        this.data = data;
    }

    @Override
    public void run() {
        try (DataOutputStream sent = new DataOutputStream(client.getOutputStream())) {
            sent.writeLong(data.length - pausedAt);
            sent.writeLong(dataOffset + pausedAt);
            sent.write(data, (int) pausedAt, (int) (data.length - pausedAt));
            sent.flush();

        } catch (IOException e) {
            System.out.println("Closing sending socket. " + e.getMessage());
        }

        closeSocket();
    }

    private void closeSocket() {
        try {
            if (client != null) {
                client.close();
            }
        } catch (IOException e) {
            System.err.println("Failed closing socket");
            e.printStackTrace();
        }
    }

}
