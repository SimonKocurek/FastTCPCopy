import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class UploaderServerSocket {

    private final Map<String, Long> configuration;
    private final ServerSocket listener;

    UploaderServerSocket(Map<String, Long> configuration) {
        this.configuration = configuration;
        sendFileSize();

        this.listener = new ServerSocket(socket.getLocalPort() + clientNumber);
    }

    private void start() {
        try {
            sendLocalPort(listener);
            startUploaders(listener);

        } catch (IOException e) {
            out.println("Uploader socket closed. " + e.getMessage());

        } finally {

            try {
                listener.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void startUploaders() throws IOException {
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        long offset = 0;
        for (int i = 0; i < threads; i++) {
            Uploader uploader = new Uploader(offset, fileChunks[i], listener.accept());
            executor.submit(uploader);

            offset += fileChunks[i].length;
        }
    }

    private void sendLocalPort() {
        out.println(listener.getLocalPort());
        out.println("Waiting for client forRequest connect forRequest uploader streams");
    }

    private void sendFileSize() {
        if (configuration.containsKey("fileSize")) {
            out.println(configuration.get("fileSize"));
        }
    }

}
