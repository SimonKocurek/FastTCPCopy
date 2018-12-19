package client;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

public class Downloader extends Thread {

    private final int id;
    private final int uploaderPort;
    private final String serverAddress;
    private final String filename;
    private final RandomAccessFile file;
    private final ProgressWatcher progressWatcher;
    private final CountDownLatch downloadingThreads;

    private long downloaded;
    private long fileStart;
    private int chunkSize = 1024;

    Downloader(int id, String serverAddress, int uploaderPort, String filename,
               RandomAccessFile file, ProgressWatcher progressWatcher, CountDownLatch downloadingThreads) {
        this.id = id;
        this.serverAddress = serverAddress;
        this.uploaderPort = uploaderPort;
        this.filename = filename;
        this.file = file;
        this.progressWatcher = progressWatcher;
        this.downloaded = 0;
        this.downloadingThreads = downloadingThreads;
    }

    @Override
    public void run() {
        try (Socket server = new Socket(serverAddress, uploaderPort)) {
            downloadData(server);
            downloadingThreads.countDown();

        } catch (IOException e) {
            System.err.println("Creating socket " + id + " on " + serverAddress + ":" + uploaderPort + " failed");
            e.printStackTrace();
        }
    }

    private void downloadData(Socket server) {
        try (DataInputStream received = new DataInputStream(server.getInputStream())) {
            long size = received.readLong();
            fileStart = received.readLong();
            byte[] message = new byte[chunkSize];

            downloadChunks(received, size, message);

        } catch (IOException e) {
            System.err.println("Failed downloading file " + server);
            e.printStackTrace();
        }
    }

    private void downloadChunks(DataInputStream received, long size, byte[] message) throws IOException {
        while (downloaded < size) {
            if (Thread.interrupted()) {
                saveState(fileStart + downloaded, fileStart + size);
                break;
            }

            int read = received.read(message, 0, message.length);
            writeContent(message, read);

            downloaded += read;
            progressWatcher.add(read);
        }
    }

    private void saveState(long pointer, long end) {
        synchronized (file) {
            File stateFile = new File(Util.stateFileFor(filename));

            try (FileOutputStream outStream = new FileOutputStream(stateFile, true)) {
                try (PrintWriter writer = new PrintWriter(outStream)) {
                    writer.println(pointer + "-" + end);
                }

            } catch (IOException e) {
                System.err.println("Failed saving paused state");
                e.printStackTrace();
            }
        }
    }

    private void writeContent(byte[] message, int read) throws IOException {
        long offset = fileStart + downloaded;

        synchronized (file) {
            file.seek(offset);
            file.write(message, 0, read);
        }
    }

}
