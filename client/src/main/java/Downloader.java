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
    private final CountDownLatch downloadingThreads;

    private long downloaded;
    private long fileStart;
    private int chunkSize = 1024;

    Downloader(int id, String serverAddress, int uploaderPort, RandomAccessFile file, CountDownLatch downloadingThreads) {
        this.id = id;
        this.serverAddress = serverAddress;
        this.uploaderPort = uploaderPort;
        this.file = file;
        this.downloaded = 0;
        this.downloadingThreads = downloadingThreads;

        System.out.println("Started downloader " + id);
    }

    @Override
    public void run() {
        try (Socket server = new Socket(serverAddress, uploaderPort)) {
            downloadData(server);
            downloadingThreads.countDown();

        } catch (IOException e) {
            System.err.println("Creating socket " + id + " on "
                    + serverAddress + ":" + uploaderPort + " failed");
            e.printStackTrace();
        }
    }

    private void downloadData(Socket server) {
        try (DataInputStream received = new DataInputStream(server.getInputStream())) {
            long size = received.readLong();
            fileStart = received.readLong();

            while (downloaded < size && !Thread.interrupted()) {
                byte[] message = new byte[chunkSize];
                int read = received.read(message, 0, message.length);
                writeContent(message, read);

                downloaded += read;
            }

        } catch (IOException e) {
            System.err.println("Failed downloading file " + server);
            e.printStackTrace();
        }
    }

    private void writeContent(byte[] message, int read) throws IOException {
        long offset = fileStart + downloaded;

        synchronized (file) {
            System.out.print("Downloader " + id + " writing at " + offset + "-" + (offset + read - 1) + " ...");
            file.seek(offset);
            file.write(message, 0, read);
            System.out.println("done");
        }
    }

}
