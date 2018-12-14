package commands;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GetCommand extends Command {

    private void handleGet() {
        out.println(fileSize());

        try (ServerSocket listener = new ServerSocket(socket.getLocalPort() + clientNumber)) {
            out.println(listener.getLocalPort());
            System.out.println("Waiting for client to connect to uploader streams");

            ExecutorService executor = Executors.newFixedThreadPool(threads);

            long offset = 0;
            for (int i = 0; i < threads; i++) {
                Uploader uploader = new Uploader(offset, fileChunks[i], listener.accept());
                executor.submit(uploader);

                offset += fileChunks[i].length;
            }

        } catch (IOException e) {
            System.out.println("Uploader socket closed. " + e.getMessage());
        }
    }

    private long fileSize() {
        return Arrays.stream(fileChunks)
                .mapToLong(chunk -> chunk.length)
                .sum();
    }

}
