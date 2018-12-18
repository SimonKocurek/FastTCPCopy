package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class UploaderServerSocket {

    private final Map<String, Long> configuration;
    private final ServerSocket listener;
    private final PrintWriter out;
    private final byte[][] fileChunks;


    UploaderServerSocket(Map<String, Long> configuration, Socket socket,
                         int clientNumber, PrintWriter out, byte[][] fileChunks)
            throws IOException {
        this.configuration = configuration;
        this.out = out;
        this.fileChunks = fileChunks;
        this.listener = new ServerSocket(socket.getLocalPort() + clientNumber);
    }

    void start() {
        try {
            sendFileSize();
            sendDownloaded();
            sendLocalPort();
            startUploaders();

        } catch (IOException e) {
            System.out.println("server.Uploader socket closed. " + e.getMessage());

        } finally {
            closeListener();
        }
    }

    private void sendFileSize() {
        if (configuration.containsKey("fileSize")) {
            out.println(configuration.get("fileSize"));
            System.out.println("Sent file size " + configuration.get("fileSize"));
        }
    }

    private void sendDownloaded() {
        Long downloaded = configuration.getOrDefault("downloaded", 0L);
        out.println(downloaded);
        System.out.println("Sent downloaded " + configuration.get("downloaded"));
    }

    private void startUploaders() throws IOException {
        int threads = Math.toIntExact(configuration.getOrDefault("threads", 1L));
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        long offset = 0;
        for (int i = 0; i < threads; i++) {
            Long pausedAt = configuration.getOrDefault("chunk_" + i, 0L);
            Uploader uploader = new Uploader(offset, pausedAt, fileChunks[i], listener.accept());
            executor.submit(uploader);

            offset += fileChunks[i].length;
        }
    }

    private void sendLocalPort() {
        out.println(listener.getLocalPort());
        out.flush();
        System.out.println("Waiting for client forRequest connect forRequest uploader streams");
    }

    private void closeListener() {
        try {
            listener.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
