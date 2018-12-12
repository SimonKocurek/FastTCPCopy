import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Uploader extends Thread {

    private final Socket client;
    private final byte[] data;
    private final long dataOffset;

    Uploader(long dataOffset, byte[] data, Socket client) {
        this.dataOffset = dataOffset;
        this.client = client;
        this.data = data;

        System.out.println("Started uploader at " + client);
    }

    @Override
    public void run() {
        try (DataOutputStream sent = new DataOutputStream(client.getOutputStream())) {
            sent.writeLong(data.length);
            sent.writeLong(dataOffset);
            sent.write(data);
            sent.flush();

        } catch (IOException e) {
            System.err.println("Failed writing data " + client);
            e.printStackTrace();
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
