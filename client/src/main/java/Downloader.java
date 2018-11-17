import java.io.DataInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

public class Downloader extends Thread {

    private final int id;
    private final int uploaderPort;
    private final String serverAddress;
    private final RandomAccessFile file;
    private final long fileStart;
    private final CountDownLatch downloadingThreads;

    Downloader(int id, String serverAddress, int uploaderPort, RandomAccessFile file,
               long fileStart, CountDownLatch downloadingThreads) {
        this.id = id;
        this.serverAddress = serverAddress;
        this.uploaderPort = uploaderPort;
        this.file = file;
        this.fileStart = fileStart;
        this.downloadingThreads = downloadingThreads;

        System.out.println("Started downloader " + id);
    }

    @Override
    public void run() {
        try (Socket server = new Socket(serverAddress, uploaderPort)) {
            downloadData(server);

        } catch (IOException e) {
            System.err.println("Creating socket " + id + " on "
                    + serverAddress + ":" + uploaderPort + " failed");
            e.printStackTrace();
        }
    }

    private void downloadData(Socket server) {
        try {
            DataInputStream received = new DataInputStream(server.getInputStream());
            int size = received.readInt();
            byte[] message = new byte[size];
            received.readFully(message, 0, message.length);

            writeContent(message);

        } catch (IOException e) {
            System.err.println("Failed downloading file " + server);
            e.printStackTrace();
        }
    }

    private void writeContent(byte[] message) throws IOException {
        synchronized (file) {
            System.out.print("Donwloader " + id + " started writing at " + fileStart + " ...");
            file.seek(fileStart);
            file.write(message);
            System.out.println("done");
        }

        downloadingThreads.countDown();
    }

}
